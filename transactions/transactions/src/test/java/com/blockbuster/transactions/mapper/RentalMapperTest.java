package com.blockbuster.transactions.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.model.entity.Rental;
import com.blockbuster.transactions.model.entity.RentalDetail;

class RentalMapperTest {

    private final RentalMapper rentalMapper = new RentalMapper();

    @Test
    void shouldReturnNullWhenRentalIsNull() {
        assertNull(rentalMapper.toResponseDTO(null));
    }

    @Test
    void shouldMapRentalAndCalculateSubtotals() {
        Rental rental = Rental.builder()
                .id(10L)
                .userId(6L)
                .rentalDate(LocalDateTime.of(2026, 6, 17, 10, 0))
                .returnDate(LocalDateTime.of(2026, 6, 20, 10, 0))
                .status("ACTIVE")
                .totalAmount(new BigDecimal("7500.00"))
                .details(List.of(
                        RentalDetail.builder()
                                .movieId(8L)
                                .quantity(2)
                                .priceAtMoment(new BigDecimal("2500.00"))
                                .build(),
                        RentalDetail.builder()
                                .movieId(9L)
                                .quantity(1)
                                .priceAtMoment(new BigDecimal("2500.00"))
                                .build()))
                .build();

        RentalResponseDTO result = rentalMapper.toResponseDTO(rental);

        assertEquals(10L, result.getId());
        assertEquals(6L, result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(new BigDecimal("7500.00"), result.getTotalAmount());
        assertEquals(2, result.getDetails().size());
        assertEquals(8L, result.getDetails().get(0).getMovieId());
        assertEquals(new BigDecimal("5000.00"), result.getDetails().get(0).getSubtotal());
        assertEquals(9L, result.getDetails().get(1).getMovieId());
        assertEquals(new BigDecimal("2500.00"), result.getDetails().get(1).getSubtotal());
    }
}
