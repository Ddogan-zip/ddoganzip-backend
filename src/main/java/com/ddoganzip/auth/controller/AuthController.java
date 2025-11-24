package com.ddoganzip.auth.controller;

import com.ddoganzip.auth.dto.*;
import com.ddoganzip.auth.service.AuthService;
import com.ddoganzip.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("=== [AuthController] POST /api/auth/register - START ===");
        log.info("[AuthController] Register request received for email: {}", request.getEmail());

        try {
            authService.register(request);
            log.info("[AuthController] Registration successful for email: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Registration successful"));
        } catch (Exception e) {
            log.error("[AuthController] Registration failed for email: {}, Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [AuthController] POST /api/auth/register - END ===");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("=== [AuthController] POST /api/auth/login - START ===");
        log.info("[AuthController] Login request received for email: {}", request.getEmail());

        try {
            TokenResponse response = authService.login(request);
            log.info("[AuthController] Login successful for email: {}", request.getEmail());
            log.debug("[AuthController] Token generated - AccessToken length: {}, RefreshToken length: {}",
                response.getAccessToken().length(), response.getRefreshToken().length());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[AuthController] Login failed for email: {}, Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [AuthController] POST /api/auth/login - END ===");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("=== [AuthController] POST /api/auth/refresh - START ===");
        log.debug("[AuthController] Refresh token request received");

        try {
            TokenResponse response = authService.refresh(request);
            log.info("[AuthController] Token refresh successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[AuthController] Token refresh failed, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [AuthController] POST /api/auth/refresh - END ===");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("=== [AuthController] POST /api/auth/logout ===");
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
