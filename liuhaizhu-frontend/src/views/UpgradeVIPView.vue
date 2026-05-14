<template>
  <div class="upgrade-vip-page" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <!-- 科幻背景效果 -->
    <CyberBackground />

    <!-- 导航栏 -->
    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="handleBackToHome" />
          <div class="nav-links-left">
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('features')">功能</a>
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('demo')">演示</a>
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('integrations')">集成</a>
            <router-link
              v-if="authStore.isAuthenticated && authStore.user?.role === 'ADMIN'"
              to="/admin"
              class="nav-link admin-link"
            >
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"/>
              </svg>
              <span>管理员</span>
            </router-link>
          </div>
        </div>
        <div class="nav-links">
          <ThemeToggle />
          <UserInfoCard
            :user="authStore.user"
            :show-edit-hint="true"
            @logout="handleLogout"
            @delete-account="handleDeleteAccountSuccess"
            @edit-profile="openUserProfile"
            @login="showLoginModal = true"
          />
          <button @click="navigateToChat" class="btn-try-nav">
            <span>立即体验</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 登录弹窗组件 -->
    <LoginModal
      v-model:showLogin="showLoginModal"
      v-model:showReset="showResetPassword"
      @login-success="onLoginSuccess"
    />

    <!-- 用户资料编辑弹窗 -->
    <UserProfileModal
      v-model:visible="showUserProfileModal"
      :user="authStore.user"
      @success="handleProfileUpdate"
    />

    <!-- 主内容区域 -->
    <div class="upgrade-content">
      <!-- VIP 状态卡片 -->
      <div class="vip-status-card">
        <div class="vip-icon">
          <svg viewBox="0 0 24 24" width="48" height="48" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
        </div>
        <h1 class="vip-title">
          <GlitchText text="成为VIP会员" class="title-gradient" />
          <span class="vip-badge">PRO</span>
        </h1>
        <p class="vip-desc">解锁会话无限制、超长上下文、构建个人知识库等高级功能</p>
        <div class="current-status" :class="{ 'is-vip': isVIP }">
          <span class="status-dot"></span>
          <span>{{ isVIP ? '您已是 VIP 会员' : '您当前是普通用户' }}</span>
        </div>
      </div>

      <!-- 升级方式选择 -->
      <div class="upgrade-options">
        <h2 class="options-title">选择升级方式</h2>
        <p class="options-subtitle">完成以下任意一项即可升级为 VIP</p>

        <div class="options-grid">
          <!-- 游戏挑战选项 -->
          <div class="option-card game-option" @click="selectOption('game')">
            <div class="option-glow"></div>
            <div class="option-icon">
              <svg viewBox="0 0 24 24" width="40" height="40" fill="currentColor">
                <path d="M21 6H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10 7H8v3H6v-3H3v-2h3V8h2v3h3v2zm4.5 2c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm4-3c-.83 0-1.5-.67-1.5-1.5S18.67 9 19.5 9s1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/>
              </svg>
            </div>
            <h3 class="option-title">游戏挑战</h3>
            <p class="option-desc">通关指定游戏即可升级</p>
            <ul class="option-features">
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>趣味游戏挑战</span>
              </li>
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>通关即送 VIP</span>
              </li>
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>无限次挑战机会</span>
              </li>
            </ul>
            <button class="option-btn">
              <span>开始挑战</span>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z"/>
              </svg>
            </button>
          </div>

          <!-- 答题挑战选项 -->
          <div class="option-card quiz-option" @click="selectOption('quiz')">
            <div class="option-glow"></div>
            <div class="option-icon">
              <svg viewBox="0 0 24 24" width="40" height="40" fill="currentColor">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
            </div>
            <h3 class="option-title">答题挑战</h3>
            <p class="option-desc">答题满分即可升级</p>
            <ul class="option-features">
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>知识问答挑战</span>
              </li>
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>满分即送 VIP</span>
              </li>
              <li>
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>多次答题机会</span>
              </li>
            </ul>
            <button class="option-btn">
              <span>开始答题</span>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- VIP 权益展示 -->
      <div class="vip-benefits">
        <h2 class="benefits-title">VIP 专属权益</h2>
        <div class="benefits-grid">
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <span class="benefit-text">无限对话次数</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
              </svg>
            </div>
            <span class="benefit-text">超长上下文token</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                <path d="M12 2l-5.5 9h11z M17.5 9l-5.5 9 5.5-9z M6.5 9h11l-5.5-9-5.5 9z"/>
              </svg>
            </div>
            <span class="benefit-text">个人专属知识库</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
              </svg>
            </div>
            <span class="benefit-text">7x24小时服务</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import { useTransitionStore } from '@/stores/transition'
import { ElMessage } from 'element-plus'
import LoginModal from '@/components/common/LoginModal.vue'
import UserProfileModal from '@/components/user/UserProfileModal.vue'
import UserInfoCard from '@/components/user/UserInfoCard.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import GlitchText from '@/components/common/GlitchText.vue'

const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const transitionStore = useTransitionStore()
const router = useRouter()

const showLoginModal = ref(false)
const showResetPassword = ref(false)
const showUserProfileModal = ref(false)

// 判断用户是否已经是VIP
const isVIP = computed(() => {
  return authStore.user?.role === 'VIP' || authStore.user?.role === 'ADMIN'
})

// 选择升级方式
const selectOption = (type) => {
  if (!authStore.isAuthenticated) {
    showLoginModal.value = true
    ElMessage.warning('请先登录后再升级VIP')
    return
  }

  if (type === 'game') {
    if (isVIP.value) {
      ElMessage.info('您已经是VIP会员了')
      return
    }
    ElMessage.info('游戏挑战功能即将上线，敬请期待！')
  } else if (type === 'quiz') {
    transitionStore.startCardDraw('quiz')
    setTimeout(() => {
      router.push('/quiz')
    }, 600)
  }
}

// 跳转到聊天页
const navigateToChat = () => {
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

// 跳转到其他页面
const navigateToPage = (page) => {
  transitionStore.startReverseCardDraw(page)
  setTimeout(() => {
    router.push(`/${page}`)
  }, 600)
}

// 返回首页
const handleBackToHome = () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

const onLoginSuccess = () => {
  showLoginModal.value = false
  ElMessage.success('登录成功')
}

const handleLogout = () => {
  authStore.handleLogout()
  ElMessage.success('已退出登录')
}

const handleDeleteAccountSuccess = () => {
  authStore.handleLogout()
  ElMessage.success('账号已删除')
}

const openUserProfile = () => {
  showUserProfileModal.value = true
}

const handleProfileUpdate = () => {
  console.log('用户资料已更新')
}
</script>

<style scoped>
.upgrade-vip-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a0f 0%, #12121c 50%, #0d0d15 100%);
  color: #e0e0e0;
  position: relative;
  overflow-x: hidden;
}

/* 导航栏样式 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(10, 10, 15, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-links-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  color: #9ca3af;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover {
  color: #a78bfa;
}

.admin-link {
  color: #fbbf24;
}

.admin-link:hover {
  color: #f59e0b;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-try-nav {
  position: relative;
  padding: 10px 20px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.btn-try-nav:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.btn-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 0%, rgba(255,255,255,0.2) 50%, transparent 100%);
  transform: translateX(-100%);
  transition: transform 0.6s;
}

.btn-try-nav:hover .btn-glow {
  transform: translateX(100%);
}

/* 主内容区域 */
.upgrade-content {
  padding-top: 100px;
  padding-bottom: 60px;
  max-width: 1200px;
  margin: 0 auto;
  padding-left: 24px;
  padding-right: 24px;
}

/* VIP 状态卡片 */
.vip-status-card {
  text-align: center;
  padding: 48px 32px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(124, 58, 237, 0.05) 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 24px;
  margin-bottom: 48px;
  position: relative;
  overflow: hidden;
}

.vip-status-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #7c3aed, #a855f7, #c084fc, #a855f7, #7c3aed);
  background-size: 200% 100%;
  animation: gradient-flow 3s linear infinite;
}

@keyframes gradient-flow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

.vip-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary) 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 32px rgba(251, 191, 36, 0.3);
}

.vip-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-gradient {
  background: linear-gradient(135deg, #bb00ff 0%, #9900ff 50%, #006eff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.vip-badge {
  font-size: 12px;
  padding: 4px 10px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary) 100%);
  color: white;
  border-radius: 20px;
  font-weight: 600;
}

.vip-desc {
  font-size: 16px;
  color: #9ca3af;
  margin-bottom: 24px;
}

.current-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(107, 114, 128, 0.2);
  border-radius: 30px;
  font-size: 14px;
  color: #9ca3af;
}

.current-status.is-vip {
  background: rgba(251, 191, 36, 0.15);
  color: #fbbf24;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #6b7280;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.current-status.is-vip .status-dot {
  background: #fbbf24;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 升级选项 */
.upgrade-options {
  margin-bottom: 48px;
}

.options-title {
  font-size: 24px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 8px;
}

.options-subtitle {
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 32px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.option-card {
  position: relative;
  padding: 32px;
  background: rgba(30, 30, 45, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.option-card:hover {
  transform: translateY(-8px);
  border-color: rgba(139, 92, 246, 0.5);
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.2);
}

.option-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.4s;
}

.option-card:hover .option-glow {
  opacity: 1;
}

.game-option:hover {
  border-color: rgba(59, 130, 246, 0.5);
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.2);
}

.quiz-option:hover {
  border-color: rgba(16, 185, 129, 0.5);
  box-shadow: 0 20px 40px rgba(16, 185, 129, 0.2);
}

.option-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(124, 58, 237, 0.1) 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a78bfa;
  margin-bottom: 20px;
}

.game-option .option-icon {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(37, 99, 235, 0.1) 100%);
  color: #00aaff;
}

.quiz-option .option-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(5, 150, 105, 0.1) 100%);
  color: #34d356;
}

.option-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.option-desc {
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 20px;
}

.option-features {
  list-style: none;
  margin-bottom: 24px;
}

.option-features li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  color: #d1d5db;
  font-size: 14px;
}

.option-features li svg {
  color: #01d676;
  flex-shrink: 0;
}

.option-btn {
  width: 100%;
  padding: 14px 24px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.game-option .option-btn {
  background: linear-gradient(135deg, #0472f7 0%, var(--color-primary) 100%);
}

.quiz-option .option-btn {
  background: linear-gradient(135deg, #01d676 0%, var(--color-primary) 100%);
}

.option-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.game-option .option-btn:hover {
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
}

.quiz-option .option-btn:hover {
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
}

/* VIP 权益展示 */
.vip-benefits {
  padding: 32px;
  background: rgba(30, 30, 45, 0.4);
  border: 1px solid rgba(139, 92, 246, 0.15);
  border-radius: 20px;
}

.benefits-title {
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(139, 92, 246, 0.08);
  border-radius: 12px;
  transition: all 0.3s;
}

.benefit-item:hover {
  background: rgba(139, 92, 246, 0.15);
  transform: translateX(4px);
}

.benefit-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(176, 36, 251, 0.2) 0%, rgba(176, 36, 251, 0.2) 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff700;
  flex-shrink: 0;
}

.benefit-text {
  font-size: 14px;
  font-weight: 500;
  color: #e0e0e0;
}

/* 浅色主题适配 */
.upgrade-vip-page.light-theme {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 50%, #e2e8f0 100%);
  color: #1f2937;
}

.upgrade-vip-page.light-theme .nav-bar {
  background: rgba(255, 255, 255, 0.9);
  border-bottom-color: rgba(124, 58, 237, 0.1);
}

.upgrade-vip-page.light-theme .nav-link {
  color: #64748b;
}

.upgrade-vip-page.light-theme .nav-link:hover {
  color: #7c3aed;
}

.upgrade-vip-page.light-theme .vip-status-card {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.08) 0%, rgba(124, 58, 237, 0.03) 100%);
  border-color: rgba(139, 92, 246, 0.15);
}

.upgrade-vip-page.light-theme .vip-desc,
.upgrade-vip-page.light-theme .options-subtitle,
.upgrade-vip-page.light-theme .option-desc {
  color: #64748b;
}

.upgrade-vip-page.light-theme .option-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(124, 58, 237, 0.15);
}

.upgrade-vip-page.light-theme .option-features li {
  color: #374151;
}

.upgrade-vip-page.light-theme .vip-benefits {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(124, 58, 237, 0.1);
}

.upgrade-vip-page.light-theme .benefit-item {
  background: rgba(139, 92, 246, 0.05);
}

.upgrade-vip-page.light-theme .benefit-item:hover {
  background: rgba(139, 92, 246, 0.1);
}

.upgrade-vip-page.light-theme .benefit-text {
  color: #374151;
}

.upgrade-vip-page.light-theme .current-status {
  background: rgba(156, 163, 175, 0.15);
  color: #64748b;
}

.upgrade-vip-page.light-theme .current-status.is-vip {
  background: rgba(251, 191, 36, 0.15);
  color: #d97706;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .nav-content {
    padding: 0 16px;
  }

  .nav-links-left {
    display: none;
  }

  .upgrade-content {
    padding-top: 80px;
    padding-left: 16px;
    padding-right: 16px;
  }

  .vip-status-card {
    padding: 32px 20px;
  }

  .vip-title {
    font-size: 28px;
    flex-direction: column;
    gap: 8px;
  }

  .options-grid {
    grid-template-columns: 1fr;
  }

  .option-card {
    padding: 24px;
  }

  .benefits-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
