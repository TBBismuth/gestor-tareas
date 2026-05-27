package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotNull;

public class RecordatorioInteligenteRequest {
	@NotNull(message = "El estado activo del recordatorio inteligente no puede ser nulo")
	private Boolean activo;

	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
