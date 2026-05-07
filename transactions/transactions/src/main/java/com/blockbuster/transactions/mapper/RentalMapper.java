package com.blockbuster.transactions.mapper;

import com.blockbuster.transactions.model.dto.*;
import com.blockbuster.transactions.model.entity.Rental;
import com.blockbuster.transactions.model.entity.RentalDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RentalMapper {


    public RentalResponseDTO toResponseDTO(Rental rental) {
        if (rental == null) return null;

        List<RentalDetailResponseDTO> detailDTOs = rental.getDetails().stream()
                .map(this::toDetailResponseDTO)
                .collect(Collectors.toList());

        return RentalResponseDTO.builder()
                .id(rental.getId())
                .userId(rental.getUserId())
                .rentalDate(rental.getRentalDate())
                .returnDate(rental.getReturnDate())
                .status(rental.getStatus())
                .totalAmount(rental.getTotalAmount())
                .details(detailDTOs)
                .build();
    }

    private RentalDetailResponseDTO toDetailResponseDTO(RentalDetail detail) {
        if (detail == null) return null;

        return RentalDetailResponseDTO.builder()
                .movieId(detail.getMovieId())
                .quantity(detail.getQuantity())
                .priceAtMoment(detail.getPriceAtMoment())
                .subtotal(detail.getPriceAtMoment().multiply(java.math.BigDecimal.valueOf(detail.getQuantity())))
                .build();
    }
}