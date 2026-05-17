package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.EstadoFiltroTarea;
import com.tugestor.gestortareas.model.Usuario;

public interface EstadoFiltroTareaRepository extends JpaRepository<EstadoFiltroTarea, Long> {
	Optional<EstadoFiltroTarea> findByUsuario(Usuario usuario);
}
