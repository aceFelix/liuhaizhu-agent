<template>
  <div class="team-page" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <CyberBackground />

    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="handleBackToLanding" />
          <div class="nav-title">
            <span class="title-icon">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
              </svg>
            </span>
            <span>团队介绍</span>
          </div>
        </div>
        <div class="nav-right">
          <ThemeToggle />
          <button @click="handleBackToLanding" class="btn-back">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            <span>返回首页</span>
          </button>
        </div>
      </div>
    </nav>

    <div class="scroll-container">
      <div class="content-wrapper">
        <section class="hero-section">
          <div class="hero-logo">
            <div class="logo-ring"></div>
            <div class="logo-glow"></div>
            <div class="logo-inner">
              <LazyImage
                src="/avatar/4132.jpg"
                alt="4132有活科技"
                width="100%"
                height="100%"
                radius="50%"
                :lazy="false"
              />
            </div>
          </div>
          <h1 class="hero-name">
            <span class="name-main">4132有活科技</span>
            <span class="name-badge">TECH</span>
          </h1>
          <p class="hero-slogan">"你可以不懂技术，但是不能没活！"</p>
          <p class="hero-desc">专注于将底层技术突破转化为垂直场景应用的硬核科技孵化器/研究团队</p>
          <div class="hero-tags">
            <span class="tag" v-for="tag in tags" :key="tag">{{ tag }}</span>
          </div>
        </section>

        <section class="intro-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
              </svg>
            </span>
            <h2>关于我们</h2>
          </div>
          <div class="intro-content">
            <p class="intro-main">4132有活科技，是一个致力于在人工智能、清洁能源、先进制造与生命科学的交叉领域，将前沿技术转化为现实生产力以服务全人类的创新团队。我们不局限于单一赛道，而是以<span class="highlight">"AI决策+硬件载体+能源基础"</span>为核心逻辑，构建从实验室到产业化的完整链路。</p>
          </div>
        </section>

        <section class="detail-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
            </span>
            <h2>详细介绍</h2>
          </div>
          <div class="detail-content">
            <p class="detail-intro">在4132有活科技，我们相信未来的科技不再是孤立的发明，而是系统级的协同进化。</p>
            <div class="detail-grid">
              <div class="detail-card">
                <div class="card-icon">
                  <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
                    <path d="M21 10.12h-6.78l2.74-2.82c-2.73-2.7-7.15-2.8-9.88-.1-2.73 2.71-2.73 7.08 0 9.79s7.15 2.71 9.88 0C18.32 15.65 19 14.08 19 12.1h2c0 1.98-.88 4.55-2.64 6.29-3.51 3.48-9.21 3.48-12.72 0-3.5-3.47-3.53-9.11-.02-12.58s9.14-3.47 12.65 0L21 3v7.12zM12.5 8v4.25l3.5 2.08-.72 1.21L11 13V8h1.5z"/>
                  </svg>
                </div>
                <h3>智能维度</h3>
                <p>研发能全流程自主决策的AI智能体，应用于AI制药、AI法律等垂直领域，通过AI手机、消费级机器人等智能硬件实现技术触达。</p>
              </div>
              <div class="detail-card">
                <div class="card-icon">
                  <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
                    <path d="M7 2v11h3v9l7-12h-4l4-8z"/>
                  </svg>
                </div>
                <h3>能源维度</h3>
                <p>深入底层材料创新，布局钠离子、固态电池及下一代核能技术，旨在解决未来算力与制造业的能源瓶颈。</p>
              </div>
              <div class="detail-card">
                <div class="card-icon">
                  <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
                    <path d="M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z"/>
                  </svg>
                </div>
                <h3>制造维度</h3>
                <p>聚焦人形机器人的工业与生活场景落地，并通过3D打印与AI仿真的结合，重塑制造流程。</p>
              </div>
              <div class="detail-card">
                <div class="card-icon">
                  <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
                  </svg>
                </div>
                <h3>前沿探索</h3>
                <p>涉足商业航天、6G通信与量子计算，为未来万物互联的世界构建技术底座。</p>
              </div>
            </div>
            <p class="detail-footer">凭借在跨学科领域的深耕，团队已荣获AAAI经典论文奖、CES最佳创新奖、GLOMO最佳突破创新奖等数十项国际权威荣誉。我们不仅是技术的追随者，更是未来产业的定义者。</p>
          </div>
        </section>

        <section class="fields-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1 9h-4v4h-2v-4H9V9h4V5h2v4h4v2z"/>
              </svg>
            </span>
            <h2>深耕领域</h2>
          </div>
          <div class="fields-tree">
            <div class="tree-line"></div>
            <div
              v-for="(field, index) in fields"
              :key="index"
              class="field-item"
              :class="{ 'reverse': index % 2 === 1 }"
              :style="{ '--delay': index * 0.15 + 's' }"
            >
              <div class="field-content">
                <div class="field-icon">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                    <path :d="getFieldIcon(index)" />
                  </svg>
                </div>
                <h3 class="field-name">{{ field.name }}</h3>
                <div class="field-items">
                  <div
                    v-for="(item, i) in field.items"
                    :key="i"
                    class="field-sub-item"
                  >
                    <span class="sub-check">✓</span>
                    <span class="sub-title">{{ item.title }}</span>
                    <span class="sub-desc">{{ item.desc }}</span>
                  </div>
                </div>
              </div>
              <div class="field-node">
                <div class="node-pulse"></div>
              </div>
              <div class="field-empty"></div>
            </div>
          </div>
        </section>

        <section class="members-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
              </svg>
            </span>
            <h2>核心成员</h2>
          </div>
          <div class="members-scroll">
            <div class="members-track">
              <div class="member-card" v-for="member in members" :key="member.name">
                <div class="member-avatar">
                  <LazyImage
                    :src="member.avatar"
                    :alt="member.name"
                    width="100%"
                    height="100%"
                    radius="50%"
                  />
                </div>
                <span class="member-name">{{ member.name }}</span>
              </div>
              <div class="member-card" v-for="member in members" :key="'dup-' + member.name">
                <div class="member-avatar">
                  <LazyImage
                    :src="member.avatar"
                    :alt="member.name"
                    width="100%"
                    height="100%"
                    radius="50%"
                  />
                </div>
                <span class="member-name">{{ member.name }}</span>
              </div>
            </div>
          </div>
        </section>

        <section class="awards-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M19 5h-2V3H7v2H5c-1.1 0-2 .9-2 2v1c0 2.55 1.92 4.63 4.39 4.94.63 1.5 1.98 2.63 3.61 2.96V19H7v2h10v-2h-4v-3.1c1.63-.33 2.98-1.46 3.61-2.96C19.08 12.63 21 10.55 21 8V7c0-1.1-.9-2-2-2zM5 8V7h2v3.82C5.84 10.4 5 9.3 5 8zm14 0c0 1.3-.84 2.4-2 2.82V7h2v1z"/>
              </svg>
            </span>
            <h2>荣誉成就</h2>
            <span class="scroll-hint">← 滚动浏览 →</span>
          </div>
          <div class="awards-scroll-container" ref="awardsScrollContainer">
            <div class="awards-track">
              <div class="award-category" v-for="category in awards" :key="category.name">
                <h3 class="category-title">{{ category.name }}</h3>
                <div class="award-items">
                  <div class="award-item" v-for="award in category.items" :key="award">
                    <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                      <path d="M12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z"/>
                    </svg>
                    <span>{{ award }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="vision-section">
          <div class="vision-content">
            <blockquote class="vision-quote">
              "前沿科技的交叉路口，我们既是路标，也是司机"
            </blockquote>
            <p class="vision-label">—— 4132有活科技</p>
          </div>
        </section>

        <section class="notice-section">
          <div class="notice-box">
            <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
            <div class="notice-content">
              <h4>温馨提示</h4>
              <p>以上内容纯属虚构！未涉及企业机密！如有相关侵权行为请联系删除！</p>
            </div>
          </div>
        </section>

        <footer class="page-footer">
          <div class="footer-qrcodes">
            <div class="footer-qrcode-item">
              <LazyImage
                src="/contactInformation/wechat-qrcode.png"
                alt="微信"
                class="footer-qrcode-img"
                width="70px"
                height="70px"
                radius="8px"
              />
              <span class="footer-qrcode-label">WeChat: your_wechat</span>
            </div>
            <div class="footer-qrcode-item">
              <LazyImage
                src="/contactInformation/qq-qrcode.png"
                alt="QQ"
                class="footer-qrcode-img"
                width="70px"
                height="70px"
                radius="8px"
              />
              <span class="footer-qrcode-label">QQ: your_qq</span>
            </div>
          </div>
          <div class="footer-center">
            <button @click="handleBackToLanding" class="btn-home">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
              </svg>
              <span>返回首页</span>
            </button>
            <p class="copyright">© 2026 4132有活科技. All Rights Reserved.</p>
          </div>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'
import { useSettingsStore } from '@/stores/settings'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import LazyImage from '@/components/common/LazyImage.vue'

const router = useRouter()
const transitionStore = useTransitionStore()
const settingsStore = useSettingsStore()

const tags = ref(['多智能体协同', '能源材料突破', '具身智能', '商业化落地'])

const members = ref([
  { name: '艾斯', avatar: '/team/aisi.jpg' },
  { name: '丰的', avatar: '/team/fengde.jpg' },
  { name: '若水', avatar: '/team/ruoshui.jpg' },
  { name: 'C', avatar: '/team/C.jpg' },
  { name: 'Redemption', avatar: '/team/Redemption.jpg' },
  { name: '勋哥', avatar: '/team/huangrenxun.jpg' },
  { name: '马哥', avatar: '/team/elon-musk.jpg' },
  { name: '盖哥', avatar: '/team/bill-gates.jpg' },
  { name: '巴哥', avatar: '/team/warren-buffett.jpg' }
])

const fields = ref([
  {
    name: '人工智能与智能体',
    items: [
      { title: 'AI智能体', desc: '全流程自主决策、多智能体协同' },
      { title: '垂直应用', desc: 'AI制药、AI法律、AI医生、AI电商' },
      { title: '智能硬件', desc: 'AI眼镜、AI手机、AIPC、消费级机器人' }
    ]
  },
  {
    name: '清洁能源与可持续技术',
    items: [
      { title: '下一代电池', desc: '钠离子电池、混合电池、固态电池' },
      { title: '核能', desc: '小型模块化反应堆、核聚变（人造太阳）' },
      { title: '循环回收', desc: '湿法冶金、废料预处理、电池回收' }
    ]
  },
  {
    name: '先进制造与机器人',
    items: [
      { title: '人形机器人', desc: '工业场景（工厂）、生活场景（陪护/家务）' },
      { title: '自动驾驶', desc: 'L3级加速商用、Robotaxi规模化' },
      { title: '3D打印', desc: 'AI仿真预测试错' }
    ]
  },
  {
    name: '生命科学与生物技术',
    items: [
      { title: 'AI制药', desc: '缩短研发周期，多管线推进临床' },
      { title: '生物信息学', desc: '数据驱动的药物研发与精准医疗' },
      { title: '前沿技术', desc: '合成生物学、基因编辑（物种复活）' }
    ]
  },
  {
    name: '航空航天与量子前沿',
    items: [
      { title: '商业航天', desc: '可回收火箭、卫星互联网星座组网' },
      { title: '6G通信', desc: '卫星直连设备、星地融合网络' },
      { title: '量子计算', desc: '超导量子处理器、量子纠错实用化' }
    ]
  },
  {
    name: '半导体与基础设施',
    items: [
      { title: 'AI芯片', desc: '国产化替代、ASIC定制化挑战英伟达' },
      { title: '存储芯片', desc: 'AI驱动的"超级周期"与带宽效率竞赛' },
      { title: '散热技术', desc: '液冷从"可选项"变为"必选项"' }
    ]
  }
])

const awards = ref([
  {
    name: '人工智能',
    items: ['AAAI 经典论文奖', 'NAS工业应用科学奖', 'AAAI 杰出论文奖']
  },
  {
    name: '清洁能源',
    items: ['Adolf Goetzberger Award', '中国可再生能源学会科学技术奖']
  },
  {
    name: '先进制造与机器人',
    items: ['TWICE Picks 奖 (CES 2026)', 'CES 最佳创新奖', 'IEEE 技术领域奖']
  },
  {
    name: '生命科学与生物技术',
    items: ['ASGCT 荣誉奖', 'ASGCT 新研究者奖']
  },
  {
    name: '航空航天',
    items: ['国际宇航联合会杰出服务奖', 'IAF 国际宇航联合会奖']
  },
  {
    name: '半导体与集成电路',
    items: ['GLOMO最佳突破创新奖 (MWC 2026)']
  }
])

const handleBackToLanding = () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

const awardsScrollContainer = ref(null)

const handleAwardsWheel = (e) => {
  if (!awardsScrollContainer.value) return

  const container = awardsScrollContainer.value
  const maxScroll = container.scrollWidth - container.clientWidth
  const currentScroll = container.scrollLeft
  const delta = e.deltaY

  if (delta > 0 && currentScroll < maxScroll) {
    e.preventDefault()
    container.scrollLeft += delta
  } else if (delta < 0 && currentScroll > 0) {
    e.preventDefault()
    container.scrollLeft += delta
  }
}

const fieldIcons = [
  'M21 10.12h-6.78l2.74-2.82c-2.73-2.7-7.15-2.8-9.88-.1-2.73 2.71-2.73 7.08 0 9.79s7.15 2.71 9.88 0C18.32 15.65 19 14.08 19 12.1h2c0 1.98-.88 4.55-2.64 6.29-3.51 3.48-9.21 3.48-12.72 0-3.5-3.47-3.53-9.11-.02-12.58s9.14-3.47 12.65 0L21 3v7.12zM12.5 8v4.25l3.5 2.08-.72 1.21L11 13V8h1.5z',
  'M7 2v11h3v9l7-12h-4l4-8z',
  'M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z',
  'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z',
  'M12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z',
  'M9 2h6c1.1 0 2 .9 2 2v16c0 1.1-.9 2-2 2H9c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2zm3 16c.55 0 1-.45 1-1s-.45-1-1-1-1 .45-1 1 .45 1 1 1z'
]

const getFieldIcon = (index) => {
  return fieldIcons[index % fieldIcons.length]
}

onMounted(() => {
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

  document.querySelectorAll('.field-item').forEach((el) => {
    observer.observe(el)
  })

  if (awardsScrollContainer.value) {
    awardsScrollContainer.value.addEventListener('wheel', handleAwardsWheel, { passive: false })
  }
})
</script>

<style scoped>
.team-page {
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
  position: relative;
  overflow: hidden;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(10, 10, 15, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-color);
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 0 5%;
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
  gap: 20px;
}

.nav-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-secondary);
}

.title-icon {
  color: #8b5cf6;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.scroll-container {
  height: 100vh;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 5% 60px;
}

.hero-section {
  text-align: center;
  padding: 60px 0;
}

.hero-logo {
  position: relative;
  width: 140px;
  height: 140px;
  margin: 0 auto 30px;
}

.logo-ring {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border: 2px solid transparent;
  border-radius: 50%;
  background: linear-gradient(135deg, #8b5cf6, #06b6d4, #10b981, #8b5cf6) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: logo-rotate 8s linear infinite;
}

@keyframes logo-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 160px;
  height: 160px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.3) 0%, transparent 70%);
  animation: logo-pulse 3s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.1); }
}

.logo-inner {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-tertiary) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--border-color);
  overflow: hidden;
}

.logo-img {
  width: 90%;
  height: 90%;
  object-fit: contain;
  border-radius: 50%;
}

.hero-name {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 16px;
}

.name-main {
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, #8b5cf6 0%, #06b6d4 50%, #10b981 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.name-badge {
  padding: 6px 14px;
  background: linear-gradient(135deg, #8b5cf6, #06b6d4);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  letter-spacing: 1px;
}

.hero-slogan {
  font-size: 20px;
  color: #f59e0b;
  font-weight: 500;
  margin-bottom: 12px;
}

.hero-desc {
  font-size: 16px;
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto 24px;
  line-height: 1.6;
}

.hero-tags {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  padding: 8px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 0.3s;
}

.tag:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-icon {
  color: #8b5cf6;
}

.section-header h2 {
  font-size: 24px;
  font-weight: 600;
}

.intro-section {
  margin-bottom: 60px;
}

.intro-content {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 32px;
}

.intro-main {
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-secondary);
}

.highlight {
  color: #8b5cf6;
  font-weight: 500;
}

.detail-section {
  margin-bottom: 60px;
}

.detail-content {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 32px;
}

.detail-intro {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: 24px;
  line-height: 1.6;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.detail-card {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 24px;
  transition: all 0.3s;
}

.detail-card:hover {
  border-color: #8b5cf6;
  transform: translateY(-2px);
}

.card-icon {
  color: #8b5cf6;
  margin-bottom: 12px;
}

.detail-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.detail-card p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.detail-footer {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.7;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.fields-section {
  margin-bottom: 60px;
}

.fields-tree {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
}

.tree-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #8b5cf6, #06b6d4, #10b981, #f59e0b, #ef4444, #ec4899);
  transform: translateX(-50%);
}

.field-item {
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

.field-item.visible {
  opacity: 1;
  transform: translateY(0);
}

.field-item.reverse {
  direction: rtl;
}

.field-item.reverse .field-content {
  direction: ltr;
}

.field-content {
  padding: 32px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  transition: all 0.4s ease;
}

.field-content:hover {
  border-color: #8b5cf6;
  transform: scale(1.02);
  box-shadow: 0 8px 32px rgba(139, 92, 246, 0.15);
}

.field-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(6, 182, 212, 0.2));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.field-icon svg {
  color: #8b5cf6;
}

.field-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.field-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field-sub-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 14px;
}

.sub-check {
  width: 18px;
  height: 18px;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #8b5cf6;
  font-weight: 700;
  flex-shrink: 0;
  margin-top: 2px;
}

.sub-title {
  color: #8b5cf6;
  font-weight: 600;
  white-space: nowrap;
}

.sub-desc {
  color: var(--text-secondary);
  line-height: 1.5;
}

.field-node {
  position: relative;
  z-index: 2;
}

.node-pulse {
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #8b5cf6, #06b6d4);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.5);
  animation: nodePulse 2s ease infinite;
}

@keyframes nodePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 20px rgba(139, 92, 246, 0.5);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 40px rgba(139, 92, 246, 0.8);
  }
}

.field-empty {
  visibility: hidden;
}

.members-section {
  margin-bottom: 60px;
}

.members-scroll {
  overflow: hidden;
  mask-image: linear-gradient(90deg, transparent, black 5%, black 95%, transparent);
}

.members-track {
  display: flex;
  gap: 16px;
  animation: scroll-members 30s linear infinite;
  width: max-content;
}

.members-track:hover {
  animation-play-state: paused;
}

@keyframes scroll-members {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

.member-card {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  min-width: 120px;
  transition: all 0.3s;
}

.member-card:hover {
  border-color: #8b5cf6;
  transform: translateY(-4px);
}

.member-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(139, 92, 246, 0.3);
  transition: all 0.3s ease;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-card:hover .member-avatar {
  border-color: #8b5cf6;
  box-shadow: 0 0 12px rgba(139, 92, 246, 0.4);
}

.member-name {
  font-size: 13px;
  color: var(--text-secondary);
  text-align: center;
  white-space: nowrap;
}

.awards-section {
  margin-bottom: 60px;
  position: relative;
}

.awards-section .section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.scroll-hint {
  font-size: 12px;
  color: var(--text-muted);
  opacity: 0.6;
  margin-left: auto;
  animation: pulse-hint 2s ease-in-out infinite;
}

@keyframes pulse-hint {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}

.awards-scroll-container {
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding-bottom: 10px;
  cursor: grab;
}

.awards-scroll-container::-webkit-scrollbar {
  display: none;
}

.awards-track {
  display: flex;
  gap: 20px;
  padding-right: 5%;
}

.award-category {
  flex-shrink: 0;
  width: 280px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.award-category:hover {
  border-color: rgba(139, 92, 246, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
}

.category-title {
  font-size: 14px;
  font-weight: 600;
  color: #8b5cf6;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.award-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.award-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}

.award-item svg {
  flex-shrink: 0;
  color: #f59e0b;
  margin-top: 2px;
}

.vision-section {
  margin-bottom: 80px;
}

.vision-content {
  text-align: center;
  padding: 60px 40px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 24px;
  position: relative;
  overflow: hidden;
}

.vision-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 0%, rgba(139, 92, 246, 0.15) 0%, transparent 60%);
  pointer-events: none;
}

.vision-icon {
  color: #8b5cf6;
  margin-bottom: 24px;
  opacity: 0.6;
  position: relative;
  z-index: 1;
}

.vision-quote {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  font-style: italic;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.vision-label {
  font-size: 16px;
  color: #8b5cf6;
  position: relative;
  z-index: 1;
}

.notice-section {
  margin-bottom: 40px;
}

.notice-box {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 12px;
  padding: 20px 24px;
}

.notice-box svg {
  flex-shrink: 0;
  color: #f59e0b;
}

.notice-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: #f59e0b;
  margin-bottom: 4px;
}

.notice-content p {
  font-size: 13px;
  color: var(--text-secondary);
}

.page-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 40px;
  border-top: 1px solid rgba(139, 92, 246, 0.2);
}

.footer-qrcodes {
  display: flex;
  gap: 24px;
}

.footer-qrcode-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.footer-qrcode-img {
  width: 70px !important;
  height: 70px !important;
  min-width: 70px;
  min-height: 70px;
  max-width: 70px;
  max-height: 70px;
  border-radius: 8px;
}

.footer-qrcode-img :deep(.lazy-image) {
  object-fit: contain;
}

.footer-qrcode-label {
  font-size: 12px;
  color: #9ca3af;
}

.footer-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.btn-home {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 24px;
}

.btn-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.4);
}

.copyright {
  font-size: 13px;
  color: #6b7280;
}

.light-theme .nav-bar {
  background: rgba(255, 255, 255, 0.9);
}

.light-theme .logo-inner {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.light-theme .name-main {
  background: linear-gradient(135deg, #7c3aed 0%, #0891b2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.light-theme .intro-content,
.light-theme .detail-content,
.light-theme .field-card,
.light-theme .member-card,
.light-theme .award-category {
  background: #f8fafc;
  border-color: #e2e8f0;
}

.light-theme .award-category:hover {
  border-color: rgba(139, 92, 246, 0.4);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.1);
}

.light-theme .scroll-hint {
  color: #64748b;
}

.light-theme .detail-card {
  background: #ffffff;
  border-color: #e2e8f0;
}

.light-theme .detail-card:hover,
.light-theme .field-card:hover,
.light-theme .member-card:hover {
  border-color: #8b5cf6;
}

.light-theme .member-avatar {
  border-color: rgba(139, 92, 246, 0.2);
}

.light-theme .member-card:hover .member-avatar {
  border-color: #8b5cf6;
}

.light-theme .vision-content {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.08) 0%, rgba(139, 92, 246, 0.03) 100%);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .vision-quote {
  color: #0f172a;
}

.light-theme .vision-label {
  color: #64748b;
}

.light-theme .vision-icon {
  color: #8b5cf6;
}

.light-theme .field-content {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .field-content:hover {
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
}

.light-theme .field-name {
  color: #0f172a;
}

.light-theme .sub-title {
  color: #7c3aed;
}

.light-theme .sub-desc {
  color: #64748b;
}

.light-theme .sub-check {
  background: rgba(139, 92, 246, 0.15);
  color: #7c3aed;
}

.light-theme .field-icon {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15), rgba(6, 182, 212, 0.15));
}

.light-theme .field-icon svg {
  color: #7c3aed;
}

.light-theme .page-footer {
  border-top-color: rgba(139, 92, 246, 0.15);
}

.light-theme .btn-home {
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
}

.light-theme .btn-home:hover {
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.3);
}

.light-theme .copyright {
  color: #94a3b8;
}

.light-theme .footer-qrcode-label {
  color: #64748b;
}

@media (max-width: 1024px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .fields-tree {
    max-width: 100%;
  }

  .field-item {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .field-item.reverse {
    direction: ltr;
  }

  .tree-line {
    left: 20px;
  }

  .field-node {
    position: absolute;
    left: 20px;
    transform: translateX(-50%);
  }

  .field-content {
    margin-left: 50px;
  }

  .field-empty {
    display: none;
  }

  .awards-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .name-main {
    font-size: 32px;
  }

  .awards-grid {
    grid-template-columns: 1fr;
  }

  .hero-tags {
    gap: 8px;
  }

  .tag {
    font-size: 12px;
    padding: 6px 12px;
  }

  .vision-content {
    padding: 40px 24px;
  }

  .vision-quote {
    font-size: 20px;
  }

  .vision-label {
    font-size: 14px;
  }
}
</style>
