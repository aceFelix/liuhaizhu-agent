<template>
  <div class="hero-demo" id="demo">
    <div
      class="demo-window"
      :class="{ 'glitching': isGlitching }"
      @click="triggerGlitch"
    >
      <!-- 故障效果层 -->
      <div class="glitch-overlay" v-if="isGlitching">
        <div class="static-noise"></div>
        <div class="scanlines"></div>
        <div class="glitch-lines"></div>
        <div class="color-shift"></div>
      </div>
      <div class="demo-header">
        <div class="window-controls">
          <span class="control red"></span>
          <span class="control yellow"></span>
          <span class="control green"></span>
        </div>
        <span class="window-title">AI 刘海柱智能聊天助手</span>
      </div>
      <div class="demo-chat">
        <div class="demo-message user">
          <div class="message-bubble">你好，请介绍一下你自己</div>
        </div>
        <div class="demo-message assistant">
          <div class="message-avatar">
            <img src="/images/ai-avatar.jpg" alt="AI" class="avatar-img" />
          </div>
          <div class="message-bubble">
            <span class="typing-text">{{ displayText }}</span>
            <span class="cursor" v-if="isTyping">|</span>
          </div>
        </div>
      </div>
      <div class="demo-input">
        <input type="text" placeholder="输入消息..." disabled />
        <button class="demo-send">发送</button>
      </div>
    </div>
    <!-- 装饰性HUD元素 -->
    <div class="hud-element hud-1">[ ACTIVE ]</div>
    <div class="hud-element hud-2">SYS:ONLINE</div>
    <div class="hud-corner corner-tl"></div>
    <div class="hud-corner corner-tr"></div>
    <div class="hud-corner corner-bl"></div>
    <div class="hud-corner corner-br"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// 打字机效果
const fullText =
  '你好！我是职业法师刘海柱，一个集成了联网搜索、知识库检索的贴心智能助手。我可以帮你回答问题、获取信息、分析文档。有什么需要帮助的吗？'
const displayText = ref('')
const isTyping = ref(true)
let typingInterval = null
let currentIndex = 0

const startTyping = () => {
  displayText.value = ''
  currentIndex = 0
  isTyping.value = true

  typingInterval = setInterval(() => {
    if (currentIndex < fullText.length) {
      displayText.value += fullText[currentIndex]
      currentIndex++
    } else {
      isTyping.value = false
      clearInterval(typingInterval)
      // 3秒后重新开始
      setTimeout(() => {
        startTyping()
      }, 3000)
    }
  }, 50)
}

// 故障闪烁效果
const isGlitching = ref(false)
let glitchTimeout = null

const triggerGlitch = () => {
  if (isGlitching.value) return

  isGlitching.value = true

  // 自动停止故障效果
  glitchTimeout = setTimeout(() => {
    isGlitching.value = false
  }, 300)
}

onMounted(() => {
  startTyping()
})

onBeforeUnmount(() => {
  if (typingInterval) {
    clearInterval(typingInterval)
  }
  if (glitchTimeout) {
    clearTimeout(glitchTimeout)
  }
})
</script>

<style scoped>
/* 演示窗口 */
.hero-demo {
  position: relative;
  perspective: 1000px;
}

.demo-window {
  background: var(--bg-tertiary, rgba(20, 20, 30, 0.9));
  border: 1px solid var(--border-color, rgba(139, 92, 246, 0.3));
  border-radius: 16px;
  overflow: hidden;
  box-shadow:
    0 20px 60px var(--shadow, rgba(0, 0, 0, 0.5)),
    0 0 40px rgba(139, 92, 246, 0.1);
  transform: rotateY(-5deg) rotateX(5deg);
  transition: transform 0.5s, background 0.3s, border-color 0.3s, box-shadow 0.3s;
  cursor: pointer;
  position: relative;
}

.demo-window:hover {
  transform: rotateY(0) rotateX(0);
}

/* 故障效果 */
.demo-window.glitching {
  animation: glitch-shake 0.1s infinite;
}

.glitch-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  pointer-events: none;
  overflow: hidden;
}

/* 静态噪点效果 */
.static-noise {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-radial-gradient(
    circle at 50% 50%,
    transparent 0%,
    rgba(255, 255, 255, 0.03) 1px,
    transparent 2px
  );
  background-size: 4px 4px;
  animation: static-flicker 0.05s infinite;
  opacity: 0.6;
}

@keyframes static-flicker {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.8; }
}

/* 扫描线效果 */
.scanlines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    var(--shadow, rgba(0, 0, 0, 0.3)) 2px,
    var(--shadow, rgba(0, 0, 0, 0.3)) 4px
  );
  animation: scanline-move 0.1s linear infinite;
}

@keyframes scanline-move {
  0% { transform: translateY(0); }
  100% { transform: translateY(4px); }
}

/* 故障线条 */
.glitch-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.glitch-lines::before,
.glitch-lines::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  height: 5px;
  background: rgba(139, 92, 246, 0.8);
  animation: glitch-line 0.2s infinite;
}

.glitch-lines::before {
  top: 30%;
  animation-delay: 0s;
}

.glitch-lines::after {
  top: 70%;
  animation-delay: 0.1s;
  background: rgba(131, 6, 159, 0.8);
}

@keyframes glitch-line {
  0%, 100% {
    transform: translateX(-100%);
    opacity: 0;
  }
  10%, 90% {
    opacity: 1;
  }
  50% {
    transform: translateX(100%);
  }
}

/* 颜色偏移效果 */
.color-shift {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    rgba(255, 0, 0, 0.1) 0%,
    transparent 33%,
    transparent 66%,
    rgba(0, 255, 255, 0.1) 100%
  );
  animation: color-shift-move 0.15s infinite;
  mix-blend-mode: screen;
}

@keyframes color-shift-move {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(10px); }
}

/* 整体抖动 */
@keyframes glitch-shake {
  0%, 100% { transform: translate(0, 0) rotateY(-5deg) rotateX(5deg); }
  10% { transform: translate(-2px, 2px) rotateY(-5deg) rotateX(5deg); }
  20% { transform: translate(2px, -2px) rotateY(-5deg) rotateX(5deg); }
  30% { transform: translate(-2px, -2px) rotateY(-5deg) rotateX(5deg); }
  40% { transform: translate(2px, 2px) rotateY(-5deg) rotateX(5deg); }
  50% { transform: translate(-1px, 1px) rotateY(-5deg) rotateX(5deg); }
  60% { transform: translate(1px, -1px) rotateY(-5deg) rotateX(5deg); }
  70% { transform: translate(-1px, -1px) rotateY(-5deg) rotateX(5deg); }
  80% { transform: translate(1px, 1px) rotateY(-5deg) rotateX(5deg); }
  90% { transform: translate(-2px, 0) rotateY(-5deg) rotateX(5deg); }
}

.demo-window.glitching:hover {
  animation: glitch-shake-hover 0.1s infinite;
}

@keyframes glitch-shake-hover {
  0%, 100% { transform: translate(0, 0); }
  10% { transform: translate(-2px, 2px); }
  20% { transform: translate(2px, -2px); }
  30% { transform: translate(-2px, -2px); }
  40% { transform: translate(2px, 2px); }
  50% { transform: translate(-1px, 1px); }
  60% { transform: translate(1px, -1px); }
  70% { transform: translate(-1px, -1px); }
  80% { transform: translate(1px, 1px); }
  90% { transform: translate(-2px, 0); }
}

.demo-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--bg-header, linear-gradient(135deg, #4c1d95 0%, #5b21b6 100%));
  border-bottom: 1px solid var(--border-color, rgba(139, 92, 246, 0.3));
}

.window-controls {
  display: flex;
  gap: 8px;
}

.control {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.control.red {
  background: #ef4444;
}
.control.yellow {
  background: #f59e0b;
}
.control.green {
  background: #10b981;
}

.window-title {
  font-size: 13px;
  color: #ffffff;
}

.demo-chat {
  padding: 24px;
  min-height: 280px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.demo-message {
  display: flex;
  gap: 12px;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.demo-message.user {
  flex-direction: row-reverse;
}

.demo-message.user .message-bubble {
  background: var(--message-user-bg, linear-gradient(135deg, #5b21b6 0%, var(--color-primary-dark) 100%));
  color: var(--message-user-text, #fff);
  border-radius: 16px 16px 4px 16px;
}

.message-avatar {
  width: 36px;
  height: 36px;
  background: var(--bg-secondary, rgba(139, 92, 246, 0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.message-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-bubble {
  padding: 12px 16px;
  background: var(--message-assistant-bg, rgba(139, 92, 246, 0.1));
  border: 1px solid var(--border-color, rgba(139, 92, 246, 0.2));
  border-radius: 16px 16px 16px 4px;
  font-size: 14px;
  line-height: 1.6;
  max-width: 320px;
  color: var(--message-assistant-text, #e5e7eb);
}

.cursor {
  animation: blink 1s infinite;
  color: var(--accent-color, #8b5cf6);
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}

.demo-input {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid var(--border-color, rgba(139, 92, 246, 0.2));
  background: var(--bg-secondary, rgba(15, 15, 25, 0.5));
}

.demo-input input {
  flex: 1;
  padding: 12px 16px;
  background: var(--input-bg, rgba(30, 30, 50, 0.8));
  border: 1px solid var(--border-color, rgba(139, 92, 246, 0.3));
  border-radius: 8px;
  color: var(--text-tertiary, #9ca3af);
  font-size: 14px;
}

.demo-send {
  padding: 12px 24px;
  background: var(--message-user-bg, linear-gradient(135deg, var(--color-primary) 0%, #8b5cf6 100%));
  color: var(--message-user-text, #fff);
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

/* HUD 装饰元素 */
.hud-element {
  position: absolute;
  font-family: monospace;
  font-size: 10px;
  color: var(--accent-color, rgba(124, 58, 237, 0.6));
  letter-spacing: 2px;
  opacity: 0.6;
}

.hud-1 {
  top: -30px;
  left: 20px;
  animation: flicker 3s infinite;
}

.hud-2 {
  bottom: -30px;
  right: 20px;
  animation: flicker 4s infinite;
}

@keyframes flicker {
  0%,
  90%,
  100% {
    opacity: 0.6;
  }
  95% {
    opacity: 0.2;
  }
}

.hud-corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color, rgba(139, 92, 246, 0.4));
}

.corner-tl {
  top: -10px;
  left: -10px;
  border-right: none;
  border-bottom: none;
}
.corner-tr {
  top: -10px;
  right: -10px;
  border-left: none;
  border-bottom: none;
}
.corner-bl {
  bottom: -10px;
  left: -10px;
  border-right: none;
  border-top: none;
}
.corner-br {
  bottom: -10px;
  right: -10px;
  border-left: none;
  border-top: none;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .demo-window {
    transform: none;
  }
}

@media (max-width: 768px) {
  .demo-window {
    border-radius: 12px;
  }

  .demo-header {
    padding: 10px 12px;
  }

  .demo-chat {
    padding: 12px;
    min-height: 200px;
  }

  .message-bubble {
    padding: 10px 14px;
    font-size: 13px;
  }
}
</style>
