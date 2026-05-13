package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.GrupoActivoRequest;
import com.tugestor.gestortareas.dto.GrupoJoinRequest;
import com.tugestor.gestortareas.dto.GrupoMiembroAddRequest;
import com.tugestor.gestortareas.dto.GrupoRequest;
import com.tugestor.gestortareas.dto.GrupoRolRequest;
import com.tugestor.gestortareas.dto.GrupoTransferirOwnershipRequest;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.GrupoMiembro;

public interface GrupoService {
	Grupo guardarGrupo(GrupoRequest grupoRequest);
	List<Grupo> obtenerMisGrupos();
	Grupo obtenerPorId(Long id);
	Grupo actualizarGrupo(Long id, GrupoRequest grupoRequest);
	void eliminarPorId(Long id);
	Grupo cambiarActivo(Long id, GrupoActivoRequest grupoActivoRequest);
	List<GrupoMiembro> obtenerMiembros(Long idGrupo);
	GrupoMiembro aniadirMiembro(Long idGrupo, GrupoMiembroAddRequest miembroAddRequest);
	void expulsarMiembro(Long idGrupo, Long idUsuario);
	GrupoMiembro cambiarRolMiembro(Long idGrupo, Long idUsuario, GrupoRolRequest grupoRolRequest);
	void salirDelGrupo(Long idGrupo);
	Grupo transferirOwnership(Long idGrupo, GrupoTransferirOwnershipRequest transferirOwnershipRequest);
	String obtenerCodigoInvitacion(Long idGrupo);
	String regenerarCodigoInvitacion(Long idGrupo);
	GrupoMiembro unirsePorCodigo(GrupoJoinRequest grupoJoinRequest);
}
