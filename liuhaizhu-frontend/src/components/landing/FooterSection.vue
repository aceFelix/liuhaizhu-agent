<template>
  <!-- 底部CTA -->
  <section class="cta-section">
    <div class="cta-content">
      <h2 class="cta-title">准备好体验了吗？</h2>
      <p class="cta-desc">立即开始与AI刘海柱对话，感受智能助手的魅力</p>
      <button @click="handleStartChat" class="btn-cta">
        <span class="btn-text">开始对话</span>
        <span class="btn-icon">→</span>
        <span class="btn-glow"></span>
      </button>
    </div>
    <div class="cta-decoration">
      <div class="circuit-lines"></div>
    </div>
  </section>

  <!-- Footer -->
  <footer class="footer">
    <div class="footer-main">
      <!-- 左侧品牌区 -->
      <div class="footer-brand">
        <Logo />
        <p class="footer-desc">职业法师刘海柱，集成联网搜索、知识库检索与智能对话的全能AI平台。</p>
        <div class="footer-social">
          <div class="social-link-wrapper">
            <a href="javascript:;" class="social-link" title="微信" @click.prevent="toggleQrcode('wechat')">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.007-.264-.03-.406-.03zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.969-.982z"/>
              </svg>
            </a>
            <Transition name="qrcode-fade">
              <div v-if="showQrcode === 'wechat'" class="qrcode-popup">
                <img src="/contactInformation/wechat-qrcode.png" alt="微信二维码" class="qrcode-img" />
                <span class="qrcode-label">WeChat: your_wechat</span>
              </div>
            </Transition>
          </div>
          <div class="social-link-wrapper">
            <a href="javascript:;" class="social-link" title="QQ" @click.prevent="toggleQrcode('qq')">
              <svg viewBox="0 0 1024 1024" width="20" height="20" fill="currentColor">
                <path d="M824.8 613.2c-16-51.4-34.4-94.6-62.7-165.3C766.5 262.2 689.3 112 511.5 112 331.7 112 256.2 265.2 261 447.9c-28.4 70.8-46.7 113.7-62.7 165.3-34 109.5-23 154.8-14.6 155.8 18 2.2 70.1-82.4 70.1-82.4 0 49 25.2 112.9 79.8 159-26.4 8.1-85.7 29.9-71.6 53.8 11.4 19.3 196.2 12.3 249.5 6.3 53.3 6 238.1 13 249.5-6.3 14.1-23.8-45.3-45.7-71.6-53.8 54.6-46.2 79.8-110.1 79.8-159 0 0 52.1 84.6 70.1 82.4 8.5-1.1 19.5-46.4-14.5-155.8z"/>
              </svg>
            </a>
            <Transition name="qrcode-fade">
              <div v-if="showQrcode === 'qq'" class="qrcode-popup">
                <img src="/contactInformation/qq-qrcode.png" alt="QQ二维码" class="qrcode-img" />
                <span class="qrcode-label">QQ: your_qq</span>
              </div>
            </Transition>
          </div>
          <a href="https://github.com/your-username" target="_blank" rel="noopener noreferrer" class="social-link" title="GitHub">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
          </a>
        </div>
      </div>

      <!-- 右侧链接区 -->
      <div class="footer-links">
        <div class="footer-column">
          <h4>关于</h4>
          <a href="javascript:;" @click.prevent="navigateToAuthor">作者简介</a>
          <a href="javascript:;" @click.prevent="showLinks = true">相关链接</a>
        </div>
        <div class="footer-column">
          <h4>合作</h4>
          <a href="javascript:;" @click.prevent="navigateToTeam">团队介绍</a>
          <a href="javascript:;" @click.prevent="showContact = true">联系我们</a>
        </div>
        <div class="footer-column">
          <h4>资源</h4>
          <a href="https://juejin.cn/user/829490584885773" target="_blank" rel="noopener noreferrer">技术博客</a>
          <a href="javascript:;" @click.prevent="showFAQ = true">常见问题</a>
        </div>
        <div class="footer-column">
          <h4>条款</h4>
          <a href="javascript:;" @click.prevent="showPolicy = true">隐私政策</a>
          <a href="javascript:;" @click.prevent="showTerms = true">服务条款</a>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <div class="footer-copyright">
        <p>© 2026 LHZ个人智能助手. All Rights Reserved.</p>
        <div class="beian-links">
          <a href="https://beian.mps.gov.cn/#/query/webSearch?code=52011302005171" target="_blank" rel="noopener noreferrer" class="beian-link">
            <img src="/备案图标.png" alt="备案图标" class="beian-icon" />
            <span>贵公网安备52011302005171号</span>
          </a>
          <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer" class="beian-link icp-link">黔ICP备2026002527号-1</a>
        </div>
      </div>
      <div class="footer-status">
        <span class="status-dot"></span>
        <span>系统运行正常</span>
      </div>
    </div>
  </footer>

  <PrivacyPolicyModal v-model:visible="showPolicy" />
  <TermsOfServiceModal v-model:visible="showTerms" />
  <FAQModal v-model:visible="showFAQ" />
  <ContactUsModal v-model:visible="showContact" />
  <RelatedLinksModal v-model:visible="showLinks" />
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'
import Logo from '@/components/common/Logo.vue'
import PrivacyPolicyModal from '@/components/footing/PrivacyPolicyModal.vue'
import TermsOfServiceModal from '@/components/footing/TermsOfServiceModal.vue'
import FAQModal from '@/components/footing/FAQModal.vue'
import ContactUsModal from '@/components/footing/ContactUsModal.vue'
import RelatedLinksModal from '@/components/footing/RelatedLinksModal.vue'

const router = useRouter()
const transitionStore = useTransitionStore()
const showQrcode = ref(null)
const showPolicy = ref(false)
const showTerms = ref(false)
const showFAQ = ref(false)
const showContact = ref(false)
const showLinks = ref(false)

const toggleQrcode = (type) => {
  showQrcode.value = showQrcode.value === type ? null : type
}

const handleClickOutside = (e) => {
  if (!e.target.closest('.social-link-wrapper')) {
    showQrcode.value = null
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const handleStartChat = async () => {
  transitionStore.startCardDraw()
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

const navigateToAuthor = () => {
  transitionStore.startCardDraw('author')
  setTimeout(() => {
    router.push('/author')
  }, 600)
}

const navigateToTeam = () => {
  transitionStore.startCardDraw('team')
  setTimeout(() => {
    router.push('/team')
  }, 600)
}
</script>

<style scoped>
/* CTA Section */
.cta-section {
  padding: 100px 5%;
  position: relative;
  overflow: hidden;
}

.cta-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.cta-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--color-primary);
}

.cta-desc {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 32px;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 16px 40px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.4);
}

.btn-icon {
  transition: transform 0.3s;
}

.btn-cta:hover .btn-icon {
  transform: translateX(4px);
}

.cta-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circuit-lines {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 400px;
  background: radial-gradient(ellipse at center, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
}

/* Footer */
.footer {
  padding: 60px 5% 30px;
  border-top: 1px solid var(--border-color);
}

.footer-main {
  display: grid;
  grid-template-columns: 1.5fr 2fr;
  gap: 60px;
  margin-bottom: 40px;
}

.footer-brand {
  max-width: 300px;
}

.footer-brand :deep(.logo) {
  margin-bottom: 16px;
}

.footer-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 20px;
}

.footer-social {
  display: flex;
  gap: 12px;
}

.social-link {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-secondary);
  transition: all 0.3s;
}

.social-link:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
  transform: translateY(-2px);
}

.social-link-wrapper {
  position: relative;
}

.qrcode-popup {
  position: absolute;
  bottom: calc(100% + 12px);
  left: 50%;
  transform: translateX(-50%);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  z-index: 100;
}

.qrcode-popup::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 12px;
  height: 12px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
}

.qrcode-img {
  width: 120px;
  height: 120px;
  object-fit: contain;
  border-radius: 8px;
}

.qrcode-label {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.qrcode-fade-enter-active,
.qrcode-fade-leave-active {
  transition: all 0.2s ease;
}

.qrcode-fade-enter-from,
.qrcode-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(8px);
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.footer-column h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.footer-column a {
  display: block;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  margin-bottom: 10px;
  transition: color 0.3s;
}

.footer-column a:hover {
  color: #8b5cf6;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 30px;
  border-top: 1px solid var(--border-color);
  font-size: 13px;
  color: var(--text-secondary);
}

.footer-copyright {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-copyright p {
  margin: 0;
}

.beian-links {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.beian-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.3s ease;
}

.beian-link:hover {
  color: #8b5cf6;
}

.beian-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.footer-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer-bottom {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .beian-links {
    justify-content: center;
    gap: 12px;
  }

  .beian-icon {
    width: 14px;
    height: 14px;
  }
}
</style>
