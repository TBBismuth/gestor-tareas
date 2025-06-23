package com.tugestor.gestortareas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
	List<Tarea> findAllByOrderByTituloAsc();
	List<Tarea> findAllByOrderByTiempoAsc();
	List<Tarea> findAllByOrderByPrioridadAsc();
	List<Tarea> findAllByOrderByFechaEntregaAsc();
	List<Tarea> findByPrioridad(Prioridad prioridad);
	List<Tarea> findByTiempoLessThanEqual(int tiempo);
	List<Tarea> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
	List<Tarea> findByCategoria_IdCategoria(Long idCategoria); // Con el "_" se indica que es un campo de la entidad Categoria (categoria.idCategoria)
	List<Tarea> findByUsuario_IdUsuario(Long idUsuario);
	
}