# Sentry Lab 🧪

이 프로젝트는 Sentry의 에러 트래킹 및 성능 모니터링 기능을 실험해보기 위한 랩(Lab) 환경입니다.
실제 똑똑의 환경과 동일한 Vue.js 프론트엔드와 Spring Boot 백엔드로 구성되어 있으며, Sentry DSN만 변경하여 손쉽게 테스트해볼 수 있도록 구성되었습니다.

## 🚀 시작하기

### 1. 사전 요구사항

- **Node.js**: v18 이상 권장
- **Java**: JDK 21 이상 (LTS 권장)
- **Sentry 계정**: 테스트할 Sentry 프로젝트(Vue, Spring Boot) 생성 및 DSN 발급

---

### 2. Frontend 설정 (Vue.js)

1. `frontend` 디렉토리로 이동하여 의존성을 설치합니다.
   ```bash
   cd frontend
   npm install
   ```

2. `.env` 파일을 생성하고 본인의 DSN을 설정합니다.
   `.env.example` 파일을 복사하여 `.env`를 생성하세요.
   ```bash
   cp .env.example .env
   ```
   `.env` 파일을 열어 `VITE_SENTRY_DSN` 값을 수정합니다.
   ```ini
   # frontend/.env
   VITE_SENTRY_DSN=https://your-public-dsn@sentry.io/project-id
   ```

3. 개발 서버를 실행합니다.
   ```bash
   npm run dev
   ```
   실행 후 브라우저에서 `http://localhost:5173`으로 접속할 수 있습니다.

---

### 3. Backend 설정 (Spring Boot)


1. DSN 설정을 위해 `application-secret.yml` 파일을 생성합니다.
   `backend/src/main/resources` 디렉토리로 이동하여 예제 파일을 복사하세요.
   ```bash
   cd backend/src/main/resources
   cp application-secret.yml.example application-secret.yml
   ```
   
2. `application-secret.yml` 파일을 열어 `sentry.dsn` 값을 본인의 Spring Boot 프로젝트 DSN으로 변경합니다.
   ```yaml
   # backend/src/main/resources/application-secret.yml
   sentry:
     dsn: https://your-public-dsn@sentry.io/project-id
   ```

3. 백엔드 서버를 실행합니다.
   `backend` 디렉토리(또는 루트)에서 아래 명령어를 실행하세요.
   ```bash
   cd backend
   
   # Windows (한글 로그 깨짐 방지 스크립트 실행)
   .\run-backend.bat

   # Mac/Linux
   ./gradlew bootRun
   ```
   서버는 `http://localhost:8080`에서 실행됩니다.

---

### 4. 테스트 및 확인

1. **프론트엔드 접속**: `http://localhost:5173`에 접속합니다.
2. **에러 트리거**: 화면에 있는 에러 발생 버튼들을 클릭합니다.
   - **Type Error**: 자바스크립트 런타임 에러
   - **Backend 500**: 백엔드 API 호출 중 예외 발생
3. **Sentry 확인**: 본인의 Sentry 대시보드에서 이슈가 수집되는지 확인합니다.
4. **Distributed Tracing 확인**: 프론트엔드에서 백엔드로 이어지는 트랜잭션이 연결되어 보이는지 확인합니다.

---

### ⚠️ 주의사항

- 이 프로젝트는 학습 및 테스트 용도입니다.
