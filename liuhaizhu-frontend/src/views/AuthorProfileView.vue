<template>
  <div class="author-page" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <CyberBackground />

    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="handleBackToLanding" />
          <div class="nav-title">
            <span class="title-icon">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </span>
            <span>作者简介</span>
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
          <div class="hero-avatar">
            <div class="avatar-ring"></div>
            <div class="avatar-glow"></div>
            <div class="avatar-inner">
              <LazyImage
                src="/avatar/author.jpg"
                alt="艾斯Felix"
                width="100%"
                height="100%"
                radius="50%"
                :lazy="false"
              />
            </div>
          </div>
          <h1 class="hero-name">
            <span class="name-main">艾斯Felix</span>
            <span class="name-badge">ACE</span>
          </h1>
          <p class="hero-title-text">全栈开发者 · 数字游民 · 餐饮创业者</p>
          <p class="hero-quote">"我不是开发者，我只是大数据的搬运工"</p>
        </section>

        <section class="info-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </span>
            <h2>基本信息</h2>
          </div>
          <div class="info-grid">
            <div class="info-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">姓名</span>
                <span class="info-value">艾斯Felix</span>
              </div>
            </div>
            <div class="info-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">性别 / 年龄</span>
                <span class="info-value">男 / 24岁</span>
              </div>
            </div>
            <div class="info-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3zm6.82 6L12 12.72 5.18 9 12 5.28 18.82 9zM17 15.99l-5 2.73-5-2.73v-3.72L12 15l5-2.73v3.72z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">母校</span>
                <span class="info-value">沈阳化工大学</span>
                <span class="info-sub">计算机科学与技术学院 E54132宿舍3床床长</span>
              </div>
            </div>
            <div class="info-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">现居地</span>
                <span class="info-value">贵阳市观山湖区</span>
                <span class="info-sub">4132有活科技CEO/食里香老素粉总店主理人</span>
              </div>
            </div>
            <div class="info-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">职业身份</span>
                <span class="info-value">数字游民</span>
                <span class="info-sub">JavaWeb开发 · 大模型应用开发 · 餐饮服务</span>
              </div>
            </div>
            <div class="info-card contact-card">
              <div class="info-icon">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                </svg>
              </div>
              <div class="info-content">
                <span class="info-label">联系方式</span>
                <div class="contact-list">
                  <div class="contact-item">
                    <span class="contact-type">邮箱</span>
                    <a href="mailto:contact@example.com" class="contact-value">contact@example.com</a>
                  </div>
                  <div class="contact-item">
                    <span class="contact-type">备用邮箱</span>
                    <a href="mailto:support@example.com" class="contact-value">support@example.com</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="skills-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z"/>
              </svg>
            </span>
            <h2>专业技能</h2>
          </div>

          <div class="skills-container">
            <div class="skill-category" v-for="category in skillCategories" :key="category.title">
              <div class="category-header" @click="category.expanded = !category.expanded">
                <div class="category-left">
                  <span class="category-icon" v-html="category.icon"></span>
                  <span class="category-title">{{ category.title }}</span>
                </div>
                <span class="expand-icon" :class="{ expanded: category.expanded }">
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/>
                  </svg>
                </span>
              </div>
              <transition name="expand">
                <div class="category-content" v-show="category.expanded">
                  <div class="skill-item" v-for="skill in category.skills" :key="skill.name">
                    <div class="skill-header">
                      <span class="skill-name">{{ skill.name }}</span>
                      <span class="skill-level">{{ skill.level }}</span>
                    </div>
                    <div class="skill-bar">
                      <div class="skill-progress" :style="{ width: skill.progress + '%' }"></div>
                    </div>
                    <p class="skill-desc">{{ skill.desc }}</p>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </section>

        <section class="projects-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1 9h-4v4h-2v-4H9V9h4V5h2v4h4v2z"/>
              </svg>
            </span>
            <h2>项目经历</h2>
          </div>

          <div class="projects-timeline">
            <div class="timeline-line"></div>
            <div class="project-card" v-for="(project, index) in projects" :key="project.name" :style="{ '--delay': index * 0.1 + 's' }">
              <div class="project-dot"></div>
              <div class="project-content">
                <div class="project-header">
                  <span class="project-tag">{{ project.tag }}</span>
                  <h3 class="project-name">{{ project.name }}</h3>
                </div>
                <p class="project-desc">{{ project.desc }}</p>
                <div class="project-tech">
                  <span class="tech-badge" v-for="tech in project.techs" :key="tech">{{ tech }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="design-section">
          <div class="section-header">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 22C6.49 22 2 17.51 2 12S6.49 2 12 2s10 4.04 10 9c0 3.31-2.69 6-6 6h-1.77c-.28 0-.5.22-.5.5 0 .12.05.23.13.33.41.47.64 1.06.64 1.67A2.5 2.5 0 0 1 12 22zm0-18c-4.41 0-8 3.59-8 8s3.59 8 8 8c.28 0 .5-.22.5-.5a.54.54 0 0 0-.14-.35c-.41-.46-.63-1.05-.63-1.65a2.5 2.5 0 0 1 2.5-2.5H16c2.21 0 4-1.79 4-4 0-3.86-3.59-7-8-7z"/>
                <circle cx="6.5" cy="11.5" r="1.5"/>
                <circle cx="9.5" cy="7.5" r="1.5"/>
                <circle cx="14.5" cy="7.5" r="1.5"/>
                <circle cx="17.5" cy="11.5" r="1.5"/>
              </svg>
            </span>
            <h2>平面设计作品</h2>
          </div>

          <div
            class="design-scroll-container"
            @mouseenter="isScrollPaused = true"
            @mouseleave="isScrollPaused = false"
          >
            <div class="design-scroll-track" :class="{ 'paused': isScrollPaused }">
              <div
                class="design-card"
                v-for="(design, index) in [...designs, ...designs]"
                :key="index"
                @click="openDesignPreview(design)"
              >
                <div class="design-preview">
                  <LazyImage
                    :src="`/design/${design.file}`"
                    :alt="design.name"
                    width="100%"
                    height="100%"
                    radius="8px"
                    object-fit="cover"
                  />
                  <div class="design-overlay">
                    <span class="view-icon">
                      <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                        <circle cx="12" cy="12" r="3"/>
                      </svg>
                    </span>
                  </div>
                </div>
                <div class="design-info">
                  <h4 class="design-name">{{ design.name }}</h4>
                  <span class="design-type">{{ design.type }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="scroll-hint">
            <span>← 鼠标悬停暂停 · 点击查看大图 →</span>
          </div>
        </section>

        <section class="manifesto-section">
          <div class="manifesto-content">
            <div class="manifesto-icon">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="currentColor">
                <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 12h-2v-2h2v2zm0-4h-2V6h2v4z"/>
              </svg>
            </div>
            <blockquote class="manifesto-quote">
              "我不是开发者，我只是大数据的搬运工"
            </blockquote>
            <p class="manifesto-author">—— 艾斯Felix</p>
          </div>
        </section>

        <Teleport to="body">
          <Transition name="preview-fade">
            <div v-if="selectedDesign" class="design-preview-modal" @click.self="closeDesignPreview">
              <div class="preview-backdrop" @click="closeDesignPreview"></div>
              <div class="preview-content">
                <button class="preview-close" @click="closeDesignPreview">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 6L6 18M6 6l12 12"/>
                  </svg>
                </button>
                <LazyImage
                  :src="`/design/${selectedDesign.file}`"
                  :alt="selectedDesign.name"
                  width="auto"
                  height="auto"
                  radius="8px"
                  object-fit="contain"
                  :lazy="false"
                />
                <div class="preview-info">
                  <h3 class="preview-name">{{ selectedDesign.name }}</h3>
                  <p class="preview-type">{{ selectedDesign.type }}</p>
                </div>
              </div>
            </div>
          </Transition>
        </Teleport>

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
            <p class="copyright">© 2026 艾斯Felix. All Rights Reserved.</p>
          </div>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTransitionStore } from '@/stores/transition'
import { useSettingsStore } from '@/stores/settings'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import LazyImage from '@/components/common/LazyImage.vue'

const router = useRouter()
const transitionStore = useTransitionStore()
const settingsStore = useSettingsStore()

const handleBackToLanding = () => {
  transitionStore.startReverseDraw()
  setTimeout(() => {
    router.push('/')
  }, 600)
}

const skillCategories = reactive([
  {
    title: '后端开发',
    expanded: true,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M20 3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-9 14H5v-2h6v2zm8-4H5v-2h14v2zm0-4H5V7h14v2z"/></svg>',
    skills: [
      { name: 'Java', level: '精通', progress: 90, desc: '熟悉JavaSE基础及Java高级特性多线程、反射、IO流等' },
      { name: 'Python', level: '熟悉', progress: 75, desc: '熟悉Python3基础及Python高级函数进阶、装饰器、网编和多线程等' },
      { name: 'FastAPI', level: '熟悉', progress: 90, desc: '熟悉FastAPI框架构建高性能、搞开发效率的API服务，如构建微服务架构及高效封装大模型等' },
      { name: 'Spring框架', level: '精通', progress: 90, desc: 'IOC和AOP编程思想深入于心，精通SpringBoot+MyBatis/MyBatisPlus各种CRUD哈哈哈' },
      { name: 'SpringSecurity', level: '熟练', progress: 85, desc: '熟悉SpringSecurity+JWT的登录认证及权限管理流程及实现' },
      { name: 'SpringCloud', level: '熟悉', progress: 75, desc: '熟悉SpringCloud及OpenFeign、GateWay、Nacos等相关组件的使用' },
      { name: 'MySQL', level: '熟练', progress: 85, desc: '熟悉MySQL存储有一定的SQL调优能力' },
      { name: 'ElasticSearch', level: '熟练', progress: 80, desc: '熟悉ElasticSearch全文检索实现' },
      { name: 'Redis', level: '熟练', progress: 85, desc: '熟悉Redis缓存技术、Redis分布式锁、RedisStack向量存储等' },
      { name: 'RabbitMQ', level: '熟练', progress: 80, desc: '熟悉RabbitMQ的异步通信及流量削峰场景的设计与实现' }
    ]
  },
  {
    title: '前端开发',
    expanded: false,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/></svg>',
    skills: [
      { name: 'HTML/CSS/JavaScript/TypeScript', level: '熟练', progress: 85, desc: '熟悉HTML、CSS、JavaScript及TypeScript等脚本语言' },
      { name: 'SVG动画', level: '熟练', progress: 80, desc: '熟悉SVG+JavaScript实现图形元素炫酷的动态生成及更新' },
      { name: 'Vue3', level: '熟练', progress: 85, desc: '熟悉Vue3快速搭建前端项目、Vite构建及Pinia状态管理' },
      { name: 'ElementUI', level: '熟练', progress: 80, desc: '熟悉ElementUI的使用' },
      { name: 'GSAP动画', level: '熟练', progress: 75, desc: '熟悉使用动画组件库GSAP提供极强的丰富的动态交互' }
    ]
  },
  {
    title: '集成部署',
    expanded: false,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM19 18H6c-2.21 0-4-1.79-4-4 0-2.05 1.53-3.76 3.56-3.97l1.07-.11.5-.95C8.08 7.14 9.94 6 12 6c2.62 0 4.88 1.86 5.39 4.43l.3 1.5 1.53.11c1.56.1 2.78 1.41 2.78 2.96 0 1.65-1.35 3-3 3z"/></svg>',
    skills: [
      { name: 'Docker', level: '熟练', progress: 85, desc: '熟悉Docker镜像拉取、制作、发布、部署，各种Docker命令已成肌肉记忆' },
      { name: 'DockerCompose', level: '熟练', progress: 80, desc: '熟悉DockerCompose单容器编排一键启动多个服务' },
      { name: 'Jenkins', level: '熟练', progress: 80, desc: '熟练使用Jenkins进行项目的持续集成和持续部署' },
      { name: 'Nginx', level: '熟练', progress: 80, desc: '熟悉Nginx的web项目部署、反向代理及负载均衡实现' },
      { name: 'Git', level: '熟练', progress: 85, desc: '熟悉git+gogs/github部署管理私有仓库' }
    ]
  },
  {
    title: '平面设计',
    expanded: false,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M12 22C6.49 22 2 17.51 2 12S6.49 2 12 2s10 4.04 10 9c0 3.31-2.69 6-6 6h-1.77c-.28 0-.5.22-.5.5 0 .12.05.23.13.33.41.47.64 1.06.64 1.67A2.5 2.5 0 0 1 12 22zm0-18c-4.41 0-8 3.59-8 8s3.59 8 8 8c.28 0 .5-.22.5-.5a.54.54 0 0 0-.14-.35c-.41-.46-.63-1.05-.63-1.65a2.5 2.5 0 0 1 2.5-2.5H16c2.21 0 4-1.79 4-4 0-3.86-3.59-7-8-7z"/></svg>',
    skills: [
      { name: 'Photoshop', level: '熟练', progress: 80, desc: '熟悉PS平面设计工具的使用' },
      { name: 'InDesign', level: '熟练', progress: 75, desc: '熟悉ID排版设计工具的使用' },
      { name: 'Illustrator', level: '熟练', progress: 75, desc: '熟悉AI矢量设计工具的使用' }
    ]
  },
  {
    title: '大模型应用开发',
    expanded: false,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M21 10.12h-6.78l2.74-2.82c-2.73-2.7-7.15-2.8-9.88-.1-2.73 2.71-2.73 7.08 0 9.79s7.15 2.71 9.88 0C18.32 15.65 19 14.08 19 12.1h2c0 1.98-.88 4.55-2.64 6.29-3.51 3.48-9.21 3.48-12.72 0-3.5-3.47-3.53-9.11-.02-12.58s9.14-3.47 12.65 0L21 3v7.12zM12.5 8v4.25l3.5 2.08-.72 1.21L11 13V8h1.5z"/></svg>',
    skills: [
      { name: 'Ollama', level: '熟练', progress: 80, desc: '熟悉使用Ollama本地拉取、部署大模型' },
      { name: 'AICoding', level: '熟练', progress: 85, desc: '熟练使用Trae、Qoder、Cursor等AI编辑器，熟悉AgentSkills开发及使用，AICoding已成日常开发必需' },
      { name: 'SpringAI/Langchain4j', level: '熟练', progress: 85, desc: '熟悉JavaAI应用框架集成DeepSeek、QWen、GML等大语言模型实现LLM聊天、记忆、搜索、结构化输出、工具调用等能力，Prompt优化得心应手、SSE流式推送手到擒来' },
      { name: 'RAG知识库', level: '熟练', progress: 80, desc: '熟悉RAG流程及多种分片策略实现知识库搭建' },
      { name: 'MCP服务', level: '熟练', progress: 75, desc: '熟悉MCP服务集成及远程工具调用实现' },
      { name: '向量数据库', level: '熟练', progress: 80, desc: '熟悉RedisStack、Qdrant等向量数据库的集成部署以实现大模型RAG功能' }
    ]
  },
  {
    title: '算法基础',
    expanded: false,
    icon: '<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor"><path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>',
    skills: [
      { name: '数据结构', level: '熟悉', progress: 75, desc: '熟悉数组、链表、栈、队列、哈希表、二叉树等数据结构及代码实现' },
      { name: '经典算法', level: '熟悉', progress: 75, desc: '熟悉排序（冒泡、快排、归并）、二分查找、双指针、滑动窗口等算法原理及代码实现' },
      { name: '算法思想', level: '熟悉', progress: 70, desc: '熟悉动态规划、回溯/剪枝、贪心算法等算法思想规则及原理' }
    ]
  }
])

const projects = ref([
  {
    tag: '信息服务',
    name: '《BitInn比特客栈》智能技术社区博客平台',
    desc: '面向技术社区的智能技术博客平台，集成AI智能助手功能，支持技术文章的创作、发布、搜索、评论及文章分析等功能',
    techs: ['SpringBoot','SpringSecurity', 'SpringAIAlibaba',  'MongoDB', 'ElasticSearch', 'MyBatis', 'Redis', 'RAG', 'MCP', 'Websocket','SSE','OSS', 'Vue3']
  },
  {
    tag: '购物商城',
    name: '《ShiliFresh食里鲜》智慧社区团购系统',
    desc: '面向社区的生鲜团购平台，支持商品展示、购物车、订单管理、配送跟踪及AI智能客服等功能',
    techs: ['SpringBoot', 'Langchain4j', 'SpringCloud', 'Nacos', 'OpenFeign', 'GateWay', 'ElasticSearch', 'RabbitMQ', 'MyBatisPlus','SSE', 'Redis', 'OSS', 'Vue3']
  },
  {
    tag: '管理SaaS',
    name: '《FoodCircle食备圈》智慧员工兼仓库管理系统',
    desc: '餐饮企业内部管理系统，AI智能管理，包含员工考勤、权限管理、库存管理、数据统计等模块',
    techs: ['SpringBoot','SpringAIAlibaba', 'SpringSecurity', 'Redis', 'MyBatis', 'MinIO', 'RAG', 'MCP', 'SSE','Vue3']
  },
  {
    tag: '餐饮服务',
    name: '《Tastylane食里香》智能外卖兼点餐系统',
    desc: '餐饮行业解决方案，支持在线点餐、堂食、外卖配送、订单管理、营业统计及AI智能客服等功能',
    techs: ['SpringBoot', 'SpringAI', 'SpringSecurity', 'Redis', 'MyBatis', 'MinIO', 'RabbitMQ', 'RAG', 'MCP', 'SSE','Vue3']
  },
  {
    tag: '信息娱乐',
    name: '《LHZ个人智能助手》多功能AI聊天助手平台',
    desc: '集成联网搜索、知识库检索与智能对话的全能AI平台，支持流式响应、文档识别等功能',
    techs: ['SpringBoot','SpringAI', 'SpringSecurity', 'MyBatisPlus', 'RedisStack', 'RAG', 'MCP', 'SSE', 'OSS', 'Vue3']
  }
])

const designs = ref([
  { name: 'SUMMER', type: '清凉夏日宣传插画', file: 'SUMMER.jpg' },
  { name: 'XXXTENTACION', type: '说唱人物海报', file: 'XXXTENTACION.jpg' },
  { name: 'XXXTENTACION2', type: '说唱人物海报', file: 'XXXTENTACION2.jpg' },
  { name: '马思唯', type: '说唱人物海报', file: 'masiwei.jpg' },
  { name: 'ChineseHongKer', type: '创意电脑桌面壁纸', file: 'ChineseHongKer.jpg' },
  { name: 'FollowMe', type: '创意电脑桌面壁纸', file: 'FollowMe.jpg' },
  { name: 'FollowMe2', type: '创意手机桌面壁纸', file: 'FollowMe2.jpg' },
  { name: '书法', type: '传统文化宣传海报', file: 'shufa.jpg' },
  { name: '书法二', type: '传统文化宣传海报', file: 'shufa2.jpg' },
  { name: '汉语', type: '传统文化宣传海报', file: 'hanyu.jpg' },
  { name: 'CUE', type: '创意设计电脑桌面壁纸', file: 'CUE.jpg' },
  { name: 'NVIDIA', type: '英伟达创意图标电脑桌面壁纸', file: 'NVIDA.jpg' }
])

const isScrollPaused = ref(false)
const selectedDesign = ref(null)

const openDesignPreview = (design) => {
  selectedDesign.value = design
}

const closeDesignPreview = () => {
  selectedDesign.value = null
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

  document.querySelectorAll('.project-card').forEach((el) => {
    observer.observe(el)
  })
})
</script>

<style scoped>
.author-page {
  min-height: 100vh;
  background: #0a0a0f;
  color: #e0e0e0;
  overflow-x: hidden;
  position: relative;
}

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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-primary);
  font-size: 16px;
  font-weight: 600;
}

.title-icon {
  display: flex;
  align-items: center;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
  color: #fff;
}

.scroll-container {
  position: relative;
  z-index: 10;
  padding-top: 80px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

.hero-section {
  text-align: center;
  padding: 60px 0 80px;
  position: relative;
}

.hero-avatar {
  position: relative;
  width: 160px;
  height: 160px;
  margin: 0 auto 32px;
}

.avatar-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border: 3px solid transparent;
  border-radius: 50%;
  background: conic-gradient(from 0deg, var(--color-primary), var(--color-primary-dark), #ec4899, var(--color-primary)) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: rotate 3s linear infinite;
  filter: blur(0.5px);
}

.avatar-ring::before {
  content: '';
  position: absolute;
  top: -12px;
  left: -12px;
  right: -12px;
  bottom: -12px;
  border: 2px solid transparent;
  border-radius: 50%;
  background: conic-gradient(from 180deg, transparent, var(--color-primary), transparent, #ec4899, transparent) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: rotate 5s linear infinite reverse;
  opacity: 0.6;
}

.avatar-ring::after {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  right: 4px;
  bottom: 4px;
  border: 2px solid transparent;
  border-radius: 50%;
  background: conic-gradient(from 90deg, #06b6d4, var(--color-primary), #06b6d4, transparent) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: rotate 2s linear infinite;
  opacity: 0.5;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.avatar-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.4) 0%, rgba(236, 72, 153, 0.2) 40%, transparent 70%);
  filter: blur(25px);
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 1; transform: translate(-50%, -50%) scale(1.15); }
}

.avatar-inner {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 40px rgba(139, 92, 246, 0.4);
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  font-size: 48px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.name-badge {
  padding: 6px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.hero-title-text {
  font-size: 20px;
  color: #9ca3af;
  margin-bottom: 24px;
}

.hero-quote {
  font-size: 18px;
  color: var(--color-primary);
  font-style: italic;
  opacity: 0.8;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
}

.section-icon {
  width: 40px;
  height: 40px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.info-section {
  margin-bottom: 80px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.info-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  transition: all 0.3s;
}

.info-card:hover {
  border-color: rgba(139, 92, 246, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(139, 92, 246, 0.15);
}

.info-card.highlight {
  grid-column: span 2;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%);
  border-color: rgba(139, 92, 246, 0.3);
}

.info-icon {
  width: 48px;
  height: 48px;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  flex-shrink: 0;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.info-value {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.info-value.link {
  color: #a78bfa;
  text-decoration: none;
  transition: color 0.3s;
}

.info-value.link:hover {
  color: #c4b5fd;
  text-decoration: underline;
}

.contact-card .contact-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-type {
  font-size: 13px;
  color: #9ca3af;
  min-width: 60px;
}

.contact-value {
  font-size: 15px;
  font-weight: 500;
  color: #a78bfa;
  text-decoration: none;
  transition: color 0.3s;
}

.contact-value:hover {
  color: #c4b5fd;
  text-decoration: underline;
}

.info-sub {
  font-size: 14px;
  color: #9ca3af;
}

.skills-section {
  margin-bottom: 80px;
}

.skills-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skill-category {
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
}

.skill-category:hover {
  border-color: rgba(139, 92, 246, 0.4);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  cursor: pointer;
  transition: background 0.3s;
}

.category-header:hover {
  background: rgba(139, 92, 246, 0.05);
}

.category-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  width: 40px;
  height: 40px;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
}

.category-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.expand-icon {
  color: #6b7280;
  transition: transform 0.3s;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.category-content {
  padding: 0 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skill-item {
  padding: 16px;
  background: rgba(10, 10, 15, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(139, 92, 246, 0.1);
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.skill-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.skill-level {
  font-size: 12px;
  padding: 4px 10px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border-radius: 12px;
}

.skill-bar {
  height: 6px;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.skill-progress {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 3px;
  transition: width 1s ease-out;
}

.skill-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 2000px;
}

.projects-section {
  margin-bottom: 80px;
}

.projects-timeline {
  position: relative;
  padding-left: 40px;
}

.timeline-line {
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, var(--color-primary) 0%, transparent 100%);
}

.project-card {
  position: relative;
  margin-bottom: 32px;
  opacity: 0;
  transform: translateX(-20px);
  transition: all 0.5s ease;
  transition-delay: var(--delay);
}

.project-card.visible {
  opacity: 1;
  transform: translateX(0);
}

.project-dot {
  position: absolute;
  left: -36px;
  top: 24px;
  width: 16px;
  height: 16px;
  background: var(--color-primary);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.5);
}

.project-content {
  padding: 24px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  transition: all 0.3s;
}

.project-content:hover {
  border-color: rgba(139, 92, 246, 0.5);
  transform: translateX(8px);
  box-shadow: 0 12px 30px rgba(139, 92, 246, 0.15);
}

.project-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.project-tag {
  padding: 4px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  color: var(--color-primary);
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.project-name {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.project-desc {
  font-size: 14px;
  color: #9ca3af;
  line-height: 1.6;
  margin-bottom: 16px;
}

.project-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-badge {
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.design-section {
  margin-bottom: 80px;
}

.design-scroll-container {
  overflow: hidden;
  padding: 20px 0;
  mask-image: linear-gradient(90deg, transparent, #000 5%, #000 95%, transparent);
  -webkit-mask-image: linear-gradient(90deg, transparent, #000 5%, #000 95%, transparent);
}

.design-scroll-track {
  display: flex;
  gap: 24px;
  animation: scroll-left 25s linear infinite;
  width: max-content;
}

.design-scroll-track.paused {
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

.design-card {
  flex-shrink: 0;
  width: 220px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.design-card:hover {
  transform: translateY(-8px);
  border-color: rgba(139, 92, 246, 0.5);
  box-shadow: 0 16px 40px rgba(139, 92, 246, 0.2);
}

.design-preview {
  height: 160px;
  position: relative;
  overflow: hidden;
}

.design-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.design-card:hover .design-img {
  transform: scale(1.1);
}

.design-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.design-card:hover .design-overlay {
  opacity: 1;
}

.view-icon {
  color: #fff;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

.design-info {
  padding: 16px;
  text-align: center;
}

.design-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.design-type {
  font-size: 12px;
  color: #6b7280;
}

.scroll-hint {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: #6b7280;
  opacity: 0.7;
}

.design-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.preview-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  backdrop-filter: blur(10px);
}

.preview-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.preview-close {
  position: absolute;
  top: -48px;
  right: 0;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.preview-close:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.preview-image {
  max-width: 100%;
  max-height: 75vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.preview-info {
  text-align: center;
  margin-top: 20px;
}

.preview-name {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.preview-type {
  font-size: 14px;
  color: #9ca3af;
}

.preview-fade-enter-active,
.preview-fade-leave-active {
  transition: all 0.3s ease;
}

.preview-fade-enter-from,
.preview-fade-leave-to {
  opacity: 0;
}

.preview-fade-enter-from .preview-content,
.preview-fade-leave-to .preview-content {
  transform: scale(0.9);
}

.manifesto-section {
  margin-bottom: 80px;
}

.manifesto-content {
  text-align: center;
  padding: 60px 40px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 24px;
  position: relative;
  overflow: hidden;
}

.manifesto-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 0%, rgba(139, 92, 246, 0.15) 0%, transparent 60%);
  pointer-events: none;
}

.manifesto-icon {
  color: var(--color-primary);
  margin-bottom: 24px;
  opacity: 0.6;
}

.manifesto-quote {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  font-style: italic;
  margin-bottom: 16px;
  position: relative;
}

.manifesto-author {
  font-size: 16px;
  color: var(--color-primary);
}

.contact-section {
  margin-bottom: 60px;
}

.contact-qrcode-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
  max-width: 600px;
  margin: 0 auto;
}

.qrcode-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 32px;
  background: rgba(20, 20, 30, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
  transition: all 0.3s;
}

.qrcode-card:hover {
  border-color: rgba(139, 92, 246, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(139, 92, 246, 0.15);
}

.qrcode-image-wrapper {
  width: 180px;
  height: 180px;
  padding: 12px;
  background: #fff;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.qrcode-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.qrcode-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.qrcode-icon.wechat {
  background: #07c160;
}

.qrcode-icon.qq {
  background: #12b7f5;
}

.qrcode-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.qrcode-label {
  font-size: 14px;
  color: #9ca3af;
}

.qrcode-value {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
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

@media (max-width: 768px) {
  .nav-bar {
    padding: 12px 16px;
  }

  .nav-title {
    display: none;
  }

  .content-wrapper {
    padding: 20px 16px 60px;
  }

  .hero-section {
    padding: 40px 0 60px;
  }

  .hero-avatar {
    width: 120px;
    height: 120px;
  }

  .name-main {
    font-size: 32px;
  }

  .name-badge {
    padding: 4px 12px;
    font-size: 12px;
  }

  .hero-title-text {
    font-size: 16px;
  }

  .hero-quote {
    font-size: 15px;
  }

  .section-header h2 {
    font-size: 22px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-card.highlight {
    grid-column: span 1;
  }

  .projects-timeline {
    padding-left: 30px;
  }

  .project-dot {
    left: -26px;
  }

  .design-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .design-preview {
    height: 100px;
  }

  .manifesto-content {
    padding: 40px 20px;
  }

  .manifesto-quote {
    font-size: 20px;
  }
}

.author-page.light-theme {
  background: #f8fafc;
  color: #1e293b;
}

.author-page.light-theme .nav-bar {
  background: rgba(255, 255, 255, 0.95);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.author-page.light-theme .nav-title {
  color: #334155;
}

.author-page.light-theme .btn-back {
  background: rgba(15, 23, 42, 0.05);
  color: #334155;
}

.author-page.light-theme .btn-back:hover {
  background: rgba(15, 23, 42, 0.1);
}

.author-page.light-theme .hero-name {
  color: #0f172a;
}

.author-page.light-theme .hero-title-text {
  color: #475569;
}

.author-page.light-theme .hero-quote {
  color: #64748b;
}

.author-page.light-theme .section-header h2 {
  color: #0f172a;
}

.author-page.light-theme .section-icon {
  color: #8b5cf6;
}

.author-page.light-theme .info-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .info-card:hover {
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.author-page.light-theme .info-label {
  color: #64748b;
}

.author-page.light-theme .info-value {
  color: #0f172a;
}

.author-page.light-theme .info-value.link {
  color: #7c3aed;
}

.author-page.light-theme .info-value.link:hover {
  color: #6d28d9;
}

.author-page.light-theme .contact-type {
  color: #64748b;
}

.author-page.light-theme .contact-value {
  color: #7c3aed;
}

.author-page.light-theme .contact-value:hover {
  color: #6d28d9;
}

.author-page.light-theme .info-sub {
  color: #94a3b8;
}

.author-page.light-theme .info-icon {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.author-page.light-theme .skill-category {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .category-header {
  background: rgba(139, 92, 246, 0.05);
}

.author-page.light-theme .category-title {
  color: #0f172a;
}

.author-page.light-theme .skill-name {
  color: #0f172a;
}

.author-page.light-theme .skill-level {
  color: #8b5cf6;
}

.author-page.light-theme .skill-bar {
  background: rgba(139, 92, 246, 0.1);
}

.author-page.light-theme .skill-desc {
  color: #64748b;
}

.author-page.light-theme .timeline-line {
  background: linear-gradient(180deg, transparent, rgba(139, 92, 246, 0.3), rgba(139, 92, 246, 0.3), transparent);
}

.author-page.light-theme .project-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .project-card:hover {
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.06);
}

.author-page.light-theme .project-dot {
  background: #8b5cf6;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.2);
}

.author-page.light-theme .project-name {
  color: #0f172a;
}

.author-page.light-theme .project-desc {
  color: #64748b;
}

.author-page.light-theme .project-tag {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.author-page.light-theme .tech-badge {
  background: rgba(15, 23, 42, 0.05);
  color: #475569;
}

.author-page.light-theme .design-card {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .design-card:hover {
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
}

.author-page.light-theme .design-name {
  color: #0f172a;
}

.author-page.light-theme .design-type {
  color: #94a3b8;
}

.author-page.light-theme .scroll-hint {
  color: #94a3b8;
}

.author-page.light-theme .manifesto-section {
  background: rgba(139, 92, 246, 0.05);
}

.author-page.light-theme .manifesto-quote {
  color: #0f172a;
}

.author-page.light-theme .manifesto-author {
  color: #64748b;
}

.author-page.light-theme .manifesto-icon {
  color: #8b5cf6;
}

.author-page.light-theme .btn-home {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.author-page.light-theme .btn-home:hover {
  background: rgba(139, 92, 246, 0.2);
}

.author-page.light-theme .copyright {
  color: #94a3b8;
}

.author-page.light-theme .footer-qrcode-label {
  color: #64748b;
}

.author-page.light-theme .qrcode-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .qrcode-value {
  color: #0f172a;
}

.author-page.light-theme .skill-item {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .skill-name {
  color: #0f172a;
}

.author-page.light-theme .project-content {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.author-page.light-theme .project-content:hover {
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.06);
}

.author-page.light-theme .name-main {
  background: none;
  -webkit-background-clip: initial;
  -webkit-text-fill-color: #6b21a8;
  background-clip: initial;
  color: #6b21a8;
}
</style>
