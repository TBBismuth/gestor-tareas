package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.NotificacionResponse;
import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoPushNotificacion;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.PreferenciasNotificacion;
import com.tugestor.gestortareas.model.RecordatorioTarea;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoNotificacion;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.AsignacionGrupoMiembroRepository;
import com.tugestor.gestortareas.repository.NotificacionRepository;
import com.tugestor.gestortareas.repository.PreferenciasNotificacionRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NotificacionServiceImpl implements NotificacionService {
	private final NotificacionRepository nr;
	private final UsuarioRepository ur;
	private final PreferenciasNotificacionRepository pnr;
	private final TareaRepository tr;
	private final AsignacionGrupoMiembroRepository agmr;

	public NotificacionServiceImpl(NotificacionRepository nr, UsuarioRepository ur,
			PreferenciasNotificacionRepository pnr, TareaRepository tr, AsignacionGrupoMiembroRepository agmr) {
		this.nr = nr;
		this.ur = ur;
		this.pnr = pnr;
		this.tr = tr;
		this.agmr = agmr;
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

	@Override
	@Transactional
	public int procesarAvisos24hPendientes() {
		LocalDateTime ahora = LocalDateTime.now();
		LocalDateTime limite = ahora.plusHours(24);
		int generadas = 0;
		for (PreferenciasNotificacion preferencias : pnr.findByNotificacionesActivasTrueAndAviso24hActivoTrue()) {
			if (preferencias.getPrioridadesAviso24h().isEmpty()) {
				continue;
			}
			Usuario usuario = preferencias.getUsuario();
			List<Tarea> tareas = tr.findByUsuarioAndCompletadaFalseAndFechaEntregaBetween(usuario, ahora, limite);
			for (Tarea tarea : tareas) {
				if (debeCrearAviso24h(preferencias, tarea)) {
					crearAviso24h(preferencias, tarea, ahora);
					generadas++;
				}
			}
		}
		return generadas;
	}

	@Override
	@Transactional
	public void crearDesdeRecordatorioInteligente(RecordatorioTarea recordatorio, LocalDateTime fechaCreacion) {
		Tarea tarea = recordatorio.getTarea();
		String tituloTarea = tarea != null ? tarea.getTitulo() : "la tarea";
		Notificacion notificacion = new Notificacion();
		notificacion.setUsuario(recordatorio.getUsuario());
		notificacion.setTarea(tarea);
		notificacion.setTipo(TipoNotificacion.RECORDATORIO_INTELIGENTE);
		notificacion.setTitulo("Recordatorio inteligente");
		notificacion.setMensaje("La tarea \"" + tituloTarea + "\" vence pronto.");
		notificacion.setFechaProgramada(recordatorio.getFechaProgramada());
		notificacion.setFechaCreacion(fechaCreacion);
		notificacion.setCerrada(false);
		notificacion.setPushEstado(EstadoPushNotificacion.NO_APLICA);
		nr.save(notificacion);
	}

	@Override
	@Transactional
	public void crearDesdeAsignacionGrupo(AsignacionGrupoMiembro asignacionGrupoMiembro) {
		Usuario destinatario = asignacionGrupoMiembro.getUsuarioMiembro();
		if (!debeNotificarAsignacionGrupo(destinatario)) {
			return;
		}
		AsignacionGrupo asignacionGrupo = asignacionGrupoMiembro.getAsignacionGrupo();
		Grupo grupo = asignacionGrupo != null ? asignacionGrupo.getGrupo() : null;
		String nombreGrupo = grupo != null ? grupo.getNombre() : "el grupo";
		LocalDateTime ahora = LocalDateTime.now();

		Notificacion notificacion = new Notificacion();
		notificacion.setUsuario(destinatario);
		notificacion.setTarea(asignacionGrupoMiembro.getTareaGenerada());
		notificacion.setGrupo(grupo);
		notificacion.setAsignacionGrupoMiembro(asignacionGrupoMiembro);
		notificacion.setTipo(TipoNotificacion.ASIGNACION_GRUPO);
		notificacion.setTitulo("Nueva tarea de grupo");
		notificacion.setMensaje("Te han asignado una nueva tarea en el grupo \"" + nombreGrupo + "\".");
		notificacion.setFechaProgramada(ahora);
		notificacion.setFechaCreacion(ahora);
		notificacion.setCerrada(false);
		notificacion.setPushEstado(EstadoPushNotificacion.NO_APLICA);
		nr.save(notificacion);
	}

	private boolean debeCrearAviso24h(PreferenciasNotificacion preferencias, Tarea tarea) {
		if (!preferencias.getPrioridadesAviso24h().contains(tarea.getPrioridad())) {
			return false;
		}
		if (nr.existsByUsuarioAndTareaAndTipo(preferencias.getUsuario(), tarea, TipoNotificacion.AVISO_24H)) {
			return false;
		}
		return agmr.findByTareaGenerada(tarea)
				.map(asignacion -> grupoPermitidoParaAviso24h(preferencias, asignacion))
				.orElse(true);
	}

	private boolean grupoPermitidoParaAviso24h(PreferenciasNotificacion preferencias,
			AsignacionGrupoMiembro asignacionGrupoMiembro) {
		if (preferencias.getGruposAviso24h().isEmpty()) {
			return false;
		}
		AsignacionGrupo asignacionGrupo = asignacionGrupoMiembro.getAsignacionGrupo();
		Grupo grupo = asignacionGrupo != null ? asignacionGrupo.getGrupo() : null;
		return grupo != null && contieneGrupo(preferencias.getGruposAviso24h(), grupo);
	}

	private boolean contieneGrupo(Set<Grupo> grupos, Grupo grupoBuscado) {
		return grupos.stream()
				.anyMatch(grupo -> grupo.getIdGrupo() != null
						&& grupo.getIdGrupo().equals(grupoBuscado.getIdGrupo()));
	}

	private void crearAviso24h(PreferenciasNotificacion preferencias, Tarea tarea, LocalDateTime ahora) {
		AsignacionGrupoMiembro asignacionGrupoMiembro = agmr.findByTareaGenerada(tarea).orElse(null);
		AsignacionGrupo asignacionGrupo = asignacionGrupoMiembro != null
				? asignacionGrupoMiembro.getAsignacionGrupo()
				: null;
		Grupo grupo = asignacionGrupo != null ? asignacionGrupo.getGrupo() : null;

		Notificacion notificacion = new Notificacion();
		notificacion.setUsuario(preferencias.getUsuario());
		notificacion.setTarea(tarea);
		notificacion.setGrupo(grupo);
		notificacion.setAsignacionGrupoMiembro(asignacionGrupoMiembro);
		notificacion.setTipo(TipoNotificacion.AVISO_24H);
		notificacion.setTitulo("Tarea proxima a vencer");
		notificacion.setMensaje("La tarea \"" + tarea.getTitulo() + "\" vence en menos de 24 horas.");
		notificacion.setFechaProgramada(ahora);
		notificacion.setFechaCreacion(ahora);
		notificacion.setCerrada(false);
		notificacion.setPushEstado(EstadoPushNotificacion.NO_APLICA);
		nr.save(notificacion);
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

	private boolean debeNotificarAsignacionGrupo(Usuario usuario) {
		PreferenciasNotificacion preferencias = pnr.findByUsuario(usuario)
				.orElseGet(() -> crearPreferenciasPorDefecto(usuario));
		return preferencias.isNotificacionesActivas() && preferencias.isNotificarAsignacionesGrupoActivo();
	}

	private PreferenciasNotificacion crearPreferenciasPorDefecto(Usuario usuario) {
		PreferenciasNotificacion preferencias = new PreferenciasNotificacion();
		preferencias.setUsuario(usuario);
		preferencias.setNotificacionesActivas(true);
		preferencias.setAviso24hActivo(false);
		preferencias.setRecordatorioInteligenteActivo(false);
		preferencias.setNotificarAsignacionesGrupoActivo(true);
		return pnr.save(preferencias);
	}
}
