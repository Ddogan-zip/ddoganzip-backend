package com.ddoganzip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화를 위한 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (JWT 사용 시 일반적으로 비활성화)
                .csrf(AbstractHttpConfigurer::disable)

                // HTTP 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않는 Stateless 설정 (JWT 사용을 위함)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI 관련 경로는 모두 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // 회원가입 및 로그인 API 경로는 모두 허용 (추후에 만들 경로)
                        .requestMatchers("/api/members/signup", "/api/members/login").permitAll()

                        // 그 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // 여기에 나중에 JWT 필터를 추가하게 됩니다.

        return http.build();
    }
}
