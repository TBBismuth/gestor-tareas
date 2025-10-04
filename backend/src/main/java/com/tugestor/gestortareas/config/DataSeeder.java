package com.tugestor.gestortareas.config;

import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.repository.CategoriaRepository;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedCategorias(CategoriaRepository categoriaRepository) {
		return args -> {
			seedCategoria(categoriaRepository, "Trabajo/Estudios", "#2563EB", "💼");
			seedCategoria(categoriaRepository, "Doméstico", "#16A34A", "🏠");
			seedCategoria(categoriaRepository, "Ocio/Personal", "#F59E0B", "🎮");
			//(Hipocresía es decirle 400 veces a la IA que no te pase emoticonos en los comentarios dentro de codigo
			// para luego acabar añadiendolos tú mismo manualmente, y dentro del propio código, no comnetarios xD)
		};
	}
	
	@Transactional
	void seedCategoria(CategoriaRepository repo, String nombre, String color, String icono) {
		var existente = repo.findByNombreIgnoreCase(nombre);

		if (existente.isPresent()) {
			Categoria c = existente.get();
			boolean changed = false;

			if (!c.isProtegida()) { c.setProtegida(true); changed = true; }
			if (!Objects.equals(c.getColor(), color)) { c.setColor(color); changed = true; }
			if (!Objects.equals(c.getIcono(), icono)) { c.setIcono(icono); changed = true; }

			if (changed) repo.save(c); // solo toca DB si hay cambios
		} else {
			Categoria c = new Categoria();
			c.setNombre(nombre);
			c.setProtegida(true);
			c.setColor(color);
			c.setIcono(icono);
			repo.save(c);
		}
	}
}