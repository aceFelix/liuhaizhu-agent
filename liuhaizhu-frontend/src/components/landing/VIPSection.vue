<template>
  <section class="vip-section" id="vip">
    <div class="section-header">
      <span class="section-tag">// MEMBERSHIP</span>
      <h2 class="section-title">会员权益</h2>
      <p class="section-desc">选择适合您的使用方案</p>
    </div>

    <div class="vip-cards">
      <!-- 普通用户卡片 -->
      <div class="vip-card free-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" width="32" height="32" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <h3 class="card-title">普通用户</h3>
          <p class="card-subtitle">免费使用</p>
        </div>
        <ul class="feature-list">
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>基础对话功能</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>会话次数限制</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>标准响应速度</span>
          </li>
          <li class="feature-item disabled">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
            <span>知识库功能</span>
          </li>
          <li class="feature-item disabled">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
            <span>超长上下文</span>
          </li>
        </ul>
        <button class="card-btn free-btn" @click="navigateToChat">
          <span>免费开始</span>
        </button>
      </div>

      <!-- VIP 用户卡片 -->
      <div class="vip-card vip-card-highlight" @click="navigateToVIP">
        <div class="card-badge">推荐</div>
        <div class="card-header">
          <div class="card-icon vip-icon">
            <svg viewBox="0 0 24 24" width="32" height="32" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
          </div>
          <h3 class="card-title">VIP 会员</h3>
          <p class="card-subtitle">解锁全部功能</p>
        </div>
        <ul class="feature-list">
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>无限对话次数</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>个人知识库</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>超长上下文支持</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>优先响应速度</span>
          </li>
          <li class="feature-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <span>AI角色切换</span>
          </li>
        </ul>
        <button class="card-btn vip-btn">
          <span>立即升级</span>
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z"/>
          </svg>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'

const router = useRouter()
const transitionStore = useTransitionStore()

// 跳转到 VIP 升级页面
const navigateToVIP = () => {
  transitionStore.startCardDraw('upgrade-vip')
  setTimeout(() => {
    router.push('/upgrade-vip')
  }, 600)
}

// 跳转到聊天页
const navigateToChat = () => {
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}
</script>

<style scoped>
.vip-section {
  padding: 100px 5%;
  position: relative;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-tag {
  display: inline-block;
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  color: var(--color-primary);
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.section-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--color-primary);
}

.section-desc {
  font-size: 18px;
  color: var(--text-secondary);
  max-width: 500px;
  margin: 0 auto;
}

.vip-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 400px));
  gap: 32px;
  justify-content: center;
  max-width: 900px;
  margin: 0 auto;
}

.vip-card {
  position: relative;
  padding: 40px 32px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.vip-card:hover {
  transform: translateY(-8px);
}

.free-card {
  border-color: rgba(107, 114, 128, 0.3);
}

.free-card:hover {
  border-color: rgba(107, 114, 128, 0.5);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.vip-card-highlight {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(124, 58, 237, 0.05) 100%);
  border-color: rgba(139, 92, 246, 0.4);
  cursor: pointer;
}

.vip-card-highlight:hover {
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.2);
}

.card-badge {
  position: absolute;
  top: -12px;
  right: 24px;
  padding: 6px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-icon {
  width: 64px;
  height: 64px;
  background: rgba(107, 114, 128, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  margin: 0 auto 16px;
}

.vip-icon {
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.2) 0%, rgba(245, 158, 11, 0.1) 100%);
  color: #fbbf24;
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

.feature-list {
  list-style: none;
  margin-bottom: 32px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: var(--text-primary);
  font-size: 15px;
}

.feature-item svg {
  color: #01d676;
  flex-shrink: 0;
}

.feature-item.disabled {
  color: var(--text-secondary);
  opacity: 0.6;
}

.feature-item.disabled svg {
  color: #ef4444;
}

.card-btn {
  width: 100%;
  padding: 14px 24px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.free-btn {
  background: rgba(107, 114, 128, 0.2);
  color: var(--text-primary);
  border: 1px solid rgba(107, 114, 128, 0.3);
}

.free-btn:hover {
  background: rgba(107, 114, 128, 0.3);
  border-color: rgba(107, 114, 128, 0.5);
}

.vip-btn {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
}

.vip-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

@media (max-width: 768px) {
  .vip-section {
    padding: 60px 5%;
  }

  .section-title {
    font-size: 32px;
  }

  .vip-cards {
    grid-template-columns: 1fr;
    max-width: 400px;
  }
}
</style>
