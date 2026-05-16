package com.tugestor.gestortareas.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	private int status;
	private String error;
	private Map<String, String> errors;

	public ErrorResponse(int status, String error) {
		this.status = status;
		this.error = error;
	}

	public ErrorResponse(int status, String error, Map<String, String> errors) {
		this.status = status;
		this.error = error;
		this.errors = errors;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
