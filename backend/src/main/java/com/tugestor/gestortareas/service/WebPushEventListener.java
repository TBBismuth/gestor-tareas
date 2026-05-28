package com.tugestor.gestortareas.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WebPushEventListener {
	private final WebPushService webPushService;

	public WebPushEventListener(WebPushService webPushService) {
		this.webPushService = webPushService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void enviarPushTrasCommit(NotificacionCreadaEvent event) {
		webPushService.enviarPushNotificacion(event.getIdNotificacion());
	}
}
