package com.blockbuster.catalog.service;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.mapper.MovieMapper;
import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.model.entity.Movie;
import com.blockbuster.catalog.repository.CategoryRepository;
import com.blockbuster.catalog.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponseDTO createMovie(MovieRequestDTO request) {
        String normalizedTitle = normalizeRequiredText(request.getTitle());
        log.info("Creando película con título: {}", normalizedTitle);

        Category category = getCategoryEntityById(request.getCategoryId());
        Movie movie = movieMapper.toEntity(request, category);
        movie.setTitle(normalizedTitle);
        movie.setAvailable(resolveAvailability(request.getAvailable(), request.getStock()));
        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(savedMovie);
    }

    @Override
    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    @Override
    public MovieResponseDTO getMovieById(Long id) {
        return movieMapper.toResponseDTO(getMovieEntityById(id));
    }

    @Override
    public List<MovieResponseDTO> getMoviesByCategory(Long categoryId) {
        return movieRepository.findByCategoryId(categoryId).stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<MovieResponseDTO> searchMoviesByTitle(String title) {
        String normalizedTitle = normalizeRequiredText(title);
        if (normalizedTitle == null || normalizedTitle.isBlank()) {
            throw new CatalogException("El texto de búsqueda de películas es obligatorio", HttpStatus.BAD_REQUEST);
        }

        return movieRepository.findByTitleContainingIgnoreCase(normalizedTitle).stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<MovieResponseDTO> getAvailableMovies() {
        return movieRepository.findByAvailableTrue().stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO request) {
        Movie movie = getMovieEntityById(id);
        Category category = getCategoryEntityById(request.getCategoryId());

        movie.setTitle(normalizeRequiredText(request.getTitle()));
        movie.setCategory(category);
        movie.setReleaseYear(request.getReleaseYear());
        movie.setStock(request.getStock());
        movie.setAvailable(resolveAvailability(request.getAvailable(), request.getStock()));

        return movieMapper.toResponseDTO(movieRepository.save(movie));
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = getMovieEntityById(id);
        movieRepository.delete(movie);
    }

    @Override
    @Transactional
    public MovieResponseDTO checkAndDiscountStock(Long movieId, int quantity) {
        log.info("Validando y descontando stock para la película ID: {} con cantidad: {}", movieId, quantity);

        if (quantity <= 0) {
            throw new CatalogException("La cantidad a descontar debe ser mayor a cero", HttpStatus.BAD_REQUEST);
        }

        Movie movie = getMovieEntityById(movieId);

        if (!Boolean.TRUE.equals(movie.getAvailable())) {
            throw new CatalogException("La película no está disponible para arriendo", HttpStatus.CONFLICT);
        }

        if (movie.getStock() < quantity) {
            throw new CatalogException("Stock insuficiente para la película con ID: " + movieId, HttpStatus.CONFLICT);
        }

        int updatedStock = movie.getStock() - quantity;
        movie.setStock(updatedStock);
        movie.setAvailable(updatedStock > 0);

        Movie updatedMovie = movieRepository.save(movie);
        log.info("Stock actualizado para la película ID {}. Nuevo stock: {}", movieId, updatedStock);

        return movieMapper.toResponseDTO(updatedMovie);
    }

    private Movie getMovieEntityById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Película no encontrada con ID: " + id, HttpStatus.NOT_FOUND));
    }

    private Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Categoría no encontrada con ID: " + id, HttpStatus.NOT_FOUND));
    }

    private String normalizeRequiredText(String value) {
        return value == null ? null : value.trim();
    }

    private boolean resolveAvailability(Boolean requestedAvailability, Integer stock) {
        if (stock == null || stock <= 0) {
            return false;
        }

        return requestedAvailability == null || requestedAvailability;
    }
}
