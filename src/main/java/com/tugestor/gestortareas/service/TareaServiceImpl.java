package com.tugestor.gestortareas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

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
	public Tarea guardarTarea(TareaRequest tareaRequest, String emailUsuario) {
		Usuario usuario = ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + emailUsuario));
		Long id = tareaRequest.getIdCategoria();
		Categoria categoria = cr.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada con el id: " + id));

		// Validación coherente completado/fechaCompletada
		validarCoherenciaCompletado(tareaRequest.isCompletada(), tareaRequest.getFechaCompletada());

		Tarea tarea = new Tarea();
		tarea.setTitulo(tareaRequest.getTitulo());
		tarea.setTiempo(tareaRequest.getTiempo());
		tarea.setPrioridad(tareaRequest.getPrioridad());
		tarea.setFechaEntrega(tareaRequest.getFechaEntrega());
		tarea.setDescripcion(tareaRequest.getDescripcion());
		tarea.setFechaAgregado(LocalDateTime.now());
		tarea.setCategoria(categoria);
		tarea.setUsuario(usuario);
		tarea.setCompletada(tareaRequest.isCompletada());
		tarea.setFechaCompletada(tareaRequest.getFechaCompletada());

		if (tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isBefore(tarea.getFechaAgregado())) {
			throw new RuntimeException("La fecha de completado no puede ser anterior a la fecha de creación.");
		}
		if (tarea.getFechaEntrega() != null && tarea.getFechaEntrega().isBefore(LocalDateTime.now())) {
			throw new ValidationException("La fecha de entrega no puede haber pasado.");
		}
		return tr.save(tarea);
	}


	@Override
	public List<Tarea> obtenerTodas(String principal) {
		return tr.findByUsuarioEmail(principal);
	}

	@Override
	public Tarea obtenerPorId(Long idTarea, String emailUsuario) {
		Tarea tarea = tr.findById(idTarea)
				.orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con id: " + idTarea));
		if (!tarea.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No puedes ver tareas que no son tuyas.");
		}
		/*findById(id) devuelve un Optional<Tarea> donde puede haber o NO una tarea
		 *Optional se usa para evitar el nullPointerException
		 *orElse(null) se usa para, si el Optional tiene tarea que la devuelva, sino(orElse), devuelve null*/
		return tarea;
	}

	@Override
	public void eliminarPorId(Long id, String emailUsuario) {
		Tarea tarea = tr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con id: " + id));
		if (!tarea.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No puedes eliminar tareas que no son tuyas.");
		}
		tr.delete(tarea);
	}

	@Override
	public Tarea actualizarPorId(Long idTarea, TareaRequest tareaRequest, String emailUsuario) {
		Optional<Tarea> tareaOriginal = tr.findById(idTarea);

		if (tareaOriginal.isPresent()) {
			Tarea existente = tareaOriginal.get();

			if (!existente.getUsuario().getEmail().equals(emailUsuario)) {
				throw new AccessDeniedException("No tienes permiso para modificar esta tarea.");
			}

			// Validación coherente antes de actualizar
			validarCoherenciaCompletado(tareaRequest.isCompletada(), tareaRequest.getFechaCompletada());

			existente.setTitulo(tareaRequest.getTitulo());
			existente.setTiempo(tareaRequest.getTiempo());
			existente.setPrioridad(tareaRequest.getPrioridad());
			existente.setFechaEntrega(tareaRequest.getFechaEntrega());
			existente.setDescripcion(tareaRequest.getDescripcion());
			existente.setCompletada(tareaRequest.isCompletada());
			existente.setFechaCompletada(tareaRequest.getFechaCompletada());

			if (existente.getFechaCompletada() != null &&
					existente.getFechaCompletada().isBefore(existente.getFechaAgregado())) {
				throw new RuntimeException("La fecha de completado no puede ser anterior a la fecha de creación.");
			}

			if (existente.getFechaEntrega() != null &&
					existente.getFechaEntrega().isBefore(LocalDateTime.now())) {
				throw new ValidationException("La fecha de entrega no puede haber pasado.");
			}

			return tr.save(existente);
		} else {
			throw new RuntimeException("No existe la tarea con ID " + idTarea);
		}
	}

	@Override
	public 	List<Tarea> obtenerPorTitulo(String emailUsuario){
		return tr.findAllByUsuarioEmailOrderByTituloAsc(emailUsuario);
	}

	@Override
	public 	List<Tarea> obtenerPorTiempo(String emailUsuario){
		return tr.findAllByUsuarioEmailOrderByTiempoAsc(emailUsuario);
	}
	
	@Override
	public 	List<Tarea> obtenerPorPrioridad(String emailUsuario){
		return tr.findAllByUsuarioEmailOrderByPrioridadAsc(emailUsuario);
	}
	
	@Override
	public List<Tarea> obtenerPorFechaEntrega(String emailUsuario){
		return tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario);
	}
	
	@Override
	public List<Tarea> filtrarPorPrioridad(String prioridad, String emailUsuario) {
		// Convierto la cadena de prioridad a un enum Prioridad
		Prioridad p = Prioridad.valueOf(prioridad.toUpperCase());
		return tr.findByUsuarioEmailAndPrioridad(emailUsuario, p);
	}
	
	@Override
	public List<Tarea> filtrarPorTiempo(int tiempo, String emailUsuario) {
		return tr.findByUsuarioEmailAndTiempoLessThanEqual(emailUsuario, tiempo);
	}
	
	@Override
	public List<Tarea> filtrarPorPalabrasClave(String palabrasClave, String emailUsuario) {
		palabrasClave = palabrasClave.replace("-", " ");
		return tr.findByUsuarioEmailAndTituloContainingIgnoreCaseOrUsuarioEmailAndDescripcionContainingIgnoreCase(emailUsuario, palabrasClave, emailUsuario, palabrasClave);
	}
	
	@Override
	public List<Tarea> filtrarPorCategoria(Long idCategoria, String emailUsuario) {
		return tr.findByUsuarioEmailAndCategoria_IdCategoria(emailUsuario, idCategoria);
	}

	@Override
	public List<Tarea> filtrarPorUsuario(Long idUsuario) {
		return tr.findByUsuario_IdUsuario(idUsuario);
	}
	
	@Override
	public Estado obtenerEstado(Long id, String emailUsuario) {
		Tarea tarea = tr.findById(id).orElseThrow(
				() -> new RuntimeException("No existe la tarea con ID " + id));
		if (!tarea.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No tienes permiso para ver el estado de esta tarea");
		}
		return tarea.getEstado();
	}
	
	@Override
	public TareaResponse marcarTareaCompletada(Long idTarea, String emailUsuarioQueCompleta) {
		// Primero obtengo la tarea por su ID
		Tarea tarea = tr.findById(idTarea).orElseThrow(
				() -> new EntityNotFoundException("Tarea no encontrada con id: " + idTarea));
		// Recupero el usuario autenticado por su email
		Usuario usuarioAutenticado = ur.findByEmail(emailUsuarioQueCompleta).orElseThrow(
				() -> new EntityNotFoundException("Usuario no encontrado con email: " + emailUsuarioQueCompleta));		
		// Verifico si el usuario autenticado es el propietario de la tarea
		if (!tarea.getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
			throw new AccessDeniedException("No puedes completar tareas que no son tuyas.");
		}
		tarea.setCompletada(true);
		tarea.setFechaCompletada(LocalDateTime.now());
		tarea.setUsuarioQueCompleta(usuarioAutenticado);
		tr.save(tarea);
		return new TareaResponse(tarea);
	}

	@Override
	public List<Tarea> filtrarPorEstado(Estado estado, String emailUsuario) {
		List<Tarea> tareasUsuario = tr.findByUsuarioEmail(emailUsuario);
		return tareasUsuario.stream()
				.filter(t -> t.getEstado() == estado)
				.toList();
	}

	@Override
	public List<Tarea> obtenerTareasHoy(String emailUsuarioCreador) {
		LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
		LocalDateTime inicioManiana = inicioHoy.plusDays(1);
		return tr.findByUsuarioEmailAndFechaEntregaBetween(emailUsuarioCreador, inicioHoy, inicioManiana);
	}
	
	private void validarCoherenciaCompletado(boolean completada, LocalDateTime fechaCompletada) {
		if (completada && fechaCompletada == null) {
			throw new RuntimeException("Una tarea completada debe incluir la fecha de finalización.");
		}
		if (!completada && fechaCompletada != null) {
			throw new RuntimeException("No se puede asignar una fecha completada a una tarea no completada.");
		}
	}


}
