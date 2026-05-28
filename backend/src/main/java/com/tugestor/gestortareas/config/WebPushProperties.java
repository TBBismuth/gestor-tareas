package com.tugestor.gestortareas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.webpush")
public class WebPushProperties {
	private boolean enabled;
	private String vapidPublicKey;
	private String vapidPrivateKey;
	private String vapidSubject = "mailto:admin@example.com";
	private String defaultUrl = "/app";

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getVapidPublicKey() {
		return vapidPublicKey;
	}
	public void setVapidPublicKey(String vapidPublicKey) {
		this.vapidPublicKey = vapidPublicKey;
	}
	public String getVapidPrivateKey() {
		return vapidPrivateKey;
	}
	public void setVapidPrivateKey(String vapidPrivateKey) {
		this.vapidPrivateKey = vapidPrivateKey;
	}
	public String getVapidSubject() {
		return vapidSubject;
	}
	public void setVapidSubject(String vapidSubject) {
		this.vapidSubject = vapidSubject;
	}
	public String getDefaultUrl() {
		return defaultUrl;
	}
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
}
