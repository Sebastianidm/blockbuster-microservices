package com.blockbuster.catalog.mapper;

import com.blockbuster.catalog.model.dto.MovieRequestDTO;
import com.blockbuster.catalog.model.dto.MovieResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.model.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO dto, Category category) {
        if (dto == null) {
            return null;
        }

        return Movie.builder()
                .title(dto.getTitle())
                .category(category)
                .releaseYear(dto.getReleaseYear())
                .stock(dto.getStock())
                .available(dto.getAvailable())
                .build();
    }

    public MovieResponseDTO toResponseDTO(Movie movie) {
        if (movie == null) {
            return null;
        }

        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .categoryId(movie.getCategory().getId())
                .categoryName(movie.getCategory().getName())
                .releaseYear(movie.getReleaseYear())
                .stock(movie.getStock())
                .available(movie.getAvailable())
                .build();
    }
}
