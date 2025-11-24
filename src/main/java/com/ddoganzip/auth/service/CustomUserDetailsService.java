package com.ddoganzip.auth.service;

import com.ddoganzip.auth.repository.AuthRepository;
import com.ddoganzip.auth.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[CustomUserDetailsService] loadUserByUsername() - START for email: {}", email);

        log.debug("[CustomUserDetailsService] Searching for customer in database: {}", email);
        Customer customer = authRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[CustomUserDetailsService] User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.info("[CustomUserDetailsService] Customer found - ID: {}, Email: {}, Role: {}",
            customer.getId(), customer.getEmail(), customer.getRole());
        log.debug("[CustomUserDetailsService] Password hash from DB - Length: {}, Starts with: {}",
            customer.getPassword().length(), customer.getPassword().substring(0, Math.min(10, customer.getPassword().length())));

        UserDetails userDetails = new User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()))
        );

        log.info("[CustomUserDetailsService] UserDetails created successfully for email: {}", email);
        return userDetails;
    }
}
