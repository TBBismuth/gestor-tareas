package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
	List<Tarea> findAllByUsuarioEmailOrderByTituloAsc(String emailUsuarioCreador);
	List<Tarea> findAllByUsuarioEmailOrderByTiempoAsc(String emailUsuarioCreador);
	List<Tarea> findAllByUsuarioEmailOrderByPrioridadAsc(String emailUsuarioCreador);
	List<Tarea> findAllByUsuarioEmailOrderByFechaEntregaAsc(String emailUsuarioCreador);
	List<Tarea> findByUsuarioEmailAndPrioridad(String emailUsuarioCreador, Prioridad prioridad);
	List<Tarea> findByUsuarioEmailAndTiempoLessThanEqual(String emailUsuarioCreador, int tiempo);
	List<Tarea> findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(String emailUsuarioCreador0, String titulo,String emailUsuarioCreador1, String descripcion);
	List<Tarea> findByUsuarioEmailAndCategoria_IdCategoria(String emailUsuarioCreador, Long idCategoria); // Con el "_" se indica que es un campo de la entidad Categoria (categoria.idCategoria)
	List<Tarea> findByUsuario_IdUsuario(Long idUsuario);
	List<Tarea> findByUsuarioEmail(String email);
	
}