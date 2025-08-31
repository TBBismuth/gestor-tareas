package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequest {
	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(min = 3, max = 32, message = "El nombre debe tener entre 3 y 32 caracteres")
	private String nombre;
	@NotBlank(message = "El email no puede estar vacío")
	@Email(message = "El email debe ser válido")
	private String email;
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
			message = "La contraseña debe tener al menos 8 caracteres, una mayuscula, minuscula y numero"
			)
	private String password;

	public UsuarioRequest() {
	}

	public UsuarioRequest(String nombre, String email, String password) {
		this.nombre = nombre;
		this.email = email;
		this.password = password;
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
}
