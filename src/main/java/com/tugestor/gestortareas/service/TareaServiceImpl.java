package com.tugestor.gestortareas.service;

import java.util.List;
import org.springframework.stereotype.Service;
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

}
