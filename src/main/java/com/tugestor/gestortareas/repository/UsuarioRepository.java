package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tugestor.gestortareas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByEmail(String email); //Optional y no List pues solo debe haber un usuario con ese email o ninguno
	
}
