import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Use vi.hoisted() so mock variables are accessible before hoisted vi.mock() calls
const mocks = vi.hoisted(() => ({
  storageData: /** @type {Record<string, any>} */ ({}),
  authUser: /** @type {any} */ (null),
}))

vi.mock('@/utils/helpers', () => ({
  secureStorage: {
    getItem: vi.fn((key) => mocks.storageData[key] ?? null),
    setItem: vi.fn((key, value) => { mocks.storageData[key] = value }),
    removeItem: vi.fn((key) => { delete mocks.storageData[key] }),
  },
  generateId: vi.fn(() => {
    const key = '__gen_counter__'
    const count = (mocks.storageData[key] || 0) + 1
    mocks.storageData[key] = count
    return 'id_' + Date.now() + '_' + count
  }),
}))

vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    user: mocks.authUser,
  })),
}))

import { useChatStore } from '@/stores/chat'

// ============================================================
// useChatStore 测试
// ============================================================
describe('useChatStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    mocks.storageData = {}
    mocks.authUser = null
  })

  describe('初始状态', () => {
    it('messages 应为空数组', () => {
      const store = useChatStore()
      expect(store.messages).toEqual([])
    })

    it('conversations 应为空数组', () => {
      const store = useChatStore()
      expect(store.conversations).toEqual([])
    })

    it('currentConversationId 应为 null', () => {
      const store = useChatStore()
      expect(store.currentConversationId).toBeNull()
    })

    it('webSearchEnabled 应为 false', () => {
      const store = useChatStore()
      expect(store.webSearchEnabled).toBe(false)
    })

    it('ragSearchEnabled 应为 false', () => {
      const store = useChatStore()
      expect(store.ragSearchEnabled).toBe(false)
    })

    it('hasKnowledgeBase 应为 false', () => {
      const store = useChatStore()
      expect(store.hasKnowledgeBase).toBe(false)
    })

    it('isLoading 应为 false', () => {
      const store = useChatStore()
      expect(store.isLoading).toBe(false)
    })
  })

  describe('userId 管理', () => {
    it('已登录用户应从 authStore 获取 userId（通过 user.id）', () => {
      mocks.authUser = { id: 'auth_user_001' }
      const store = useChatStore()

      expect(store.userId).toBe('auth_user_001')
    })

    it('未登录用户应从 storage 恢复 userId', () => {
      mocks.storageData = { chat_user_id: 'stored_user_abc' }
      const store = useChatStore()

      expect(store.userId).toBe('stored_user_abc')
    })

    it('无任何 userId 来源时应生成新 ID', () => {
      const store = useChatStore()

      expect(store.userId).toBeTruthy()
      expect(typeof store.userId).toBe('string')
    })

    it('updateUserId 应更新并持久化', () => {
      const store = useChatStore()
      store.updateUserId('new_user_789')

      expect(store.userId).toBe('new_user_789')
      expect(mocks.storageData.chat_user_id).toBe('new_user_789')
    })

    it('updateUserId(null) 应清除存储的 userId', () => {
      mocks.storageData.chat_user_id = 'old_id'
      const store = useChatStore()

      store.updateUserId(null)

      expect(store.userId).toBeNull()
      expect(mocks.storageData.chat_user_id).toBeUndefined()
    })
  })

  describe('ensureUserId', () => {
    it('userId 存在时不应重新生成', () => {
      mocks.storageData.chat_user_id = 'existing_id'
      const store = useChatStore()
      const originalId = store.userId

      store.ensureUserId()
      expect(store.userId).toBe(originalId)
    })

    it('userId 为 null 时应生成新 ID', () => {
      const store = useChatStore()
      store.updateUserId(null)

      store.ensureUserId()
      expect(store.userId).toBeTruthy()
    })
  })

  describe('消息管理', () => {
    describe('addUserMessage', () => {
      it('应添加用户消息到 messages 数组', () => {
        const store = useChatStore()
        store.addUserMessage('你好，AI！')

        expect(store.messages).toHaveLength(1)
        expect(store.messages[0].role).toBe('user')
        expect(store.messages[0].content).toBe('你好，AI！')
        expect(store.messages[0]).toHaveProperty('id')
        expect(store.messages[0]).toHaveProperty('timestamp')
        expect(store.messages[0].isFailed).toBe(false)
      })

      it('应支持标记失败消息', () => {
        const store = useChatStore()
        store.addUserMessage('失败的消息', true)

        expect(store.messages[0].isFailed).toBe(true)
      })

      it('每条消息应有唯一 ID', () => {
        const store = useChatStore()
        store.addUserMessage('msg1')
        store.addUserMessage('msg2')
        store.addUserMessage('msg3')

        const ids = store.messages.map((m) => m.id)
        const uniqueIds = new Set(ids)
        expect(uniqueIds.size).toBe(3)
      })
    })

    describe('addAssistantMessage', () => {
      it('应添加 AI 回复消息', () => {
        const store = useChatStore()
        const msgId = store.addAssistantMessage('你好！我是AI助手。')

        expect(store.messages).toHaveLength(1)
        expect(store.messages[0].role).toBe('assistant')
        expect(store.messages[0].content).toBe('你好！我是AI助手。')
        expect(msgId).toBeTruthy()
      })

      it('应支持指定消息 ID', () => {
        const store = useChatStore()
        const msgId = store.addAssistantMessage('回复', 'custom_msg_id')

        expect(msgId).toBe('custom_msg_id')
        expect(store.messages[0].id).toBe('custom_msg_id')
      })
    })

    describe('updateAssistantMessage', () => {
      it('应更新指定消息的内容（流式追加）', () => {
        const store = useChatStore()
        const msgId = store.addAssistantMessage('Hel')

        store.updateAssistantMessage(msgId, 'Hello')
        expect(store.messages[0].content).toBe('Hello')

        store.updateAssistantMessage(msgId, 'Hello, World!')
        expect(store.messages[0].content).toBe('Hello, World!')
      })

      it('不存在的消息 ID 不应报错', () => {
        const store = useChatStore()
        expect(() => store.updateAssistantMessage('nonexistent', 'text')).not.toThrow()
      })
    })

    describe('updateMessageFailed', () => {
      it('应标记消息为失败', () => {
        const store = useChatStore()
        const msgId = store.addAssistantMessage('正在生成...')

        store.updateMessageFailed(msgId, true)
        expect(store.messages[0].isFailed).toBe(true)
      })

      it('应能取消失败标记', () => {
        const store = useChatStore()
        store.addUserMessage('消息', true)
        const msgId = store.messages[0].id

        store.updateMessageFailed(msgId, false)
        expect(store.messages[0].isFailed).toBe(false)
      })
    })

    describe('resendMessage', () => {
      it('应清除失败标记并返回消息内容', () => {
        const store = useChatStore()
        store.addUserMessage('重新发送的消息', true)
        const msgId = store.messages[0].id

        const content = store.resendMessage(msgId)

        expect(content).toBe('重新发送的消息')
        expect(store.messages[0].isFailed).toBe(false)
      })

      it('消息不存在时应返回 null', () => {
        const store = useChatStore()
        expect(store.resendMessage('nonexistent')).toBeNull()
      })
    })

    describe('clearMessages', () => {
      it('应清空所有消息', () => {
        const store = useChatStore()
        store.addUserMessage('msg1')
        store.addAssistantMessage('msg2')
        store.addUserMessage('msg3')

        expect(store.messages).toHaveLength(3)

        store.clearMessages()
        expect(store.messages).toHaveLength(0)
      })
    })
  })

  describe('搜索模式切换', () => {
    describe('toggleWebSearch', () => {
      it('应切换联网搜索状态', () => {
        const store = useChatStore()
        expect(store.webSearchEnabled).toBe(false)

        store.toggleWebSearch()
        expect(store.webSearchEnabled).toBe(true)

        store.toggleWebSearch()
        expect(store.webSearchEnabled).toBe(false)
      })

      it('开启联网搜索应关闭 RAG 搜索', () => {
        const store = useChatStore()
        store.ragSearchEnabled = true

        store.toggleWebSearch()

        expect(store.webSearchEnabled).toBe(true)
        expect(store.ragSearchEnabled).toBe(false)
      })
    })

    describe('toggleRagSearch', () => {
      it('应切换 RAG 搜索状态', () => {
        const store = useChatStore()
        expect(store.ragSearchEnabled).toBe(false)

        store.toggleRagSearch()
        expect(store.ragSearchEnabled).toBe(true)

        store.toggleRagSearch()
        expect(store.ragSearchEnabled).toBe(false)
      })

      it('开启 RAG 搜索应关闭联网搜索', () => {
        const store = useChatStore()
        store.webSearchEnabled = true

        store.toggleRagSearch()

        expect(store.ragSearchEnabled).toBe(true)
        expect(store.webSearchEnabled).toBe(false)
      })
    })
  })

  describe('hasKnowledgeBase', () => {
    it('setHasKnowledgeBase 应正确设置', () => {
      const store = useChatStore()
      store.setHasKnowledgeBase(true)
      expect(store.hasKnowledgeBase).toBe(true)

      store.setHasKnowledgeBase(false)
      expect(store.hasKnowledgeBase).toBe(false)
    })
  })

  describe('会话管理', () => {
    it('setConversations 应设置会话列表', () => {
      const store = useChatStore()
      const list = [
        { id: 'c1', title: '对话1' },
        { id: 'c2', title: '对话2' },
      ]

      store.setConversations(list)
      expect(store.conversations).toEqual(list)
    })

    it('setCurrentConversationId 应设置当前会话ID', () => {
      const store = useChatStore()
      store.setCurrentConversationId('conv_123')
      expect(store.currentConversationId).toBe('conv_123')

      store.setCurrentConversationId(null)
      expect(store.currentConversationId).toBeNull()
    })

    it('updateConversationTitle 应更新会话标题', () => {
      const store = useChatStore()
      store.conversations = [
        { id: 'c1', title: '旧标题' },
        { id: 'c2', title: '其他' },
      ]

      store.updateConversationTitle('c1', '新标题')

      expect(store.conversations[0].title).toBe('新标题')
      expect(store.conversations[1].title).toBe('其他')
    })

    it('更新不存在的会话不应报错', () => {
      const store = useChatStore()
      expect(() => store.updateConversationTitle('ghost', 'title')).not.toThrow()
    })
  })

  describe('loadConversationMessages', () => {
    it('应加载历史消息并转换格式', () => {
      const store = useChatStore()
      const historyMessages = [
        {
          id: 'h1',
          role: 'user',
          content: '历史问题',
          createTime: '2025-01-15T10:00:00',
          isFailed: false,
        },
        {
          id: 'h2',
          role: 'assistant',
          content: '历史回答',
          createTime: '2025-01-15T10:00:05',
        },
      ]

      store.loadConversationMessages(historyMessages)

      expect(store.messages).toHaveLength(2)
      expect(store.messages[0].role).toBe('user')
      expect(store.messages[0].content).toBe('历史问题')
      expect(store.messages[1].role).toBe('assistant')
    })

    it('空数组应清空消息', () => {
      const store = useChatStore()
      store.addUserMessage('已有消息')

      store.loadConversationMessages([])

      expect(store.messages).toHaveLength(0)
    })
  })

  describe('isLoading', () => {
    it('应可切换加载状态', () => {
      const store = useChatStore()
      store.isLoading = true
      expect(store.isLoading).toBe(true)

      store.isLoading = false
      expect(store.isLoading).toBe(false)
    })
  })
})
