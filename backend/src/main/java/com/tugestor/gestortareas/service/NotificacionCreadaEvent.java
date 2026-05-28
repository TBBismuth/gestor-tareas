package com.tugestor.gestortareas.service;

public class NotificacionCreadaEvent {
	private final Long idNotificacion;

	public NotificacionCreadaEvent(Long idNotificacion) {
		this.idNotificacion = idNotificacion;
	}

	public Long getIdNotificacion() {
		return idNotificacion;
	}
}
