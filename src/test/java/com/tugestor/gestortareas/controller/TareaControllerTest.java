package com.tugestor.gestortareas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.service.TareaService;

import jakarta.persistence.EntityNotFoundException;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;

@WebMvcTest(TareaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad
class TareaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TareaService tareaService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void listarTareas_devuelveListaDeTareas() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea 1");
		tarea.setTiempo(60);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setDescripcion("Descripción tarea");
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerTodas(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea 1"));
	}
	@Test
	void listarTareas_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerTodas(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void aniadirTarea_creaTareaCorrectamente() throws Exception {
		// Arrange
		TareaRequest request = new TareaRequest();
		request.setTitulo("Nueva tarea");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		request.setDescripcion("Descripción");

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Nueva tarea");
		tarea.setTiempo(30);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setDescripcion("Descripción");
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.guardarTarea(any(TareaRequest.class), anyString()))
		.thenReturn(tarea);

		// Act & Assert
		mockMvc.perform(post("/api/tarea/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "usuario@ejemplo.com")) // << AQUI
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.titulo").value("Nueva tarea"))
		.andExpect(jsonPath("$.tiempo").value(30))
		.andExpect(jsonPath("$.prioridad").value("ALTA"))
		.andExpect(jsonPath("$.categoriaNombre").value("Trabajo"))
		.andExpect(jsonPath("$.idCategoria").value(1))
		.andExpect(jsonPath("$.idUsuario").value(1));
	}
	@Test
	void aniadirTarea_datosInvalidos() throws Exception {
		// Arrange
		TareaRequest request = new TareaRequest(); // Vacío

		// Act & Assert
		mockMvc.perform(post("/api/tarea/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isBadRequest());
	}

	@Test
	void listarTareaId_devuelveTarea() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea prueba");
		tarea.setTiempo(45);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setDescripcion("Descripción");
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerPorId(eq(1L), anyString()))
		.thenReturn(tarea);

		// Act & Assert
		mockMvc.perform(get("/api/tarea/1")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.titulo").value("Tarea prueba"))
		.andExpect(jsonPath("$.prioridad").value("MEDIA"));
	}
	@Test
	void listarTareaId_tareaNoEncontrada() throws Exception {
		// Arrange
		when(tareaService.obtenerPorId(eq(99L), anyString()))
		.thenThrow(new EntityNotFoundException("Tarea no encontrada"));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/99")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isNotFound());
	}

	@Test
	void eliminarTarea_eliminaCorrectamente() throws Exception {
		// Arrange
		doNothing().when(tareaService).eliminarPorId(eq(1L), anyString());

		// Act & Assert
		mockMvc.perform(delete("/api/tarea/delete/1")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk());

		verify(tareaService, times(1)).eliminarPorId(eq(1L), anyString());
	}
	@Test
	void eliminarTarea_tareaNoEncontrada() throws Exception {
		// Arrange
		doThrow(new EntityNotFoundException("Tarea no encontrada"))
		.when(tareaService).eliminarPorId(eq(99L), anyString());

		// Act & Assert
		mockMvc.perform(delete("/api/tarea/delete/99")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isNotFound());
	}

	@Test
	void modificarTarea_actualizaCorrectamente() throws Exception {
		// Arrange
		TareaRequest request = new TareaRequest();
		request.setTitulo("Tarea actualizada");
		request.setTiempo(90);
		request.setPrioridad(Prioridad.BAJA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		request.setDescripcion("Desc actualizada");

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea actualizada");
		tarea.setTiempo(90);
		tarea.setPrioridad(Prioridad.BAJA);
		tarea.setDescripcion("Desc actualizada");
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.actualizarPorId(eq(1L), any(TareaRequest.class), anyString()))
		.thenReturn(tarea);

		// Act & Assert
		mockMvc.perform(put("/api/tarea/update/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.titulo").value("Tarea actualizada"))
		.andExpect(jsonPath("$.prioridad").value("BAJA"));
	}
	@Test
	void modificarTarea_datosInvalidos() throws Exception {
		// Arrange
		TareaRequest request = new TareaRequest(); // vacío

		// Act & Assert
		mockMvc.perform(put("/api/tarea/update/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isBadRequest());
	}
	@Test
	void modificarTarea_tareaNoEncontrada() throws Exception {
		// Arrange
		TareaRequest request = new TareaRequest();
		request.setTitulo("Tarea actualizada");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.MEDIA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		
		when(tareaService.actualizarPorId(eq(99L), any(TareaRequest.class), anyString()))
		.thenThrow(new EntityNotFoundException("No encontrada"));

		// Act & Assert
		mockMvc.perform(put("/api/tarea/update/99")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isNotFound());
	}

	@Test
	void listarPorTitulo_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("A tarea");
		tarea.setTiempo(30);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerPorTitulo(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/titulo")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("A tarea"));
	}
	@Test
	void listarPorTitulo_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerPorTitulo(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/titulo")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarPorTiempo_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea tiempo");
		tarea.setTiempo(45);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerPorTiempo(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/tiempo")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea tiempo"));
	}
	@Test
	void listarPorTiempo_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerPorTiempo(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/tiempo")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarPorPrioridad_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea prioridad");
		tarea.setTiempo(20);
		tarea.setPrioridad(Prioridad.BAJA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerPorPrioridad(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/prioridad")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea prioridad"));
	}
	@Test
	void listarPorPrioridad_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerPorPrioridad(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/prioridad")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarPorFechaEntrega_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea fecha");
		tarea.setTiempo(25);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.obtenerPorFechaEntrega(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/fecha")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea fecha"));
	}
	@Test
	void listarPorFechaEntrega_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerPorFechaEntrega(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/fecha")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void filtrarPorPrioridad_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Filtrada prioridad");
		tarea.setTiempo(50);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.filtrarPorPrioridad(eq("ALTA"), anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/ALTA")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Filtrada prioridad"));
	}
	@Test
	void filtrarPorPrioridad_listaVacia() throws Exception {
		// Arrange
		when(tareaService.filtrarPorPrioridad(eq("ALTA"), anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/ALTA")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void filtrarPorTiempo_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Filtrada tiempo");
		tarea.setTiempo(60);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.filtrarPorTiempo(eq(60), anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/tiempo/60")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Filtrada tiempo"));
	}
	@Test
	void filtrarPorTiempo_listaVacia() throws Exception {
		// Arrange
		when(tareaService.filtrarPorTiempo(eq(60), anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/tiempo/60")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void filtrarPorPalabrasClave_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Informe importante");
		tarea.setTiempo(90);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.filtrarPorPalabrasClave(eq("informe"), anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/palabras/informe")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Informe importante"));
	}
	@Test
	void filtrarPorPalabrasClave_listaVacia() throws Exception {
		// Arrange
		when(tareaService.filtrarPorPalabrasClave(eq("inexistente"), anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/palabras/inexistente")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void filtrarPorCategoria_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(2L);
		categoria.setNombre("Salud");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Cita médica");
		tarea.setTiempo(20);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.filtrarPorCategoria(eq(2L), anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/categoria/2")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Cita médica"));
	}
	@Test
	void filtrarPorCategoria_listaVacia() throws Exception {
		// Arrange
		when(tareaService.filtrarPorCategoria(eq(2L), anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/categoria/2")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void filtrarPorEstado_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea en curso");
		tarea.setTiempo(60);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tareaService.filtrarPorEstado(eq(Estado.EN_CURSO), anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/estado/EN_CURSO")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea en curso"));
	}
	@Test
	void filtrarPorEstado_listaVacia() throws Exception {
		// Arrange
		when(tareaService.filtrarPorEstado(eq(Estado.EN_CURSO), anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/filtrar/estado/EN_CURSO")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void obtenerEstadoTarea_devuelveEstado() throws Exception {
		// Arrange
		when(tareaService.obtenerEstado(eq(1L), anyString()))
		.thenReturn(Estado.EN_CURSO);

		// Act & Assert
		mockMvc.perform(get("/api/tarea/estado/1")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(content().string("\"EN_CURSO\""));
	}
	@Test
	void obtenerEstadoTarea_tareaNoEncontrada() throws Exception {
		// Arrange
		when(tareaService.obtenerEstado(eq(99L), anyString()))
		.thenThrow(new RuntimeException("No encontrada"));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/estado/99")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isBadRequest());
	}

	@Test
	void completarTarea_marcaCompletada() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea completada");
		tarea.setTiempo(30);
		tarea.setPrioridad(Prioridad.ALTA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));
		tarea.setCompletada(true);
		tarea.setFechaCompletada(LocalDateTime.now());

		TareaResponse response = new TareaResponse(tarea);

		when(tareaService.marcarTareaCompletada(eq(1L), anyString()))
		.thenReturn(response);

		// Act & Assert
		mockMvc.perform(patch("/api/tarea/completar/1")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.titulo").value("Tarea completada"))
		.andExpect(jsonPath("$.completada").value(true));
	}
	@Test
	void completarTarea_noAutorizado() throws Exception {
		// Arrange
		when(tareaService.marcarTareaCompletada(eq(1L), anyString()))
		.thenThrow(new AccessDeniedException("No autorizado"));

		// Act & Assert
		mockMvc.perform(patch("/api/tarea/completar/1")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isForbidden());
	}

	@Test
	void listarTareasHoy_devuelveLista() throws Exception {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Usuario usuario = new Usuario(1L);

		Tarea tarea = new Tarea(1L);
		tarea.setTitulo("Tarea de hoy");
		tarea.setTiempo(25);
		tarea.setPrioridad(Prioridad.MEDIA);
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setFechaEntrega(LocalDateTime.now());

		when(tareaService.obtenerTareasHoy(anyString()))
		.thenReturn(List.of(tarea));

		// Act & Assert
		mockMvc.perform(get("/api/tarea/hoy")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].titulo").value("Tarea de hoy"));
	}
	@Test
	void listarTareasHoy_listaVacia() throws Exception {
		// Arrange
		when(tareaService.obtenerTareasHoy(anyString()))
		.thenReturn(List.of());

		// Act & Assert
		mockMvc.perform(get("/api/tarea/hoy")
				.principal(() -> "usuario@ejemplo.com"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}
}
