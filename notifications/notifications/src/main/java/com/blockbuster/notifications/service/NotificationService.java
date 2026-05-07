package com.blockbuster.notifications.service;

import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;

public interface NotificationService {

    NotificationResponseDTO sendNotification(NotificationRequestDTO request);

}