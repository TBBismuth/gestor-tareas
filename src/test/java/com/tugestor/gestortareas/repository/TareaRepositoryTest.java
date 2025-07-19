package com.tugestor.gestortareas.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;

@DataJpaTest
@ActiveProfiles("test")
public class TareaRepositoryTest {
	
	@Autowired
	private TareaRepository tr;
	@Autowired
	private UsuarioRepository ur;
	@Autowired
	private CategoriaRepository cr;	

	@Test
	void findAllByUsuarioEmailOrderByTituloAsc_devuelveTareasOrdenadasPorTitulo() {
		// Arrange – crear y guardar usuario
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario A");
		usuario.setEmail("usuarioA@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Crear tareas con títulos desordenados
		Tarea t1 = new Tarea();
		t1.setTitulo("Zoológico");
		t1.setTiempo(30);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Almuerzo");
		t2.setTiempo(20);
		t2.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t2.setPrioridad(Prioridad.ALTA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act – buscar tareas ordenadas por título ascendente
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByTituloAsc(usuario.getEmail());

		// Assert – deben estar ordenadas: Almuerzo, Zoológico
		assertEquals(2, resultado.size());
		assertEquals("Almuerzo", resultado.get(0).getTitulo());
		assertEquals("Zoológico", resultado.get(1).getTitulo());
	}
	@Test
	void findAllByUsuarioEmailOrderByTituloAsc_devuelveListaVacia_siNoTieneTareas() {
		// Arrange – crear y guardar usuario sin tareas
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario B");
		usuario.setEmail("usuarioB@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByTituloAsc(usuario.getEmail());

		// Assert
		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAllByUsuarioEmailOrderByTiempoAsc_devuelveTareasOrdenadasPorTiempo() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario C");
		usuario.setEmail("usuarioC@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea A");
		t1.setTiempo(60);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.BAJA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Tarea B");
		t2.setTiempo(15);
		t2.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t2.setPrioridad(Prioridad.MEDIA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByTiempoAsc(usuario.getEmail());

		// Assert
		assertEquals(2, resultado.size());
		assertEquals(15, resultado.get(0).getTiempo());
		assertEquals(60, resultado.get(1).getTiempo());
	}
	@Test
	void findAllByUsuarioEmailOrderByTiempoAsc_devuelveListaVacia_siNoTieneTareas() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario D");
		usuario.setEmail("usuarioD@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByTiempoAsc(usuario.getEmail());

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findAllByUsuarioEmailOrderByPrioridadAsc_devuelveTareasOrdenadasPorPrioridad() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario E");
		usuario.setEmail("usuarioE@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea Alta");
		t1.setTiempo(20);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.ALTA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Tarea Baja");
		t2.setTiempo(20);
		t2.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t2.setPrioridad(Prioridad.BAJA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByPrioridadAsc(usuario.getEmail());

		// Assert
		assertEquals(2, resultado.size());
		assertEquals(Prioridad.ALTA, resultado.get(0).getPrioridad());
		assertEquals(Prioridad.BAJA, resultado.get(1).getPrioridad());
	}
	@Test
	void findAllByUsuarioEmailOrderByPrioridadAsc_devuelveListaVacia_siNoTieneTareas() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario F");
		usuario.setEmail("usuarioF@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByPrioridadAsc(usuario.getEmail());

		// Assert
		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAllByUsuarioEmailOrderByFechaEntregaAsc_devuelveTareasOrdenadasPorFecha() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario G");
		usuario.setEmail("usuarioG@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Entrega tarde");
		t1.setTiempo(30);
		t1.setFechaEntrega(LocalDate.now().plusDays(5).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Entrega pronto");
		t2.setTiempo(30);
		t2.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t2.setPrioridad(Prioridad.MEDIA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(usuario.getEmail());

		// Assert
		assertEquals(2, resultado.size());
		assertEquals("Entrega pronto", resultado.get(0).getTitulo());
		assertEquals("Entrega tarde", resultado.get(1).getTitulo());
	}
	@Test
	void findAllByUsuarioEmailOrderByFechaEntregaAsc_devuelveListaVacia_siNoTieneTareas() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario H");
		usuario.setEmail("usuarioH@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(usuario.getEmail());

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioEmailAndPrioridad_devuelveTareasConEsaPrioridad() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario I");
		usuario.setEmail("usuarioI@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea alta");
		t1.setTiempo(20);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.ALTA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Tarea baja");
		t2.setTiempo(20);
		t2.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t2.setPrioridad(Prioridad.BAJA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndPrioridad(usuario.getEmail(), Prioridad.ALTA);

		// Assert
		assertEquals(1, resultado.size());
		assertEquals(Prioridad.ALTA, resultado.get(0).getPrioridad());
	}
	@Test
	void findByUsuarioEmailAndPrioridad_devuelveListaVacia_siNoCoincidePrioridad() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario J");
		usuario.setEmail("usuarioJ@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndPrioridad(usuario.getEmail(), Prioridad.IMPRESCINDIBLE);

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioEmailAndTiempoLessThanEqual_devuelveTareasConTiempoMenorOIgual() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario K");
		usuario.setEmail("usuarioK@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea corta");
		t1.setTiempo(20);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Tarea larga");
		t2.setTiempo(60);
		t2.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t2.setPrioridad(Prioridad.ALTA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndTiempoLessThanEqual(usuario.getEmail(), 30);

		// Assert
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getTiempo() <= 30);
	}
	@Test
	void findByUsuarioEmailAndTiempoLessThanEqual_devuelveListaVacia_siNoHayCoincidencias() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario L");
		usuario.setEmail("usuarioL@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndTiempoLessThanEqual(usuario.getEmail(), 10);

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioEmailAndPalabraClave_devuelveTareasConCoincidenciaEnTitulo() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario M");
		usuario.setEmail("usuarioM@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Estudiar Java");
		t1.setDescripcion("sin coincidencia");
		t1.setTiempo(30);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				usuario.getEmail(), "java", usuario.getEmail(), "java"
				);

		// Assert
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getTitulo().toLowerCase().contains("java"));
	}
	@Test
	void findByUsuarioEmailAndPalabraClave_devuelveTareasConCoincidenciaEnDescripcion() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario N");
		usuario.setEmail("usuarioN@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("otra cosa");
		t1.setDescripcion("Revisar spring");
		t1.setTiempo(40);
		t1.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t1.setPrioridad(Prioridad.ALTA);
		t1.setUsuario(usuario);
		tr.save(t1);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				usuario.getEmail(), "spring", usuario.getEmail(), "spring"
				);

		// Assert
		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getDescripcion().toLowerCase().contains("spring"));
	}
	@Test
	void findByUsuarioEmailAndPalabraClave_devuelveListaVacia_siNoHayCoincidencias() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario O");
		usuario.setEmail("usuarioO@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(
				usuario.getEmail(), "inexistente", usuario.getEmail(), "inexistente"
				);

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioEmailAndCategoriaId_devuelveTareasConCategoria() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario P");
		usuario.setEmail("usuarioP@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Categoria categoria = new Categoria();
		categoria.setNombre("Trabajo");
		cr.save(categoria);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea con categoría");
		t1.setTiempo(25);
		t1.setFechaEntrega(LocalDate.now().plusDays(1).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		t1.setCategoria(categoria);
		tr.save(t1);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndCategoria_IdCategoria(usuario.getEmail(), categoria.getIdCategoria());

		// Assert
		assertEquals(1, resultado.size());
		assertEquals("Tarea con categoría", resultado.get(0).getTitulo());
	}
	@Test
	void findByUsuarioEmailAndCategoriaId_devuelveListaVacia_siNoTieneTareasConCategoria() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario Q");
		usuario.setEmail("usuarioQ@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Categoria categoria = new Categoria();
		categoria.setNombre("Salud");
		cr.save(categoria);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmailAndCategoria_IdCategoria(usuario.getEmail(), categoria.getIdCategoria());

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioIdUsuario_devuelveTareasDelUsuario() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario R");
		usuario.setEmail("usuarioR@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea ID");
		t1.setTiempo(40);
		t1.setFechaEntrega(LocalDate.now().plusDays(3).atStartOfDay());
		t1.setPrioridad(Prioridad.ALTA);
		t1.setUsuario(usuario);
		tr.save(t1);

		// Act
		List<Tarea> resultado = tr.findByUsuario_IdUsuario(usuario.getIdUsuario());

		// Assert
		assertEquals(1, resultado.size());
		assertEquals("Tarea ID", resultado.get(0).getTitulo());
	}
	@Test
	void findByUsuarioIdUsuario_devuelveListaVacia_siNoTieneTareas() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario S");
		usuario.setEmail("usuarioS@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findByUsuario_IdUsuario(usuario.getIdUsuario());

		// Assert
		assertTrue(resultado.isEmpty());
	}

	@Test
	void findByUsuarioEmail_devuelveTareasDelUsuario() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario T");
		usuario.setEmail("usuarioT@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("Tarea por email");
		t1.setTiempo(35);
		t1.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmail(usuario.getEmail());

		// Assert
		assertEquals(1, resultado.size());
		assertEquals("Tarea por email", resultado.get(0).getTitulo());
	}
	@Test
	void findByUsuarioEmail_devuelveListaVacia_siNoTieneTareas() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario U");
		usuario.setEmail("usuarioU@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		List<Tarea> resultado = tr.findByUsuarioEmail(usuario.getEmail());

		// Assert
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findByUsuarioEmailAndFechaEntregaBetween_devuelveTareasEnRango() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario V");
		usuario.setEmail("usuarioV@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		Tarea t1 = new Tarea();
		t1.setTitulo("En rango");
		t1.setTiempo(20);
		t1.setFechaEntrega(LocalDate.now().plusDays(2).atStartOfDay());
		t1.setPrioridad(Prioridad.MEDIA);
		t1.setUsuario(usuario);
		tr.save(t1);

		Tarea t2 = new Tarea();
		t2.setTitulo("Fuera de rango");
		t2.setTiempo(20);
		t2.setFechaEntrega(LocalDate.now().plusDays(10).atStartOfDay());
		t2.setPrioridad(Prioridad.ALTA);
		t2.setUsuario(usuario);
		tr.save(t2);

		// Act
		LocalDateTime inicio = LocalDate.now().plusDays(1).atStartOfDay();
		LocalDateTime fin = LocalDate.now().plusDays(3).atStartOfDay();
		List<Tarea> resultado = tr.findByUsuarioEmailAndFechaEntregaBetween(usuario.getEmail(), inicio, fin);

		// Assert
		assertEquals(1, resultado.size());
		assertEquals("En rango", resultado.get(0).getTitulo());
	}
	@Test
	void findByUsuarioEmailAndFechaEntregaBetween_devuelveListaVacia_siNoHayTareasEnRango() {
		// Arrange
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario W");
		usuario.setEmail("usuarioW@example.com");
		usuario.setPassword("Test1234");
		ur.save(usuario);

		// Act
		LocalDateTime inicio = LocalDate.now().plusDays(1).atStartOfDay();
		LocalDateTime fin = LocalDate.now().plusDays(2).atStartOfDay();
		List<Tarea> resultado = tr.findByUsuarioEmailAndFechaEntregaBetween(usuario.getEmail(), inicio, fin);

		// Assert
		assertTrue(resultado.isEmpty());
	}
}
