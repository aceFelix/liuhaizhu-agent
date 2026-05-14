<template>
  <TransitionGroup name="toast" tag="div" class="toast-container">
    <div
      v-for="toast in toasts"
      :key="toast.id"
      class="toast-item"
      :class="[`toast-${toast.type}`]"
    >
      <div class="toast-icon">{{ getIcon(toast.type) }}</div>
      <div class="toast-content">
        <p class="toast-title">{{ toast.title }}</p>
        <p v-if="toast.message" class="toast-message">{{ toast.message }}</p>
      </div>
      <button class="toast-close" @click="removeToast(toast.id)">×</button>
      <div class="toast-progress" :style="{ animationDuration: `${toast.duration}ms` }"></div>
    </div>
  </TransitionGroup>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const toasts = ref([])
let toastId = 0

const getIcon = (type) => {
  const icons = {
    success: '✅',
    error: '❌',
    warning: '⚠️',
    info: 'ℹ️'
  }
  return icons[type] || 'ℹ️'
}

const addToast = (options) => {
  const id = ++toastId
  const toast = {
    id,
    type: options.type || 'info',
    title: options.title || '',
    message: options.message || '',
    duration: options.duration || 3000
  }

  toasts.value.push(toast)

  // 自动移除
  setTimeout(() => {
    removeToast(id)
  }, toast.duration)

  return id
}

const removeToast = (id) => {
  const index = toasts.value.findIndex(t => t.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

// 暴露方法给外部使用
defineExpose({
  addToast,
  removeToast,
  success: (title, message, duration) => addToast({ type: 'success', title, message, duration }),
  error: (title, message, duration) => addToast({ type: 'error', title, message, duration }),
  warning: (title, message, duration) => addToast({ type: 'warning', title, message, duration }),
  info: (title, message, duration) => addToast({ type: 'info', title, message, duration })
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: none;
}

.toast-item {
  pointer-events: auto;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  background: var(--bg-tertiary);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
  min-width: 300px;
  max-width: 400px;
  position: relative;
  overflow: hidden;
}

.toast-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.toast-message {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.toast-close {
  background: none;
  border: none;
  font-size: 20px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.toast-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

/* 进度条 */
.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background: currentColor;
  opacity: 0.3;
  animation: progress linear forwards;
}

@keyframes progress {
  from { width: 100%; }
  to { width: 0%; }
}

/* 不同类型样式 */
.toast-success {
  border-left: 4px solid #22c55e;
}

.toast-success .toast-progress {
  background: #22c55e;
}

.toast-error {
  border-left: 4px solid #ef4444;
}

.toast-error .toast-progress {
  background: #ef4444;
}

.toast-warning {
  border-left: 4px solid #f59e0b;
}

.toast-warning .toast-progress {
  background: #f59e0b;
}

.toast-info {
  border-left: 4px solid var(--color-primary);
}

.toast-info .toast-progress {
  background: var(--color-primary);
}

/* 动画 */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
