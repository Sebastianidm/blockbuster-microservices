package com.blockbuster.catalog.service;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.mapper.MovieMapper;
import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.model.entity.Movie;
import com.blockbuster.catalog.repository.CategoryRepository;
import com.blockbuster.catalog.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void shouldCreateMovieSuccessfully() {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Inception");
        request.setCategoryId(3L);
        request.setReleaseYear(2010);
        request.setStock(6);
        request.setAvailable(true);

        Category category = Category.builder()
                .id(3L)
                .name("Sci-Fi")
                .build();

        Movie movie = Movie.builder()
                .title("Inception")
                .category(category)
                .releaseYear(2010)
                .stock(6)
                .available(true)
                .build();

        Movie savedMovie = Movie.builder()
                .id(11L)
                .title("Inception")
                .category(category)
                .releaseYear(2010)
                .stock(6)
                .available(true)
                .build();

        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(11L)
                .title("Inception")
                .categoryId(3L)
                .categoryName("Sci-Fi")
                .releaseYear(2010)
                .stock(6)
                .available(true)
                .build();

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(movieMapper.toEntity(request, category)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(savedMovie);
        when(movieMapper.toResponseDTO(savedMovie)).thenReturn(response);

        MovieResponseDTO result = movieService.createMovie(request);

        assertThat(result.getId()).isEqualTo(11L);
        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    void shouldDiscountStockAndKeepMovieAvailable() {
        Category category = Category.builder()
                .id(1L)
                .name("Action")
                .build();

        Movie movie = Movie.builder()
                .id(7L)
                .title("Mad Max")
                .category(category)
                .releaseYear(2015)
                .stock(5)
                .available(true)
                .build();

        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(7L)
                .title("Mad Max")
                .categoryId(1L)
                .categoryName("Action")
                .releaseYear(2015)
                .stock(3)
                .available(true)
                .build();

        when(movieRepository.findById(7L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)).thenReturn(response);

        MovieResponseDTO result = movieService.checkAndDiscountStock(7L, 2);

        assertThat(result.getStock()).isEqualTo(3);
        assertThat(result.getAvailable()).isTrue();
        assertThat(movie.getStock()).isEqualTo(3);
        assertThat(movie.getAvailable()).isTrue();
    }

    @Test
    void shouldDiscountStockAndMarkMovieAsUnavailableWhenStockReachesZero() {
        Category category = Category.builder()
                .id(2L)
                .name("Comedy")
                .build();

        Movie movie = Movie.builder()
                .id(8L)
                .title("The Mask")
                .category(category)
                .releaseYear(1994)
                .stock(1)
                .available(true)
                .build();

        when(movieRepository.findById(8L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)).thenReturn(MovieResponseDTO.builder()
                .id(8L)
                .title("The Mask")
                .categoryId(2L)
                .categoryName("Comedy")
                .releaseYear(1994)
                .stock(0)
                .available(false)
                .build());

        MovieResponseDTO result = movieService.checkAndDiscountStock(8L, 1);

        assertThat(result.getStock()).isZero();
        assertThat(result.getAvailable()).isFalse();
        assertThat(movie.getStock()).isZero();
        assertThat(movie.getAvailable()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        assertThatThrownBy(() -> movieService.checkAndDiscountStock(5L, 0))
                .isInstanceOf(CatalogException.class)
                .hasMessage("La cantidad a descontar debe ser mayor a cero");
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.checkAndDiscountStock(99L, 1))
                .isInstanceOf(CatalogException.class)
                .hasMessage("Película no encontrada con ID: 99");
    }

    @Test
    void shouldThrowExceptionWhenMovieIsUnavailable() {
        Movie movie = Movie.builder()
                .id(6L)
                .stock(4)
                .available(false)
                .build();

        when(movieRepository.findById(6L)).thenReturn(Optional.of(movie));

        assertThatThrownBy(() -> movieService.checkAndDiscountStock(6L, 1))
                .isInstanceOf(CatalogException.class)
                .hasMessage("La película no está disponible para arriendo");
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        Movie movie = Movie.builder()
                .id(12L)
                .stock(2)
                .available(true)
                .build();

        when(movieRepository.findById(12L)).thenReturn(Optional.of(movie));

        assertThatThrownBy(() -> movieService.checkAndDiscountStock(12L, 3))
                .isInstanceOf(CatalogException.class)
                .hasMessage("Stock insuficiente para la película con ID: 12");
    }

    @Test
    void shouldPersistUpdatedMovieWhenDiscountingStock() {
        Category category = Category.builder()
                .id(4L)
                .name("Sci-Fi")
                .build();

        Movie movie = Movie.builder()
                .id(13L)
                .title("The Matrix")
                .category(category)
                .releaseYear(1999)
                .stock(4)
                .available(true)
                .build();

        when(movieRepository.findById(13L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)).thenReturn(MovieResponseDTO.builder()
                .id(13L)
                .title("The Matrix")
                .categoryId(4L)
                .categoryName("Sci-Fi")
                .releaseYear(1999)
                .stock(2)
                .available(true)
                .build());

        movieService.checkAndDiscountStock(13L, 2);

        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(movieCaptor.capture());

        assertThat(movieCaptor.getValue().getStock()).isEqualTo(2);
        assertThat(movieCaptor.getValue().getAvailable()).isTrue();
    }
}
