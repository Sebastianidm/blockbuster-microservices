package com.blockbuster.users.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.blockbuster.users.client.dto.NotificationRequest;

@FeignClient(name = "notificationsClient", url = "${notifications.service.url}")
public interface NotificationsClient {

	@PostMapping("/api/v1/notifications")
	void sendNotification(@RequestBody NotificationRequest request);
}
