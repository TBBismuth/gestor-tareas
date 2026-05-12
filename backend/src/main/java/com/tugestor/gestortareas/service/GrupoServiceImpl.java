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
import com.tugestor.gestortareas.dto.GrupoRequest;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.GrupoMiembro;
import com.tugestor.gestortareas.model.RolGrupo;
import com.tugestor.gestortareas.model.Usuario;
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
	@Value("${app.grupos.requireVerifiedUsers:false}")
	private boolean requireVerifiedUsers;

	public GrupoServiceImpl(GrupoRepository gr, GrupoMiembroRepository gmr, UsuarioRepository ur) {
		this.gr = gr;
		this.gmr = gmr;
		this.ur = ur;
	}

	private Usuario getUsuarioActual() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ur.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado: " + email));
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

	private Grupo obtenerGrupo(Long id) {
		return gr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + id));
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

	private void validarCreador(Grupo grupo, Usuario usuario) {
		if (!esCreador(grupo, usuario)) {
			throw new AccessDeniedException("Solo el creador puede realizar esta accion sobre el grupo.");
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
