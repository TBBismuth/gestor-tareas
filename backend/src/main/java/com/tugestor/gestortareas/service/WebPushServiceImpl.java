package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugestor.gestortareas.config.WebPushProperties;
import com.tugestor.gestortareas.model.EstadoPushNotificacion;
import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.PushSubscripcion;
import com.tugestor.gestortareas.repository.NotificacionRepository;
import com.tugestor.gestortareas.repository.PushSubscripcionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class WebPushServiceImpl implements WebPushService {
	private static final Logger logger = LoggerFactory.getLogger(WebPushServiceImpl.class);

	private final WebPushProperties properties;
	private final PushSubscripcionRepository psr;
	private final NotificacionRepository nr;
	private final ObjectMapper objectMapper;

	public WebPushServiceImpl(WebPushProperties properties, PushSubscripcionRepository psr,
			NotificacionRepository nr, ObjectMapper objectMapper) {
		this.properties = properties;
		this.psr = psr;
		this.nr = nr;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public void enviarPushNotificacion(Long idNotificacion) {
		Notificacion notificacion = nr.findById(idNotificacion).orElse(null);
		if (notificacion == null) {
			return;
		}
		if (!properties.isEnabled()) {
			marcarNoAplica(notificacion);
			return;
		}
		if (!configuracionValida()) {
			marcarFallida(notificacion, "Configuracion Web Push incompleta.");
			logger.error("Web Push esta habilitado, pero faltan claves VAPID o subject.");
			return;
		}
		List<PushSubscripcion> subscripciones = psr.findByUsuarioAndActivaTrue(notificacion.getUsuario());
		if (subscripciones.isEmpty()) {
			marcarNoAplica(notificacion);
			return;
		}

		PushService pushService;
		try {
			pushService = new PushService(
					properties.getVapidPublicKey().trim(),
					properties.getVapidPrivateKey().trim(),
					properties.getVapidSubject().trim());
		} catch (LinkageError error) {
			marcarFallida(notificacion, "Dependencia Web Push no disponible.");
			logger.error("No se pudo inicializar Web Push por un problema de dependencias: {}",
					error.getClass().getSimpleName());
			return;
		} catch (Exception ex) {
			marcarFallida(notificacion, "No se pudo inicializar Web Push.");
			logger.error("No se pudo inicializar Web Push con la configuracion VAPID.", ex);
			return;
		}

		int enviadas = 0;
		String ultimoError = null;
		String payload = construirPayload(notificacion);
		for (PushSubscripcion subscripcion : subscripciones) {
			try {
				Notification push = new Notification(
						subscripcion.getEndpoint(),
						subscripcion.getP256dh(),
						subscripcion.getAuth(),
						payload);
				HttpResponse response = pushService.send(push);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode >= 200 && statusCode < 300) {
					enviadas++;
				} else {
					ultimoError = "Servicio push respondio con estado " + statusCode + ".";
					if (statusCode == 404 || statusCode == 410) {
						darDeBajaSubscripcion(subscripcion);
					}
				}
			} catch (LinkageError error) {
				ultimoError = "Dependencia Web Push no disponible.";
				logger.error("Fallo enviando Web Push por un problema de dependencias: {}",
						error.getClass().getSimpleName());
			} catch (Exception ex) {
				ultimoError = "No se pudo enviar Web Push.";
				logger.warn("Fallo enviando Web Push para una subscripcion activa: {}", ex.getClass().getSimpleName());
			}
		}
		actualizarEstadoFinal(notificacion, enviadas, ultimoError);
	}

	private String construirPayload(Notificacion notificacion) {
		try {
			return objectMapper.writeValueAsString(Map.of(
					"title", notificacion.getTitulo(),
					"body", notificacion.getMensaje(),
					"url", normalizarUrl(),
					"requireInteraction", true));
		} catch (JsonProcessingException ex) {
			return "{\"title\":\"" + notificacion.getTitulo() + "\",\"body\":\""
					+ notificacion.getMensaje() + "\",\"url\":\"" + normalizarUrl()
					+ "\",\"requireInteraction\":true}";
		}
	}

	private void actualizarEstadoFinal(Notificacion notificacion, int enviadas, String ultimoError) {
		notificacion.setFechaEnvioPush(LocalDateTime.now());
		if (enviadas > 0) {
			notificacion.setPushEstado(EstadoPushNotificacion.ENVIADA);
			notificacion.setErrorPush(null);
		} else {
			notificacion.setPushEstado(EstadoPushNotificacion.FALLIDA);
			notificacion.setErrorPush(ultimoError != null ? ultimoError : "No se pudo enviar Web Push.");
		}
		nr.save(notificacion);
	}

	private void marcarNoAplica(Notificacion notificacion) {
		notificacion.setPushEstado(EstadoPushNotificacion.NO_APLICA);
		notificacion.setErrorPush(null);
		nr.save(notificacion);
	}

	private void marcarFallida(Notificacion notificacion, String error) {
		notificacion.setPushEstado(EstadoPushNotificacion.FALLIDA);
		notificacion.setFechaEnvioPush(LocalDateTime.now());
		notificacion.setErrorPush(error);
		nr.save(notificacion);
	}

	private void darDeBajaSubscripcion(PushSubscripcion subscripcion) {
		subscripcion.setActiva(false);
		subscripcion.setFechaBaja(LocalDateTime.now());
		psr.save(subscripcion);
	}

	private boolean configuracionValida() {
		return tieneTexto(properties.getVapidPublicKey())
				&& tieneTexto(properties.getVapidPrivateKey())
				&& tieneTexto(properties.getVapidSubject());
	}

	private boolean tieneTexto(String valor) {
		return valor != null && !valor.isBlank();
	}

	private String normalizarUrl() {
		return tieneTexto(properties.getDefaultUrl()) ? properties.getDefaultUrl().trim() : "/app";
	}
}
