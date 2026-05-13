package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.Grupo;

public interface AsignacionGrupoRepository extends JpaRepository<AsignacionGrupo, Long> {
	List<AsignacionGrupo> findByGrupo(Grupo grupo);
	List<AsignacionGrupo> findByGrupoOrderByFechaCreacionDesc(Grupo grupo);
}
