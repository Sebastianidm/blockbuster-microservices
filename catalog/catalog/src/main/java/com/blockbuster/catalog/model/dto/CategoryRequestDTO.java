package com.blockbuster.catalog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Cuerpo de la peticion para crear o actualizar una categoria")
public class CategoryRequestDTO {

    @Schema(description = "Nombre de la categoria", example = "Sci-Fi")
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 100, message = "El nombre de la categoria no puede superar los 100 caracteres")
    private String name;

    @Schema(description = "Descripcion breve de la categoria", example = "Peliculas de ciencia ficcion")
    @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres")
    private String description;
}
