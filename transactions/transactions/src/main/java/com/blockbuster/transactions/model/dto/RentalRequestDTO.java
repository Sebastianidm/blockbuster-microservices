package com.blockbuster.transactions.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Cuerpo de la peticion para crear un nuevo arriendo")
public class RentalRequestDTO {

    @Schema(description = "Id único del usuario registrado en el sistema", example = "1" )
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @Schema(description = "Lista de peliculas que el usuario desea arrendar")
    @NotEmpty(message = "Debe arrendar al menos una película")
    private List<RentalDetailRequestDTO> movies;
}