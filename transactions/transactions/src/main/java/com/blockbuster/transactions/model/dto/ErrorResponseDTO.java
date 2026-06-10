package com.blockbuster.transactions.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Estructura estándar para el retorno de excepciones y errores (GlobalExceptionHandler)")
public class ErrorResponseDTO {

    @Schema(description = "Momento exacto en el que ocurrió el fallo", example = "2026-06-09T20:35:12")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP devuelto", example = "400")
    private int status;

    @Schema(description = "Mensaje principal o tipo de error detectado", example = "Bad Request")
    private String error;

    @Schema(description = "Lista con los detalles específicos de los errores (ej. validaciones de campos vacíos)", example = "[\"El ID de la película es obligatorio\", \"La cantidad mínima es 1\"]")
    private List<String> details;
}
