package com.tugestor.gestortareas.service;

import com.tugestor.gestortareas.dto.AsignacionGrupoRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoResponse;

public interface AsignacionGrupoService {
	AsignacionGrupoResponse crearAsignacion(Long idGrupo, AsignacionGrupoRequest asignacionGrupoRequest, String emailUsuario);
}
