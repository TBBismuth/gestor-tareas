package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
	@NotBlank(message = "El refresh token no puede estar vacio")
	private String refreshToken;

	public RefreshTokenRequest() {
	}

	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
