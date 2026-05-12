package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotNull;

public class GrupoTransferirOwnershipRequest {
	@NotNull(message = "El usuario destino no puede ser nulo")
	private Long idUsuario;

	public GrupoTransferirOwnershipRequest() {
	}
	public GrupoTransferirOwnershipRequest(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
}
