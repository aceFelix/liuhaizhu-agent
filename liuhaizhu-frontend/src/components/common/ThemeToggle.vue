<template>
  <button
    @click="handleToggle"
    class="btn-theme"
    :title="settingsStore.theme === 'dark' ? '切换到浅色模式' : '切换到深色模式'"
  >
    <span v-if="settingsStore.theme === 'dark'">☀️</span>
    <span v-else>🌙</span>
  </button>
</template>

<script setup>
import { useSettingsStore } from '@/stores/settings'

const settingsStore = useSettingsStore()

const handleToggle = () => {
  settingsStore.toggleTheme()
}
</script>

<style scoped>
.btn-theme {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(139, 92, 246, 0.3);
  background: rgba(15, 23, 42, 0.6);
  color: #a78bfa;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-theme:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
  transform: scale(1.1);
}

/* 浅色主题适配 */
:global(.light-theme) .btn-theme {
  background: rgba(241, 245, 249, 0.8);
  border-color: rgba(124, 58, 237, 0.2);
  color: var(--color-primary);
}

:global(.light-theme) .btn-theme:hover {
  background: rgba(124, 58, 237, 0.1);
  border-color: rgba(124, 58, 237, 0.4);
}
</style>
