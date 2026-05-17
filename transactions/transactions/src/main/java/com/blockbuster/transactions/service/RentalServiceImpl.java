package com.blockbuster.transactions.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blockbuster.transactions.client.CatalogClient;
import com.blockbuster.transactions.client.NotificationsClient;
import com.blockbuster.transactions.client.UsersClient;
import com.blockbuster.transactions.client.dto.NotificationClientRequest;
import com.blockbuster.transactions.client.dto.UserClientResponse;
import com.blockbuster.transactions.exception.TransactionException;
import com.blockbuster.transactions.mapper.RentalMapper;
import com.blockbuster.transactions.model.dto.RentalDetailRequestDTO;
import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.model.entity.Rental;
import com.blockbuster.transactions.model.entity.RentalDetail;
import com.blockbuster.transactions.repository.RentalRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UsersClient usersClient;
    private final CatalogClient catalogClient;
    private final NotificationsClient notificationsClient;

    private static final BigDecimal DAILY_RENTAL_PRICE = new BigDecimal("2500.00");
    private static final int RENTAL_DAYS_ALLOWED = 3;

    @Override
    @Transactional
    public RentalResponseDTO createRental(RentalRequestDTO request) {
        log.info("Iniciando creacion de arriendo para el usuario ID: {}", request.getUserId());

        UserClientResponse user = validateUserExists(request.getUserId());
        enforceRentalOwnershipForAuthenticatedUser(user);

        Rental rental = Rental.builder()
                .userId(request.getUserId())
                .rentalDate(LocalDateTime.now())
                .returnDate(LocalDateTime.now().plusDays(RENTAL_DAYS_ALLOWED))
                .status("ACTIVE")
                .totalAmount(BigDecimal.ZERO)
                .details(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (RentalDetailRequestDTO movieRequest : request.getMovies()) {
            validateAndDiscountMovieStock(movieRequest.getMovieId(), movieRequest.getQuantity());

            RentalDetail detail = RentalDetail.builder()
                    .rental(rental)
                    .movieId(movieRequest.getMovieId())
                    .quantity(movieRequest.getQuantity())
                    .priceAtMoment(DAILY_RENTAL_PRICE)
                    .build();

            rental.getDetails().add(detail);

            BigDecimal subtotal = DAILY_RENTAL_PRICE.multiply(BigDecimal.valueOf(movieRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        rental.setTotalAmount(totalAmount);

        Rental savedRental = rentalRepository.save(rental);
        sendRentalConfirmation(savedRental, user);

        log.info("Arriendo ID {} creado exitosamente con un total de ${}", savedRental.getId(), savedRental.getTotalAmount());

        return rentalMapper.toResponseDTO(savedRental);
    }

    @Override
    public List<RentalResponseDTO> getRentalsByUser(Long userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(rentalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RentalResponseDTO getRentalById(Long id) {
        return rentalMapper.toResponseDTO(getRentalEntityById(id));
    }

    @Override
    @Transactional
    public RentalResponseDTO returnRental(Long rentalId) {
        Rental rental = getRentalEntityById(rentalId);
        rental.setStatus("RETURNED");
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDTO(updatedRental);
    }

    @Override
    public List<RentalResponseDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(rentalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRental(Long id) {
        Rental rental = getRentalEntityById(id);
        rentalRepository.delete(rental);
        log.info("Arriendo ID {} eliminado de la base de datos", id);
    }

    private Rental getRentalEntityById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND,
                        "No existe un arriendo con ID: " + id));
    }

    private UserClientResponse validateUserExists(Long userId) {
        try {
            return usersClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            throw new TransactionException(HttpStatus.NOT_FOUND, "No existe un usuario con ID: " + userId);
        } catch (FeignException ex) {
            throw new TransactionException(HttpStatus.BAD_GATEWAY, "No fue posible validar el usuario en ms-users");
        }
    }

    private void validateAndDiscountMovieStock(Long movieId, Integer quantity) {
        try {
            catalogClient.checkAndDiscountStock(movieId, quantity);
        } catch (FeignException.NotFound ex) {
            throw new TransactionException(HttpStatus.NOT_FOUND, "No existe una pelicula con ID: " + movieId);
        } catch (FeignException.BadRequest | FeignException.Conflict ex) {
            throw new TransactionException(HttpStatus.CONFLICT,
                    "No hay stock suficiente o la pelicula no esta disponible para el ID: " + movieId);
        } catch (FeignException ex) {
            throw new TransactionException(HttpStatus.BAD_GATEWAY, "No fue posible validar el stock en ms-catalog");
        }
    }

    private void enforceRentalOwnershipForAuthenticatedUser(UserClientResponse user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            return;
        }

        boolean isPlainUser = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority()));

        if (!isPlainUser) {
            return;
        }

        String authenticatedUsername = authentication.getName();
        if (authenticatedUsername != null && authenticatedUsername.equals(user.getUsername())) {
            return;
        }

        throw new TransactionException(HttpStatus.FORBIDDEN,
                "Un usuario cliente solo puede crear arriendos para su propia cuenta");
    }

    private void sendRentalConfirmation(Rental rental, UserClientResponse user) {
        NotificationClientRequest notificationRequest = NotificationClientRequest.builder()
                .userId(user.getId())
                .recipientEmail(user.getEmail())
                .subject("Confirmacion de arriendo Blockbuster")
                .message(buildRentalMessage(rental))
                .type("RENTAL_CONFIRMATION")
                .build();

        try {
            notificationsClient.sendNotification(notificationRequest);
        } catch (RuntimeException ex) {
            log.warn("No se pudo enviar la confirmacion del arriendo {} al usuario {}", rental.getId(), user.getId(), ex);
        }
    }

    private String buildRentalMessage(Rental rental) {
        int totalMovies = rental.getDetails() == null ? 0 : rental.getDetails().stream()
                .mapToInt(RentalDetail::getQuantity)
                .sum();

        return "Tu arriendo #" + rental.getId()
                + " fue creado con exito. Total peliculas: " + totalMovies
                + ". Monto total: $" + rental.getTotalAmount() + ".";
    }
}
