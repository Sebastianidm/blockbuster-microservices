package com.blockbuster.notifications.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void shouldBuildOpenApiMetadata() {
        OpenAPI openApi = config.customOpenApi();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).isEqualTo("Api de Notificaciones Blockbuster");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openApi.getInfo().getDescription()).contains("notificaciones");
        assertThat(openApi.getInfo().getContact()).isNotNull();
        assertThat(openApi.getInfo().getContact().getName()).isNotBlank();
        assertThat(openApi.getInfo().getContact().getEmail()).contains("@");
    }
}
