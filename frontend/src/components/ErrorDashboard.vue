<template>
  <div class="dashboard">
    <h2 class="dashboard-title">🔥 Error Factory</h2>
    <p class="dashboard-subtitle">버튼을 클릭하여 의도적으로 에러를 발생시키고, Sentry에서 추적해보세요.</p>

    <!-- 에러 버튼 그리드 -->
    <div class="button-grid">
      <!-- JS Error -->
      <div class="card card-js">
        <div class="card-icon">⚡</div>
        <h3>JS Error</h3>
        <p class="card-desc">존재하지 않는 함수를 호출하여 TypeError를 발생시킵니다.</p>
        <button
          data-testid="btn-js-error"
          class="btn btn-danger"
          @click="triggerJsError"
        >
          TypeError 발생
        </button>
      </div>

      <!-- Vue Error -->
      <div class="card card-vue">
        <div class="card-icon">🖼️</div>
        <h3>Vue Error</h3>
        <p class="card-desc">컴포넌트 렌더링 중 null 참조 에러를 발생시킵니다.</p>
        <button
          data-testid="btn-vue-error"
          class="btn btn-warning"
          @click="triggerVueError"
        >
          렌더링 에러 발생
        </button>
      </div>

      <!-- BE Connect -->
      <div class="card card-connect">
        <div class="card-icon">🔗</div>
        <h3>BE Connect</h3>
        <p class="card-desc">백엔드 /api/test 엔드포인트를 호출하여 연결을 확인합니다.</p>
        <button
          data-testid="btn-be-connect"
          class="btn btn-success"
          @click="testBackendConnection"
        >
          연결 테스트
        </button>
      </div>

      <!-- BE 500 Error -->
      <div class="card card-500">
        <div class="card-icon">💥</div>
        <h3>BE 500 Error</h3>
        <p class="card-desc">백엔드에서 RuntimeException을 강제로 발생시킵니다.</p>
        <button
          data-testid="btn-be-500-error"
          class="btn btn-danger"
          @click="triggerBackendError"
        >
          500 에러 발생
        </button>
      </div>

      <!-- DB Error -->
      <div class="card card-db">
        <div class="card-icon">🗄️</div>
        <h3>DB Error</h3>
        <p class="card-desc">잘못된 SQL 쿼리로 DataAccessException을 유발합니다.</p>
        <button
          data-testid="btn-db-error"
          class="btn btn-danger"
          @click="triggerDbError"
        >
          DB 에러 발생
        </button>
      </div>
    </div>

    <!-- 결과 표시 영역 -->
    <div
      v-if="result"
      class="result-panel"
      :class="result.type"
      data-testid="result-display"
    >
      <div class="result-header">
        <span class="result-icon">{{ result.type === 'success' ? '✅' : '❌' }}</span>
        <strong>{{ result.title }}</strong>
        <span class="result-time">{{ result.timestamp }}</span>
      </div>
      <pre class="result-body">{{ result.message }}</pre>
    </div>

    <!-- Vue Error 렌더링 영역 -->
    <div v-if="showBrokenComponent" class="broken-area">
      <BrokenComponent />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import apiClient from '../api/axios.js'
import BrokenComponent from './BrokenComponent.vue'

const result = ref(null)
const showBrokenComponent = ref(false)

function setResult(type, title, message) {
  result.value = {
    type,
    title,
    message,
    timestamp: new Date().toLocaleTimeString('ko-KR'),
  }
}

// 1. JS Error - TypeError
function triggerJsError() {
  try {
    // 존재하지 않는 함수 호출
    const nonExistent = undefined
    nonExistent.callSomething()
  } catch (e) {
    setResult('error', 'TypeError 발생!', `${e.name}: ${e.message}`)
    throw e // Sentry가 캡처할 수 있도록 다시 throw
  }
}

// 2. Vue Error - 렌더링 도중 null 참조
function triggerVueError() {
  setResult('error', 'Vue 렌더링 에러 발생 시도...', 'BrokenComponent를 렌더링합니다.')
  showBrokenComponent.value = true
  // 에러 발생 후 컴포넌트 제거 (다시 시도 가능하도록)
  setTimeout(() => {
    showBrokenComponent.value = false
  }, 100)
}

// 3. BE Connect - /api/test 호출
async function testBackendConnection() {
  setResult('success', '요청 중...', 'GET /api/test 호출 중...')
  try {
    const response = await apiClient.get('/api/test')
    setResult('success', '연결 성공!', JSON.stringify(response.data, null, 2))
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', '연결 실패!', msg)
  }
}

// 4. BE 500 Error - /api/error 호출
async function triggerBackendError() {
  setResult('error', '요청 중...', 'GET /api/error 호출 중...')
  try {
    const response = await apiClient.get('/api/error')
    setResult('success', '응답 수신', JSON.stringify(response.data, null, 2))
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', 'BE 500 에러 수신!', msg)
  }
}

// 5. DB Error - /api/db-error 호출
async function triggerDbError() {
  setResult('error', '요청 중...', 'GET /api/db-error 호출 중...')
  try {
    const response = await apiClient.get('/api/db-error')
    setResult('success', '응답 수신', JSON.stringify(response.data, null, 2))
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', 'DB 에러 수신!', msg)
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 900px;
  margin: 0 auto;
}

.dashboard-title {
  font-size: 2rem;
  margin-bottom: 0.25rem;
  background: linear-gradient(135deg, #f97316, #ef4444, #ec4899);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dashboard-subtitle {
  color: #94a3b8;
  margin-bottom: 2rem;
  font-size: 0.95rem;
}

/* 버튼 그리드 */
.button-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.25rem;
  margin-bottom: 2rem;
}

.card {
  background: rgba(30, 41, 59, 0.7);
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 16px;
  padding: 1.5rem;
  backdrop-filter: blur(12px);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.card-icon {
  font-size: 2rem;
  margin-bottom: 0.75rem;
}

.card h3 {
  font-size: 1.1rem;
  color: #f1f5f9;
  margin-bottom: 0.5rem;
}

.card-desc {
  font-size: 0.85rem;
  color: #94a3b8;
  margin-bottom: 1rem;
  line-height: 1.5;
}

/* 버튼 */
.btn {
  width: 100%;
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.btn:active {
  transform: scale(0.97);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
}

.btn-danger:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.4);
}

.btn-warning {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
}

.btn-warning:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.btn-success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.btn-success:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.4);
}

/* 결과 패널 */
.result-panel {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.15);
  animation: slideIn 0.3s ease;
}

.result-panel.success {
  border-left: 4px solid #22c55e;
}

.result-panel.error {
  border-left: 4px solid #ef4444;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  color: #f1f5f9;
}

.result-icon {
  font-size: 1.1rem;
}

.result-time {
  margin-left: auto;
  font-size: 0.8rem;
  color: #64748b;
}

.result-body {
  background: rgba(15, 23, 42, 0.6);
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #cbd5e1;
  overflow-x: auto;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.broken-area {
  position: absolute;
  width: 0;
  height: 0;
  overflow: hidden;
}
</style>
