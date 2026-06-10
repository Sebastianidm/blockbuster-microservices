package com.blockbuster.notifications.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Objeto de respuesta de una notificación")
public class NotificationResponseDTO {
    @Schema(description = "ID único generado para la notificacion", example = "120")
    private String id;
    @Schema(description = "Correo electrónico del destinatario", example = "seba@duocuc.cl")
    private String recipientEmail;

    @Schema(description = "Asunto del correo electrónico", example = "Confirmación de arriendo Blockbuster")
    private String subject;

    @Schema(description = "Estado actual del envío de la notificación", example = "SENT")
    private String status;

    @Schema(description = "Fecha y hora en que se procesó o registró la notificación", example = "2026-06-09T20:45:00")
    private LocalDateTime timestamp;
}