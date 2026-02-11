import { createApp } from 'vue'
import * as Sentry from '@sentry/vue'
import App from './App.vue'

const app = createApp(App)

// Sentry 초기화
const sentryDsn = import.meta.env.VITE_SENTRY_DSN

if (sentryDsn) {
    Sentry.init({
        app,
        dsn: sentryDsn,
        environment: 'local-study',
        sendDefaultPii: true,

        // 성능 모니터링 - 모든 트랜잭션 수집
        tracesSampleRate: 1.0,

        // 분산 추적 - localhost 백엔드로의 요청에 sentry-trace, baggage 헤더 자동 추가
        tracePropagationTargets: ['localhost', /^\//],

        integrations: [
            Sentry.browserTracingIntegration(),
            Sentry.replayIntegration(),
        ],

        // Session Replay 설정
        replaysSessionSampleRate: 0.1,
        replaysOnErrorSampleRate: 1.0,
    })

    console.log('✅ Sentry initialized successfully')
} else {
    console.warn('⚠️ Sentry DSN이 설정되지 않았습니다. .env 파일에 VITE_SENTRY_DSN을 입력해주세요.')
}

app.mount('#app')
