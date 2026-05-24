package com.back.zapateria.config;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.back.zapateria.service.JwtService;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @AfterEach
    @SuppressWarnings("unused")
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_returnsTrue_withoutBearerHeader() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertTrue(filter.shouldNotFilter(request));

        request.addHeader("Authorization", "Basic abc");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternal_setsAuthentication_whenTokenIsValid() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.isTokenValid("token-1")).thenReturn(true);
        when(jwtService.extractEmail("token-1")).thenReturn("user@demo.com");
        when(jwtService.extractRole("token-1")).thenReturn("USER");

        filter.doFilterInternal(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("user@demo.com", authentication.getPrincipal());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void doFilterInternal_setsAuthenticationWithNoAuthorities_whenRoleIsNull() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-2");

        when(jwtService.isTokenValid("token-2")).thenReturn(true);
        when(jwtService.extractEmail("token-2")).thenReturn("user@demo.com");
        when(jwtService.extractRole("token-2")).thenReturn(null);

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void doFilterInternal_clearsContext_whenJwtServiceThrows() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-3");

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("existing", null)
        );

        when(jwtService.isTokenValid("token-3")).thenThrow(new RuntimeException("invalid token"));

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
