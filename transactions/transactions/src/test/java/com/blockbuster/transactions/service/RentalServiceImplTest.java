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
import java.util.Optional;

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
import com.blockbuster.transactions.model.entity.RentalDetail;
import com.blockbuster.transactions.repository.RentalDetailRepository;
import com.blockbuster.transactions.repository.RentalRepository;

import feign.FeignException;
import feign.Request;
import feign.Response;
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

    @Test
    void shouldReturnRentalRestoringStockAndSendingNotification() {
        Rental rental = Rental.builder()
                .id(200L)
                .userId(25L)
                .rentalDate(LocalDateTime.of(2026, 5, 17, 3, 0))
                .returnDate(LocalDateTime.of(2026, 5, 20, 3, 0))
                .status("ACTIVE")
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of(RentalDetail.builder()
                        .movieId(42L)
                        .quantity(1)
                        .priceAtMoment(new BigDecimal("2500.00"))
                        .build()))
                .build();

        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(200L)
                .userId(25L)
                .status("RETURNED")
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();

        when(rentalRepository.findById(200L)).thenReturn(java.util.Optional.of(rental));
        when(catalogClient.restoreStock(42L, 1)).thenReturn(movieResponse(42L));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(rentalMapper.toResponseDTO(rental)).thenReturn(response);

        RentalResponseDTO result = rentalService.returnRental(200L);

        assertEquals("RETURNED", rental.getStatus());
        assertEquals(response, result);
        verify(catalogClient).restoreStock(42L, 1);
        verify(notificationsClient).sendNotification(argThat(notification ->
                notification.getUserId().equals(25L)
                        && notification.getType().equals("RENTAL_RETURN")));
    }

    @Test
    void shouldRejectReturningAlreadyReturnedRental() {
        Rental rental = Rental.builder()
                .id(201L)
                .userId(25L)
                .status("RETURNED")
                .details(List.of())
                .build();

        when(rentalRepository.findById(201L)).thenReturn(java.util.Optional.of(rental));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.returnRental(201L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(catalogClient, never()).restoreStock(any(Long.class), any(Integer.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldGetRentalsByUserMappingRepositoryResults() {
        Rental rental = Rental.builder().id(301L).userId(25L).details(List.of()).build();
        RentalResponseDTO response = RentalResponseDTO.builder().id(301L).userId(25L).details(List.of()).build();

        when(rentalRepository.findByUserId(25L)).thenReturn(List.of(rental));
        when(rentalMapper.toResponseDTO(rental)).thenReturn(response);

        List<RentalResponseDTO> result = rentalService.getRentalsByUser(25L);

        assertEquals(List.of(response), result);
    }

    @Test
    void shouldGetRentalByIdWhenExists() {
        Rental rental = Rental.builder().id(302L).userId(25L).details(List.of()).build();
        RentalResponseDTO response = RentalResponseDTO.builder().id(302L).userId(25L).details(List.of()).build();

        when(rentalRepository.findById(302L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDTO(rental)).thenReturn(response);

        RentalResponseDTO result = rentalService.getRentalById(302L);

        assertEquals(response, result);
    }

    @Test
    void shouldThrowNotFoundWhenGettingMissingRentalById() {
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.getRentalById(999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No existe un arriendo con ID: 999", exception.getMessage());
    }

    @Test
    void shouldGetAllRentalsMappingRepositoryResults() {
        Rental rental = Rental.builder().id(303L).userId(25L).details(List.of()).build();
        RentalResponseDTO response = RentalResponseDTO.builder().id(303L).userId(25L).details(List.of()).build();

        when(rentalRepository.findAll()).thenReturn(List.of(rental));
        when(rentalMapper.toResponseDTO(rental)).thenReturn(response);

        List<RentalResponseDTO> result = rentalService.getAllRentals();

        assertEquals(List.of(response), result);
    }

    @Test
    void shouldDeleteRentalWhenExists() {
        Rental rental = Rental.builder().id(304L).userId(25L).details(List.of()).build();
        when(rentalRepository.findById(304L)).thenReturn(Optional.of(rental));

        rentalService.deleteRental(304L);

        verify(rentalRepository).delete(rental);
    }

    @Test
    void shouldFailWhenUsersServiceReturnsUnexpectedError() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenThrow(feignException(500, "users unavailable", Request.HttpMethod.GET));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("No fue posible validar el usuario en ms-users", exception.getMessage());
        verify(catalogClient, never()).checkAndDiscountStock(any(Long.class), any(Integer.class));
    }

    @Test
    void shouldFailWhenCatalogRejectsInternalAuthenticationDuringCreateRental() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 1))
                .thenThrow(new FeignException.Unauthorized("unauthorized", request(Request.HttpMethod.PATCH), null, Map.of()));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("La autenticacion interna contra ms-catalog fue rechazada. Verifica INTERNAL_API_KEY",
                exception.getMessage());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldFailWhenCatalogReturnsUnexpectedErrorDuringCreateRental() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("martin", null, List.of(() -> "ROLE_USER")));

        RentalRequestDTO request = new RentalRequestDTO();
        request.setUserId(25L);
        request.setMovies(List.of(movieRequest(42L, 1)));

        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(catalogClient.checkAndDiscountStock(42L, 1))
                .thenThrow(feignException(500, "catalog unavailable", Request.HttpMethod.PATCH));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.createRental(request));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("No fue posible validar el stock en ms-catalog", exception.getMessage());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldReturnRentalWithoutRestoringStockWhenDetailsAreEmpty() {
        Rental rental = Rental.builder()
                .id(305L)
                .userId(25L)
                .status("ACTIVE")
                .totalAmount(new BigDecimal("2500.00"))
                .details(List.of())
                .build();
        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(305L)
                .userId(25L)
                .status("RETURNED")
                .details(List.of())
                .build();

        when(rentalRepository.findById(305L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(usersClient.getUserById(25L)).thenReturn(userResponse(25L));
        when(rentalMapper.toResponseDTO(rental)).thenReturn(response);

        RentalResponseDTO result = rentalService.returnRental(305L);

        assertEquals("RETURNED", rental.getStatus());
        assertEquals(response, result);
        verify(catalogClient, never()).restoreStock(any(Long.class), any(Integer.class));
    }

    @Test
    void shouldFailWhenCatalogRejectsRestoreQuantity() {
        Rental rental = Rental.builder()
                .id(306L)
                .userId(25L)
                .status("ACTIVE")
                .details(List.of(RentalDetail.builder().movieId(42L).quantity(1).priceAtMoment(new BigDecimal("2500.00")).build()))
                .build();

        when(rentalRepository.findById(306L)).thenReturn(Optional.of(rental));
        when(catalogClient.restoreStock(42L, 1))
                .thenThrow(new FeignException.BadRequest("bad request", request(Request.HttpMethod.PATCH), null, Map.of()));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.returnRental(306L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("La cantidad a reintegrar es invalida para la pelicula con ID: 42", exception.getMessage());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldFailWhenCatalogRejectsInternalAuthenticationDuringRestore() {
        Rental rental = Rental.builder()
                .id(307L)
                .userId(25L)
                .status("ACTIVE")
                .details(List.of(RentalDetail.builder().movieId(42L).quantity(1).priceAtMoment(new BigDecimal("2500.00")).build()))
                .build();

        when(rentalRepository.findById(307L)).thenReturn(Optional.of(rental));
        when(catalogClient.restoreStock(42L, 1))
                .thenThrow(new FeignException.Forbidden("forbidden", request(Request.HttpMethod.PATCH), null, Map.of()));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.returnRental(307L));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("La autenticacion interna contra ms-catalog fue rechazada al reintegrar stock",
                exception.getMessage());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void shouldFailWhenCatalogReturnsUnexpectedErrorDuringRestore() {
        Rental rental = Rental.builder()
                .id(308L)
                .userId(25L)
                .status("ACTIVE")
                .details(List.of(RentalDetail.builder().movieId(42L).quantity(1).priceAtMoment(new BigDecimal("2500.00")).build()))
                .build();

        when(rentalRepository.findById(308L)).thenReturn(Optional.of(rental));
        when(catalogClient.restoreStock(42L, 1))
                .thenThrow(feignException(503, "catalog unavailable", Request.HttpMethod.PATCH));

        TransactionException exception = assertThrows(TransactionException.class, () -> rentalService.returnRental(308L));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("No fue posible reintegrar el stock en ms-catalog", exception.getMessage());
        verify(rentalRepository, never()).save(any(Rental.class));
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
        return new FeignException.NotFound(message, request(Request.HttpMethod.GET), null, Map.of());
    }

    private FeignException.Conflict conflictException(String message) {
        return new FeignException.Conflict(message, request(Request.HttpMethod.PATCH), null, Map.of());
    }

    private Request request(Request.HttpMethod method) {
        return Request.create(method, "/test", Map.of(), null, new RequestTemplate());
    }

    private FeignException feignException(int status, String message, Request.HttpMethod method) {
        Request request = request(method);
        Response response = Response.builder()
                .status(status)
                .reason(message)
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("test", response);
    }
}
