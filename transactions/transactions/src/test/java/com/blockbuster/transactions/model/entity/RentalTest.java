package com.blockbuster.transactions.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class RentalTest {

    @Test
    void shouldAssignDefaultValuesOnPrePersist() {
        Rental rental = new Rental();

        rental.onCrete();

        assertNotNull(rental.getRentalDate());
        assertEquals("ACTIVE", rental.getStatus());
    }

    @Test
    void shouldPreserveExistingValuesOnPrePersist() {
        LocalDateTime rentalDate = LocalDateTime.of(2026, 6, 17, 12, 0);
        Rental rental = Rental.builder()
                .rentalDate(rentalDate)
                .status("RETURNED")
                .build();

        rental.onCrete();

        assertEquals(rentalDate, rental.getRentalDate());
        assertEquals("RETURNED", rental.getStatus());
    }
}
