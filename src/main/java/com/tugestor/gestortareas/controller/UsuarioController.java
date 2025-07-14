package com.tugestor.gestortareas.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.dto.UsuarioResponse;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;
import com.tugestor.gestortareas.security.JwtService;
import com.tugestor.gestortareas.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
	private final UsuarioService us;
	private final AuthenticationManager am;
	private final JwtService jwts;
	private final UsuarioRepository ur;
	public UsuarioController(UsuarioService us, AuthenticationManager am, JwtService jwts, UsuarioRepository ur) {
		this.us = us;
		this.am = am;
		this.jwts = jwts;
		this.ur= ur;
	}
	
	@GetMapping
	@Operation(
			summary = "Listar todos los usuarios",
			description = "Devuelve un listado con todos los usuarios registrados."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido correctamente")
	})
	public List<UsuarioResponse> listarUsuarios() {
		List<Usuario> usuarios = us.obtenerTodos();
		return usuarios.stream()
				.map(UsuarioResponse::new)
				.toList();
	}
	
	@GetMapping("/{id}")
	@Operation(
			summary = "Obtener usuario por ID",
			description = "Devuelve la información de un usuario según su ID."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del usuario", example = "3")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	public UsuarioResponse listarUsuarioId(@PathVariable Long id) {
		Usuario usuario = us.obtenerPorId(id);
		return new UsuarioResponse(usuario);
	}
	
	@GetMapping("/email/{email}")
	@Operation(
			summary = "Obtener usuario por email",
			description = "Devuelve la información de un usuario según su dirección de email."
			)
	@Parameters({
		@Parameter(name = "email", description = "Email del usuario", example = "usuario@ejemplo.com")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	public ResponseEntity<UsuarioResponse> listarUsuarioEmail(@PathVariable String email) {
		return us.obtenerPorEmail(email)
				.map(usuario -> ResponseEntity.ok(new UsuarioResponse(usuario)))
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
	/*ResponseEntity es una clase de Spring que representa una respuesta completa HTTP
	 *Body, codigo de estado y headers*/
	}
	
	@PostMapping("/add")
	@Valid
	@Operation(
			summary = "Registrar un nuevo usuario",
			description = "Crea un nuevo usuario con los datos proporcionados y devuelve su información."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos del usuario inválidos")
	})
	public ResponseEntity<UsuarioResponse> aniadirUsuario(@RequestBody UsuarioRequest usuarioRequest) {
		Usuario nuevoUsuario = us.guardarUsuario(usuarioRequest);
		// Creamos una URI para el recurso recién creado (ej: /usuario/5)
		// Esto es solo una referencia de "dónde se puede consultar este nuevo recurso"
		URI location = URI.create("/usuario/" + nuevoUsuario.getIdUsuario());
		// Devolvemos una respuesta con:
		// -Código 201 Created
		// -Cabecera Location con la URI del nuevo usuario
		// -Cuerpo: el usuario recién creado en formato JSON
		UsuarioResponse response = new UsuarioResponse(nuevoUsuario);
		// UsuarioResponse es un DTO que contiene solo los datos necesarios para la respuesta
		return ResponseEntity.created(location).body(response);
	}

	@DeleteMapping("/delete/{id}")
	@Operation(
			summary = "Eliminar un usuario",
			description = "Elimina el usuario indicado por su ID."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del usuario a eliminar", example = "4")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	public void eliminarUsuario(@PathVariable Long id) {
		us.eliminarPorId(id);
	}

	@PostMapping("/login")
	@Valid
	@Operation(
			summary = "Autenticar usuario y obtener JWT",
			description = "Verifica las credenciales del usuario y devuelve un token JWT si son correctas."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
		@ApiResponse(responseCode = "401", description = "Credenciales inválidas")
	})
	public ResponseEntity<LoginResponse> loginUsuario(@RequestBody LoginRequest loginRequest){
		// Autentifico el usuario con email y pass usando CustomUserDetailsService y PasswordEncoder
		Authentication auth = am.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()
						)
				);
		// Extraigo los detalles del usuario autentificado
		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		// Genero el token usando JwtService
		String jwtToken = jwts.generateToken(userDetails);
		// Recupero el Usuario de la BBDD para obtener id y nombre
		Usuario usuario = ur.findByEmail(userDetails.getUsername())	// El Username es el email
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userDetails.getUsername()));
		// Construyo el LoginResponse con el id, nombre, email y token
		LoginResponse response = new LoginResponse(
				usuario.getIdUsuario(),
				usuario.getNombre(),
				usuario.getEmail(),
				jwtToken
				);
		return ResponseEntity.ok(response);	// .ok devuelve HTP 200 + el contenido
	}
}
