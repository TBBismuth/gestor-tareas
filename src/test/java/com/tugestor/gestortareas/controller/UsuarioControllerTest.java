package com.tugestor.gestortareas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;
import com.tugestor.gestortareas.security.JwtService;
import com.tugestor.gestortareas.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UsuarioService usuarioService;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void listarUsuarios_devuelveLista() throws Exception {
		// Arrange
		Usuario usuario = new Usuario(1L);
		usuario.setNombre("Miguel");
		usuario.setEmail("miguel@ejemplo.com");

		when(usuarioService.obtenerTodos())
		.thenReturn(List.of(usuario));

		// Act & Assert
		mockMvc.perform(get("/api/usuario"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].nombre").value("Miguel"))
		.andExpect(jsonPath("$[0].email").value("miguel@ejemplo.com"));
	}
	@Test
	void listarUsuarios_listaVacia() throws Exception {
		// Arrange
		when(usuarioService.obtenerTodos())
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/usuario"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarUsuarioId_devuelveUsuario() throws Exception {
		// Arrange
		Usuario usuario = new Usuario(1L);
		usuario.setNombre("Miguel");
		usuario.setEmail("miguel@ejemplo.com");

		when(usuarioService.obtenerPorId(eq(1L)))
		.thenReturn(usuario);

		// Act & Assert
		mockMvc.perform(get("/api/usuario/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nombre").value("Miguel"))
		.andExpect(jsonPath("$.email").value("miguel@ejemplo.com"));
	}
	@Test
	void listarUsuarioId_noEncontrado() throws Exception {
		// Arrange
		when(usuarioService.obtenerPorId(eq(99L)))
		.thenThrow(new EntityNotFoundException("No encontrado"));

		// Act & Assert
		mockMvc.perform(get("/api/usuario/99"))
		.andExpect(status().isNotFound());
	}

	@Test
	void listarUsuarioEmail_devuelveUsuario() throws Exception {
		// Arrange
		Usuario usuario = new Usuario(1L);
		usuario.setNombre("Miguel");
		usuario.setEmail("miguel@ejemplo.com");

		when(usuarioService.obtenerPorEmail(eq("miguel@ejemplo.com")))
		.thenReturn(Optional.of(usuario));

		// Act & Assert
		mockMvc.perform(get("/api/usuario/email/miguel@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nombre").value("Miguel"))
		.andExpect(jsonPath("$.email").value("miguel@ejemplo.com"));
	}
	@Test
	void listarUsuarioEmail_noEncontrado() throws Exception {
		// Arrange
		when(usuarioService.obtenerPorEmail(eq("inexistente@ejemplo.com")))
		.thenReturn(Optional.empty());

		// Act & Assert
		mockMvc.perform(get("/api/usuario/email/inexistente@ejemplo.com"))
		.andExpect(status().isBadRequest());
	}

	@Test
	void aniadirUsuario_creaUsuario() throws Exception {
		// Arrange
		UsuarioRequest request = new UsuarioRequest();
		request.setNombre("Nuevo usuario");
		request.setEmail("nuevo@ejemplo.com");
		request.setPassword("password123");

		Usuario usuario = new Usuario(1L);
		usuario.setNombre("Nuevo usuario");
		usuario.setEmail("nuevo@ejemplo.com");

		when(usuarioService.guardarUsuario(any(UsuarioRequest.class)))
		.thenReturn(usuario);

		// Act & Assert
		mockMvc.perform(post("/api/usuario/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.nombre").value("Nuevo usuario"))
		.andExpect(jsonPath("$.email").value("nuevo@ejemplo.com"));
	}
	@Test
	void aniadirUsuario_datosInvalidos() throws Exception {
		// Arrange
		UsuarioRequest request = new UsuarioRequest(); // vacío

		// Act & Assert
		mockMvc.perform(post("/api/usuario/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest());
	}

	@Test
	void eliminarUsuario_eliminaCorrectamente() throws Exception {
		// Arrange
		doNothing().when(usuarioService).eliminarPorId(eq(1L));

		// Act & Assert
		mockMvc.perform(delete("/api/usuario/delete/1"))
		.andExpect(status().isOk());

		verify(usuarioService, times(1)).eliminarPorId(eq(1L));
	}
	@Test
	void eliminarUsuario_noEncontrado() throws Exception {
		// Arrange
		doThrow(new EntityNotFoundException("No encontrado"))
		.when(usuarioService).eliminarPorId(eq(99L));

		// Act & Assert
		mockMvc.perform(delete("/api/usuario/delete/99"))
		.andExpect(status().isNotFound());
	}

	@Test
	void loginUsuario_loginCorrecto() throws Exception {
		// Arrange
		LoginRequest request = new LoginRequest();
		request.setEmail("usuario@ejemplo.com");
		request.setPassword("password123");

		Authentication authentication = mock(Authentication.class);
		UserDetails userDetails = mock(UserDetails.class);
		Usuario usuario = new Usuario(1L);
		usuario.setNombre("Miguel");
		usuario.setEmail("usuario@ejemplo.com");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
		.thenReturn(authentication);

		when(authentication.getPrincipal())
		.thenReturn(userDetails);

		when(userDetails.getUsername())
		.thenReturn("usuario@ejemplo.com");

		when(jwtService.generateToken(userDetails))
		.thenReturn("fake-jwt-token");

		when(usuarioRepository.findByEmail("usuario@ejemplo.com"))
		.thenReturn(Optional.of(usuario));

		// Act & Assert
		mockMvc.perform(post("/api/usuario/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nombre").value("Miguel"))
		.andExpect(jsonPath("$.email").value("usuario@ejemplo.com"))
		.andExpect(jsonPath("$.token").value("fake-jwt-token"));
	}
	@Test
	void loginUsuario_credencialesInvalidas() throws Exception {
		// Arrange
		LoginRequest request = new LoginRequest();
		request.setEmail("usuario@ejemplo.com");
		request.setPassword("wrong-password");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
		.thenThrow(new RuntimeException("Credenciales inválidas"));

		// Act & Assert
		mockMvc.perform(post("/api/usuario/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest());
	}
}
