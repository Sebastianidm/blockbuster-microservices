package com.blockbuster.transactions.service;

import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;

import java.util.List;

public interface RentalService {

    // POST
    RentalResponseDTO createRental(RentalRequestDTO request);

    // Obtener todos los arriendos de un usuario específico
    List<RentalResponseDTO> getRentalsByUser(Long userId);

    // Buscar un arriendo por su ID
    RentalResponseDTO getRentalById(Long id);

    // Marcar un arriendo como devuelto
    RentalResponseDTO returnRental(Long rentalId);
}