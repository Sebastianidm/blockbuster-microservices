package com.blockbuster.catalog.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovieRequestDTO {

    @NotBlank(message = "El título de la película es obligatorio")
    @Size(max = 150, message = "El título de la película no puede superar los 150 caracteres")
    private String title;

    @NotNull(message = "La categoría de la película es obligatoria")
    private Long categoryId;

    @NotNull(message = "El año de estreno es obligatorio")
    @Min(value = 1900, message = "El año de estreno debe ser mayor o igual a 1900")
    @Max(value = 2100, message = "El año de estreno debe ser menor o igual a 2100")
    private Integer releaseYear;

    @NotNull(message = "El stock de la película es obligatorio")
    @Min(value = 0, message = "El stock de la película no puede ser negativo")
    private Integer stock;

    private Boolean available;
}
