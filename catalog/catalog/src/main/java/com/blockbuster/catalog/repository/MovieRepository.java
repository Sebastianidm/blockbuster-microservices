package com.blockbuster.catalog.repository;

import com.blockbuster.catalog.model.entity.Movie;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    List<Movie> findAll();

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<Movie> findById(Long id);

    @EntityGraph(attributePaths = "category")
    List<Movie> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = "category")
    List<Movie> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(attributePaths = "category")
    List<Movie> findByAvailableTrue();
}
