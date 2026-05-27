package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.NotificacionResponse;

public interface NotificacionService {
	List<NotificacionResponse> obtenerActivas(String emailUsuario);
	long contarActivas(String emailUsuario);
	NotificacionResponse cerrarNotificacion(Long idNotificacion, String emailUsuario);
	void cerrarTodas(String emailUsuario);
}
