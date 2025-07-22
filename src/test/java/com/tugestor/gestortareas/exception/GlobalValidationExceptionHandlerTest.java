package com.tugestor.gestortareas.exception;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.tugestor.gestortareas.controller.TestValidationController;

@ActiveProfiles("test")
@WebMvcTest(controllers = TestValidationController.class)
@Import(GlobalExceptionHandler.class)
class GlobalValidationExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void handleValidationException_badRequestConErrores() throws Exception {
		String jsonInvalido = "{}";

		mockMvc.perform(post("/error/validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonInvalido)
				.with(csrf())
				.with(user("test").password("test").roles("USER")))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.nombre").value("El campo nombre es obligatorio"));
	}
}
