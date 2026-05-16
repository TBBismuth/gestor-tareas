package com.tugestor.gestortareas.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.GrupoActivoRequest;
import com.tugestor.gestortareas.dto.GrupoJoinRequest;
import com.tugestor.gestortareas.dto.GrupoMiembroAddRequest;
import com.tugestor.gestortareas.dto.GrupoRequest;
import com.tugestor.gestortareas.dto.GrupoRolRequest;
import com.tugestor.gestortareas.dto.GrupoTransferirOwnershipRequest;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.GrupoMiembro;
import com.tugestor.gestortareas.model.RolGrupo;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.AsignacionGrupoRepository;
import com.tugestor.gestortareas.repository.GrupoMiembroRepository;
import com.tugestor.gestortareas.repository.GrupoRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GrupoServiceImpl implements GrupoService {
	private static final String CODIGO_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	private final GrupoRepository gr;
	private final GrupoMiembroRepository gmr;
	private final UsuarioRepository ur;
	private final AsignacionGrupoRepository agr;
	@Value("${app.grupos.requireVerifiedUsers:false}")
	private boolean requireVerifiedUsers;

	public GrupoServiceImpl(GrupoRepository gr, GrupoMiembroRepository gmr, UsuarioRepository ur,
			AsignacionGrupoRepository agr) {
		this.gr = gr;
		this.gmr = gmr;
		this.ur = ur;
		this.agr = agr;
	}

	private Usuario getUsuarioActual() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ur.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}

	@Transactional
	@Override
	public Grupo guardarGrupo(GrupoRequest grupoRequest) {
		Usuario actual = getUsuarioActual();
		validarUsuarioVerificadoSiAplica(actual);

		Grupo grupo = new Grupo();
		grupo.setNombre(grupoRequest.getNombre());
		grupo.setDescripcion(grupoRequest.getDescripcion());
		grupo.setIcono(grupoRequest.getIcono());
		grupo.setColor(grupoRequest.getColor());
		grupo.setActivo(true);
		grupo.setFechaCreacion(LocalDateTime.now());
		grupo.setCreador(actual);
		grupo.setCodigoPublico(generarCodigoPublico());
		grupo.setCodigoInvitacion(generarCodigoInvitacion());

		Grupo guardado = gr.save(grupo);

		GrupoMiembro miembroCreador = new GrupoMiembro();
		miembroCreador.setGrupo(guardado);
		miembroCreador.setUsuario(actual);
		miembroCreador.setRol(RolGrupo.ADMIN);
		miembroCreador.setFechaUnion(LocalDateTime.now());
		miembroCreador.setAnadidoPor(actual);
		gmr.save(miembroCreador);

		return guardado;
	}

	@Override
	public List<Grupo> obtenerMisGrupos() {
		Usuario actual = getUsuarioActual();
		return gmr.findByUsuario(actual).stream()
				.map(GrupoMiembro::getGrupo)
				.toList();
	}

	@Override
	public Grupo obtenerPorId(Long id) {
		Grupo grupo = obtenerGrupo(id);
		Usuario actual = getUsuarioActual();
		validarMiembro(grupo, actual);
		return grupo;
	}

	@Override
	public Grupo actualizarGrupo(Long id, GrupoRequest grupoRequest) {
		Grupo grupo = obtenerGrupo(id);
		Usuario actual = getUsuarioActual();
		validarCreadorOAdmin(grupo, actual);

		grupo.setNombre(grupoRequest.getNombre());
		grupo.setDescripcion(grupoRequest.getDescripcion());
		grupo.setIcono(grupoRequest.getIcono());
		grupo.setColor(grupoRequest.getColor());
		return gr.save(grupo);
	}

	@Transactional
	@Override
	public void eliminarPorId(Long id) {
		Grupo grupo = obtenerGrupo(id);
		Usuario actual = getUsuarioActual();
		validarCreador(grupo, actual);
		agr.findByGrupo(grupo).forEach(asignacion -> {
			asignacion.setGrupo(null);
			agr.save(asignacion);
		});
		gmr.deleteByGrupo(grupo);
		gr.delete(grupo);
	}

	@Override
	public Grupo cambiarActivo(Long id, GrupoActivoRequest grupoActivoRequest) {
		Grupo grupo = obtenerGrupo(id);
		Usuario actual = getUsuarioActual();
		validarCreador(grupo, actual);
		grupo.setActivo(grupoActivoRequest.getActivo());
		return gr.save(grupo);
	}

	@Override
	public List<GrupoMiembro> obtenerMiembros(Long idGrupo) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarMiembro(grupo, actual);
		return gmr.findByGrupo(grupo);
	}

	@Override
	public GrupoMiembro aniadirMiembro(Long idGrupo, GrupoMiembroAddRequest miembroAddRequest) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarPuedeGestionarMiembros(grupo, actual);
		Usuario nuevoMiembro = ur.findByEmailIgnoreCase(miembroAddRequest.getEmail())
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + miembroAddRequest.getEmail()));
		validarUsuarioVerificadoSiAplica(nuevoMiembro);
		if (gmr.existsByGrupoAndUsuario(grupo, nuevoMiembro)) {
			throw new RuntimeException("El usuario ya pertenece a este grupo.");
		}

		GrupoMiembro miembro = new GrupoMiembro();
		miembro.setGrupo(grupo);
		miembro.setUsuario(nuevoMiembro);
		miembro.setRol(RolGrupo.MIEMBRO);
		miembro.setFechaUnion(LocalDateTime.now());
		miembro.setAnadidoPor(actual);
		return gmr.save(miembro);
	}

	@Transactional
	@Override
	public void expulsarMiembro(Long idGrupo, Long idUsuario) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		GrupoMiembro actor = obtenerMembresiaActual(grupo, actual);
		GrupoMiembro objetivo = obtenerMembresiaPorIdUsuario(grupo, idUsuario);

		if (objetivo.getUsuario().getIdUsuario().equals(actual.getIdUsuario())) {
			throw new AccessDeniedException("No puedes expulsarte por esta via. Usa salir del grupo.");
		}
		if (esCreador(grupo, objetivo.getUsuario())) {
			throw new AccessDeniedException("No se puede expulsar al creador del grupo.");
		}
		if (esCreador(grupo, actual)) {
			gmr.delete(objetivo);
			return;
		}
		if (actor.getRol() != RolGrupo.ADMIN) {
			throw new AccessDeniedException("Los miembros no pueden expulsar a otros miembros del grupo.");
		}
		if (objetivo.getRol() != RolGrupo.MIEMBRO) {
			throw new AccessDeniedException("Un admin normal no puede expulsar admins. Solo el creador puede hacerlo.");
		}
		gmr.delete(objetivo);
	}

	@Override
	public GrupoMiembro cambiarRolMiembro(Long idGrupo, Long idUsuario, GrupoRolRequest grupoRolRequest) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarPuedeCambiarRoles(grupo, actual);
		GrupoMiembro objetivo = obtenerMembresiaPorIdUsuario(grupo, idUsuario);

		if (esCreador(grupo, objetivo.getUsuario())) {
			throw new AccessDeniedException("No se puede cambiar el rol del creador por esta via.");
		}
		if (grupoRolRequest.getRol() != RolGrupo.ADMIN && grupoRolRequest.getRol() != RolGrupo.MIEMBRO) {
			throw new RuntimeException("Rol de grupo invalido.");
		}
		objetivo.setRol(grupoRolRequest.getRol());
		return gmr.save(objetivo);
	}

	@Transactional
	@Override
	public void salirDelGrupo(Long idGrupo) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		if (esCreador(grupo, actual)) {
			throw new AccessDeniedException("El creador debe transferir el ownership o borrar el grupo antes de salir.");
		}
		GrupoMiembro membresia = obtenerMembresiaActual(grupo, actual);
		gmr.delete(membresia);
	}

	@Override
	public Grupo transferirOwnership(Long idGrupo, GrupoTransferirOwnershipRequest transferirOwnershipRequest) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarPuedeTransferirOwnership(grupo, actual);
		GrupoMiembro antiguoCreador = obtenerMembresia(grupo, actual);
		GrupoMiembro nuevoCreador = obtenerMembresiaPorIdUsuario(grupo, transferirOwnershipRequest.getIdUsuario());
		if (nuevoCreador.getUsuario().getIdUsuario().equals(actual.getIdUsuario())) {
			throw new RuntimeException("El ownership debe transferirse a otro usuario.");
		}
		if (nuevoCreador.getRol() != RolGrupo.ADMIN) {
			throw new AccessDeniedException("Solo se puede transferir el ownership a un admin del grupo.");
		}
		if (antiguoCreador.getRol() != RolGrupo.ADMIN) {
			antiguoCreador.setRol(RolGrupo.ADMIN);
			gmr.save(antiguoCreador);
		}
		grupo.setCreador(nuevoCreador.getUsuario());
		return gr.save(grupo);
	}

	@Override
	public String obtenerCodigoInvitacion(Long idGrupo) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarPuedeVerCodigoInvitacion(grupo, actual);
		return grupo.getCodigoInvitacion();
	}

	@Override
	public String regenerarCodigoInvitacion(Long idGrupo) {
		Grupo grupo = obtenerGrupo(idGrupo);
		Usuario actual = getUsuarioActual();
		validarPuedeRegenerarCodigoInvitacion(grupo, actual);
		grupo.setCodigoInvitacion(generarCodigoInvitacion());
		return gr.save(grupo).getCodigoInvitacion();
	}

	@Transactional
	@Override
	public GrupoMiembro unirsePorCodigo(GrupoJoinRequest grupoJoinRequest) {
		Usuario actual = getUsuarioActual();
		validarUsuarioVerificadoSiAplica(actual);
		Grupo grupo = gr.findByCodigoInvitacion(grupoJoinRequest.getCodigoInvitacion())
				.orElseThrow(() -> new EntityNotFoundException("Codigo de invitacion invalido o inexistente."));
		if (!grupo.isActivo()) {
			throw new AccessDeniedException("No puedes unirte a un grupo inactivo.");
		}
		if (gmr.existsByGrupoAndUsuario(grupo, actual)) {
			throw new RuntimeException("Ya perteneces a este grupo.");
		}

		GrupoMiembro miembro = new GrupoMiembro();
		miembro.setGrupo(grupo);
		miembro.setUsuario(actual);
		miembro.setRol(RolGrupo.MIEMBRO);
		miembro.setFechaUnion(LocalDateTime.now());
		miembro.setAnadidoPor(null);
		return gmr.save(miembro);
	}

	private Grupo obtenerGrupo(Long id) {
		return gr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + id));
	}

	private GrupoMiembro obtenerMembresia(Grupo grupo, Usuario usuario) {
		return gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new EntityNotFoundException("Miembro no encontrado en este grupo."));
	}

	private GrupoMiembro obtenerMembresiaActual(Grupo grupo, Usuario usuario) {
		return gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new AccessDeniedException("No perteneces a este grupo."));
	}

	private GrupoMiembro obtenerMembresiaPorIdUsuario(Grupo grupo, Long idUsuario) {
		Usuario usuario = ur.findById(idUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));
		return gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new EntityNotFoundException("El usuario no pertenece a este grupo."));
	}

	private void validarMiembro(Grupo grupo, Usuario usuario) {
		if (!gmr.existsByGrupoAndUsuario(grupo, usuario)) {
			throw new AccessDeniedException("No perteneces a este grupo.");
		}
	}

	private void validarCreadorOAdmin(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		if (!gmr.existsByGrupoAndUsuarioAndRol(grupo, usuario, RolGrupo.ADMIN)) {
			throw new AccessDeniedException("No tienes permiso para modificar este grupo.");
		}
	}

	private void validarPuedeGestionarMiembros(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		GrupoMiembro membresia = gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new AccessDeniedException("No perteneces a este grupo."));
		if (membresia.getRol() != RolGrupo.ADMIN) {
			throw new AccessDeniedException("Los miembros no pueden anadir nuevos miembros al grupo.");
		}
	}

	private void validarCreador(Grupo grupo, Usuario usuario) {
		if (!esCreador(grupo, usuario)) {
			throw new AccessDeniedException("Solo el creador puede realizar esta accion sobre el grupo.");
		}
	}

	private void validarPuedeCambiarRoles(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		GrupoMiembro membresia = gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new AccessDeniedException("No perteneces a este grupo."));
		if (membresia.getRol() == RolGrupo.ADMIN) {
			throw new AccessDeniedException("Un admin normal no puede cambiar roles. Solo el creador puede promover o degradar miembros.");
		}
		throw new AccessDeniedException("Los miembros no pueden cambiar roles. Solo el creador puede promover o degradar miembros.");
	}

	private void validarPuedeTransferirOwnership(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		GrupoMiembro membresia = gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new AccessDeniedException("No perteneces a este grupo."));
		if (membresia.getRol() == RolGrupo.ADMIN) {
			throw new AccessDeniedException("Un admin normal no puede transferir el ownership. Solo el creador puede hacerlo.");
		}
		throw new AccessDeniedException("Los miembros no pueden transferir el ownership. Solo el creador puede hacerlo.");
	}

	private void validarPuedeVerCodigoInvitacion(Grupo grupo, Usuario usuario) {
		if (esCreador(grupo, usuario)) {
			return;
		}
		GrupoMiembro membresia = gmr.findByGrupoAndUsuario(grupo, usuario)
				.orElseThrow(() -> new AccessDeniedException("No perteneces a este grupo."));
		if (membresia.getRol() != RolGrupo.ADMIN) {
			throw new AccessDeniedException("Los miembros no pueden ver el codigo de invitacion del grupo.");
		}
	}

	private void validarPuedeRegenerarCodigoInvitacion(Grupo grupo, Usuario usuario) {
		if (!esCreador(grupo, usuario)) {
			throw new AccessDeniedException("Solo el creador puede regenerar el codigo de invitacion del grupo.");
		}
	}

	private boolean esCreador(Grupo grupo, Usuario usuario) {
		return grupo.getCreador() != null && grupo.getCreador().getIdUsuario().equals(usuario.getIdUsuario());
	}

	private void validarUsuarioVerificadoSiAplica(Usuario usuario) {
		if (requireVerifiedUsers && !usuario.isVerificado()) {
			throw new AccessDeniedException("El usuario debe estar verificado para usar grupos.");
		}
	}

	private String generarCodigoPublico() {
		String codigo;
		do {
			codigo = generarCodigo(8);
		} while (gr.existsByCodigoPublico(codigo));
		return codigo;
	}

	private String generarCodigoInvitacion() {
		String codigo;
		do {
			codigo = generarCodigo(24);
		} while (gr.existsByCodigoInvitacion(codigo));
		return codigo;
	}

	private String generarCodigo(int longitud) {
		StringBuilder codigo = new StringBuilder(longitud);
		for (int i = 0; i < longitud; i++) {
			codigo.append(CODIGO_CHARS.charAt(RANDOM.nextInt(CODIGO_CHARS.length())));
		}
		return codigo.toString();
	}
}
