package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.PushSubscripcionDeleteRequest;
import com.tugestor.gestortareas.dto.PushSubscripcionRequest;
import com.tugestor.gestortareas.dto.PushSubscripcionResponse;
import com.tugestor.gestortareas.model.PushSubscripcion;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.PushSubscripcionRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PushSubscripcionServiceImpl implements PushSubscripcionService {
	private final PushSubscripcionRepository psr;
	private final UsuarioRepository ur;

	public PushSubscripcionServiceImpl(PushSubscripcionRepository psr, UsuarioRepository ur) {
		this.psr = psr;
		this.ur = ur;
	}

	@Override
	@Transactional
	public PushSubscripcionResponse registrarOActualizar(PushSubscripcionRequest request, String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		LocalDateTime ahora = LocalDateTime.now();
		String endpoint = request.getEndpoint().trim();
		PushSubscripcion pushSubscripcion = psr.findByEndpoint(endpoint).orElseGet(() -> {
			PushSubscripcion nueva = new PushSubscripcion();
			nueva.setEndpoint(endpoint);
			nueva.setFechaAlta(ahora);
			return nueva;
		});
		pushSubscripcion.setUsuario(usuario);
		pushSubscripcion.setP256dh(request.getP256dh().trim());
		pushSubscripcion.setAuth(request.getAuth().trim());
		pushSubscripcion.setUserAgent(normalizarOpcional(request.getUserAgent()));
		pushSubscripcion.setNombreDispositivo(normalizarOpcional(request.getNombreDispositivo()));
		pushSubscripcion.setActiva(true);
		pushSubscripcion.setFechaUltimoUso(ahora);
		pushSubscripcion.setFechaBaja(null);
		return new PushSubscripcionResponse(psr.save(pushSubscripcion));
	}

	@Override
	@Transactional
	public void desactivar(PushSubscripcionDeleteRequest request, String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		String endpoint = request.getEndpoint().trim();
		psr.findByEndpoint(endpoint).ifPresent(pushSubscripcion -> {
			validarPropietario(pushSubscripcion, usuario);
			pushSubscripcion.setActiva(false);
			pushSubscripcion.setFechaBaja(LocalDateTime.now());
			psr.save(pushSubscripcion);
		});
	}

	private void validarPropietario(PushSubscripcion pushSubscripcion, Usuario usuario) {
		if (pushSubscripcion.getUsuario() == null
				|| !pushSubscripcion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
			throw new AccessDeniedException("No puedes desactivar una subscripcion push de otro usuario.");
		}
	}

	private Usuario obtenerUsuarioAutenticado(String emailUsuario) {
		return ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}

	private String normalizarOpcional(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}
}
