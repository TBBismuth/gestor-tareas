package com.tugestor.gestortareas.service;

import java.util.*;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

@Service
public class TareaServiceImpl implements TareaService{
	// Inyección de dependencias a través de @Autowired
	// @Autowired
	// private TareaRepository tr;
	// @Autowired
	// private CategoriaRepository cr;
	
	// Inyección de dependencias a través del constructor
	private final TareaRepository tr;
	private final CategoriaRepository cr;
	private final UsuarioRepository ur;
	public TareaServiceImpl(TareaRepository tr, CategoriaRepository cr, UsuarioRepository ur) {
		this.tr = tr;
		this.cr = cr;
		this.ur = ur;
	}

	@Override
	public Tarea guardarTarea(Tarea tarea) {
		Long id = tarea.getCategoria().getIdCategoria();
		Categoria categoria = cr.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada con el id: " + id));
		tarea.setCategoria(categoria);
		if (tarea.isCompletada() && tarea.getFechaCompletada() == null) {
			throw new RuntimeException("Una tarea completada debe incluir la fecha de finalización.");
		}	//Validacion de completada sin fechaCompletada
		if (tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isBefore(tarea.getFechaAgregado())) {
			throw new RuntimeException("La fecha de completado no puede ser anterior a la fecha de creación.");
		}	//Validacion de fechaCompletada anterior a fechaAgregado
		return tr.save(tarea);
	}

	@Override
	public List<Tarea> obtenerTodas() {
		return tr.findAll();
	}

	@Override
	public Tarea obtenerPorId(Long idTarea) {
		/*findById(id) devuelve un Optional<Tarea> donde puede haber o NO una tarea
		 *Optional se usa para evitar el nullPointerException
		 *orElse(null) se usa para, si el Optional tiene tarea que la devuelva, sino(orElse), devuelve null*/
		return tr.findById(idTarea).orElseThrow(() -> new RuntimeException("No existe la tarea con ID " + idTarea));
	}

	@Override
	public void eliminarPorId(Long id) {
		tr.deleteById(id);
	}
	
	@Override
	public Tarea actualizarPorId(Long idTarea, Tarea tareaModificada) {
		Optional<Tarea> tareaOriginal = tr.findById(idTarea);
		// Optional<Tarea> para evitar errores null. Puede contener una tarea o estar vacío.
		// Si la tarea existe creo copia y sobreescribo la original. Si no, lanzamos una excepción.
		if(tareaOriginal.isPresent()) {
			Tarea existente = tareaOriginal.get();
			existente.setTitulo(tareaModificada.getTitulo());
			existente.setTiempo(tareaModificada.getTiempo());
			existente.setPrioridad(tareaModificada.getPrioridad());
			existente.setFechaEntrega(tareaModificada.getFechaEntrega());
			existente.setDescripcion(tareaModificada.getDescripcion());
			existente.setCompletada(tareaModificada.isCompletada());
			if (tareaModificada.getFechaCompletada() != null) { // Si se ha completado, y por tanto, tiene fechaCompletada
				existente.setFechaCompletada(tareaModificada.getFechaCompletada());
			}
			if (existente.isCompletada() && existente.getFechaCompletada() == null) {
				throw new RuntimeException("Una tarea completada debe incluir la fecha de finalización.");
			}	//Validacion de completada sin fechaCompletada
			if (existente.getFechaCompletada() != null && existente.getFechaCompletada().isBefore(existente.getFechaAgregado())) {
				throw new RuntimeException("La fecha de completado no puede ser anterior a la fecha de creación.");
			}	//Validacion de fechaCompletada anterior a fechaAgregado
			return tr.save(existente);
		}else {
			throw new RuntimeException("No existe la tarea con ID "+ idTarea);
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

	
	@Override
	public List<Tarea> filtrarPorCategoria(Long idCategoria) {
		return tr.findByCategoria_IdCategoria(idCategoria);
	}

	@Override
	public Estado obtenerEstado(Long id) {
		return tr.findById(id).orElseThrow(() -> new RuntimeException("No existe la tarea con ID " + id))
				.getEstado();
	}

	@Override
	public List<Tarea> filtrarPorUsuario(Long idUsuario) {
		return tr.findByUsuario_IdUsuario(idUsuario);
	}

	@Override
	public Tarea guardarTarea(TareaRequest tareaRequest) {
		Tarea tarea = new Tarea();
		tarea.setTitulo(tareaRequest.getTitulo());
		tarea.setTiempo(tareaRequest.getTiempo());
		tarea.setPrioridad(tareaRequest.getPrioridad());
		tarea.setFechaEntrega(tareaRequest.getFechaEntrega());
		tarea.setDescripcion(tareaRequest.getDescripcion());
		
		Long idCategoria = tareaRequest.getIdCategoria();
		Categoria categoria = cr.findById(idCategoria)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada con el id: " + idCategoria));
		tarea.setCategoria(categoria);
		
		Long idUsuario = tareaRequest.getIdUsuario();
		Usuario usuario = ur.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + idUsuario));
		tarea.setUsuario(usuario);
		
		tarea.setCompletada(false);
		tarea.setFechaCompletada(null);
		return tr.save(tarea);
	}
}
