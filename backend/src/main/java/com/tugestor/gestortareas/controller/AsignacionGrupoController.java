package com.tugestor.gestortareas.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.AsignacionGrupoRequest;
import com.tugestor.gestortareas.dto.AsignacionGrupoResponse;
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
}
