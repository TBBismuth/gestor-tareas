package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.RolGrupo;

import jakarta.validation.constraints.NotNull;

public class GrupoRolRequest {
	@NotNull(message = "El rol no puede ser nulo")
	private RolGrupo rol;

	public GrupoRolRequest() {
	}
	public GrupoRolRequest(RolGrupo rol) {
		this.rol = rol;
	}

	public RolGrupo getRol() {
		return rol;
	}
	public void setRol(RolGrupo rol) {
		this.rol = rol;
	}
}
