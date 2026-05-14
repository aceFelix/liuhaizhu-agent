<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="policy-modal" @click.self="$emit('update:visible', false)">
        <div class="policy-content">
          <div class="policy-header">
            <h2>隐私政策</h2>
            <button class="policy-close" @click="$emit('update:visible', false)">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="policy-body">
            <section>
              <h3>一、信息收集</h3>
              <p>为了提供更好的服务，我们可能会收集以下信息：</p>
              <ul>
                <li><strong>账户信息</strong>：当您注册账户时，我们会收集用户名、邮箱等基本信息。</li>
                <li><strong>使用数据</strong>：包括您的对话记录、知识库、使用频率等。</li>
              </ul>
            </section>
            <section>
              <h3>二、信息使用</h3>
              <p>我们收集的信息将用于：</p>
              <ul>
                <li>提供、维护和改进我们的服务</li>
                <li>个性化您的使用体验</li>
                <li>保障服务安全，防止欺诈行为</li>
                <li>与您沟通，回应您的咨询和反馈</li>
              </ul>
            </section>
            <section>
              <h3>三、信息存储与保护</h3>
              <ul>
                <li>我们采用行业标准的安全措施保护您的数据，包括数据加密存储。</li>
                <li>我们不会将您的个人信息出售给第三方。</li>
                <li>您有权申请查看、修改或删除您的个人数据。</li>
              </ul>
            </section>
            <section>
              <h3>四、第三方服务</h3>
              <p>本服务集成了第三方AI服务，您的对话内容可能会被发送至第三方服务器进行处理。我们建议您不要在对话中输入敏感个人信息。</p>
            </section>
            <section>
              <h3>五、政策更新</h3>
              <p>我们可能会不时更新本隐私政策。更新后的政策将在本页面发布，建议您定期查阅。</p>
            </section>
            <section>
              <h3>六、联系我们</h3>
              <p>如有任何隐私相关问题，请通过以下方式联系我们：</p>
              <ul>
                <li>邮箱：contact@example.com</li>
                <li>GitHub：github.com/your-username</li>
              </ul>
            </section>
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

defineEmits(['update:visible'])
</script>

<style scoped>
.policy-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.policy-content {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  width: 100%;
  max-width: 720px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.policy-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
}

.policy-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.policy-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s;
}

.policy-close:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.policy-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.policy-body section {
  margin-bottom: 24px;
}

.policy-body section:last-child {
  margin-bottom: 0;
}

.policy-body h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 12px;
}

.policy-body p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 8px;
}

.policy-body ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.policy-body li {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  padding-left: 16px;
  position: relative;
  margin-bottom: 6px;
}

.policy-body li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--color-primary);
}

.policy-body strong {
  color: var(--text-primary);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .policy-content,
.modal-fade-leave-to .policy-content {
  transform: scale(0.95);
}
</style>
