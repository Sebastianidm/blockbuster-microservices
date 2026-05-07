package com.blockbuster.notifications.mapper;

import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.model.entity.Notification;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequestDTO dto) {
        if (dto == null) return null;

        return Notification.builder()
                .userId(dto.getUserId())
                .recipientEmail(dto.getRecipientEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .type(dto.getType())
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        if (notification == null) return null;

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .recipientEmail(notification.getRecipientEmail())
                .subject(notification.getSubject())
                .status(notification.getStatus())
                .timestamp(notification.getTimestamp())
                .build();
    }
}