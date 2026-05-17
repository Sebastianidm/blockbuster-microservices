package com.blockbuster.transactions.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blockbuster.transactions.controller.RentalController;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.service.RentalService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(RentalController.class)
@Import({
        SecurityConfig.class,
        JwtUtils.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@TestPropertySource(properties = "jwt.secret=test-jwt-secret-with-at-least-32-characters")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RentalService rentalService;

    @Test
    void shouldRejectRentalCreationWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/rentals")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"userId\":25,\"movies\":[{\"movieId\":1,\"quantity\":1}]}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("No autorizado"));
    }

    @Test
    void shouldAllowRentalCreationForAuthenticatedUser() throws Exception {
        when(rentalService.createRental(any())).thenReturn(RentalResponseDTO.builder()
                .id(10L)
                .userId(25L)
                .rentalDate(LocalDateTime.now())
                .returnDate(LocalDateTime.now().plusDays(3))
                .status("ACTIVE")
                .totalAmount(BigDecimal.valueOf(5000))
                .details(List.of())
                .build());

        mockMvc.perform(post("/api/v1/rentals")
                        .with(user("martin").roles("USER"))
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"userId\":25,\"movies\":[{\"movieId\":1,\"quantity\":1}]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDenyUserRoleForAdminEmployeeEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/rentals/user/25")
                        .with(user("martin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acceso denegado"));
    }

    @Test
    void shouldAllowAdminRoleForRentalLookup() throws Exception {
        when(rentalService.getRentalsByUser(25L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rentals/user/25")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyUserRoleForGetAllRentals() throws Exception {
        mockMvc.perform(get("/api/v1/rentals")
                        .with(user("martin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acceso denegado"));
    }

    @Test
    void shouldAllowAdminRoleForGetAllRentals() throws Exception {
        when(rentalService.getAllRentals()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rentals")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowEmployeeToReturnRentalWithPatch() throws Exception {
        when(rentalService.returnRental(5L)).thenReturn(RentalResponseDTO.builder()
                .id(5L)
                .userId(25L)
                .rentalDate(LocalDateTime.now().minusDays(3))
                .returnDate(LocalDateTime.now())
                .status("RETURNED")
                .totalAmount(BigDecimal.valueOf(5000))
                .details(List.of())
                .build());

        mockMvc.perform(patch("/api/v1/rentals/5/return")
                        .with(user("employee").roles("EMPLOYEE"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowEmployeeToReturnRentalWithPutForBackwardCompatibility() throws Exception {
        when(rentalService.returnRental(5L)).thenReturn(RentalResponseDTO.builder()
                .id(5L)
                .userId(25L)
                .rentalDate(LocalDateTime.now().minusDays(3))
                .returnDate(LocalDateTime.now())
                .status("RETURNED")
                .totalAmount(BigDecimal.valueOf(5000))
                .details(List.of())
                .build());

        mockMvc.perform(put("/api/v1/rentals/5/return")
                        .with(user("employee").roles("EMPLOYEE"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToDeleteRental() throws Exception {
        mockMvc.perform(delete("/api/v1/rentals/5")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
