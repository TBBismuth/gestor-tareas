package com.tugestor.gestortareas.exception;

public class EmailDuplicadoException extends RuntimeException {
	public EmailDuplicadoException(String mensaje) {
		super(mensaje);
	}
}