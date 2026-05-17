package com.blockbuster.catalog.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SecurityTestController.class)
@Import({
        SecurityConfig.class,
        InternalApiKeyFilter.class,
        JwtUtils.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@ActiveProfiles("test")
@TestPropertySource(properties = "internal.api.key=test-internal-key")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/secure-ping").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("No autorizado"));
    }

    @Test
    void shouldRejectInternalDiscountEndpointWithoutApiKey() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/1/stock/discount").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"message\":\"API key interna invalida\"}"));
    }

    @Test
    void shouldAllowInternalDiscountEndpointWithApiKey() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/1/stock/discount")
                        .header("X-Internal-Api-Key", "test-internal-key")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("discount"));
    }

    @Test
    void shouldRejectInternalRestoreEndpointWithoutApiKey() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/1/stock/restore").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"message\":\"API key interna invalida\"}"));
    }

    @Test
    void shouldAllowInternalRestoreEndpointWithApiKey() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/1/stock/restore")
                        .header("X-Internal-Api-Key", "test-internal-key")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("restore"));
    }

    @Test
    void shouldAllowProtectedEndpointWithAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/secure-ping")
                        .with(user("martin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("secured"));
    }

    @Test
    void shouldDenyAdminEndpointWhenRoleIsInsufficient() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/admin-ping")
                        .with(user("martin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acceso denegado"));
    }
}
