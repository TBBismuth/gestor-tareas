package com.tugestor.gestortareas.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceImplTest {
	@Mock
	private CategoriaRepository cr;
	@Mock
	private TareaRepository tr;
	@InjectMocks
	private CategoriaServiceImpl csimpl;
	
	@Test
	void guardarCategoria_categoriaGuardadaCorrectamente() {
		// Arrange
		Categoria categoria = new Categoria(1L);
		categoria.setNombre("Categoría test");
		categoria.setColor("#FFFFFF");
		categoria.setIcono("icono");

		when(cr.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Categoria resultado = csimpl.guardarCategoria(categoria);

		// Assert
		assertNotNull(resultado);
		assertEquals("Categoría test", resultado.getNombre());
		assertEquals("#FFFFFF", resultado.getColor());
		assertEquals("icono", resultado.getIcono());

		verify(cr, times(1)).save(any(Categoria.class));
	}

	@Test
	void obtenerTodas_listaConCategorias() {
		// Arrange
		Categoria cat1 = new Categoria(1L);
		cat1.setNombre("Trabajo");

		Categoria cat2 = new Categoria(2L);
		cat2.setNombre("Ocio");

		List<Categoria> lista = List.of(cat1, cat2);

		when(cr.findAll()).thenReturn(lista);

		// Act
		List<Categoria> resultado = csimpl.obtenerTodas();

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());
		assertEquals("Trabajo", resultado.get(0).getNombre());

		verify(cr, times(1)).findAll();
	}
	@Test
	void obtenerTodas_listaVacia() {
		// Arrange
		when(cr.findAll()).thenReturn(List.of());

		// Act
		List<Categoria> resultado = csimpl.obtenerTodas();

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(cr, times(1)).findAll();
	}

	@Test
	void eliminarPorId_categoriaEliminadaCorrectamente() {
		// Arrange
		Long idCategoria = 1L;

		Categoria categoria = new Categoria(idCategoria);
		categoria.setNombre("Personal");
		categoria.setProtegida(false);

		when(cr.findById(idCategoria)).thenReturn(Optional.of(categoria));
		when(tr.findByCategoria_IdCategoria(idCategoria)).thenReturn(Collections.emptyList());

		// Act
		csimpl.eliminarPorId(idCategoria);

		// Assert
		verify(cr, times(1)).delete(categoria);
	}

	@Test
	void eliminarPorId_lanzaExcepcionSiNoExiste() {
		// Arrange
		Long idCategoria = 99L;

		when(cr.findById(idCategoria)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> {
			csimpl.eliminarPorId(idCategoria);
		});

		verify(cr, never()).delete(any(Categoria.class));
	}
	@Test
	void eliminarPorId_lanzaExcepcionSiCategoriaProtegida() {
		// Arrange
		Long idCategoria = 2L;

		Categoria categoria = new Categoria(idCategoria);
		categoria.setNombre("Base");
		categoria.setProtegida(true);

		when(cr.findById(idCategoria)).thenReturn(Optional.of(categoria));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			csimpl.eliminarPorId(idCategoria);
		});

		verify(cr, never()).delete(any(Categoria.class));
	}

	@Test
	void actualizarPorId_categoriaActualizadaCorrectamente() {
		// Arrange
		Long idCategoria = 1L;

		Categoria existente = new Categoria(idCategoria);
		existente.setNombre("Original");
		existente.setColor("#000000");
		existente.setIcono("icono1");

		Categoria modificada = new Categoria(idCategoria);
		modificada.setNombre("Actualizada");
		modificada.setColor("#FFFFFF");
		modificada.setIcono("icono2");

		when(cr.findById(idCategoria)).thenReturn(Optional.of(existente));
		when(cr.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Categoria resultado = csimpl.actualizarPorId(idCategoria, modificada);

		// Assert
		assertNotNull(resultado);
		assertEquals("Actualizada", resultado.getNombre());
		assertEquals("#FFFFFF", resultado.getColor());
		assertEquals("icono2", resultado.getIcono());

		verify(cr, times(1)).save(any(Categoria.class));
	}
	@Test
	void actualizarPorId_lanzaExcepcionSiNoExiste() {
		// Arrange
		Long idCategoria = 99L;

		Categoria modificada = new Categoria(idCategoria);
		modificada.setNombre("Actualizada");

		when(cr.findById(idCategoria)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			csimpl.actualizarPorId(idCategoria, modificada);
		});

		verify(cr, never()).save(any(Categoria.class));
	}

	@Test
	void obtenerPorNombre_listaConResultados() {
		// Arrange
		String nombreParcial = "Tra";

		Categoria cat1 = new Categoria(1L);
		cat1.setNombre("Trabajo");

		List<Categoria> lista = List.of(cat1);

		when(cr.findByNombreIgnoreCaseContaining(nombreParcial)).thenReturn(lista);

		// Act
		List<Categoria> resultado = csimpl.obtenerPorNombre(nombreParcial);

		// Assert
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getNombre().toLowerCase().contains("tra"));

		verify(cr, times(1)).findByNombreIgnoreCaseContaining(nombreParcial);
	}
	@Test
	void obtenerPorNombre_listaVacia() {
		// Arrange
		String nombreParcial = "Nada";

		when(cr.findByNombreIgnoreCaseContaining(nombreParcial)).thenReturn(List.of());

		// Act
		List<Categoria> resultado = csimpl.obtenerPorNombre(nombreParcial);

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(cr, times(1)).findByNombreIgnoreCaseContaining(nombreParcial);
	}

	@Test
	void guardarCategoria_conRequest_categoriaGuardadaCorrectamente() {
		// Arrange
		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Nueva categoría");
		request.setColor("#ABCDEF");
		request.setIcono("icono");

		when(cr.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Categoria resultado = csimpl.guardarCategoria(request);

		// Assert
		assertNotNull(resultado);
		assertEquals("Nueva categoría", resultado.getNombre());
		assertEquals("#ABCDEF", resultado.getColor());
		assertEquals("icono", resultado.getIcono());

		verify(cr, times(1)).save(any(Categoria.class));
	}
	
	@Test
	void actualizarCategoria_categoriaActualizadaCorrectamente() {
		// Arrange
		Long idCategoria = 1L;

		Categoria existente = new Categoria(idCategoria);
		existente.setNombre("Original");
		existente.setColor("#000000");
		existente.setIcono("icono1");

		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Actualizada");
		request.setColor("#FFFFFF");
		request.setIcono("icono2");

		when(cr.findById(idCategoria)).thenReturn(Optional.of(existente));
		when(cr.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Categoria resultado = csimpl.actualizarCategoria(idCategoria, request);

		// Assert
		assertNotNull(resultado);
		assertEquals("Actualizada", resultado.getNombre());
		assertEquals("#FFFFFF", resultado.getColor());
		assertEquals("icono2", resultado.getIcono());

		verify(cr, times(1)).save(any(Categoria.class));
	}
	@Test
	void actualizarCategoria_lanzaExcepcionSiNoExiste() {
		// Arrange
		Long idCategoria = 99L;

		CategoriaRequest request = new CategoriaRequest();
		request.setNombre("Actualizada");

		when(cr.findById(idCategoria)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			csimpl.actualizarCategoria(idCategoria, request);
		});

		verify(cr, never()).save(any(Categoria.class));
	}
}
