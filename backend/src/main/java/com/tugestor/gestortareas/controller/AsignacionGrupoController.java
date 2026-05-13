package com.tugestor.gestortareas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.AsignacionGrupoListadoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoMiembroResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoReopenRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoResponse;
import com.tugestor.gestortareas.dto.AsignacionGrupoRevisionRequest;
import com.tugestor.gestortareas.service.AsignacionGrupoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupo")
public class AsignacionGrupoController {
	private final AsignacionGrupoService ags;
	public AsignacionGrupoController(AsignacionGrupoService ags) {
		this.ags = ags;
	}

	@PostMapping("/{id}/asignaciones/add")
	@Operation(
			summary = "Crear una asignacion de tarea desde un grupo",
			description = "Crea una asignacion de grupo y genera una tarea individual para cada destinatario."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Asignacion creada correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos de asignacion invalidos"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos para asignar desde el grupo"),
		@ApiResponse(responseCode = "404", description = "Grupo o usuario no encontrado")
	})
	public AsignacionGrupoResponse crearAsignacion(@PathVariable Long id,
			@Valid @RequestBody AsignacionGrupoRequest asignacionGrupoRequest, Principal principal) {
		return ags.crearAsignacion(id, asignacionGrupoRequest, principal.getName());
	}

	@GetMapping("/{id}/asignaciones")
	@Operation(
			summary = "Listar asignaciones de un grupo",
			description = "Devuelve las asignaciones creadas desde un grupo si el usuario autenticado es admin."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de asignaciones obtenido correctamente"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos para ver asignaciones"),
		@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	})
	public List<AsignacionGrupoListadoResponse> listarAsignaciones(@PathVariable Long id, Principal principal) {
		return ags.listarAsignacionesGrupo(id, principal.getName());
	}

	@GetMapping("/{id}/asignaciones/{idAsignacion}")
	@Operation(
			summary = "Ver detalle de una asignacion de grupo",
			description = "Devuelve la asignacion y el estado individual de sus destinatarios si el usuario autenticado es admin."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo", example = "1"),
		@Parameter(name = "idAsignacion", description = "ID de la asignacion", example = "5")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Detalle de asignacion obtenido correctamente"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos para ver la asignacion"),
		@ApiResponse(responseCode = "404", description = "Grupo o asignacion no encontrada")
	})
	public AsignacionGrupoResponse detalleAsignacion(@PathVariable Long id, @PathVariable Long idAsignacion,
			Principal principal) {
		return ags.obtenerDetalleAsignacion(id, idAsignacion, principal.getName());
	}

	@PatchMapping("/asignaciones/{idAsignacionGrupoMiembro}/validate")
	@Operation(
			summary = "Validar una entrega individual",
			description = "Marca una entrega individual como validada si esta en estado ENTREGADA."
			)
	@Parameters({
		@Parameter(name = "idAsignacionGrupoMiembro", description = "ID de la asignacion del miembro", example = "7")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Entrega validada correctamente"),
		@ApiResponse(responseCode = "400", description = "Estado no validable"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos para validar"),
		@ApiResponse(responseCode = "404", description = "Asignacion de miembro no encontrada")
	})
	public AsignacionGrupoMiembroResponse validarEntrega(@PathVariable Long idAsignacionGrupoMiembro,
			@RequestBody(required = false) AsignacionGrupoRevisionRequest revisionRequest, Principal principal) {
		return ags.validarEntrega(idAsignacionGrupoMiembro, revisionRequest, principal.getName());
	}

	@PatchMapping("/asignaciones/{idAsignacionGrupoMiembro}/reopen")
	@Operation(
			summary = "Reabrir una entrega individual",
			description = "Reabre una entrega individual y deja su tarea generada como no completada."
			)
	@Parameters({
		@Parameter(name = "idAsignacionGrupoMiembro", description = "ID de la asignacion del miembro", example = "7")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Entrega reabierta correctamente"),
		@ApiResponse(responseCode = "400", description = "Estado no reabrible o comentario invalido"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos para reabrir"),
		@ApiResponse(responseCode = "404", description = "Asignacion de miembro no encontrada")
	})
	public AsignacionGrupoMiembroResponse reabrirEntrega(@PathVariable Long idAsignacionGrupoMiembro,
			@Valid @RequestBody AsignacionGrupoReopenRequest reopenRequest, Principal principal) {
		return ags.reabrirEntrega(idAsignacionGrupoMiembro, reopenRequest, principal.getName());
	}
}
