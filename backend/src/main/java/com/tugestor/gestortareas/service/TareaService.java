package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Tarea;

public interface TareaService {
	/*Recuerda que los metodos de una interfaz son public abstract por defecto
	 *Por lo tanto es lo mismo que poner:
	 *(public abstract) Tarea guardarTarea(Tarea tarea); */
	Tarea guardarTarea(TareaRequest tarea, String emailUsuarioCreador);
	List<Tarea> obtenerTodas(String emailUsuarioCreador);
	Tarea obtenerPorId(Long id, String emailUsuarioCreador);
	void eliminarPorId(Long id, String emailUsuarioCreador);
	Tarea actualizarPorId(Long idTarea, TareaRequest tareaRequest, String emailUsuarioModificador);
	List<Tarea> obtenerPorTitulo(String emailUsuarioCreador);
	List<Tarea> obtenerPorTiempo(String emailUsuarioCreador);
	List<Tarea> obtenerPorPrioridad(String emailUsuarioCreador);
	List<Tarea> obtenerPorFechaEntrega(String emailUsuarioCreador);
	List<Tarea> filtrarPorPrioridad(String prioridad, String emailUsuarioCreador);
	List<Tarea> filtrarPorTiempo(int tiempo, String emailUsuarioCreador);
	List<Tarea> filtrarPorPalabrasClave(String palabrasClave, String emailUsuarioCreador);
	List<Tarea> filtrarPorCategoria(Long idCategoria, String emailUsuarioCreador);
	List<Tarea> filtrarPorUsuario(Long idUsuario);
	List<Tarea> filtrarPorEstado(Estado estado, String emailUsuarioCreador);
	Estado obtenerEstado(Long id, String emailUsuarioCreador);
	TareaResponse marcarTareaCompletada(Long idTarea, String emailUsuarioQueCompleta);
	List<Tarea> obtenerTareasHoy(String emailUsuarioCreador);
}