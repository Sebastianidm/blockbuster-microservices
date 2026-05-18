package com.blockbuster.notifications.service;

import com.blockbuster.notifications.mapper.NotificationMapper;
import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.model.entity.Notification;
import com.blockbuster.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        log.info("Procesando notificación para el usuario: {}", request.getUserId());

        Notification notification = mapper.toEntity(request);
        log.info("ENVIANDO CORREO A: {} | ASUNTO: {}", notification.getRecipientEmail(), notification.getSubject());
        notification.setStatus("SENT");

        Notification savedNotification = repository.save(notification);


        return mapper.toResponseDTO(savedNotification);
    }
}