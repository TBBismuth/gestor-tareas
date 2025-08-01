package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idUsuario;
	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(min = 3, max = 32, message = "El nombre debe tener entre 3 y 32 caracteres")
	private String nombre;
	@Email(message = "El email debe ser válido")
	@NotBlank(message = "El email no puede estar vacío")
	private String email;
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
			message = "La contraseña debe tener al menos 8 caracteres, una mayuscula, minuscula y numero"
			)
	private String password;
	private LocalDateTime fechaRegistro = LocalDateTime.now();
	private boolean activo;
	private boolean verificado;
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	/*	Mapeado por el atributo usuario en la clase Tarea
		Si borras un usuario se borran las tareas asociadas
		Si quitas una tarea de la lista se elimina de la BBDD*/
	private List<Tarea> tareas = new ArrayList<>();

	
	public Usuario() {
		// Constructor por defecto para JPA
	}
	public Usuario(String nombre, String email, String password, boolean activo, boolean verificado) {
		this.nombre = nombre;
		this.email = email;
		this.password = password;
		this.activo = activo;
		this.verificado = verificado;
	}
	public Usuario(Long idUsuario) {	// Constructor para tests
		this.idUsuario = idUsuario;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public boolean isVerificado() {
		return verificado;
	}
	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}
	
	// Estas funciones son para evitar problemas de serialización
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Usuario usuario = (Usuario) o;
		return Objects.equals(idUsuario, usuario.idUsuario);
	}
	@Override
	public int hashCode() {
		return Objects.hash(idUsuario);
	}


}
