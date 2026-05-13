package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;

public class AsignacionGrupoReopenRequest {
	@NotBlank(message = "El comentario de revision es obligatorio al reabrir.")
	private String comentarioRevision;

	public AsignacionGrupoReopenRequest() {
	}

	public String getComentarioRevision() {
		return comentarioRevision;
	}
	public void setComentarioRevision(String comentarioRevision) {
		this.comentarioRevision = comentarioRevision;
	}
}
