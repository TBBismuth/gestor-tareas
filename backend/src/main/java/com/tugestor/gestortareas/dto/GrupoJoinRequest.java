package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;

public class GrupoJoinRequest {
	@NotBlank(message = "El codigo de invitacion no puede estar vacio")
	private String codigoInvitacion;

	public GrupoJoinRequest() {
	}
	public GrupoJoinRequest(String codigoInvitacion) {
		this.codigoInvitacion = codigoInvitacion;
	}

	public String getCodigoInvitacion() {
		return codigoInvitacion;
	}
	public void setCodigoInvitacion(String codigoInvitacion) {
		this.codigoInvitacion = codigoInvitacion;
	}
}
