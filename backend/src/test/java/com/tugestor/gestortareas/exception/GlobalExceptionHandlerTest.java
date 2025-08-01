package com.tugestor.gestortareas.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;


@WebMvcTest (controllers = DummyExceptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestErrorController.class)

class GlobalExceptionHandlerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void handleRuntime_badRequest() throws Exception {
		mockMvc.perform(get("/runtime"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.error").value("Mensaje de error runtime"));
	}
	
	@Test
	void handleEntityNotFound_notFound() throws Exception {
		mockMvc.perform(get("/entity-not-found"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.error").value("Entidad no encontrada"));
	}
	
	@Test
	void handleAccessDenied_forbidden() throws Exception {
		mockMvc.perform(get("/access-denied"))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.error").value("Acceso denegado"));
	}
	
	@Test
	void handleUsernameNotFound_unauthorized() throws Exception {
		mockMvc.perform(get("/username-not-found"))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.error").value("Credenciales inválidas."));
	}

	@Test
	void handleTypeMismatch_badRequest() throws Exception {
		mockMvc.perform(get("/type-mismatch"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.error", containsString("no es válido para el parámetro")));
	}

	@Test
	void handleConstraintViolation_retorna400ConErrores() throws Exception {
		mockMvc.perform(get("/error/constraint"))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.campo").value("Error de restricción simulado"));
	}
}
