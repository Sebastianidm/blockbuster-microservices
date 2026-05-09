package com.blockbuster.notifications.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private String id;
    private String recipientEmail;
    private String subject;
    private String status;
    private LocalDateTime timestamp;
}