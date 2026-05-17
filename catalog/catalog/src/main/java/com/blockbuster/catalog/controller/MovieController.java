package com.blockbuster.catalog.controller;

import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.service.MovieService;
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
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(request));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(movieService.getMoviesByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDTO>> searchMoviesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMoviesByTitle(title));
    }

    @GetMapping("/available")
    public ResponseEntity<List<MovieResponseDTO>> getAvailableMovies() {
        return ResponseEntity.ok(movieService.getAvailableMovies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id,
                                                        @Valid @RequestBody MovieRequestDTO request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @PatchMapping("/{id}/stock/discount")
    public ResponseEntity<MovieResponseDTO> checkAndDiscountStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(movieService.checkAndDiscountStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
