package com.tugestor.gestortareas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.NotificacionCountResponse;
import com.tugestor.gestortareas.dto.NotificacionResponse;
import com.tugestor.gestortareas.dto.PreferenciasNotificacionRequest;
import com.tugestor.gestortareas.dto.PreferenciasNotificacionResponse;
import com.tugestor.gestortareas.service.NotificacionService;
import com.tugestor.gestortareas.service.PreferenciasNotificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
	private final NotificacionService ns;
	private final PreferenciasNotificacionService pns;

	public NotificacionController(NotificacionService ns, PreferenciasNotificacionService pns) {
		this.ns = ns;
		this.pns = pns;
	}

	@GetMapping
	public List<NotificacionResponse> listarActivas(Principal principal) {
		return ns.obtenerActivas(principal.getName());
	}

	@GetMapping("/count")
	public NotificacionCountResponse contarActivas(Principal principal) {
		return new NotificacionCountResponse(ns.contarActivas(principal.getName()));
	}

	@PatchMapping("/{id}/cerrar")
	public NotificacionResponse cerrarNotificacion(@PathVariable Long id, Principal principal) {
		return ns.cerrarNotificacion(id, principal.getName());
	}

	@PatchMapping("/cerrar-todas")
	public void cerrarTodas(Principal principal) {
		ns.cerrarTodas(principal.getName());
	}

	@GetMapping("/preferencias")
	public PreferenciasNotificacionResponse obtenerPreferencias(Principal principal) {
		return pns.obtenerPreferencias(principal.getName());
	}

	@PutMapping("/preferencias")
	public PreferenciasNotificacionResponse actualizarPreferencias(
			@Valid @RequestBody PreferenciasNotificacionRequest request, Principal principal) {
		return pns.actualizarPreferencias(request, principal.getName());
	}
}
