package com.blockbuster.transactions.model.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RentalDetailResponseDTO {
    private Long movieId;
    private Integer quantity;
    private BigDecimal priceAtMoment;
    private BigDecimal subtotal;
}