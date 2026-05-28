package com.tugestor.gestortareas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationSchedulerService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerService.class);

	private final RecordatorioTareaService recordatorioTareaService;
	private final NotificacionService notificacionService;

	public NotificationSchedulerService(RecordatorioTareaService recordatorioTareaService,
			NotificacionService notificacionService) {
		this.recordatorioTareaService = recordatorioTareaService;
		this.notificacionService = notificacionService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void procesarRecordatoriosAlArrancar() {
		ejecutarProcesamiento("arranque");
	}

	@Scheduled(
			initialDelayString = "${app.notificaciones.scheduler.recordatorios-initial-delay-ms:30000}",
			fixedDelayString = "${app.notificaciones.scheduler.recordatorios-fixed-delay-ms:60000}"
	)
	public void procesarRecordatoriosProgramados() {
		// Uso esta tarea programada para revisar recordatorios pendientes sin depender de una peticion del usuario.
		ejecutarProcesamiento("programado");
	}

	private void ejecutarProcesamiento(String origen) {
		try {
			int recordatoriosProcesados = recordatorioTareaService.procesarRecordatoriosInteligentesVencidos();
			int avisos24hGenerados = notificacionService.procesarAvisos24hPendientes();
			if (recordatoriosProcesados > 0 || avisos24hGenerados > 0) {
				logger.info("Notificaciones procesadas en ciclo {}. Recordatorios: {}, avisos 24h: {}",
						origen, recordatoriosProcesados, avisos24hGenerados);
			}
		} catch (Exception ex) {
			logger.error("Error procesando notificaciones pendientes en ciclo {}.", origen, ex);
		}
	}
}
