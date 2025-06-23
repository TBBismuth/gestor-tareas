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
	public List<Usuario> listarUsuarios() {
		return us.obtenerTodos();
	}
	@GetMapping("/{id}")
	public Usuario listarUsuarioId(@PathVariable Long id) {
		return us.obtenerPorId(id);
	}
	@GetMapping("/email/{email}")
	public ResponseEntity<Usuario> listarUsuarioEmail(@PathVariable String email) {
		return us.obtenerPorEmail(email).map(ResponseEntity::ok)
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
	/*ResponseEntity es una clase de Spring que representa una respuesta completa HTTP
	 *Body, codigo de estado y headers*/
	}
	@PostMapping("/add")
	@Valid
	public ResponseEntity<Usuario> aniadirUsuario(@RequestBody Usuario usuario) {
		Usuario nuevoUsuario = us.guardarUsuario(usuario);
		// Creamos una URI para el recurso recién creado (ej: /usuario/5)
		// Esto es solo una referencia de "dónde se puede consultar este nuevo recurso"
		URI location = URI.create("/usuario/" + nuevoUsuario.getIdUsuario());
		// Devolvemos una respuesta con:
		// -Código 201 Created
		// -Cabecera Location con la URI del nuevo usuario
		// -Cuerpo: el usuario recién creado en formato JSON
		return ResponseEntity.created(location).body(nuevoUsuario);
	}
	@DeleteMapping("/delete/{id}")
	public void eliminarUsuario(@PathVariable Long id) {
		us.eliminarPorId(id);
	}
}
