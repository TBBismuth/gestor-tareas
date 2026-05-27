package com.tugestor.gestortareas.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.PreferenciasNotificacionRequest;
import com.tugestor.gestortareas.dto.PreferenciasNotificacionResponse;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.PreferenciasNotificacion;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.GrupoMiembroRepository;
import com.tugestor.gestortareas.repository.GrupoRepository;
import com.tugestor.gestortareas.repository.PreferenciasNotificacionRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PreferenciasNotificacionServiceImpl implements PreferenciasNotificacionService {
	private final PreferenciasNotificacionRepository pnr;
	private final UsuarioRepository ur;
	private final GrupoRepository gr;
	private final GrupoMiembroRepository gmr;

	public PreferenciasNotificacionServiceImpl(PreferenciasNotificacionRepository pnr, UsuarioRepository ur,
			GrupoRepository gr, GrupoMiembroRepository gmr) {
		this.pnr = pnr;
		this.ur = ur;
		this.gr = gr;
		this.gmr = gmr;
	}

	@Override
	@Transactional
	public PreferenciasNotificacionResponse obtenerPreferencias(String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		PreferenciasNotificacion preferencias = obtenerOCrearPreferencias(usuario);
		return new PreferenciasNotificacionResponse(preferencias);
	}

	@Override
	@Transactional
	public PreferenciasNotificacionResponse actualizarPreferencias(PreferenciasNotificacionRequest request,
			String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		PreferenciasNotificacion preferencias = obtenerOCrearPreferencias(usuario);
		Set<Grupo> gruposAviso24h = validarGruposAviso24h(request.getGruposAviso24h(), usuario);
		Set<Prioridad> prioridadesAviso24h = new HashSet<>(
				request.getPrioridadesAviso24h() != null ? request.getPrioridadesAviso24h() : List.of());

		preferencias.setNotificacionesActivas(request.getNotificacionesActivas());
		preferencias.setAviso24hActivo(request.getAviso24hActivo());
		preferencias.setPrioridadesAviso24h(prioridadesAviso24h);
		preferencias.setGruposAviso24h(gruposAviso24h);
		preferencias.setRecordatorioInteligenteActivo(request.getRecordatorioInteligenteActivo());
		preferencias.setNotificarAsignacionesGrupoActivo(request.getNotificarAsignacionesGrupoActivo());

		return new PreferenciasNotificacionResponse(pnr.save(preferencias));
	}

	private PreferenciasNotificacion obtenerOCrearPreferencias(Usuario usuario) {
		return pnr.findByUsuario(usuario).orElseGet(() -> {
			PreferenciasNotificacion preferencias = new PreferenciasNotificacion();
			preferencias.setUsuario(usuario);
			preferencias.setNotificacionesActivas(true);
			preferencias.setAviso24hActivo(false);
			preferencias.setPrioridadesAviso24h(new HashSet<>());
			preferencias.setGruposAviso24h(new HashSet<>());
			preferencias.setRecordatorioInteligenteActivo(false);
			preferencias.setNotificarAsignacionesGrupoActivo(true);
			return pnr.save(preferencias);
		});
	}

	private Set<Grupo> validarGruposAviso24h(List<Long> idsGrupo, Usuario usuario) {
		Set<Grupo> grupos = new HashSet<>();
		if (idsGrupo == null || idsGrupo.isEmpty()) {
			return grupos;
		}
		for (Long idGrupo : new HashSet<>(idsGrupo)) {
			Grupo grupo = gr.findById(idGrupo)
					.orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con id: " + idGrupo));
			if (!gmr.existsByGrupoAndUsuario(grupo, usuario)) {
				throw new AccessDeniedException("El grupo indicado no pertenece al usuario autenticado.");
			}
			grupos.add(grupo);
		}
		return grupos;
	}

	private Usuario obtenerUsuarioAutenticado(String emailUsuario) {
		return ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}
}
