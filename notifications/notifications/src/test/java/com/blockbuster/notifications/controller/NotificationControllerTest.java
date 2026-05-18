package com.blockbuster.notifications.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blockbuster.notifications.exception.GlobalExceptionHandler;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.security.InternalApiKeyFilter;
import com.blockbuster.notifications.service.NotificationService;

@WebMvcTest(NotificationController.class)
@Import({GlobalExceptionHandler.class, InternalApiKeyFilter.class})
@TestPropertySource(properties = "internal.api.key=test-internal-key")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldRejectNotificationWithoutApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/notifications")
                        .contentType("application/json")
                        .content("{\"userId\":1,\"recipientEmail\":\"martin@duocuc.cl\",\"subject\":\"Hola\",\"message\":\"Test\",\"type\":\"USER_REGISTRATION\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("API key interna invalida"))
                .andExpect(jsonPath("$.path").value("/api/v1/notifications"));
    }

    @Test
    void shouldSendNotificationWhenApiKeyIsValid() throws Exception {
        when(notificationService.sendNotification(any())).thenReturn(NotificationResponseDTO.builder()
                .id("abc123")
                .recipientEmail("martin@duocuc.cl")
                .subject("Hola")
                .status("SENT")
                .build());

        mockMvc.perform(post("/api/v1/notifications")
                        .header("X-Internal-Api-Key", "test-internal-key")
                        .contentType("application/json")
                        .content("{\"userId\":1,\"recipientEmail\":\"martin@duocuc.cl\",\"subject\":\"Hola\",\"message\":\"Test\",\"type\":\"USER_REGISTRATION\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }
}
