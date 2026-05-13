package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.AsignacionGrupoListadoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoMiembroResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoReopenRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoRevisionRequest;

public interface AsignacionGrupoService {
	AsignacionGrupoResponse crearAsignacion(Long idGrupo, AsignacionGrupoRequest asignacionGrupoRequest, String emailUsuario);
	List<AsignacionGrupoListadoResponse> listarAsignacionesGrupo(Long idGrupo, String emailUsuario);
	AsignacionGrupoResponse obtenerDetalleAsignacion(Long idGrupo, Long idAsignacion, String emailUsuario);
	AsignacionGrupoMiembroResponse validarEntrega(Long idAsignacionGrupoMiembro,
			AsignacionGrupoRevisionRequest revisionRequest, String emailUsuario);
	AsignacionGrupoMiembroResponse reabrirEntrega(Long idAsignacionGrupoMiembro,
			AsignacionGrupoReopenRequest reopenRequest, String emailUsuario);
}
