package com.tugestor.gestortareas.service;

import com.tugestor.gestortareas.dto.PreferenciasNotificacionRequest;
import com.tugestor.gestortareas.dto.PreferenciasNotificacionResponse;

public interface PreferenciasNotificacionService {
	PreferenciasNotificacionResponse obtenerPreferencias(String emailUsuario);
	PreferenciasNotificacionResponse actualizarPreferencias(PreferenciasNotificacionRequest request,
			String emailUsuario);
}
