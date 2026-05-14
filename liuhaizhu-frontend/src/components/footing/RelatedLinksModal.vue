<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="modal-overlay" @click.self="handleClose">
        <div class="links-modal">
          <div class="modal-header">
            <h2>相关链接</h2>
            <button @click="handleClose" class="btn-close">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="modal-content">
            <!-- 技术资源链接 -->
            <div class="links-section">
              <div class="section-header">
                <div class="section-icon">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z"/>
                  </svg>
                </div>
                <h3>技术资源链接</h3>
              </div>
              <div class="links-list">
                <a href="https://vuejs.org/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Vue.js官方文档</span>
                  <span class="link-desc">本项目前端框架</span>
                </a>
                <a href="https://pinia.web3doc.top/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Pinia官方文档</span>
                  <span class="link-desc">Vue3状态管理器</span>
                </a>
                <a href="https://spring.io/projects/spring-ai" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">SpringAI官方文档</span>
                  <span class="link-desc">Spring官方的AI应用框架</span>
                </a>
                <a href="https://docs.langchain4j.dev/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Langchain4j官方文档</span>
                  <span class="link-desc">专为Java打造的AI应用框架</span>
                </a>
              </div>
            </div>

            <!-- AI/大模型相关 -->
            <div class="links-section">
              <div class="section-header">
                <div class="section-icon">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                </div>
                <h3>AI/大模型相关</h3>
              </div>
              <div class="links-list">
                <a href="https://ollama.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Ollama</span>
                  <span class="link-desc">开放模型构建平台</span>
                </a>
                <a href="https://www.deepseek.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">DeepSeek</span>
                  <span class="link-desc">深度求索官方</span>
                </a>
                <a href="https://bailian.console.aliyun.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">DashScope</span>
                  <span class="link-desc">阿里云大模型百炼平台</span>
                </a>
                <a href="https://minimaxi.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">MiniMax</span>
                  <span class="link-desc">MiniMax系列模型</span>
                </a>
                <a href="https://bigmodel.cn/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">智谱AI</span>
                  <span class="link-desc">GLM系列模型</span>
                </a>
                <a href="https://www.moonshot.cn/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">月之暗面</span>
                  <span class="link-desc">Kimi系列模型</span>
                </a>
              </div>
            </div>

            <!-- 开发工具推荐 -->
            <div class="links-section">
              <div class="section-header">
                <div class="section-icon">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M22 7V5h-2V3H2v18h20v-2h2V7h-2zM4 19V5h14v14H4zm16-2h-2v-2h2v2zm0-4h-2v-2h2v2zm0-4h-2V7h2v2z"/>
                  </svg>
                </div>
                <h3>开发工具推荐</h3>
              </div>
              <div class="links-list">
                <a href="https://kiro.dev/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Kiro</span>
                  <span class="link-desc">亚马逊旗下的轻量级AI编程IDE</span>
                </a>
                <a href="https://www.trae.cn/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Trae</span>
                  <span class="link-desc">国产黑马字节旗下的AI编程IDE</span>
                </a>
                <a href="https://qoder.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Qoder</span>
                  <span class="link-desc">阿里旗下的智能体编程平台</span>
                </a>
                <a href="https://cursor.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Cursor</span>
                  <span class="link-desc">功能强大的AI编辑器和编码智能体</span>
                </a>
                <a href="https://opencode.ai/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">OpenCode</span>
                  <span class="link-desc">开源AI编程代理</span>
                </a>
              </div>
            </div>

            <!-- 实用工具网站 -->
            <div class="links-section">
              <div class="section-header">
                <div class="section-icon">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
                  </svg>
                </div>
                <h3>实用工具网站</h3>
              </div>
              <div class="links-list">
                <a href="https://mcp.so/zh" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">MCP Server</span>
                  <span class="link-desc">MCP服务与客户端</span>
                </a>
                <a href="https://www.modelscope.cn/mcp" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">ModelScope</span>
                  <span class="link-desc">魔塔社区MCP广场</span>
                </a>
                <a href="https://skillsmp.com/zh" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Agent Skills</span>
                  <span class="link-desc">AgentSkills市场</span>
                </a>
                <a href="https://www.iconfont.cn/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">IconFont</span>
                  <span class="link-desc">阿里巴巴矢量图标库</span>
                </a>
                <a href="https://ui-ux-pro-max-skill.nextlevelbuilder.io/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">UI UX Pro Max</span>
                  <span class="link-desc">AI全能设计Skill</span>
                </a>
                <a href="https://gsap.com/docs/v3/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">GSAP</span>
                  <span class="link-desc">业界顶尖的JavaScript动画库</span>
                </a>
              </div>
            </div>

            <!-- 社区/学习资源 -->
            <div class="links-section">
              <div class="section-header">
                <div class="section-icon">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                  </svg>
                </div>
                <h3>社区/学习资源</h3>
              </div>
              <div class="links-list">
                <a href="https://juejin.cn/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">稀土掘金</span>
                  <span class="link-desc">技术社区</span>
                </a>
                <a href="https://www.oschina.net/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">OSCHINA</span>
                  <span class="link-desc">开源中国社区</span>
                </a>
                <a href="https://gitee.com/explore" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">Gitee</span>
                  <span class="link-desc">国内开源社区及代码托管平台</span>
                </a>
                <a href="https://github.com/" target="_blank" rel="noopener noreferrer" class="link-item">
                  <span class="link-name">GitHub</span>
                  <span class="link-desc">全球最大的开源社区及代码托管平台</span>
                </a>
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

.links-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  max-width: 700px;
  width: 100%;
  max-height: 85vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
}

.modal-header h2 {
  font-size: 22px;
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
  padding: 20px 24px;
  overflow-y: auto;
}

.links-section {
  margin-bottom: 24px;
}

.links-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.section-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(6, 182, 212, 0.2));
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8b5cf6;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.links-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 42px;
}

.link-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.link-item:hover {
  border-color: rgba(139, 92, 246, 0.5);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(6, 182, 212, 0.1));
  transform: translateX(4px);
}

.link-name {
  font-size: 14px;
  font-weight: 500;
  color: #8b5cf6;
  white-space: nowrap;
}

.link-desc {
  font-size: 13px;
  color: var(--text-secondary);
  position: relative;
  padding-left: 12px;
}

.link-desc::before {
  content: '-';
  position: absolute;
  left: 0;
  color: var(--text-muted);
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

  .links-modal {
    max-height: 90vh;
  }

  .modal-header {
    padding: 16px 20px;
  }

  .modal-header h2 {
    font-size: 20px;
  }

  .modal-content {
    padding: 16px 20px;
  }

  .links-list {
    padding-left: 0;
  }

  .link-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .link-desc {
    padding-left: 0;
  }

  .link-desc::before {
    display: none;
  }
}
</style>
