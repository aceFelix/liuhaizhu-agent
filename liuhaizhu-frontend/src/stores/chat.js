import { defineStore } from 'pinia'
import { ref } from 'vue'
import { secureStorage, generateId } from '@/utils/helpers'
import { useAuthStore } from './auth'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const conversations = ref([])
  const currentConversationId = ref(null)
  const webSearchEnabled = ref(false)
  const ragSearchEnabled = ref(false)
  const hasKnowledgeBase = ref(false)

  const userId = ref(null)

  const isLoading = ref(false)

  const updateUserId = (newUserId) => {
    userId.value = newUserId
    if (newUserId) {
      secureStorage.setItem('chat_user_id', newUserId)
    } else {
      secureStorage.removeItem('chat_user_id')
    }
  }

  const initializeUserId = () => {
    const authStore = useAuthStore()

    if (authStore.user && authStore.user.id) {
      userId.value = authStore.user.id
      secureStorage.setItem('chat_user_id', authStore.user.id)
      return
    }

    const storedUserId = secureStorage.getItem('chat_user_id')
    if (storedUserId) {
      userId.value = storedUserId
    } else {
      userId.value = generateId()
      secureStorage.setItem('chat_user_id', userId.value)
    }
  }

  const ensureUserId = () => {
    if (!userId.value) {
      initializeUserId()
    }
  }

  initializeUserId()

  const addUserMessage = (content, isFailed = false) => {
    messages.value.push({
      id: generateId(),
      role: 'user',
      content,
      timestamp: new Date().toLocaleTimeString(),
      isFailed,
    })
  }

  const addAssistantMessage = (content, id) => {
    const msgId = id || generateId()
    messages.value.push({
      id: msgId,
      role: 'assistant',
      content,
      timestamp: new Date().toLocaleTimeString(),
    })
    return msgId
  }

  const updateAssistantMessage = (id, content) => {
    const msg = messages.value.find((m) => m.id === id)
    if (msg) {
      msg.content = content
    }
  }

  const updateMessageFailed = (id, failed = true) => {
    const msg = messages.value.find((m) => m.id === id)
    if (msg) {
      msg.isFailed = failed
    }
  }

  const resendMessage = (id) => {
    const msg = messages.value.find((m) => m.id === id)
    if (msg) {
      msg.isFailed = false
      return msg.content
    }
    return null
  }

  const toggleWebSearch = () => {
    webSearchEnabled.value = !webSearchEnabled.value
    if (webSearchEnabled.value) {
      ragSearchEnabled.value = false
    }
  }

  const toggleRagSearch = () => {
    ragSearchEnabled.value = !ragSearchEnabled.value
    if (ragSearchEnabled.value) {
      webSearchEnabled.value = false
    }
  }

  const setHasKnowledgeBase = (value) => {
    hasKnowledgeBase.value = value
  }

  const clearMessages = () => {
    messages.value = []
  }

  const setConversations = (list) => {
    conversations.value = list
  }

  const setCurrentConversationId = (id) => {
    currentConversationId.value = id
  }

  const loadConversationMessages = (messageList) => {
    messages.value = messageList.map((msg) => ({
      id: msg.id || generateId(),
      role: msg.role,
      content: msg.content,
      timestamp: new Date(msg.createTime).toLocaleTimeString(),
      isFailed: msg.isFailed || false,
    }))
  }

  const updateConversationTitle = (conversationId, title) => {
    const conv = conversations.value.find((c) => c.id === conversationId)
    if (conv) {
      conv.title = title
    }
  }

  return {
    messages,
    conversations,
    currentConversationId,
    webSearchEnabled,
    ragSearchEnabled,
    hasKnowledgeBase,
    userId,
    isLoading,
    updateUserId,
    ensureUserId,
    addUserMessage,
    addAssistantMessage,
    updateAssistantMessage,
    updateMessageFailed,
    resendMessage,
    toggleWebSearch,
    toggleRagSearch,
    setHasKnowledgeBase,
    clearMessages,
    setConversations,
    setCurrentConversationId,
    loadConversationMessages,
    updateConversationTitle,
  }
})
