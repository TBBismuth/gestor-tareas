package com.tugestor.gestortareas.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.tugestor.gestortareas.model.Usuario;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

	@Autowired
	private UsuarioRepository ur;

	@Test
	void findByEmail_devuelveUsuario_siExiste() {
		// Arrange – crear y guardar usuario
		Usuario usuario = new Usuario();
		usuario.setNombre("Test User");
		usuario.setEmail("test@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act – buscar por email
		Optional<Usuario> resultado = ur.findByEmail("test@example.com");

		// Assert – verificar que se encontró
		assertTrue(resultado.isPresent());
		assertEquals("Test User", resultado.get().getNombre());
	}
	@Test
	void findByEmail_devuelveEmpty_siNoExiste() {
		// Act
		Optional<Usuario> resultado = ur.findByEmail("inexistente@example.com");

		// Assert
		assertTrue(resultado.isEmpty());
	}

}
