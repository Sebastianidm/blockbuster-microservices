package com.blockbuster.transactions.controller;

import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    // POST: http://localhost:8083/api/v1/rentals
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO request) {
        RentalResponseDTO createdRental = rentalService.createRental(request);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

    // GET: http://localhost:8083/api/v1/rentals/user/1
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByUser(@PathVariable Long userId) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByUser(userId);
        return ResponseEntity.ok(rentals);
    }

    // GET: Obtener todos los arriendos.
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping
    public ResponseEntity<List<RentalResponseDTO>> getAll() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    // PUT: http://localhost:8083/api/v1/rentals/1/return
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/{id}/return")
    public ResponseEntity<RentalResponseDTO> returnRental(@PathVariable Long id) {
        RentalResponseDTO returnedRental = rentalService.returnRental(id);
        return ResponseEntity.ok(returnedRental);
    }

    // DEL: Eliminar arriendo por id ( solicitado en la rubrica )
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }
}
