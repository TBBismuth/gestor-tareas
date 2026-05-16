package com.tugestor.gestortareas.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.exception.EmailDuplicadoException;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioServiceImpl implements UsuarioService{
	private final UsuarioRepository ur;
	private final PasswordEncoder pe;
	private final CategoriaRepository cr;
	private final TareaRepository tr;
	public UsuarioServiceImpl(UsuarioRepository ur, PasswordEncoder pe, CategoriaRepository cr, TareaRepository tr) {
		this.ur = ur;
		this.pe = pe;
		this.cr = cr;
		this.tr = tr;
	}
	
	@Deprecated(since="2.0", forRemoval=false)
	@Override
	public Usuario guardarUsuario(Usuario usuario) {
		ur.findByEmail(usuario.getEmail()).ifPresent(temp -> {
			throw new EmailDuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
		});
		usuario.setPassword(pe.encode(usuario.getPassword()));
		return ur.save(usuario);
	}

	@Override
	public Usuario obtenerUsuarioActual() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ur.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado: " + email));
	}

	@Transactional
	@Override
	public void eliminarUsuarioActual() {
		Usuario usuario = obtenerUsuarioActual();
		List<Tarea> tareasCompletadasPorUsuario = tr.findByUsuarioQueCompleta_IdUsuario(usuario.getIdUsuario());
		for (Tarea tarea : tareasCompletadasPorUsuario) {
			tarea.setUsuarioQueCompleta(null);
		}
		if (!tareasCompletadasPorUsuario.isEmpty()) {
			tr.saveAll(tareasCompletadasPorUsuario);
		}
		List<Tarea> tareasUsuario = tr.findByUsuario_IdUsuario(usuario.getIdUsuario());
		if (!tareasUsuario.isEmpty()) {
			tr.deleteAll(tareasUsuario);
		}
		List<Categoria> categoriasUsuario = cr.findAllByUsuario(usuario);
		if (!categoriasUsuario.isEmpty()) {
			cr.deleteAll(categoriasUsuario);
		}
		ur.delete(usuario);
	}

	@Override
	public LoginResponse login(LoginRequest login) {
		Usuario usuario = ur.findByEmail(login.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Credenciales inválidas."));
		if (!pe.matches(login.getPassword(), usuario.getPassword())) {
			throw new BadCredentialsException("Credenciales inválidas.");
		}
		return new LoginResponse(usuario);
	}
	
	@Transactional
	@Override
	public Usuario guardarUsuario(UsuarioRequest usuarioRequest) {
		Usuario usuario = new Usuario(
				usuarioRequest.getNombre(),
				usuarioRequest.getEmail(),
				usuarioRequest.getPassword(),
				true,
				false);
		
		ur.findByEmail(usuario.getEmail()).ifPresent(temp ->{
			throw new EmailDuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
		});
		
		usuario.setPassword(pe.encode(usuario.getPassword()));
		Usuario guardado = ur.save(usuario);
		crearCategoriasBaseUsuario(guardado);
		return guardado;
	}
	
	private void crearCategoriasBaseUsuario(Usuario usuario) {
	    List<Categoria> nuevas = new ArrayList<>();

	    if (!cr.existsByUsuarioAndNombreIgnoreCase(usuario, "Trabajo/Estudios"))
	        nuevas.add(cat(usuario, "Trabajo/Estudios", "#2563EB", "💼"));
	    if (!cr.existsByUsuarioAndNombreIgnoreCase(usuario, "Doméstico"))
	        nuevas.add(cat(usuario, "Doméstico", "#16A34A", "🏠"));
	    if (!cr.existsByUsuarioAndNombreIgnoreCase(usuario, "Ocio/Personal"))
	        nuevas.add(cat(usuario, "Ocio/Personal", "#F59E0B", "🎮"));

	    if (!nuevas.isEmpty())
	    	cr.saveAll(nuevas);
	}
	private Categoria cat(Usuario usuario, String nombre, String color, String icono) {
	    Categoria categoria = new Categoria();
	    categoria.setUsuario(usuario);
	    categoria.setNombre(nombre);
	    categoria.setColor(color);
	    categoria.setIcono(icono);
	    categoria.setProtegida(true);
	    return categoria;
	}
}
