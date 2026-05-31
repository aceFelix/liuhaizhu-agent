import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import { usePermission, RoleEnum } from '@/utils/permission'

// Mock secureStorage and API modules
vi.mock('@/utils/helpers', () => ({
  secureStorage: {
    getItem: vi.fn(() => null),
    setItem: vi.fn(),
    removeItem: vi.fn(),
  },
}))

vi.mock('@/api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  refreshToken: vi.fn(),
  getCurrentUser: vi.fn(),
  deleteAccount: vi.fn(),
  upgradeToVip: vi.fn(),
}))

// ============================================================
// usePermission 测试
// ============================================================
describe('usePermission', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('RoleEnum', () => {
    it('应包含三个角色', () => {
      expect(RoleEnum.ADMIN).toBe('ADMIN')
      expect(RoleEnum.VIP).toBe('VIP')
      expect(RoleEnum.USER).toBe('USER')
    })
  })

  describe('未登录用户（user 为 null）', () => {
    it('userRole 应默认为 USER', () => {
      const perm = usePermission()
      expect(perm.userRole.value).toBe(RoleEnum.USER)
    })

    it('isAdmin 应为 false', () => {
      const perm = usePermission()
      expect(perm.isAdmin.value).toBe(false)
    })

    it('isVip 应为 false', () => {
      const perm = usePermission()
      expect(perm.isVip.value).toBe(false)
    })

    it('isNormalUser 应为 true', () => {
      const perm = usePermission()
      expect(perm.isNormalUser.value).toBe(true)
    })
  })

  describe('管理员用户', () => {
    beforeEach(() => {
      const authStore = useAuthStore()
      authStore.user = { userId: '1', username: 'admin', role: 'ADMIN' }
    })

    it('isAdmin 应为 true', () => {
      const perm = usePermission()
      expect(perm.isAdmin.value).toBe(true)
    })

    it('isVip 应为 true（ADMIN 隐含 VIP 权限）', () => {
      const perm = usePermission()
      expect(perm.isVip.value).toBe(true)
    })

    it('hasPermission 对所有权限应返回 true', () => {
      const perm = usePermission()
      expect(perm.hasPermission('knowledgeBaseUpload')).toBe(true)
      expect(perm.hasPermission('emailTool')).toBe(true)
      expect(perm.hasPermission('externalMcp')).toBe(true)
      expect(perm.hasPermission('chat')).toBe(true)
    })

    it('hasAnyPermission 应返回 true', () => {
      const perm = usePermission()
      expect(perm.hasAnyPermission(['knowledgeBaseUpload', 'emailTool'])).toBe(true)
    })

    it('hasAllPermissions 应返回 true', () => {
      const perm = usePermission()
      expect(perm.hasAllPermissions(['knowledgeBaseUpload', 'emailTool', 'externalMcp'])).toBe(true)
    })
  })

  describe('VIP 用户', () => {
    beforeEach(() => {
      const authStore = useAuthStore()
      authStore.user = { userId: '2', username: 'vipuser', role: 'VIP' }
    })

    it('isVip 应为 true', () => {
      const perm = usePermission()
      expect(perm.isVip.value).toBe(true)
    })

    it('isAdmin 应为 false', () => {
      const perm = usePermission()
      expect(perm.isAdmin.value).toBe(false)
    })

    it('hasPermission 对所有权限应返回 true', () => {
      const perm = usePermission()
      expect(perm.hasPermission('knowledgeBaseUpload')).toBe(true)
      expect(perm.hasPermission('emailTool')).toBe(true)
      expect(perm.hasPermission('externalMcp')).toBe(true)
    })
  })

  describe('普通用户', () => {
    beforeEach(() => {
      const authStore = useAuthStore()
      authStore.user = { userId: '3', username: 'normal', role: 'USER' }
    })

    it('isNormalUser 应为 true', () => {
      const perm = usePermission()
      expect(perm.isNormalUser.value).toBe(true)
    })

    it('基础功能应有权限', () => {
      const perm = usePermission()
      expect(perm.hasPermission('chat')).toBe(true)
      expect(perm.hasPermission('basicSearch')).toBe(true)
    })

    it('knowledgeBaseUpload 应返回 false', () => {
      const perm = usePermission()
      expect(perm.hasPermission('knowledgeBaseUpload')).toBe(false)
    })

    it('emailTool 应返回 false', () => {
      const perm = usePermission()
      expect(perm.hasPermission('emailTool')).toBe(false)
    })

    it('externalMcp 应返回 false', () => {
      const perm = usePermission()
      expect(perm.hasPermission('externalMcp')).toBe(false)
    })

    it('hasAnyPermission 对无权限列表应返回 false', () => {
      const perm = usePermission()
      expect(perm.hasAnyPermission(['knowledgeBaseUpload', 'emailTool'])).toBe(false)
    })

    it('hasAnyPermission 部分有权限应返回 true', () => {
      const perm = usePermission()
      expect(perm.hasAnyPermission(['chat', 'knowledgeBaseUpload'])).toBe(true)
    })

    it('hasAllPermissions 包含无权限时应返回 false', () => {
      const perm = usePermission()
      expect(perm.hasAllPermissions(['chat', 'knowledgeBaseUpload'])).toBe(false)
    })
  })

  describe('hasAnyRole', () => {
    it('用户角色在列表中应返回 true', () => {
      const authStore = useAuthStore()
      authStore.user = { userId: '1', username: 'test', role: 'VIP' }

      const perm = usePermission()
      expect(perm.hasAnyRole(['ADMIN', 'VIP'])).toBe(true)
      expect(perm.hasAnyRole(['USER'])).toBe(false)
    })
  })

  describe('hasAllRoles', () => {
    it('检查所有角色匹配', () => {
      const authStore = useAuthStore()
      authStore.user = { userId: '1', username: 'test', role: 'ADMIN' }

      const perm = usePermission()
      expect(perm.hasAllRoles(['ADMIN'])).toBe(true)
      expect(perm.hasAllRoles(['VIP'])).toBe(false)
    })
  })

  describe('getRoleName', () => {
    it('应返回中文角色名', () => {
      const perm = usePermission()
      expect(perm.getRoleName('ADMIN')).toBe('管理员')
      expect(perm.getRoleName('VIP')).toBe('VIP用户')
      expect(perm.getRoleName('USER')).toBe('普通用户')
    })

    it('未知角色应返回"未知"', () => {
      const perm = usePermission()
      expect(perm.getRoleName('SUPER_ADMIN')).toBe('未知')
    })
  })
})
