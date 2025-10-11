package com.ddoganzip.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT 인증 처리를 위한 설정
        String securitySchemeName = "bearerAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        // 개발 서버 URL 설정
        Server devServer = new Server().url("http://localhost:8080").description("개발 서버");

        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(devServer)) // 서버 정보 추가
                .addSecurityItem(securityRequirement) // 인증 요구사항 추가
                .components(components); // 인증 스키마 추가
    }

    private Info apiInfo() {
        return new Info()
                .title("ddoganzip API Documentation") // API 문서 제목
                .description("API for ddoganzip project") // API 문서 설명
                .version("1.0.0"); // API 버전
    }
}
