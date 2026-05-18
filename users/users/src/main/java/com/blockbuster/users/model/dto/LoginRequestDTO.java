package com.blockbuster.users.model.dto;

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
public class LoginRequestDTO {

	@NotBlank(message = "El username es obligatorio")
	@Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
	private String username;

	@NotBlank(message = "La password es obligatoria")
	@Size(min = 8, max = 72, message = "La password debe tener entre 8 y 72 caracteres")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,72}$",
		message = "La password debe incluir mayuscula, minuscula, numero y caracter especial"
	)
	private String password;
}
