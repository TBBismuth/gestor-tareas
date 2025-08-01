package com.tugestor.gestortareas.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.tugestor.gestortareas.model.Categoria;

@DataJpaTest
@ActiveProfiles("test")
public class CategoriaRepositoryTest {
	
	@Autowired
	private CategoriaRepository cr;
	
	@Test
	void findByNombreContainingIgnoreCase_devuelveCategoriasCoincidentes() {
		// Arrange
		Categoria cat1 = new Categoria();
		cat1.setNombre("Trabajo");
		cr.save(cat1);

		Categoria cat2 = new Categoria();
		cat2.setNombre("Tareas domésticas");
		cr.save(cat2);

		// Act
		List<Categoria> resultado = cr.findByNombreIgnoreCaseContaining("trAB");

		// Assert
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getNombre().toLowerCase().contains("trab"));
	}
	@Test
	void findByNombreContainingIgnoreCase_devuelveListaVacia_siNoCoincide() {
		// Act
		List<Categoria> resultado = cr.findByNombreIgnoreCaseContaining("inexistente");

		// Assert
		assertTrue(resultado.isEmpty());
	}
}
