package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.PreferenciasNotificacion;
import com.tugestor.gestortareas.model.Usuario;

public interface PreferenciasNotificacionRepository extends JpaRepository<PreferenciasNotificacion, Long> {
	Optional<PreferenciasNotificacion> findByUsuario(Usuario usuario);
}
