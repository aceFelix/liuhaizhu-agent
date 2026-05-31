import { describe, it, expect, beforeEach, vi } from 'vitest'

// Use vi.hoisted() so mock variables are accessible before hoisted vi.mock() calls
const mocks = vi.hoisted(() => ({
  post: vi.fn(),
  get: vi.fn(),
  delete: vi.fn(),
  requestUse: vi.fn(),
  responseUse: vi.fn(),
}))

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      post: mocks.post,
      get: mocks.get,
      delete: mocks.delete,
      interceptors: {
        request: { use: mocks.requestUse },
        response: { use: mocks.responseUse },
      },
    })),
  },
  create: vi.fn(() => ({
    post: mocks.post,
    get: mocks.get,
    delete: mocks.delete,
    interceptors: {
      request: { use: mocks.requestUse },
      response: { use: mocks.responseUse },
    },
  })),
}))

vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    token: null,
    handleRefreshToken: vi.fn(),
    handleLogout: vi.fn(),
  })),
}))

vi.mock('@/utils/helpers', () => ({
  secureStorage: {
    getItem: vi.fn(() => null),
    setItem: vi.fn(),
    removeItem: vi.fn(),
  },
}))

// Now import the modules that trigger the mocks
import { request } from '@/api/config'
import {
  login,
  register,
  sendRegisterCode,
  refreshToken,
  getCurrentUser,
  logout,
  deleteAccount,
  upgradeToVip,
} from '@/api/auth'

// ============================================================
// Auth API 模块测试
// ============================================================
describe('Auth API 模块', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('应调用 POST /api/auth/login', async () => {
      const data = { username: 'alice', password: 'pass123' }
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await login(data)

      expect(mocks.post).toHaveBeenCalledWith('/api/auth/login', data)
    })
  })

  describe('register', () => {
    it('应调用 POST /api/auth/register', async () => {
      const data = { username: 'newUser', password: 'pass', registerMode: 'username' }
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await register(data)

      expect(mocks.post).toHaveBeenCalledWith('/api/auth/register', data)
    })
  })

  describe('sendRegisterCode', () => {
    it('应调用 POST /api/auth/send-register-code', async () => {
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await sendRegisterCode('test@example.com')

      expect(mocks.post).toHaveBeenCalledWith(
        '/api/auth/send-register-code',
        { email: 'test@example.com' },
      )
    })
  })

  describe('refreshToken', () => {
    it('应调用 POST /api/auth/refresh', async () => {
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await refreshToken('refresh_token_abc')

      expect(mocks.post).toHaveBeenCalledWith(
        '/api/auth/refresh',
        { refreshToken: 'refresh_token_abc' },
      )
    })
  })

  describe('getCurrentUser', () => {
    it('应调用 GET /api/auth/me', async () => {
      mocks.get.mockResolvedValue({
        data: { code: 200, data: { userId: '1' } },
      })

      await getCurrentUser()

      expect(mocks.get).toHaveBeenCalledWith('/api/auth/me')
    })
  })

  describe('logout', () => {
    it('应调用 POST /api/auth/logout', async () => {
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await logout()

      expect(mocks.post).toHaveBeenCalledWith('/api/auth/logout')
    })
  })

  describe('deleteAccount', () => {
    it('应调用 DELETE /api/auth/delete-account', async () => {
      mocks.delete.mockResolvedValue({ data: { code: 200 } })

      await deleteAccount()

      expect(mocks.delete).toHaveBeenCalledWith('/api/auth/delete-account')
    })
  })

  describe('upgradeToVip', () => {
    it('应调用 POST /api/auth/upgrade-vip', async () => {
      mocks.post.mockResolvedValue({ data: { code: 200 } })

      await upgradeToVip()

      expect(mocks.post).toHaveBeenCalledWith('/api/auth/upgrade-vip')
    })
  })
})

// ============================================================
// Axios Request Config 测试
// ============================================================
describe('Axios 实例配置', () => {
  it('应创建 axios 实例', () => {
    expect(request).toBeDefined()
    expect(request.post).toBeDefined()
    expect(request.get).toBeDefined()
  })

  // 拦截器在模块加载时注册，通过模块被成功导入即可验证
  it('应成功导入 axios 实例', () => {
    expect(request).toBeDefined()
    expect(typeof request.post).toBe('function')
  })
})
