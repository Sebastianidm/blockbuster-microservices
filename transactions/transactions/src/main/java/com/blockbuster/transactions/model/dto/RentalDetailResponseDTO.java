package com.blockbuster.transactions.model.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Desglose de los valores calculados por cada película arrendada")
public class RentalDetailResponseDTO {

    @Schema(description = "ID de la película arrendada", example = "42")
    private Long movieId;

    @Schema(description = "Cantidad de copias arrendadas", example = "2")
    private Integer quantity;

    @Schema(description = "Precio de la película fijado al momento de la transacción", example = "2500.00")
    private BigDecimal priceAtMoment;

    @Schema(description = "Subtotal por este ítem (Cantidad x Precio)", example = "5000.00")
    private BigDecimal subtotal;
}