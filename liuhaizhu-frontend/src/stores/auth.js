import { defineStore } from 'pinia'
import { ref } from 'vue'
import { secureStorage } from '@/utils/helpers'
import { login, register, refreshToken, getCurrentUser, deleteAccount, upgradeToVip as upgradeToVipApi } from '@/api/auth'
import { useChatStore } from './chat'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(secureStorage.getItem('auth_token') || null)
  const refreshTokenValue = ref(secureStorage.getItem('auth_refresh_token') || null)
  const user = ref(null)
  const isAuthenticated = ref(false)

  const saveTokens = (accessToken, refreshToken) => {
    token.value = accessToken
    refreshTokenValue.value = refreshToken
    secureStorage.setItem('auth_token', accessToken)
    secureStorage.setItem('auth_refresh_token', refreshToken)
  }

  const clearTokens = () => {
    token.value = null
    refreshTokenValue.value = null
    user.value = null
    isAuthenticated.value = false
    secureStorage.removeItem('auth_token')
    secureStorage.removeItem('auth_refresh_token')

    const chatStore = useChatStore()
    chatStore.updateUserId(null)
    chatStore.clearMessages()
    chatStore.setConversations([])
    chatStore.setCurrentConversationId(null)
  }

  const handleLogin = async (credentials) => {
    try {
      const response = await login(credentials)
      if (response.data.code === 200 && response.data.data) {
        const { token: accessToken, refreshToken, user: userData } = response.data.data
        saveTokens(accessToken, refreshToken)
        user.value = userData
        isAuthenticated.value = true

        const chatStore = useChatStore()
        // 后端返回的是 userId 字段，不是 id
        const userId = userData.userId || userData.id
        chatStore.updateUserId(userId)

        return { success: true, data: userData }
      } else {
        return { success: false, message: response.data.message || '登录失败' }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: error.message || '登录失败，请稍后重试' }
    }
  }

  const handleRegister = async (userData) => {
    try {
      const response = await register(userData)
      if (response.data.code === 200 && response.data.data) {
        const { token: accessToken, refreshToken, user: registeredUser } = response.data.data
        saveTokens(accessToken, refreshToken)
        user.value = registeredUser
        isAuthenticated.value = true

        const chatStore = useChatStore()
        // 后端返回的是 userId 字段，不是 id
        const userId = registeredUser.userId || registeredUser.id
        chatStore.updateUserId(userId)

        return { success: true, data: registeredUser }
      } else {
        return { success: false, message: response.data.message || '注册失败' }
      }
    } catch (error) {
      console.error('注册失败:', error)
      return { success: false, message: error.message || '注册失败，请稍后重试' }
    }
  }

  const handleRefreshToken = async () => {
    if (!refreshTokenValue.value) {
      return { success: false }
    }

    try {
      const response = await refreshToken(refreshTokenValue.value)
      if (response.data.code === 200 && response.data.data) {
        const { token: accessToken, refreshToken: newRefreshToken, user: userData } = response.data.data
        saveTokens(accessToken, newRefreshToken)
        user.value = userData
        isAuthenticated.value = true

        const chatStore = useChatStore()
        // 后端返回的是 userId 字段，不是 id
        const userId = userData.userId || userData.id
        chatStore.updateUserId(userId)

        return { success: true }
      } else {
        clearTokens()
        return { success: false }
      }
    } catch (error) {
      console.error('刷新token失败:', error)
      clearTokens()
      return { success: false }
    }
  }

  const handleLogout = () => {
    clearTokens()
  }

  const handleDeleteAccount = async () => {
    try {
      const response = await deleteAccount()
      if (response.data.code === 200) {
        clearTokens()
        return { success: true }
      } else {
        return { success: false, message: response.data.message || '账户注销失败' }
      }
    } catch (error) {
      console.error('账户注销失败:', error)
      return { success: false, message: error.message || '账户注销失败，请稍后重试' }
    }
  }

  const fetchCurrentUser = async () => {
    if (!token.value) {
      return { success: false }
    }

    try {
      const response = await getCurrentUser()
      if (response.data.code === 200 && response.data.data) {
        user.value = response.data.data
        isAuthenticated.value = true

        // 更新 chatStore 的 userId
        const chatStore = useChatStore()
        const userId = response.data.data.userId || response.data.data.id
        chatStore.updateUserId(userId)

        return { success: true, data: response.data.data }
      } else {
        clearTokens()
        return { success: false }
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      clearTokens()
      return { success: false }
    }
  }

  const checkAuth = async () => {
    if (isAuthenticated.value && user.value) {
      return true
    }

    if (token.value) {
      const result = await fetchCurrentUser()
      return result.success
    }

    return false
  }

  const upgradeToVip = async () => {
    try {
      const response = await upgradeToVipApi()
      if (response.data.code === 200) {
        if (user.value) {
          user.value = { ...user.value, role: 'VIP' }
        }
        return { success: true }
      } else {
        return { success: false, message: response.data.message || 'VIP升级失败' }
      }
    } catch (error) {
      console.error('VIP升级失败:', error)
      return { success: false, message: error.message || 'VIP升级失败，请稍后重试' }
    }
  }

  return {
    token,
    refreshTokenValue,
    user,
    isAuthenticated,
    saveTokens,
    clearTokens,
    handleLogin,
    handleRegister,
    handleRefreshToken,
    handleLogout,
    handleDeleteAccount,
    fetchCurrentUser,
    checkAuth,
    upgradeToVip,
  }
})
