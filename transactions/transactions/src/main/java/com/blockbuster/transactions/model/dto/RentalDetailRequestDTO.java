package com.blockbuster.transactions.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Detalle de cada pelicula solicitada en el arriendo")
public class RentalDetailRequestDTO {

    @Schema(description = "ID de la pelicula en el catalogo", example = "42")
    @NotNull(message = "El ID de la película es obligatorio")
    private Long movieId;

    @Schema(description = "Cantidad de copias a arrendar de esta pelicula", example = "1")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer quantity;
}