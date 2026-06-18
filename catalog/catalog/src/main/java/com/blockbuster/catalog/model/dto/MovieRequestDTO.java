package com.blockbuster.catalog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Cuerpo de la peticion para registrar o actualizar una pelicula")
public class MovieRequestDTO {

    @Schema(description = "Titulo de la pelicula", example = "Coraline")
    @NotBlank(message = "El titulo de la pelicula es obligatorio")
    @Size(max = 150, message = "El titulo de la pelicula no puede superar los 150 caracteres")
    private String title;

    @Schema(description = "Identificador de la categoria asociada", example = "6")
    @NotNull(message = "La categoria de la pelicula es obligatoria")
    private Long categoryId;

    @Schema(description = "Ano de estreno", example = "2009")
    @NotNull(message = "El ano de estreno es obligatorio")
    @Min(value = 1900, message = "El ano de estreno debe ser mayor o igual a 1900")
    @Max(value = 2100, message = "El ano de estreno debe ser menor o igual a 2100")
    private Integer releaseYear;

    @Schema(description = "Stock disponible", example = "3")
    @NotNull(message = "El stock de la pelicula es obligatorio")
    @Min(value = 0, message = "El stock de la pelicula no puede ser negativo")
    private Integer stock;

    @Schema(description = "Indica si la pelicula esta disponible para arriendo", example = "true")
    private Boolean available;
}
