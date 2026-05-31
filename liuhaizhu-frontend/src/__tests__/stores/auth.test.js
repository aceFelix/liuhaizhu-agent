import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Use vi.hoisted() so mock variables are accessible before hoisted vi.mock() calls
const mocks = vi.hoisted(() => ({
  storageData: /** @type {Record<string, any>} */ ({}),
  loginMock: vi.fn(),
  registerMock: vi.fn(),
  refreshTokenMock: vi.fn(),
  getCurrentUserMock: vi.fn(),
  deleteAccountMock: vi.fn(),
  upgradeToVipMock: vi.fn(),
}))

vi.mock('@/utils/helpers', () => ({
  secureStorage: {
    getItem: vi.fn((key) => mocks.storageData[key] ?? null),
    setItem: vi.fn((key, value) => { mocks.storageData[key] = value }),
    removeItem: vi.fn((key) => { delete mocks.storageData[key] }),
  },
  generateId: vi.fn(() => 'id_test_' + Math.random().toString(36).substr(2, 9)),
}))

vi.mock('@/api/auth', () => ({
  login: (...args) => mocks.loginMock(...args),
  register: (...args) => mocks.registerMock(...args),
  refreshToken: (...args) => mocks.refreshTokenMock(...args),
  getCurrentUser: (...args) => mocks.getCurrentUserMock(...args),
  deleteAccount: (...args) => mocks.deleteAccountMock(...args),
  upgradeToVip: (...args) => mocks.upgradeToVipMock(...args),
}))

vi.mock('@/stores/chat', () => ({
  useChatStore: vi.fn(() => ({
    updateUserId: vi.fn(),
    clearMessages: vi.fn(),
    setConversations: vi.fn(),
    setCurrentConversationId: vi.fn(),
  })),
}))

import { useAuthStore } from '@/stores/auth'

// Helper to create API response
const apiResponse = (code, data, message = '') => ({
  data: { code, data, message },
})

// ============================================================
// useAuthStore 测试
// ============================================================
describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    mocks.storageData = {}
  })

  describe('初始状态', () => {
    it('token 应为 null（当 storage 为空时）', () => {
      const store = useAuthStore()
      expect(store.token).toBeNull()
    })

    it('refreshTokenValue 应为 null', () => {
      const store = useAuthStore()
      expect(store.refreshTokenValue).toBeNull()
    })

    it('user 应为 null', () => {
      const store = useAuthStore()
      expect(store.user).toBeNull()
    })

    it('isAuthenticated 应为 false', () => {
      const store = useAuthStore()
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('从 secureStorage 恢复状态', () => {
    it('storage 中有 token 时应恢复', () => {
      mocks.storageData = {
        auth_token: 'stored_access_token',
        auth_refresh_token: 'stored_refresh_token',
      }

      const store = useAuthStore()
      expect(store.token).toBe('stored_access_token')
      expect(store.refreshTokenValue).toBe('stored_refresh_token')
    })
  })

  describe('saveTokens', () => {
    it('应同时更新 state 和 storage', () => {
      const store = useAuthStore()
      store.saveTokens('new_access', 'new_refresh')

      expect(store.token).toBe('new_access')
      expect(store.refreshTokenValue).toBe('new_refresh')
      expect(mocks.storageData.auth_token).toBe('new_access')
      expect(mocks.storageData.auth_refresh_token).toBe('new_refresh')
    })
  })

  describe('clearTokens', () => {
    it('应清除所有状态和 storage', () => {
      const store = useAuthStore()
      store.token = 'existing_token'
      store.refreshTokenValue = 'existing_refresh'
      store.user = { id: 1, name: 'test' }
      store.isAuthenticated = true
      mocks.storageData.auth_token = 'existing_token'
      mocks.storageData.auth_refresh_token = 'existing_refresh'

      store.clearTokens()

      expect(store.token).toBeNull()
      expect(store.refreshTokenValue).toBeNull()
      expect(store.user).toBeNull()
      expect(store.isAuthenticated).toBe(false)
      expect(mocks.storageData.auth_token).toBeUndefined()
      expect(mocks.storageData.auth_refresh_token).toBeUndefined()
    })
  })

  describe('handleLogin', () => {
    it('登录成功应设置 token 和 user', async () => {
      mocks.loginMock.mockResolvedValue(apiResponse(200, {
        token: 'access_abc',
        refreshToken: 'refresh_abc',
        user: { userId: 'user123', username: 'alice', role: 'USER' },
      }))

      const store = useAuthStore()
      const result = await store.handleLogin({ username: 'alice', password: 'pass123' })

      expect(result.success).toBe(true)
      expect(store.token).toBe('access_abc')
      expect(store.refreshTokenValue).toBe('refresh_abc')
      expect(store.user).toEqual({ userId: 'user123', username: 'alice', role: 'USER' })
      expect(store.isAuthenticated).toBe(true)
    })

    it('登录失败应返回失败结果', async () => {
      mocks.loginMock.mockResolvedValue(apiResponse(401, null, '密码错误'))

      const store = useAuthStore()
      const result = await store.handleLogin({ username: 'alice', password: 'wrong' })

      expect(result.success).toBe(false)
      expect(result.message).toBe('密码错误')
      expect(store.isAuthenticated).toBe(false)
    })

    it('网络错误应捕获并返回失败', async () => {
      mocks.loginMock.mockRejectedValue(new Error('网络连接失败'))

      const store = useAuthStore()
      const result = await store.handleLogin({ username: 'alice', password: 'pass' })

      expect(result.success).toBe(false)
      expect(result.message).toContain('网络连接失败')
    })

    it('返回数据缺少 userId 字段时，应从 id 字段读取', async () => {
      mocks.loginMock.mockResolvedValue(apiResponse(200, {
        token: 'access_xyz',
        refreshToken: 'refresh_xyz',
        user: { id: 'user456', username: 'bob', role: 'VIP' },
      }))

      const store = useAuthStore()
      await store.handleLogin({ username: 'bob', password: 'pass' })

      expect(store.user.id).toBe('user456')
    })
  })

  describe('handleRegister', () => {
    it('注册成功应设置认证状态', async () => {
      mocks.registerMock.mockResolvedValue(apiResponse(200, {
        token: 'reg_token',
        refreshToken: 'reg_refresh',
        user: { userId: 'newUser', username: 'newUser', role: 'USER' },
      }))

      const store = useAuthStore()
      const result = await store.handleRegister({
        username: 'newUser',
        password: 'pass123',
        registerMode: 'username',
      })

      expect(result.success).toBe(true)
      expect(store.isAuthenticated).toBe(true)
      expect(store.token).toBe('reg_token')
    })

    it('注册失败应返回错误消息', async () => {
      mocks.registerMock.mockResolvedValue(apiResponse(400, null, '用户名已存在'))

      const store = useAuthStore()
      const result = await store.handleRegister({
        username: 'existing',
        password: 'pass123',
        registerMode: 'username',
      })

      expect(result.success).toBe(false)
      expect(result.message).toBe('用户名已存在')
    })
  })

  describe('handleRefreshToken', () => {
    it('refreshToken 为空时应直接返回失败', async () => {
      const store = useAuthStore()
      store.refreshTokenValue = null

      const result = await store.handleRefreshToken()
      expect(result.success).toBe(false)
      expect(mocks.refreshTokenMock).not.toHaveBeenCalled()
    })

    it('刷新成功应更新 token', async () => {
      const store = useAuthStore()
      store.refreshTokenValue = 'old_refresh'

      mocks.refreshTokenMock.mockResolvedValue(apiResponse(200, {
        token: 'new_access',
        refreshToken: 'new_refresh',
        user: { userId: 'user123', username: 'alice', role: 'USER' },
      }))

      const result = await store.handleRefreshToken()

      expect(result.success).toBe(true)
      expect(store.token).toBe('new_access')
      expect(store.refreshTokenValue).toBe('new_refresh')
      expect(store.isAuthenticated).toBe(true)
    })

    it('刷新失败应清除 token', async () => {
      const store = useAuthStore()
      store.token = 'old_token'
      store.refreshTokenValue = 'old_refresh'
      store.isAuthenticated = true

      mocks.refreshTokenMock.mockResolvedValue(apiResponse(401, null, 'token已过期'))

      const result = await store.handleRefreshToken()

      expect(result.success).toBe(false)
      expect(store.token).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('handleLogout', () => {
    it('应清除所有认证状态', () => {
      const store = useAuthStore()
      store.token = 'active_token'
      store.user = { id: 1 }
      store.isAuthenticated = true

      store.handleLogout()

      expect(store.token).toBeNull()
      expect(store.user).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('handleDeleteAccount', () => {
    it('注销成功应清除状态', async () => {
      mocks.deleteAccountMock.mockResolvedValue(apiResponse(200, null))

      const store = useAuthStore()
      store.token = 'token'
      store.isAuthenticated = true

      const result = await store.handleDeleteAccount()

      expect(result.success).toBe(true)
      expect(store.token).toBeNull()
    })

    it('注销失败应返回错误', async () => {
      mocks.deleteAccountMock.mockResolvedValue(apiResponse(500, null, '服务器错误'))

      const store = useAuthStore()
      const result = await store.handleDeleteAccount()

      expect(result.success).toBe(false)
    })
  })

  describe('fetchCurrentUser', () => {
    it('token 为空应返回失败', async () => {
      const store = useAuthStore()
      store.token = null

      const result = await store.fetchCurrentUser()
      expect(result.success).toBe(false)
      expect(mocks.getCurrentUserMock).not.toHaveBeenCalled()
    })

    it('获取成功应设置 user', async () => {
      const store = useAuthStore()
      store.token = 'valid_token'

      mocks.getCurrentUserMock.mockResolvedValue(apiResponse(200, {
        userId: 'user123',
        username: 'alice',
        role: 'VIP',
      }))

      const result = await store.fetchCurrentUser()

      expect(result.success).toBe(true)
      expect(store.user.username).toBe('alice')
      expect(store.isAuthenticated).toBe(true)
    })

    it('获取失败应清除 token', async () => {
      const store = useAuthStore()
      store.token = 'expired_token'
      store.isAuthenticated = true

      mocks.getCurrentUserMock.mockResolvedValue(apiResponse(401, null))

      const result = await store.fetchCurrentUser()

      expect(result.success).toBe(false)
      expect(store.token).toBeNull()
    })
  })

  describe('checkAuth', () => {
    it('已认证且有用户信息应直接返回 true', async () => {
      const store = useAuthStore()
      store.isAuthenticated = true
      store.user = { userId: '1' }

      const result = await store.checkAuth()
      expect(result).toBe(true)
    })

    it('有 token 但未认证应尝试获取用户信息', async () => {
      const store = useAuthStore()
      store.token = 'existing_token'
      store.isAuthenticated = false

      mocks.getCurrentUserMock.mockResolvedValue(apiResponse(200, {
        userId: '1', username: 'test', role: 'USER',
      }))

      const result = await store.checkAuth()
      expect(result).toBe(true)
      expect(mocks.getCurrentUserMock).toHaveBeenCalled()
    })

    it('无 token 应直接返回 false', async () => {
      const store = useAuthStore()
      store.token = null

      const result = await store.checkAuth()
      expect(result).toBe(false)
    })
  })

  describe('upgradeToVip', () => {
    it('升级成功应更新 user.role', async () => {
      mocks.upgradeToVipMock.mockResolvedValue(apiResponse(200, null, '升级成功'))

      const store = useAuthStore()
      store.user = { userId: '1', username: 'test', role: 'USER' }

      const result = await store.upgradeToVip()

      expect(result.success).toBe(true)
      expect(store.user.role).toBe('VIP')
    })

    it('升级失败应返回错误消息', async () => {
      mocks.upgradeToVipMock.mockResolvedValue(apiResponse(400, null, '已经是VIP'))

      const store = useAuthStore()
      store.user = { userId: '1', username: 'test', role: 'VIP' }

      const result = await store.upgradeToVip()

      expect(result.success).toBe(false)
      expect(result.message).toBe('已经是VIP')
    })
  })
})
