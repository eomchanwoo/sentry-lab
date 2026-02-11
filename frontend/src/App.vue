<template>
  <div id="sentry-app" data-testid="app-root">
    <!-- 헤더 -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo-area">
          <span class="logo-icon">🛡️</span>
          <h1 class="app-title">Sentry Error Factory</h1>
        </div>
        <div class="sentry-status" data-testid="sentry-status">
          <span class="status-dot" :class="sentryActive ? 'active' : 'inactive'"></span>
          <span class="status-text">{{ sentryActive ? 'Sentry 연결됨' : 'Sentry 미설정' }}</span>
        </div>
      </div>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <ErrorDashboard />
    </main>

    <!-- 푸터 -->
    <footer class="app-footer">
      <p>Sentry 학습용 풀스택 토이 프로젝트 · Vue.js 3 + Spring Boot 3</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ErrorDashboard from './components/ErrorDashboard.vue'

const sentryActive = computed(() => {
  return !!import.meta.env.VITE_SENTRY_DSN
})
</script>

<style>
/* ======= Global Styles ======= */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

*,
*::before,
*::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: #0f172a;
  color: #e2e8f0;
  min-height: 100vh;
  line-height: 1.6;
}

/* 배경 그라데이션 효과 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(ellipse at 20% 50%, rgba(120, 40, 200, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(239, 68, 68, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 80%, rgba(59, 130, 246, 0.1) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

#sentry-app {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ======= Header ======= */
.app-header {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-icon {
  font-size: 1.75rem;
}

.app-title {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #818cf8, #c084fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sentry-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(30, 41, 59, 0.6);
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.active {
  background: #22c55e;
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.6);
  animation: pulse 2s infinite;
}

.status-dot.inactive {
  background: #f59e0b;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.4);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-size: 0.85rem;
  color: #94a3b8;
  font-weight: 500;
}

/* ======= Main ======= */
.main-content {
  flex: 1;
  padding: 2.5rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* ======= Footer ======= */
.app-footer {
  text-align: center;
  padding: 1.5rem;
  color: #475569;
  font-size: 0.8rem;
  border-top: 1px solid rgba(148, 163, 184, 0.05);
}

/* ======= Responsive ======= */
@media (max-width: 768px) {
  .app-header {
    padding: 1rem;
  }

  .header-content {
    flex-direction: column;
    gap: 0.75rem;
  }

  .main-content {
    padding: 1.5rem 1rem;
  }

  .app-title {
    font-size: 1.25rem;
  }
}
</style>
