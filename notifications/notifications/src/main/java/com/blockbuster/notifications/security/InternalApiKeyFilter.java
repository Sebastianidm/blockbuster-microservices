package com.blockbuster.notifications.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Data;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final String expectedApiKey;

    public InternalApiKeyFilter(@Value("${internal.api.key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().startsWith("/api/v1/notifications");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-Internal-Api-Key");

        if (expectedApiKey.equals(apiKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("API key interna invalida")
                .path(request.getRequestURI())
                .build();
        response.getWriter().write(buildJsonResponse(errorResponse));
    }

    private String buildJsonResponse(ApiErrorResponse errorResponse) {
        return "{\"timestamp\":\"" + errorResponse.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "\",\"status\":" + errorResponse.getStatus()
                + ",\"message\":\"" + errorResponse.getMessage()
                + "\",\"path\":\"" + errorResponse.getPath()
                + "\"}";
    }

    @Data
    @Builder
    private static class ApiErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String message;
        private String path;
    }
}
