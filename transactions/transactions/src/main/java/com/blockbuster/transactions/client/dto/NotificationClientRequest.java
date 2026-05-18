package com.blockbuster.transactions.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationClientRequest {

    private Long userId;

    private String recipientEmail;

    private String subject;

    private String message;

    private String type;
}
