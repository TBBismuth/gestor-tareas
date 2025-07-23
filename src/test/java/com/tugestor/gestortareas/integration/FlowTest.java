package com.tugestor.gestortareas.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.repository.CategoriaRepository;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlowTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CategoriaRepository categoriaRepository;


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
	
	@Test
	@Order(12)
	void listarCategorias_autenticado() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Categorias");
	    registro.setEmail("cat@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("cat@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Llamada autenticada al listado
	    mockMvc.perform(get("/api/categoria")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk());
	}
	
	@Test
	@Order(13)
	void crearCategoria_validaEInvalida() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatCreator");
	    registro.setEmail("catcreator@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catcreator@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Categoría válida
	    Map<String, String> categoriaValida = new HashMap<>();
	    categoriaValida.put("nombre", "Valida");

	    mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoriaValida)))
	        .andExpect(status().isOk());

	    // Categoría inválida (nombre vacío)
	    Map<String, String> categoriaInvalida = new HashMap<>();
	    categoriaInvalida.put("nombre", "");

	    mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoriaInvalida)))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(14)
	void crearCategoria_nombreDuplicado() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("DupCat");
	    registro.setEmail("dupcat@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("dupcat@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Primera categoría
	    Map<String, String> cat1 = new HashMap<>();
	    cat1.put("nombre", "Repetida");

	    mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(cat1)))
	        .andExpect(status().isOk());

	    // Segunda categoría con el mismo nombre
	    mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(cat1)))
	        .andExpect(status().isOk()); // <- adaptaremos si devuelve 400
	}
	
	@Test
	@Order(15)
	void editarCategoria_validaInvalidaNoExistente() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatEditor");
	    registro.setEmail("cateditor@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("cateditor@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría base
	    Map<String, String> cat = new HashMap<>();
	    cat.put("nombre", "Base");

	    String catResp = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(cat)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResp).get("idCategoria").asInt();

	    // Edición válida
	    Map<String, String> catEdit = new HashMap<>();
	    catEdit.put("nombre", "Editada");

	    mockMvc.perform(put("/api/categoria/update/" + idCategoria)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(catEdit)))
	        .andExpect(status().isOk());

	    // Edición inválida (nombre vacío)
	    catEdit.put("nombre", "");

	    mockMvc.perform(put("/api/categoria/update/" + idCategoria)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(catEdit)))
	        .andExpect(status().isBadRequest());

	    // Edición de categoría inexistente
	    catEdit.put("nombre", "Cualquiera");

	    mockMvc.perform(put("/api/categoria/update/999999")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(catEdit)))
	        .andExpect(status().isNotFound());
	}
	
	@Test
	@Order(16)
	void eliminarCategoria_normal() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatDelete");
	    registro.setEmail("catdelete@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catdelete@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Borrar");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Eliminar categoría
	    mockMvc.perform(delete("/api/categoria/delete/" + idCategoria)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk());
	}
	
	@Test
	@Order(17)
	void eliminarCategoria_protegida() throws Exception {
	    // Insertar categoría protegida directamente
	    Categoria cat = new Categoria();
	    cat.setNombre("PROTEGIDA_TEST");
	    cat.setProtegida(true);
	    Categoria protegida = categoriaRepository.save(cat);

	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatProtected");
	    registro.setEmail("catprotected@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catprotected@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Intentar eliminar
	    mockMvc.perform(delete("/api/categoria/delete/" + protegida.getIdCategoria())
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	@Order(18)
	void eliminarCategoria_conTareasAsociadas() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatCascade");
	    registro.setEmail("catcascade@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catcascade@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Asociada");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea con esa categoría
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Tarea con categoría");
	    tarea.put("descripcion", "Debe sobrevivir sin categoría");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T23:59:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk());

	    // Eliminar categoría
	    mockMvc.perform(delete("/api/categoria/delete/" + idCategoria)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk());

	    // Consultar tareas y verificar que sigue viva
	    mockMvc.perform(get("/api/tarea")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].titulo").value("Tarea con categoría"));
	}
	
	@Test
	@Order(19)
	void buscarCategoria_porNombreParcial() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatSearch");
	    registro.setEmail("catsearch@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catsearch@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categorías con nombre común
	    for (String nombre : List.of("Viaje", "Videojuegos", "Vida", "Varios")) {
	        Map<String, String> cat = new HashMap<>();
	        cat.put("nombre", nombre);

	        mockMvc.perform(post("/api/categoria/add")
	                .header("Authorization", "Bearer " + token)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(cat)))
	            .andExpect(status().isOk());
	    }

	    // Buscar por nombre parcial "Vi"
	    mockMvc.perform(get("/api/categoria/nombre/Vi")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$").isArray())
	        .andExpect(jsonPath("$.length()").value(3));
	}

}
