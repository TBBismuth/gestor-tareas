package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.model.Tarea;

public interface TareaService {
	/*Recuerda que los metodos de una interfaz son public abstract por defecto
	 *Por lo tanto es lo mismo que poner:
	 *(public abstract) Tarea guardarTarea(Tarea tarea); */
	Tarea guardarTarea(Tarea tarea);
	List<Tarea> obtenerTodas();
	Tarea obtenerPorId(Long id);
	void eliminarPorId(Long id);
	Tarea actualizarPorId(Long id, Tarea tareaModificada);
	List<Tarea> obtenerPorTitulo();
	List<Tarea> obtenerPorTiempo();
	List<Tarea> obtenerPorPrioridad();
	List<Tarea> obtenerPorFechaEntrega();
	List<Tarea> filtrarPorPrioridad(String prioridad);
	List<Tarea> filtrarPorTiempo(int tiempo);
	List<Tarea> filtrarPorPalabrasClave(String palabrasClave);
	

}