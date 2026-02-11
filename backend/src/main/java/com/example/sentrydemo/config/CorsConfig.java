package com.example.sentrydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS 설정
 * - Vue.js 개발 서버 (localhost:5173)에서의 요청 허용
 * - sentry-trace, baggage 헤더를 허용하여 분산 추적 지원
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173"));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더 (sentry-trace, baggage 포함 - 분산 추적에 필수)
        config.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "sentry-trace",
                "baggage"));

        // 브라우저에 노출할 응답 헤더
        config.setExposedHeaders(List.of(
                "sentry-trace",
                "baggage"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
