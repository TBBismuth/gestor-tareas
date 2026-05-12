package com.tugestor.gestortareas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
	boolean existsByCodigoPublico(String codigoPublico);
	boolean existsByCodigoInvitacion(String codigoInvitacion);
}
