package com.tugestor.gestortareas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tugestor.gestortareas.config.SelfPingProperties;

@Service
public class SelfPingScheduler {
	private static final Logger logger = LoggerFactory.getLogger(SelfPingScheduler.class);

	private final SelfPingProperties properties;
	private final RestTemplate restTemplate;

	public SelfPingScheduler(SelfPingProperties properties) {
		this.properties = properties;
		this.restTemplate = new RestTemplate();
	}

	@Scheduled(
			initialDelayString = "${app.self-ping.initial-delay-ms:60000}",
			fixedDelayString = "${app.self-ping.fixed-delay-ms:840000}"
	)
	public void ejecutarSelfPing() {
		if (!properties.isEnabled()) {
			return;
		}
		if (properties.getUrl() == null || properties.getUrl().isBlank()) {
			logger.warn("Self-ping habilitado pero app.self-ping.url no esta configurada.");
			return;
		}
		try {
			restTemplate.getForEntity(properties.getUrl().trim(), String.class);
		} catch (RestClientException ex) {
			logger.warn("Self-ping fallido contra la URL configurada: {}", ex.getClass().getSimpleName());
		}
	}
}
