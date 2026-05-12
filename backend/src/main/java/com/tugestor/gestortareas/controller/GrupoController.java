package com.tugestor.gestortareas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.GrupoActivoRequest;
import com.tugestor.gestortareas.dto.GrupoRequest;
import com.tugestor.gestortareas.dto.GrupoResponse;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.service.GrupoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupo")
public class GrupoController {
	private final GrupoService gs;
	public GrupoController(GrupoService gs) {
		this.gs = gs;
	}

	@GetMapping
	@Operation(
			summary = "Listar grupos del usuario autenticado",
			description = "Devuelve los grupos a los que pertenece el usuario autenticado."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de grupos obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token invalido")
	})
	public List<GrupoResponse> listarGrupos(){
		List<Grupo> grupos = gs.obtenerMisGrupos();
		return grupos.stream().map(GrupoResponse::new).toList();
	}

	@PostMapping("/add")
	@Operation(
			summary = "Crear un nuevo grupo",
			description = "Crea un grupo y asigna al usuario autenticado como creador y administrador."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Grupo creado correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos del grupo invalidos"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token invalido")
	})
	public GrupoResponse aniadirGrupo(@Valid @RequestBody GrupoRequest grupoRequest){
		Grupo grupo = gs.guardarGrupo(grupoRequest);
		return new GrupoResponse(grupo);
	}

	@GetMapping("/{id}")
	@Operation(
			summary = "Obtener un grupo por ID",
			description = "Devuelve los detalles de un grupo si el usuario autenticado pertenece a el."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Grupo encontrado correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token invalido"),
		@ApiResponse(responseCode = "403", description = "El usuario no pertenece al grupo"),
		@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	})
	public GrupoResponse listarGrupoId(@PathVariable Long id) {
		Grupo grupo = gs.obtenerPorId(id);
		return new GrupoResponse(grupo);
	}

	@PutMapping("/update/{id}")
	@Operation(
			summary = "Modificar un grupo",
			description = "Actualiza los datos editables de un grupo si el usuario autenticado es creador o admin."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo a modificar", example = "3")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Grupo actualizado correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos del grupo invalidos"),
		@ApiResponse(responseCode = "403", description = "No tiene permisos sobre el grupo"),
		@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	})
	public GrupoResponse modificarGrupo(@PathVariable Long id, @Valid @RequestBody GrupoRequest grupoRequest) {
		Grupo grupo = gs.actualizarGrupo(id, grupoRequest);
		return new GrupoResponse(grupo);
	}

	@DeleteMapping("/delete/{id}")
	@Operation(
			summary = "Eliminar un grupo",
			description = "Elimina un grupo si el usuario autenticado es su creador."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo a eliminar", example = "5")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Grupo eliminado correctamente"),
		@ApiResponse(responseCode = "403", description = "Solo el creador puede eliminar el grupo"),
		@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminarGrupo(@PathVariable Long id) {
		gs.eliminarPorId(id);
	}

	@PatchMapping("/active/{id}")
	@Operation(
			summary = "Activar o inactivar un grupo",
			description = "Cambia el estado activo de un grupo si el usuario autenticado es su creador."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID del grupo", example = "7")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Estado del grupo actualizado correctamente"),
		@ApiResponse(responseCode = "400", description = "Estado invalido"),
		@ApiResponse(responseCode = "403", description = "Solo el creador puede activar o inactivar el grupo"),
		@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	})
	public GrupoResponse cambiarActivoGrupo(@PathVariable Long id,
			@Valid @RequestBody GrupoActivoRequest grupoActivoRequest) {
		Grupo grupo = gs.cambiarActivo(id, grupoActivoRequest);
		return new GrupoResponse(grupo);
	}
}
