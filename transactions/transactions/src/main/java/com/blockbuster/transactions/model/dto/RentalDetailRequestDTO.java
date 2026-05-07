package com.blockbuster.transactions.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalDetailRequestDTO {

    @NotNull(message = "El ID de la película es obligatorio")
    private Long movieId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer quantity;
}