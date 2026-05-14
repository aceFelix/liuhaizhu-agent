<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="policy-modal" @click.self="$emit('update:visible', false)">
        <div class="policy-content">
          <div class="policy-header">
            <h2>服务条款</h2>
            <button class="policy-close" @click="$emit('update:visible', false)">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="policy-body">
            <section>
              <h3>一、服务说明</h3>
              <ul>
                <li>本服务为个人免费项目，基于AI技术提供智能对话服务，仅供学习交流使用。</li>
                <li>我们不保证服务的稳定性、连续性，保留随时修改或终止服务的权利。</li>
              </ul>
            </section>
            <section>
              <h3>二、用户责任</h3>
              <p>使用本服务时，您承诺：</p>
              <ul>
                <li>不利用本服务从事任何违法或不当活动</li>
                <li>不攻击、破坏或试图入侵本系统</li>
                <li>不批量爬取、滥用API接口</li>
                <li>不利用AI生成虚假信息、诈骗内容或侵犯他人权益</li>
              </ul>
            </section>
            <section>
              <h3>三、免责声明</h3>
              <p><strong>重要提示：</strong></p>
              <ul>
                <li><strong>AI生成内容可能存在错误、偏见或不准确信息</strong>，仅供参考，不构成法律、医疗、金融等专业建议。</li>
                <li>用户需自行判断AI生成内容的准确性和适用性，我们对因使用AI生成内容造成的任何损失不承担责任。</li>
                <li>因网络、服务器故障导致的服务中断，我们不承担责任。</li>
                <li>本服务包含第三方链接，我们不对第三方内容负责。</li>
              </ul>
            </section>
            <section>
              <h3>四、知识产权</h3>
              <ul>
                <li>本网站的设计、代码归作者所有，欢迎学习交流，请勿商用。</li>
                <li>AI生成内容的知识产权归属遵循相关法律法规。</li>
              </ul>
            </section>
            <section>
              <h3>五、账户与安全</h3>
              <ul>
                <li>您有责任保护自己的账户安全，不要将账户信息透露给他人。</li>
                <li>如发现账户被盗用，请及时联系我们。</li>
                <li>我们有权对违规账户进行封禁处理。</li>
              </ul>
            </section>
            <section>
              <h3>六、法律适用</h3>
              <p>本条款适用中华人民共和国法律。如发生争议，双方应友好协商解决。</p>
            </section>
            <section>
              <h3>七、联系我们</h3>
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
