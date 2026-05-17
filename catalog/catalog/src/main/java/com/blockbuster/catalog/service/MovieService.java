package com.blockbuster.catalog.service;

import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {

    MovieResponseDTO createMovie(MovieRequestDTO request);

    List<MovieResponseDTO> getAllMovies();

    MovieResponseDTO getMovieById(Long id);

    List<MovieResponseDTO> getMoviesByCategory(Long categoryId);

    List<MovieResponseDTO> searchMoviesByTitle(String title);

    List<MovieResponseDTO> getAvailableMovies();

    MovieResponseDTO updateMovie(Long id, MovieRequestDTO request);

    void deleteMovie(Long id);

    MovieResponseDTO checkAndDiscountStock(Long movieId, int quantity);
}
