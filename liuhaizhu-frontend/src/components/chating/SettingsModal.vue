<template>
  <div
    v-if="visible"
    class="modal-overlay"
    :class="{ 'light-theme': settingsStore.theme === 'light' }"
    @click="close"
  >
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>设置</h2>
        <button @click="close" class="btn-close">×</button>
      </div>

      <!-- 选项卡导航 -->
      <div class="modal-tabs">
        <button
          @click="activeTab = 'general'"
          :class="['tab-btn', { active: activeTab === 'general' }]"
        >
          <span class="tab-icon">⚙️</span>
          <span>通用</span>
        </button>
        <button
          @click="handleKnowledgeTabClick"
          :class="['tab-btn', { active: activeTab === 'knowledge' }]"
        >
          <span class="tab-icon">📚</span>
          <span>我的知识库</span>
          <span v-if="knowledgeCount > 0 && canAccessKnowledgeBase" class="tab-badge">{{ knowledgeCount }}</span>
          <span v-else-if="!canAccessKnowledgeBase" class="tab-badge vip-badge">VIP</span>
        </button>
      </div>

      <div class="modal-body">
        <!-- 通用设置 -->
        <div v-if="activeTab === 'general'" class="tab-content">
          <div class="setting-item">
            <label class="setting-label">
              <span class="label-text">主题模式</span>
            </label>
            <div class="theme-selector">
              <button
                @click="settingsStore.setTheme('light')"
                :class="['theme-btn', { active: settingsStore.theme === 'light' }]"
              >
                <span class="theme-icon">☀️</span>
                <span>浅色</span>
              </button>
              <button
                @click="settingsStore.setTheme('dark')"
                :class="['theme-btn', { active: settingsStore.theme === 'dark' }]"
              >
                <span class="theme-icon">🌙</span>
                <span>深色</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 知识库管理 -->
        <div v-else-if="activeTab === 'knowledge'" class="tab-content">
          <!-- 有权限时显示知识库管理器 -->
          <KnowledgeBaseManager
            v-if="canAccessKnowledgeBase"
            ref="knowledgeBaseRef"
            :visible="activeTab === 'knowledge'"
            @upload-success="handleUploadSuccess"
            @delete-success="handleDeleteSuccess"
            @upgrade-vip="handleUpgradeVip"
          />
          <!-- 无权限时显示锁图标 -->
          <div v-else class="knowledge-locked" @click="triggerLockGlow">
            <div class="lock-icon" :class="{ 'glowing': isLockGlowing }">
              <img src="/images/lock.svg" alt="锁定" />
              <span class="lock-glow-effect"></span>
            </div>
            <div class="lock-text">
              <p v-if="!authStore.isAuthenticated">登录后查看知识库</p>
              <p v-else>升级VIP后使用知识库</p>
            </div>
            <div class="lock-subtext">
              <span v-if="!authStore.isAuthenticated">注册/登录即可构建个人知识库</span>
              <span v-else>开通VIP即可上传文档构建知识库</span>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button @click="close" class="btn-confirm">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import { usePermission } from '@/utils/permission'
import KnowledgeBaseManager from '../knowledge-base/KnowledgeBaseManager.vue'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['update:visible'])

// Store
const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const { isVip, isAdmin } = usePermission()

// 状态
const activeTab = ref('general')
const knowledgeBaseRef = ref(null)
const isLockGlowing = ref(false)
let lockGlowTimeout = null

// 计算属性：是否有权限访问知识库
const canAccessKnowledgeBase = computed(() => {
  return authStore.isAuthenticated && (isVip.value || isAdmin.value)
})

// 计算属性：知识库数量
const knowledgeCount = computed(() => {
  return knowledgeBaseRef.value?.knowledgeBases?.length || 0
})

// 监听弹窗显示，重置状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    activeTab.value = 'general'
    // 禁用滚动吸附，确保弹窗显示在正确位置
    document.documentElement.style.scrollSnapType = 'none'
  } else {
    // 恢复滚动吸附
    document.documentElement.style.scrollSnapType = 'y proximity'
  }
})

// 方法
const close = () => {
  emit('update:visible', false)
}

// 处理知识库标签点击 - 所有用户都可以查看，但无权限时显示锁图标
const handleKnowledgeTabClick = () => {
  activeTab.value = 'knowledge'
}

// 处理上传成功
const handleUploadSuccess = () => {
  // 可以添加提示或其他逻辑
}

// 处理删除成功
const handleDeleteSuccess = () => {
  // 可以添加提示或其他逻辑
}

// 处理升级VIP
const handleUpgradeVip = () => {
  // 可以跳转到VIP购买页面或显示提示
  alert('VIP功能即将上线，敬请期待！')
}

// 触发锁图标发光效果
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
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.modal-content {
  background: var(--bg-tertiary);
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
  border: 1px solid var(--border-color);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.btn-close {
  background: none;
  border: none;
  font-size: 28px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

/* 选项卡样式 */
.modal-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 24px 0;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}

.tab-icon {
  font-size: 16px;
}

.tab-badge {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.tab-badge.vip-badge {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  font-size: 10px;
  font-weight: 600;
}

.modal-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
  min-height: 300px;
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}

.modal-body:hover {
  scrollbar-color: var(--text-tertiary) var(--bg-secondary);
}

.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: transparent;
}

.modal-body::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 3px;
}

.modal-body:hover::-webkit-scrollbar-thumb {
  background: var(--text-tertiary);
}

.modal-body:hover::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

.setting-item {
  margin-bottom: 24px;
}

.setting-label {
  display: block;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.theme-selector {
  display: flex;
  gap: 12px;
}

.theme-btn {
  flex: 1;
  padding: 16px;
  background: var(--bg-secondary);
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  color: var(--text-primary);
}

.theme-btn:hover {
  border-color: #8b5cf6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.theme-btn.active {
  border-color: #8b5cf6;
  background: rgba(139, 92, 246, 0.1);
}

.theme-icon {
  font-size: 32px;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  background: var(--bg-tertiary);
}

.btn-confirm {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

/* ===================
   浅色主题样式
   =================== */
.modal-overlay.light-theme {
  background: rgba(0, 0, 0, 0.3);
}

.modal-overlay.light-theme .modal-content {
  background: #fff;
  border-color: #e2e8f0;
}

.modal-overlay.light-theme .modal-header,
.modal-overlay.light-theme .modal-tabs,
.modal-overlay.light-theme .modal-footer {
  background: #fff;
  border-color: #e2e8f0;
}

.modal-overlay.light-theme .modal-header h2 {
  color: #1e293b;
}

.modal-overlay.light-theme .btn-close {
  color: #64748b;
}

.modal-overlay.light-theme .btn-close:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.modal-overlay.light-theme .tab-btn {
  color: #64748b;
}

.modal-overlay.light-theme .tab-btn:hover {
  color: #1e293b;
}

.modal-overlay.light-theme .tab-btn.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}

.modal-overlay.light-theme .tab-badge.vip-badge {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
}

/* 知识库锁定状态样式 */
.knowledge-locked {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  min-height: 300px;
}

.lock-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
  opacity: 0.8;
}

.lock-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.knowledge-locked {
  cursor: pointer;
}

.lock-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.lock-icon:hover {
  transform: scale(1.05);
}

.lock-icon.glowing {
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

.lock-icon.glowing .lock-glow-effect {
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

.lock-text {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.lock-text p {
  margin: 0;
}

.lock-subtext {
  font-size: 12px;
  color: #6b7280;
}

/* 浅色主题下的锁定样式 */
.modal-overlay.light-theme .lock-text {
  color: #64748b;
}

.modal-overlay.light-theme .lock-subtext {
  color: #94a3b8;
}

.modal-overlay.light-theme .setting-label {
  color: #1e293b;
}

.modal-overlay.light-theme .theme-btn {
  background: #f8fafc;
  border-color: #e2e8f0;
  color: #1e293b;
}

.modal-overlay.light-theme .theme-btn:hover {
  border-color: var(--color-primary);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.15);
}

.modal-overlay.light-theme .theme-btn.active {
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.1);
}

.modal-overlay.light-theme .btn-confirm {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
}

.modal-overlay.light-theme .modal-body::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.modal-overlay.light-theme .modal-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
}

.modal-overlay.light-theme .modal-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
