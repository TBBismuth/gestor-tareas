package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.GrupoActivoRequest;
import com.tugestor.gestortareas.dto.GrupoRequest;
import com.tugestor.gestortareas.model.Grupo;

public interface GrupoService {
	Grupo guardarGrupo(GrupoRequest grupoRequest);
	List<Grupo> obtenerMisGrupos();
	Grupo obtenerPorId(Long id);
	Grupo actualizarGrupo(Long id, GrupoRequest grupoRequest);
	void eliminarPorId(Long id);
	Grupo cambiarActivo(Long id, GrupoActivoRequest grupoActivoRequest);
}
