<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="modal-overlay" @click.self="handleClose">
        <div class="contact-modal">
          <div class="modal-header">
            <h2>联系我们</h2>
            <button @click="handleClose" class="btn-close">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="modal-content">
            <div class="contact-section">
              <div class="section-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                </svg>
              </div>
              <h3>与我们合作？</h3>
              <p>业务：后端系统开发，个人网站设计，服务器搭建咨询，毕业设计咨询等等......</p>
            </div>

            <div class="contact-section">
              <div class="section-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                </svg>
              </div>
              <h3>or加入我们？</h3>
              <p>成为4132有活科技一员，贡献开源项目，为全人类社会发展做出哪怕一点微薄的贡献......</p>
            </div>

            <div class="contact-section">
              <div class="section-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                </svg>
              </div>
              <h3>联系方式</h3>
              <div class="contact-info">
                <div class="info-group">
                  <span class="info-label">邮箱：</span>
                  <div class="info-items">
                    <a href="mailto:contact@example.com" class="info-link">contact@example.com</a>
                  </div>
                </div>
                <div class="info-group">
                  <span class="info-label">GitHub：</span>
                  <a href="https://github.com/your-username" target="_blank" rel="noopener noreferrer" class="info-link">https://github.com/your-username</a>
                </div>
              </div>
            </div>

            <div class="qrcode-section">
              <div class="qrcode-item">
                <img src="/contactInformation/wechat-qrcode.png" alt="微信二维码" class="qrcode-img" />
                <span class="qrcode-label">WeChat: your_wechat</span>
              </div>
              <div class="qrcode-item">
                <img src="/contactInformation/qq-qrcode.png" alt="QQ二维码" class="qrcode-img" />
                <span class="qrcode-label">QQ: your_qq</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible'])

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.contact-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.btn-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-close:hover {
  background: var(--bg-tertiary);
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.modal-content {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.contact-section {
  margin-bottom: 24px;
}

.section-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(6, 182, 212, 0.2));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  color: #8b5cf6;
}

.contact-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.contact-section p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

.contact-info {
  margin-top: 12px;
}

.info-group {
  margin-bottom: 12px;
}

.info-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.info-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-link {
  font-size: 14px;
  color: #8b5cf6;
  text-decoration: none;
  transition: color 0.3s;
  word-break: break-all;
}

.info-link:hover {
  color: #06b6d4;
  text-decoration: underline;
}

.qrcode-section {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.qrcode-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.qrcode-img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 8px;
  background: white;
  padding: 4px;
}

.qrcode-label {
  font-size: 11px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .modal-overlay {
    padding: 12px;
  }

  .contact-modal {
    max-height: 85vh;
  }

  .modal-header {
    padding: 16px;
  }

  .modal-header h2 {
    font-size: 20px;
  }

  .modal-content {
    padding: 16px;
  }

  .qrcode-section {
    gap: 24px;
  }

  .qrcode-img {
    width: 70px;
    height: 70px;
  }

  .qrcode-label {
    font-size: 10px;
  }
}
</style>
