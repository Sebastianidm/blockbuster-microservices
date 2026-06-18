package com.blockbuster.catalog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Representacion de una categoria expuesta por la API")
public class CategoryResponseDTO {

    @Schema(description = "Identificador de la categoria", example = "3")
    private Long id;

    @Schema(description = "Nombre de la categoria", example = "Sci-Fi")
    private String name;

    @Schema(description = "Descripcion de la categoria", example = "Peliculas de ciencia ficcion")
    private String description;
}
