package com.example.sentrydemo.filter;

import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * SentryContextFilter
 * - 모든 API 요청에 대해 MDC와 Sentry Context에 추가 정보를 설정합니다.
 * - requestTime: 요청 시간
 * - dummyUserId: 더미 유저 ID (학습용)
 * - requestPath: 요청 경로
 */
@Component
@Order(1)
public class SentryContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SentryContextFilter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestTime = LocalDateTime.now().format(FORMATTER);
        String dummyUserId = "user-" + UUID.randomUUID().toString().substring(0, 8);
        String requestPath = request.getMethod() + " " + request.getRequestURI();

        try {
            // 1. MDC에 정보 추가 (logback 로그에 포함)
            MDC.put("requestTime", requestTime);
            MDC.put("dummyUserId", dummyUserId);
            MDC.put("requestPath", requestPath);

            // 2. Sentry Scope에 정보 추가
            Sentry.configureScope(scope -> {
                scope.setTag("requestTime", requestTime);
                scope.setTag("requestPath", requestPath);
                scope.setExtra("dummyUserId", dummyUserId);
                scope.setExtra("requestMethod", request.getMethod());
                scope.setExtra("requestURI", request.getRequestURI());
                scope.setExtra("queryString", request.getQueryString());

                // 더미 유저 정보 설정
                io.sentry.protocol.User sentryUser = new io.sentry.protocol.User();
                sentryUser.setId(dummyUserId);
                sentryUser.setUsername("study-user");
                sentryUser.setEmail("study@example.com");
                scope.setUser(sentryUser);
            });

            log.info("📥 요청 수신: {} | userId: {} | time: {}", requestPath, dummyUserId, requestTime);

            filterChain.doFilter(request, response);

        } finally {
            // MDC 정리
            MDC.remove("requestTime");
            MDC.remove("dummyUserId");
            MDC.remove("requestPath");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // H2 Console, 정적 리소스는 필터 제외
        return path.startsWith("/h2-console")
                || path.startsWith("/favicon")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png");
    }
}
