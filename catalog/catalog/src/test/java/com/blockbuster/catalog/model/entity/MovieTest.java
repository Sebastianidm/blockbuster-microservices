package com.blockbuster.catalog.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieTest {

    @Test
    void shouldSetDefaultStockAndAvailabilityOnCreate() {
        Movie movie = Movie.builder()
                .title("Coraline")
                .releaseYear(2009)
                .build();

        movie.onCreate();

        assertEquals(0, movie.getStock());
        assertTrue(movie.getAvailable());
    }

    @Test
    void shouldPreserveExistingStockAndAvailabilityOnCreate() {
        Movie movie = Movie.builder()
                .title("Home Alone")
                .releaseYear(1990)
                .stock(2)
                .available(false)
                .build();

        movie.onCreate();

        assertEquals(2, movie.getStock());
        assertFalse(movie.getAvailable());
    }
}
