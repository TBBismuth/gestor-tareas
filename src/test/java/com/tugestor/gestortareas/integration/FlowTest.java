package com.tugestor.gestortareas.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.UsuarioRequest;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlowTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@Order(1)
	void registroUsuario_exitoso() throws Exception {
		UsuarioRequest request = new UsuarioRequest();
		request.setNombre("Test User");
		request.setEmail("test@flow.com");
		request.setPassword("Password123");

		mockMvc.perform(post("/api/usuario/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.nombre").value("Test User"))
		.andExpect(jsonPath("$.email").value("test@flow.com"));
	}

	@Test
	@Order(2)
	void registroUsuario_emailDuplicado() throws Exception {
		UsuarioRequest request = new UsuarioRequest();
		request.setNombre("Otro usuario");
		request.setEmail("test@flow.com"); // mismo email que el test anterior
		request.setPassword("Password123");

		mockMvc.perform(post("/api/usuario/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(3)
	void registroUsuario_datosInvalidos() throws Exception {
	    UsuarioRequest request = new UsuarioRequest();
	    request.setNombre("");                    // @NotBlank
	    request.setEmail("no-es-email");          // @Email
	    request.setPassword("password");          // no cumple el patrón

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.nombre").exists())
	        .andExpect(jsonPath("$.email").exists())
	        .andExpect(jsonPath("$.password").exists());
	}
	
	@Test
	@Order(4)
	void loginUsuario_exitoso() throws Exception {
	    // Primero registramos al usuario
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Login Test");
	    registro.setEmail("login@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    // Luego hacemos login con las mismas credenciales
	    LoginRequest login = new LoginRequest();
	    login.setEmail("login@flow.com");
	    login.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.token").exists())
	        .andExpect(jsonPath("$.email").value("login@flow.com"));
	}
	
	@Test
	@Order(5)
	void loginUsuario_emailInexistente() throws Exception {
	    LoginRequest login = new LoginRequest();
	    login.setEmail("noexiste@flow.com");
	    login.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andExpect(status().isUnauthorized());
	}
	
	@Test
	@Order(6)
	void loginUsuario_contraseñaIncorrecta() throws Exception {
	    // Registramos un usuario válido
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Contraseña Incorrecta");
	    registro.setEmail("malpass@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    // Intentamos login con contraseña equivocada
	    LoginRequest login = new LoginRequest();
	    login.setEmail("malpass@flow.com");
	    login.setPassword("WrongPassword1");

	    mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andExpect(status().isUnauthorized());
	}
	
	@Test
	@Order(7)
	void accesoSinToken_rechazado() throws Exception {
	    mockMvc.perform(post("/api/usuario/delete/1"))  // endpoint protegido
	        .andExpect(status().isForbidden());
	}
}
