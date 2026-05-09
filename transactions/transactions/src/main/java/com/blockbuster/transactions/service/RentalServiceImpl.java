package com.blockbuster.transactions.service;

import com.blockbuster.transactions.mapper.RentalMapper;
import com.blockbuster.transactions.model.dto.RentalDetailRequestDTO;
import com.blockbuster.transactions.model.dto.RentalRequestDTO;
import com.blockbuster.transactions.model.dto.RentalResponseDTO;
import com.blockbuster.transactions.model.entity.Rental;
import com.blockbuster.transactions.model.entity.RentalDetail;
import com.blockbuster.transactions.repository.RentalDetailRepository;
import com.blockbuster.transactions.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalDetailRepository rentalDetailRepository;
    private final RentalMapper rentalMapper;

    private static final BigDecimal DAILY_RENTAL_PRICE = new BigDecimal("2500.00");
    private static final int RENTAL_DAYS_ALLOWED = 3;

    @Override
    @Transactional // Si falla algo no se guarda nada en la bd ( esto es Rollback Martín :D )
    public RentalResponseDTO createRental(RentalRequestDTO request) {
        log.info("Iniciando creación de arriendo para el usuario ID: {}", request.getUserId());

        // Aquí llamaremos al microservicio users con feign para validar que el usuario existe

        // Crear la cabecera del arriendo
        Rental rental = Rental.builder()
                .userId(request.getUserId())
                .rentalDate(LocalDateTime.now())
                .returnDate(LocalDateTime.now().plusDays(RENTAL_DAYS_ALLOWED))
                .status("ACTIVE")
                .totalAmount(BigDecimal.ZERO)
                .details(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Procesar cada película solicitada
        for (RentalDetailRequestDTO movieRequest : request.getMovies()) {
            //  Aquí llamaremos al microservicio catalogo con feign para validar stock y obtener precio real
            RentalDetail detail = RentalDetail.builder()
                    .rental(rental)
                    .movieId(movieRequest.getMovieId())
                    .quantity(movieRequest.getQuantity())
                    .priceAtMoment(DAILY_RENTAL_PRICE)
                    .build();

            rental.getDetails().add(detail);

            // Calcular subtotal de esta película (precio * cantidad)
            BigDecimal subtotal = DAILY_RENTAL_PRICE.multiply(BigDecimal.valueOf(movieRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        // Actualizar el monto total en la cabecera
        rental.setTotalAmount(totalAmount);

        // Al guardar el Rental, gracias al CascadeType.ALL que pusimos en la entidad,
        // los RentalDetails se guardarán automáticamente también.
        Rental savedRental = rentalRepository.save(rental);

        log.info("Arriendo ID {} creado exitosamente con un total de ${}", savedRental.getId(), savedRental.getTotalAmount());

        // Usamos el mapper para no devolver la entidad cruda
        return rentalMapper.toResponseDTO(savedRental);
    }

    @Override
    public List<RentalResponseDTO> getRentalsByUser(Long userId) {
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        // Convertimos la lista de Entities a lista de DTOs
        return rentals.stream()
                .map(rentalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RentalResponseDTO getRentalById(Long id) {
        Rental rental = getRentalEntityById(id);
        return rentalMapper.toResponseDTO(rental);
    }

    @Override
    @Transactional
    public RentalResponseDTO returnRental(Long rentalId) {
        Rental rental = getRentalEntityById(rentalId);
        rental.setStatus("RETURNED");
        Rental updatedRental = rentalRepository.save(rental);

        return rentalMapper.toResponseDTO(updatedRental);
    }

    // Método privado auxiliar para buscar en la base de datos (retorna la Entidad para uso interno)
    private Rental getRentalEntityById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arriendo no encontrado con ID: " + id));
    }
}