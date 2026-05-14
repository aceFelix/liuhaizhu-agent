<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="policy-modal" @click.self="$emit('update:visible', false)">
        <div class="policy-content">
          <div class="policy-header">
            <h2>常见问题</h2>
            <button class="policy-close" @click="$emit('update:visible', false)">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="policy-body">
            <section>
              <h3>Q: 这个AI助手是什么？</h3>
              <p>A: LHZ个人智能助手是一个基于大语言模型的AI对话平台，集成了联网搜索、知识库检索等功能，可以回答问题、协助写作、代码编程等多种任务。</p>
            </section>
            <section>
              <h3>Q: 这个服务是免费的吗？</h3>
              <p>A: 是的，本服务为个人免费项目，仅供学习交流使用。由于资源有限，可能会对使用频率进行一定限制。</p>
            </section>
            <section>
              <h3>Q: AI生成的内容准确吗？</h3>
              <p>A: AI生成的内容可能存在错误或不准确之处，仅供参考。对于重要决策，请务必核实信息来源，不要完全依赖AI的回答。</p>
            </section>
            <section>
              <h3>Q: 我的对话数据会被保存吗？</h3>
              <p>A: 对话记录会临时保存以提供连续对话体验。我们不会将您的个人数据出售给第三方，您也可以随时要求删除您的数据。</p>
            </section>
            <section>
              <h3>Q: 支持哪些功能？</h3>
              <p>A: 目前支持以下功能：</p>
              <ul>
                <li>智能对话：回答各类问题、协助写作</li>
                <li>联网搜索：实时获取网络信息</li>
                <li>知识库检索：查询专业知识库内容</li>
                <li>代码辅助：编程问题解答、代码生成</li>
              </ul>
            </section>
            <section>
              <h3>Q: 如何获得更好的回答？</h3>
              <p>A: 建议您：</p>
              <ul>
                <li>问题描述尽量清晰具体</li>
                <li>提供必要的背景信息</li>
                <li>复杂问题可以分步骤提问</li>
                <li>对回答不满意时可以追问或换个方式提问</li>
              </ul>
            </section>
            <section>
              <h3>Q: 为什么有时候回答很慢？</h3>
              <p>A: 可能的原因包括：网络延迟、服务器负载较高、问题复杂需要更多处理时间。请耐心等待，或稍后重试。</p>
            </section>
            <section>
              <h3>Q: 如何反馈问题或建议？</h3>
              <p>A: 您可以通过以下方式联系我们：</p>
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
  font-size: 15px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 8px;
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
  margin-bottom: 4px;
}

.policy-body li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--color-primary);
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
