package com.blockbuster.transactions.controller;

import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Crear nuevo arriendo", description = "Registra un nuevo arriendo y descuenta stock del catalogo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Arriendo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario o película no encontrada en el sistema central"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente en el catálogo")
    })
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO request) {
        RentalResponseDTO createdRental = rentalService.createRental(request);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

    // GET: http://localhost:8083/api/v1/rentals/user/1
    @Operation(summary = "Historial por usuario", description = "Obtiene todo el historial de arriendos de un usuario especifico.")
    @ApiResponse(responseCode = "200", description = "Historial recuperado exitosamente")
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByUser(@PathVariable Long userId) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByUser(userId);
        return ResponseEntity.ok(rentals);
    }

    // GET: Obtener todos los arriendos.
    @Operation(summary = "Listar todos los arriendos", description = "Obtiene el registro global de arriendos. Uso exclusivo de administración.")
    @ApiResponse(responseCode = "200", description = "Listado recuperado exitosamente")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping
    public ResponseEntity<List<RentalResponseDTO>> getAll() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    // PATCH: http://localhost:8083/api/v1/rentals/1/return
    @Operation(summary = "Devolver arriendo", description = "Marca un arriendo como devuelto y reintegra el stock de las películas al catálogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devolución procesada exitosamente"),
            @ApiResponse(responseCode = "404", description = "El ID del arriendo no existe"),
            @ApiResponse(responseCode = "409", description = "El arriendo ya había sido devuelto previamente")
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @RequestMapping(path = "/{id}/return", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<RentalResponseDTO> returnRental(@PathVariable Long id) {
        RentalResponseDTO returnedRental = rentalService.returnRental(id);
        return ResponseEntity.ok(returnedRental);
    }

    // DEL: Eliminar arriendo por id ( solicitado en la rubrica )
    @Operation(summary = "Eliminación física (CRUD)", description = "Elimina físicamente un arriendo de la base de datos por cumplimiento de rúbrica.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Arriendo eliminado sin contenido de respuesta"),
            @ApiResponse(responseCode = "404", description = "Arriendo no encontrado")
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }
}
