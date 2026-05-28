package com.tugestor.gestortareas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.EstadoFiltroTareaResponse;
import com.tugestor.gestortareas.dto.FiltroTareaCombinadoRequest;
import com.tugestor.gestortareas.dto.TareaAsignadaGrupoResponse;
import com.tugestor.gestortareas.dto.TareaFiltroCombinadoResponse;
import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.CriterioOrdenTareaCombinado;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.EstadoFiltroTarea;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.OrigenTareaFiltro;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoRecordatorioTarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.AsignacionGrupoMiembroRepository;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.EstadoFiltroTareaRepository;
import com.tugestor.gestortareas.repository.GrupoMiembroRepository;
import com.tugestor.gestortareas.repository.GrupoRepository;
import com.tugestor.gestortareas.repository.NotificacionRepository;
import com.tugestor.gestortareas.repository.RecordatorioTareaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;
import com.tugestor.gestortareas.service.scoring.TareaInteligenteRankingService;
import com.tugestor.gestortareas.service.scoring.TareaScoringContext;

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
	private final AsignacionGrupoMiembroRepository agmr;
	private final GrupoRepository gr;
	private final GrupoMiembroRepository gmr;
	private final EstadoFiltroTareaRepository eftr;
	private final RecordatorioTareaRepository rtr;
	private final NotificacionRepository nr;
	private final TareaInteligenteRankingService rankingInteligenteService;
	public TareaServiceImpl(TareaRepository tr, CategoriaRepository cr, UsuarioRepository ur,
			AsignacionGrupoMiembroRepository agmr, GrupoRepository gr, GrupoMiembroRepository gmr,
			EstadoFiltroTareaRepository eftr, RecordatorioTareaRepository rtr, NotificacionRepository nr,
			TareaInteligenteRankingService rankingInteligenteService) {
		this.tr = tr;
		this.cr = cr;
		this.ur = ur;
		this.agmr = agmr;
		this.gr = gr;
		this.gmr = gmr;
		this.eftr = eftr;
		this.rtr = rtr;
		this.nr = nr;
		this.rankingInteligenteService = rankingInteligenteService;
	}
	
	@Override
	public Tarea guardarTarea(TareaRequest tareaRequest, String emailUsuario) {
		Usuario usuario = ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
		Long id = tareaRequest.getIdCategoria();
		Categoria categoria = validarPertenencia(id, usuario);
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
			throw new ValidationException("La fecha de completado no puede ser anterior a la fecha de creación.");
		}
		if (tarea.getFechaEntrega() != null && tarea.getFechaEntrega().isBefore(LocalDateTime.now())) {
			throw new ValidationException("La fecha de entrega no puede haber pasado.");
		}
		return tr.save(tarea);
	}

	@Override
	public TareaResponse crearTareaResponse(Tarea tarea, String emailUsuarioCreador) {
		return new TareaResponse(tarea, tieneRecordatorioInteligenteActivo(tarea, emailUsuarioCreador));
	}

	@Override
	public List<TareaResponse> crearTareaResponses(List<Tarea> tareas, String emailUsuarioCreador) {
		Set<Long> idsConRecordatorio = obtenerIdsTareasConRecordatorioInteligenteActivo(
				tareas.stream().map(Tarea::getIdTarea).toList(), emailUsuarioCreador);
		return tareas.stream()
				.map(tarea -> new TareaResponse(tarea, idsConRecordatorio.contains(tarea.getIdTarea())))
				.toList();
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
	@Transactional
	public void eliminarPorId(Long id, String emailUsuario) {
		Tarea tarea = tr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con id: " + id));
		if (!tarea.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No puedes eliminar tareas que no son tuyas.");
		}
		if (agmr.existsByTareaGenerada(tarea)) {
			throw new AccessDeniedException("No puedes eliminar una tarea asignada desde un grupo.");
		}
		nr.desvincularTarea(tarea);
		rtr.deleteByTarea(tarea);
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
			if (agmr.existsByTareaGenerada(existente)) {
				throw new AccessDeniedException("No puedes editar una tarea asignada desde un grupo.");
			}
			
			Long id = tareaRequest.getIdCategoria();
			Categoria categoria = validarPertenencia(id,existente.getUsuario());
			validarCoherenciaCompletado(tareaRequest.isCompletada(), tareaRequest.getFechaCompletada());
			
			if (!tareaRequest.isCompletada()) {
				LocalDateTime nuevaEntrega = tareaRequest.getFechaEntrega();
				if (nuevaEntrega != null) {
					LocalDateTime entregaAnterior = existente.getFechaEntrega();
					boolean seCambiaFechaEntrega = (entregaAnterior == null) || !nuevaEntrega.equals(entregaAnterior);
					if (seCambiaFechaEntrega && nuevaEntrega.isBefore(LocalDateTime.now())) {
						throw new ValidationException("La fecha de entrega no puede haber pasado.");
					}
				}
			}
			
			existente.setTitulo(tareaRequest.getTitulo());
			existente.setTiempo(tareaRequest.getTiempo());
			existente.setPrioridad(tareaRequest.getPrioridad());
			existente.setFechaEntrega(tareaRequest.getFechaEntrega());
			existente.setDescripcion(tareaRequest.getDescripcion());
			existente.setCompletada(tareaRequest.isCompletada());
			existente.setFechaCompletada(tareaRequest.getFechaCompletada());
			existente.setCategoria(categoria);
			
			if (existente.getFechaCompletada() != null &&
					existente.getFechaCompletada().isBefore(existente.getFechaAgregado())) {
				throw new ValidationException("La fecha de completado no puede ser anterior a la fecha de creación.");
			}
			
			return tr.save(existente);
		} else {
			throw new EntityNotFoundException("Tarea no encontrada con id: " + idTarea);
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
	public List<Tarea> obtenerPorPrioridad(String emailUsuario){
		List<Tarea> tareas = tr.findAllByUsuarioEmail(emailUsuario);
		tareas.sort(Comparator.comparingInt((Tarea t) -> t.getPrioridad().ordinal()).reversed());
		return tareas;
	}
	
	@Override
	public List<Tarea> obtenerPorFechaEntrega(String emailUsuario){
		return tr.findAllByUsuarioEmailOrderByFechaEntregaAsc(emailUsuario);
	}

	@Override
	public List<Tarea> filtrarPorPrioridad(String prioridad, String emailUsuario) {
		// Convierto la cadena de prioridad a un enum Prioridad
		Prioridad p;
		try {
			p = Prioridad.valueOf(prioridad.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Prioridad no válida: " + prioridad);
		}
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
				() -> new EntityNotFoundException("Tarea no encontrada con id: " + id));
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
				() -> new EntityNotFoundException("Usuario autenticado no encontrado."));		
		// Verifico si el usuario autenticado es el propietario de la tarea
		if (!tarea.getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
			throw new AccessDeniedException("No puedes completar tareas que no son tuyas.");
		}
		tarea.setCompletada(true);
		tarea.setFechaCompletada(LocalDateTime.now());
		tarea.setUsuarioQueCompleta(usuarioAutenticado);
		tr.save(tarea);
		actualizarRevisionSiEsAsignacionGrupo(tarea);
		return crearTareaResponse(tarea, emailUsuarioQueCompleta);
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
	
	@Override
	public List<Tarea> obtenerTareasProximas(String emailUsuarioCreador) {
		return tr.findByUsuarioEmailAndCompletadaFalseAndFechaEntregaAfterOrderByFechaEntregaAsc(
				emailUsuarioCreador, LocalDateTime.now());
	}
	
	@Override
	public List<Tarea> obtenerTareasVencidas(String emailUsuarioCreador) {
		return tr.findByUsuarioEmailAndCompletadaFalseAndFechaEntregaBeforeOrderByFechaEntregaAsc(
				emailUsuarioCreador, LocalDateTime.now());
	}
	
	@Override
	public List<TareaAsignadaGrupoResponse> obtenerTareasAsignadasGrupo(String emailUsuarioCreador) {
		return mapearTareasAsignadasGrupo(
				agmr.findTareasAsignadasGrupoUsuario(emailUsuarioCreador), emailUsuarioCreador);
	}
	
	@Override
	public List<TareaAsignadaGrupoResponse> obtenerTareasAsignadasGrupoPorGrupo(Long idGrupo,
			String emailUsuarioCreador) {
		return mapearTareasAsignadasGrupo(
				agmr.findTareasAsignadasGrupoUsuarioPorGrupo(emailUsuarioCreador, idGrupo),
				emailUsuarioCreador);
	}
	
	@Override
	public List<TareaFiltroCombinadoResponse> filtrarCombinado(FiltroTareaCombinadoRequest filtro,
			String emailUsuarioCreador) {
		FiltroTareaCombinadoRequest filtroNormalizado = filtro != null ? filtro : new FiltroTareaCombinadoRequest();
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuarioCreador);
		OrigenTareaFiltro origen = filtroNormalizado.getOrigen() != null
				? filtroNormalizado.getOrigen()
				: OrigenTareaFiltro.TODAS;
		CriterioOrdenTareaCombinado criterioOrden = filtroNormalizado.getCriterioOrdenActivo() != null
				? filtroNormalizado.getCriterioOrdenActivo()
				: CriterioOrdenTareaCombinado.FECHA_AGREGADO;
		
		validarFiltroCombinado(filtroNormalizado, usuario, origen);
		
		List<AsignacionGrupoMiembro> asignacionesGrupo = filtroNormalizado.getIdGrupo() != null
				? agmr.findTareasAsignadasGrupoUsuarioPorGrupo(emailUsuarioCreador, filtroNormalizado.getIdGrupo())
				: agmr.findTareasAsignadasGrupoUsuario(emailUsuarioCreador);
		
		Set<Long> idsTareasGrupo = asignacionesGrupo.stream()
				.map(AsignacionGrupoMiembro::getTareaGenerada)
				.filter(Objects::nonNull)
				.map(Tarea::getIdTarea)
				.collect(java.util.stream.Collectors.toSet());
		
		List<TareaScoringContext> contextos = construirContextosFiltroCombinado(
				filtroNormalizado, emailUsuarioCreador, origen, asignacionesGrupo, idsTareasGrupo);
		
		if (criterioOrden == CriterioOrdenTareaCombinado.INTELIGENTE) {
			return mapearContextosFiltroCombinado(
					rankingInteligenteService.ordenarPorScoreDesc(contextos), emailUsuarioCreador);
		}
		
		List<TareaFiltroCombinadoResponse> resultado = new ArrayList<>(
				mapearContextosFiltroCombinado(contextos, emailUsuarioCreador));
		resultado.sort(comparadorFiltroCombinado(criterioOrden));
		return resultado;
	}

	@Override
	public List<TareaFiltroCombinadoResponse> obtenerTareasRecomendadas(FiltroTareaCombinadoRequest filtro,
			String emailUsuarioCreador) {
		return filtrarCombinado(normalizarFiltroRecomendadas(filtro), emailUsuarioCreador);
	}

	@Override
	public EstadoFiltroTareaResponse obtenerEstadoFiltroCombinadoGuardado(String emailUsuario) {
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		return eftr.findByUsuario(usuario)
				.map(EstadoFiltroTareaResponse::new)
				.orElseGet(EstadoFiltroTareaResponse::porDefecto);
	}
	
	@Override
	public EstadoFiltroTareaResponse guardarEstadoFiltroCombinado(FiltroTareaCombinadoRequest filtro,
			String emailUsuario) {
		FiltroTareaCombinadoRequest filtroNormalizado = filtro != null ? filtro : new FiltroTareaCombinadoRequest();
		Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
		OrigenTareaFiltro origen = filtroNormalizado.getOrigen() != null
				? filtroNormalizado.getOrigen()
				: OrigenTareaFiltro.TODAS;
		validarFiltroCombinado(filtroNormalizado, usuario, origen);
		List<Prioridad> prioridades = normalizarPrioridades(filtroNormalizado);
		List<Estado> estados = normalizarEstados(filtroNormalizado);
		
		EstadoFiltroTarea estadoFiltro = eftr.findByUsuario(usuario).orElseGet(EstadoFiltroTarea::new);
		estadoFiltro.setUsuario(usuario);
		estadoFiltro.setOrigen(origen);
		estadoFiltro.setIdGrupo(filtroNormalizado.getIdGrupo());
		estadoFiltro.setPrioridad(primerElementoONull(prioridades));
		estadoFiltro.setPrioridades(prioridades);
		estadoFiltro.setEstado(primerElementoONull(estados));
		estadoFiltro.setEstados(estados);
		estadoFiltro.setPalabrasClave(filtroNormalizado.getPalabrasClave());
		estadoFiltro.setTiempoMax(filtroNormalizado.getTiempoMax());
		estadoFiltro.setIdCategoria(filtroNormalizado.getIdCategoria());
		estadoFiltro.setFechaEntregaExacta(filtroNormalizado.getFechaEntregaExacta());
		estadoFiltro.setFechaEntregaHasta(filtroNormalizado.getFechaEntregaHasta());
		estadoFiltro.setCriterioOrdenActivo(filtroNormalizado.getCriterioOrdenActivo() != null
				? filtroNormalizado.getCriterioOrdenActivo()
				: CriterioOrdenTareaCombinado.FECHA_AGREGADO);
		estadoFiltro.setSoloPorCompletar(filtroNormalizado.getSoloPorCompletar() != null
				? filtroNormalizado.getSoloPorCompletar()
				: true);
		estadoFiltro.setUpdatedAt(LocalDateTime.now());
		return new EstadoFiltroTareaResponse(eftr.save(estadoFiltro));
	}
	
	
	private void validarCoherenciaCompletado(boolean completada, LocalDateTime fechaCompletada) {
		if (completada && fechaCompletada == null) {
			throw new ValidationException("Una tarea completada debe incluir la fecha de finalización.");
		}
		if (!completada && fechaCompletada != null) {
			throw new ValidationException("No se puede asignar una fecha completada a una tarea no completada.");
		}
	}
	
	private void actualizarRevisionSiEsAsignacionGrupo(Tarea tarea) {
		agmr.findByTareaGenerada(tarea).ifPresent(asignacionMiembro -> {
			if (asignacionMiembro.getEstadoRevision() == EstadoRevisionAsignacion.PENDIENTE
					|| asignacionMiembro.getEstadoRevision() == EstadoRevisionAsignacion.REABIERTA) {
				asignacionMiembro.setEstadoRevision(EstadoRevisionAsignacion.ENTREGADA);
				agmr.save(asignacionMiembro);
			}
		});
	}
	
	private boolean cumpleFiltrosTarea(Tarea tarea, FiltroTareaCombinadoRequest filtro) {
		if (tarea == null) {
			return false;
		}
		List<Prioridad> prioridades = normalizarPrioridades(filtro);
		List<Estado> estados = normalizarEstados(filtro);
		if (estados.isEmpty() && Boolean.TRUE.equals(filtro.getSoloPorCompletar())
				&& esEstadoCompletado(tarea.getEstado())) {
			return false;
		}
		if (!prioridades.isEmpty() && !prioridades.contains(tarea.getPrioridad())) {
			return false;
		}
		if (!estados.isEmpty() && !estados.contains(tarea.getEstado())) {
			return false;
		}
		if (filtro.getTiempoMax() != null && tarea.getTiempo() > filtro.getTiempoMax()) {
			return false;
		}
		if (filtro.getIdCategoria() != null
				&& (tarea.getCategoria() == null
						|| !filtro.getIdCategoria().equals(tarea.getCategoria().getIdCategoria()))) {
			return false;
		}
		if (tienePalabrasClave(filtro)
				&& !contienePalabrasClave(tarea, filtro.getPalabrasClave().trim().toLowerCase(Locale.ROOT))) {
			return false;
		}
		if (filtro.getFechaEntregaExacta() != null
				&& (tarea.getFechaEntrega() == null
						|| !tarea.getFechaEntrega().toLocalDate().equals(filtro.getFechaEntregaExacta()))) {
			return false;
		}
		if (filtro.getFechaEntregaHasta() != null) {
			if (tarea.getFechaEntrega() == null) {
				return false;
			}
			LocalDate fechaEntrega = tarea.getFechaEntrega().toLocalDate();
			LocalDate hoy = LocalDate.now();
			if (fechaEntrega.isBefore(hoy) || fechaEntrega.isAfter(filtro.getFechaEntregaHasta())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean tienePalabrasClave(FiltroTareaCombinadoRequest filtro) {
		return filtro.getPalabrasClave() != null && !filtro.getPalabrasClave().trim().isBlank();
	}
	
	private boolean contienePalabrasClave(Tarea tarea, String palabrasClave) {
		String titulo = tarea.getTitulo() != null ? tarea.getTitulo().toLowerCase(Locale.ROOT) : "";
		String descripcion = tarea.getDescripcion() != null ? tarea.getDescripcion().toLowerCase(Locale.ROOT) : "";
		return titulo.contains(palabrasClave) || descripcion.contains(palabrasClave);
	}
	
	private void validarFiltroCombinado(FiltroTareaCombinadoRequest filtro, Usuario usuario, OrigenTareaFiltro origen) {
		if (origen == OrigenTareaFiltro.PERSONAL && filtro.getIdGrupo() != null) {
			throw new ValidationException("No se puede filtrar por grupo cuando el origen es PERSONAL.");
		}
		if (filtro.getFechaEntregaExacta() != null && filtro.getFechaEntregaHasta() != null) {
			throw new ValidationException(
					"No se puede filtrar por fecha exacta y fecha hasta en la misma peticion.");
		}
		if (filtro.getTiempoMax() != null && filtro.getTiempoMax() < 0) {
			throw new ValidationException("El tiempo maximo no puede ser negativo.");
		}
		validarGrupoFiltro(filtro.getIdGrupo(), usuario);
		validarCategoriaFiltro(filtro.getIdCategoria(), usuario);
	}

	private List<Prioridad> normalizarPrioridades(FiltroTareaCombinadoRequest filtro) {
		if (filtro.getPrioridades() != null && !filtro.getPrioridades().isEmpty()) {
			return filtro.getPrioridades();
		}
		if (filtro.getPrioridad() != null) {
			return List.of(filtro.getPrioridad());
		}
		return List.of();
	}

	private List<Estado> normalizarEstados(FiltroTareaCombinadoRequest filtro) {
		if (filtro.getEstados() != null && !filtro.getEstados().isEmpty()) {
			return filtro.getEstados();
		}
		if (filtro.getEstado() != null) {
			return List.of(filtro.getEstado());
		}
		return List.of();
	}

	private <T> T primerElementoONull(List<T> valores) {
		return valores.isEmpty() ? null : valores.get(0);
	}
	
	private void validarGrupoFiltro(Long idGrupo, Usuario usuario) {
		if (idGrupo == null) {
			return;
		}
		Grupo grupo = gr.findById(idGrupo)
				.orElseThrow(() -> new ValidationException("El grupo indicado no pertenece al usuario autenticado."));
		if (!gmr.existsByGrupoAndUsuario(grupo, usuario)) {
			throw new ValidationException("El grupo indicado no pertenece al usuario autenticado.");
		}
	}
	
	private void validarCategoriaFiltro(Long idCategoria, Usuario usuario) {
		if (idCategoria == null) {
			return;
		}
		Categoria categoria = cr.findById(idCategoria)
				.orElseThrow(() -> new ValidationException(
						"La categoria indicada no pertenece al usuario autenticado."));
		if (categoria.getUsuario() == null || !categoria.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
			throw new ValidationException("La categoria indicada no pertenece al usuario autenticado.");
		}
	}
	
	private boolean esEstadoCompletado(Estado estado) {
		return estado == Estado.COMPLETADA || estado == Estado.COMPLETADA_CON_RETRASO;
	}

	private FiltroTareaCombinadoRequest normalizarFiltroRecomendadas(FiltroTareaCombinadoRequest filtro) {
		FiltroTareaCombinadoRequest origen = filtro != null ? filtro : new FiltroTareaCombinadoRequest();
		FiltroTareaCombinadoRequest recomendado = new FiltroTareaCombinadoRequest();
		recomendado.setOrigen(origen.getOrigen() != null ? origen.getOrigen() : OrigenTareaFiltro.TODAS);
		recomendado.setIdGrupo(origen.getIdGrupo());
		recomendado.setPrioridad(origen.getPrioridad());
		recomendado.setEstado(origen.getEstado());
		recomendado.setPrioridades(origen.getPrioridades());
		recomendado.setEstados(origen.getEstados());
		recomendado.setPalabrasClave(origen.getPalabrasClave());
		recomendado.setTiempoMax(origen.getTiempoMax());
		recomendado.setIdCategoria(origen.getIdCategoria());
		recomendado.setFechaEntregaExacta(origen.getFechaEntregaExacta());
		recomendado.setFechaEntregaHasta(origen.getFechaEntregaHasta());
		recomendado.setSoloPorCompletar(origen.getSoloPorCompletar() != null ? origen.getSoloPorCompletar() : true);
		recomendado.setCriterioOrdenActivo(CriterioOrdenTareaCombinado.INTELIGENTE);
		return recomendado;
	}
	
	private List<TareaScoringContext> construirContextosFiltroCombinado(FiltroTareaCombinadoRequest filtro,
			String emailUsuarioCreador, OrigenTareaFiltro origen, List<AsignacionGrupoMiembro> asignacionesGrupo,
			Set<Long> idsTareasGrupo) {
		List<TareaScoringContext> contextos = new ArrayList<>();
		if (filtro.getIdGrupo() == null
				&& (origen == OrigenTareaFiltro.TODAS || origen == OrigenTareaFiltro.PERSONAL)) {
			tr.findByUsuarioEmail(emailUsuarioCreador).stream()
					.filter(tarea -> !idsTareasGrupo.contains(tarea.getIdTarea()))
					.filter(tarea -> cumpleFiltrosTarea(tarea, filtro))
					.map(TareaScoringContext::personal)
					.forEach(contextos::add);
		}
		if (origen == OrigenTareaFiltro.TODAS || origen == OrigenTareaFiltro.GRUPO) {
			asignacionesGrupo.stream()
					.filter(asignacion -> cumpleFiltrosTarea(asignacion.getTareaGenerada(), filtro))
					.map(TareaScoringContext::grupo)
					.forEach(contextos::add);
		}
		return contextos;
	}
	
	private TareaFiltroCombinadoResponse mapearContextoFiltroCombinado(TareaScoringContext contexto,
			Set<Long> idsConRecordatorio) {
		Long idTarea = contexto.getTarea() != null ? contexto.getTarea().getIdTarea() : null;
		boolean recordatorioActivo = idTarea != null && idsConRecordatorio.contains(idTarea);
		if (contexto.tieneAsignacionGrupo()) {
			return new TareaFiltroCombinadoResponse(contexto.getAsignacionGrupoMiembro(), recordatorioActivo);
		}
		return new TareaFiltroCombinadoResponse(contexto.getTarea(), recordatorioActivo);
	}

	private List<TareaFiltroCombinadoResponse> mapearContextosFiltroCombinado(
			List<TareaScoringContext> contextos, String emailUsuarioCreador) {
		Set<Long> idsConRecordatorio = obtenerIdsTareasConRecordatorioInteligenteActivo(
				contextos.stream()
						.map(TareaScoringContext::getTarea)
						.filter(Objects::nonNull)
						.map(Tarea::getIdTarea)
						.toList(),
				emailUsuarioCreador);
		return contextos.stream()
				.map(contexto -> mapearContextoFiltroCombinado(contexto, idsConRecordatorio))
				.toList();
	}

	private List<TareaAsignadaGrupoResponse> mapearTareasAsignadasGrupo(
			List<AsignacionGrupoMiembro> asignaciones, String emailUsuarioCreador) {
		Set<Long> idsConRecordatorio = obtenerIdsTareasConRecordatorioInteligenteActivo(
				asignaciones.stream()
						.map(AsignacionGrupoMiembro::getTareaGenerada)
						.filter(Objects::nonNull)
						.map(Tarea::getIdTarea)
						.toList(),
				emailUsuarioCreador);
		return asignaciones.stream()
				.map(asignacion -> {
					Tarea tarea = asignacion.getTareaGenerada();
					boolean recordatorioActivo = tarea != null
							&& idsConRecordatorio.contains(tarea.getIdTarea());
					return new TareaAsignadaGrupoResponse(asignacion, recordatorioActivo);
				})
				.toList();
	}

	private boolean tieneRecordatorioInteligenteActivo(Tarea tarea, String emailUsuarioCreador) {
		if (tarea == null || tarea.getIdTarea() == null) {
			return false;
		}
		return obtenerIdsTareasConRecordatorioInteligenteActivo(
				List.of(tarea.getIdTarea()), emailUsuarioCreador).contains(tarea.getIdTarea());
	}

	private Set<Long> obtenerIdsTareasConRecordatorioInteligenteActivo(
			List<Long> idsTareas, String emailUsuarioCreador) {
		List<Long> idsNormalizados = idsTareas.stream()
				.filter(Objects::nonNull)
				.distinct()
				.toList();
		if (idsNormalizados.isEmpty()) {
			return Set.of();
		}
		return new HashSet<>(rtr.findActiveTaskIdsByUsuarioEmailAndTipo(
				emailUsuarioCreador, TipoRecordatorioTarea.RECORDATORIO_INTELIGENTE, idsNormalizados));
	}
	
	private Usuario obtenerUsuarioAutenticado(String emailUsuario) {
		return ur.findByEmail(emailUsuario)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado."));
	}
	
	private Comparator<TareaFiltroCombinadoResponse> comparadorFiltroCombinado(
			CriterioOrdenTareaCombinado criterioOrden) {
		Comparator<TareaFiltroCombinadoResponse> desempate = Comparator
				.comparing(TareaFiltroCombinadoResponse::getIdTarea, Comparator.nullsLast(Long::compareTo));
		return switch (criterioOrden) {
		case FECHA_ENTREGA -> Comparator
				.comparing(TareaFiltroCombinadoResponse::getFechaEntrega,
						Comparator.nullsLast(LocalDateTime::compareTo))
				.thenComparing(desempate);
		case PRIORIDAD -> Comparator
				.comparingInt((TareaFiltroCombinadoResponse tarea) -> prioridadOrden(tarea.getPrioridad()))
				.reversed()
				.thenComparing(desempate);
		case TIEMPO -> Comparator
				.comparingInt(TareaFiltroCombinadoResponse::getTiempo)
				.thenComparing(desempate);
		case FECHA_AGREGADO -> Comparator
				.comparingInt((TareaFiltroCombinadoResponse tarea) -> tarea.getFechaAgregado() == null ? 1 : 0)
				.thenComparing(TareaFiltroCombinadoResponse::getFechaAgregado,
						Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(desempate);
		case INTELIGENTE -> Comparator
				.comparing(TareaFiltroCombinadoResponse::getIdTarea, Comparator.nullsLast(Long::compareTo));
		};
	}
	
	private int prioridadOrden(Prioridad prioridad) {
		return prioridad != null ? prioridad.ordinal() : -1;
	}
	
	private Categoria validarPertenencia(Long idCategoria, Usuario usuario) {
		Categoria categoria = cr.findById(idCategoria).orElseThrow(() ->
		new EntityNotFoundException("Categoría no encontrada con id: " + idCategoria));
		
		if (categoria.getUsuario() == null || !categoria.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
			throw new AccessDeniedException("La categoría no pertenece al usuario autenticado.");
		}
		return categoria;
	}

}
