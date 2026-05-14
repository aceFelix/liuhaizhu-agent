<template>
  <!-- 科幻背景效果 -->
  <div class="cyber-bg">
    <div class="grid-lines"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    <div class="glow-orb orb-3"></div>
  </div>
</template>

<script setup>
/**
 * 科幻背景组件
 * 提供赛博朋克风格的动态背景效果
 * 包含网格线和发光球体动画
 */
</script>

<style scoped>
.cyber-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

/* 确保背景在滚动内容之下 */
:global(.scroll-viewbox) ~ .cyber-bg,
:global(.landing-page) .cyber-bg {
  z-index: 0;
}

.grid-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(139, 92, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 92, 246, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--color-primary) 0%, transparent 70%);
  top: -50px;
  right: -50px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, #06b6d4 0%, transparent 70%);
  bottom: 10%;
  left: 30%;
  animation: float 10s ease-in-out infinite reverse;
}

.orb-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, #e6547b 0%, transparent 70%);
  top: 40%;
  left: -100px;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}
</style>
