import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
// 使用普通对象 + getter，模拟 Pinia 的 ref 自动解包行为
const mockUser = { current: null }

vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    get user() { return mockUser.current },
    token: null,
    isAuthenticated: false,
  })),
}))

import permissionDirective from '@/directives/permission'

/**
 * v-permission 指令测试
 */
describe('v-permission 指令', () => {
  let mockElement

  beforeEach(() => {
    setActivePinia(createPinia())
    mockElement = document.createElement('div')
    mockElement.style.display = ''
    mockUser.current = null
  })

  describe('未登录用户', () => {
    it('字符串权限 — 应隐藏元素', () => {
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.style.display).toBe('none')
    })

    it('数组权限 — 应隐藏元素', () => {
      permissionDirective.mounted(mockElement, { value: ['knowledgeBaseUpload'] })
      expect(mockElement.style.display).toBe('none')
    })
  })

  describe('管理员用户', () => {
    beforeEach(() => {
      mockUser.current = { userId: '1', username: 'admin', role: 'ADMIN' }
    })

    it('所有权限都应显示元素', () => {
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.style.display).toBe('')

      permissionDirective.mounted(mockElement, { value: 'emailTool' })
      expect(mockElement.style.display).toBe('')

      permissionDirective.mounted(mockElement, { value: 'externalMcp' })
      expect(mockElement.style.display).toBe('')
    })

    it('对象格式 { roles: [...] } — 应显示元素', () => {
      permissionDirective.mounted(mockElement, { value: { roles: ['ADMIN'] } })
      expect(mockElement.style.display).toBe('')
    })

    it('对象格式 { allRoles: [...] } — 应显示元素', () => {
      permissionDirective.mounted(mockElement, { value: { allRoles: ['ADMIN'] } })
      expect(mockElement.style.display).toBe('')
    })
  })

  describe('普通用户', () => {
    beforeEach(() => {
      mockUser.current = { userId: '3', username: 'normal', role: 'USER' }
    })

    it('knowledgeBaseUpload — 应隐藏元素', () => {
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.style.display).toBe('none')
    })

    it('基本功能 — 应显示元素', () => {
      permissionDirective.mounted(mockElement, { value: 'chat' })
      expect(mockElement.style.display).toBe('')
    })

    it('对象格式 { permission: "emailTool" } — 应隐藏元素', () => {
      permissionDirective.mounted(mockElement, { value: { permission: 'emailTool' } })
      expect(mockElement.style.display).toBe('none')
    })

    it('对象格式 { any: [...] } 包含无权限 — 应隐藏', () => {
      permissionDirective.mounted(mockElement, {
        value: { any: ['knowledgeBaseUpload', 'emailTool'] },
      })
      expect(mockElement.style.display).toBe('none')
    })

    it('对象格式 { any: [...] } 部分有权限 — 应显示', () => {
      permissionDirective.mounted(mockElement, {
        value: { any: ['chat', 'knowledgeBaseUpload'] },
      })
      expect(mockElement.style.display).toBe('')
    })

    it('对象格式 { all: [...] } 包含无权限 — 应隐藏', () => {
      permissionDirective.mounted(mockElement, {
        value: { all: ['chat', 'knowledgeBaseUpload'] },
      })
      expect(mockElement.style.display).toBe('none')
    })
  })

  describe('updated 钩子', () => {
    it('应正确响应 binding 更新', () => {
      mockUser.current = { userId: '1', username: 'admin', role: 'ADMIN' }

      // 先挂载
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.style.display).toBe('')

      // 更新为新权限
      permissionDirective.updated(mockElement, { value: 'emailTool' })
      expect(mockElement.style.display).toBe('')
    })
  })

  describe('元素标记', () => {
    it('隐藏时应设置 v-permission-hidden 属性', () => {
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.getAttribute('v-permission-hidden')).toBe('true')
    })

    it('显示时应移除 v-permission-hidden 属性', () => {
      mockUser.current = { userId: '1', username: 'admin', role: 'ADMIN' }

      mockElement.setAttribute('v-permission-hidden', 'true')
      permissionDirective.mounted(mockElement, { value: 'knowledgeBaseUpload' })
      expect(mockElement.hasAttribute('v-permission-hidden')).toBe(false)
    })
  })
})
