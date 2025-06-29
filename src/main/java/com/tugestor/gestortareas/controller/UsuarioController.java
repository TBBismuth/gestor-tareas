package com.tugestor.gestortareas.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
import com.tugestor.gestortareas.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
	private final UsuarioService us;
	public UsuarioController(UsuarioService us) {
		this.us = us;
	}
	
	@GetMapping
	public List<UsuarioResponse> listarUsuarios() {
		List<Usuario> usuarios = us.obtenerTodos();
		return usuarios.stream()
				.map(UsuarioResponse::new)
				.toList();
	}
	@GetMapping("/{id}")
	public UsuarioResponse listarUsuarioId(@PathVariable Long id) {
		Usuario usuario = us.obtenerPorId(id);
		return new UsuarioResponse(usuario);
	}
	@GetMapping("/email/{email}")
	public ResponseEntity<UsuarioResponse> listarUsuarioEmail(@PathVariable String email) {
		return us.obtenerPorEmail(email)
				.map(usuario -> ResponseEntity.ok(new UsuarioResponse(usuario)))
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
	/*ResponseEntity es una clase de Spring que representa una respuesta completa HTTP
	 *Body, codigo de estado y headers*/
	}
	@PostMapping("/add")
	@Valid
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
	public void eliminarUsuario(@PathVariable Long id) {
		us.eliminarPorId(id);
	}
	@PostMapping("/login")
	@Valid
	public LoginResponse loginUsuario(@RequestBody LoginRequest login) {
		return us.login(login);
	}
}
