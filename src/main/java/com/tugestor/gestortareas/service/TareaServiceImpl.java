package com.tugestor.gestortareas.service;

import java.util.*;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.repository.TareaRepository;

@Service
public class TareaServiceImpl implements TareaService{
	
	private final TareaRepository tr;
	public TareaServiceImpl(TareaRepository tr) {
		this.tr = tr;
	}

	@Override
	public Tarea guardarTarea(Tarea tarea) {	
		return tr.save(tarea);
	}

	@Override
	public List<Tarea> obtenerTodas() {
		return tr.findAll();
	}

	@Override
	public Tarea obtenerPorId(Long id) {
		/*findById(id) devuelve un Optional<Tarea> donde puede haber o NO una tarea
		 *Optional se usa para evitar el nullPointerException
		 *orElse(null) se usa para, si el Optional tiene tarea que la devuelva, sino(orElse), devuelve null*/
		return tr.findById(id).orElse(null);
	}

	@Override
	public void eliminarPorId(Long id) {
		tr.deleteById(id);
	}
	
	@Override
	public Tarea actualizarPorId(Long id, Tarea tareaModificada) {
		Optional<Tarea> tareaOriginal = tr.findById(id);
		// Optional<Tarea> para evitar errores null. Puede contener una tarea o estar vacío.
		// Si la tarea existe, la modificamos. Si no, lanzamos una excepción.
		if(tareaOriginal.isPresent()) {
			Tarea existente = tareaOriginal.get();
			existente.setTitulo(tareaModificada.getTitulo());
			existente.setTiempo(tareaModificada.getTiempo());
			existente.setPrioridad(tareaModificada.getPrioridad());
			existente.setFechaEntrega(tareaModificada.getFechaEntrega());
			existente.setDescripcion(tareaModificada.getDescripcion());
			return tr.save(existente);
		}else {
			throw new RuntimeException("No existe la tarea con ID "+ id);
		}
	}
	
	@Override
	public 	List<Tarea> obtenerPorTitulo(){
		return tr.findAllByOrderByTituloAsc();
	}

	@Override
	public 	List<Tarea> obtenerPorTiempo(){
		return tr.findAllByOrderByTiempoAsc();
	}
	
	@Override
	public 	List<Tarea> obtenerPorPrioridad(){
		List<Tarea> lista = tr.findAll();
		lista.sort(Comparator.comparing(t -> t.getPrioridad().ordinal()));
		return lista;
	}
	
	@Override
	public List<Tarea> obtenerPorFechaEntrega(){
		return tr.findAllByOrderByFechaEntregaAsc();
	}
	
	@Override
	public List<Tarea> filtrarPorPrioridad(String prioridad) {
		// Convierto la cadena de prioridad a un enum Prioridad
		Prioridad p = Prioridad.valueOf(prioridad.toUpperCase());
		return tr.findByPrioridad(p);
	}
	
	@Override
	public List<Tarea> filtrarPorTiempo(int tiempo) {
		return tr.findByTiempoLessThanEqual(tiempo);
	}
	
	@Override
	public List<Tarea> filtrarPorPalabrasClave(String palabrasClave) {
		palabrasClave = palabrasClave.replace("-", " ");
		return tr.findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(palabrasClave, palabrasClave);
	}
}
