<template>
  <span
    class="glitch-text"
    :class="{ 'text-glitching': isGlitching }"
    :data-text="text"
  >
    {{ text }}
    <span class="glitch-layer"></span>
  </span>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  text: {
    type: String,
    required: true
  },
  // 故障效果间隔时间（毫秒），默认3000ms
  interval: {
    type: Number,
    default: 3000
  },
  // 故障效果持续时间（毫秒），默认300ms
  duration: {
    type: Number,
    default: 300
  },
  // 是否自动启动
  autoStart: {
    type: Boolean,
    default: true
  }
})

const isGlitching = ref(false)
let glitchInterval = null

// 启动间歇性故障效果
const startGlitch = () => {
  // 先停止之前的定时器
  stopGlitch()

  // 立即触发一次
  triggerGlitch()

  // 设置定时器，每隔一段时间触发一次
  glitchInterval = setInterval(() => {
    triggerGlitch()
  }, props.interval)
}

// 触发一次故障效果
const triggerGlitch = () => {
  isGlitching.value = true
  setTimeout(() => {
    isGlitching.value = false
  }, props.duration)
}

// 停止故障效果
const stopGlitch = () => {
  if (glitchInterval) {
    clearInterval(glitchInterval)
    glitchInterval = null
  }
  isGlitching.value = false
}

onMounted(() => {
  if (props.autoStart) {
    startGlitch()
  }
})

onUnmounted(() => {
  stopGlitch()
})

defineExpose({
  startGlitch,
  stopGlitch,
  triggerGlitch
})
</script>

<style scoped>
.glitch-text {
  position: relative;
  display: inline-block;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-blue) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 文字故障效果 - 只在添加类名时触发 */
.glitch-text.text-glitching {
  animation: text-glitch-skew 0.3s infinite;
  text-shadow:
    2px 0 var(--color-primary),
    -2px 0 #ec4899;
}

.glitch-text.text-glitching::before,
.glitch-text.text-glitching::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 上层 - 紫色 */
.glitch-text.text-glitching::before {
  animation: text-glitch-1 0.5s infinite linear alternate-reverse;
  clip-path: polygon(0 0, 100% 0, 100% 45%, 0 45%);
  color: #a95cf0;
  -webkit-text-fill-color: #a855f7;
  text-shadow:
    0 0 10px rgba(168, 85, 247, 0.8),
    0 0 20px rgba(168, 85, 247, 0.5);
}

/* 下层 - 粉色 */
.glitch-text.text-glitching::after {
  animation: text-glitch-2 0.5s infinite linear alternate-reverse;
  clip-path: polygon(0 55%, 100% 55%, 100% 100%, 0 100%);
  color: #e952b4;
  -webkit-text-fill-color: #624bd2;
  text-shadow:
    0 0 10px rgba(35, 74, 190, 0.8),
    0 0 20px rgba(35, 16, 181, 0.5);
}

/* 额外的故障层 - 青色 */
.glitch-text.text-glitching .glitch-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  content: attr(data-text);
  animation: text-glitch-3 0.3s infinite linear;
  clip-path: polygon(0 30%, 100% 30%, 100% 70%, 0 70%);
  color: #06b6d4;
  -webkit-text-fill-color: #06b6d4;
  text-shadow:
    0 0 10px rgba(6, 182, 212, 0.8),
    0 0 20px rgba(6, 182, 212, 0.5);
}

@keyframes text-glitch-skew {
  0% { transform: skew(0deg) scale(1); }
  10% { transform: skew(-3deg) scale(1.02); }
  20% { transform: skew(3deg) scale(0.98); }
  30% { transform: skew(-2deg) scale(1.01); }
  40% { transform: skew(2deg) scale(0.99); }
  50% { transform: skew(-1deg) scale(1.005); }
  60% { transform: skew(1deg) scale(0.995); }
  70% { transform: skew(-0.5deg) scale(1.002); }
  80% { transform: skew(0.5deg) scale(0.998); }
  90% { transform: skew(-0.2deg) scale(1.001); }
  100% { transform: skew(0deg) scale(1); }
}

@keyframes text-glitch-1 {
  0% { transform: translateX(-8px) translateY(-2px); opacity: 1; }
  20% { transform: translateX(8px) translateY(2px); opacity: 0.8; }
  40% { transform: translateX(-6px) translateY(-1px); opacity: 1; }
  60% { transform: translateX(6px) translateY(1px); opacity: 0.9; }
  80% { transform: translateX(-4px) translateY(-0.5px); opacity: 1; }
  100% { transform: translateX(8px) translateY(2px); opacity: 0.8; }
}

@keyframes text-glitch-2 {
  0% { transform: translateX(8px) translateY(2px); opacity: 1; }
  20% { transform: translateX(-8px) translateY(-2px); opacity: 0.8; }
  40% { transform: translateX(6px) translateY(1px); opacity: 1; }
  60% { transform: translateX(-6px) translateY(-1px); opacity: 0.9; }
  80% { transform: translateX(4px) translateY(0.5px); opacity: 1; }
  100% { transform: translateX(-8px) translateY(-2px); opacity: 0.8; }
}

@keyframes text-glitch-3 {
  0% { transform: translateX(-12px); opacity: 0; }
  25% { transform: translateX(12px); opacity: 1; }
  50% { transform: translateX(-8px); opacity: 0.5; }
  75% { transform: translateX(8px); opacity: 0.8; }
  100% { transform: translateX(-12px); opacity: 0; }
}
</style>
