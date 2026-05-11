package com.tugestor.gestortareas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.AccessTokenResponse;
import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.RefreshTokenRequest;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.dto.UsuarioResponse;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;
import com.tugestor.gestortareas.security.JwtService;
import com.tugestor.gestortareas.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
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
	private final UserDetailsService uds;
	public UsuarioController(UsuarioService us, AuthenticationManager am, JwtService jwts, UsuarioRepository ur,
			UserDetailsService uds) {
		this.us = us;
		this.am = am;
		this.jwts = jwts;
		this.ur= ur;
		this.uds = uds;
	}

	@GetMapping("/me")
	@Operation(
			summary = "Obtener la cuenta del usuario autenticado",
			description = "Devuelve la informacion de la cuenta asociada al JWT enviado en la peticion."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cuenta obtenida correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token invalido")
	})
	public ResponseEntity<UsuarioResponse> obtenerMiCuenta() {
		Usuario usuario = us.obtenerUsuarioActual();
		return ResponseEntity.ok(new UsuarioResponse(usuario));
	}

	@PostMapping("/add")
	@Operation(
			summary = "Registrar un nuevo usuario",
			description = "Crea un nuevo usuario con los datos proporcionados y devuelve su informacion."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos del usuario invalidos")
	})
	public ResponseEntity<UsuarioResponse> aniadirUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
		Usuario nuevoUsuario = us.guardarUsuario(usuarioRequest);
		UsuarioResponse response = new UsuarioResponse(nuevoUsuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/me")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(
			summary = "Eliminar la cuenta del usuario autenticado",
			description = "Elimina la cuenta asociada al JWT enviado en la peticion."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token invalido")
	})
	public void eliminarMiCuenta() {
		us.eliminarUsuarioActual();
	}

	@PostMapping("/login")
	@Operation(
			summary = "Autenticar usuario y obtener JWT",
			description = "Verifica las credenciales del usuario y devuelve un token JWT si son correctas."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Autenticacion exitosa"),
		@ApiResponse(responseCode = "401", description = "Credenciales invalidas")
	})
	public ResponseEntity<LoginResponse> loginUsuario(@Valid @RequestBody LoginRequest loginRequest){
		Authentication auth = am.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()
						)
		);
		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		String accessToken = jwts.generateAccessToken(userDetails);
		String refreshToken = jwts.generateRefreshToken(userDetails);
		Usuario usuario = ur.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userDetails.getUsername()));
		LoginResponse response = new LoginResponse(
				usuario.getIdUsuario(),
				usuario.getNombre(),
				usuario.getEmail(),
				accessToken,
				refreshToken
				);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	@Operation(
			summary = "Renovar access token",
			description = "Recibe un refresh token valido y devuelve un nuevo access token."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Access token renovado correctamente"),
		@ApiResponse(responseCode = "401", description = "Refresh token invalido")
	})
	public ResponseEntity<AccessTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
		String refreshToken = refreshTokenRequest.getRefreshToken();
		try {
			String username = jwts.extractUsername(refreshToken);
			UserDetails userDetails = uds.loadUserByUsername(username);
			if (!jwts.isRefreshTokenValid(refreshToken, userDetails)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String accessToken = jwts.generateAccessToken(userDetails);
			return ResponseEntity.ok(new AccessTokenResponse(accessToken));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
