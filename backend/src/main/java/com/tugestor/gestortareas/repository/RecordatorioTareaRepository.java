package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.RecordatorioTarea;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoRecordatorioTarea;
import com.tugestor.gestortareas.model.Usuario;

public interface RecordatorioTareaRepository extends JpaRepository<RecordatorioTarea, Long> {
	Optional<RecordatorioTarea> findByUsuarioAndTareaAndTipo(Usuario usuario, Tarea tarea,
			TipoRecordatorioTarea tipo);
}
