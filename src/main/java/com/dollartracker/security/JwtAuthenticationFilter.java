package com.dollartracker.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = null;

        // Try to get token from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        // Try to get token from cookies
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        try {
            if (token != null && tokenProvider.validateToken(token)) {
                String userId = tokenProvider.getUserIdFromToken(token);
                String email = tokenProvider.getEmailFromToken(token);

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authentication.setDetails(email);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException ex) {
            logger.error("Could not set user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }
}
