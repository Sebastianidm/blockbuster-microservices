package com.blockbuster.notifications.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Cuerpo de la peticion para crear una notificación")
public class NotificationRequestDTO {

    @Schema(description = "ID único generado para la notificacion", example = "120")
    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long userId;

    @Schema(description = "Correo electrónico del destinatario", example = "seba@duocuc.cl")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido (ejemplo@gmail.com)")
    private String recipientEmail;

    @Schema(description = "Asunto del correo electrónico", example = "Confirmación de arriendo Blockbuster")
    @NotBlank(message = "El asunto de la notificación es obligatorio")
    private String subject;

    @Schema(description = "Contenido o cuerpo principal del mensaje a enviar", example = "Tu arriendo 120 fue creado con éxito. Total películas: 2. Monto total: $5000.00")
    @NotBlank(message = "El mensaje de la notificación no puede estar vacío")
    private String message;

    @Schema(description = "Categoría o tipo de evento que dispara la notificación", example = "RENTAL_CONFIRMATION")
    @NotBlank(message = "El tipo de notificación es obligatorio (ej. RENTAL_CONFIRMATION)")
    private String type;
}