package com.blockbuster.transactions.model.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Objeto de respuesta que representa un arriendo consolidado")
public class RentalResponseDTO {

    @Schema(description = "ID único generado para la transacción de arriendo", example = "150")
    private Long id;

    @Schema(description = "ID del usuario que realizó el arriendo", example = "1")
    private Long userId;

    @Schema(description = "Fecha y hora en que se registró el arriendo", example = "2026-06-09T20:30:00")
    private LocalDateTime rentalDate;

    @Schema(description = "Fecha y hora límite para la devolución", example = "2026-06-12T20:30:00")
    private LocalDateTime returnDate;

    @Schema(description = "Estado actual de la transacción", example = "ACTIVE")
    private String status;

    @Schema(description = "Monto final calculado por el total de las películas", example = "5000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Desglose de las películas incluidas en esta transacción")
    private List<RentalDetailResponseDTO> details;
    
}