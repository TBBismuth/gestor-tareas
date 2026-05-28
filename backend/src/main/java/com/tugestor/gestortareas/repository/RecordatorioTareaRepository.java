package com.tugestor.gestortareas.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tugestor.gestortareas.model.RecordatorioTarea;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoRecordatorioTarea;
import com.tugestor.gestortareas.model.Usuario;

public interface RecordatorioTareaRepository extends JpaRepository<RecordatorioTarea, Long> {
	Optional<RecordatorioTarea> findByUsuarioAndTareaAndTipo(Usuario usuario, Tarea tarea,
			TipoRecordatorioTarea tipo);
	List<RecordatorioTarea> findByTipoAndActivoTrueAndNotificacionGeneradaFalseAndFechaProgramadaLessThanEqual(
			TipoRecordatorioTarea tipo, LocalDateTime fechaLimite);
	@Query("""
			SELECT r.tarea.idTarea FROM RecordatorioTarea r
			WHERE LOWER(r.usuario.email) = LOWER(:emailUsuario)
			AND r.tipo = :tipo
			AND r.activo = true
			AND r.tarea.idTarea IN :idsTareas
			""")
	List<Long> findActiveTaskIdsByUsuarioEmailAndTipo(
			@Param("emailUsuario") String emailUsuario,
			@Param("tipo") TipoRecordatorioTarea tipo,
			@Param("idsTareas") List<Long> idsTareas);
	@Modifying
	int deleteByTarea(Tarea tarea);
}
