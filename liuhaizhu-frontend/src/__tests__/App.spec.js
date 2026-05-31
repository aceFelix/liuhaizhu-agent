import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// 为 App.vue 需要的 store 提供 mock
vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    token: null,
    user: null,
    isAuthenticated: false,
    checkAuth: vi.fn(),
  })),
}))
vi.mock('@/stores/chat', () => ({
  useChatStore: vi.fn(() => ({
    messages: [],
    conversations: [],
    currentConversationId: null,
    webSearchEnabled: false,
    ragSearchEnabled: false,
    isLoading: false,
  })),
}))
vi.mock('@/stores/settings', () => ({
  useSettingsStore: vi.fn(() => ({
    theme: 'dark',
    toggleTheme: vi.fn(),
  })),
}))
vi.mock('@/stores/transition', () => ({
  useTransitionStore: vi.fn(() => ({
    isTransitioning: false,
    reset: vi.fn(),
  })),
}))

// Partial mock vue-router: keep RouterView/RouterLink, mock useRoute/useRouter
vi.mock('vue-router', async (importOriginal) => {
  const actual = await importOriginal()
  return {
    ...actual,
    useRoute: vi.fn(() => ({ path: '/' })),
    useRouter: vi.fn(() => ({ push: vi.fn(), replace: vi.fn() })),
  }
})

import App from '@/App.vue'

describe('App', () => {
  it('应正常挂载而不报错', () => {
    setActivePinia(createPinia())
    const wrapper = mount(App, {
      global: {
        stubs: {
          'router-view': { template: '<div class="router-view-stub"></div>' },
          'router-link': { template: '<a class="router-link-stub"><slot /></a>' },
          'transition': false,
        },
      },
    })
    expect(wrapper.exists()).toBe(true)
  })
})
