import { defineStore } from 'pinia'
import { ref, nextTick } from 'vue'

export const useTransitionStore = defineStore('transition', () => {
  const isCardDrawing = ref(false)
  const isReversing = ref(false)
  const showLanding = ref(true)
  const showChat = ref(false)
  const isAnimating = ref(false)
  const savedScrollTop = ref(0)
  const scrollContentTransform = ref('')
  const targetPage = ref('chat')
  const isReverseCardDraw = ref(false)

  const startCardDraw = async (page = 'chat') => {
    savedScrollTop.value = window.pageYOffset || document.documentElement.scrollTop
    const scrollContent = document.querySelector('.scroll-content')
    if (scrollContent) {
      scrollContentTransform.value = scrollContent.style.transform
    }

    targetPage.value = page
    isCardDrawing.value = true
    isReversing.value = false
    isReverseCardDraw.value = false
    showChat.value = true
    isAnimating.value = false

    await nextTick()

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        isAnimating.value = true
      })
    })

    return new Promise((resolve) => {
      setTimeout(() => {
        showLanding.value = false
        isCardDrawing.value = false
        isAnimating.value = false
        // 动画结束后再重置首页滚动位置（用户看不到）
        window.scrollTo(0, 0)
        document.body.style.height = ''
        resolve()
      }, 600)
    })
  }

  const startReverseCardDraw = async (page = 'chat') => {
    savedScrollTop.value = window.pageYOffset || document.documentElement.scrollTop
    const scrollContent = document.querySelector('.scroll-content')
    if (scrollContent) {
      scrollContentTransform.value = scrollContent.style.transform
    }

    targetPage.value = page
    isCardDrawing.value = true
    isReversing.value = false
    isReverseCardDraw.value = true
    showChat.value = true
    isAnimating.value = false

    await nextTick()

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        isAnimating.value = true
      })
    })

    return new Promise((resolve) => {
      setTimeout(() => {
        showLanding.value = false
        isCardDrawing.value = false
        isReverseCardDraw.value = false
        isAnimating.value = false
        // 动画结束后再重置
        window.scrollTo(0, 0)
        document.body.style.height = ''
        resolve()
      }, 600)
    })
  }

  const startReverseDraw = async () => {
    isCardDrawing.value = true
    isReversing.value = true
    isReverseCardDraw.value = false
    showLanding.value = true
    isAnimating.value = false

    await nextTick()

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        isAnimating.value = true
      })
    })

    return new Promise((resolve) => {
      setTimeout(() => {
        isCardDrawing.value = false
        isReversing.value = false
        isAnimating.value = false
        showChat.value = false
        targetPage.value = 'chat'
        // 动画结束后再重置
        window.scrollTo(0, 0)
        const scrollContent = document.querySelector('.scroll-content')
        if (scrollContent) {
          scrollContent.style.transform = 'translateY(0px)'
        }
        document.body.style.height = ''
        resolve()
      }, 600)
    })
  }

  const reset = () => {
    showLanding.value = true
    showChat.value = false
    isCardDrawing.value = false
    isReversing.value = false
    isReverseCardDraw.value = false
    isAnimating.value = false
    savedScrollTop.value = 0
    scrollContentTransform.value = ''
    targetPage.value = 'chat'
  }

  const restoreScrollPosition = () => {
    return {
      scrollTop: savedScrollTop.value,
      transform: scrollContentTransform.value
    }
  }

  return {
    isCardDrawing,
    isReversing,
    showLanding,
    showChat,
    isAnimating,
    savedScrollTop,
    scrollContentTransform,
    targetPage,
    isReverseCardDraw,
    startCardDraw,
    startReverseCardDraw,
    startReverseDraw,
    reset,
    restoreScrollPosition
  }
})
