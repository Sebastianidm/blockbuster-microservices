package com.blockbuster.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blockbuster.notifications.mapper.NotificationMapper;
import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.model.entity.Notification;
import com.blockbuster.notifications.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void shouldPersistNotificationAndReturnResponse() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(7L);
        request.setRecipientEmail("martin@duocuc.cl");
        request.setSubject("Confirmacion");
        request.setMessage("Mensaje");
        request.setType("RENTAL_CONFIRMATION");

        Notification mappedEntity = Notification.builder()
                .userId(7L)
                .recipientEmail("martin@duocuc.cl")
                .subject("Confirmacion")
                .message("Mensaje")
                .type("RENTAL_CONFIRMATION")
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .build();

        Notification savedEntity = Notification.builder()
                .id("ntf-1")
                .userId(7L)
                .recipientEmail("martin@duocuc.cl")
                .subject("Confirmacion")
                .message("Mensaje")
                .type("RENTAL_CONFIRMATION")
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .build();

        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .id("ntf-1")
                .recipientEmail("martin@duocuc.cl")
                .subject("Confirmacion")
                .status("SENT")
                .timestamp(savedEntity.getTimestamp())
                .build();

        when(mapper.toEntity(request)).thenReturn(mappedEntity);
        when(repository.save(any(Notification.class))).thenReturn(savedEntity);
        when(mapper.toResponseDTO(savedEntity)).thenReturn(response);

        NotificationResponseDTO result = service.sendNotification(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("SENT");
        assertThat(result).isEqualTo(response);
    }
}
