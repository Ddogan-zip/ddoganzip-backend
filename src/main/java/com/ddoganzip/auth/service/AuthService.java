package com.ddoganzip.auth.service;

import com.ddoganzip.auth.dto.*;
import com.ddoganzip.auth.repository.AuthRepository;
import com.ddoganzip.auth.util.JwtUtil;
import com.ddoganzip.customers.cart.entity.Cart;
import com.ddoganzip.auth.entity.Customer;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request) {
        log.info("[AuthService] register() - START for email: {}", request.getEmail());

        log.debug("[AuthService] Checking if email exists: {}", request.getEmail());
        if (authRepository.existsByEmail(request.getEmail())) {
            log.warn("[AuthService] Registration failed - Email already exists: {}", request.getEmail());
            throw new CustomException("Email already exists");
        }

        log.debug("[AuthService] Creating new customer for email: {}", request.getEmail());
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());

        log.debug("[AuthService] Encoding password for email: {}", request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("[AuthService] Password encoded successfully - Hash length: {}", encodedPassword.length());
        customer.setPassword(encodedPassword);

        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());

        log.debug("[AuthService] Creating cart for customer: {}", request.getEmail());
        Cart cart = new Cart();
        cart.setCustomer(customer);
        customer.setCart(cart);

        log.debug("[AuthService] Saving customer to database: {}", request.getEmail());
        Customer savedCustomer = authRepository.save(customer);
        log.info("[AuthService] Customer registered successfully with ID: {} for email: {}",
            savedCustomer.getId(), request.getEmail());
    }

    public TokenResponse login(LoginRequest request) {
        log.info("[AuthService] login() - START for email: {}", request.getEmail());

        try {
            log.debug("[AuthService] Authenticating user: {}", request.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("[AuthService] Authentication successful for email: {}", request.getEmail());
        } catch (Exception e) {
            log.error("[AuthService] Authentication failed for email: {}, Error: {}",
                request.getEmail(), e.getMessage());
            throw e;
        }

        log.debug("[AuthService] Generating access token for email: {}", request.getEmail());
        String accessToken = jwtUtil.generateAccessToken(request.getEmail());
        log.debug("[AuthService] Access token generated - Length: {}", accessToken.length());

        log.debug("[AuthService] Generating refresh token for email: {}", request.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
        log.debug("[AuthService] Refresh token generated - Length: {}", refreshToken.length());

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenValidity() / 1000)
                .build();

        log.info("[AuthService] login() - SUCCESS for email: {}", request.getEmail());
        return response;
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        log.info("[AuthService] refresh() - START");

        log.debug("[AuthService] Validating refresh token");
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            log.warn("[AuthService] Invalid refresh token provided");
            throw new CustomException("Invalid refresh token");
        }
        log.debug("[AuthService] Refresh token is valid");

        String email = jwtUtil.getEmailFromToken(request.getRefreshToken());
        log.info("[AuthService] Refreshing tokens for email: {}", email);

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        log.info("[AuthService] refresh() - SUCCESS for email: {}", email);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenValidity() / 1000)
                .build();
    }
}
