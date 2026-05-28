package com.tugestor.gestortareas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoNotificacion;
import com.tugestor.gestortareas.model.Usuario;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
	List<Notificacion> findByUsuarioEmailAndCerradaFalseOrderByFechaCreacionDesc(String emailUsuario);
	List<Notificacion> findByUsuarioAndCerradaFalse(Usuario usuario);
	long countByUsuarioEmailAndCerradaFalse(String emailUsuario);
	boolean existsByUsuarioAndTareaAndTipo(Usuario usuario, Tarea tarea, TipoNotificacion tipo);
	@Modifying
	@Query("UPDATE Notificacion n SET n.tarea = null WHERE n.tarea = :tarea")
	int desvincularTarea(@Param("tarea") Tarea tarea);
}
