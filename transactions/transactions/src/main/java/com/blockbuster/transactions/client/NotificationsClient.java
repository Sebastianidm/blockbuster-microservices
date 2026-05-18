package com.blockbuster.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.blockbuster.transactions.client.dto.NotificationClientRequest;

@FeignClient(name = "notificationsClient", url = "${notifications.service.url}")
public interface NotificationsClient {

    @PostMapping("/api/v1/notifications")
    void sendNotification(@RequestBody NotificationClientRequest request);
}
