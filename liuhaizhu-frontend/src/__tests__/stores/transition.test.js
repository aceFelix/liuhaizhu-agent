import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTransitionStore } from '@/stores/transition'

// ============================================================
// useTransitionStore 测试
// 注意：startCardDraw/startReverseDraw 等异步动画方法因依赖
// requestAnimationFrame + nextTick，在集成/端到端测试中验证
// ============================================================
describe('useTransitionStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('初始状态', () => {
    it('showLanding 应为 true', () => {
      const store = useTransitionStore()
      expect(store.showLanding).toBe(true)
    })

    it('showChat 应为 false', () => {
      const store = useTransitionStore()
      expect(store.showChat).toBe(false)
    })

    it('isCardDrawing 应为 false', () => {
      const store = useTransitionStore()
      expect(store.isCardDrawing).toBe(false)
    })

    it('isReversing 应为 false', () => {
      const store = useTransitionStore()
      expect(store.isReversing).toBe(false)
    })

    it('isAnimating 应为 false', () => {
      const store = useTransitionStore()
      expect(store.isAnimating).toBe(false)
    })

    it('isReverseCardDraw 应为 false', () => {
      const store = useTransitionStore()
      expect(store.isReverseCardDraw).toBe(false)
    })

    it('targetPage 默认为 chat', () => {
      const store = useTransitionStore()
      expect(store.targetPage).toBe('chat')
    })

    it('savedScrollTop 应为 0', () => {
      const store = useTransitionStore()
      expect(store.savedScrollTop).toBe(0)
    })

    it('scrollContentTransform 应为空字符串', () => {
      const store = useTransitionStore()
      expect(store.scrollContentTransform).toBe('')
    })
  })

  describe('reset', () => {
    it('应重置所有状态到初始值', () => {
      const store = useTransitionStore()

      // 修改所有状态
      store.showLanding = false
      store.showChat = true
      store.isCardDrawing = true
      store.isReversing = true
      store.isAnimating = true
      store.isReverseCardDraw = true
      store.savedScrollTop = 500
      store.scrollContentTransform = 'translateY(-100px)'
      store.targetPage = 'features'

      store.reset()

      expect(store.showLanding).toBe(true)
      expect(store.showChat).toBe(false)
      expect(store.isCardDrawing).toBe(false)
      expect(store.isReversing).toBe(false)
      expect(store.isAnimating).toBe(false)
      expect(store.isReverseCardDraw).toBe(false)
      expect(store.savedScrollTop).toBe(0)
      expect(store.scrollContentTransform).toBe('')
      expect(store.targetPage).toBe('chat')
    })
  })

  describe('restoreScrollPosition', () => {
    it('应返回保存的滚动位置和变换', () => {
      const store = useTransitionStore()
      store.savedScrollTop = 300
      store.scrollContentTransform = 'translateY(-100px)'

      const result = store.restoreScrollPosition()

      expect(result.scrollTop).toBe(300)
      expect(result.transform).toBe('translateY(-100px)')
    })

    it('默认值应都为 0 / 空串', () => {
      const store = useTransitionStore()
      const result = store.restoreScrollPosition()

      expect(result.scrollTop).toBe(0)
      expect(result.transform).toBe('')
    })
  })
})
