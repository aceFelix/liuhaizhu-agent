<template>
  <div class="landing-page" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <!-- 科幻背景效果 -->
    <CyberBackground />

    <!-- 导航栏 - 固定在顶部，不参与阻尼滚动 -->
    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="handleLogoClick" />
          <div class="nav-links-left">
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('features')">功能</a>
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('demo')">演示</a>
            <a href="javascript:;" class="nav-link" @click.prevent="navigateToPage('integrations')">集成</a>
            <!-- 管理员入口 - 仅管理员可见 -->
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
          <button @click="navigateToUpgradeVIP" class="btn-upgrade-vip" title="升级VIP">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <span>会员</span>
          </button>
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

    <!-- 阻尼滚动容器 - 固定视窗 -->
    <div class="scroll-viewbox" ref="scrollViewbox">
      <!-- 阻尼滚动内容 - 跟随滚动位置移动 -->
      <div class="scroll-content" ref="scrollContent">
        <!-- Hero 区域 -->
        <section class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          <span>AI 驱动 · 智能对话</span>
        </div>
        <h1 class="hero-title">
          <span class="title-line">下一代</span>
          <GlitchText text="AI 智能助手" class="title-highlight" />
        </h1>
        <p class="hero-desc">
          集成文档识别、联网搜索与知识库检索的全能AI平台。
          <br />智能对话实时流式响应，让对话更流畅自然。
        </p>
        <div class="hero-actions">
          <button @click="navigateToChat" class="btn-primary">
            <span class="btn-text">Try Now</span>
            <span class="btn-icon">→</span>
            <span class="btn-glow"></span>
          </button>
          <a href="javascript:;" class="btn-secondary" @click.prevent="navigateToPage('demo')">
            <span class="btn-text">查看演示</span>
          </a>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-value">∞</span>
            <span class="stat-label">对话有限制</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">&gt;1s</span>
            <span class="stat-label">响应速度</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">24/7</span>
            <span class="stat-label">非全天候服务</span>
          </div>
        </div>
      </div>

      <!-- 聊天预览演示 -->
      <DemoWindow />
    </section>

    <!-- 功能卡片区域 -->
    <FeaturesSection />

    <!-- 技术栈区域 -->
    <TechStackSection />

    <!-- VIP 会员区域 -->
    <VIPSection />

        <!-- 页脚区域 -->
        <FooterSection />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import { useTransitionStore } from '@/stores/transition'
import LoginModal from '@/components/common/LoginModal.vue'
import UserProfileModal from '@/components/user/UserProfileModal.vue'
import UserInfoCard from '@/components/user/UserInfoCard.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import GlitchText from '@/components/common/GlitchText.vue'
import FeaturesSection from '@/components/landing/FeaturesSection.vue'
import TechStackSection from '@/components/landing/TechStackSection.vue'
import VIPSection from '@/components/landing/VIPSection.vue'
import FooterSection from '@/components/landing/FooterSection.vue'
import DemoWindow from '@/components/landing/DemoWindow.vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['mounted'])

// 主题设置
const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const transitionStore = useTransitionStore()
const router = useRouter()

// 登录弹窗控制
const showLoginModal = ref(false)
const showResetPassword = ref(false)
// 用户资料编辑弹窗
const showUserProfileModal = ref(false)

// 跳转到聊天页（带抽卡动画）
const navigateToChat = async () => {
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

// 跳转到子页面（带抽卡动画）
const navigateToPage = async (page) => {
  transitionStore.startCardDraw(page)
  setTimeout(() => {
    router.push(`/${page}`)
  }, 600)
}

// 跳转到VIP升级页面
const navigateToUpgradeVIP = () => {
  transitionStore.startCardDraw('upgrade-vip')
  setTimeout(() => {
    router.push('/upgrade-vip')
  }, 600)
}

const onLoginSuccess = (userData) => {
  showLoginModal.value = false
  ElMessage.success('登录成功')
  // 管理员留在落地页，普通用户跳转到聊天页
  const role = userData?.role || authStore.user?.role
  if (role !== 'ADMIN') {
    navigateToChat()
  }
}

const handleLogout = () => {
  authStore.handleLogout()
  ElMessage.success('已退出登录')
}

const handleLogoClick = () => {
  window.location.reload()
}

// 打开用户资料编辑
const openUserProfile = () => {
  showUserProfileModal.value = true
}

// 处理资料更新成功
const handleProfileUpdate = () => {
  // 资料已更新，用户信息会自动同步
  console.log('用户资料已更新')
}

// 处理账户注销成功
const handleDeleteAccountSuccess = () => {
  authStore.handleLogout()
  ElMessage.success('账户已注销')
  router.push('/')
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 滑动阻尼效果
const scrollViewbox = ref(null)
const scrollContent = ref(null)
let scrollHandler = null
let resizeHandler = null

// 更新 body 高度以启用滚动
const updateBodyHeight = () => {
  if (scrollContent.value) {
    const contentHeight = scrollContent.value.offsetHeight
    // 设置 body 高度为内容高度，使滚动条可用
    document.body.style.height = `${contentHeight}px`
  }
}

// 处理滚动事件 - 内容跟随滚动位置移动
const handleScroll = () => {
  if (scrollContent.value) {
    const scrollY = window.scrollY || window.pageYOffset
    scrollContent.value.style.transform = `translateY(${-scrollY}px)`
  }
}

// 初始化主题
onMounted(() => {
  settingsStore.initTheme()

  // 初始化滑动阻尼效果
  // 使用 nextTick 确保 DOM 已渲染
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

onBeforeUnmount(() => {
  // 清理滑动阻尼事件监听
  if (scrollHandler) {
    window.removeEventListener('scroll', scrollHandler)
  }
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  // 恢复 body 高度
  document.body.style.height = ''
})
</script>

<style scoped>
/* 基础样式 */
.landing-page {
  min-height: 100vh;
  background: #0a0a0f;
  color: #e0e0e0;
  overflow-x: hidden;
  position: relative;
}

/* 滑动阻尼效果 - 固定视窗容器 */
.scroll-viewbox {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  z-index: 10;
}

/* 滑动阻尼效果 - 内容容器 */
.scroll-content {
  position: relative;
  width: 100%;
  will-change: transform;
  transition: transform 0.15s ease-out;
}

/* 导航栏 - 固定在顶部，z-index 高于滚动内容 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 40px;
  background: rgba(10, 10, 15, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.2);
}

.nav-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 48px;
}

.nav-links-left {
  display: flex;
  align-items: center;
  gap: 32px;
}



.logo-icon {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
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

.btn-upgrade-vip svg {
  filter: drop-shadow(0 1px 2px rgba(0,0,0,0.2));
}

.btn-login-nav {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-login-nav:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
  color: #fff;
}

.nav-link {
  color: #9ca3af;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover {
  color: var(--color-primary);
}

.nav-link.admin-link {
  color: #f59e0b;
  font-weight: 500;
}

.nav-link.admin-link:hover {
  color: #fbbf24;
}

.nav-link.admin-link svg {
  filter: drop-shadow(0 0 4px rgba(245, 158, 11, 0.5));
}

.btn-try-nav {
  position: relative;
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  transition:
    transform 0.3s,
    box-shadow 0.3s;
}

.btn-try-nav:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(139, 92, 246, 0.4);
}

/* Hero 区域 */
.hero-section {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 120px 48px 80px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  max-width: 1400px;
  margin: 0 auto;
  align-items: center;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 100px;
  font-size: 12px;
  color: #a78bfa;
  width: fit-content;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: var(--color-primary);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.hero-title {
  font-size: 66px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.title-line {
  display: block;
  color: #fff;
}

.title-highlight {
  display: block;
}

.title-highlight :deep(.glitch-text) {
  font-size: 66px;
  font-weight: 700;
}

.hero-desc {
  font-size: 18px;
  color: #9ca3af;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 32px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  transition:
    transform 0.3s,
    box-shadow 0.3s;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(139, 92, 246, 0.5);
}

.btn-primary .btn-icon {
  transition: transform 0.3s;
}

.btn-primary:hover .btn-icon {
  transform: translateX(4px);
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  padding: 16px 32px;
  background: transparent;
  color: #9ca3af;
  text-decoration: none;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-secondary:hover {
  color: #fff;
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.1);
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-top: 15px;
  padding-top: 30px;
  border-top: 1px solid rgba(139, 92, 246, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(139, 92, 246, 0.3);
}

/* 功能卡片区域 */
.features-section {
  position: relative;
  z-index: 1;
  padding: 100px 48px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-tag {
  font-family: monospace;
  font-size: 12px;
  color: var(--color-primary);
  letter-spacing: 2px;
}

.section-title {
  font-size: 40px;
  font-weight: 700;
  color: #fff;
  margin: 16px 0;
}

.section-desc {
  font-size: 16px;
  color: #6b7280;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  position: relative;
  padding: 32px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  transition: all 0.3s;
  overflow: hidden;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px);
  border-color: rgba(139, 92, 246, 0.5);
  box-shadow: 0 20px 40px rgba(139, 92, 246, 0.2);
}

.feature-card:hover .card-glow {
  opacity: 1;
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 50% 0%, rgba(139, 92, 246, 0.15) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.card-icon {
  width: 56px;
  height: 56px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 12px;
}

.card-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: rgba(139, 92, 246, 0.05);
  border-radius: 12px;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(139, 92, 246, 0.4);
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.2);
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
}

.profile-email {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 12px 0;
}

.profile-role {
  display: inline-block;
}

.role-badge {
  padding: 4px 12px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.profile-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 0 24px;
}

.profile-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 8px;
}

.profile-stats .stat-label {
  font-size: 12px;
  color: #6b7280;
}

.profile-stats .stat-value {
  font-size: 14px;
  color: #e0e0e0;
  font-weight: 500;
}

.profile-stats .stat-value.active {
  color: #10b981;
}

.btn-logout-full {
  width: 100%;
  padding: 12px 24px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-logout-full:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}
</style>
<style scoped>
.footer-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

.footer-status .status-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: statusPulse 2s ease-in-out infinite;
}

@keyframes statusPulse {
  0%, 100% {
    opacity: 1;
    box-shadow: 0 0 4px #10b981;
  }
  50% {
    opacity: 0.6;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hero-section {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }

  .hero-content {
    align-items: center;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-stats {
    justify-content: center;
  }

  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .nav-bar {
    padding: 0 16px;
  }

  .hero-section {
    padding: 100px 16px 60px;
    gap: 30px;
  }

  .hero-title {
    font-size: 28px;
    line-height: 1.3;
  }

  .hero-desc {
    font-size: 15px;
  }

  .hero-actions {
    flex-direction: column;
    width: 100%;
    padding: 0;
    gap: 12px;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
    padding: 14px 24px;
    font-size: 15px;
  }

  .hero-stats {
    flex-wrap: wrap;
    gap: 16px;
    justify-content: center;
  }

  .stat-item {
    min-width: 80px;
  }

  .stat-value {
    font-size: 24px;
  }

  .stat-label {
    font-size: 12px;
  }

  .stat-divider {
    display: none;
  }

  .features-section,
  .tech-section,
  .cta-section,
  .footer-section {
    padding: 60px 16px;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .feature-card {
    padding: 20px;
  }

  .section-title {
    font-size: 24px;
  }

  .section-subtitle {
    font-size: 14px;
    padding: 0 20px;
  }

  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .tech-card {
    padding: 16px;
  }

  .tech-icon {
    width: 40px;
    height: 40px;
  }

  .tech-name {
    font-size: 13px;
  }

  .cta-title {
    font-size: 24px;
  }

  .cta-desc {
    font-size: 14px;
    padding: 0 20px;
  }

  .cta-buttons {
    flex-direction: column;
    width: 100%;
    padding: 0 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
    text-align: center;
  }

  .footer-brand {
    align-items: center;
  }

  .footer-links {
    justify-content: center;
    flex-wrap: wrap;
    gap: 16px;
  }

  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}

/* 小屏幕手机适配 */
@media (max-width: 480px) {
  .hero-title {
    font-size: 24px;
  }

  .hero-badge {
    font-size: 12px;
    padding: 6px 12px;
  }

  .logo-text {
    font-size: 16px;
  }

  .demo-images {
    gap: 4px;
  }

  .demo-images img {
    width: 50px;
    height: 50px;
  }

  .feature-icon {
    width: 40px;
    height: 40px;
  }

  .feature-title {
    font-size: 16px;
  }

  .feature-desc {
    font-size: 13px;
  }

  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .tech-card {
    padding: 12px;
  }

  .tech-icon {
    width: 36px;
    height: 36px;
  }

  .tech-name {
    font-size: 12px;
  }
}

/* ===================
   浅色主题样式 - 类似第二张参考图的简洁现代风格
   =================== */
.landing-page.light-theme {
  background: #f8fafc;
  color: #1e293b;
}

.landing-page.light-theme .cyber-bg {
  display: none;
}

.landing-page.light-theme .nav-bar {
  background: rgba(255, 255, 255, 0.95);
  border-bottom: 1px solid #e2e8f0;
}

.landing-page.light-theme .logo-text {
  background: linear-gradient(135deg, #1e293b 0%, var(--color-primary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.landing-page.light-theme .logo-icon {
  color: var(--color-primary);
}

.landing-page.light-theme .nav-link {
  color: #64748b;
}

.landing-page.light-theme .nav-link:hover {
  color: var(--color-primary);
}

.landing-page.light-theme .nav-link.admin-link {
  color: #d97706;
}

.landing-page.light-theme .nav-link.admin-link:hover {
  color: #b45309;
}

.landing-page.light-theme .hero-badge {
  background: rgba(124, 58, 237, 0.08);
  border-color: rgba(124, 58, 237, 0.2);
  color: var(--color-primary);
}

.landing-page.light-theme .badge-dot {
  background: var(--color-primary);
}

.landing-page.light-theme .title-line {
  color: #1e293b;
}

.landing-page.light-theme .hero-desc {
  color: #64748b;
}

.landing-page.light-theme .btn-secondary {
  color: #1e293b;
  border-color: #e2e8f0;
  background: #fff;
}

.landing-page.light-theme .btn-secondary:hover {
  background: #f8fafc;
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.landing-page.light-theme .stat-value {
  color: #1e293b;
}

.landing-page.light-theme .stat-label {
  color: #64748b;
}

.landing-page.light-theme .stat-divider {
  background: #e2e8f0;
}

.landing-page.light-theme .hero-stats {
  border-top-color: #e2e8f0;
}

.landing-page.light-theme .section-tag {
  color: var(--color-primary);
}

.landing-page.light-theme .section-title {
  color: #1e293b;
}

.landing-page.light-theme .section-desc {
  color: #64748b;
}

.landing-page.light-theme .feature-card {
  background: #fff;
  border-color: #e2e8f0;
}

.landing-page.light-theme .feature-card:hover {
  border-color: var(--color-primary);
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.1);
}

.landing-page.light-theme .card-icon {
  background: rgba(124, 58, 237, 0.08);
  border-color: rgba(124, 58, 237, 0.2);
  color: var(--color-primary);
}

.landing-page.light-theme .card-title {
  color: #1e293b;
}

.landing-page.light-theme .card-desc {
  color: #64748b;
}

/* 浅色主题下的滑动阻尼样式 - 确保背景色一致 */
.landing-page.light-theme .scroll-viewbox {
  background: #f8fafc;
}
</style>
