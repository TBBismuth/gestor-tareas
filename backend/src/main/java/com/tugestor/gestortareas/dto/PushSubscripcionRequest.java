package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PushSubscripcionRequest {
	@NotBlank(message = "El endpoint de la subscripcion push no puede estar vacio")
	@Size(max = 2048, message = "El endpoint de la subscripcion push no puede exceder los 2048 caracteres")
	private String endpoint;
	@NotBlank(message = "La clave p256dh de la subscripcion push no puede estar vacia")
	@Size(max = 512, message = "La clave p256dh de la subscripcion push no puede exceder los 512 caracteres")
	private String p256dh;
	@NotBlank(message = "La clave auth de la subscripcion push no puede estar vacia")
	@Size(max = 512, message = "La clave auth de la subscripcion push no puede exceder los 512 caracteres")
	private String auth;
	@Size(max = 1000, message = "El userAgent no puede exceder los 1000 caracteres")
	private String userAgent;
	@Size(max = 100, message = "El nombre del dispositivo no puede exceder los 100 caracteres")
	private String nombreDispositivo;

	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getP256dh() {
		return p256dh;
	}
	public void setP256dh(String p256dh) {
		this.p256dh = p256dh;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getNombreDispositivo() {
		return nombreDispositivo;
	}
	public void setNombreDispositivo(String nombreDispositivo) {
		this.nombreDispositivo = nombreDispositivo;
	}
}
