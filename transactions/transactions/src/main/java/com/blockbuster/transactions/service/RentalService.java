package com.blockbuster.transactions.service;

import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;

import java.util.List;

public interface RentalService {

    // POST
    RentalResponseDTO createRental(RentalRequestDTO request);

    // GET  Obtener todos los arriendos de un usuario específico
    List<RentalResponseDTO> getRentalsByUser(Long userId);

    // GET Buscar un arriendo por su ID
    RentalResponseDTO getRentalById(Long id);

    // PATCH  Marcar un arriendo como devuelto
    RentalResponseDTO returnRental(Long rentalId);

    // GET ALL
    List<RentalResponseDTO> getAllRentals();

    // DEL
    void deleteRental(Long id);
}
