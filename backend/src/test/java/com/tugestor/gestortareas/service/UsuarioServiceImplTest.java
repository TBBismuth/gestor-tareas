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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {
	@Mock
	private UsuarioRepository ur;
	@Mock
	private PasswordEncoder pe;
	@InjectMocks
	private UsuarioServiceImpl usimpl;

	@Test
	void guardarUsuario_usuarioGuardadoCorrectamente() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario test");
		usuario.setEmail("usuario@ejemplo.com");
		usuario.setPassword("1234");

		when(ur.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
		when(pe.encode("1234")).thenReturn("encoded1234");
		when(ur.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Usuario resultado = usimpl.guardarUsuario(usuario);

		// Assert
		assertNotNull(resultado);
		assertEquals("Usuario test", resultado.getNombre());
		assertEquals("usuario@ejemplo.com", resultado.getEmail());
		assertEquals("encoded1234", resultado.getPassword());

		verify(ur, times(1)).save(any(Usuario.class));
	}
	@Test
	void guardarUsuario_lanzaExcepcionSiEmailDuplicado() {
		// Arrange
		Usuario existente = new Usuario();
		existente.setEmail("usuario@ejemplo.com");

		Usuario nuevo = new Usuario();
		nuevo.setEmail("usuario@ejemplo.com");

		when(ur.findByEmail("usuario@ejemplo.com")).thenReturn(Optional.of(existente));

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			usimpl.guardarUsuario(nuevo);
		});

		verify(ur, never()).save(any(Usuario.class));
	}

	@Test
	void obtenerPorEmail_devuelveUsuario() {
		// Arrange
		String email = "user@ejemplo.com";
		Usuario usuario = new Usuario();
		usuario.setEmail(email);

		when(ur.findByEmail(email)).thenReturn(Optional.of(usuario));

		// Act
		Optional<Usuario> resultado = usimpl.obtenerPorEmail(email);

		// Assert
		assertTrue(resultado.isPresent());
		assertEquals(email, resultado.get().getEmail());

		verify(ur, times(1)).findByEmail(email);
	}
	@Test
	void obtenerPorEmail_noExisteUsuario() {
		// Arrange
		String email = "user@ejemplo.com";

		when(ur.findByEmail(email)).thenReturn(Optional.empty());

		// Act
		Optional<Usuario> resultado = usimpl.obtenerPorEmail(email);

		// Assert
		assertTrue(resultado.isEmpty());

		verify(ur, times(1)).findByEmail(email);
	}

	@Test
	void obtenerPorId_devuelveUsuario() {
		// Arrange
		Long idUsuario = 1L;

		Usuario usuario = new Usuario(idUsuario);
		usuario.setEmail("user@ejemplo.com");

		when(ur.findById(idUsuario)).thenReturn(Optional.of(usuario));

		// Act
		Usuario resultado = usimpl.obtenerPorId(idUsuario);

		// Assert
		assertNotNull(resultado);
		assertEquals(idUsuario, resultado.getIdUsuario());

		verify(ur, times(1)).findById(idUsuario);
	}
	@Test
	void obtenerPorId_lanzaExcepcionSiNoExiste() {
		// Arrange
		Long idUsuario = 99L;

		when(ur.findById(idUsuario)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			usimpl.obtenerPorId(idUsuario);
		});

		verify(ur, times(1)).findById(idUsuario);
	}

	@Test
	void obtenerTodos_listaConUsuarios() {
		// Arrange
		Usuario usuario1 = new Usuario(1L);
		Usuario usuario2 = new Usuario(2L);

		List<Usuario> lista = List.of(usuario1, usuario2);

		when(ur.findAll()).thenReturn(lista);

		// Act
		List<Usuario> resultado = usimpl.obtenerTodos();

		// Assert
		assertNotNull(resultado);
		assertEquals(2, resultado.size());

		verify(ur, times(1)).findAll();
	}
	@Test
	void obtenerTodos_listaVacia() {
		// Arrange
		when(ur.findAll()).thenReturn(List.of());

		// Act
		List<Usuario> resultado = usimpl.obtenerTodos();

		// Assert
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());

		verify(ur, times(1)).findAll();
	}

	@Test
	void eliminarPorId_usuarioEliminadoCorrectamente() {
		// Arrange
		Long idUsuario = 1L;

		Usuario usuario = new Usuario(idUsuario);
		usuario.setEmail("user@ejemplo.com");

		when(ur.findById(idUsuario)).thenReturn(Optional.of(usuario));

		// Act
		usimpl.eliminarPorId(idUsuario);

		// Assert
		verify(ur, times(1)).delete(usuario);
	}
	@Test
	void eliminarPorId_lanzaExcepcionSiNoExiste() {
		// Arrange
		Long idUsuario = 99L;

		when(ur.findById(idUsuario)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			usimpl.eliminarPorId(idUsuario);
		});

		verify(ur, never()).delete(any(Usuario.class));
	}

	@Test
	void login_loginCorrecto() {
		// Arrange
		String email = "user@ejemplo.com";
		String rawPassword = "1234";
		String encodedPassword = "encoded1234";

		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setPassword(encodedPassword);

		LoginRequest request = new LoginRequest();
		request.setEmail(email);
		request.setPassword(rawPassword);

		when(ur.findByEmail(email)).thenReturn(Optional.of(usuario));
		when(pe.matches(rawPassword, encodedPassword)).thenReturn(true);

		// Act
		LoginResponse respuesta = usimpl.login(request);

		// Assert
		assertNotNull(respuesta);
		assertEquals(email, respuesta.getEmail());

		verify(ur, times(1)).findByEmail(email);
	}
	@Test
	void login_lanzaExcepcionSiUsuarioNoExiste() {
		// Arrange
		String email = "user@ejemplo.com";

		LoginRequest request = new LoginRequest();
		request.setEmail(email);
		request.setPassword("1234");

		when(ur.findByEmail(email)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			usimpl.login(request);
		});

		verify(ur, times(1)).findByEmail(email);
	}
	@Test
	void login_lanzaExcepcionSiPasswordIncorrecta() {
	    // Arrange
	    String email = "user@ejemplo.com";
	    String rawPassword = "1234";
	    String encodedPassword = "encoded1234";

	    Usuario usuario = new Usuario();
	    usuario.setEmail(email);
	    usuario.setPassword(encodedPassword);

	    LoginRequest request = new LoginRequest();
	    request.setEmail(email);
	    request.setPassword(rawPassword);

	    when(ur.findByEmail(email)).thenReturn(Optional.of(usuario));
	    when(pe.matches(rawPassword, encodedPassword)).thenReturn(false);

	    // Act & Assert
	    assertThrows(RuntimeException.class, () -> {
	        usimpl.login(request);
	    });

	    verify(ur, times(1)).findByEmail(email);
	}
	
	@Test
	void guardarUsuario_conRequest_usuarioGuardadoCorrectamente() {
	    // Arrange
	    UsuarioRequest request = new UsuarioRequest();
	    request.setNombre("Usuario");
	    request.setEmail("user@ejemplo.com");
	    request.setPassword("1234");

	    when(ur.findByEmail(request.getEmail())).thenReturn(Optional.empty());
	    when(pe.encode("1234")).thenReturn("encoded1234");
	    when(ur.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Act
	    Usuario resultado = usimpl.guardarUsuario(request);

	    // Assert
	    assertNotNull(resultado);
	    assertEquals("Usuario", resultado.getNombre());
	    assertEquals("user@ejemplo.com", resultado.getEmail());
	    assertEquals("encoded1234", resultado.getPassword());

	    verify(ur, times(1)).save(any(Usuario.class));
	}
	@Test
	void guardarUsuario_conRequest_lanzaExcepcionSiEmailDuplicado() {
	    // Arrange
	    UsuarioRequest request = new UsuarioRequest();
	    request.setNombre("Usuario");
	    request.setEmail("user@ejemplo.com");
	    request.setPassword("1234");

	    Usuario existente = new Usuario();
	    existente.setEmail(request.getEmail());

	    when(ur.findByEmail(request.getEmail())).thenReturn(Optional.of(existente));

	    // Act & Assert
	    assertThrows(RuntimeException.class, () -> {
	        usimpl.guardarUsuario(request);
	    });

	    verify(ur, never()).save(any(Usuario.class));
	}
}
