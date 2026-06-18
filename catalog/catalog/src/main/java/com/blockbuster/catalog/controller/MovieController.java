package com.blockbuster.catalog.controller;

import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Validated
@Tag(name = "movie-controller", description = "Endpoints para la gestion del catalogo de peliculas")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Crear pelicula", description = "Registra una nueva pelicula en el catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pelicula creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(request));
    }

    @GetMapping
    @Operation(summary = "Listar peliculas", description = "Obtiene el listado completo de peliculas registradas.")
    @ApiResponse(responseCode = "200", description = "Listado recuperado exitosamente")
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pelicula por id", description = "Obtiene una pelicula especifica por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pelicula encontrada"),
            @ApiResponse(responseCode = "404", description = "Pelicula no encontrada")
    })
    public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Listar peliculas por categoria", description = "Obtiene las peliculas asociadas a una categoria especifica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado recuperado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(movieService.getMoviesByCategory(categoryId));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar peliculas por titulo", description = "Permite filtrar peliculas por coincidencia parcial de titulo.")
    @ApiResponse(responseCode = "200", description = "Busqueda ejecutada exitosamente")
    public ResponseEntity<List<MovieResponseDTO>> searchMoviesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMoviesByTitle(title));
    }

    @GetMapping("/available")
    @Operation(summary = "Listar peliculas disponibles", description = "Retorna solo las peliculas con stock disponible y habilitadas para arriendo.")
    @ApiResponse(responseCode = "200", description = "Listado recuperado exitosamente")
    public ResponseEntity<List<MovieResponseDTO>> getAvailableMovies() {
        return ResponseEntity.ok(movieService.getAvailableMovies());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pelicula", description = "Actualiza los datos principales de una pelicula existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pelicula actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Pelicula o categoria no encontrada")
    })
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id,
                                                        @Valid @RequestBody MovieRequestDTO request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @PatchMapping("/{id}/stock/discount")
    @Operation(summary = "Descontar stock", description = "Valida disponibilidad y descuenta stock para un arriendo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad invalida"),
            @ApiResponse(responseCode = "404", description = "Pelicula no encontrada"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    public ResponseEntity<MovieResponseDTO> checkAndDiscountStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(movieService.checkAndDiscountStock(id, quantity));
    }

    @PatchMapping("/{id}/stock/restore")
    @Operation(summary = "Restaurar stock", description = "Reintegra stock de una pelicula cuando un arriendo es devuelto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock restaurado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad invalida"),
            @ApiResponse(responseCode = "404", description = "Pelicula no encontrada")
    })
    public ResponseEntity<MovieResponseDTO> restoreStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(movieService.restoreMovieStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pelicula", description = "Elimina una pelicula del catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pelicula eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pelicula no encontrada")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
