package com.blockbuster.notifications.model.dto;

import lombok.Data;

@Data
public class NotificationRequestDTO {
    private Long userId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String type;
}