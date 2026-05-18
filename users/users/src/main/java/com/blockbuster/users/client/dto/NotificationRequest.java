package com.blockbuster.users.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {

	private Long userId;

	private String recipientEmail;

	private String subject;

	private String message;

	private String type;
}
