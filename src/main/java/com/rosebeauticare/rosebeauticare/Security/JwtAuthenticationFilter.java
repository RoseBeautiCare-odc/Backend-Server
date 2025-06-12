package com.rosebeauticare.rosebeauticare.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        logger.debug("Processing request for URI: {}", request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            logger.debug("JWT token received: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");

            if (!jwtUtil.validateToken(jwt)) {
                logger.error("Invalid JWT token for URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String username = jwtUtil.getUsernameFromToken(jwt);
                String role = jwtUtil.getRoleFromToken(jwt);
                logger.debug("Extracted username: {}, role: {}", username, role);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    logger.debug("Created authority: {}", authority.getAuthority());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set for user: {} with role: {}", username, role);
                } else {
                    logger.debug("Authentication not set: username is {} or authentication already exists", username);
                }
            } catch (Exception e) {
                logger.error("JWT processing failed for token: {}. Error: {}", 
                    jwt.substring(0, Math.min(jwt.length(), 20)) + "...", e.getMessage());
            }
        } else {
            logger.debug("No valid Authorization header found for URI: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}