package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
	boolean existsByCodigoPublico(String codigoPublico);
	boolean existsByCodigoInvitacion(String codigoInvitacion);
	Optional<Grupo> findByCodigoInvitacion(String codigoInvitacion);
}
