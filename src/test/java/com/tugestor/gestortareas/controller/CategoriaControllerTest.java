package com.tugestor.gestortareas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.service.CategoriaService;

import jakarta.persistence.EntityNotFoundException;

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.dto.CategoriaRequest;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CategoriaService categoriaService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void listarCategoria_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		when(categoriaService.obtenerTodas())
		.thenReturn(List.of(categoria));

		// Act & Assert
		mockMvc.perform(get("/api/categoria"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].nombre").value("Trabajo"));
	}
	@Test
	void listarCategoria_listaVacia() throws Exception {
		// Arrange
		when(categoriaService.obtenerTodas())
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/categoria"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	void aniadirCategoria_creaCategoria() throws Exception {
		// Arrange
		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Nueva categoria");

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Nueva categoria");

		when(categoriaService.guardarCategoria(any(CategoriaRequest.class)))
		.thenReturn(categoria);

		// Act & Assert
		mockMvc.perform(post("/api/categoria/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nombre").value("Nueva categoria"));
	}
	@Test
	void aniadirCategoria_datosInvalidos() throws Exception {
		// Arrange
		CategoriaRequest request = new CategoriaRequest(); // vacío

		// Act & Assert
		mockMvc.perform(post("/api/categoria/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest());
	}

	@Test
	void eliminarCategoria_eliminaCorrectamente() throws Exception {
		// Arrange
		doNothing().when(categoriaService).eliminarPorId(eq(1L));

		// Act & Assert
		mockMvc.perform(delete("/api/categoria/delete/1"))
		.andExpect(status().isOk());

		verify(categoriaService, times(1)).eliminarPorId(eq(1L));
	}
	@Test
	void eliminarCategoria_categoriaNoEncontrada() throws Exception {
		// Arrange
		doThrow(new EntityNotFoundException("No encontrada"))
		.when(categoriaService).eliminarPorId(eq(99L));

		// Act & Assert
		mockMvc.perform(delete("/api/categoria/delete/99"))
		.andExpect(status().isNotFound());
	}

	@Test
	void modificarCategoria_actualizaCorrectamente() throws Exception {
		// Arrange
		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Categoria actualizada");

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Categoria actualizada");

		when(categoriaService.actualizarCategoria(eq(1L), any(CategoriaRequest.class)))
		.thenReturn(categoria);

		// Act & Assert
		mockMvc.perform(put("/api/categoria/update/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nombre").value("Categoria actualizada"));
	}
	@Test
	void modificarCategoria_datosInvalidos() throws Exception {
		// Arrange
		CategoriaRequest request = new CategoriaRequest(); // vacío

		// Act & Assert
		mockMvc.perform(put("/api/categoria/update/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest());
	}
	@Test
	void modificarCategoria_categoriaNoEncontrada() throws Exception {
		// Arrange
		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Categoria actualizada");

		when(categoriaService.actualizarCategoria(eq(99L), any(CategoriaRequest.class)))
		.thenThrow(new EntityNotFoundException("No encontrada"));

		// Act & Assert
		mockMvc.perform(put("/api/categoria/update/99")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isNotFound());
	}

	@Test
	void listarPorNombre_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		when(categoriaService.obtenerPorNombre(eq("Trab")))
		.thenReturn(List.of(categoria));

		// Act & Assert
		mockMvc.perform(get("/api/categoria/nombre/Trab"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].nombre").value("Trabajo"));
	}
	@Test
	void listarPorNombre_listaVacia() throws Exception {
		// Arrange
		when(categoriaService.obtenerPorNombre(eq("Inexistente")))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/categoria/nombre/Inexistente"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}
}
