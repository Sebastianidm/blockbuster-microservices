package com.blockbuster.catalog.controller;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.exception.GlobalExceptionHandler;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.security.JwtAuthenticationFilter;
import com.blockbuster.catalog.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateMovieSuccessfully() throws Exception {
        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(10L)
                .title("Inception")
                .categoryId(3L)
                .categoryName("Sci-Fi")
                .releaseYear(2010)
                .stock(6)
                .available(true)
                .build();

        when(movieService.createMovie(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType("application/json")
                        .content("{\"title\":\"Inception\",\"categoryId\":3,\"releaseYear\":2010,\"stock\":6,\"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void shouldReturnValidationErrorWhenMovieTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"categoryId\":3,\"releaseYear\":2010,\"stock\":6,\"available\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El titulo de la pelicula es obligatorio"))
                .andExpect(jsonPath("$.path").value("/api/v1/movies"));
    }

    @Test
    void shouldGetAllMoviesSuccessfully() throws Exception {
        when(movieService.getAllMovies()).thenReturn(List.of(
                MovieResponseDTO.builder().id(1L).title("The Matrix").build(),
                MovieResponseDTO.builder().id(2L).title("Interstellar").build()
        ));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Matrix"))
                .andExpect(jsonPath("$[1].title").value("Interstellar"));
    }

    @Test
    void shouldGetMovieByIdSuccessfully() throws Exception {
        when(movieService.getMovieById(9L)).thenReturn(MovieResponseDTO.builder()
                .id(9L)
                .title("The Pursuit of Happyness")
                .categoryId(4L)
                .categoryName("Drama")
                .releaseYear(2006)
                .stock(1)
                .available(true)
                .build());

        mockMvc.perform(get("/api/v1/movies/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.title").value("The Pursuit of Happyness"));
    }

    @Test
    void shouldGetMoviesByCategorySuccessfully() throws Exception {
        when(movieService.getMoviesByCategory(3L)).thenReturn(List.of(
                MovieResponseDTO.builder().id(1L).title("The Matrix").categoryId(3L).build(),
                MovieResponseDTO.builder().id(3L).title("Interstellar").categoryId(3L).build()
        ));

        mockMvc.perform(get("/api/v1/movies/category/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(3))
                .andExpect(jsonPath("$[1].title").value("Interstellar"));
    }

    @Test
    void shouldSearchMoviesByTitleSuccessfully() throws Exception {
        when(movieService.searchMoviesByTitle("Matrix")).thenReturn(List.of(
                MovieResponseDTO.builder().id(1L).title("The Matrix").build()
        ));

        mockMvc.perform(get("/api/v1/movies/search").param("title", "Matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Matrix"));
    }

    @Test
    void shouldGetAvailableMoviesSuccessfully() throws Exception {
        when(movieService.getAvailableMovies()).thenReturn(List.of(
                MovieResponseDTO.builder().id(1L).title("The Matrix").categoryId(3L).categoryName("Sci-Fi").releaseYear(1999).stock(4).available(true).build()
        ));

        mockMvc.perform(get("/api/v1/movies/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Matrix"));
    }

    @Test
    void shouldUpdateMovieSuccessfully() throws Exception {
        when(movieService.updateMovie(eq(5L), any())).thenReturn(MovieResponseDTO.builder()
                .id(5L)
                .title("Rush Hour")
                .categoryId(1L)
                .categoryName("Action")
                .releaseYear(1998)
                .stock(4)
                .available(true)
                .build());

        mockMvc.perform(put("/api/v1/movies/5")
                        .contentType("application/json")
                        .content("{\"title\":\"Rush Hour\",\"categoryId\":1,\"releaseYear\":1998,\"stock\":4,\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Rush Hour"));
    }

    @Test
    void shouldDiscountMovieStockSuccessfully() throws Exception {
        when(movieService.checkAndDiscountStock(7L, 2)).thenReturn(MovieResponseDTO.builder()
                .id(7L)
                .title("Mad Max")
                .categoryId(1L)
                .categoryName("Action")
                .releaseYear(2015)
                .stock(3)
                .available(true)
                .build());

        mockMvc.perform(patch("/api/v1/movies/7/stock/discount")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(3))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldRestoreMovieStockSuccessfully() throws Exception {
        when(movieService.restoreMovieStock(7L, 2)).thenReturn(MovieResponseDTO.builder()
                .id(7L)
                .title("Mad Max")
                .categoryId(1L)
                .categoryName("Action")
                .releaseYear(2015)
                .stock(5)
                .available(true)
                .build());

        mockMvc.perform(patch("/api/v1/movies/7/stock/restore")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(5))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturnConflictWhenMovieStockIsInsufficient() throws Exception {
        when(movieService.checkAndDiscountStock(7L, 10))
                .thenThrow(new CatalogException("Stock insuficiente para la pelicula con ID: 7", HttpStatus.CONFLICT));

        mockMvc.perform(patch("/api/v1/movies/7/stock/discount")
                        .param("quantity", "10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Stock insuficiente para la pelicula con ID: 7"));
    }

    @Test
    void shouldDeleteMovieSuccessfully() throws Exception {
        doNothing().when(movieService).deleteMovie(eq(5L));

        mockMvc.perform(delete("/api/v1/movies/5"))
                .andExpect(status().isNoContent());
    }
}
