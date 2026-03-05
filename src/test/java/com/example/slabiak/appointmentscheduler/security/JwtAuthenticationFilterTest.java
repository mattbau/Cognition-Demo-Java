package com.example.slabiak.appointmentscheduler.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.Assert.*;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private static final String SECRET = "testSecretKey12345";

    @Before
    public void setUp() throws Exception {
        filter = new JwtAuthenticationFilter();
        Field secretField = JwtAuthenticationFilter.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(filter, SECRET);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldNotFilterNonAgentApiPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/appointments");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    public void shouldFilterAgentApiPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    public void shouldAuthenticateWithValidToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("agent")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("agent", auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void shouldNotAuthenticateWithInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        request.addHeader("Authorization", "Bearer invalidtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void shouldNotAuthenticateWithMissingHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void shouldNotAuthenticateWithExpiredToken() throws Exception {
        String token = Jwts.builder()
                .setSubject("agent")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void shouldNotAuthenticateWithWrongSecret() throws Exception {
        String token = Jwts.builder()
                .setSubject("agent")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, "wrongSecret")
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void shouldContinueFilterChainRegardless() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/agent-api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        // FilterChain.doFilter was called (request passed through)
        assertNotNull(filterChain.getRequest());
    }
}
