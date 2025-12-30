package com.tugestor.gestortareas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Usuario;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	List<Categoria> findByNombreIgnoreCaseContaining(String emailUsuarioCreador);
	boolean existsByNombreIgnoreCase(String nombre);
	Optional<Categoria> findByNombreIgnoreCase(String nombre);
	List<Categoria> findAllByUsuario(Usuario usuario);
	boolean existsByUsuarioAndNombreIgnoreCase(Usuario usuario, String nombre);
	List<Categoria> findByUsuarioAndNombreIgnoreCaseContaining(Usuario usuario, String nombreParcial);

}
