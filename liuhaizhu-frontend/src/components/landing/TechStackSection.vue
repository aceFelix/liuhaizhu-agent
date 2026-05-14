<template>
  <section class="integrations-section" id="integrations">
    <div class="section-header">
      <span class="section-tag">// TECH STACK</span>
      <h2 class="section-title">技术栈支持</h2>
      <p class="section-desc">采用业界领先技术打造</p>
    </div>
    <div class="integrations-track">
      <!-- 第一行：正向滚动（从左到右） -->
      <div class="integrations-row-wrapper row-forward">
        <div class="integration-row-scroll">
          <a v-for="tech in firstRowTechs" :key="tech.name" :href="tech.url" target="_blank" class="integration-item">
            <img :src="tech.logo" :alt="tech.name" class="tech-logo" />
            <span class="tech-name">{{ tech.name }}</span>
          </a>
          <!-- 复制一份实现无缝循环 -->
          <a v-for="tech in firstRowTechs" :key="`${tech.name}-dup`" :href="tech.url" target="_blank" class="integration-item">
            <img :src="tech.logo" :alt="tech.name" class="tech-logo" />
            <span class="tech-name">{{ tech.name }}</span>
          </a>
        </div>
      </div>
      <!-- 第二行：反向滚动（从右到左） -->
      <div class="integrations-row-wrapper row-reverse">
        <div class="integration-row-scroll">
          <a v-for="tech in secondRowTechs" :key="tech.name" :href="tech.url" target="_blank" class="integration-item">
            <img :src="tech.logo" :alt="tech.name" class="tech-logo" />
            <span class="tech-name">{{ tech.name }}</span>
          </a>
          <!-- 复制一份实现无缝循环 -->
          <a v-for="tech in secondRowTechs" :key="`${tech.name}-dup`" :href="tech.url" target="_blank" class="integration-item">
            <img :src="tech.logo" :alt="tech.name" class="tech-logo" />
            <span class="tech-name">{{ tech.name }}</span>
          </a>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
// 技术栈数据
const firstRowTechs = [
  { name: 'AgentSkills', logo: '/svg/tech-stack/AgentSkills.svg', url: 'https://agentskills.io/home' },
  { name: 'AlibabaCloud', logo: '/svg/tech-stack/AlibabaCloud.svg', url: 'https://www.aliyun.com/' },
  { name: 'AlibabaCloudOSS', logo: '/svg/tech-stack/AlibabaCloudOSS.svg', url: 'https://oss.console.aliyun.com/overview' },
  { name: 'Axios', logo: '/svg/tech-stack/Axios.svg', url: 'https://axios-http.com/ru/' },
  { name: 'CloudServer', logo: '/svg/tech-stack/CloudServer.svg', url: 'https://www.aliyun.com/product/ecs' },
  { name: 'DashScope', logo: '/svg/tech-stack/DashScope.svg', url: 'https://bailian.console.aliyun.com/cn-beijing/#/home' },
  { name: 'Docker', logo: '/svg/tech-stack/Docker.svg', url: 'https://www.docker.com/' },
  { name: 'ElementPlus', logo: '/svg/tech-stack/ElementPlus.svg', url: 'https://element-plus.org/' },
  { name: 'Git', logo: '/svg/tech-stack/Git.svg', url: 'https://git-scm.com/' },
  { name: 'Gogs', logo: '/svg/tech-stack/Gogs.svg', url: 'https://gogs.chuckchen.top/' },
  { name: 'JWT', logo: '/svg/tech-stack/JWT.svg', url: 'https://www.jwt.io/' },
  { name: 'Jenkins', logo: '/svg/tech-stack/Jenkins.svg', url: 'https://www.jenkins.io/' },
  { name: 'MCP', logo: '/svg/tech-stack/MCP.svg', url: 'https://modelcontextprotocol.io/docs/getting-started/intro' },
  { name: 'MyBatis', logo: '/svg/tech-stack/MyBatis.svg', url: 'https://www.mybatis.net.cn/' },
  { name: 'MyBatisPlus', logo: '/svg/tech-stack/MyBatisPlus.svg', url: 'https://baomidou.com/' },
  { name: 'MySQL', logo: '/svg/tech-stack/MySQL.svg', url: 'https://www.mysql.com/' },
]

const secondRowTechs = [
  { name: 'MinIO', logo: '/svg/tech-stack/MinIO.svg', url: 'https://www.min.io/' },
  { name: 'Nginx', logo: '/svg/tech-stack/Nginx.svg', url: 'https://nginx.org/' },
  { name: 'NodeJS', logo: '/svg/tech-stack/NodeJS.svg', url: 'https://nodejs.org/zh-cn' },
  { name: 'Pinia', logo: '/svg/tech-stack/Pinia.svg', url: 'https://pinia.web3doc.top/' },
  { name: 'Qwen3', logo: '/svg/tech-stack/Qwen3.svg', url: 'https://www.qianwen.com/' },
  { name: 'RAG', logo: '/svg/tech-stack/RAG.svg', url: 'https://www.optimizely.com/optimization-glossary/retrieval-augmented-generation-rag/' },
  { name: 'RedisStack', logo: '/svg/tech-stack/RedisStack.svg', url: 'https://redis.io/blog/introducing-redis-stack/' },
  { name: 'SSE', logo: '/svg/tech-stack/SSE.svg', url: 'https://www.baeldung.com/spring-server-sent-events' },
  { name: 'SearXNG', logo: '/svg/tech-stack/SearXNG.svg', url: 'https://docs.searxng.org/' },
  { name: 'Spring', logo: '/svg/tech-stack/Spring.svg', url: 'https://spring.io/' },
  { name: 'SpringAI', logo: '/svg/tech-stack/SpringAI.svg', url: 'https://spring.io/projects/spring-ai' },
  { name: 'SpringBoot', logo: '/svg/tech-stack/SpringBoot.svg', url: 'https://spring.io/projects/spring-boot' },
  { name: 'SpringSecurity', logo: '/svg/tech-stack/SpringSecurity.svg', url: 'https://spring.io/projects/spring-security' },
  { name: 'Vite', logo: '/svg/tech-stack/Vite.svg', url: 'https://vitejs.cn/vite3-cn/' },
  { name: 'Vitest', logo: '/svg/tech-stack/Vitest.svg', url: 'https://cn.vitest.dev/' },
  { name: 'Vue3', logo: '/svg/tech-stack/Vue3.svg', url: 'https://vuejs.org/' },
]
</script>

<style scoped>
.integrations-section {
  padding: 100px 0;
  overflow: hidden;
  position: relative;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
  padding: 0 5%;
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

.integrations-track {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.integrations-row-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
  mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
  -webkit-mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
}

.integration-row-scroll {
  display: flex;
  gap: 32px;
  width: max-content;
}

.row-forward .integration-row-scroll {
  animation: scroll-left 40s linear infinite;
}

.row-reverse .integration-row-scroll {
  animation: scroll-right 40s linear infinite;
}

.integrations-row-wrapper:hover .integration-row-scroll {
  animation-play-state: paused;
}

@keyframes scroll-left {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

@keyframes scroll-right {
  0% {
    transform: translateX(-50%);
  }
  100% {
    transform: translateX(0);
  }
}

.integration-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  min-width: 100px;
  transition: all 0.3s;
  text-decoration: none;
}

.integration-item:hover {
  border-color: rgba(139, 92, 246, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
}

.tech-logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.tech-name {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
}
</style>
