package com.blockbuster.transactions.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

class JwtAccessDeniedHandlerTest {

    private final JwtAccessDeniedHandler handler = new JwtAccessDeniedHandler();

    @Test
    void shouldWriteForbiddenJsonResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(new MockHttpServletRequest(), response, new AccessDeniedException("denied"));

        assertEquals(403, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"message\":\"Acceso denegado\"}", response.getContentAsString());
    }
}
