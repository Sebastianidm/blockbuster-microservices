package com.blockbuster.transactions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.blockbuster.transactions.client.CatalogClient;
import com.blockbuster.transactions.client.NotificationsClient;
import com.blockbuster.transactions.client.UsersClient;
import com.blockbuster.transactions.client.dto.MovieClientResponse;
import com.blockbuster.transactions.client.dto.NotificationClientRequest;
import com.blockbuster.transactions.client.dto.RoleClientResponse;
import com.blockbuster.transactions.client.dto.UserClientResponse;
import com.blockbuster.transactions.exception.TransactionException;
import com.blockbuster.transactions.mapper.RentalMapper;
import com.blockbuster.transactions.model.dto.RentalDetailRequestDTO;
import com.blockbuster.transactions.model.dto.RentalDetailResponseDTO;
import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.model.entity.Rental;
import com.blockbuster.transactions.repository.RentalDetailRepository;
import com.blockbuster.transactions.repository.RentalRepository;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalDetailRepository rentalDetailRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private UsersClient usersClient;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private NotificationsClient notificationsClient;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @org.junit.jupiter.api.AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateRentalValidatingUserAndDiscountingStock() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);

        RentalDetailRequestDTO detailRequest = new RentalDetailRequestDTO();
        detailRequest.setMovieId(42L);
        detailRequest.setQuantity(2);
        request.setMovies(List.of(detailRequest));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 2)).thenReturn(movieResponse(42L));

        Rental savedRental = Rental.builder()
                .id(100L)
                .userId(25L)
                .rentalDate(LocalDateTime.of(2026, 5, 17, 3, 0))
                .returnDate(LocalDateTime.of(2026, 5, 20, 3, 0))
                .status("ACTIVE")
                .totalAmount(new BigDecimal("5000.00"))
                .build();
        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(100L)
                .userId(25L)
                .totalAmount(new BigDecimal("5000.00"))
                .details(List.of(RentalDetailResponseDTO.builder()
                        .movieId(42L)
                        .quantity(2)
                        .priceAtMoment(new BigDecimal("2500.00"))
                        .subtotal(new BigDecimal("5000.00"))
                        .build()))
                .build();

        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(rentalMapper.toResponseDTO(savedRental)).thenReturn(response);

        RentalResponseDTO result = rentalService.createRental(request);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());
        verify(usersClient).getUserById(25L);
        verify(catalogClient).checkAndDiscountStock(42L, 2);
        verify(notificationsClient).sendNotification(argThat(notification ->
                notification.getUserId().equals(25L)
                        && notification.getRecipientEmail().equals("martin@blockbuster.com")
                        && notification.getType().equals("RENTAL_CONFIRMATION")));

        Rental captured = rentalCaptor.getValue();
        assertEquals(25L, captured.getUserId());
        assertEquals(new BigDecimal("5000.00"), captured.getTotalAmount());
        assertEquals(1, captured.getDetails().size());
        assertEquals(response, result);
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(99L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(99L)).thenThrow(notFoundException("user missing"));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(rentalRepository, never()).save(any(Rental.class));
        verify(catalogClient, never()).checkAndDiscountStock(any(Long.class), any(Integer.class));
    }

    @Test
    void shouldFailWhenCatalogRejectsStockDiscount() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 1)).thenThrow(conflictException("stock conflict"));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldCreateRentalEvenWhenNotificationFails() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 1)).thenReturn(movieResponse(42L));
        doThrow(new RuntimeException("notifications down")).when(notificationsClient).sendNotification(any(NotificationClientRequest.class));

        Rental savedRental = Rental.builder()
                .id(101L)
                .userId(25L)
                .rentalDate(LocalDateTime.of(2026, 5, 17, 3, 0))
                .returnDate(LocalDateTime.of(2026, 5, 20, 3, 0))
                .status("ACTIVE")
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();
        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(101L)
                .userId(25L)
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();

        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(rentalMapper.toResponseDTO(savedRental)).thenReturn(response);

        RentalResponseDTO result = assertDoesNotThrow(() -> rentalService.createRental(request));

        assertEquals(response, result);
    }

    @Test
    void shouldRejectWhenUserTriesToCreateRentalForDifferentAccount() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(99L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(99L)).thenReturn(userResponse(99L, "other-user", "other@blockbuster.com"));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(catalogClient, never()).checkAndDiscountStock(any(Long.class), any(Integer.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldAllowAdminToCreateRentalForAnotherUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, List.of(() -> "ROLE_ADMIN")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 1)).thenReturn(movieResponse(42L));

        Rental savedRental = Rental.builder()
                .id(102L)
                .userId(25L)
                .rentalDate(LocalDateTime.of(2026, 5, 17, 3, 0))
                .returnDate(LocalDateTime.of(2026, 5, 20, 3, 0))
                .status("ACTIVE")
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();
        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(102L)
                .userId(25L)
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();

        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(rentalMapper.toResponseDTO(savedRental)).thenReturn(response);

        RentalResponseDTO result = assertDoesNotThrow(() -> rentalService.createRental(request));

        assertEquals(response, result);
    }

    private RentalDetailRequestDTO movieRequest(Long movieId, Integer quantity) {
        RentalDetailRequestDTO detailRequest = new RentalDetailRequestDTO();
        detailRequest.setMovieId(movieId);
        detailRequest.setQuantity(quantity);
        return detailRequest;
    }

    private UserClientResponse userResponse(Long userId) {
        return userResponse(userId, "martin", "martin@blockbuster.com");
    }

    private UserClientResponse userResponse(Long userId, String username, String email) {
        UserClientResponse response = new UserClientResponse();
        response.setId(userId);
        response.setUsername(username);
        response.setEmail(email);
        RoleClientResponse role = new RoleClientResponse();
        role.setId(1L);
        role.setName("ROLE_USER");
        response.setRole(role);
        return response;
    }

    private MovieClientResponse movieResponse(Long movieId) {
        MovieClientResponse response = new MovieClientResponse();
        response.setId(movieId);
        response.setTitle("Matrix");
        response.setStock(4);
        response.setAvailable(true);
        return response;
    }

    private FeignException.NotFound notFoundException(String message) {
        Request request = Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, new RequestTemplate());
        return new FeignException.NotFound(message, request, null, Map.of());
    }

    private FeignException.Conflict conflictException(String message) {
        Request request = Request.create(Request.HttpMethod.PATCH, "/test", Map.of(), null, new RequestTemplate());
        return new FeignException.Conflict(message, request, null, Map.of());
    }
}
