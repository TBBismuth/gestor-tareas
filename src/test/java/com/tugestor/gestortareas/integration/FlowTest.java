package com.tugestor.gestortareas.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hamcrest.Matchers;
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
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;


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
	
	@Autowired
	private TareaRepository tareaRepository;


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
	
	@Test
	@Order(20)
	void listarTareas_delUsuario() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("TaskLister");
	    registro.setEmail("tasklister@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("tasklister@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Primer listado: sin tareas
	    mockMvc.perform(get("/api/tarea")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.length()").value(0));

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Tareas");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear una tarea
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Tarea simple");
	    tarea.put("descripcion", "Una sola tarea para probar");
	    tarea.put("tiempo", 45);
	    tarea.put("fechaEntrega", "2099-12-31T23:59:00");
	    tarea.put("prioridad", "ALTA");
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk());

	    // Segundo listado: con una tarea
	    mockMvc.perform(get("/api/tarea")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.length()").value(1))
	        .andExpect(jsonPath("$[0].titulo").value("Tarea simple"));
	}
	
	@Test
	@Order(21)
	void crearTarea_valida() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("TaskCreator");
	    registro.setEmail("taskcreator@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("taskcreator@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Trabajo");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Nueva tarea");
	    tarea.put("descripcion", "Tarea creada correctamente");
	    tarea.put("tiempo", 60);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "BAJA");
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.titulo").value("Nueva tarea"))
	        .andExpect(jsonPath("$.completada").value(false));
	}
	
	@Test
	@Order(22)
	void crearTarea_categoriaInexistente() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CatNotFound");
	    registro.setEmail("catnotfound@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("catnotfound@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Intentar crear tarea con categoría inválida
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Categoría inválida");
	    tarea.put("descripcion", "La categoría no existe");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", 999999L); // ID inexistente

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(23)
	void crearTarea_datosInvalidos_individuales() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("InvalidFields");
	    registro.setEmail("invalidfields@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("invalidfields@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría válida para usar en los tests
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "ValidCat");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // 1. Título vacío
	    Map<String, Object> tarea1 = new HashMap<>();
	    tarea1.put("titulo", "");
	    tarea1.put("descripcion", "desc");
	    tarea1.put("tiempo", 10);
	    tarea1.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea1.put("prioridad", "MEDIA");
	    tarea1.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea1)))
	        .andExpect(status().isBadRequest());

	    // 2. Tiempo <= 0
	    Map<String, Object> tarea2 = new HashMap<>();
	    tarea2.put("titulo", "Título");
	    tarea2.put("descripcion", "desc");
	    tarea2.put("tiempo", 0);
	    tarea2.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea2.put("prioridad", "MEDIA");
	    tarea2.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea2)))
	        .andExpect(status().isBadRequest());

	    // 3. Fecha de entrega pasada
	    Map<String, Object> tarea3 = new HashMap<>();
	    tarea3.put("titulo", "Título");
	    tarea3.put("descripcion", "desc");
	    tarea3.put("tiempo", 10);
	    tarea3.put("fechaEntrega", "2000-01-01T00:00:00");
	    tarea3.put("prioridad", "MEDIA");
	    tarea3.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea3)))
	        .andExpect(status().isBadRequest());

	    // 4. Prioridad inválida
	    Map<String, Object> tarea4 = new HashMap<>();
	    tarea4.put("titulo", "Título");
	    tarea4.put("descripcion", "desc");
	    tarea4.put("tiempo", 10);
	    tarea4.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea4.put("prioridad", "ALTÍSIMA"); // inválido
	    tarea4.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea4)))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(24)
	void crearTarea_completadaSinFechaCompletado() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("SinFechaCompletado");
	    registro.setEmail("sinfecha@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("sinfecha@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Valida");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Intentar crear tarea completada sin fechaCompletado
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Completada sin fecha");
	    tarea.put("descripcion", "Esto no debe permitirse");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("completada", true); // pero sin fechaCompletado
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(25)
	void crearTarea_completadaConFechaCompletado() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("CompletaDeInicio");
	    registro.setEmail("completainicio@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("completainicio@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Completa");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Intentar crear tarea ya completada
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Completada desde inicio");
	    tarea.put("descripcion", "Esto no se permite");
	    tarea.put("tiempo", 45);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("completada", true);
	    tarea.put("fechaCompletado", "2099-12-01T10:00:00");
	    tarea.put("idCategoria", idCategoria);

	    mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(26)
	void obtenerTarea_porId_variantes() throws Exception {
	    // Usuario A: propietario de la tarea
	    UsuarioRequest userA = new UsuarioRequest();
	    userA.setNombre("Dueño");
	    userA.setEmail("dueno@flow.com");
	    userA.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(userA)))
	        .andExpect(status().isCreated());

	    LoginRequest loginA = new LoginRequest();
	    loginA.setEmail("dueno@flow.com");
	    loginA.setPassword("Password123");

	    String loginResponseA = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(loginA)))
	        .andReturn().getResponse().getContentAsString();

	    String tokenA = objectMapper.readTree(loginResponseA).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Consulta");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + tokenA)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Consulta por ID");
	    tarea.put("descripcion", "Debe poder accederse");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + tokenA)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // ✅ Acceso a tarea propia
	    mockMvc.perform(get("/api/tarea/" + idTarea)
	            .header("Authorization", "Bearer " + tokenA))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.titulo").value("Consulta por ID"));

	    // ❌ Tarea inexistente
	    mockMvc.perform(get("/api/tarea/999999")
	            .header("Authorization", "Bearer " + tokenA))
	        .andExpect(status().isNotFound());

	    // Usuario B: intenta acceder a tarea de otro
	    UsuarioRequest userB = new UsuarioRequest();
	    userB.setNombre("Ajeno");
	    userB.setEmail("ajeno@flow.com");
	    userB.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(userB)))
	        .andExpect(status().isCreated());

	    LoginRequest loginB = new LoginRequest();
	    loginB.setEmail("ajeno@flow.com");
	    loginB.setPassword("Password123");

	    String loginResponseB = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(loginB)))
	        .andReturn().getResponse().getContentAsString();

	    String tokenB = objectMapper.readTree(loginResponseB).get("token").asText();

	    // ❌ Acceso a tarea ajena
	    mockMvc.perform(get("/api/tarea/" + idTarea)
	            .header("Authorization", "Bearer " + tokenB))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	@Order(27)
	void editarTarea_propiaConDatosValidos() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Editor");
	    registro.setEmail("editor@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("editor@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Editar");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea original
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Original");
	    tarea.put("descripcion", "Antes de editar");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Editar la tarea
	    Map<String, Object> modificacion = new HashMap<>();
	    modificacion.put("titulo", "Modificada");
	    modificacion.put("descripcion", "Después de editar");
	    modificacion.put("tiempo", 45);
	    modificacion.put("fechaEntrega", "2099-12-31T12:00:00");
	    modificacion.put("prioridad", "ALTA");
	    modificacion.put("idCategoria", idCategoria);

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(modificacion)))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.titulo").value("Modificada"))
	        .andExpect(jsonPath("$.prioridad").value("ALTA"));
	}
	
	@Test
	@Order(28)
	void editarTarea_completarConFechaValida() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Completable");
	    registro.setEmail("completable@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("completable@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Completar");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "A completar");
	    tarea.put("descripcion", "Aún no está completada");
	    tarea.put("tiempo", 40);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "BAJA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Editar: marcar como completada con fecha válida
	    Map<String, Object> modificacion = new HashMap<>();
	    modificacion.put("titulo", "A completar");
	    modificacion.put("descripcion", "Ya está completada");
	    modificacion.put("tiempo", 40);
	    modificacion.put("fechaEntrega", "2099-12-31T12:00:00");
	    modificacion.put("prioridad", "BAJA");
	    modificacion.put("completada", true);
	    modificacion.put("fechaCompletada", "2099-10-01T10:00:00"); // CORREGIDO: femenino
	    modificacion.put("idCategoria", idCategoria);

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(modificacion)))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.completada").value(true))
	        .andExpect(jsonPath("$.fechaCompletada").value("2099-10-01T10:00:00"));
	}
	
	@Test
	@Order(29)
	void editarTarea_incompletaConFechaCompletada_debeFallar() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Incongruente");
	    registro.setEmail("incongruente@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("incongruente@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Incoherente");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea válida
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Caso incoherente");
	    tarea.put("descripcion", "No está completada");
	    tarea.put("tiempo", 30);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Intentar editar: completada = false con fechaCompletada ≠ null
	    Map<String, Object> modificacion = new HashMap<>();
	    modificacion.put("titulo", "Incoherente editada");
	    modificacion.put("descripcion", "Intento inválido");
	    modificacion.put("tiempo", 30);
	    modificacion.put("fechaEntrega", "2099-12-31T12:00:00");
	    modificacion.put("prioridad", "MEDIA");
	    modificacion.put("completada", false);
	    modificacion.put("fechaCompletada", "2099-10-01T10:00:00");
	    modificacion.put("idCategoria", idCategoria);

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(modificacion)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.error").value("No se puede asignar una fecha completada a una tarea no completada."));
	}
	
	@Test
	@Order(30)
	void editarTarea_fechaEntregaPasada_debeFallar() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Anticipado");
	    registro.setEmail("anticipado@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("anticipado@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Pasado");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea válida
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Valida inicial");
	    tarea.put("descripcion", "Será invalidada");
	    tarea.put("tiempo", 20);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "ALTA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Intentar modificar con fecha en el pasado
	    Map<String, Object> modificacion = new HashMap<>();
	    modificacion.put("titulo", "Invalida");
	    modificacion.put("descripcion", "Fecha inválida");
	    modificacion.put("tiempo", 20);
	    modificacion.put("fechaEntrega", "2000-01-01T10:00:00"); // Fecha en el pasado
	    modificacion.put("prioridad", "ALTA");
	    modificacion.put("completada", false);
	    modificacion.put("fechaCompletada", null);
	    modificacion.put("idCategoria", idCategoria);

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(modificacion)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.error").value("La fecha de entrega no puede haber pasado."));
	}
	
	@Test
	@Order(31)
	void eliminarTarea_propiaInexistenteAjena() throws Exception {
	    // Usuario 1 (propietario de la tarea)
	    UsuarioRequest u1 = new UsuarioRequest();
	    u1.setNombre("Propietario");
	    u1.setEmail("propietario@flow.com");
	    u1.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(u1)))
	        .andExpect(status().isCreated());

	    LoginRequest login1 = new LoginRequest();
	    login1.setEmail("propietario@flow.com");
	    login1.setPassword("Password123");

	    String loginResp1 = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login1)))
	        .andReturn().getResponse().getContentAsString();

	    String token1 = objectMapper.readTree(loginResp1).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Eliminar");

	    String catResp = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResp).get("idCategoria").asInt();

	    // Crear tarea
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Tarea para borrar");
	    tarea.put("descripcion", "Temporal");
	    tarea.put("tiempo", 15);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "BAJA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResp = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResp).get("idTarea").asLong();

	    // Eliminar tarea propia
	    mockMvc.perform(delete("/api/tarea/delete/" + idTarea)
	            .header("Authorization", "Bearer " + token1))
	        .andExpect(status().isOk());

	    // Intentar eliminar tarea inexistente (ya eliminada)
	    mockMvc.perform(delete("/api/tarea/delete/" + idTarea)
	            .header("Authorization", "Bearer " + token1))
	        .andExpect(status().isNotFound());

	    // Crear usuario 2 (que intentará eliminar una tarea ajena)
	    UsuarioRequest u2 = new UsuarioRequest();
	    u2.setNombre("Intruso");
	    u2.setEmail("intruso@flow.com");
	    u2.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(u2)))
	        .andExpect(status().isCreated());

	    LoginRequest login2 = new LoginRequest();
	    login2.setEmail("intruso@flow.com");
	    login2.setPassword("Password123");

	    String loginResp2 = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login2)))
	        .andReturn().getResponse().getContentAsString();

	    String token2 = objectMapper.readTree(loginResp2).get("token").asText();

	    // Usuario 1 crea nueva tarea
	    String tareaResp2 = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andReturn().getResponse().getContentAsString();

	    long idTareaAjena = objectMapper.readTree(tareaResp2).get("idTarea").asLong();

	    // Usuario 2 intenta eliminarla
	    mockMvc.perform(delete("/api/tarea/delete/" + idTareaAjena)
	            .header("Authorization", "Bearer " + token2))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	@Order(32)
	void estadoTarea_fechaFuturaSinCompletar_enCurso() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("EstadoFuturo");
	    registro.setEmail("estadofuturo@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("estadofuturo@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Estados");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea futura y sin completar
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Pendiente");
	    tarea.put("descripcion", "Estado futuro");
	    tarea.put("tiempo", 10);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "BAJA");
	    tarea.put("completada", false);
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Consultar estado
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(content().string(Matchers.containsString("EN_CURSO")));
	}
	
	@Test
	@Order(33)
	void estadoTarea_simuladaVencida() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("EstadoPasado");
	    registro.setEmail("estadopasado@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("estadopasado@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Pasadas");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea con fecha futura (válida)
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Simulada vencida");
	    tarea.put("descripcion", "Tarea que simula vencimiento");
	    tarea.put("tiempo", 10);
	    tarea.put("fechaEntrega", "2099-01-01T12:00:00");
	    tarea.put("prioridad", "BAJA");
	    tarea.put("completada", false);
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Simular vencimiento directamente desde el repositorio
	    Optional<Tarea> tareaBD = tareaRepository.findById(idTarea);
	    tareaBD.ifPresent(t -> {
	        t.setFechaEntrega(LocalDateTime.of(2000, 1, 1, 10, 0)); // pasado
	        tareaRepository.save(t);
	    });

	    // Consultar estado
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(content().string(Matchers.containsString("VENCIDA")));
	}
	
	@Test
	@Order(34)
	void estadoTarea_completadaAntesVencimiento_completada() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Completador");
	    registro.setEmail("completador@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("completador@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Completadas");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea sin completar
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Completar a tiempo");
	    tarea.put("descripcion", "Completada antes de vencer");
	    tarea.put("tiempo", 20);
	    tarea.put("fechaEntrega", "2099-01-01T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("completada", false);
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Marcar tarea como completada ahora
	    Map<String, Object> actualizacion = new HashMap<>(tarea);
	    actualizacion.put("completada", true);
	    actualizacion.put("fechaCompletada", LocalDateTime.now().toString());

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(actualizacion)))
	        .andExpect(status().isOk());

	    // Consultar estado
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(content().string(Matchers.containsString("COMPLETADA")));
	}
	
	@Test
	@Order(35)
	void estadoTarea_completadaConRetraso() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("Retrasado");
	    registro.setEmail("retrasado@flow.com");
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("retrasado@flow.com");
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Retrasadas");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea futura
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Completada tarde");
	    tarea.put("descripcion", "Esta tarea fue completada con retraso");
	    tarea.put("tiempo", 15);
	    tarea.put("fechaEntrega", "2099-01-01T12:00:00");
	    tarea.put("prioridad", "ALTA");
	    tarea.put("completada", false);
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Simular vencimiento
	    Optional<Tarea> tareaBD = tareaRepository.findById(idTarea);
	    tareaBD.ifPresent(t -> {
	        t.setFechaEntrega(LocalDateTime.of(2000, 1, 1, 10, 0)); // vencida
	        tareaRepository.save(t);
	    });

	    // Marcar como completada con fecha actual
	    Map<String, Object> actualizacion = new HashMap<>(tarea);
	    actualizacion.put("completada", true);
	    actualizacion.put("fechaCompletada", LocalDateTime.now().toString());

	    mockMvc.perform(put("/api/tarea/update/" + idTarea)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(actualizacion)))
	        .andExpect(status().isOk());

	    // Consultar estado
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(content().string(Matchers.containsString("COMPLETADA_CON_RETRASO")));
	}
	
	@Test
	@Order(36)
	void estadoTarea_sinFechaEntrega_consultaYFiltro() throws Exception {
	    // Registro y login
	    UsuarioRequest registro = new UsuarioRequest();
	    registro.setNombre("SinFecha");
	    registro.setEmail("sinfecha2@flow.com"); // cambiado
	    registro.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(registro)))
	        .andExpect(status().isCreated());

	    LoginRequest login = new LoginRequest();
	    login.setEmail("sinfecha2@flow.com"); // cambiado
	    login.setPassword("Password123");

	    String loginResponse = mockMvc.perform(post("/api/usuario/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(login)))
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear categoría
	    Map<String, String> categoria = new HashMap<>();
	    categoria.put("nombre", "Indefinidas");

	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(categoria)))
	        .andReturn().getResponse().getContentAsString();

	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    // Crear tarea sin fechaEntrega
	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Tarea sin fecha");
	    tarea.put("descripcion", "Una tarea sin fecha de entrega");
	    tarea.put("tiempo", 10);
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("completada", false);
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // 1. Consultar estado directamente
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(content().string(Matchers.containsString("SIN_FECHA")));

	    // 2. Consultar filtro por estado
	    mockMvc.perform(get("/api/tarea/filtrar/estado/SIN_FECHA")
	            .header("Authorization", "Bearer " + token))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].idTarea").value(idTarea))
	        .andExpect(jsonPath("$[0].estado").value("SIN_FECHA"));
	}
	
	@Test
	@Order(37)
	void estadoTarea_noExistenteYOtraPersona_debeDarErrores() throws Exception {
	    // Usuario 1: dueñ@ de una tarea
	    UsuarioRequest user1 = new UsuarioRequest();
	    user1.setNombre("Dueño");
	    user1.setEmail("dueno2@flow.com");
	    user1.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(user1)))
	        .andExpect(status().isCreated());

	    LoginRequest login1 = new LoginRequest();
	    login1.setEmail("dueno2@flow.com");
	    login1.setPassword("Password123");

	    String token1 = objectMapper.readTree(
	            mockMvc.perform(post("/api/usuario/login")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(login1)))
	                .andExpect(status().isOk())
	                .andReturn().getResponse().getContentAsString())
	            .get("token").asText();

	    // Crear categoría y tarea
	    Map<String, String> cat = Map.of("nombre", "Privada");
	    String catResponse = mockMvc.perform(post("/api/categoria/add")
	            .header("Authorization", "Bearer " + token1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(cat)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();
	    int idCategoria = objectMapper.readTree(catResponse).get("idCategoria").asInt();

	    Map<String, Object> tarea = new HashMap<>();
	    tarea.put("titulo", "Privada");
	    tarea.put("descripcion", "Ajena");
	    tarea.put("tiempo", 10);
	    tarea.put("fechaEntrega", "2099-12-31T12:00:00");
	    tarea.put("prioridad", "MEDIA");
	    tarea.put("idCategoria", idCategoria);

	    String tareaResponse = mockMvc.perform(post("/api/tarea/add")
	            .header("Authorization", "Bearer " + token1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(tarea)))
	        .andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();
	    long idTarea = objectMapper.readTree(tareaResponse).get("idTarea").asLong();

	    // Usuario 2: intenta acceder a la tarea del usuario 1
	    UsuarioRequest user2 = new UsuarioRequest();
	    user2.setNombre("Ajeno");
	    user2.setEmail("ajeno2@flow.com");
	    user2.setPassword("Password123");

	    mockMvc.perform(post("/api/usuario/add")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(user2)))
	        .andExpect(status().isCreated());

	    LoginRequest login2 = new LoginRequest();
	    login2.setEmail("ajeno2@flow.com");
	    login2.setPassword("Password123");

	    String token2 = objectMapper.readTree(
	            mockMvc.perform(post("/api/usuario/login")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(login2)))
	                .andExpect(status().isOk())
	                .andReturn().getResponse().getContentAsString())
	            .get("token").asText();

	    // Caso 1: tarea que no existe
	    mockMvc.perform(get("/api/tarea/estado/999999")
	            .header("Authorization", "Bearer " + token2))
	        .andExpect(status().isBadRequest());

	    // Caso 2: tarea ajena
	    mockMvc.perform(get("/api/tarea/estado/" + idTarea)
	            .header("Authorization", "Bearer " + token2))
	        .andExpect(status().isForbidden());
	}


}
