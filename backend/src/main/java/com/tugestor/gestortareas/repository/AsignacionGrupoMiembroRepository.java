package com.tugestor.gestortareas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.Tarea;

public interface AsignacionGrupoMiembroRepository extends JpaRepository<AsignacionGrupoMiembro, Long> {
	Optional<AsignacionGrupoMiembro> findByTareaGenerada(Tarea tareaGenerada);
	boolean existsByTareaGenerada(Tarea tareaGenerada);
	List<AsignacionGrupoMiembro> findByAsignacionGrupo(AsignacionGrupo asignacionGrupo);
	@Query("""
			SELECT agm FROM AsignacionGrupoMiembro agm
			WHERE LOWER(agm.usuarioMiembro.email) = LOWER(:emailUsuario)
			AND LOWER(agm.tareaGenerada.usuario.email) = LOWER(:emailUsuario)
			ORDER BY CASE WHEN agm.tareaGenerada.fechaEntrega IS NULL THEN 1 ELSE 0 END,
				agm.tareaGenerada.fechaEntrega ASC
			""")
	List<AsignacionGrupoMiembro> findTareasAsignadasGrupoUsuario(@Param("emailUsuario") String emailUsuario);
	@Query("""
			SELECT agm FROM AsignacionGrupoMiembro agm
			WHERE LOWER(agm.usuarioMiembro.email) = LOWER(:emailUsuario)
			AND LOWER(agm.tareaGenerada.usuario.email) = LOWER(:emailUsuario)
			AND agm.asignacionGrupo.grupo.idGrupo = :idGrupo
			ORDER BY CASE WHEN agm.tareaGenerada.fechaEntrega IS NULL THEN 1 ELSE 0 END,
				agm.tareaGenerada.fechaEntrega ASC
			""")
	List<AsignacionGrupoMiembro> findTareasAsignadasGrupoUsuarioPorGrupo(
			@Param("emailUsuario") String emailUsuario, @Param("idGrupo") Long idGrupo);
	long countByAsignacionGrupo(AsignacionGrupo asignacionGrupo);
}
