<div align="center">

```
  _    _  _ _______                           _   _   ___ 
 | |  | || |_  / _ \___ _ _ ___ ___ _ _  __ _| | /_\ |_ _|
 | |__| __ |/ /|  _/ -) _(\\--/ _ \ ' \/ _` | |/ _ \ | | 
 |____|_||_/___|_| \___|_| /__/\___/_||_\__,_|_/_/ \_\___|
                                                          
```

**刘海柱Agent——Java大模型应用开发项目学习使用**

<a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot-3.5.8-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot" /></a>
<a href="https://spring.io/projects/spring-ai"><img src="https://img.shields.io/badge/Spring_AI-1.0.3-6DB33F?style=flat-square&logo=java&logoColor=white" alt="Spring AI" /></a>
<a href="https://vuejs.org/"><img src="https://img.shields.io/badge/Vue_3-3.x-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white" alt="Vue 3" /></a>
<a href="https://bailian.console.aliyun.com/cn-beijing/?tab=model#/model-market"><img src="https://img.shields.io/badge/Qwen3--Max-DashScope-FF6A00?style=flat-square&logo=alibabacloud&logoColor=white" alt="Qwen3-Max" /></a>
<a href="https://redis.io/blog/introducing-redis-stack/"><img src="https://img.shields.io/badge/Redis-Stack-DC382D?style=flat-square&logo=redis&logoColor=white" alt="Redis" /></a>

</div>

# LHZ 个人智能助手

代号"刘海柱"的 AI 智能助手全栈项目，融合大模型对话、联网搜索、知识库检索（RAG）、MCP 工具调用等核心 AI 能力，提供赛博朋克风格的现代交互体验。

🌐 在线体验：[https://ailhz.top](https://ailhz.top)

## 项目组成

| 子项目 | 技术栈 | 说明 |
|--------|--------|------|
| [liuhaizhu-backend](./liuhaizhu-backend) | Spring Boot 3 + Spring AI + MyBatis Plus | AI 对话后端服务 |
| [liuhaizhu-frontend](./liuhaizhu-frontend) | Vue 3 + Vite + Element Plus | 前端交互界面 |

## 系统架构

```
┌──────────────────────────────────────────────────────────────────┐
│                     Nginx (HTTPS + 反向代理)                       │
│                         :80 / :443                                │
└────────────┬─────────────────────────────────┬───────────────────┘
             │                                 │
             ▼                                 ▼
┌────────────────────────┐     ┌───────────────────────────────────┐
│   Vue 3 Frontend        │     │   Spring Boot Backend (:8000)     │
│   赛博朋克 UI            │────▶│   ┌──────────┐  ┌─────────────┐  │
│   SSE 流式对话           │ SSE │   │ Chat     │  │ RAG         │  │
│   GSAP 动效             │◀────│   │ Service  │  │ Service     │  │
│   Pinia 状态管理        │     │   └────┬─────┘  └──────┬──────┘  │
└────────────────────────┘     │        │               │          │
                               │        ▼               ▼          │
                               │   ┌──────────────────────────┐   │
                               │   │     Spring AI 1.0.3      │   │
                               │   │  (DashScope / Qwen3-Max) │   │
                               │   └──────────┬───────────────┘   │
                               │              │                    │
                               │   ┌──────────┴───────────┐       │
                               │   │   MCP Tools          │       │
                               │   │   Date/Email/Product │       │
                               │   └──────────────────────┘       │
                               └───────────┬───────────────────────┘
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    │                      │                      │
                    ▼                      ▼                      ▼
             ┌──────────┐         ┌──────────────┐       ┌──────────┐
             │  MySQL   │         │  Redis Stack  │       │ SearXNG  │
             │  8.0+    │         │ (缓存+向量)    │       │ 搜索引擎  │
             └──────────┘         └──────────────┘       └──────────┘
```

## 核心功能

### 后端

- **AI 智能对话**：基于 Qwen3-Max 模型 + Spring AI，SSE 流式响应，30 轮对话窗口记忆
- **联网搜索**：集成 SearXNG 搜索引擎，搜索结果注入 LLM 对话上下文
- **知识库检索（RAG）**：Tika 文档解析 → 递归文本分割 → Redis 向量存储 → 语义检索
- **MCP 工具调用**：内置 DateTool / EmailTool / ProductTool，支持外部 MCP Server（如高德地图）
- **用户系统**：邮箱验证注册、JWT 双 Token 认证（Access 24h + Refresh 7d）、BCrypt 密码加密
- **权限控制**：Spring Security + 自定义注解（`@RequirePermission`）+ AOP 切面
- **高级特性**：请求限流（Redis Lua）| 分布式锁 | 慢请求告警 | 会话数量控制 | Actuator 健康监控

### 前端

- **流式对话**：SSE 字符级实时流式输出，支持文本和文件两种输入方式
- **三种对话模式**：普通对话 / 联网搜索 / 知识库检索，互斥切换
- **会话管理**：多会话 CRUD，消息持久化，标题自定义编辑
- **用户系统**：用户名/邮箱注册、JWT 自动刷新、个人信息管理、数据加密存储
- **管理后台**：用户管理、知识库管理、Token 用量统计，路由守卫 + 权限指令双重保护
- **赛博朋克 UI**：科幻粒子背景、故障文字特效、HUD 装饰、暗黑/明亮双主题
- **交互动效**：GSAP 卡片抽出页面转场、Lenis 阻尼滚动、打字机效果

## 技术栈总览

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 (Composition API) |
| 构建工具 | Vite 7 |
| 状态管理 | Pinia 3 |
| UI 组件 | Element Plus 2 |
| 动画 | GSAP 3 + Lenis 1 |
| 后端框架 | Spring Boot 3.5.8 |
| AI 框架 | Spring AI 1.0.3 + DashScope (Qwen3-Max) |
| ORM | MyBatis Plus 3.5.12 |
| 数据库 | MySQL 9.0+ |
| 缓存/向量 | Redis Stack |
| 搜索引擎 | SearXNG |
| 安全 | Spring Security + JWT (auth0) |
| 文件存储 | x-file-storage (MinIO / Aliyun OSS) |
| MCP 协议 | Spring AI MCP 1.0.3 |
| 容器化 | Docker + Docker Compose |
| CI/CD | Jenkins |
| Web 服务器 | Nginx (HTTPS + 反向代理) |

## 目录结构

```
liuhaizhu-agent/
├── liuhaizhu-backend/          # Spring Boot 后端
│   ├── src/main/java/.../      # Java 源码
│   ├── src/main/resources/     # 配置 / SQL / 提示词
│   ├── Dockerfile              # 后端镜像构建
│   ├── docker-compose.yml      # 后端容器编排
│   ├── Jenkinsfile             # 后端 CI/CD
│   └── pom.xml                 # Maven 配置
├── liuhaizhu-frontend/         # Vue 3 前端
│   ├── src/                    # Vue 源码
│   ├── public/                 # 静态资源
│   ├── Dockerfile              # 前端镜像构建
│   ├── docker-compose.yml      # 前端容器编排
│   ├── nginx.conf              # Nginx 配置
│   └── Jenkinsfile             # 前端 CI/CD
├── LICENSE                     # MIT License
└── README.md                   # 本文件
```

## 快速开始

### 环境要求

- **后端**：JDK 17+ / Maven 3.6+ / MySQL 9.0+ / Redis Stack / SearXNG
- **前端**：Node.js ^20.19.0 / Yarn 1.22+

### 后端启动

```bash
cd liuhaizhu-backend

# 1. 配置环境变量
cp src/main/resources/.env-template .env
# 编辑 .env 填入实际配置（数据库、Redis、DashScope API Key 等）

# 2. 初始化数据库（执行 SQL 脚本）
# src/main/resources/sql/liuhaizhu.sql

# 3. 启动
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8000`。

### 前端启动

```bash
cd liuhaizhu-frontend

# 安装依赖
yarn install

# 启动开发服务器
yarn dev
```

前端默认运行在 `http://localhost:5173`，已配置反向代理到后端 8000 端口。

### Docker Compose 一键部署（生产环境）

前后端均有独立的 `docker-compose.yml`，可按需分别部署：

```bash
# 部署后端
cd liuhaizhu-backend
# 编辑 docker-compose.yml 中的环境变量
docker-compose up -d --build

# 部署前端（含 Nginx HTTPS）
cd liuhaizhu-frontend
# 准备 SSL 证书到 ssl/ 目录，编辑 docker-compose.yml
docker-compose up -d --build
```

## 默认管理员

系统首次启动时会自动创建默认管理员账户（用户名 `admin`，角色 ADMIN），密码请查看 `DataInitializer.java` 中的初始化逻辑，**生产环境务必修改默认密码**。

## 文档索引

- [后端 README](./liuhaizhu-backend/README.md) — API 接口、架构设计、MCP 工具、开发规范
- [前端 README](./liuhaizhu-frontend/README.md) — 路由表、组件说明、主题系统、权限控制

## License

[MIT](./LICENSE)
