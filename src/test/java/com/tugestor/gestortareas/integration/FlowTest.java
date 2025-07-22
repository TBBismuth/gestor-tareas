package com.tugestor.gestortareas.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

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
	
	@Test
	@Order(8)
	void listarUsuarios_autenticado() throws Exception {
	    // Registramos un usuario y obtenemos su token
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Listador");
	    registro.setEmail("listador@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("listador@flow.com");
	    login.setPassword("Password123");

	    String token = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andExpect(status().isOk())
	        .andReturn()
	        .getResponse()
	        .getContentAsString();

	    String jwt = objectMapper.readTree(token).get("token").asText();

	    // Llamada autenticada al endpoint
	    mockMvc.perform(get("/api/usuario")
	            .header("Authorization", "Bearer " + jwt))
	        .andExpect(status().isOk());
	}
	
	@Test
	@Order(9)
	void obtenerUsuarioPorId_existenteYNoExistente() throws Exception {
	    // Registramos usuario
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("PorId");
	    registro.setEmail("porid@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    // Login para obtener token
	    LoginRequest login = new LoginRequest();
	    login.setEmail("porid@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();
	    int id = objectMapper.readTree(loginResponse).get("idUsuario").asInt();

	    // Consulta válida por ID existente
	    mockMvc.perform(get("/api/usuario/" + id)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.email").value("porid@flow.com"));

	    // Consulta por ID inexistente
	    mockMvc.perform(get("/api/usuario/999999")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isNotFound());
	}
	
	@Test
	@Order(10)
	void obtenerUsuarioPorEmail_existenteYNoExistente() throws Exception {
	    // Registro usuario
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("EmailCheck");
	    registro.setEmail("emailcheck@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    // Login
	    LoginRequest login = new LoginRequest();
	    login.setEmail("emailcheck@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Consulta por email existente
	    mockMvc.perform(get("/api/usuario/email/emailcheck@flow.com")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.nombre").value("EmailCheck"));

	    // Consulta por email no existente
	    mockMvc.perform(get("/api/usuario/email/noexiste@flow.com")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(11)
	void eliminarUsuario_conTareas_seEliminanCorrectamente() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Eliminar");
	    registro.setEmail("eliminar@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("eliminar@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();
	    int id = objectMapper.readTree(loginResponse).get("idUsuario").asInt();

	    // Crear categoría válida
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Temporal");

	    String categoriaResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(categoriaResponse).get("idCategoria").asInt();

	    // Crear tarea asociada al usuario y la categoría creada
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Tarea temporal");
	    tarea.put("descripcion", "Tarea de prueba");
	    tarea.put("tiempo", 60);
	    tarea.put("fechaEntrega", "2030-12-31T23:59:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk());

	    // Eliminar usuario
	    mockMvc.perform(delete("/api/usuario/delete/" + id)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk());

	    // Verificar que ya no hay tareas del usuario
	    mockMvc.perform(get("/api/tarea")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isUnauthorized());
	}





}
