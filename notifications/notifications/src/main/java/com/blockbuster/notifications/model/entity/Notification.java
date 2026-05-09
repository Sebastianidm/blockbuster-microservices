package com.blockbuster.notifications.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;

    private Long userId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String type;
    private String status;
    private LocalDateTime timestamp;
}