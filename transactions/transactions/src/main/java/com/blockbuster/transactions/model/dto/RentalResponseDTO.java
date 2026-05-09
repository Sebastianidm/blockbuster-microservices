package com.blockbuster.transactions.model.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RentalResponseDTO {
    private Long id;
    private Long userId;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private String status;
    private BigDecimal totalAmount;
    private List<RentalDetailResponseDTO> details;
}