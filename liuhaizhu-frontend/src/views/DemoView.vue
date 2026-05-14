<template>
  <div class="demo-page">
    <CyberBackground />

    <!-- 导航栏 -->
    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="goBackToLanding" />
          <div class="nav-links-left">
            <router-link to="/features" class="nav-link">功能</router-link>
            <router-link to="/demo" class="nav-link active">演示</router-link>
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

    <!-- 阻尼滚动视窗 -->
    <div class="scroll-viewbox">
      <!-- 滚动内容 -->
      <div class="scroll-content" ref="scrollContent">
        <!-- 主内容区 -->
        <main class="main-content">
      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            <GlitchText text="实时演示" />
          </h1>
          <p class="hero-desc">观看刘海柱AI智能助手的实际运行效果</p>
        </div>
      </section>

      <!-- 对话演示 -->
      <section class="demo-section">
        <div class="section-header">
          <div class="section-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <div class="section-info">
            <h2 class="section-title">流式对话演示</h2>
            <p class="section-subtitle">实时响应，逐字输出，体验自然的对话交互</p>
          </div>
        </div>

        <div class="tv-container">
          <div class="tv-frame">
            <div class="tv-screen">
              <div class="tv-glow"></div>
              <div class="chat-demo">
                <div class="chat-header">
                  <div class="window-controls">
                    <span class="dot red"></span>
                    <span class="dot yellow"></span>
                    <span class="dot green"></span>
                  </div>
                  <span class="chat-title">AI 刘海柱</span>
                </div>
                <div class="chat-messages" ref="chatMessagesContainer">
                  <div
                    v-for="(msg, index) in displayedMessages"
                    :key="index"
                    class="message"
                    :class="msg.type"
                  >
                    <div class="message-avatar" v-if="msg.type === 'ai'">
                      <div class="avatar-icon">AI</div>
                    </div>
                    <div class="message-content">
                      <div class="message-text" v-html="msg.displayText"></div>
                      <span class="typing-cursor" v-if="msg.isTyping">|</span>
                      <div class="message-time" v-if="!msg.isTyping">{{ msg.time }}</div>
                    </div>
                  </div>
                </div>
                <div class="chat-input">
                  <div class="input-box" :class="{ 'has-text': inputText }">
                    <span class="input-text" v-if="inputText">{{ inputText }}</span>
                    <span class="placeholder" v-else>输入消息...</span>
                    <span class="input-cursor" v-if="isUserTyping">|</span>
                    <button class="send-btn" :class="{ 'sending': isSending }">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div class="tv-stand"></div>
          </div>
        </div>
      </section>

      <!-- 邮件发送演示 -->
      <section class="demo-section">
        <div class="section-header">
          <div class="section-icon mail">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          <div class="section-info">
            <h2 class="section-title">智能邮件推送</h2>
            <p class="section-subtitle">AI自动整理对话内容，发送至您的手机</p>
          </div>
        </div>

        <div class="tv-container">
          <div class="tv-frame">
            <div class="tv-screen mail-demo">
              <div class="tv-glow mail-glow"></div>
              <div class="mail-flow">
                <!-- AI处理中 -->
                <div class="flow-step" :class="{ 'active': mailStep >= 1, 'completed': mailStep > 1 }">
                  <div class="step-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                    </svg>
                  </div>
                  <div class="step-content">
                    <h4>AI分析对话</h4>
                    <p>提取关键信息，生成摘要</p>
                  </div>
                  <div class="step-progress">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: mailStep > 1 ? '100%' : '60%' }"></div>
                    </div>
                  </div>
                </div>

                <!-- 生成邮件 -->
                <div class="flow-step" :class="{ 'active': mailStep >= 2, 'completed': mailStep > 2 }">
                  <div class="step-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </div>
                  <div class="step-content">
                    <h4>生成邮件内容</h4>
                    <p>格式化排版，添加主题</p>
                  </div>
                  <div class="step-progress">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: mailStep > 2 ? '100%' : (mailStep === 2 ? '40%' : '0') }"></div>
                    </div>
                  </div>
                </div>

                <!-- 发送中 -->
                <div class="flow-step" :class="{ 'active': mailStep >= 3, 'completed': mailStep > 3 }">
                  <div class="step-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                    </svg>
                  </div>
                  <div class="step-content">
                    <h4>推送到手机</h4>
                    <p>通过邮件/短信发送</p>
                  </div>
                  <div class="step-progress">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: mailStep > 3 ? '100%' : (mailStep === 3 ? '80%' : '0') }"></div>
                    </div>
                  </div>
                </div>

                <!-- 完成 -->
                <div class="flow-step success" :class="{ 'active': mailStep >= 4 }">
                  <div class="step-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <div class="step-content">
                    <h4>发送成功</h4>
                    <p>已送达您的手机</p>
                  </div>
                  <div class="success-badge">
                    <span class="checkmark">✓</span>
                  </div>
                </div>

                <!-- 手机预览 -->
                <div class="phone-preview" :class="{ 'show': mailStep >= 4 }">
                  <div class="phone-frame">
                    <div class="phone-screen">
                      <div class="notification">
                        <div class="notification-header">
                          <span class="app-icon">📧</span>
                          <span class="app-name">刘海柱AI</span>
                          <span class="time">刚刚</span>
                        </div>
                        <div class="notification-title">对话摘要已生成</div>
                        <div class="notification-body">您与AI助手的对话内容已整理完成，点击查看详情...</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="tv-stand"></div>
          </div>
        </div>
      </section>

      <!-- CTA区域 -->
      <section class="cta-section">
        <div class="cta-content">
          <h2 class="cta-title">亲自体验这些功能</h2>
          <p class="cta-desc">立即开始使用刘海柱AI智能助手</p>
          <button @click="navigateToChat" class="btn-cta">
            <span>开始对话</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import GlitchText from '@/components/common/GlitchText.vue'

const router = useRouter()
const transitionStore = useTransitionStore()

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

// 阻尼滚动
const scrollContent = ref(null)
let scrollHandler = null
let resizeHandler = null

// 更新 body 高度以启用滚动
const updateBodyHeight = () => {
  if (scrollContent.value) {
    const contentHeight = scrollContent.value.offsetHeight
    document.body.style.height = `${contentHeight}px`
  }
}

// 处理滚动事件
const handleScroll = () => {
  if (scrollContent.value) {
    const scrollY = window.scrollY || window.pageYOffset
    scrollContent.value.style.transform = `translateY(${-scrollY}px)`
  }
}

// 对话演示数据
const chatMessagesContainer = ref(null)
const displayedMessages = ref([])
const inputText = ref('')
const isUserTyping = ref(false)
const isSending = ref(false)

const demoConversations = [
  {
    user: '帮我分析一下2026年人工智能的发展趋势',
    ai: '2026年AI发展呈现以下趋势：\n\n1. 大模型小型化 - 端侧AI成为新焦点\n2. 多模态融合 - 文本、图像、视频统一处理\n3. AI Agent爆发 - 自主决策能力大幅提升\n4. 垂直领域深化 - 医疗、教育、法律等专业应用'
  },
  {
    user: '这些趋势对普通用户有什么影响？',
    ai: '对普通用户的影响：\n\n• 更智能的助手 - 手机、电脑内置AI能力\n• 个性化服务 - AI深度理解个人需求\n• 创作门槛降低 - 人人都能成为创作者\n• 隐私保护增强 - 本地AI处理敏感数据'
  }
]

let typingTimeout = null
let conversationIndex = 0

const getCurrentTime = () => {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

const scrollToBottom = () => {
  if (chatMessagesContainer.value) {
    chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight
  }
}

const typeInInput = (text, callback) => {
  inputText.value = ''
  isUserTyping.value = true
  let charIndex = 0

  const typeChar = () => {
    if (charIndex < text.length) {
      inputText.value += text[charIndex]
      charIndex++
      typingTimeout = setTimeout(typeChar, 80 + Math.random() * 60)
    } else {
      isUserTyping.value = false
      setTimeout(() => {
        isSending.value = true
        setTimeout(() => {
          isSending.value = false
          inputText.value = ''
          callback()
        }, 300)
      }, 500)
    }
  }

  typeChar()
}

const typeAiResponse = (text, callback) => {
  const msg = {
    type: 'ai',
    displayText: '',
    time: getCurrentTime(),
    isTyping: true
  }
  displayedMessages.value.push(msg)

  let charIndex = 0

  const typeChar = () => {
    if (charIndex < text.length) {
      const char = text[charIndex]
      if (char === '\n') {
        msg.displayText += '<br>'
      } else {
        msg.displayText += char
      }
      charIndex++
      scrollToBottom()
      typingTimeout = setTimeout(typeChar, 25 + Math.random() * 35)
    } else {
      msg.isTyping = false
      setTimeout(callback, 800)
    }
  }

  typeChar()
}

const runConversation = () => {
  if (conversationIndex >= demoConversations.length) {
    conversationIndex = 0
    setTimeout(() => {
      displayedMessages.value = []
      runConversation()
    }, 3000)
    return
  }

  const conv = demoConversations[conversationIndex]

  typeInInput(conv.user, () => {
    displayedMessages.value.push({
      type: 'user',
      displayText: conv.user,
      time: getCurrentTime(),
      isTyping: false
    })

    setTimeout(() => {
      typeAiResponse(conv.ai, () => {
        conversationIndex++
        setTimeout(runConversation, 1500)
      })
    }, 500)
  })
}

const startTypewriter = () => {
  displayedMessages.value = []
  conversationIndex = 0
  inputText.value = ''
  runConversation()
}

// 邮件演示步骤
const mailStep = ref(0)
let mailInterval = null

const startMailDemo = () => {
  mailStep.value = 0
  mailInterval = setInterval(() => {
    if (mailStep.value < 4) {
      mailStep.value++
    } else {
      setTimeout(() => {
        mailStep.value = 0
      }, 3000)
    }
  }, 2000)
}

onMounted(() => {
  startTypewriter()
  startMailDemo()

  // 初始化阻尼滚动
  setTimeout(() => {
    // 先重置 transform，确保初始状态正确
    if (scrollContent.value) {
      scrollContent.value.style.transform = 'translateY(0px)'
    }

    updateBodyHeight()
    scrollHandler = handleScroll
    resizeHandler = updateBodyHeight
    window.addEventListener('scroll', scrollHandler, { passive: true })
    window.addEventListener('resize', resizeHandler, { passive: true })
  }, 100)
})

onUnmounted(() => {
  if (typingTimeout) {
    clearTimeout(typingTimeout)
  }
  if (mailInterval) {
    clearInterval(mailInterval)
  }
  if (scrollHandler) {
    window.removeEventListener('scroll', scrollHandler)
  }
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  document.body.style.height = ''
})
</script>

<style scoped>
.demo-page {
  width: 100vw;
  height: 100vh;
  background: var(--bg-primary, #0a0a0f);
  position: relative;
  overflow: hidden;
}

/* 确保科幻背景可见 - 背景在滚动内容之上但允许点击穿透 */
.demo-page :deep(.cyber-bg) {
  z-index: 3;
}

/* 阻尼滚动视窗 */
.scroll-viewbox {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  z-index: 2;
  pointer-events: none;
}

.scroll-viewbox > * {
  pointer-events: auto;
}

/* 滚动内容 */
.scroll-content {
  position: relative;
  width: 100%;
  will-change: transform;
  transition: transform 0.15s ease-out;
}

/* 导航栏 */
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

/* 主内容 */
.main-content {
  padding-top: 80px;
}

/* Hero区域 */
.hero-section {
  padding: 80px 40px 60px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  margin-bottom: 16px;
}

.hero-title :deep(.glitch-text) {
  font-size: 56px;
  font-weight: 800;
}

.hero-desc {
  font-size: 20px;
  color: var(--text-secondary, #9ca3af);
}

/* 演示区域 */
.demo-section {
  padding: 60px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
}

.section-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(59, 130, 246, 0.2));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(139, 92, 246, 0.3);
}

.section-icon.mail {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(6, 182, 212, 0.2));
  border-color: rgba(59, 130, 246, 0.3);
}

.section-icon svg {
  width: 28px;
  height: 28px;
  color: var(--color-primary);
}

.section-icon.mail svg {
  color: var(--color-blue);
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 4px;
}

.section-subtitle {
  font-size: 16px;
  color: var(--text-secondary, #9ca3af);
}

/* 电视容器 */
.tv-container {
  display: flex;
  justify-content: center;
  perspective: 1000px;
}

.tv-frame {
  position: relative;
  width: 800px;
  max-width: 90vw;
  background: linear-gradient(145deg, #1a1a2e, #0f0f1a);
  border-radius: 20px;
  padding: 20px;
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.tv-screen {
  position: relative;
  background: var(--bg-tertiary, #000);
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16/10;
}

.tv-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(
    ellipse at 50% 0%,
    rgba(139, 92, 246, 0.1) 0%,
    transparent 50%
  );
  pointer-events: none;
}

.tv-glow.mail-glow {
  background: radial-gradient(
    ellipse at 50% 0%,
    rgba(59, 130, 246, 0.1) 0%,
    transparent 50%
  );
}

.tv-stand {
  width: 200px;
  height: 30px;
  background: linear-gradient(145deg, #2a2a3e, #1a1a2e);
  margin: 20px auto 0;
  border-radius: 0 0 10px 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

/* 对话演示 */
.chat-demo {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #0d0d12 0%, #0a0a0f 100%);
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.02);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.window-controls {
  display: flex;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.red { background: #ff5f57; }
.dot.yellow { background: #febc2e; }
.dot.green { background: #28c840; }

.chat-title {
  font-size: 14px;
  color: var(--text-secondary, #9ca3af);
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
  gap: 10px;
  opacity: 0;
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.avatar-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  color: white;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
}

.message.user .message-content {
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue));
}

.message-text {
  font-size: 13px;
  color: var(--text-primary, #ffffff);
  line-height: 1.6;
}

.typing-cursor {
  display: inline-block;
  color: var(--color-primary);
  animation: cursor-blink 1s infinite;
  font-weight: bold;
  margin-left: 2px;
}

@keyframes cursor-blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.message.user .message-text {
  color: white;
}

.message-time {
  font-size: 11px;
  color: var(--text-secondary, #9ca3af);
  margin-top: 4px;
}

.message.user .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  align-self: flex-start;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: 50%;
  animation: typing 1.4s ease infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-10px); }
}

.chat-input {
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.input-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: border-color 0.3s, background 0.3s;
}

.input-box.has-text {
  border-color: rgba(139, 92, 246, 0.3);
  background: rgba(139, 92, 246, 0.05);
}

.placeholder {
  font-size: 13px;
  color: var(--text-secondary, #9ca3af);
}

.input-text {
  font-size: 13px;
  color: var(--text-primary, #ffffff);
}

.input-cursor {
  display: inline-block;
  color: var(--color-primary);
  animation: cursor-blink 1s infinite;
  font-weight: bold;
  margin-left: 1px;
}

.send-btn {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue));
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.send-btn.sending {
  transform: scale(0.9);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.5);
}

.send-btn svg {
  width: 14px;
  height: 14px;
  color: white;
}

/* 邮件演示 */
.mail-demo {
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mail-flow {
  width: 100%;
  max-width: 500px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.flow-step {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.4s ease;
  opacity: 0.4;
}

.flow-step.active {
  opacity: 1;
  border-color: rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.05);
}

.flow-step.completed {
  opacity: 0.7;
  border-color: rgba(6, 182, 212, 0.3);
}

.flow-step.success {
  border-color: rgba(34, 197, 94, 0.3);
  background: rgba(34, 197, 94, 0.05);
}

.step-icon {
  width: 44px;
  height: 44px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-icon svg {
  width: 22px;
  height: 22px;
  color: var(--color-blue);
}

.flow-step.success .step-icon {
  background: rgba(34, 197, 94, 0.1);
}

.flow-step.success .step-icon svg {
  color: #22c55e;
}

.step-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  margin-bottom: 4px;
}

.step-content p {
  font-size: 13px;
  color: var(--text-secondary, #9ca3af);
}

.step-progress {
  margin-left: auto;
  width: 80px;
}

.progress-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-blue), #06b6d4);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.success-badge {
  margin-left: auto;
  width: 32px;
  height: 32px;
  background: rgba(34, 197, 94, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkmark {
  color: #22c55e;
  font-size: 18px;
  font-weight: 700;
}

/* 手机预览 */
.phone-preview {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%) translateX(100px);
  opacity: 0;
  transition: all 0.5s ease;
}

.phone-preview.show {
  transform: translateY(-50%) translateX(0);
  opacity: 1;
}

.phone-frame {
  width: 160px;
  height: 320px;
  background: #1a1a2e;
  border-radius: 24px;
  padding: 8px;
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.4),
    0 0 0 2px rgba(255, 255, 255, 0.1);
}

.phone-screen {
  width: 100%;
  height: 100%;
  background: var(--bg-tertiary, #000);
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.notification {
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px;
  animation: notificationPop 0.5s ease;
}

@keyframes notificationPop {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.app-icon {
  font-size: 14px;
}

.app-name {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  flex: 1;
}

.time {
  font-size: 10px;
  color: var(--text-secondary, #9ca3af);
}

.notification-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  margin-bottom: 4px;
}

.notification-body {
  font-size: 10px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.4;
}

/* CTA区域 */
.cta-section {
  padding: 100px 40px;
  text-align: center;
  background: radial-gradient(ellipse at center, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
}

.cta-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 12px;
}

.cta-desc {
  font-size: 16px;
  color: var(--text-secondary, #9ca3af);
  margin-bottom: 32px;
}

.btn-cta {
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
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
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(124, 58, 237, 0.4);
}

.btn-cta svg {
  width: 18px;
  height: 18px;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-title :deep(.glitch-text) {
    font-size: 36px;
  }

  .nav-links-left {
    display: none;
  }

  .phone-preview {
    display: none;
  }

  .tv-frame {
    padding: 12px;
  }
}
</style>
