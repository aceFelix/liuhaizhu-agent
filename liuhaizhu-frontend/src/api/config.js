import axios from 'axios'
import { secureStorage } from '@/utils/helpers'
import { useAuthStore } from '@/stores/auth'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  (import.meta.env.PROD ? '' : 'http://localhost:8000')

// 创建 axios 实例
export const request = axios.create({
  baseURL: BASE_URL,
  timeout: 300000,
  headers: {
    'Content-Type': 'application/json',
  },
})

let isRefreshing = false
let refreshSubscribers = []

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback)
}

const onTokenRefreshed = (token) => {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    const userId = secureStorage.getItem('chat_user_id')
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    const authStore = useAuthStore()
    const originalRequest = error.config

    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token) => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`
            resolve(request(originalRequest))
          })
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const result = await authStore.handleRefreshToken()
        if (result.success) {
          onTokenRefreshed(authStore.token)
          return request(originalRequest)
        }
      } catch (refreshError) {
        console.error('刷新token失败:', refreshError)
        authStore.handleLogout()
        window.location.href = '/'
      } finally {
        isRefreshing = false
      }
    }

    if (error.code === 'ECONNABORTED') {
      error.message = '请求超时，请稍后重试'
    } else if (error.response) {
      const status = error.response.status
      if (status === 401) {
        error.message = '未授权，请重新登录'
      } else if (status === 403) {
        error.message = '拒绝访问'
      } else if (status === 404) {
        error.message = '请求的资源不存在'
      } else if (status === 500) {
        error.message = '服务器错误'
      } else if (status === 502) {
        error.message = '网关错误'
      } else if (status === 503) {
        error.message = '服务不可用'
      } else if (status === 504) {
        error.message = '网关超时'
      }
    } else if (error.message.includes('Network Error')) {
      error.message = '网络连接失败，请检查网络'
    }
    return Promise.reject(error)
  },
)

export default request
