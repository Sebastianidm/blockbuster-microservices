package com.blockbuster.transactions.controller;

import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    // POST: http://localhost:8083/api/v1/rentals
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO request) {
        RentalResponseDTO createdRental = rentalService.createRental(request);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

    // GET: http://localhost:8083/api/v1/rentals/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByUser(@PathVariable Long userId) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByUser(userId);
        return ResponseEntity.ok(rentals);
    }

    // PUT: http://localhost:8083/api/v1/rentals/1/return
    @PutMapping("/{id}/return")
    public ResponseEntity<RentalResponseDTO> returnRental(@PathVariable Long id) {
        RentalResponseDTO returnedRental = rentalService.returnRental(id);
        return ResponseEntity.ok(returnedRental);
    }
}