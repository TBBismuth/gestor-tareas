package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	List<Categoria> findByNombreIgnoreCaseContaining(String emailUsuarioCreador);
}
