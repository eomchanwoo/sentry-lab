package com.example.sentrydemo.controller;

import io.sentry.Sentry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * GET /api/test
     * 정상 응답 - 프론트엔드와의 연결 확인용
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        log.info("✅ /api/test 호출됨 - 연결 테스트");

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Connection successful!",
                "timestamp", LocalDateTime.now().toString(),
                "traceId", getTraceId()));
    }

    /**
     * GET /api/error
     * RuntimeException을 강제로 발생시킴
     * → Sentry가 자동으로 캡처
     */
    @GetMapping("/error")
    public ResponseEntity<Map<String, String>> triggerError() {
        log.warn("⚠️ /api/error 호출됨 - 의도적 에러 발생 예정");
        log.info("에러 발생 직전의 Breadcrumb 기록");

        throw new RuntimeException("Intentional Server Error - Sentry 학습용 의도적 에러입니다.");
    }

    /**
     * GET /api/db-error
     * 잘못된 SQL 쿼리를 실행하여 DataAccessException 유발
     * → Sentry가 자동으로 캡처
     */
    @GetMapping("/db-error")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> triggerDbError() {
        log.warn("⚠️ /api/db-error 호출됨 - DB 에러 발생 예정");
        log.info("잘못된 SQL 쿼리를 실행합니다: SELECT * FROM non_existent_table");

        // 존재하지 않는 테이블을 조회 → DataAccessException
        entityManager.createNativeQuery("SELECT * FROM non_existent_table").getResultList();

        // 이 코드는 실행되지 않음
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /**
     * 현재 Sentry Trace ID를 반환
     */
    private String getTraceId() {
        try {
            var span = Sentry.getSpan();
            if (span != null) {
                return span.getSpanContext().getTraceId().toString();
            }
        } catch (Exception ignored) {
        }
        return "no-trace";
    }
}
