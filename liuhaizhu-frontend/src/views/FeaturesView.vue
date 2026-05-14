<template>
  <div class="features-page">
    <CyberBackground />

    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="goBackToLanding" />
          <div class="nav-links-left">
            <router-link to="/features" class="nav-link active">功能</router-link>
            <router-link to="/demo" class="nav-link">演示</router-link>
            <router-link to="/integrations" class="nav-link">集成</router-link>
          </div>
        </div>
        <div class="nav-links">
          <ThemeToggle />
          <button @click="navigateToUpgradeVIP" class="btn-upgrade-vip" title="升级VIP">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <span>会员</span>
          </button>
          <button @click="navigateToChat" class="btn-primary">立即体验</button>
        </div>
      </div>
    </nav>

    <div class="scroll-wrapper" ref="scrollWrapper">
      <div class="sticky-container">
        <div class="horizontal-content" ref="horizontalContent">
          <section class="section title-section">
            <div class="section-content">
              <h1 class="main-title">
                <GlitchText text="强大功能" />
                <span class="subtitle">全方位AI能力</span>
              </h1>
              <p class="section-desc">探索刘海柱AI智能助手的核心功能，体验下一代人工智能交互</p>
              <div class="scroll-hint">
                <span>滚动浏览功能</span>
                <div class="scroll-arrow">→</div>
              </div>
            </div>
          </section>

          <section
            v-for="(feature, index) in features"
            :key="index"
            class="section feature-section"
          >
            <div class="feature-card">
              <div class="card-glow"></div>
              <div class="card-content">
                <div class="feature-icon">
                  <div class="icon-bg">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <path v-if="feature.icon === 'stream'" d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
                      <path v-if="feature.icon === 'knowledge'" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      <path v-if="feature.icon === 'search'" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                      <path v-if="feature.icon === 'session'" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                      <path v-if="feature.icon === 'file'" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      <path v-if="feature.icon === 'tools'" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                      <path v-if="feature.icon === 'role'" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                </div>
                <h3 class="feature-title">{{ feature.title }}</h3>
                <p class="feature-desc">{{ feature.description }}</p>
                <div class="feature-tags">
                  <span v-for="(tag, tIndex) in feature.tags" :key="tIndex" class="tag">{{ tag }}</span>
                </div>
              </div>
              <div class="card-number">0{{ index + 1 }}</div>
            </div>
          </section>

          <section class="section end-section">
            <div class="section-content">
              <h2 class="end-title">准备好开始了吗？</h2>
              <p class="end-desc">立即体验刘海柱AI智能助手的强大功能</p>
              <button @click="navigateToChat" class="btn-cta">
                <span>开始对话</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>

    <div class="scroll-indicator">
      <div
        v-for="i in totalSections"
        :key="i"
        class="indicator-dot"
        :class="{ 'active': currentIndex === i - 1 }"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, onActivated, watch } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useTransitionStore } from '@/stores/transition'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import GlitchText from '@/components/common/GlitchText.vue'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const transitionStore = useTransitionStore()
const scrollWrapper = ref(null)
const horizontalContent = ref(null)
const currentIndex = ref(0)
const totalSections = ref(9)
let scrollTriggerInitialized = false
let stopWatcher = null

const goBackToLanding = () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

const navigateToChat = () => {
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

const navigateToUpgradeVIP = () => {
  transitionStore.startCardDraw('upgrade-vip')
  setTimeout(() => {
    router.push('/upgrade-vip')
  }, 600)
}

const features = [
  {
    icon: 'stream',
    title: '流式对话',
    description: '实时流式响应，无需等待完整回复，即刻获取AI反馈，让对话更加自然流畅',
    tags: ['实时响应', '低延迟', '自然交互']
  },
  {
    icon: 'knowledge',
    title: '个人知识库',
    description: '上传文档构建专属知识库，AI基于您的资料提供精准回答，打造个性化智能助手',
    tags: ['文档上传', '向量检索', '精准回答']
  },
  {
    icon: 'search',
    title: '联网搜索',
    description: '集成实时网络搜索能力，获取最新信息，打破知识边界，让AI知晓天下事',
    tags: ['实时信息', '多源聚合', '智能摘要']
  },
  {
    icon: 'session',
    title: '会话管理',
    description: '智能保存对话历史，支持多会话并行，随时切换话题，让交流有条不紊',
    tags: ['历史记录', '多会话', '智能分类']
  },
  {
    icon: 'file',
    title: '文件识别',
    description: '支持多种格式文件解析，PDF、Word、图片等一键上传，AI自动提取关键信息',
    tags: ['多格式支持', 'OCR识别', '智能提取']
  },
  {
    icon: 'tools',
    title: '多工具支持',
    description: '集成日历日期时间、旅行路线规划、邮件自动发送等多种工具，一站式解决各类问题',
    tags: ['时间日期', '地图集成', '邮件推送']
  },
  {
    icon: 'role',
    title: 'AI角色切换',
    description: '多种AI角色随心切换，程序员、作家、顾问等，满足不同场景的专业需求',
    tags: ['角色扮演', '专业模式', '场景适配']
  }
]

const initScroll = () => {
  if (!scrollWrapper.value || !horizontalContent.value) return
  if (scrollTriggerInitialized) return

  const viewportWidth = window.innerWidth
  const totalWidth = totalSections.value * viewportWidth
  const scrollDistance = totalWidth - viewportWidth

  scrollWrapper.value.style.height = `${scrollDistance}px`

  ScrollTrigger.create({
    trigger: scrollWrapper.value,
    start: 'top top',
    end: 'bottom bottom',
    scrub: 0.5,
    pin: horizontalContent.value,
    onUpdate: (self) => {
      const x = self.progress * scrollDistance
      horizontalContent.value.style.transform = `translateX(-${x}px)`
      currentIndex.value = Math.round(self.progress * (totalSections.value - 1))
    }
  })

  scrollTriggerInitialized = true
}

onMounted(async () => {
  document.body.style.overflow = ''
  document.documentElement.style.overflow = ''

  await nextTick()

  if (transitionStore.isCardDrawing) {
    stopWatcher = watch(() => transitionStore.isCardDrawing, (isDrawing) => {
      if (!isDrawing && !scrollTriggerInitialized) {
        setTimeout(() => {
          initScroll()
        }, 100)
      }
    })
  } else {
    setTimeout(() => {
      initScroll()
    }, 100)
  }

  window.addEventListener('resize', () => {
    ScrollTrigger.refresh()
  })
})

onActivated(() => {
  ScrollTrigger.refresh()
})

onUnmounted(() => {
  if (stopWatcher) {
    stopWatcher()
  }
  ScrollTrigger.getAll().forEach(st => st.kill())
  ScrollTrigger.clearScrollMemory()
  ScrollTrigger.defaults({})
  document.body.style.overflow = ''
  document.documentElement.style.overflow = ''
  scrollTriggerInitialized = false
})
</script>

<style scoped>
.features-page {
  width: 100%;
  height: 100%;
  background: var(--bg-primary, #0a0a0f);
  position: relative;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 32px;
  background: var(--nav-bg, rgba(10, 10, 15, 0.8));
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--nav-border, rgba(255, 255, 255, 0.05));
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.nav-links-left {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: var(--text-secondary, #9ca3af);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.3s ease;
  position: relative;
}

.nav-link:hover,
.nav-link.active {
  color: var(--text-primary, #ffffff);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-blue));
  border-radius: 1px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.btn-upgrade-vip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
}

.btn-upgrade-vip:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(124, 58, 237, 0.4);
}

.btn-primary {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(139, 92, 246, 0.4);
}

.scroll-wrapper {
  position: relative;
  width: 100%;
}

.sticky-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
}

.horizontal-content {
  display: flex;
  height: 100%;
  will-change: transform;
}

.section {
  flex: 0 0 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 80px 40px 40px;
}

.section-content {
  text-align: center;
  max-width: 800px;
}

.title-section {
  background: radial-gradient(ellipse at center, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
}

.main-title {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.main-title :deep(.glitch-text) {
  font-size: clamp(48px, 8vw, 80px);
  font-weight: 700;
}

.subtitle {
  font-size: clamp(18px, 3vw, 24px);
  color: var(--text-secondary, #9ca3af);
  font-weight: 400;
}

.section-desc {
  font-size: 16px;
  color: var(--text-tertiary, #6b7280);
  line-height: 1.6;
  margin-bottom: 32px;
}

.scroll-hint {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-tertiary, #6b7280);
  font-size: 14px;
}

.scroll-arrow {
  font-size: 20px;
  animation: arrowMove 1.5s infinite;
}

@keyframes arrowMove {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(10px); }
}

.feature-section {
  padding: 80px 60px;
}

.feature-card {
  position: relative;
  width: 100%;
  max-width: 600px;
  padding: 48px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 24px;
  overflow: hidden;
  transition: all 0.4s ease;
}

.feature-card:hover {
  border-color: rgba(139, 92, 246, 0.3);
  transform: translateY(-8px);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(180deg, rgba(139, 92, 246, 0.1) 0%, transparent 100%);
  pointer-events: none;
}

.card-content {
  position: relative;
  z-index: 1;
}

.feature-icon {
  margin-bottom: 24px;
}

.icon-bg {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(59, 130, 246, 0.2));
  border-radius: 20px;
  margin: 0 auto;
}

.icon-bg svg {
  width: 40px;
  height: 40px;
  color: var(--color-primary);
}

.feature-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  margin-bottom: 16px;
}

.feature-desc {
  font-size: 16px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.6;
  margin-bottom: 24px;
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.tag {
  padding: 6px 14px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
  font-size: 13px;
  color: #a78bfa;
}

.card-number {
  position: absolute;
  top: 20px;
  right: 24px;
  font-size: 48px;
  font-weight: 700;
  color: rgba(139, 92, 246, 0.1);
  font-family: monospace;
}

.end-section {
  background: radial-gradient(ellipse at center, rgba(59, 130, 246, 0.1) 0%, transparent 70%);
}

.end-title {
  font-size: clamp(32px, 5vw, 48px);
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  margin-bottom: 16px;
}

.end-desc {
  font-size: 18px;
  color: var(--text-secondary, #9ca3af);
  margin-bottom: 32px;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 32px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(124, 58, 237, 0.4);
}

.btn-cta svg {
  width: 20px;
  height: 20px;
}

.scroll-indicator {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 50;
  padding: 12px 20px;
  background: var(--nav-bg, rgba(10, 10, 15, 0.6));
  backdrop-filter: blur(8px);
  border-radius: 30px;
  border: 1px solid var(--nav-border, rgba(255, 255, 255, 0.05));
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--text-tertiary, rgba(255, 255, 255, 0.2));
  transition: all 0.3s ease;
  cursor: pointer;
}

.indicator-dot.active {
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue));
  transform: scale(1.3);
}

.indicator-dot:hover {
  background: rgba(124, 58, 237, 0.5);
}
</style>
