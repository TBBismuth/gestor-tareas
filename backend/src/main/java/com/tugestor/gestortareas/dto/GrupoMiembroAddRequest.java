package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class GrupoMiembroAddRequest {
	@Email(message = "El email debe ser valido")
	@NotBlank(message = "El email no puede estar vacio")
	private String email;

	public GrupoMiembroAddRequest() {
	}
	public GrupoMiembroAddRequest(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
