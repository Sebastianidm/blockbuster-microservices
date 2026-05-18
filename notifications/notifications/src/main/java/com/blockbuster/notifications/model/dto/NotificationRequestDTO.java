package com.blockbuster.notifications.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {

    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long userId;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido (ejemplo@gmail.com)")
    private String recipientEmail;

    @NotBlank(message = "El asunto de la notificación es obligatorio")
    private String subject;

    @NotBlank(message = "El mensaje de la notificación no puede estar vacío")
    private String message;

    @NotBlank(message = "El tipo de notificación es obligatorio (ej. RENTAL_CONFIRMATION)")
    private String type;
}