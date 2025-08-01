package com.tugestor.gestortareas.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
public class DummyExceptionController {

	@GetMapping("/runtime")
	public void runtime() {
		throw new RuntimeException("Mensaje de error runtime");
	}

	@GetMapping("/entity-not-found")
	public void entityNotFound() {
		throw new EntityNotFoundException("Entidad no encontrada");
	}

	@GetMapping("/access-denied")
	public void accessDenied() {
		throw new AccessDeniedException("Acceso denegado");
	}

	@GetMapping("/username-not-found")
	public void usernameNotFound() {
		throw new UsernameNotFoundException("Usuario no encontrado");
	}

	@GetMapping("/type-mismatch")
	public void typeMismatch() {
		throw new MethodArgumentTypeMismatchException("valor", String.class, "param", null, new IllegalArgumentException("Tipo incorrecto"));
	}
}