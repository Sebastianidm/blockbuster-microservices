package com.blockbuster.catalog.repository;

import com.blockbuster.catalog.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByCategoryId(Long categoryId);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByAvailableTrue();
}
