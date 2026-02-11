<template>
  <div class="dashboard">
    <h2 class="dashboard-title">🔥 Error Factory</h2>
    <p class="dashboard-subtitle">버튼을 클릭하여 의도적으로 에러를 발생시키고, Sentry에서 추적해보세요.</p>

    <!-- Frontend Zone -->
    <section class="error-section">
      <div class="section-header section-fe">
        <span class="section-badge badge-fe">FE</span>
        <h3 class="section-title">Frontend Zone 🎨</h3>
        <p class="section-desc">브라우저에서 직접 발생하는 에러</p>
      </div>
      <div class="button-grid">
        <!-- JS Error -->
        <div class="card card-fe">
          <div class="card-icon">⚡</div>
          <h3>JS TypeError</h3>
          <p class="card-desc">존재하지 않는 함수를 호출하여 TypeError를 발생시킵니다.</p>
          <button
            data-testid="btn-js-error"
            class="btn btn-fe"
            @click="triggerJsError"
          >
            TypeError 발생
          </button>
        </div>

        <!-- Vue Error -->
        <div class="card card-fe">
          <div class="card-icon">🖼️</div>
          <h3>Vue Render Error</h3>
          <p class="card-desc">컴포넌트 렌더링 중 null 참조 에러를 발생시킵니다.</p>
          <button
            data-testid="btn-vue-error"
            class="btn btn-fe"
            @click="triggerVueError"
          >
            렌더링 에러 발생
          </button>
        </div>
      </div>
    </section>

    <!-- Backend Zone -->
    <section class="error-section">
      <div class="section-header section-be">
        <span class="section-badge badge-be">BE</span>
        <h3 class="section-title">Backend Zone ☕</h3>
        <p class="section-desc">백엔드 API를 호출하여 서버에서 발생하는 에러</p>
      </div>
      <div class="button-grid">
        <!-- BE Connect -->
        <div class="card card-be">
          <div class="card-icon">🔗</div>
          <h3>API 연결 테스트</h3>
          <p class="card-desc">백엔드 /api/test 엔드포인트를 호출하여 연결을 확인합니다.</p>
          <button
            data-testid="btn-be-connect"
            class="btn btn-be-success"
            @click="testBackendConnection"
          >
            연결 테스트
          </button>
        </div>

        <!-- BE 500 Error -->
        <div class="card card-be">
          <div class="card-icon">💥</div>
          <h3>500 Server Error</h3>
          <p class="card-desc">백엔드에서 RuntimeException을 강제로 발생시킵니다.</p>
          <button
            data-testid="btn-be-500-error"
            class="btn btn-be"
            @click="triggerBackendError"
          >
            500 에러 발생
          </button>
        </div>

        <!-- DB Error -->
        <div class="card card-be">
          <div class="card-icon">🗄️</div>
          <h3>DB Error</h3>
          <p class="card-desc">잘못된 SQL 쿼리로 DataAccessException을 유발합니다.</p>
          <button
            data-testid="btn-db-error"
            class="btn btn-be"
            @click="triggerDbError"
          >
            DB 에러 발생
          </button>
        </div>
      </div>
    </section>

    <!-- 결과 표시 영역 -->
    <div
      v-if="result"
      class="result-panel"
      :class="[result.type, `source-${result.source}`]"
      data-testid="result-display"
    >
      <div class="result-header">
        <span class="source-badge" :class="`badge-${result.source}`">{{ result.source === 'fe' ? 'FE' : 'BE' }}</span>
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

function setResult(type, title, message, source = 'fe') {
  result.value = {
    type,
    title,
    message,
    source,
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
    setResult('error', 'TypeError 발생!', `${e.name}: ${e.message}`, 'fe')
    throw e // Sentry가 캡처할 수 있도록 다시 throw
  }
}

// 2. Vue Error - 렌더링 도중 null 참조
function triggerVueError() {
  setResult('error', 'Vue 렌더링 에러 발생 시도...', 'BrokenComponent를 렌더링합니다.', 'fe')
  showBrokenComponent.value = true
  // 에러 발생 후 컴포넌트 제거 (다시 시도 가능하도록)
  setTimeout(() => {
    showBrokenComponent.value = false
  }, 100)
}

// 3. BE Connect - /api/test 호출
async function testBackendConnection() {
  setResult('success', '요청 중...', 'GET /api/test 호출 중...', 'be')
  try {
    const response = await apiClient.get('/api/test')
    setResult('success', '연결 성공!', JSON.stringify(response.data, null, 2), 'be')
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', '연결 실패!', msg, 'be')
  }
}

// 4. BE 500 Error - /api/error 호출
async function triggerBackendError() {
  setResult('error', '요청 중...', 'GET /api/error 호출 중...', 'be')
  try {
    const response = await apiClient.get('/api/error')
    setResult('success', '응답 수신', JSON.stringify(response.data, null, 2), 'be')
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', 'BE 500 에러 수신!', msg, 'be')
  }
}

// 5. DB Error - /api/db-error 호출
async function triggerDbError() {
  setResult('error', '요청 중...', 'GET /api/db-error 호출 중...', 'be')
  try {
    const response = await apiClient.get('/api/db-error')
    setResult('success', '응답 수신', JSON.stringify(response.data, null, 2), 'be')
  } catch (error) {
    const msg = error.response
      ? `Status: ${error.response.status}\n${JSON.stringify(error.response.data, null, 2)}`
      : `Network Error: ${error.message}`
    setResult('error', 'DB 에러 수신!', msg, 'be')
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

/* ======= Section Layout ======= */
.error-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  flex-wrap: wrap;
}

.section-fe {
  background: rgba(168, 85, 247, 0.08);
  border: 1px solid rgba(168, 85, 247, 0.2);
}

.section-be {
  background: rgba(59, 130, 246, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.section-badge {
  padding: 0.2rem 0.6rem;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.badge-fe {
  background: linear-gradient(135deg, #a855f7, #7c3aed);
  color: white;
}

.badge-be {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.section-title {
  font-size: 1.1rem;
  color: #f1f5f9;
  margin: 0;
}

.section-desc {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0;
  margin-left: auto;
}

/* ======= Button Grid ======= */
.button-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.25rem;
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

.card-fe {
  border-top: 3px solid rgba(168, 85, 247, 0.5);
}

.card-fe:hover {
  box-shadow: 0 12px 40px rgba(168, 85, 247, 0.15);
}

.card-be {
  border-top: 3px solid rgba(59, 130, 246, 0.5);
}

.card-be:hover {
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.15);
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

/* ======= Buttons ======= */
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

/* Frontend buttons - purple theme */
.btn-fe {
  background: linear-gradient(135deg, #a855f7, #7c3aed);
  color: white;
  box-shadow: 0 4px 15px rgba(168, 85, 247, 0.3);
}

.btn-fe:hover {
  background: linear-gradient(135deg, #9333ea, #6d28d9);
  box-shadow: 0 6px 20px rgba(168, 85, 247, 0.4);
}

/* Backend buttons - blue theme */
.btn-be {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.btn-be:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

/* Backend success button - green/blue theme */
.btn-be-success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.btn-be-success:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.4);
}

/* ======= Source Badge (Result) ======= */
.source-badge {
  padding: 0.15rem 0.5rem;
  border-radius: 5px;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 1px;
}

/* ======= Result Panel ======= */
.result-panel {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.15);
  animation: slideIn 0.3s ease;
  margin-top: 2rem;
}

.result-panel.source-fe {
  border-left: 4px solid #a855f7;
}

.result-panel.source-be {
  border-left: 4px solid #3b82f6;
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

/* ======= Responsive ======= */
@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .section-desc {
    margin-left: 0;
  }
}
</style>
