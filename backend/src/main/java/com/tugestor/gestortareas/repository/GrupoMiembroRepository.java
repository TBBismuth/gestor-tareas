package com.tugestor.gestortareas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.GrupoMiembro;
import com.tugestor.gestortareas.model.RolGrupo;
import com.tugestor.gestortareas.model.Usuario;

public interface GrupoMiembroRepository extends JpaRepository<GrupoMiembro, Long> {
	List<GrupoMiembro> findByUsuario(Usuario usuario);
	Optional<GrupoMiembro> findByGrupoAndUsuario(Grupo grupo, Usuario usuario);
	boolean existsByGrupoAndUsuario(Grupo grupo, Usuario usuario);
	boolean existsByGrupoAndUsuarioAndRol(Grupo grupo, Usuario usuario, RolGrupo rol);
	void deleteByGrupo(Grupo grupo);
}
