<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import { onMounted, onUnmounted, watch, computed } from 'vue'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import { useTransitionStore } from '@/stores/transition'
import LandingView from '@/views/LandingView.vue'
import ChatView from '@/views/ChatView.vue'
import FeaturesView from '@/views/FeaturesView.vue'
import DemoView from '@/views/DemoView.vue'
import IntegrationsView from '@/views/IntegrationsView.vue'
import UpgradeVIPView from '@/views/UpgradeVIPView.vue'
import QuizView from '@/views/QuizView.vue'
import AuthorProfileView from '@/views/AuthorProfileView.vue'
import TeamView from '@/views/TeamView.vue'

const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const transitionStore = useTransitionStore()
const route = useRoute()
const router = useRouter()

let tokenRefreshInterval = null

const subPages = ['chat', 'features', 'demo', 'integrations', 'upgrade-vip', 'author']

const currentPage = computed(() => transitionStore.targetPage)

const PageComponent = computed(() => {
  switch (currentPage.value) {
    case 'features': return FeaturesView
    case 'demo': return DemoView
    case 'integrations': return IntegrationsView
    case 'upgrade-vip': return UpgradeVIPView
    case 'quiz': return QuizView
    case 'author': return AuthorProfileView
    case 'team': return TeamView
    default: return ChatView
  }
})

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    transitionStore.reset()
  }
})

const initTokenRefresh = () => {
  if (tokenRefreshInterval) {
    clearInterval(tokenRefreshInterval)
  }

  tokenRefreshInterval = setInterval(async () => {
    if (authStore.token && authStore.refreshTokenValue) {
      try {
        await authStore.handleRefreshToken()
      } catch (error) {
        console.error('自动刷新token失败:', error)
      }
    }
  }, 30 * 60 * 1000)
}

onMounted(async () => {
  settingsStore.initTheme()

  if (authStore.token) {
    await authStore.checkAuth()
    initTokenRefresh()
  }
})

onUnmounted(() => {
  if (tokenRefreshInterval) {
    clearInterval(tokenRefreshInterval)
  }
})
</script>

<template>
  <div class="app-container">
    <RouterView />

    <div
      v-if="transitionStore.isCardDrawing"
      class="card-draw-overlay"
      :class="{
        'drawing': transitionStore.isAnimating && !transitionStore.isReversing && !transitionStore.isReverseCardDraw,
        'reversing': transitionStore.isReversing && transitionStore.isAnimating,
        'reverse-init': transitionStore.isReversing && !transitionStore.isAnimating,
        'reverse-card-draw': transitionStore.isReverseCardDraw && transitionStore.isAnimating,
        'reverse-card-init': transitionStore.isReverseCardDraw && !transitionStore.isAnimating
      }"
    >
      <div class="overlay-landing">
        <LandingView v-if="transitionStore.isReversing" />
      </div>
      <div class="overlay-target">
        <component :is="PageComponent" v-if="!transitionStore.isReversing" />
      </div>
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg-primary: #ffffff;
  --bg-secondary: #f9fafb;
  --bg-tertiary: #ffffff;
  --bg-header: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  --text-primary: #374151;
  --text-secondary: #6b7280;
  --text-tertiary: #9ca3af;
  --border-color: #e5e7eb;
  --border-hover: #d1d5db;
  --shadow: rgba(0, 0, 0, 0.1);
  --shadow-light: rgba(0, 0, 0, 0.05);
  --accent-color: var(--color-primary);
  --accent-hover: var(--color-primary-dark);
  --message-user-bg: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  --message-assistant-bg: #ffffff;
  --message-user-text: #ffffff;
  --message-assistant-text: #374151;
  --accent-color: var(--color-primary);
  --accent-hover: var(--color-primary-dark);
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --error-color: #ef4444;
  --nav-bg: rgba(255, 255, 255, 0.9);
  --nav-border: rgba(0, 0, 0, 0.05);
  --code-bg: #f8f9fa;
  --code-header-bg: rgba(0, 0, 0, 0.02);
  --code-keyword: #d73a49;
  --code-string: #032f62;
  --code-annotation: #6a737d;
  --code-class: #6f42c1;
  --code-method: #005cc5;
  --input-bg: #ffffff;
}

[data-theme='dark'] {
  --bg-primary: #0a0a0f;
  --bg-secondary: #0f0f18;
  --bg-tertiary: #14141e;
  --bg-header: linear-gradient(135deg, #4c1d95 0%, #5b21b6 100%);
  --text-primary: #ffffff;
  --text-secondary: #e5e7eb;
  --text-tertiary: #9ca3af;
  --border-color: rgba(139, 92, 246, 0.2);
  --border-hover: rgba(139, 92, 246, 0.4);
  --shadow: rgba(0, 0, 0, 0.4);
  --shadow-light: rgba(0, 0, 0, 0.2);
  --input-bg: #1a1a28;
  --message-user-bg: linear-gradient(135deg, #5b21b6 0%, var(--color-primary-dark) 100%);
  --message-assistant-bg: #14141e;
  --message-user-text: #ffffff;
  --message-assistant-text: #e5e7eb;
  --accent-color: var(--color-primary);
  --accent-hover: var(--color-primary-dark);
  --success-color: #34d399;
  --warning-color: #fbbf24;
  --error-color: #f87171;
  --nav-bg: rgba(10, 10, 15, 0.8);
  --nav-border: rgba(255, 255, 255, 0.05);
  --code-bg: #0d0d12;
  --code-header-bg: rgba(255, 255, 255, 0.02);
  --code-keyword: #c792ea;
  --code-string: #c3e88d;
  --code-annotation: #7f848e;
  --code-class: #82aaff;
  --code-method: #82aaff;
  --input-bg: rgba(30, 30, 50, 0.8);
}

body,
html {
  min-height: 100%;
  width: 100%;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell',
    'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

#app {
  width: 100%;
  min-height: 100%;
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

.app-container {
  width: 100%;
  min-height: 100vh;
  overflow-x: hidden;
}

.card-draw-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  pointer-events: none;
}

.overlay-landing {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--bg-primary, #0a0a0f);
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(-100%);
  pointer-events: auto;
  z-index: 1;
}

.overlay-target {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform: translateX(100%);
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: -20px 0 60px rgba(0, 0, 0, 0.5);
  pointer-events: auto;
  z-index: 2;
}

.card-draw-overlay.drawing .overlay-landing {
  transform: translateX(-100%);
  opacity: 0;
}

.card-draw-overlay.drawing .overlay-target {
  transform: translateX(0);
}

.card-draw-overlay.reverse-init .overlay-landing {
  transform: translateX(-100%);
}

.card-draw-overlay.reverse-init .overlay-target {
  transform: translateX(0);
}

.card-draw-overlay.reversing .overlay-landing {
  transform: translateX(0);
  z-index: 10;
}

.card-draw-overlay.reversing .overlay-target {
  transform: translateX(0);
  z-index: 5;
}

.card-draw-overlay.reverse-card-init .overlay-landing {
  transform: translateX(0);
}

.card-draw-overlay.reverse-card-init .overlay-target {
  transform: translateX(-100%);
}

.card-draw-overlay.reverse-card-draw .overlay-landing {
  transform: translateX(100%);
  opacity: 0;
}

.card-draw-overlay.reverse-card-draw .overlay-target {
  transform: translateX(0);
}

.chat-container,
.chat-layout,
.main-content,
.sidebar,
.knowledge-panel {
  background-color: var(--bg-primary);
  color: var(--text-primary);
  border-color: var(--border-color);
}

.chat-header,
.modal-header,
.panel-header {
  background: var(--bg-header);
  color: #ffffff;
  border-color: var(--border-color);
}

.chat-messages,
.modal-content,
.knowledge-panel {
  background-color: var(--bg-secondary);
}

.chat-input-area,
.chat-features {
  background-color: var(--bg-tertiary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.form-input,
.form-select,
.message-input {
  background-color: var(--input-bg);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.conversation-item {
  background-color: var(--bg-tertiary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.conversation-item:hover {
  background-color: var(--bg-secondary);
  border-color: var(--border-hover);
}

.conversation-item.active {
  background-color: var(--bg-tertiary);
  border-color: var(--color-primary);
}

.kb-item {
  background-color: var(--bg-tertiary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.kb-item.selected {
  background-color: var(--bg-tertiary);
  border-color: var(--color-primary);
}

.feature-btn,
.btn-cancel {
  background-color: var(--bg-tertiary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.conversations-panel {
  background-color: var(--bg-primary);
  border-color: var(--border-color);
}

.conversation-title {
  color: var(--text-primary);
}

.conversation-meta {
  color: var(--text-tertiary);
}

.modal-content {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
}

.modal-header,
.modal-footer {
  background-color: var(--bg-tertiary);
  border-color: var(--border-color);
  color: var(--text-primary);
}

.label-text,
.checkbox-item span {
  color: var(--text-primary);
}

.value {
  color: var(--text-primary);
}

.toggle-sidebar-btn {
  color: var(--text-secondary);
}

.toggle-sidebar-btn:hover {
  background-color: var(--bg-secondary);
  color: var(--color-primary);
}
</style>
