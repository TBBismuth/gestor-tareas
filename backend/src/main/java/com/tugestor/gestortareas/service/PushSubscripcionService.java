package com.tugestor.gestortareas.service;

import com.tugestor.gestortareas.dto.PushSubscripcionDeleteRequest;
import com.tugestor.gestortareas.dto.PushSubscripcionRequest;
import com.tugestor.gestortareas.dto.PushSubscripcionResponse;

public interface PushSubscripcionService {
	PushSubscripcionResponse registrarOActualizar(PushSubscripcionRequest request, String emailUsuario);
	void desactivar(PushSubscripcionDeleteRequest request, String emailUsuario);
}
