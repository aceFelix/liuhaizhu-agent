import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  // 主题模式：'light' | 'dark'，默认深色
  const theme = ref(localStorage.getItem('settings_theme') || 'dark')

  /**
   * 初始化主题
   */
  const initTheme = () => {
    applyTheme(theme.value)
  }

  /**
   * 应用主题
   */
  const applyTheme = (newTheme) => {
    if (newTheme === 'dark') {
      document.body.classList.add('dark-mode')
      document.documentElement.setAttribute('data-theme', 'dark')
    } else {
      document.body.classList.remove('dark-mode')
      document.documentElement.setAttribute('data-theme', 'light')
    }
  }

  /**
   * 切换主题
   */
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('settings_theme', theme.value)
    applyTheme(theme.value)
  }

  /**
   * 设置主题
   */
  const setTheme = (newTheme) => {
    if (newTheme === 'light' || newTheme === 'dark') {
      theme.value = newTheme
      localStorage.setItem('settings_theme', theme.value)
      applyTheme(theme.value)
    }
  }

  // 监听主题变化，自动应用
  watch(theme, (newTheme) => {
    applyTheme(newTheme)
  })

  return {
    theme,
    initTheme,
    toggleTheme,
    setTheme,
  }
})
