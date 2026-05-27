package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.NotificacionResponse;
import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.NotificacionRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NotificacionServiceImpl implements NotificacionService {
	private final NotificacionRepository nr;
	private final UsuarioRepository ur;

	public NotificacionServiceImpl(NotificacionRepository nr, UsuarioRepository ur) {
		this.nr = nr;
		this.ur = ur;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacionResponse> obtenerActivas(String emailUsuario) {
		return nr.findByUsuarioEmailAndCerradaFalseOrderByFechaCreacionDesc(emailUsuario).stream()
				.map(NotificacionResponse::new)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public long contarActivas(String emailUsuario) {
		return nr.countByUsuarioEmailAndCerradaFalse(emailUsuario);
	}

	@Override
	@Transactional
	public NotificacionResponse cerrarNotificacion(Long idNotificacion, String emailUsuario) {
		Notificacion notificacion = nr.findById(idNotificacion)
				.orElseThrow(() -> new EntityNotFoundException(
						"Notificacion no encontrada con id: " + idNotificacion));
		validarPropietario(notificacion, emailUsuario);
		if (!notificacion.isCerrada()) {
			notificacion.setCerrada(true);
			notificacion.setFechaCierre(LocalDateTime.now());
			notificacion = nr.save(notificacion);
		}
		return new NotificacionResponse(notificacion);
	}

	@Override
	@Transactional
	public void cerrarTodas(String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		LocalDateTime fechaCierre = LocalDateTime.now();
		List<Notificacion> notificaciones = nr.findByUsuarioAndCerradaFalse(usuario);
		notificaciones.forEach(notificacion -> {
			notificacion.setCerrada(true);
			notificacion.setFechaCierre(fechaCierre);
		});
		nr.saveAll(notificaciones);
	}

	private void validarPropietario(Notificacion notificacion, String emailUsuario) {
		if (notificacion.getUsuario() == null
				|| !notificacion.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No puedes modificar notificaciones de otro usuario.");
		}
	}

	private Usuario obtenerUsuarioAutenticado(String emailUsuario) {
		return ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}
}
