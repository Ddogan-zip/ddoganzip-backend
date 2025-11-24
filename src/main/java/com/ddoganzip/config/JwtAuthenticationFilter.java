package com.ddoganzip.config;

import com.ddoganzip.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("[JwtAuthenticationFilter] Processing request: {} {}", request.getMethod(), requestURI);

        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            log.debug("[JwtAuthenticationFilter] JWT token found in request - Token length: {}", token.length());

            if (jwtUtil.validateToken(token)) {
                log.debug("[JwtAuthenticationFilter] JWT token is valid");

                String email = jwtUtil.getEmailFromToken(token);
                log.info("[JwtAuthenticationFilter] Extracted email from token: {}", email);

                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    log.debug("[JwtAuthenticationFilter] UserDetails loaded for email: {}", email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("[JwtAuthenticationFilter] Authentication set in SecurityContext for email: {}", email);
                } catch (Exception e) {
                    log.error("[JwtAuthenticationFilter] Failed to authenticate user: {}, Error: {}",
                        email, e.getMessage(), e);
                }
            } else {
                log.warn("[JwtAuthenticationFilter] JWT token validation failed");
            }
        } else {
            log.debug("[JwtAuthenticationFilter] No JWT token found in request for URI: {}", requestURI);
        }

        log.debug("[JwtAuthenticationFilter] Continuing filter chain for: {}", requestURI);
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("[JwtAuthenticationFilter] Extracted token from Authorization header");
            return token;
        }
        log.debug("[JwtAuthenticationFilter] No Bearer token found in Authorization header");
        return null;
    }
}
