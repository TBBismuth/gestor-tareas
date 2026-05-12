package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotNull;

public class GrupoActivoRequest {
	@NotNull(message = "El estado activo no puede ser nulo")
	private Boolean activo;

	public GrupoActivoRequest() {
	}
	public GrupoActivoRequest(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
