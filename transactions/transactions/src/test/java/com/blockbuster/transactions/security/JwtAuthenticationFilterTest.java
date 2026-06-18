package com.blockbuster.transactions.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipAuthenticationWhenHeaderIsMissing() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtils);

        filter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtils);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtUtils.extractUsername("valid-token")).thenReturn("martin");
        when(jwtUtils.extractRole("valid-token")).thenReturn("ROLE_EMPLOYEE");
        when(jwtUtils.isTokenValid(org.mockito.ArgumentMatchers.eq("valid-token"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);

        filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);

        assertEquals("martin", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals("ROLE_EMPLOYEE",
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldClearContextWhenTokenParsingFails() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtils);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("old-user", null, java.util.List.of()));

        when(jwtUtils.extractUsername("invalid-token")).thenThrow(new JwtException("bad token"));

        filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
