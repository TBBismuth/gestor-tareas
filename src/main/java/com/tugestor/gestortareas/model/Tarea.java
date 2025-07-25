package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Entity	// Anotación de JPA para indicar que esta clase es una entidad
public class Tarea {
	@Id // Anotación de JPA para indicar que este campo es la clave primaria
	@GeneratedValue(strategy= GenerationType.IDENTITY) 
	/* Generación automática del ID
	 * IDENTITY usa AUTO_INCREMENT en la BBDD en MariaDB */
	private Long idTarea;
	@NotBlank(message = "El titulo no puede estar vacio")
	@Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
	private String titulo;
	@Min(value = 1, message = "El tiempo debe ser mayor a 0")
	private int tiempo;
	@NotNull(message = "La prioridad no puede ser nula")
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	private LocalDateTime fechaAgregado = LocalDateTime.now();
	private LocalDateTime fechaEntrega;
	@Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
	private String descripcion;
	@ManyToOne // Relación muchos a uno con la entidad Categoria (muchas tareas -> una categoría)
	@JoinColumn(name = "id_categoria") // Nombre de la columna en la tabla Tarea que referencia a Categoria
	private Categoria categoria;
	private LocalDateTime fechaCompletada;
	private boolean completada;
	@ManyToOne // Relación muchos a uno con la entidad Usuario
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	@ManyToOne
	@JoinColumn(name = "usuario_que_completa_id")
	private Usuario usuarioQueCompleta;
	
	public Tarea() {
		// Obligatorio para JPA
	}
	public Tarea(String titulo, int tiempo, Prioridad prioridad, LocalDateTime fechaEntrega, String descripcion, Categoria categoria, Usuario usuario) {
		this.titulo = titulo;
		this.tiempo = tiempo;
		this.prioridad = prioridad;
		this.fechaEntrega = fechaEntrega;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.usuario = usuario;
	}
	public Tarea(Long idTarea) {	// Constructor para tests
		this.idTarea = idTarea;
	}
	
	public Long getIdTarea() {
		return idTarea;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String nombre) {
		this.titulo = nombre;
	}
	public int getTiempo() {
		return tiempo;
	}
	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}
	public LocalDateTime getFechaAgregado() {
		return fechaAgregado;
	}
	public void setFechaAgregado(LocalDateTime fechaAgregado) {
		this.fechaAgregado = fechaAgregado;
	}
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(LocalDateTime fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public LocalDateTime getFechaCompletada() {
		return fechaCompletada;
	}
	public void setFechaCompletada(LocalDateTime fechaCompletada) {
		this.fechaCompletada = fechaCompletada;
	}
	public boolean isCompletada() {
		return completada;
	}
	public void setCompletada(boolean completada) {
		this.completada = completada;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Usuario getUsuarioQueCompleta() {
		return usuarioQueCompleta;
	}
	public void setUsuarioQueCompleta(Usuario usuarioQueCompleta) {
		this.usuarioQueCompleta = usuarioQueCompleta;
	}
	public Estado getEstado() {
		if (fechaEntrega == null) {
			return Estado.SIN_FECHA;
		}
		if (completada) {
			if (fechaCompletada.isAfter(fechaAgregado)) {
				return Estado.COMPLETADA_CON_RETRASO;
			} else {
				return Estado.COMPLETADA;
			}
		} else {
			if (LocalDateTime.now().isAfter(fechaEntrega)) {
				return Estado.VENCIDA;
			} else {
				return Estado.EN_CURSO;
			}
		}
	}

	

}