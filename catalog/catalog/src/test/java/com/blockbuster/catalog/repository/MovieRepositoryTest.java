package com.blockbuster.catalog.repository;

import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.model.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindMoviesByCategoryId() {
        Category category = categoryRepository.save(Category.builder()
                .name("Horror")
                .description("Películas de terror")
                .build());

        movieRepository.save(Movie.builder()
                .title("Scream")
                .category(category)
                .releaseYear(1996)
                .stock(5)
                .available(true)
                .build());

        movieRepository.save(Movie.builder()
                .title("The Conjuring")
                .category(category)
                .releaseYear(2013)
                .stock(3)
                .available(true)
                .build());

        List<Movie> movies = movieRepository.findByCategoryId(category.getId());

        assertThat(movies).hasSize(2);
        assertThat(movies)
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Scream", "The Conjuring");
    }

    @Test
    void shouldFindMoviesByTitleContainingIgnoringCase() {
        Category category = categoryRepository.save(Category.builder()
                .name("Adventure")
                .description("Películas de aventura")
                .build());

        movieRepository.save(Movie.builder()
                .title("Indiana Jones")
                .category(category)
                .releaseYear(1981)
                .stock(6)
                .available(true)
                .build());

        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase("jones");

        assertThat(movies).isNotEmpty();
        assertThat(movies.getFirst().getTitle()).isEqualTo("Indiana Jones");
    }

    @Test
    void shouldFindOnlyAvailableMovies() {
        Category category = categoryRepository.save(Category.builder()
                .name("Fantasy")
                .description("Películas fantásticas")
                .build());

        movieRepository.save(Movie.builder()
                .title("Harry Potter")
                .category(category)
                .releaseYear(2001)
                .stock(8)
                .available(true)
                .build());

        movieRepository.save(Movie.builder()
                .title("Pan's Labyrinth")
                .category(category)
                .releaseYear(2006)
                .stock(0)
                .available(false)
                .build());

        List<Movie> availableMovies = movieRepository.findByAvailableTrue();

        assertThat(availableMovies)
                .extracting(Movie::getTitle)
                .contains("Harry Potter")
                .doesNotContain("Pan's Labyrinth");
    }
}
