package com.tugestor.gestortareas.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
public class TareaServiceImplTest {
	// Inyección de dependencias con Mockito (simuladas)
	@Mock
	private TareaRepository tr;
	@Mock
	private CategoriaRepository cr;
	@Mock
	private UsuarioRepository ur;
	// La clase que estamos probando con los mocks inyectados
	@InjectMocks
	private TareaServiceImpl tsimpl;
	
	@Test
	void guardarTarea_tareaGuardadaCorrectamente() {
		// Arrange de datos y mocks
		String emailUsuario = "usuario@ejemplo.com";
		
		TareaRequest request = new TareaRequest();
		request.setTitulo("Titulo Test");
		request.setTiempo(60);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(java.time.LocalDateTime.now().plusDays(1));
		request.setDescripcion("Descripción del test");
		request.setIdCategoria(1L);
		
		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);
		
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Categoria Test");
		
		// Simular el comportamiento de los repositorios
		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(cr.findById(1L)).thenReturn(Optional.of(categoria));
		// Simular que el save devuelve la tarea guardada
		when(tr.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		// Act
		Tarea tareaGuardada = tsimpl.guardarTarea(request, emailUsuario);

		// Assert
		assertNotNull(tareaGuardada);
		assertEquals("Titulo Test", tareaGuardada.getTitulo());
		assertEquals("Descripción del test", tareaGuardada.getDescripcion());
		assertEquals(Prioridad.ALTA, tareaGuardada.getPrioridad());
		assertEquals(categoria, tareaGuardada.getCategoria());
		assertEquals(usuario, tareaGuardada.getUsuario());
		assertFalse(tareaGuardada.isCompletada());

		// Verificar que save() se llamó
		verify(tr, times(1)).save(any(Tarea.class));
	}
	@Test
	void guardarTarea_lanzaExcepcionSiUsuarioNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		request.setIdCategoria(1L);

		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			tsimpl.guardarTarea(request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void guardarTarea_lanzaExcepcionSiCategoriaNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		request.setIdCategoria(99L);

		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(cr.findById(99L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.guardarTarea(request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void guardarTarea_lanzaExcepcionSiCompletadaSinFecha() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(1));
		request.setIdCategoria(1L);
		request.setCompletada(true);
		request.setFechaCompletada(null);

		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(cr.findById(1L)).thenReturn(Optional.of(categoria));

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.guardarTarea(request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void guardarTarea_lanzaExcepcionSiFechaCompletadaAnteriorAFechaAgregado() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(2));
		request.setIdCategoria(1L);
		request.setCompletada(true);
		request.setFechaCompletada(LocalDateTime.now().minusDays(2)); // Fecha completada anterior

		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(cr.findById(1L)).thenReturn(Optional.of(categoria));

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.guardarTarea(request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void guardarTarea_lanzaExcepcionSiFechaEntregaPasada() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().minusDays(1)); // Fecha en pasado
		request.setIdCategoria(1L);

		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(cr.findById(1L)).thenReturn(Optional.of(categoria));

		// Act & Assert
		assertThrows(ValidationException.class, () -> {
			tsimpl.guardarTarea(request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	
	@Test
	void obtenerTodas_listaDeTareas() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Tarea tarea1 = new Tarea();
		tarea1.setTitulo("Tarea 1");

		Tarea tarea2 = new Tarea();
		tarea2.setTitulo("Tarea 2");

		List<Tarea> tareasSimuladas = List.of(tarea1, tarea2);

		when(tr.findByUsuarioEmail(emailUsuario)).thenReturn(tareasSimuladas);

		// Act
		List<Tarea> resultado = tsimpl.obtenerTodas(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertEquals("Tarea 1", resultado.get(0).getTitulo());
		assertEquals("Tarea 2", resultado.get(1).getTitulo());

		// Verificar que se llamó al repositorio
		verify(tr, times(1)).findByUsuarioEmail(emailUsuario);
	}
	@Test
	void obtenerTodas_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		when(tr.findByUsuarioEmail(emailUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.obtenerTodas(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuarioEmail(emailUsuario);
	}
	
	@Test
	void obtenerPorId_tareaSiPerteneceAlUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(usuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act
		Tarea resultado = tsimpl.obtenerPorId(idTarea, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(idTarea, resultado.getIdTarea());
		assertEquals(usuario, resultado.getUsuario());

		// Verificar llamada
		verify(tr, times(1)).findById(idTarea);
	}
	@Test
	void obtenerPorId_lanzaExcepcionSiNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 99L;

		when(tr.findById(idTarea)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			tsimpl.obtenerPorId(idTarea, emailUsuario);
		});

		// Verificar llamada
		verify(tr, times(1)).findById(idTarea);
	}
	@Test
	void obtenerPorId_lanzaExcepcionSiTareaEsDeOtroUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		String otroEmail = "otro@ejemplo.com";
		Long idTarea = 1L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail(otroEmail);

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(otroUsuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			tsimpl.obtenerPorId(idTarea, emailUsuario);
		});

		// Verificar llamada
		verify(tr, times(1)).findById(idTarea);
	}
	
	@Test
	void eliminarPorId_tareaEliminadaCorrectamente() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(usuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act
		tsimpl.eliminarPorId(idTarea, emailUsuario);

		// Assert
		verify(tr, times(1)).delete(tarea);
	}
	@Test
	void eliminarPorId_lanzaExcepcionSiTareaNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 99L;

		when(tr.findById(idTarea)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			tsimpl.eliminarPorId(idTarea, emailUsuario);
		});

		verify(tr, never()).delete(any(Tarea.class));
	}
	@Test
	void eliminarPorId_lanzaExcepcionSiTareaEsDeOtroUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 2L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail("otro@ejemplo.com");

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(otroUsuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			tsimpl.eliminarPorId(idTarea, emailUsuario);
		});

		verify(tr, never()).delete(any(Tarea.class));
	}
	
	@Test
	void actualizarPorId_actualizaTareaCorrectamente() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Trabajo");

		Tarea tareaExistente = new Tarea(1L);
		tareaExistente.setTitulo("Antiguo título");
		tareaExistente.setFechaAgregado(LocalDateTime.now().minusDays(2));
		tareaExistente.setUsuario(usuario);
		tareaExistente.setCategoria(categoria);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tareaExistente));

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título actualizado");
		request.setDescripcion("Descripción actualizada");
		request.setTiempo(90);
		request.setPrioridad(Prioridad.MEDIA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(3));
		request.setCompletada(false);

		when(tr.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Tarea tareaActualizada = tsimpl.actualizarPorId(idTarea, request, emailUsuario);

		// Assert
		assertNotNull(tareaActualizada);
		assertEquals("Título actualizado", tareaActualizada.getTitulo());
		assertEquals("Descripción actualizada", tareaActualizada.getDescripcion());
		assertEquals(90, tareaActualizada.getTiempo());
		assertEquals(Prioridad.MEDIA, tareaActualizada.getPrioridad());
		assertEquals(usuario, tareaActualizada.getUsuario());
		assertFalse(tareaActualizada.isCompletada());

		verify(tr, times(1)).save(any(Tarea.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiTareaNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 99L;

		when(tr.findById(idTarea)).thenReturn(Optional.empty());

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(2));

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.actualizarPorId(idTarea, request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiTareaEsDeOtroUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 2L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail("otro@ejemplo.com");

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(otroUsuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(2));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			tsimpl.actualizarPorId(idTarea, request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiTareaCompletadaSinFecha() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 3L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tareaExistente = new Tarea(1L);
		tareaExistente.setFechaAgregado(LocalDateTime.now().minusDays(1));
		tareaExistente.setUsuario(usuario);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tareaExistente));

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(2));
		request.setCompletada(true); // Indicamos que está completada pero sin fecha

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.actualizarPorId(idTarea, request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiFechaCompletadaAnteriorAFechaAgregado() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 4L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tareaExistente = new Tarea(1l);
		tareaExistente.setUsuario(usuario);
		tareaExistente.setFechaAgregado(LocalDateTime.now().minusDays(1));

		when(tr.findById(idTarea)).thenReturn(Optional.of(tareaExistente));

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().plusDays(2));
		request.setCompletada(true);
		request.setFechaCompletada(LocalDateTime.now().minusDays(2)); // Fecha completada anterior

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.actualizarPorId(idTarea, request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiFechaEntregaPasada() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 5L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tareaExistente = new Tarea(1l);
		tareaExistente.setUsuario(usuario);
		tareaExistente.setFechaAgregado(LocalDateTime.now().minusDays(1));

		when(tr.findById(idTarea)).thenReturn(Optional.of(tareaExistente));

		TareaRequest request = new TareaRequest();
		request.setTitulo("Título");
		request.setTiempo(30);
		request.setPrioridad(Prioridad.ALTA);
		request.setFechaEntrega(LocalDateTime.now().minusDays(1)); // Fecha pasada

		// Act & Assert
		assertThrows(ValidationException.class, () -> {
			tsimpl.actualizarPorId(idTarea, request, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	
	@Test
	void obtenerPorTitulo_listaConTareas() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTitulo("A - Primera tarea");

		Tarea tarea2 = new Tarea(2L);
		tarea2.setTitulo("B - Segunda tarea");

		List<Tarea> lista = List.of(tarea1, tarea2);

		when(tr.findAllByUsuarioEmailOrderByTituloAsc(emailUsuario)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorTitulo(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertEquals("A - Primera tarea", resultado.get(0).getTitulo());
		assertEquals("B - Segunda tarea", resultado.get(1).getTitulo());

		verify(tr, times(1)).findAllByUsuarioEmailOrderByTituloAsc(emailUsuario);
	}
	@Test
	void obtenerPorTitulo_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		when(tr.findAllByUsuarioEmailOrderByTituloAsc(emailUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorTitulo(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findAllByUsuarioEmailOrderByTituloAsc(emailUsuario);
	}
	
	@Test
	void obtenerPorTiempo_listaConTareas() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTitulo("Tarea rápida");
		tarea1.setTiempo(10);

		Tarea tarea2 = new Tarea(2L);
		tarea2.setTitulo("Tarea lenta");
		tarea2.setTiempo(120);

		List<Tarea> lista = List.of(tarea1, tarea2);

		when(tr.findAllByUsuarioEmailOrderByTiempoAsc(emailUsuario)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorTiempo(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertEquals(10, resultado.get(0).getTiempo());
		assertEquals(120, resultado.get(1).getTiempo());

		verify(tr, times(1)).findAllByUsuarioEmailOrderByTiempoAsc(emailUsuario);
	}
	@Test
	void obtenerPorTiempo_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		when(tr.findAllByUsuarioEmailOrderByTiempoAsc(emailUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorTiempo(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findAllByUsuarioEmailOrderByTiempoAsc(emailUsuario);
	}
	
	@Test
	void obtenerPorPrioridad_listaConTareas() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTitulo("Tarea alta");
		tarea1.setPrioridad(Prioridad.ALTA);

		Tarea tarea2 = new Tarea(2L);
		tarea2.setTitulo("Tarea baja");
		tarea2.setPrioridad(Prioridad.BAJA);

		List<Tarea> lista = new ArrayList<>(List.of(tarea1, tarea2));

		when(tr.findAllByUsuarioEmail(emailUsuario)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorPrioridad(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertEquals(Prioridad.ALTA, resultado.get(0).getPrioridad());
		assertEquals(Prioridad.BAJA, resultado.get(1).getPrioridad());

		verify(tr, times(1)).findAllByUsuarioEmail(emailUsuario);
	}
	@Test
	void obtenerPorPrioridad_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		when(tr.findAllByUsuarioEmail(emailUsuario)).thenReturn(new ArrayList<>());

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorPrioridad(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findAllByUsuarioEmail(emailUsuario);
	}
	
	@Test
	void obtenerPorFechaEntrega_listaConTareas() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTitulo("Tarea próxima");
		tarea1.setFechaEntrega(LocalDateTime.now().plusDays(1));

		Tarea tarea2 = new Tarea(2L);
		tarea2.setTitulo("Tarea lejana");
		tarea2.setFechaEntrega(LocalDateTime.now().plusDays(10));

		List<Tarea> lista = List.of(tarea1, tarea2);

		when(tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorFechaEntrega(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertTrue(resultado.get(0).getFechaEntrega().isBefore(resultado.get(1).getFechaEntrega()));

		verify(tr, times(1)).findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario);
	}
	@Test
	void obtenerPorFechaEntrega_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";

		when(tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.obtenerPorFechaEntrega(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario);
	}

	@Test
	void filtrarPorPrioridad_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Prioridad prioridad = Prioridad.ALTA;

		Tarea tarea1 = new Tarea(1L);
		tarea1.setPrioridad(Prioridad.ALTA);

		List<Tarea> lista = List.of(tarea1);

		when(tr.findByUsuarioEmailAndPrioridad(emailUsuario, prioridad)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorPrioridad(prioridad.name(), emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(Prioridad.ALTA, resultado.get(0).getPrioridad());

		verify(tr, times(1)).findByUsuarioEmailAndPrioridad(emailUsuario, prioridad);
	}
	@Test
	void filtrarPorPrioridad_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Prioridad prioridad = Prioridad.ALTA;

		when(tr.findByUsuarioEmailAndPrioridad(emailUsuario, prioridad)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorPrioridad(prioridad.name(), emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuarioEmailAndPrioridad(emailUsuario, prioridad);
	}

	@Test
	void filtrarPorTiempo_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		int tiempo = 60;

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTiempo(45);

		List<Tarea> lista = List.of(tarea1);

		when(tr.findByUsuarioEmailAndTiempoLessThanEqual(emailUsuario, tiempo)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorTiempo(tiempo, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getTiempo() <= tiempo);

		verify(tr, times(1)).findByUsuarioEmailAndTiempoLessThanEqual(emailUsuario, tiempo);
	}
	@Test
	void filtrarPorTiempo_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		int tiempo = 60;

		when(tr.findByUsuarioEmailAndTiempoLessThanEqual(emailUsuario, tiempo)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorTiempo(tiempo, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuarioEmailAndTiempoLessThanEqual(emailUsuario, tiempo);
	}

	@Test
	void filtrarPorPalabrasClave_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		String palabrasClave = "importante";

		Tarea tarea1 = new Tarea(1L);
		tarea1.setTitulo("Tarea importante");

		List<Tarea> lista = List.of(tarea1);

		when(tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				eq(emailUsuario), eq(palabrasClave), eq(emailUsuario), eq(palabrasClave)))
		.thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorPalabrasClave("importante", emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getTitulo().toLowerCase().contains("importante"));

		verify(tr, times(1))
		.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				emailUsuario, palabrasClave, emailUsuario, palabrasClave);
	}
	@Test
	void filtrarPorPalabrasClave_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		String palabrasClave = "importante";

		when(tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				eq(emailUsuario), eq(palabrasClave), eq(emailUsuario), eq(palabrasClave)))
		.thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorPalabrasClave("importante", emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1))
		.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				emailUsuario, palabrasClave, emailUsuario, palabrasClave);
	}

	@Test
	void filtrarPorCategoria_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idCategoria = 1L;

		Tarea tarea1 = new Tarea(1L);
		Categoria categoria = new Categoria(idCategoria);
		tarea1.setCategoria(categoria);

		List<Tarea> lista = List.of(tarea1);

		when(tr.findByUsuarioEmailAndCategoria_IdCategoria(emailUsuario, idCategoria)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorCategoria(idCategoria, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(idCategoria, resultado.get(0).getCategoria().getIdCategoria());

		verify(tr, times(1)).findByUsuarioEmailAndCategoria_IdCategoria(emailUsuario, idCategoria);
	}
	@Test
	void filtrarPorCategoria_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idCategoria = 1L;

		when(tr.findByUsuarioEmailAndCategoria_IdCategoria(emailUsuario, idCategoria)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorCategoria(idCategoria, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuarioEmailAndCategoria_IdCategoria(emailUsuario, idCategoria);
	}

	@Test
	void filtrarPorUsuario_listaConResultados() {
		// Arrange
		Long idUsuario = 1L;

		Tarea tarea1 = new Tarea(1L);

		List<Tarea> lista = List.of(tarea1);

		when(tr.findByUsuario_IdUsuario(idUsuario)).thenReturn(lista);

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorUsuario(idUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());

		verify(tr, times(1)).findByUsuario_IdUsuario(idUsuario);
	}
	@Test
	void filtrarPorUsuario_listaVacia() {
		// Arrange
		Long idUsuario = 1L;

		when(tr.findByUsuario_IdUsuario(idUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorUsuario(idUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuario_IdUsuario(idUsuario);
	}

	@Test
	void obtenerEstado_tareaDelUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(usuario);
		tarea.setCompletada(false);
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));


		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act
		Estado estado = tsimpl.obtenerEstado(idTarea, emailUsuario);

		// Assert
		assertNotNull(estado);
		assertEquals(Estado.EN_CURSO, estado);

		verify(tr, times(1)).findById(idTarea);
	}
	@Test
	void obtenerEstado_lanzaExcepcionSiTareaNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 99L;

		when(tr.findById(idTarea)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			tsimpl.obtenerEstado(idTarea, emailUsuario);
		});

		verify(tr, times(1)).findById(idTarea);
	}
	@Test
	void obtenerEstado_lanzaExcepcionSiTareaEsDeOtroUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 2L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail("otro@ejemplo.com");

		Tarea tarea = new Tarea(2L);
		tarea.setUsuario(otroUsuario);
		tarea.setCompletada(false);
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			tsimpl.obtenerEstado(idTarea, emailUsuario);
		});

		verify(tr, times(1)).findById(idTarea);
	}

	@Test
	void marcarTareaCompletada_tareaMarcadaCorrectamente() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(usuario);
		tarea.setCompletada(false);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));
		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
		when(tr.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		TareaResponse respuesta = tsimpl.marcarTareaCompletada(idTarea, emailUsuario);

		// Assert
		assertNotNull(respuesta);
		assertTrue(respuesta.isCompletada());
		assertNotNull(respuesta.getFechaCompletada());

		verify(tr, times(1)).save(any(Tarea.class));
	}
	@Test
	void marcarTareaCompletada_lanzaExcepcionSiTareaNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 99L;

		when(tr.findById(idTarea)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			tsimpl.marcarTareaCompletada(idTarea, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void marcarTareaCompletada_lanzaExcepcionSiUsuarioNoExiste() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 1L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail("otro@ejemplo.com");

		Tarea tarea = new Tarea(1L);
		tarea.setUsuario(otroUsuario);
		tarea.setCompletada(false);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));
		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			tsimpl.marcarTareaCompletada(idTarea, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}
	@Test
	void marcarTareaCompletada_lanzaExcepcionSiTareaEsDeOtroUsuario() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Long idTarea = 2L;

		Usuario otroUsuario = new Usuario(2L);
		otroUsuario.setEmail("otro@ejemplo.com");

		Usuario usuario = new Usuario(1L);
		usuario.setEmail(emailUsuario);

		Tarea tarea = new Tarea(2L);
		tarea.setUsuario(otroUsuario);
		tarea.setCompletada(false);

		when(tr.findById(idTarea)).thenReturn(Optional.of(tarea));
		when(ur.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			tsimpl.marcarTareaCompletada(idTarea, emailUsuario);
		});

		verify(tr, never()).save(any(Tarea.class));
	}

	@Test
	void filtrarPorEstado_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Estado estado = Estado.EN_CURSO;

		Tarea tarea = new Tarea(1L);
		tarea.setCompletada(false);
		tarea.setFechaEntrega(LocalDateTime.now().plusDays(1));

		when(tr.findByUsuarioEmail(emailUsuario)).thenReturn(List.of(tarea));

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorEstado(estado, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(estado, resultado.get(0).getEstado());

		verify(tr, times(1)).findByUsuarioEmail(emailUsuario);
	}
	@Test
	void filtrarPorEstado_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		Estado estado = Estado.EN_CURSO;

		when(tr.findByUsuarioEmail(emailUsuario)).thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.filtrarPorEstado(estado, emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1)).findByUsuarioEmail(emailUsuario);
	}

	@Test
	void obtenerTareasHoy_listaConResultados() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
		LocalDateTime inicioManiana = inicioHoy.plusDays(1);

		Tarea tarea = new Tarea(1L);
		tarea.setFechaEntrega(LocalDateTime.now());

		when(tr.findByUsuarioEmailAndFechaEntregaBetween(eq(emailUsuario), eq(inicioHoy), eq(inicioManiana)))
		.thenReturn(List.of(tarea));

		// Act
		List<Tarea> resultado = tsimpl.obtenerTareasHoy(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());

		verify(tr, times(1))
		.findByUsuarioEmailAndFechaEntregaBetween(eq(emailUsuario), eq(inicioHoy), eq(inicioManiana));
	}
	@Test
	void obtenerTareasHoy_listaVacia() {
		// Arrange
		String emailUsuario = "user@ejemplo.com";
		LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
		LocalDateTime inicioManiana = inicioHoy.plusDays(1);

		when(tr.findByUsuarioEmailAndFechaEntregaBetween(eq(emailUsuario), eq(inicioHoy), eq(inicioManiana)))
		.thenReturn(List.of());

		// Act
		List<Tarea> resultado = tsimpl.obtenerTareasHoy(emailUsuario);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(tr, times(1))
		.findByUsuarioEmailAndFechaEntregaBetween(eq(emailUsuario), eq(inicioHoy), eq(inicioManiana));
	}
}
