package com.tugestor.gestortareas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.Tarea;

public interface AsignacionGrupoMiembroRepository extends JpaRepository<AsignacionGrupoMiembro, Long> {
	Optional<AsignacionGrupoMiembro> findByTareaGenerada(Tarea tareaGenerada);
	boolean existsByTareaGenerada(Tarea tareaGenerada);
	List<AsignacionGrupoMiembro> findByAsignacionGrupo(AsignacionGrupo asignacionGrupo);
	long countByAsignacionGrupo(AsignacionGrupo asignacionGrupo);
}
