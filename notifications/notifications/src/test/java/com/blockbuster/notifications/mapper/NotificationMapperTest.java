package com.blockbuster.notifications.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.model.entity.Notification;

class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    @Test
    void shouldMapRequestDtoToEntityWithPendingStatusAndTimestamp() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(10L);
        request.setRecipientEmail("martin@duocuc.cl");
        request.setSubject("Confirmacion");
        request.setMessage("Tu arriendo fue creado");
        request.setType("RENTAL_CONFIRMATION");

        Notification entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getRecipientEmail()).isEqualTo("martin@duocuc.cl");
        assertThat(entity.getSubject()).isEqualTo("Confirmacion");
        assertThat(entity.getMessage()).isEqualTo("Tu arriendo fue creado");
        assertThat(entity.getType()).isEqualTo("RENTAL_CONFIRMATION");
        assertThat(entity.getStatus()).isEqualTo("PENDING");
        assertThat(entity.getTimestamp()).isNotNull();
    }

    @Test
    void shouldReturnNullEntityWhenRequestIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseDto() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 6, 17, 20, 30);
        Notification entity = Notification.builder()
                .id("abc123")
                .recipientEmail("martin@duocuc.cl")
                .subject("Confirmacion")
                .status("SENT")
                .timestamp(timestamp)
                .build();

        NotificationResponseDTO response = mapper.toResponseDTO(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("abc123");
        assertThat(response.getRecipientEmail()).isEqualTo("martin@duocuc.cl");
        assertThat(response.getSubject()).isEqualTo("Confirmacion");
        assertThat(response.getStatus()).isEqualTo("SENT");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }
}
