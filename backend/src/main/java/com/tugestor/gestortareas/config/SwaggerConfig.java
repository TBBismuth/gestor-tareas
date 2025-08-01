package com.tugestor.gestortareas.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				// Define un servidor con URL y descripción
				.servers(List.of(
						new Server()
						.url("http://localhost:8080")
						.description("Servidor local")
						))
				// Define la información de la API
				.info(new Info()
						.title("API Gestor de Tareas")
						.version("1.0.0")
						.description("API REST para gestión de tareas, usuarios y categorías.")
						.contact(new Contact()
								.name("Miguel Guerrero Murillo")
								.url("")
								.email("")
								)
						);
	}
}
