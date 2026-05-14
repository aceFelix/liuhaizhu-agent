<template>
  <div class="chat-container" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <!-- 科幻背景效果 -->
    <CyberBackground />

    <!-- 左侧会话列表组件 -->
    <ConversationList ref="conversationListRef" @open-settings="showSettings = true" />

    <!-- 右侧主聊天区域 -->
    <div class="main-content">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-title">
          <!-- 桌面端显示 Logo -->
          <Logo class="desktop-only" :clickable="true" @click="handleBackToHome" />
          <!-- 移动端显示展开会话列表按钮 -->
          <button class="btn-toggle-sidebar mobile-only" @click="toggleConversationList" title="会话列表">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z"/>
            </svg>
          </button>
        </div>
        <div class="header-actions">
          <button @click="handleClearChat" class="btn-header" :disabled="messages.length === 0">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path
                d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"
              />
            </svg>
            <span>清空</span>
          </button>
          <UserInfoCard
            :user="authStore.user"
            :show-edit-hint="true"
            @logout="handleLogout"
            @delete-account="handleDeleteAccountSuccess"
            @edit-profile="openUserProfile"
            @login="showLoginModal = true"
          />
          <button @click="handleBackToHome" class="btn-header btn-back">
            <span>← 返回首页</span>
          </button>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messagesContainer" @scroll="handleScroll">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-icon">
            <div class="welcome-images">
              <LazyImage src="/wcnm/1.jpg" alt="" class="welcome-img-first" width="80px" height="80px" radius="8px 0 0 8px" object-fit="cover" :lazy="false" />
              <LazyImage src="/wcnm/2.jpg" alt="" class="welcome-img-middle" width="80px" height="80px" radius="0" object-fit="cover" :lazy="false" />
              <LazyImage src="/wcnm/3.jpg" alt="" class="welcome-img-middle" width="80px" height="80px" radius="0" object-fit="cover" :lazy="false" />
              <LazyImage src="/wcnm/4.jpg" alt="" class="welcome-img-last" width="80px" height="80px" radius="0 8px 8px 0" object-fit="cover" :lazy="false" />
            </div>
          </div>
          <h2>职业法师刘海柱！一款真正人性化的智能聊天助手！</h2>
          <p>黄胶鞋，七分裤，我是海柱你记住！</p>
          <p>Q Pizi，G Mazi，Z Fengzi，C Shazi，只有你想不到没有柱子做不到！</p>
          <div class="feature-tips">
            <div class="tip-item">
              <span class="tip-icon">🌐</span>
              <span>开启联网搜索，获取最新信息</span>
            </div>
            <div class="tip-item">
              <span class="tip-icon">📚</span>
              <span>开启知识库，基于你的知识库回答</span>
            </div>
          </div>
        </div>

        <div v-for="msg in messages" :key="msg.id" class="message-item" :class="[msg.role, { failed: msg.isFailed }]">
          <div class="message-avatar">
            <LazyImage
              v-if="msg.role === 'user'"
              src="/images/user-avatar.jpg"
              alt="User"
              width="100%"
              height="100%"
              radius="50%"
              :lazy="false"
            />
            <LazyImage
              v-else
              src="/images/ai-avatar.jpg"
              alt="AI"
              width="100%"
              height="100%"
              radius="50%"
              :lazy="false"
            />
          </div>
          <div class="message-content">
            <div class="message-text" v-html="escapeHtml(msg.content)"></div>
            <div class="message-actions">
              <span class="message-time">{{ msg.timestamp }}</span>
              <button @click="copyMessage(msg.content)" class="btn-copy" title="复制">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path
                    d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"
                  />
                </svg>
              </button>
              <button v-if="msg.isFailed" @click="resendMessage(msg.id)" class="btn-resend" title="重新发送">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M19 8l-4 4h3c0 3.31-2.69 6-6 6-1.01 0-1.97-.25-2.8-.7l-1.46 1.46C8.97 19.54 10.43 20 12 20c4.42 0 8-3.58 8-8h3l-4-4zM6 12c0-3.31 2.69-6 6-6 1.01 0 1.97.25 2.8.7l1.46-1.46C15.03 4.46 13.57 4 12 4c-4.42 0-8 3.58-8 8H1l4 4 4-4H6z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <div v-if="isLoading && !isStreaming" class="message-item assistant">
          <div class="message-avatar">
            <LazyImage
              src="/images/ai-avatar.jpg"
              alt="AI"
              width="100%"
              height="100%"
              radius="50%"
              :lazy="false"
            />
          </div>
          <div class="message-content">
            <div class="thinking-bubble">
              <span class="thinking-text">柱子思考中</span>
              <span class="dot">.</span>
              <span class="dot">.</span>
              <span class="dot">.</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能开关区域 -->
      <div class="chat-features">
        <div class="feature-item">
          <label class="cyber-checkbox">
            <input type="checkbox" :checked="webSearchEnabled" @change="toggleWebSearch" />
            <span class="checkbox-custom"></span>
            <span class="feature-icon">🌐</span>
            <span>联网搜索</span>
          </label>
        </div>
        <div class="feature-item">
          <label class="cyber-checkbox">
            <input type="checkbox" :checked="ragSearchEnabled" @change="toggleRagSearch" />
            <span class="checkbox-custom"></span>
            <span class="feature-icon">📚</span>
            <span>知识库搜索</span>
          </label>
        </div>
        <div class="hud-status">
          <span class="status-dot"></span>
          <span>SYS:ONLINE</span>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <div class="input-wrapper">
          <!-- 已上传文件显示 -->
          <div v-if="uploadedFile" class="uploaded-file">
            <div class="file-info">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
              </svg>
              <span class="file-name">{{ uploadedFile.name }}</span>
              <span class="file-size">({{ formatFileSize(uploadedFile.size) }})</span>
            </div>
            <button @click="removeUploadedFile" class="btn-remove-file" title="移除文件">
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>
          <textarea
            v-model="inputMessage"
            @keydown.enter.exact.prevent="handleSendMessage"
            placeholder="输入消息... (Enter发送, Shift+Enter换行)"
            class="message-input"
            rows="3"
            :maxlength="MAX_MESSAGE_LENGTH"
          ></textarea>
          <div class="input-footer">
            <div class="char-count" :class="{ warning: messageLengthPercent > 80 }">
              {{ messageLength }}/{{ MAX_MESSAGE_LENGTH }}
            </div>
          </div>
        </div>
        <div class="input-actions">
          <!-- 文件上传按钮 -->
          <input
            ref="fileInput"
            type="file"
            style="display: none"
            accept=".txt,.pdf,.doc,.docx,.ppt,.pptx"
            @change="handleFileSelect"
          />
          <button
            @click="triggerFileUpload"
            :disabled="isLoading || isUploading || uploadedFile"
            class="btn-upload"
            title="上传文件"
          >
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M16.5 6v11.5c0 2.21-1.79 4-4 4s-4-1.79-4-4V5c0-1.38 1.12-2.5 2.5-2.5s2.5 1.12 2.5 2.5v10.5c0 .55-.45 1-1 1s-1-.45-1-1V6H10v9.5c0 1.38 1.12 2.5 2.5 2.5s2.5-1.12 2.5-2.5V5c0-2.21-1.79-4-4-4S7 2.79 7 5v12.5c0 3.04 2.46 5.5 5.5 5.5s5.5-2.46 5.5-5.5V6h-1.5z"/>
            </svg>
          </button>
          <button
            @click="handleSendMessage"
            :disabled="(!inputMessage.trim() && !uploadedFile) || isLoading || isUploading || isConnectingSSE"
            class="btn-send"
          >
            <span v-if="isConnectingSSE">连接中...</span>
            <span v-else-if="!isLoading && !isUploading">发送</span>
            <span v-else-if="isUploading">上传中...</span>
            <span v-else>发送中...</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </div>

    <!-- 设置弹窗组件 -->
    <SettingsModal v-model:visible="showSettings" />

    <!-- 登录弹窗组件 -->
    <LoginModal
      v-model:showLogin="showLoginModal"
      v-model:showReset="showResetPassword"
      @login-success="onLoginSuccess"
    />

    <!-- 用户信息编辑弹窗 -->
    <UserProfileModal
      v-model:visible="showUserProfile"
      :user="authStore.user"
      @success="onProfileUpdateSuccess"
    />

  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import {
  doChat,
  webSearch,
  ragSearch,
  connectSSE,
  fileChat,
} from '@/api/chat'
import {
  createConversation,
} from '@/api/conversation'
import { getRagDocumentList } from '@/api/rag'
import { ElMessage, ElMessageBox } from 'element-plus'

import ConversationList from '@/components/chating/ConversationList.vue'
import SettingsModal from '@/components/chating/SettingsModal.vue'
import LoginModal from '@/components/common/LoginModal.vue'
import UserProfileModal from '@/components/user/UserProfileModal.vue'
import UserInfoCard from '@/components/user/UserInfoCard.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import LazyImage from '@/components/common/LazyImage.vue'
import { useSettingsStore } from '@/stores/settings'
import { useTransitionStore } from '@/stores/transition'
import { usePermission } from '@/utils/permission'
import { useRouter } from 'vue-router'

const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const transitionStore = useTransitionStore()
const router = useRouter()
const { isVip, isAdmin } = usePermission()

const chatStore = useChatStore()
const {
  messages,
  currentConversationId,
  webSearchEnabled,
  ragSearchEnabled,
  hasKnowledgeBase,
  userId,
  isLoading,
} = storeToRefs(chatStore)

const conversationListRef = ref(null)
const messagesContainer = ref(null)

const inputMessage = ref('')
const showSettings = ref(false)
const showLoginModal = ref(false)
const showResetPassword = ref(false)
const showUserProfile = ref(false)
const isStreaming = ref(false)
const isUploading = ref(false)
const isConnectingSSE = ref(false) // SSE连接中状态
const uploadedFile = ref(null)
const fileInput = ref(null)

let sseConnection = null
let sseReconnectTimer = null
let sseHeartbeatTimer = null
let reconnectAttempts = 0
const MAX_RECONNECT_ATTEMPTS = 5
const HEARTBEAT_INTERVAL = 30000 // 30秒心跳检测

// 记录正在回复的会话ID
let activeConversationId = null

const MAX_MESSAGE_LENGTH = 10000

const messageLength = computed(() => inputMessage.value.length)
const messageLengthPercent = computed(() => (messageLength.value / MAX_MESSAGE_LENGTH) * 100)

const escapeHtml = (text) => {
  if (!text) return ''
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

onMounted(async () => {
  settingsStore.initTheme()

  await authStore.checkAuth()

  if (authStore.user) {
    // 后端返回的是 userId 字段，不是 id
    const uid = authStore.user.userId || authStore.user.id
    if (uid) {
      chatStore.updateUserId(uid)
      // 知识库检查不阻塞SSE连接建立（并行执行）
      checkUserKnowledgeBase(uid)
    }
  } else {
    // 未登录用户也需要确保userId初始化（匿名ID）
    chatStore.ensureUserId()
  }

  // userId就绪后立即初始化SSE连接（不等待其他网络请求）
  if (userId.value) {
    initSSE().catch(() => {})
  }
  conversationListRef.value?.loadConversations()
  loadPersistedMessages()
})

// 检查用户是否有知识库
const checkUserKnowledgeBase = async (uid) => {
  try {
    const response = await getRagDocumentList(uid)
    if (response.data.code === 200 && response.data.data) {
      chatStore.setHasKnowledgeBase(response.data.data.length > 0)
    } else {
      chatStore.setHasKnowledgeBase(false)
    }
  } catch (error) {
    console.error('检查知识库状态失败:', error)
    chatStore.setHasKnowledgeBase(false)
  }
}

watch(userId, (newUserId, oldUserId) => {
  if (newUserId !== oldUserId) {
    console.log('userId变化，重新初始化SSE连接:', oldUserId, '->', newUserId)
    cleanupSSE()
    initSSE().catch(() => {})
    conversationListRef.value?.loadConversations()
    chatStore.clearMessages()
    chatStore.setCurrentConversationId(null)
    // 重新检查知识库状态
    if (newUserId) {
      checkUserKnowledgeBase(newUserId)
    } else {
      chatStore.setHasKnowledgeBase(false)
    }
  }
})

onBeforeUnmount(() => {
  cleanupSSE()
})

const cleanupSSE = () => {
  if (sseHeartbeatTimer) {
    clearInterval(sseHeartbeatTimer)
    sseHeartbeatTimer = null
  }
  if (sseReconnectTimer) {
    clearTimeout(sseReconnectTimer)
    sseReconnectTimer = null
  }
  if (sseConnection) {
    sseConnection.close()
    sseConnection = null
  }
}

// 检查SSE连接状态
const isSSEConnected = () => {
  return sseConnection && sseConnection.readyState === EventSource.OPEN
}

// 确保SSE连接可用并等待连接就绪（自动重试，最多30秒）
const ensureSSEConnection = async () => {
  if (isSSEConnected()) {
    return true
  }

  if (!userId.value) {
    chatStore.ensureUserId()
    if (!userId.value) {
      console.error('用户ID未初始化，无法建立SSE连接')
      return false
    }
  }

  // 自动重试机制：最多尝试3次，每次10秒超时
  const MAX_RETRIES = 3
  for (let attempt = 0; attempt < MAX_RETRIES; attempt++) {
    isConnectingSSE.value = true
    console.log(`SSE连接中... (第${attempt + 1}/${MAX_RETRIES}次)`)

    try {
      await initSSE()
      console.log('SSE连接已就绪')
      return true
    } catch (error) {
      console.warn(`SSE连接第${attempt + 1}次失败:`, error.message)
      // 如果不是最后一次，短暂等待后重试
      if (attempt < MAX_RETRIES - 1) {
        await new Promise(resolve => setTimeout(resolve, 1000))
        reconnectAttempts = 0
      }
    }
  }

  isConnectingSSE.value = false
  return false
}

// 启动心跳检测
const startHeartbeat = () => {
  if (sseHeartbeatTimer) {
    clearInterval(sseHeartbeatTimer)
  }
  sseHeartbeatTimer = setInterval(() => {
    if (!isSSEConnected()) {
      console.log('心跳检测：SSE连接已断开，正在重连...')
      reconnectAttempts = 0
      initSSE().catch(() => {})
    }
  }, HEARTBEAT_INTERVAL)
}

// 初始化SSE连接，返回Promise，连接成功resolve，失败reject
const initSSE = () => {
  cleanupSSE()

  if (!userId.value) {
    console.log('userId为空，不建立SSE连接')
    return Promise.reject(new Error('userId为空'))
  }

  if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
    console.error('SSE重连次数超过限制')
    return Promise.reject(new Error('SSE重连次数超过限制'))
  }

  return new Promise((resolve, reject) => {
    try {
      sseConnection = connectSSE(userId.value, authStore.token)

      sseConnection.addEventListener('add', (event) => {
        // 只在当前会话与正在回复的会话匹配时更新消息
        // 注意：activeConversationId 可能为 null（新会话），此时应该允许更新
        if (activeConversationId !== null && activeConversationId !== currentConversationId.value) {
          return
        }
        const content = event.data
        isStreaming.value = true
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant') {
          lastMsg.content += content
        } else {
          const botMessageId = 'bot_' + Date.now()
          chatStore.addAssistantMessage(content, botMessageId)
        }
        nextTick(() => scrollToBottom())
      })

      sseConnection.addEventListener('finish', () => {
        // 只在当前会话与正在回复的会话匹配时更新状态
        if (activeConversationId === currentConversationId.value) {
          isLoading.value = false
          isStreaming.value = false
          userScrolledUp.value = false
          saveMessagesToStorage()
        }
        activeConversationId = null
        reconnectAttempts = 0
      })

      sseConnection.addEventListener('title_update', (event) => {
        try {
          const data = JSON.parse(event.data)
          if (data.conversationId && data.title) {
            chatStore.updateConversationTitle(data.conversationId, data.title)
          }
        } catch (error) {
          console.error('解析title_update事件失败:', error)
        }
      })

      sseConnection.addEventListener('error', (event) => {
        console.error('SSE收到错误事件:', event.data)
        isLoading.value = false
        isStreaming.value = false
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant' && !lastMsg.content) {
          lastMsg.content = '抱歉，AI服务暂时不可用，请稍后重试。'
        } else if (!lastMsg || lastMsg.role !== 'assistant') {
          chatStore.addAssistantMessage('抱歉，AI服务暂时不可用，请稍后重试。')
        }
        activeConversationId = null
      })

      sseConnection.addEventListener('connected', () => {
        clearTimeout(connectionTimeout)
        console.log('SSE收到connected确认事件，连接已就绪')
        reconnectAttempts = 0
        startHeartbeat()
        resolve()
      })

      // 连接超时定时器
      const connectionTimeout = setTimeout(() => {
        reject(new Error('SSE连接超时'))
      }, 10000)

      sseConnection.onerror = () => {
        clearTimeout(connectionTimeout)
        console.error('SSE连接断开，3秒后自动重连...')
        isLoading.value = false
        reconnectAttempts++
        reject(new Error('SSE连接失败'))
        sseReconnectTimer = setTimeout(() => initSSE().catch(() => {}), 3000)
      }

      sseConnection.onopen = () => {
        clearTimeout(connectionTimeout)
        console.log('SSE连接成功(onopen)')
        reconnectAttempts = 0
        startHeartbeat()
        resolve() // 兜底resolve，如果connected事件已经resolve过则无效
      }
    } catch (error) {
      console.error('SSE连接初始化失败:', error)
      isLoading.value = false
      reject(error)
    }
  })
}

// 触发文件上传
const triggerFileUpload = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件类型
  const allowedTypes = [
    'text/plain',
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation'
  ]

  // 通过后缀名检查
  const allowedExtensions = ['.txt', '.pdf', '.doc', '.docx', '.ppt', '.pptx']
  const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()

  if (!allowedExtensions.includes(fileExtension)) {
    ElMessage.error('不支持的文件类型，请上传 TXT、PDF、Word 或 PPT 文件')
    event.target.value = ''
    return
  }

  // 检查文件大小（最大10MB）
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    event.target.value = ''
    return
  }

  uploadedFile.value = file
  ElMessage.success(`文件 "${file.name}" 已选择`)

  // 清空input，允许重复选择同一文件
  event.target.value = ''
}

// 移除已上传的文件
const removeUploadedFile = () => {
  uploadedFile.value = null
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleSendMessage = async (resendContent = null) => {
  // 防止传入事件对象，确保 resendContent 是字符串
  const trimmedMessage = typeof resendContent === 'string' ? resendContent : inputMessage.value.trim()

  // 如果没有消息且没有上传文件，不发送
  if ((!trimmedMessage && !uploadedFile.value) || isLoading.value) return

  if (trimmedMessage.length > MAX_MESSAGE_LENGTH) {
    ElMessage.warning(`消息长度不能超过${MAX_MESSAGE_LENGTH}个字符`)
    return
  }

  chatStore.ensureUserId()

  // 发送前确保SSE连接可用并等待就绪（自动重试，最多30秒）
  const sseReady = await ensureSSEConnection()
  isConnectingSSE.value = false
  if (!sseReady) {
    ElMessage.warning('SSE连接未就绪，请稍后重试')
    return
  }

  if (!userId.value) {
    ElMessage.error('用户ID未初始化，请刷新页面重试')
    return
  }

  if (typeof resendContent !== 'string') {
    inputMessage.value = ''
  }

  // 未登录用户不创建会话，直接发送消息
  if (!currentConversationId.value && authStore.isAuthenticated) {
    try {
      const response = await createConversation(userId.value)
      if (response.data.code === 200) {
        const newConvId = response.data.data
        chatStore.setCurrentConversationId(newConvId)
        await conversationListRef.value?.loadConversations()
      }
    } catch (error) {
      console.error('创建会话失败:', error)
      ElMessage.error(error.message || '创建会话失败')
      return
    }
  }

  // 如果有上传文件，显示文件信息在消息中
  const displayMessage = uploadedFile.value
    ? `[文件: ${uploadedFile.value.name}] ${trimmedMessage}`
    : trimmedMessage

  chatStore.addUserMessage(displayMessage)
  userScrolledUp.value = false // 发送新消息时重置
  await nextTick()
  scrollToBottom(true) // 强制滚动到底部

  isLoading.value = true
  isUploading.value = !!uploadedFile.value
  activeConversationId = currentConversationId.value // 记录当前回复的会话ID
  const botMessageId = 'bot_' + Date.now()
  const userMessageId = typeof resendContent === 'string' ? null : 'user_' + Date.now()

  try {
    // 如果有上传文件，使用文件聊天接口
    if (uploadedFile.value) {
      const formData = new FormData()
      formData.append('file', uploadedFile.value)
      formData.append('message', trimmedMessage || '请分析这个文件的内容')
      formData.append('userMessageId', userMessageId)
      formData.append('botMessageId', botMessageId)
      formData.append('userId', userId.value)
      if (currentConversationId.value) {
        formData.append('conversationId', currentConversationId.value)
      }

      await fileChat(formData)
      // 发送成功后清除上传的文件
      uploadedFile.value = null
    } else {
      const requestData = {
        userId: userId.value,
        message: trimmedMessage,
        botMessageId: botMessageId,
        conversationId: currentConversationId.value,
        userMessageId: userMessageId,
      }

      if (webSearchEnabled.value) {
        await webSearch(requestData)
      } else if (ragSearchEnabled.value && hasKnowledgeBase.value) {
        await ragSearch(requestData)
      } else {
        await doChat(requestData)
      }
    }

    await conversationListRef.value?.loadConversations()
  } catch (error) {
    console.error('发送消息失败:', error)
    chatStore.updateAssistantMessage(botMessageId, '抱歉，发生了错误，请稍后再试。')
    // 标记用户消息为失败状态
    const messages = chatStore.messages
    const failedMsg = messages.find(m => m.content === displayMessage && m.role === 'user')
    if (failedMsg) {
      chatStore.updateMessageFailed(failedMsg.id, true)
    }
    isLoading.value = false
    ElMessage.error(error.message || '发送消息失败')
  } finally {
    isUploading.value = false
  }
}

const handleClearChat = async () => {
  if (!currentConversationId.value) {
    chatStore.clearMessages()
    saveMessagesToStorage()
    return
  }

  try {
    await ElMessageBox.confirm('确定要清空当前会话的所有对话记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return // 用户取消
  }

  try {
    console.log('开始清空会话，参数:', {
      conversationId: currentConversationId.value,
      userId: userId.value
    })
    const { clearConversationMessages } = await import('@/api/chat')
    console.log('clearConversationMessages 函数:', clearConversationMessages)
    const response = await clearConversationMessages(currentConversationId.value, userId.value)
    console.log('清空会话响应:', response)
    if (response.data.code === 200) {
      chatStore.clearMessages()
      await conversationListRef.value?.loadConversations()
      saveMessagesToStorage()
      ElMessage.success('清空成功')
    }
  } catch (error) {
    console.error('清空会话消息失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      request: error.request,
      config: error.config
    })
    ElMessage.error(error.message || '清空失败')
  }
}

const toggleWebSearch = () => {
  chatStore.toggleWebSearch()
}

const toggleRagSearch = () => {
  chatStore.toggleRagSearch()
}

// 判断用户是否在底部附近（100px阈值）
const isNearBottom = () => {
  if (!messagesContainer.value) return true
  const { scrollTop, scrollHeight, clientHeight } = messagesContainer.value
  return scrollHeight - scrollTop - clientHeight < 100
}

// 用户是否主动向上滚动
const userScrolledUp = ref(false)

const scrollToBottom = (force = false) => {
  if (messagesContainer.value && (force || !userScrolledUp.value)) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听滚动事件
const handleScroll = () => {
  userScrolledUp.value = !isNearBottom()
}

const saveMessagesToStorage = () => {
  if (currentConversationId.value) {
    const key = `chat_messages_${currentConversationId.value}`
    localStorage.setItem(key, JSON.stringify(messages.value))
  }
}

const loadPersistedMessages = () => {
  if (currentConversationId.value) {
    const key = `chat_messages_${currentConversationId.value}`
    const saved = localStorage.getItem(key)
    if (saved) {
      try {
        const parsed = JSON.parse(saved)
        chatStore.loadConversationMessages(parsed)
      } catch (error) {
        console.error('加载持久化消息失败:', error)
      }
    }
  }
}

const copyMessage = (content) => {
  navigator.clipboard
    .writeText(content)
    .then(() => {
      ElMessage.success('已复制到剪贴板')
    })
    .catch(() => {
      ElMessage.error('复制失败')
    })
}

const resendMessage = (msgId) => {
  const content = chatStore.resendMessage(msgId)
  if (content) {
    handleSendMessage(content)
  }
}

const onLoginSuccess = () => {
  showLoginModal.value = false
  // 避免重复显示登录成功消息，LoginModal 中已经显示了
  // ElMessage.success('登录成功')

  // 后端返回的是 userId 字段，不是 id
  const userId = authStore.user?.userId || authStore.user?.id
  if (userId) {
    chatStore.updateUserId(userId)
  }

  cleanupSSE()
  initSSE().catch(() => {})

  conversationListRef.value?.loadConversations()
  chatStore.clearMessages()
  chatStore.setCurrentConversationId(null)
}

const handleLogout = () => {
  authStore.handleLogout()
  ElMessage.success('已退出登录')
}

const handleBackToHome = async () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

// 切换会话列表显示（移动端用）
const toggleConversationList = () => {
  conversationListRef.value?.toggleSidebar()
}

const openUserProfile = () => {
  showUserProfile.value = true
}

const onProfileUpdateSuccess = () => {
  // 用户信息已更新，可以在这里执行一些额外操作
  console.log('用户信息更新成功')
}

const handleDeleteAccountSuccess = async () => {
  // 账户注销成功后的处理
  ElMessage.success('账户已注销，所有数据已删除')
}
</script>

<style scoped>
/* 基础容器 */
.chat-container {
  display: flex;
  height: 100vh;
  background: #0a0a0f;
  color: #e0e0e0;
  position: relative;
  overflow: hidden;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 1100px;
  margin: 0 auto;
  width: 100%;
  position: relative;
  z-index: 1;
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: rgba(20, 20, 30, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.2);
  position: relative;
  z-index: 10;
}

.header-title {
  display: flex;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.btn-header {
  padding: 8px 14px;
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
}

.btn-header:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
  color: #fff;
}

.btn-header:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: rgba(139, 92, 246, 0.05);
  border-radius: 12px;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(139, 92, 246, 0.4);
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.2);
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
}

.profile-email {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 12px 0;
}

.profile-role {
  display: inline-block;
}

.role-badge {
  padding: 4px 12px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.profile-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 0 24px;
}

.profile-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 8px;
}

.profile-stats .stat-label {
  font-size: 12px;
  color: #6b7280;
}

.profile-stats .stat-value {
  font-size: 14px;
  color: #e0e0e0;
  font-weight: 500;
}

.profile-stats .stat-value.active {
  color: #10b981;
}

.btn-logout-full {
  width: 100%;
  padding: 12px 24px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-logout-full:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}

.btn-back {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border: none;
}

.btn-back:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.4);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: transparent;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
  height: 100%;
}

.welcome-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding: 8px;
  background: rgba(20, 20, 30, 0.6);
  border: 2px solid rgba(139, 92, 246, 0.4);
  border-radius: 16px;
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.3), inset 0 0 10px rgba(139, 92, 246, 0.1);
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
  transition: all 0.3s;
}

.welcome-icon:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

.welcome-images {
  display: flex;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
}

.welcome-images::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: welcome-shimmer 2s infinite;
  pointer-events: none;
  z-index: 1;
}

@keyframes welcome-shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

.welcome-images img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  position: relative;
  z-index: 0;
}

.welcome-images img:first-child {
  border-radius: 8px 0 0 8px;
}

.welcome-images img:last-child {
  border-radius: 0 8px 8px 0;
}

.welcome-images .lazy-image-container {
  width: 80px !important;
  height: 80px !important;
  min-width: 80px;
  min-height: 80px;
  max-width: 80px;
  max-height: 80px;
}

.welcome-images .lazy-image-container :deep(.lazy-image) {
  object-fit: cover;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.welcome-message h2 {
  font-size: 30px;
  margin-bottom: 20px;
  color: #fff;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-blue) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-message p {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 20px;
}

.feature-tips {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 12px;
  font-size: 14px;
  color: #e0e0e0;
  transition: all 0.3s;
}

.tip-item:hover {
  background: rgba(139, 92, 246, 0.15);
  border-color: rgba(139, 92, 246, 0.4);
  transform: translateY(-2px);
}

.tip-icon {
  font-size: 20px;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(139, 92, 246, 0.2);
  border: 2px solid rgba(139, 92, 246, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-avatar .lazy-image-container {
  width: 100% !important;
  height: 100% !important;
}

.message-avatar .lazy-image-container :deep(.lazy-image) {
  object-fit: cover;
}

.ai-avatar {
  font-size: 22px;
}

.message-content {
  max-width: 70%;
}

.message-item.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  padding: 14px 18px;
  border-radius: 16px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 14px;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #5b21b6 0%, #9120c9 100%);
  color: #fff;
  border-radius: 16px 16px 4px 16px;
}

.message-item.assistant .message-text {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(139, 92, 246, 0.2);
  color: #e0e0e0;
  border-radius: 16px 16px 16px 4px;
}

.message-time {
  margin-top: 6px;
  font-size: 11px;
  color: #6b7280;
}

.message-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.btn-copy {
  background: transparent;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-copy:hover {
  color: var(--color-primary);
  background: rgba(124, 58, 237, 0.1);
}

.btn-resend {
  background: transparent;
  border: none;
  color: #ef4444;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-resend:hover {
  color: #f87171;
  background: rgba(239, 68, 68, 0.1);
}

.message-item.failed .message-text {
  border: 2px solid #ef4444;
  animation: pulse-failed 2s infinite;
}

@keyframes pulse-failed {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4);
  }
  50% {
    box-shadow: 0 0 0 4px rgba(239, 68, 68, 0);
  }
}

/* 思考气泡 */
.thinking-bubble {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 14px 18px;
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px 16px 16px 4px;
}

.thinking-text {
  color: #a78bfa;
  font-size: 14px;
}

.thinking-bubble .dot {
  color: var(--color-primary);
  font-size: 18px;
  font-weight: bold;
  animation: blink 1.4s infinite;
}

.thinking-bubble .dot:nth-child(2) {
  animation-delay: 0.2s;
}
.thinking-bubble .dot:nth-child(3) {
  animation-delay: 0.4s;
}
.thinking-bubble .dot:nth-child(4) {
  animation-delay: 0.6s;
}

@keyframes blink {
  0%,
  60%,
  100% {
    opacity: 0.3;
  }
  30% {
    opacity: 1;
  }
}

/* 功能开关区域 */
.chat-features {
  display: flex;
  gap: 20px;
  padding: 12px 24px;
  background: rgba(15, 15, 25, 0.8);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(139, 92, 246, 0.15);
  align-items: center;
}

.feature-item {
  display: flex;
  align-items: center;
}

/* 科幻风格复选框 */
.cyber-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #9ca3af;
  transition: color 0.3s;
}

.cyber-checkbox:hover {
  color: #e0e0e0;
}

.cyber-checkbox input {
  display: none;
}

.checkbox-custom {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(139, 92, 246, 0.4);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.cyber-checkbox input:checked + .checkbox-custom {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: var(--color-primary);
}

.cyber-checkbox input:checked + .checkbox-custom::after {
  content: '✓';
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.feature-icon {
  font-size: 16px;
}

.hud-status {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: monospace;
  font-size: 11px;
  color: rgba(139, 92, 246, 0.6);
  letter-spacing: 1px;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
  animation: statusPulse 2s ease-in-out infinite;
}

@keyframes statusPulse {
  0%,
  100% {
    opacity: 1;
    box-shadow: 0 0 4px #10b981;
  }
  50% {
    opacity: 0.5;
    box-shadow: none;
  }
}

/* 输入区域 */
.chat-input-area {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  background: rgba(15, 15, 25, 0.9);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(139, 92, 246, 0.15);
}

.input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-input {
  flex: 1;
  padding: 14px 16px;
  background: var(--input-bg, rgba(30, 30, 50, 0.8));
  border: 1px solid var(--border-color, rgba(139, 92, 246, 0.3));
  border-radius: 12px;
  font-size: 14px;
  resize: none;
  font-family: inherit;
  transition: all 0.3s;
  color: var(--text-primary, #e0e0e0);
  min-height: 70px;
}

.message-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 20px rgba(124, 58, 237, 0.15);
}

.message-input::placeholder {
  color: #6b7280;
}

.input-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.char-count {
  font-size: 11px;
  color: #6b7280;
}

.char-count.warning {
  color: #f59e0b;
}

/* 已上传文件显示 */
.uploaded-file {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  margin-bottom: 8px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #a78bfa;
  font-size: 13px;
}

.file-name {
  font-weight: 500;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: #6b7280;
  font-size: 11px;
}

.btn-remove-file {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background: transparent;
  border: none;
  border-radius: 4px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-file:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

/* 输入区域操作按钮 */
.input-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-upload {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  color: #a78bfa;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-upload:hover:not(:disabled) {
  background: rgba(124, 58, 237, 0.2);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-upload:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-send {
  position: relative;
  padding: 14px 28px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  overflow: hidden;
}

.btn-send:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.btn-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(20, 20, 30, 0.5);
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.3);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 92, 246, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 12px 16px;
  }

  .chat-header h1 {
    font-size: 16px;
  }

  .header-actions {
    gap: 6px;
  }

  .btn-header {
    padding: 6px 10px;
    font-size: 12px;
  }

  .btn-back span {
    display: none;
  }

  .btn-back::before {
    content: '←';
  }

  .chat-messages {
    padding: 16px;
  }

  .welcome-message h2 {
    font-size: 22px;
  }

  .welcome-icon {
    font-size: 48px;
  }

  .feature-tips {
    flex-direction: column;
    gap: 10px;
  }

  .tip-item {
    padding: 12px 16px;
    font-size: 13px;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-features {
    padding: 10px 16px;
    flex-wrap: wrap;
    gap: 12px;
  }

  .hud-status {
    display: none;
  }

  .chat-input-area {
    padding: 12px 16px;
    flex-direction: column;
  }

  .message-input {
    min-height: 60px;
  }

  .btn-send {
    width: 100%;
    padding: 12px 24px;
  }

  .btn-upload {
    width: 44px;
    height: 44px;
  }

  .message-avatar {
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 480px) {

  .chat-header h1 {
    font-size: 14px;
  }

  .welcome-message h2 {
    font-size: 18px;
  }

  .message-content {
    max-width: 90%;
  }

  .welcome-icon {
    width: 240px;
    height: 70px;
    padding: 8px;
  }

  .welcome-images img {
    width: 56px;
    height: 56px;
  }

  .welcome-images .lazy-image-container {
    width: 56px !important;
    height: 56px !important;
    min-width: 56px;
    min-height: 56px;
    max-width: 56px;
    max-height: 56px;
  }

  .welcome-desc {
    font-size: 13px;
    padding: 0 10px;
  }

  .feature-tips {
    padding: 0 10px;
  }

  .tip-item {
    padding: 10px 12px;
    font-size: 12px;
  }

  .message {
    gap: 8px;
  }

  .message-avatar {
    width: 32px;
    height: 32px;
  }

  .message-content {
    padding: 10px 14px;
    font-size: 14px;
  }

  .chat-features {
    padding: 8px 12px;
  }

  .feature-item {
    font-size: 12px;
    padding: 4px 8px;
  }

  .chat-input-area {
    padding: 10px 12px;
    gap: 8px;
  }

  .input-actions {
    flex-direction: row;
    justify-content: flex-end;
  }

  .btn-upload {
    width: 40px;
    height: 40px;
    padding: 8px;
  }

  .btn-upload svg {
    width: 18px;
    height: 18px;
  }

  .message-input {
    min-height: 50px;
    padding: 10px 14px;
    font-size: 16px;
  }

  .btn-send {
    padding: 10px 16px;
    font-size: 13px;
  }

  .btn-header {
    padding: 5px 8px;
    font-size: 11px;
  }

  .btn-header svg {
    width: 14px;
    height: 14px;
  }
}

/* ===================
   移动端/桌面端显示控制
   =================== */
.mobile-only {
  display: none;
}

.desktop-only {
  display: flex;
}

@media (max-width: 768px) {
  .mobile-only {
    display: flex;
  }

  .desktop-only {
    display: none;
  }
}

/* 移动端展开会话列表按钮 */
.btn-toggle-sidebar {
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(139, 92, 246, 0.15);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  color: #a78bfa;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-toggle-sidebar:hover {
  background: rgba(139, 92, 246, 0.25);
  border-color: rgba(139, 92, 246, 0.5);
}

/* ===================
   浅色主题样式 - 类似第二张参考图的简洁现代风格
   =================== */
.chat-container.light-theme {
  background: #f8fafc;
  color: #1e293b;
}

.chat-container.light-theme .cyber-bg {
  display: none;
}

.chat-container.light-theme .chat-header {
  background: #fff;
  border-bottom-color: #e2e8f0;
}

.chat-container.light-theme .chat-header h1 {
  background: linear-gradient(135deg, #1e293b 0%, var(--color-primary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}



.chat-container.light-theme .btn-header {
  background: rgba(124, 58, 237, 0.08);
  color: var(--color-primary);
  border-color: #e2e8f0;
}

.chat-container.light-theme .btn-header:hover {
  background: rgba(124, 58, 237, 0.15);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.chat-container.light-theme .btn-back {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
}

.chat-container.light-theme .chat-messages {
  background: #f8fafc;
}

.chat-container.light-theme .welcome-message h2 {
  color: #1e293b;
  background: linear-gradient(135deg, var(--color-primary) 0%, #20b0c9 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.chat-container.light-theme .welcome-message p {
  color: #64748b;
}

.chat-container.light-theme .welcome-icon {
  background: #fff;
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.chat-container.light-theme .tip-item {
  background: #fff;
  border-color: #e2e8f0;
  color: #1e293b;
}

.chat-container.light-theme .tip-item:hover {
  background: rgba(124, 58, 237, 0.05);
  border-color: var(--color-primary);
}

.chat-container.light-theme .message-avatar {
  background: rgba(124, 58, 237, 0.1);
  border-color: #e2e8f0;
}

.chat-container.light-theme .message-item.user .message-text {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
}

.chat-container.light-theme .message-item.assistant .message-text {
  background: #fff;
  border-color: #e2e8f0;
  color: #1e293b;
}

.chat-container.light-theme .message-time {
  color: #94a3b8;
}

.chat-container.light-theme .btn-copy {
  color: #94a3b8;
}

.chat-container.light-theme .btn-copy:hover {
  color: var(--color-primary);
  background: rgba(124, 58, 237, 0.1);
}

.chat-container.light-theme .btn-resend {
  color: #ef4444;
}

.chat-container.light-theme .btn-resend:hover {
  color: #dc2626;
  background: rgba(239, 68, 68, 0.1);
}

.chat-container.light-theme .message-item.failed .message-text {
  border-color: #ef4444;
}

.chat-container.light-theme .thinking-bubble {
  background: #fff;
  border-color: #e2e8f0;
}

.chat-container.light-theme .thinking-text {
  color: var(--color-primary);
}

.chat-container.light-theme .thinking-bubble .dot {
  color: var(--color-primary);
}

.chat-container.light-theme .chat-features {
  background: #fff;
  border-top-color: #e2e8f0;
}

.chat-container.light-theme .cyber-checkbox {
  color: #64748b;
}

.chat-container.light-theme .cyber-checkbox:hover {
  color: #1e293b;
}

.chat-container.light-theme .checkbox-custom {
  border-color: #e2e8f0;
}

.chat-container.light-theme .hud-status {
  color: #94a3b8;
}

.chat-container.light-theme .chat-input-area {
  background: #fff;
  border-top-color: #e2e8f0;
}

.chat-container.light-theme .uploaded-file {
  background: rgba(124, 58, 237, 0.05);
  border-color: rgba(124, 58, 237, 0.2);
}

.chat-container.light-theme .file-info {
  color: var(--color-primary);
}

.chat-container.light-theme .btn-upload {
  background: rgba(124, 58, 237, 0.05);
  border-color: rgba(124, 58, 237, 0.2);
  color: var(--color-primary);
}

.chat-container.light-theme .btn-upload:hover:not(:disabled) {
  background: rgba(124, 58, 237, 0.1);
  border-color: var(--color-primary);
}

.chat-container.light-theme .message-input {
  background: #f8fafc;
  border-color: #e2e8f0;
  color: #1e293b;
}

.chat-container.light-theme .message-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 20px rgba(124, 58, 237, 0.1);
}

.chat-container.light-theme .message-input::placeholder {
  color: #94a3b8;
}

.chat-container.light-theme .char-count {
  color: #94a3b8;
}

.chat-container.light-theme .chat-messages::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.chat-container.light-theme .chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
}

.chat-container.light-theme .chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
