package com.blockbuster.catalog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Representacion de una pelicula expuesta por la API")
public class MovieResponseDTO {

    @Schema(description = "Identificador de la pelicula", example = "12")
    private Long id;

    @Schema(description = "Titulo de la pelicula", example = "Coraline")
    private String title;

    @Schema(description = "Identificador de la categoria", example = "6")
    private Long categoryId;

    @Schema(description = "Nombre de la categoria", example = "Family")
    private String categoryName;

    @Schema(description = "Ano de estreno", example = "2009")
    private Integer releaseYear;

    @Schema(description = "Stock disponible", example = "1")
    private Integer stock;

    @Schema(description = "Disponibilidad para arriendo", example = "true")
    private Boolean available;
}
