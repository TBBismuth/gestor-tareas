package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tugestor.gestortareas.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
	List<Tarea> findAllByOrderByTituloAsc();
	List<Tarea> findAllByOrderByTiempoAsc();
	List<Tarea> findAllByOrderByPrioridadAsc();
	List<Tarea> findAllByOrderByFechaEntregaAsc();


}