package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.AsignacionGrupoListadoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoMiembroResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoReopenRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoRevisionRequest;
import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.GrupoMiembro;
import com.tugestor.gestortareas.model.RolGrupo;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.AsignacionGrupoMiembroRepository;
import com.tugestor.gestortareas.repository.AsignacionGrupoRepository;
import com.tugestor.gestortareas.repository.GrupoMiembroRepository;
import com.tugestor.gestortareas.repository.GrupoRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

@Service
public class AsignacionGrupoServiceImpl implements AsignacionGrupoService {
	private final AsignacionGrupoRepository agr;
	private final AsignacionGrupoMiembroRepository agmr;
	private final GrupoRepository gr;
	private final GrupoMiembroRepository gmr;
	private final TareaRepository tr;
	private final UsuarioRepository ur;

	public AsignacionGrupoServiceImpl(AsignacionGrupoRepository agr, AsignacionGrupoMiembroRepository agmr,
			GrupoRepository gr, GrupoMiembroRepository gmr, TareaRepository tr, UsuarioRepository ur) {
		this.agr = agr;
		this.agmr = agmr;
		this.gr = gr;
		this.gmr = gmr;
		this.tr = tr;
		this.ur = ur;
	}

	@Transactional
	@Override
	public AsignacionGrupoResponse crearAsignacion(Long idGrupo, AsignacionGrupoRequest asignacionGrupoRequest,
			String emailUsuario) {
		Grupo grupo = gr.findById(idGrupo)
				.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + idGrupo));
		Usuario creadorAsignacion = ur.findByEmailIgnoreCase(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
		validarCreadorOAdmin(grupo, creadorAsignacion);
		validarFechaEntrega(asignacionGrupoRequest.getFechaEntrega());

		List<Usuario> destinatarios = resolverDestinatarios(grupo, asignacionGrupoRequest);
		if (destinatarios.isEmpty()) {
			throw new ValidationException("La asignacion debe tener al menos un destinatario.");
		}

		AsignacionGrupo asignacion = new AsignacionGrupo();
		asignacion.setGrupo(grupo);
		asignacion.setCreadorAsignacion(creadorAsignacion);
		asignacion.setTitulo(asignacionGrupoRequest.getTitulo());
		asignacion.setDescripcion(asignacionGrupoRequest.getDescripcion());
		asignacion.setPrioridad(asignacionGrupoRequest.getPrioridad());
		asignacion.setTiempo(asignacionGrupoRequest.getTiempo());
		asignacion.setFechaEntrega(asignacionGrupoRequest.getFechaEntrega());
		asignacion.setFechaCreacion(LocalDateTime.now());
		asignacion.setTipoAsignacion(asignacionGrupoRequest.getTipoAsignacion());
		AsignacionGrupo guardada = agr.save(asignacion);

		List<AsignacionGrupoMiembro> miembrosAsignados = new ArrayList<>();
		for (Usuario destinatario : destinatarios) {
			Tarea tarea = crearTareaParaDestinatario(asignacionGrupoRequest, destinatario);

			AsignacionGrupoMiembro asignacionMiembro = new AsignacionGrupoMiembro();
			asignacionMiembro.setAsignacionGrupo(guardada);
			asignacionMiembro.setUsuarioMiembro(destinatario);
			asignacionMiembro.setTareaGenerada(tarea);
			asignacionMiembro.setEstadoRevision(EstadoRevisionAsignacion.PENDIENTE);
			asignacionMiembro.setFechaAsignacion(LocalDateTime.now());
			asignacionMiembro.setFechaEntregaInicial(asignacionGrupoRequest.getFechaEntrega());
			asignacionMiembro.setFechaEntregaActual(asignacionGrupoRequest.getFechaEntrega());
			miembrosAsignados.add(agmr.save(asignacionMiembro));
		}

		return new AsignacionGrupoResponse(guardada, miembrosAsignados);
	}

	@Override
	public List<AsignacionGrupoListadoResponse> listarAsignacionesGrupo(Long idGrupo, String emailUsuario) {
		Grupo grupo = gr.findById(idGrupo)
				.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + idGrupo));
		Usuario usuario = obtenerUsuarioPorEmail(emailUsuario);
		validarAdminGrupo(grupo, usuario);
		return agr.findByGrupoOrderByFechaCreacionDesc(grupo).stream()
				.map(asignacion -> new AsignacionGrupoListadoResponse(asignacion,
						(int) agmr.countByAsignacionGrupo(asignacion)))
				.toList();
	}

	@Override
	public AsignacionGrupoResponse obtenerDetalleAsignacion(Long idGrupo, Long idAsignacion, String emailUsuario) {
		Grupo grupo = gr.findById(idGrupo)
				.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + idGrupo));
		Usuario usuario = obtenerUsuarioPorEmail(emailUsuario);
		validarAdminGrupo(grupo, usuario);
		AsignacionGrupo asignacion = obtenerAsignacionDelGrupo(idAsignacion, grupo);
		return new AsignacionGrupoResponse(asignacion, agmr.findByAsignacionGrupo(asignacion));
	}

	@Transactional
	@Override
	public AsignacionGrupoMiembroResponse validarEntrega(Long idAsignacionGrupoMiembro,
			AsignacionGrupoRevisionRequest revisionRequest, String emailUsuario) {
		AsignacionGrupoMiembro asignacionMiembro = obtenerAsignacionMiembro(idAsignacionGrupoMiembro);
		validarPuedeRevisar(asignacionMiembro, emailUsuario);
		if (asignacionMiembro.getEstadoRevision() != EstadoRevisionAsignacion.ENTREGADA) {
			throw new ValidationException("Solo se pueden validar entregas en estado ENTREGADA.");
		}

		asignacionMiembro.setEstadoRevision(EstadoRevisionAsignacion.VALIDADA);
		asignacionMiembro.setComentarioRevision(normalizarComentario(
				revisionRequest != null ? revisionRequest.getComentarioRevision() : null));
		asignacionMiembro.setFechaRevision(LocalDateTime.now());
		return new AsignacionGrupoMiembroResponse(agmr.save(asignacionMiembro));
	}

	@Transactional
	@Override
	public AsignacionGrupoMiembroResponse reabrirEntrega(Long idAsignacionGrupoMiembro,
			AsignacionGrupoReopenRequest reopenRequest, String emailUsuario) {
		AsignacionGrupoMiembro asignacionMiembro = obtenerAsignacionMiembro(idAsignacionGrupoMiembro);
		validarPuedeRevisar(asignacionMiembro, emailUsuario);
		if (asignacionMiembro.getEstadoRevision() != EstadoRevisionAsignacion.ENTREGADA
				&& asignacionMiembro.getEstadoRevision() != EstadoRevisionAsignacion.VALIDADA) {
			throw new ValidationException("Solo se pueden reabrir entregas en estado ENTREGADA o VALIDADA.");
		}

		Tarea tarea = asignacionMiembro.getTareaGenerada();
		tarea.setCompletada(false);
		tarea.setFechaCompletada(null);
		tarea.setUsuarioQueCompleta(null);
		tr.save(tarea);

		asignacionMiembro.setEstadoRevision(EstadoRevisionAsignacion.REABIERTA);
		asignacionMiembro.setComentarioRevision(reopenRequest.getComentarioRevision().trim());
		asignacionMiembro.setFechaRevision(LocalDateTime.now());
		return new AsignacionGrupoMiembroResponse(agmr.save(asignacionMiembro));
	}

	private List<Usuario> resolverDestinatarios(Grupo grupo, AsignacionGrupoRequest asignacionGrupoRequest) {
		if (asignacionGrupoRequest.getTipoAsignacion() == TipoAsignacionGrupo.TODO_GRUPO) {
			return gmr.findByGrupo(grupo).stream()
					.map(GrupoMiembro::getUsuario)
					.filter(usuario -> !esCreador(grupo, usuario))
					.toList();
		}
		if (asignacionGrupoRequest.getTipoAsignacion() == TipoAsignacionGrupo.SELECCION_MANUAL) {
			return resolverDestinatariosManuales(grupo, asignacionGrupoRequest.getIdsUsuarios());
		}
		throw new ValidationException("Tipo de asignacion invalido.");
	}

	private List<Usuario> resolverDestinatariosManuales(Grupo grupo, List<Long> idsUsuarios) {
		if (idsUsuarios == null || idsUsuarios.isEmpty()) {
			throw new ValidationException("Debes indicar al menos un usuario destinatario.");
		}
		Set<Long> idsUnicos = new LinkedHashSet<>(idsUsuarios);
		List<Usuario> destinatarios = new ArrayList<>();
		for (Long idUsuario : idsUnicos) {
			Usuario usuario = ur.findById(idUsuario)
					.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));
			if (esCreador(grupo, usuario)) {
				throw new AccessDeniedException("No se puede asignar al creador del grupo por este flujo.");
			}
			if (!gmr.existsByGrupoAndUsuario(grupo, usuario)) {
				throw new EntityNotFoundException("El usuario no pertenece a este grupo.");
			}
			destinatarios.add(usuario);
		}
		return destinatarios;
	}

	private Tarea crearTareaParaDestinatario(AsignacionGrupoRequest request, Usuario destinatario) {
		Tarea tarea = new Tarea();
		tarea.setTitulo(request.getTitulo());
		tarea.setDescripcion(request.getDescripcion());
		tarea.setPrioridad(request.getPrioridad());
		tarea.setTiempo(request.getTiempo());
		tarea.setFechaEntrega(request.getFechaEntrega());
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setCategoria(null);
		tarea.setUsuario(destinatario);
		tarea.setCompletada(false);
		tarea.setFechaCompletada(null);
		return tr.save(tarea);
	}

	private void validarCreadorOAdmin(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		if (!gmr.existsByGrupoAndUsuarioAndRol(grupo, usuario, RolGrupo.ADMIN)) {
			throw new AccessDeniedException("No tienes permiso para asignar tareas desde este grupo.");
		}
	}

	private void validarAdminGrupo(Grupo grupo, Usuario usuario) {
		if (!gmr.existsByGrupoAndUsuarioAndRol(grupo, usuario, RolGrupo.ADMIN)) {
			throw new AccessDeniedException("No tienes permiso para revisar asignaciones de este grupo.");
		}
	}

	private void validarFechaEntrega(LocalDateTime fechaEntrega) {
		if (fechaEntrega != null && fechaEntrega.isBefore(LocalDateTime.now())) {
			throw new ValidationException("La fecha de entrega no puede haber pasado.");
		}
	}

	private Usuario obtenerUsuarioPorEmail(String emailUsuario) {
		return ur.findByEmailIgnoreCase(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}

	private AsignacionGrupo obtenerAsignacionDelGrupo(Long idAsignacion, Grupo grupo) {
		AsignacionGrupo asignacion = agr.findById(idAsignacion)
				.orElseThrow(() -> new EntityNotFoundException("Asignacion no encontrada con id: " + idAsignacion));
		if (asignacion.getGrupo() == null || !asignacion.getGrupo().getIdGrupo().equals(grupo.getIdGrupo())) {
			throw new EntityNotFoundException("La asignacion no pertenece a este grupo.");
		}
		return asignacion;
	}

	private AsignacionGrupoMiembro obtenerAsignacionMiembro(Long idAsignacionGrupoMiembro) {
		return agmr.findById(idAsignacionGrupoMiembro)
				.orElseThrow(() -> new EntityNotFoundException("Asignacion de miembro no encontrada con id: "
						+ idAsignacionGrupoMiembro));
	}

	private void validarPuedeRevisar(AsignacionGrupoMiembro asignacionMiembro, String emailUsuario) {
		AsignacionGrupo asignacion = asignacionMiembro.getAsignacionGrupo();
		if (asignacion == null || asignacion.getGrupo() == null) {
			throw new EntityNotFoundException("La asignacion no esta asociada a un grupo activo.");
		}
		Usuario usuario = obtenerUsuarioPorEmail(emailUsuario);
		validarAdminGrupo(asignacion.getGrupo(), usuario);
	}

	private String normalizarComentario(String comentario) {
		if (comentario == null || comentario.isBlank()) {
			return null;
		}
		return comentario.trim();
	}

	private boolean esCreador(Grupo grupo, Usuario usuario) {
		return grupo.getCreador() != null && usuario != null
				&& grupo.getCreador().getIdUsuario().equals(usuario.getIdUsuario());
	}
}
