package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PushSubscripcionDeleteRequest {
	@NotBlank(message = "El endpoint de la subscripcion push no puede estar vacio")
	@Size(max = 2048, message = "El endpoint de la subscripcion push no puede exceder los 2048 caracteres")
	private String endpoint;

	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}
