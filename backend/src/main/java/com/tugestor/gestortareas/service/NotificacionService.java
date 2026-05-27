package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.NotificacionResponse;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.RecordatorioTarea;

public interface NotificacionService {
	List<NotificacionResponse> obtenerActivas(String emailUsuario);
	long contarActivas(String emailUsuario);
	NotificacionResponse cerrarNotificacion(Long idNotificacion, String emailUsuario);
	void cerrarTodas(String emailUsuario);
	void crearDesdeRecordatorioInteligente(RecordatorioTarea recordatorio, java.time.LocalDateTime fechaCreacion);
	void crearDesdeAsignacionGrupo(AsignacionGrupoMiembro asignacionGrupoMiembro);
}
