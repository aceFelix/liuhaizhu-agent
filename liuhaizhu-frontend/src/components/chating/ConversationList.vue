<template>
  <div
    class="sidebar"
    :class="{ collapsed: sidebarCollapsed, 'light-theme': settingsStore.theme === 'light' }"
  >
    <div class="sidebar-header">
      <button
        @click="handleNewConversation"
        class="btn-new-chat"
        :disabled="!authStore.isAuthenticated"
        :title="!authStore.isAuthenticated ? '请先登录' : ''"
      >
        <span class="icon">+</span>
        <span v-if="!sidebarCollapsed">新建对话</span>
      </button>
      <div class="header-actions">
        <button
          v-if="!sidebarCollapsed && authStore.isAuthenticated && conversations.length > 0"
          @click="toggleBatchMode"
          class="btn-batch-mode"
          :class="{ active: isBatchMode }"
          title="批量管理"
        >
          <span>📋</span>
        </button>
        <button @click="toggleSidebar" class="btn-toggle" title="收起/展开">
          <img v-if="sidebarCollapsed" src="/images/expand.svg" alt="展开" class="toggle-icon" />
          <img v-else src="/images/foldUp.svg" alt="收起" class="toggle-icon" />
        </button>
      </div>
    </div>

    <div v-if="!sidebarCollapsed" class="conversation-list">
      <!-- 批量操作工具栏 -->
      <div v-if="!sidebarCollapsed && isBatchMode" class="batch-toolbar">
        <button @click="toggleSelectAll" class="btn-batch">
          {{ selectedConversations.size === conversations.length && conversations.length > 0 ? '取消全选' : '全选' }}
        </button>
        <span class="batch-count">{{ selectedConversations.size }}</span>
        <button @click="handleBatchDelete" class="btn-batch-delete" :disabled="selectedConversations.size === 0">
          批量删除
        </button>
        <button @click="toggleBatchMode" class="btn-batch-cancel">取消</button>
      </div>

      <div v-if="!authStore.isAuthenticated" class="login-prompt" @click="triggerLockGlow">
        <div class="prompt-icon" :class="{ 'glowing': isLockGlowing }">
          <img src="/images/lock.svg" alt="lock" class="lock-icon" />
          <span class="lock-glow-effect"></span>
        </div>
        <p>登录后查看历史会话</p>
        <p class="prompt-hint">注册/登录即可保存聊天记录</p>
      </div>

      <div v-else-if="conversations.length === 0" class="empty-conversations">
        <p>暂无会话历史</p>
      </div>

      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conversation-item"
        :class="{
          active: conv.id === currentConversationId,
          'is-pinned': conv.pinned === 1,
          'is-selected': selectedConversations.has(conv.id)
        }"
        @click="handleSelectConversation(conv.id)"
      >
        <!-- 批量选择模式下的选择框 -->
        <div v-if="isBatchMode" class="selection-checkbox" @click.stop="toggleConversationSelect(conv.id)">
          <div class="checkbox" :class="{ checked: selectedConversations.has(conv.id) }">
            <span v-if="selectedConversations.has(conv.id)">✓</span>
          </div>
        </div>

        <div class="conversation-content" :class="{ 'with-selection': isBatchMode }">
          <div v-if="editingConvId === conv.id" class="title-edit">
            <input
              v-model="editingTitle"
              @blur="handleSaveTitle(conv.id)"
              @keyup.enter="handleSaveTitle(conv.id)"
              @click.stop
              class="title-input"
            />
          </div>
          <div v-else class="conversation-info">
            <div class="conversation-title">{{ conv.title }}</div>
            <div class="conversation-meta">
              <span class="message-count">{{ conv.messageCount || 0 }}条消息</span>
              <span class="conversation-time">{{ formatTime(conv.updateTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 更多操作按钮 - 放在最右边 -->
        <div class="conversation-actions" @click.stop>
          <div class="more-actions-wrapper">
            <button
              @click="toggleMoreMenu(conv.id)"
              class="btn-action btn-more"
              :class="{ active: activeMenuId === conv.id }"
              title="更多操作"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
              </svg>
            </button>

            <!-- 下拉菜单 -->
            <div v-if="activeMenuId === conv.id" class="action-menu" v-click-outside="closeMoreMenu">
              <button @click="handleTogglePin(conv); closeMoreMenu()" class="menu-item">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M16 12V4h1V2H7v2h1v8l-2 2v2h5.2v6h1.6v-6H18v-2l-2-2z"/>
                </svg>
                <span>{{ conv.pinned === 1 ? '取消置顶' : '置顶' }}</span>
              </button>
              <button @click="handleEditTitle(conv); closeMoreMenu()" class="menu-item">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                </svg>
                <span>重命名</span>
              </button>
              <div class="menu-divider"></div>
              <button @click="handleDeleteConfirm(conv); closeMoreMenu()" class="menu-item menu-item-danger">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 设置按钮 -->
    <div class="sidebar-footer">
      <button @click="$emit('open-settings')" class="btn-settings-sidebar" title="设置">
        <span class="settings-icon">⚙️</span>
        <span v-if="!sidebarCollapsed">设置</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useChatStore } from '@/stores/chat'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import {
  createConversation,
  getConversationList,
  getConversationDetail,
  deleteConversation,
  updateConversationTitle,
  updateConversationPinned,
  batchDeleteConversations,
} from '@/api/conversation'
import { ElMessage, ElMessageBox } from 'element-plus'

// Emits
defineEmits(['open-settings'])

// Store
const chatStore = useChatStore()
const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const { conversations, currentConversationId, userId, isLoading } = storeToRefs(chatStore)

// 状态
const sidebarCollapsed = ref(false)
const editingConvId = ref(null)
const editingTitle = ref('')
const selectedConversations = ref(new Set())
const isBatchMode = ref(false)
const activeMenuId = ref(null)
const isLockGlowing = ref(false)
let lockGlowTimeout = null

// 移动端检测 - 如果是移动端则默认收起侧边栏
onMounted(() => {
  const isMobile = window.innerWidth <= 768
  if (isMobile) {
    sidebarCollapsed.value = true
  }
})

// 点击外部关闭菜单的指令
const vClickOutside = {
  mounted(el, binding) {
    el._clickOutside = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutside)
  }
}

const toggleMoreMenu = (convId) => {
  activeMenuId.value = activeMenuId.value === convId ? null : convId
}

const closeMoreMenu = () => {
  activeMenuId.value = null
}

/**
 * 加载会话列表
 */
const loadConversations = async () => {
  console.log('加载会话列表 - 认证状态:', authStore.isAuthenticated, 'userId:', userId.value)

  if (!authStore.isAuthenticated) {
    console.log('用户未认证，清空会话列表')
    chatStore.setConversations([])
    return
  }

  if (!userId.value) {
    console.error('userId 为空，无法加载会话列表')
    ElMessage.error('用户ID未初始化，请刷新页面重试')
    return
  }

  try {
    console.log('正在调用 getConversationList，userId:', userId.value)
    const response = await getConversationList(userId.value)
    console.log('getConversationList 响应:', response.data)
    if (response.data.code === 200) {
      chatStore.setConversations(response.data.data)
      console.log('会话列表加载成功，数量:', response.data.data?.length || 0)
    } else {
      console.error('加载会话列表失败，错误码:', response.data.code, '消息:', response.data.message)
      ElMessage.error(response.data.message || '加载会话列表失败')
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    ElMessage.error('加载会话列表失败: ' + (error.response?.data?.message || error.message))
  }
}

/**
 * 新建会话
 */
const handleNewConversation = async () => {
  try {
    const response = await createConversation(userId.value)
    if (response.data.code === 200) {
      const newConvId = response.data.data
      await loadConversations()
      chatStore.setCurrentConversationId(newConvId)
      chatStore.clearMessages()
      isLoading.value = false // 重置加载状态
      ElMessage.success('新建会话成功')
    }
  } catch (error) {
    console.error('新建会话失败:', error)
    ElMessage.error('新建会话失败')
  }
}

/**
 * 选择会话
 */
const handleSelectConversation = async (convId) => {
  if (convId === currentConversationId.value) return // 已是当前会话，无需切换
  try {
    isLoading.value = false // 切换时重置加载状态
    chatStore.setCurrentConversationId(convId)
    const response = await getConversationDetail(convId, userId.value)
    if (response.data.code === 200) {
      chatStore.loadConversationMessages(response.data.data)
    }
  } catch (error) {
    console.error('加载会话详情失败:', error)
    ElMessage.error('加载会话失败')
  }
}

/**
 * 删除确认对话框
 */
const handleDeleteConfirm = async (conv) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除会话「${conv.title}」吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    // 用户确认删除
    const response = await deleteConversation(conv.id, userId.value)
    if (response.data.code === 200) {
      await loadConversations()
      if (currentConversationId.value === conv.id) {
        chatStore.setCurrentConversationId(null)
        chatStore.clearMessages()
      }
      ElMessage.success('删除成功')
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    // 用户取消或发生错误
    if (error !== 'cancel') {
      console.error('删除会话失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 编辑会话标题
 */
const handleEditTitle = (conv) => {
  editingConvId.value = conv.id
  editingTitle.value = conv.title
  nextTick(() => {
    const input = document.querySelector('.title-input')
    if (input) {
      input.focus()
      input.select()
    }
  })
}

/**
 * 保存标题
 */
const handleSaveTitle = async (convId) => {
  if (!editingTitle.value.trim()) {
    editingConvId.value = null
    return
  }

  try {
    console.log('保存标题 - convId:', convId, 'userId:', userId.value, 'title:', editingTitle.value)
    if (!userId.value) {
      ElMessage.error('用户ID未初始化，请刷新页面重试')
      editingConvId.value = null
      return
    }
    const response = await updateConversationTitle(convId, userId.value, editingTitle.value)
    console.log('updateConversationTitle 响应:', response.data)
    if (response.data.code === 200) {
      await loadConversations()
      ElMessage.success('修改成功')
    } else {
      console.error('修改标题失败，错误码:', response.data.code, '消息:', response.data.message)
      ElMessage.error(response.data.message || '修改失败')
    }
  } catch (error) {
    console.error('更新标题失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    ElMessage.error('修改失败: ' + (error.response?.data?.message || error.message))
  } finally {
    editingConvId.value = null
  }
}

/**
 * 切换侧边栏
 */
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

/**
 * 切换批量选择模式
 */
const toggleBatchMode = () => {
  isBatchMode.value = !isBatchMode.value
  if (!isBatchMode.value) {
    selectedConversations.value.clear()
  }
}

/**
 * 触发锁图标发光效果
 */
const triggerLockGlow = () => {
  if (isLockGlowing.value) return
  isLockGlowing.value = true
  if (lockGlowTimeout) {
    clearTimeout(lockGlowTimeout)
  }
  lockGlowTimeout = setTimeout(() => {
    isLockGlowing.value = false
  }, 600)
}

/**
 * 切换会话选择
 */
const toggleConversationSelect = (convId) => {
  if (selectedConversations.value.has(convId)) {
    selectedConversations.value.delete(convId)
  } else {
    selectedConversations.value.add(convId)
  }
}

/**
 * 全选/取消全选
 */
const toggleSelectAll = () => {
  if (selectedConversations.value.size === conversations.value.length) {
    selectedConversations.value.clear()
  } else {
    conversations.value.forEach(conv => {
      selectedConversations.value.add(conv.id)
    })
  }
}

/**
 * 批量删除会话
 */
const handleBatchDelete = async () => {
  if (selectedConversations.value.size === 0) {
    ElMessage.warning('请先选择要删除的会话')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedConversations.value.size} 个会话吗？此操作不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const conversationIds = Array.from(selectedConversations.value)
    const response = await batchDeleteConversations(conversationIds, userId.value)

    if (response.data.code === 200) {
      await loadConversations()
      selectedConversations.value.clear()
      isBatchMode.value = false
      ElMessage.success(`成功删除 ${conversationIds.length} 个会话`)

      // 如果删除的是当前会话，清空消息
      if (currentConversationId.value && conversationIds.includes(currentConversationId.value)) {
        chatStore.setCurrentConversationId(null)
        chatStore.clearMessages()
      }
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

/**
 * 切换会话置顶状态
 */
const handleTogglePin = async (conv) => {
  try {
    const newPinned = conv.pinned !== 1
    const response = await updateConversationPinned(conv.id, userId.value, newPinned)

    if (response.data.code === 200) {
      await loadConversations()
      ElMessage.success(newPinned ? '置顶成功' : '取消置顶成功')
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('置顶操作失败:', error)
    ElMessage.error('操作失败')
  }
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
  }
}

// 暴露方法供父组件调用
defineExpose({
  loadConversations,
  toggleSidebar,
})
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: rgba(15, 15, 25, 0.95);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(139, 92, 246, 0.2);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
  position: relative;
  z-index: 10;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar.collapsed .sidebar-header {
  flex-direction: column;
  padding: 12px 8px;
  gap: 12px;
}

.sidebar.collapsed .btn-new-chat {
  flex: none;
  width: 44px;
  height: 44px;
  padding: 0;
}

.sidebar.collapsed .btn-toggle {
  width: 44px;
  height: 36px;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.15);
  display: flex;
  gap: 8px;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.btn-batch-mode {
  width: 36px;
  height: 36px;
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  color: #a78bfa;
}

.btn-batch-mode:hover {
  background: rgba(139, 92, 246, 0.3);
  color: #c4b5fd;
}

.btn-batch-mode.active {
  background: rgba(124, 58, 237, 0.5);
  border-color: var(--color-primary);
}

.batch-toolbar {
  padding: 12px 16px;
  background: rgba(124, 58, 237, 0.1);
  border-bottom: 1px solid rgba(124, 58, 237, 0.2);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.batch-count {
  color: #a78bfa;
  font-size: 13px;
  min-width: 24px;
  text-align: center;
}

.btn-batch {
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 6px;
  color: #a78bfa;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch:hover {
  background: rgba(139, 92, 246, 0.3);
}

.btn-batch-delete {
  padding: 6px 12px;
  background: #dc2626;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch-delete:hover:not(:disabled) {
  background: #b91c1c;
}

.btn-batch-delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-batch-cancel {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 6px;
  color: #a78bfa;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch-cancel:hover {
  background: rgba(139, 92, 246, 0.2);
}

.btn-new-chat {
  flex: 1;
  padding: 12px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.btn-new-chat::before {
  content: '';
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

.btn-new-chat:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

.btn-new-chat .icon {
  font-size: 20px;
  font-weight: bold;
}

.btn-toggle {
  padding: 8px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  cursor: pointer;
  color: #a78bfa;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-toggle:hover {
  background: rgba(124, 58, 237, 0.2);
  border-color: var(--color-primary);
  color: #fff;
}

.btn-toggle .toggle-icon {
  width: 20px;
  height: 20px;
  display: block;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(124, 58, 237, 0.15);
  margin-top: auto;
}

.btn-settings-sidebar {
  width: 100%;
  padding: 10px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #9ca3af;
  transition: all 0.3s;
}

.btn-settings-sidebar:hover {
  background: rgba(124, 58, 237, 0.2);
  border-color: var(--color-primary);
  color: #fff;
}

.sidebar.collapsed .sidebar-footer {
  padding: 12px 8px;
}

.sidebar.collapsed .btn-settings-sidebar {
  width: 44px;
  height: 44px;
  padding: 0;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.empty-conversations {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
  font-size: 14px;
}

.login-prompt {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  font-size: 14px;
}

.login-prompt .prompt-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.login-prompt .prompt-icon .lock-icon {
  width: 48px;
  height: 48px;
}

.login-prompt .prompt-icon {
  position: relative;
  display: inline-block;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.login-prompt .prompt-icon:hover {
  transform: scale(1.05);
}

.login-prompt .prompt-icon.glowing {
  animation: lock-pulse 0.6s ease-out;
}

.lock-glow-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.6) 0%, transparent 70%);
  opacity: 0;
  pointer-events: none;
}

.login-prompt .prompt-icon.glowing .lock-glow-effect {
  animation: lock-glow-ripple 0.6s ease-out;
}

@keyframes lock-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes lock-glow-ripple {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.5);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.5);
  }
}

.login-prompt p {
  margin: 8px 0;
}

.login-prompt .prompt-hint {
  color: #6b7280;
  font-size: 12px;
}

.conversation-item {
  padding: 12px 14px;
  margin-bottom: 6px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.conversation-item.is-pinned {
  border-color: rgba(251, 191, 36, 0.4);
  background: rgba(251, 191, 36, 0.05);
}

.conversation-item.is-pinned .conversation-title::before {
  content: '📌 ';
}

.conversation-item.is-selected {
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.2);
}

.selection-checkbox {
  flex-shrink: 0;
}

.checkbox {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(124, 58, 237, 0.4);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.checkbox:hover {
  border-color: var(--color-primary);
}

.checkbox.checked {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.conversation-content.with-selection {
  margin-left: 0;
}

.conversation-item:hover {
  background: rgba(124, 58, 237, 0.1);
  border-color: rgba(124, 58, 237, 0.3);
}

.conversation-item.active {
  background: rgba(124, 58, 237, 0.15);
  border-color: var(--color-primary);
  box-shadow: 0 0 15px rgba(124, 58, 237, 0.2);
}

.conversation-content {
  flex: 1;
  min-width: 0;
  margin-right: 8px;
}

.conversation-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.conversation-title {
  font-size: 13px;
  font-weight: 500;
  color: #e0e0e0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-meta {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: #6b7280;
}

.title-edit {
  width: 100%;
}

.title-input {
  width: 100%;
  padding: 6px 10px;
  background: rgba(30, 30, 50, 0.8);
  border: 1px solid var(--color-primary);
  border-radius: 6px;
  color: #e0e0e0;
  font-size: 13px;
}

.title-input:focus {
  outline: none;
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.3);
}

.conversation-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
  flex-shrink: 0;
}

.conversation-item:hover .conversation-actions {
  opacity: 1;
}

.btn-action {
  padding: 4px 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 6px;
  transition: all 0.3s;
}

.btn-action:hover {
  background: rgba(139, 92, 246, 0.2);
}

/* 更多操作按钮和下拉菜单 */
.more-actions-wrapper {
  position: relative;
}

.btn-more {
  padding: 4px;
  color: #a78bfa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-more:hover,
.btn-more.active {
  background: rgba(139, 92, 246, 0.3);
  color: #c4b5fd;
}

.action-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  min-width: 120px;
  background: rgba(30, 30, 45, 0.98);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  padding: 4px;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  animation: menuFadeIn 0.15s ease-out;
}

@keyframes menuFadeIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px 12px;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: #e0e0e0;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.menu-item:hover {
  background: rgba(139, 92, 246, 0.2);
}

.menu-item-danger {
  color: #ef4444;
}

.menu-item-danger:hover {
  background: rgba(239, 68, 68, 0.2);
}

.menu-divider {
  height: 1px;
  background: rgba(139, 92, 246, 0.2);
  margin: 4px 0;
}

/* 滚动条样式 */
.conversation-list::-webkit-scrollbar {
  width: 4px;
}

.conversation-list::-webkit-scrollbar-track {
  background: rgba(20, 20, 30, 0.5);
}

.conversation-list::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.3);
  border-radius: 2px;
}

.conversation-list::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 92, 246, 0.5);
}

@media (max-width: 768px) {
  .sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.3);
  }

  .sidebar.collapsed {
    transform: translateX(-100%);
  }

  .conversation-item {
    padding: 10px 12px;
  }

  .conversation-title {
    font-size: 12px;
  }

  .conversation-meta {
    font-size: 10px;
  }
}

/* ===================
   浅色主题样式 - 类似第二张参考图的简洁现代风格
   =================== */
.sidebar.light-theme {
  background: #fff;
  border-right-color: #e2e8f0;
}

.sidebar.light-theme .sidebar-header {
  border-bottom-color: #e2e8f0;
}

.sidebar.light-theme .btn-toggle {
  background: rgba(124, 58, 237, 0.08);
  border-color: #e2e8f0;
  color: var(--color-primary);
}

.sidebar.light-theme .btn-toggle:hover {
  background: rgba(124, 58, 237, 0.15);
  border-color: var(--color-primary);
}

.sidebar.light-theme .sidebar-footer {
  border-top-color: #e2e8f0;
}

.sidebar.light-theme .btn-settings-sidebar {
  background: rgba(124, 58, 237, 0.08);
  border-color: #e2e8f0;
  color: #64748b;
}

.sidebar.light-theme .btn-settings-sidebar:hover {
  background: rgba(124, 58, 237, 0.15);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.sidebar.light-theme .empty-conversations {
  color: #94a3b8;
}

.sidebar.light-theme .login-prompt {
  color: #64748b;
}

.sidebar.light-theme .login-prompt .prompt-hint {
  color: #94a3b8;
}

.sidebar.light-theme .conversation-item {
  background: #f8fafc;
  border-color: #e2e8f0;
}

.sidebar.light-theme .conversation-item:hover {
  background: rgba(124, 58, 237, 0.05);
  border-color: var(--color-primary);
}

.sidebar.light-theme .conversation-item.active {
  background: rgba(124, 58, 237, 0.1);
  border-color: var(--color-primary);
  box-shadow: 0 0 15px rgba(124, 58, 237, 0.1);
}

.sidebar.light-theme .conversation-title {
  color: #1e293b;
}

.sidebar.light-theme .conversation-meta {
  color: #94a3b8;
}

.sidebar.light-theme .title-input {
  background: #fff;
  border-color: var(--color-primary);
  color: #1e293b;
}

.sidebar.light-theme .title-input:focus {
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.2);
}

.sidebar.light-theme .btn-action:hover {
  background: rgba(124, 58, 237, 0.1);
}

.sidebar.light-theme .conversation-list::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.sidebar.light-theme .conversation-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
}

.sidebar.light-theme .conversation-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}


</style>
