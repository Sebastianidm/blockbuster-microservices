package com.blockbuster.notifications.controller;

import com.blockbuster.notifications.model.dto.NotificationRequestDTO;
import com.blockbuster.notifications.model.dto.NotificationResponseDTO;
import com.blockbuster.notifications.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "notification-controller", description = "Endpoint para el envio de notificaciones")
public class NotificationController {

    private final NotificationService service;

    @Operation(summary = "Enviar notificacion", description = "Envia notificacion al usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificacion enviada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o error de validacion")
    })
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> send(@Valid @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.sendNotification(request));
    }
}
