package com.tugestor.gestortareas.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebPushEventListener {
	private static final Logger logger = LoggerFactory.getLogger(WebPushEventListener.class);
	private final WebPushService webPushService;

	public WebPushEventListener(WebPushService webPushService) {
		this.webPushService = webPushService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void enviarPushTrasCommit(NotificacionCreadaEvent event) {
		try {
			webPushService.enviarPushNotificacion(event.getIdNotificacion());
		} catch (LinkageError error) {
			logger.error("Web Push no se pudo completar por un problema de dependencias: {}",
					error.getClass().getSimpleName());
		} catch (RuntimeException ex) {
			logger.error("Web Push no se pudo completar tras el commit: {}", ex.getClass().getSimpleName());
		}
	}
}
