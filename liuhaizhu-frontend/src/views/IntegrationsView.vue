<template>
  <div class="integrations-page">
    <CyberBackground />

    <!-- 导航栏 -->
    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="goBackToLanding" />
          <div class="nav-links-left">
            <router-link to="/features" class="nav-link">功能</router-link>
            <router-link to="/demo" class="nav-link">演示</router-link>
            <router-link to="/integrations" class="nav-link active">集成</router-link>
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
          <button @click="navigateToChat" class="btn-primary">立即体验</button>
        </div>
      </div>
    </nav>

    <!-- 阻尼滚动视窗 -->
    <div class="scroll-viewbox">
      <!-- 滚动内容 -->
      <div class="scroll-content" ref="scrollContent">
        <!-- 主内容 -->
        <main class="main-content">
      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <div class="hero-badge">
            <span class="badge-icon">🔌</span>
            <span>开放生态</span>
          </div>
          <h1 class="hero-title">
            <GlitchText text="无缝集成" />
          </h1>
          <p class="hero-desc">
            刘海柱AI智能助手支持多种第三方服务集成，<br>
            打造强大的AI应用生态系统
          </p>
        </div>

        <!-- 集成统计 -->
        <div class="stats-grid">
          <div class="stat-card" v-for="(stat, index) in stats" :key="index">
            <div class="stat-number">{{ stat.number }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </section>

      <!-- 向量数据库集成 -->
      <section class="integration-section">
        <div class="section-container">
          <div class="section-header">
            <div class="header-icon vector">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
              </svg>
            </div>
            <div class="header-content">
              <h2 class="section-title">向量数据库集成</h2>
              <p class="section-subtitle">高性能向量检索，支持海量知识库存储与查询</p>
            </div>
          </div>

          <div class="integration-grid">
            <div
              v-for="(db, index) in vectorDatabases"
              :key="index"
              class="integration-card"
              :class="{ 'featured': db.featured }"
              @mouseenter="activeCard = `vector-${index}`"
              @mouseleave="activeCard = null"
            >
              <div class="card-glow" v-if="activeCard === `vector-${index}`"></div>
              <div class="card-content">
                <div class="card-header">
                  <div class="db-icon">
                    <img :src="`/svg/integration/${db.name}.svg`" :alt="db.name" class="db-logo" />
                  </div>
                  <div class="db-status" :class="db.status">
                    <span class="status-dot"></span>
                    {{ db.statusText }}
                  </div>
                </div>
                <h3 class="db-name">{{ db.name }}</h3>
                <p class="db-desc">{{ db.description }}</p>
                <div class="db-features">
                  <span v-for="(feature, fIndex) in db.features" :key="fIndex" class="feature-tag">
                    {{ feature }}
                  </span>
                </div>
              </div>
              <div class="card-footer">
                <a :href="db.docUrl" target="_blank" rel="noopener noreferrer" class="btn-link">
                  查看文档
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M7 17L17 7M17 7H7M17 7V17" />
                  </svg>
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- MCP服务集成 -->
      <section class="integration-section dark">
        <div class="section-container">
          <div class="section-header">
            <div class="header-icon mcp">
              <svg viewBox="0 0 1024 1024" fill="currentColor">
                <path d="M424.64 115.392a170.432 170.432 0 0 1 289.664 139.008 166.464 166.464 0 0 1 137.728 282.752l-307.84 307.712 94.912 94.848a36.736 36.736 0 0 1-51.712 51.84l-120.768-120.768a36.544 36.544 0 0 1 0-51.712l333.568-333.568a93.312 93.312 0 0 0-131.648-132.096l-3.008 2.88v0.128L428.352 592.96a36.544 36.544 0 0 1-56.064-5.44c-0.576-0.768-0.96-1.664-1.472-2.432a36.288 36.288 0 0 1-4.352-23.68l0.32-1.92a36.352 36.352 0 0 1 5.12-12.608l4.8-5.76 236.992-236.416 6.72-7.488A97.28 97.28 0 0 0 476.288 167.04L136.576 507.008a36.544 36.544 0 0 1-51.712-51.712l339.84-339.84z m94.912 94.4a36.544 36.544 0 0 1 51.008 52.352L329.024 497.856a98.368 98.368 0 1 0 138.624 139.52l240.64-243.584a36.672 36.672 0 0 1 52.096 51.328l-240.64 243.584A171.584 171.584 0 1 1 277.952 445.44l241.6-235.648z" />
              </svg>
            </div>
            <div class="header-content">
              <h2 class="section-title">MCP服务集成</h2>
              <p class="section-subtitle">Model Context Protocol，标准化AI模型上下文交互协议</p>
            </div>
          </div>

          <div class="mcp-showcase">
            <div class="mcp-visual">
              <div class="mcp-center">
                <div class="center-core">
                  <span>MCP</span>
                </div>
                <div class="core-rings">
                  <div class="ring ring-1"></div>
                  <div class="ring ring-2"></div>
                  <div class="ring ring-3"></div>
                </div>
              </div>
              <div class="mcp-nodes">
                <div
                  v-for="(node, index) in mcpNodes"
                  :key="index"
                  class="mcp-node"
                  :style="{
                    '--angle': `${index * (360 / mcpNodes.length)}deg`,
                    '--delay': `${index * 0.1}s`
                  }"
                >
                  <div class="node-icon">{{ node.icon }}</div>
                  <div class="node-label">{{ node.name }}</div>
                </div>
              </div>
            </div>

            <div class="mcp-info">
              <div class="info-card" v-for="(info, index) in mcpInfos" :key="index">
                <div class="info-icon">{{ info.icon }}</div>
                <h4>{{ info.title }}</h4>
                <p>{{ info.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- AgentSkills集成 -->
      <section class="integration-section">
        <div class="section-container">
          <div class="section-header">
            <div class="header-icon agent">
              <svg viewBox="0 0 1024 1024" fill="currentColor">
                <path d="M812.107852 126.445037a41.566815 41.566815 0 1 1 58.785185 58.785185l-271.928889 271.815111 129.137778 129.175704a187.733333 187.733333 0 0 1 40.656593-4.475259l9.633185 0.265481a186.974815 186.974815 0 0 1 173.89037 222.738963 41.604741 41.604741 0 0 1-75.851852 14.449778 41.453037 41.453037 0 0 0-29.582222-18.887111l-5.385481-0.379259c-22.945185 0-41.528889 18.583704-41.528889 41.566814l0.379259 5.385482c1.592889 12.325926 8.647111 23.021037 18.887111 29.582222a41.566815 41.566815 0 0 1-14.411852 75.813926 186.974815 186.974815 0 0 1-222.738963-173.89037l-0.303407-9.633185c0-13.918815 1.554963-27.534222 4.475259-40.656593l-129.175704-129.137778-1.744592 1.782519a41.566815 41.566815 0 0 1-29.354667 12.174222H318.464l-157.582222 157.620148a41.566815 41.566815 0 0 0 0 58.785185l7.130074 7.130074a41.566815 41.566815 0 0 0 58.785185 0l128.18963-128.227555a41.566815 41.566815 0 0 1 58.785185 58.747259l-128.227556 128.227556a124.662519 124.662519 0 0 1-176.317629 0l-7.130074-7.130075a124.662519 124.662519 0 0 1 0-176.279703L271.928889 541.961481l3.034074-2.768592c7.395556-6.068148 16.687407-9.40563 26.358518-9.40563h107.444149l18.204444-18.280296 0.758519-0.758519 0.758518-0.758518 81.73037-81.654519 0.606815-0.682666 0.758519-0.758519 300.562963-300.487111z m-296.239408 413.733926l146.62163 146.659556c11.643259 11.643259 15.322074 29.051259 9.367704 44.373333a103.61363 103.61363 0 0 0-6.978371 37.546667l0.530963 10.619259a103.917037 103.917037 0 0 0 52.717037 80.023703 125.383111 125.383111 0 0 1-1.024-9.671111l-0.303407-8.267851a124.662519 124.662519 0 0 1 124.700444-124.662519l8.229926 0.303407c3.26163 0.227556 6.485333 0.568889 9.671111 1.024a103.917037 103.917037 0 0 0-80.023703-52.717037l-10.619259-0.530963c-13.312 0-25.941333 2.503111-37.546667 6.978371-15.322074 5.95437-32.730074 2.275556-44.373333-9.367704l-146.659556-146.62163-24.310519 24.272593z" p-id="28478"></path><path d="M228.54163 41.566815a186.974815 186.974815 0 0 1 182.461629 227.555555l54.689185 54.727111a41.528889 41.528889 0 1 1-58.747259 58.74726L334.810074 310.46163a41.566815 41.566815 0 0 1-9.40563-44.41126 103.803259 103.803259 0 0 0-46.269629-128.189629A124.662519 124.662519 0 0 1 137.860741 279.134815a103.803259 103.803259 0 0 0 128.189629 46.269629c15.397926-5.95437 32.805926-2.275556 44.449186 9.367704l72.135111 72.135111a41.528889 41.528889 0 1 1-58.785186 58.785185l-54.689185-54.727111a186.974815 186.974815 0 0 1-224.104296-218.453333l1.706667-6.068148a41.604741 41.604741 0 0 1 74.107259-8.343704 41.566815 41.566815 0 1 0 57.268148-57.268148 41.566815 41.566815 0 0 1 14.449778-75.851852c11.643259-2.275556 23.665778-3.451259 36.029629-3.451259z" />
              </svg>
            </div>
            <div class="header-content">
              <h2 class="section-title">AgentSkills集成</h2>
              <p class="section-subtitle">可扩展的AI技能系统，让助手具备专业能力</p>
            </div>
          </div>

          <div class="skills-timeline">
            <div class="timeline-line"></div>
            <div
              v-for="(skill, index) in agentSkills"
              :key="index"
              class="skill-item"
              :class="{ 'reverse': index % 2 === 1 }"
              :style="{ '--delay': index * 0.15 + 's' }"
            >
              <div class="skill-content">
                <div class="skill-icon">{{ skill.icon }}</div>
                <h3 class="skill-name">{{ skill.name }}</h3>
                <p class="skill-desc">{{ skill.description }}</p>
                <div class="skill-capabilities">
                  <div
                    v-for="(cap, cIndex) in skill.capabilities"
                    :key="cIndex"
                    class="capability-item"
                  >
                    <span class="cap-check">✓</span>
                    <span>{{ cap }}</span>
                  </div>
                </div>
              </div>
              <div class="skill-node">
                <div class="node-pulse"></div>
              </div>
              <div class="skill-empty"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA区域 -->
      <section class="cta-section">
        <div class="cta-container">
          <div class="cta-content">
            <h2 class="cta-title">开始构建您的智能助手</h2>
            <p class="cta-desc">使用您的MCPServer和AgentSkills，快速集成AI能力到您的智能助手中</p>
            <div class="cta-buttons">
              <button class="btn-primary-large">
                <span>查看构建文档</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </button>
              <button @click="navigateToChat" class="btn-secondary-large">
                <span>立即体验</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </div>
          <div class="cta-visual">
            <div class="code-preview">
              <div class="code-header">
                <div class="code-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <span class="code-title">McpConfig.java</span>
              </div>
              <pre class="code-content"><code><span class="code-annotation">@Configuration</span>
<span class="code-keyword">public class</span> <span class="code-class">McpConfig</span> {

  <span class="code-annotation">@Bean</span>
  <span class="code-keyword">public</span> McpClient <span class="code-method">mcpClient</span>() {
    <span class="code-keyword">return</span> McpClient.builder()
      .serverUrl(<span class="code-string">"http://localhost:3000"</span>)
      .build();
  }

  <span class="code-annotation">@Bean</span>
  <span class="code-keyword">public</span> ToolCallbackProvider <span class="code-method">mcpTools</span>(
      McpClient mcpClient) {
    <span class="code-keyword">return</span> <span class="code-keyword">new</span> McpToolCallbackProvider(mcpClient);
  }
}</code></pre>
            </div>
          </div>
        </div>
      </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import GlitchText from '@/components/common/GlitchText.vue'

const router = useRouter()
const transitionStore = useTransitionStore()

const goBackToLanding = () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

const navigateToChat = () => {
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

const navigateToUpgradeVIP = () => {
  transitionStore.startCardDraw('upgrade-vip')
  setTimeout(() => {
    router.push('/upgrade-vip')
  }, 600)
}

// 阻尼滚动
const scrollContent = ref(null)
let scrollHandler = null
let resizeHandler = null

// 更新 body 高度以启用滚动
const updateBodyHeight = () => {
  if (scrollContent.value) {
    const contentHeight = scrollContent.value.offsetHeight
    document.body.style.height = `${contentHeight}px`
  }
}

// 处理滚动事件
const handleScroll = () => {
  if (scrollContent.value) {
    const scrollY = window.scrollY || window.pageYOffset
    scrollContent.value.style.transform = `translateY(${-scrollY}px)`
  }
}

const activeCard = ref(null)

const stats = [
  { number: '3+', label: '向量数据库' },
  { number: '10+', label: 'MCP服务' },
  { number: '8+', label: 'Agent技能' },
  { number: '99.9%', label: '服务可用性' }
]

const vectorDatabases = [
  {
    name: 'Redis Stack',
    status: 'active',
    statusText: '已集成',
    featured: true,
    description: '基于Redis的高性能向量存储，支持实时检索，延迟低于10ms',
    features: ['实时检索', '内存存储', '低延迟', '高并发'],
    docUrl: 'https://redis.io/about/redis-stack/'
  },
  {
    name: 'Milvus',
    status: 'coming',
    statusText: '即将支持',
    featured: false,
    description: '开源向量数据库，专为AI应用设计，支持十亿级向量存储',
    features: ['海量存储', '分布式', 'GPU加速', '多索引类型'],
    docUrl: 'https://milvus.io/docs/zh'
  },
  {
    name: 'Qdrant',
    status: 'coming',
    statusText: '即将支持',
    featured: false,
    description: '开源向量搜索引擎，Rust编写，高性能、可扩展的向量相似性搜索',
    features: ['Rust高性能', '过滤搜索', '分布式', 'REST/gRPC'],
    docUrl: 'https://qdrant.tech/qdrant-vector-database/'
  }
]

const mcpNodes = [
  { name: '文件系统', icon: '📁' },
  { name: '数据库', icon: '🗄️' },
  { name: 'API网关', icon: '🌐' },
  { name: '消息队列', icon: '📨' },
  { name: '缓存服务', icon: '⚡' },
  { name: '搜索引擎', icon: '🔍' }
]

const mcpInfos = [
  {
    icon: '🔌',
    title: '标准化协议',
    description: '统一的上下文交互标准，降低集成复杂度'
  },
  {
    icon: '⚡',
    title: '即插即用',
    description: '支持多种服务快速接入，无需重复开发'
  },
  {
    icon: '🔒',
    title: '安全可靠',
    description: '内置认证与权限控制，保障数据安全'
  }
]

const agentSkills = [
  {
    icon: '💻',
    name: '代码助手',
    description: '专业的编程辅助能力，支持多种编程语言和框架',
    capabilities: ['代码生成', 'Bug修复', '代码审查', '架构建议']
  },
  {
    icon: '🔍',
    name: '搜索专家',
    description: '智能信息检索与整合，快速获取精准答案',
    capabilities: ['全网搜索', '信息整合', '来源验证', '摘要生成']
  },
  {
    icon: '📊',
    name: '数据分析师',
    description: '数据可视化与洞察提取，辅助业务决策',
    capabilities: ['数据清洗', '可视化', '趋势分析', '报告生成']
  },
  {
    icon: '✍️',
    name: '内容创作',
    description: '多场景文案创作，提升内容生产效率',
    capabilities: ['文章撰写', '营销文案', '技术文档', '创意写作']
  }
]

onMounted(() => {
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

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible')
          }
        })
      },
      { threshold: 0.1 }
    )

    document.querySelectorAll('.skill-item').forEach((el) => {
      observer.observe(el)
    })
  }, 100)
})

onUnmounted(() => {
  if (scrollHandler) {
    window.removeEventListener('scroll', scrollHandler)
  }
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  document.body.style.height = ''
})
</script>

<style scoped>
.integrations-page {
  width: 100vw;
  height: 100vh;
  background: var(--bg-primary, #0a0a0f);
  position: relative;
  overflow: hidden;
}

/* 确保科幻背景可见 - 背景在滚动内容之上但允许点击穿透 */
.integrations-page :deep(.cyber-bg) {
  z-index: 3;
}

/* 阻尼滚动视窗 */
.scroll-viewbox {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  z-index: 2;
  pointer-events: none;
}

.scroll-viewbox > * {
  pointer-events: auto;
}

/* 滚动内容 */
.scroll-content {
  position: relative;
  width: 100%;
  will-change: transform;
  transition: transform 0.15s ease-out;
}

/* 导航栏 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 32px;
  background: var(--nav-bg, rgba(10, 10, 15, 0.8));
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--nav-border, rgba(255, 255, 255, 0.05));
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.nav-links-left {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: var(--text-secondary, #9ca3af);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.3s ease;
  position: relative;
}

.nav-link:hover,
.nav-link.active {
  color: var(--text-primary, #ffffff);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-blue));
  border-radius: 1px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
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

.btn-primary {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

/* 主内容 */
.main-content {
  padding-top: 80px;
}

/* Hero区域 */
.hero-section {
  padding: 80px 40px 60px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto 60px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
  font-size: 14px;
  color: var(--color-primary);
  margin-bottom: 24px;
}

.badge-icon {
  font-size: 16px;
}

.hero-title {
  margin-bottom: 20px;
}

.hero-title :deep(.glitch-text) {
  font-size: 56px;
  font-weight: 800;
}

.hero-desc {
  font-size: 18px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.7;
}

/* 统计卡片 */
.stats-grid {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
}

.stat-card {
  text-align: center;
  padding: 24px 40px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  min-width: 140px;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary, #9ca3af);
}

/* 集成区域 */
.integration-section {
  padding: 100px 40px;
}

:.integration-section.dark {
  background: var(--bg-secondary, rgba(0, 0, 0, 0.2));
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 48px;
}

.header-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.header-icon.vector {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(59, 130, 246, 0.2));
  border: 1px solid rgba(139, 92, 246, 0.3);
}

.header-icon.mcp {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.2), rgba(249, 115, 22, 0.2));
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.header-icon.agent {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.2), rgba(16, 185, 129, 0.2));
  border: 1px solid rgba(34, 197, 94, 0.3);
}

.header-icon svg {
  width: 28px;
  height: 28px;
  color: var(--color-primary);
}

.header-icon.mcp svg {
  color: #f59e0b;
}

.header-icon.agent svg {
  color: #22c55e;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 8px;
}

.section-subtitle {
  font-size: 16px;
  color: var(--text-secondary, #9ca3af);
}

/* 集成卡片网格 */
.integration-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.integration-card {
  position: relative;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  padding: 28px;
  transition: all 0.4s ease;
  overflow: hidden;
}

.integration-card.featured {
  border-color: rgba(139, 92, 246, 0.3);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.05), rgba(59, 130, 246, 0.05));
}

.integration-card:hover {
  transform: translateY(-4px);
  border-color: rgba(139, 92, 246, 0.2);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(
    circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
    rgba(139, 92, 246, 0.15) 0%,
    transparent 50%
  );
  pointer-events: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.db-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.db-logo {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.db-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.db-status.active {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.db-status.coming {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.db-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 8px;
}

.db-desc {
  font-size: 14px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.6;
  margin-bottom: 16px;
}

.db-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.feature-tag {
  padding: 4px 10px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 12px;
  font-size: 12px;
  color: var(--color-primary);
}

.card-footer {
  margin-top: auto;
}

.btn-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--color-primary);
  font-size: 14px;
  font-weight: 500;
  background: none;
  border: none;
  cursor: pointer;
  transition: gap 0.3s ease;
}

.btn-link:hover {
  gap: 10px;
}

.btn-link svg {
  width: 16px;
  height: 16px;
}

/* MCP展示 */
.mcp-showcase {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.mcp-visual {
  position: relative;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mcp-center {
  position: relative;
  z-index: 2;
}

.center-core {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 800;
  color: white;
  box-shadow:
    0 0 60px rgba(245, 158, 11, 0.5),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  animation: pulseCore 2s ease infinite;
}

@keyframes pulseCore {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.core-rings {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 50%;
  animation: ringExpand 3s ease infinite;
}

.ring-1 {
  width: 150px;
  height: 150px;
  animation-delay: 0s;
}

.ring-2 {
  width: 200px;
  height: 200px;
  animation-delay: 1s;
}

.ring-3 {
  width: 250px;
  height: 250px;
  animation-delay: 2s;
}

@keyframes ringExpand {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0;
  }
}

.mcp-nodes {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
}

.mcp-node {
  position: absolute;
  top: 50%;
  left: 50%;
  transform:
    translate(-50%, -50%)
    rotate(var(--angle))
    translateX(140px)
    rotate(calc(var(--angle) * -1));
  text-align: center;
  animation: nodeFloat 3s ease infinite;
  animation-delay: var(--delay);
}

@keyframes nodeFloat {
  0%, 100% { transform: translate(-50%, -50%) rotate(var(--angle)) translateX(140px) rotate(calc(var(--angle) * -1)) translateY(0); }
  50% { transform: translate(-50%, -50%) rotate(var(--angle)) translateX(140px) rotate(calc(var(--angle) * -1)) translateY(-10px); }
}

.node-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 8px;
}

.node-label {
  font-size: 12px;
  color: var(--text-secondary, #9ca3af);
  white-space: nowrap;
}

.mcp-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card {
  padding: 24px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  transition: all 0.3s ease;
}

.info-card:hover {
  border-color: rgba(245, 158, 11, 0.2);
  transform: translateX(8px);
}

.info-icon {
  width: 44px;
  height: 44px;
  background: rgba(245, 158, 11, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.info-card h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #ffffff);
  margin-bottom: 8px;
}

.info-card p {
  font-size: 14px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.6;
}

/* 技能时间线 */
.skills-timeline {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
}

.timeline-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, var(--color-primary), var(--color-blue), #22c55e);
  transform: translateX(-50%);
}

.skill-item {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 40px;
  align-items: center;
  margin-bottom: 60px;
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.6s ease, transform 0.6s ease;
  transition-delay: var(--delay);
}

.skill-item.visible {
  opacity: 1;
  transform: translateY(0);
}

.skill-item.reverse {
  direction: rtl;
}

.skill-item.reverse .skill-content {
  direction: ltr;
}

.skill-content {
  padding: 32px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  transition: all 0.4s ease;
}

.skill-content:hover {
  border-color: rgba(34, 197, 94, 0.2);
  transform: scale(1.02);
}

.skill-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.2), rgba(16, 185, 129, 0.2));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 16px;
}

.skill-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 8px;
}

.skill-desc {
  font-size: 14px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.6;
  margin-bottom: 16px;
}

.skill-capabilities {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.capability-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-secondary, #9ca3af);
}

.cap-check {
  width: 18px;
  height: 18px;
  background: rgba(34, 197, 94, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #22c55e;
  font-weight: 700;
}

.skill-node {
  position: relative;
  z-index: 2;
}

.node-pulse {
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #22c55e, #10b981);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(34, 197, 94, 0.5);
  animation: nodePulse 2s ease infinite;
}

@keyframes nodePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 20px rgba(34, 197, 94, 0.5);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 40px rgba(34, 197, 94, 0.8);
  }
}

/* CTA区域 */
.cta-section {
  padding: 100px 40px;
  background: radial-gradient(ellipse at center, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
}

.cta-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.cta-content {
  text-align: center;
}

.cta-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary, #ffffff);
  margin-bottom: 16px;
}

.cta-desc {
  font-size: 16px;
  color: var(--text-secondary, #9ca3af);
  margin-bottom: 32px;
}

.cta-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-primary-large,
.btn-secondary-large {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary-large {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  border: none;
}

.btn-primary-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(124, 58, 237, 0.4);
}

.btn-secondary-large {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary, #ffffff);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-secondary-large:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.btn-primary-large svg,
.btn-secondary-large svg {
  width: 18px;
  height: 18px;
}

/* 代码预览 */
.code-preview {
  background: var(--code-bg, #0d0d12);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--border-color, rgba(255, 255, 255, 0.05));
}

.code-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--code-header-bg, rgba(255, 255, 255, 0.02));
  border-bottom: 1px solid var(--border-color, rgba(255, 255, 255, 0.05));
}

.code-dots {
  display: flex;
  gap: 6px;
}

.code-dots span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.code-dots span:nth-child(1) { background: #ff5f57; }
.code-dots span:nth-child(2) { background: #febc2e; }
.code-dots span:nth-child(3) { background: #28c840; }

.code-title {
  font-size: 13px;
  color: var(--text-secondary, #9ca3af);
}

.code-content {
  padding: 20px;
  margin: 0;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.8;
  overflow-x: auto;
}

.code-keyword { color: var(--code-keyword, #c792ea); }
.code-string { color: var(--code-string, #c3e88d); }
.code-annotation { color: var(--code-annotation, #7f848e); }
.code-class { color: var(--code-class, #82aaff); }
.code-method { color: var(--code-method, #82aaff); }

/* 响应式 */
@media (max-width: 968px) {
  .mcp-showcase,
  .cta-container {
    grid-template-columns: 1fr;
  }

  .mcp-visual {
    height: 300px;
  }

  .skill-item {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .skill-item.reverse {
    direction: ltr;
  }

  .timeline-line {
    display: none;
  }

  .skill-node {
    display: none;
  }

  .cta-buttons {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .hero-title :deep(.glitch-text) {
    font-size: 36px;
  }

  .nav-links-left {
    display: none;
  }

  .stats-grid {
    gap: 16px;
  }

  .stat-card {
    padding: 16px 24px;
    min-width: auto;
  }
}
</style>
