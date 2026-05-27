package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.Usuario;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
	List<Notificacion> findByUsuarioEmailAndCerradaFalseOrderByFechaCreacionDesc(String emailUsuario);
	List<Notificacion> findByUsuarioAndCerradaFalse(Usuario usuario);
	long countByUsuarioEmailAndCerradaFalse(String emailUsuario);
}
