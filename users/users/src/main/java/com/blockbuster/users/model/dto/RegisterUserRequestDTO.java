package com.blockbuster.users.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDTO {

	@NotBlank(message = "El username es obligatorio")
	@Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
	@Pattern(
		regexp = "^[A-Za-z0-9._-]+$",
		message = "El username solo puede contener letras, numeros, puntos, guiones y guion bajo"
	)
	private String username;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email debe tener un formato valido")
	@Size(max = 120, message = "El email no puede superar los 120 caracteres")
	private String email;

	@NotBlank(message = "La password es obligatoria")
	@Size(min = 8, max = 72, message = "La password debe tener entre 8 y 72 caracteres")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,72}$",
		message = "La password debe incluir mayuscula, minuscula, numero y caracter especial"
	)
	private String password;
}
