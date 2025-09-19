package com.tugestor.gestortareas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaRedirectConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// Captura "el resto" (puede incluir barras) y reenvía a index.html
		registry.addViewController("/{*path}")
				.setViewName("forward:/index.html");

		// Asegura que este fallback se evalúa el último
		registry.setOrder(Ordered.LOWEST_PRECEDENCE);
	}
}