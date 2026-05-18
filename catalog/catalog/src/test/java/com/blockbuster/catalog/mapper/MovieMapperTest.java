package com.blockbuster.catalog.mapper;

import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.model.entity.Movie;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovieMapperTest {

    private final MovieMapper mapper = new MovieMapper();

    @Test
    void shouldMapRequestDtoToEntity() {
        Category category = Category.builder()
                .id(3L)
                .name("Sci-Fi")
                .build();

        MovieRequestDTO requestDTO = new MovieRequestDTO();
        requestDTO.setTitle("Interstellar");
        requestDTO.setCategoryId(3L);
        requestDTO.setReleaseYear(2014);
        requestDTO.setStock(7);
        requestDTO.setAvailable(true);

        Movie movie = mapper.toEntity(requestDTO, category);

        assertThat(movie).isNotNull();
        assertThat(movie.getTitle()).isEqualTo("Interstellar");
        assertThat(movie.getCategory()).isEqualTo(category);
        assertThat(movie.getReleaseYear()).isEqualTo(2014);
        assertThat(movie.getStock()).isEqualTo(7);
        assertThat(movie.getAvailable()).isTrue();
    }

    @Test
    void shouldMapEntityToResponseDto() {
        Category category = Category.builder()
                .id(1L)
                .name("Action")
                .build();

        Movie movie = Movie.builder()
                .id(9L)
                .title("John Wick")
                .category(category)
                .releaseYear(2014)
                .stock(4)
                .available(true)
                .build();

        MovieResponseDTO responseDTO = mapper.toResponseDTO(movie);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(9L);
        assertThat(responseDTO.getTitle()).isEqualTo("John Wick");
        assertThat(responseDTO.getCategoryId()).isEqualTo(1L);
        assertThat(responseDTO.getCategoryName()).isEqualTo("Action");
        assertThat(responseDTO.getReleaseYear()).isEqualTo(2014);
        assertThat(responseDTO.getStock()).isEqualTo(4);
        assertThat(responseDTO.getAvailable()).isTrue();
    }

    @Test
    void shouldReturnNullWhenRequestDtoIsNull() {
        assertThat(mapper.toEntity(null, null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }
}
