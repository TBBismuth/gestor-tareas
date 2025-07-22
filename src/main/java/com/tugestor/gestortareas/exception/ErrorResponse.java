package com.tugestor.gestortareas.exception;

public class ErrorResponse {
	private int status;
	private String mensaje;

	public ErrorResponse(int status, String mensaje) {
		this.status = status;
		this.mensaje = mensaje;
	}

	public int getStatus() {
		return status;
	}

	public String getMensaje() {
		return mensaje;
	}
}
