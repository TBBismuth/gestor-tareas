package com.tugestor.gestortareas.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/error")
public class TestValidationController {

	public static class DummyRequest {
		@NotBlank(message = "El campo nombre es obligatorio")
		public String nombre;
	}

	@PostMapping("/validation")
	public void triggerValidation(@RequestBody @Valid DummyRequest request) {
		// Si el campo nombre es vacío, salta MethodArgumentNotValidException
	}
}
