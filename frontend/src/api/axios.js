import axios from 'axios'

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
})

// 응답 인터셉터 - 에러 로깅
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('[API Error]', error.message)
        return Promise.reject(error)
    }
)

export default apiClient
