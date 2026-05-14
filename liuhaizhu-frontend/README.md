# LHZ 个人智能助手 - 前端项目

基于 Vue 3 + Vite 构建的 AI 智能聊天助手前端应用，集成联网搜索、知识库检索、实时流式对话等多种 AI 能力，提供赛博朋克风格的现代交互体验。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | ^3.5.22 |
| 构建工具 | Vite | ^7.1.11 |
| 状态管理 | Pinia | ^3.0.3 |
| 路由 | Vue Router | ^4.6.3 |
| UI 组件库 | Element Plus | ^2.7.6 |
| HTTP 客户端 | Axios | ^1.7.7 |
| 动画引擎 | GSAP | ^3.14.2 |
| 平滑滚动 | Lenis | ^1.3.17 |
| 代码规范 | ESLint + Prettier | - |
| 单元测试 | Vitest + @vue/test-utils | - |
| Node 版本 | ^20.19.0 \|\| >=22.12.0 | - |
| 包管理器 | Yarn | 1.22.22 |

## 核心功能

### 🤖 智能对话
- **流式响应**：基于 SSE (Server-Sent Events) 的字符级实时流式输出，对话体验流畅自然
- **多模态输入**：支持文本和文件上传两种交互方式
- **消息操作**：支持消息复制、失败重发等便捷操作

### 🌐 联网搜索
- 一键开启联网搜索模式，实时获取互联网最新信息
- 基于 SearXNG 搜索引擎聚合，确保回答紧跟时事

### 📚 知识库检索 (RAG)
- 支持上传文档构建个人专属知识库
- 基于向量检索（Milvus / Qdrant / Redis Stack）的精准语义匹配
- 支持多种文档格式，文件大小上限 10MB

### 💬 会话管理
- 多会话支持，可创建、切换、删除历史会话
- 会话消息持久化存储，刷新页面不丢失
- 会话标题自定义编辑

### 👤 用户系统
- 用户名/邮箱多种注册方式
- JWT 双 Token 认证（Access Token + Refresh Token），自动刷新
- 用户个人信息管理与头像编辑
- 数据加密存储（localStorage 加密）

### 👑 VIP 会员
- 会员升级页面，展示不同等级权益
- 答题挑战趣味互动

### 🛡️ 管理后台
- 用户管理：查看、禁用/启用、删除用户，分配角色
- 知识库管理：查看和管理所有用户的知识库文档
- 仅管理员角色可访问，路由守卫 + 权限指令双重保护

### 🎨 界面体验
- **赛博朋克视觉风格**：科幻背景、故障文字特效、HUD 装饰元素
- **暗黑/明亮双主题**：一键切换，主题偏好持久化
- **卡片抽出转场动画**：基于 GSAP 的页面切换动效
- **阻尼滚动**：基于 Lenis 的平滑滚动体验
- **全站响应式**：适配桌面端和移动端

### 🖼️ 特色动效
- `CyberBackground`：动态科幻粒子背景
- `GlitchText`：故障风格文字特效
- `LazyImage`：图片懒加载 + 渐进加载
- `DemoWindow`：打字机效果 + 故障干扰模拟

## 项目结构

```
liuhaizhu-ai-chat-frontend/
├── public/                        # 静态资源
│   ├── avatar/                    # 用户头像资源
│   ├── contactInformation/        # 联系方式二维码
│   ├── design/                    # 设计素材
│   ├── images/                    # 图片资源
│   ├── svg/                       # SVG 图标
│   │   ├── integration/           # 集成服务图标
│   │   └── tech-stack/            # 技术栈图标
│   ├── team/                      # 团队成员头像
│   └── favicon.ico                # 网站图标
├── src/
│   ├── api/                       # API 接口层
│   │   ├── config.js              # Axios 实例配置 + 拦截器
│   │   ├── index.js               # API 统一导出
│   │   ├── auth.js                # 认证相关接口
│   │   ├── chat.js                # 聊天 + SSE 连接
│   │   ├── conversation.js        # 会话管理接口
│   │   ├── knowledge.js           # 知识库管理接口（管理员）
│   │   ├── rag.js                 # RAG 知识库接口
│   │   └── user.js                # 用户管理接口
│   ├── components/                # 公共组件
│   │   ├── chating/               # 聊天相关组件
│   │   │   ├── ConversationList.vue    # 会话列表
│   │   │   └── SettingsModal.vue       # 设置弹窗
│   │   ├── common/                # 通用组件
│   │   │   ├── CyberBackground.vue     # 科幻背景
│   │   │   ├── GlitchText.vue          # 故障文字
│   │   │   ├── LazyImage.vue           # 懒加载图片
│   │   │   ├── LoginModal.vue          # 登录/注册弹窗
│   │   │   ├── Logo.vue                # Logo 组件
│   │   │   ├── ThemeToggle.vue          # 主题切换
│   │   │   └── ToastNotification.vue   # Toast 通知
│   │   ├── footing/               # 页脚弹窗
│   │   │   ├── ContactUsModal.vue      # 联系我们
│   │   │   ├── FAQModal.vue            # 常见问题
│   │   │   ├── PrivacyPolicyModal.vue  # 隐私政策
│   │   │   ├── RelatedLinksModal.vue   # 相关链接
│   │   │   └── TermsOfServiceModal.vue # 服务条款
│   │   ├── knowledge-base/        # 知识库管理
│   │   │   ├── AddDocumentDialog.vue   # 添加文档弹窗
│   │   │   ├── DeleteConfirmDialog.vue # 删除确认弹窗
│   │   │   ├── KnowledgeBaseItem.vue   # 知识库条目
│   │   │   └── KnowledgeBaseManager.vue# 知识库管理器
│   │   ├── landing/               # 首页模块
│   │   │   ├── DemoWindow.vue          # 演示窗口
│   │   │   ├── FeaturesSection.vue     # 功能亮点
│   │   │   ├── FooterSection.vue       # 页脚区域
│   │   │   ├── TechStackSection.vue    # 技术栈展示
│   │   │   └── VIPSection.vue          # VIP 区域
│   │   └── user/                  # 用户组件
│   │       ├── UserInfoCard.vue         # 用户信息卡片
│   │       └── UserProfileModal.vue     # 用户资料编辑
│   ├── composables/               # 组合式函数
│   │   └── useToast.js            # Toast 通知 Hook
│   ├── directives/                # 自定义指令
│   │   └── permission.js          # v-permission 权限指令
│   ├── router/
│   │   └── index.js               # 路由配置 + 导航守卫
│   ├── stores/                    # Pinia 状态管理
│   │   ├── auth.js                # 认证状态
│   │   ├── chat.js                # 聊天状态
│   │   ├── settings.js            # 设置状态（主题等）
│   │   └── transition.js          # 页面转场状态
│   ├── styles/
│   │   └── variables.css          # CSS 变量定义
│   ├── utils/                     # 工具函数
│   │   ├── helpers.js             # 安全存储、ID 生成等
│   │   ├── imageUtils.js          # 图片处理工具
│   │   └── permission.js          # 权限校验工具
│   ├── views/                     # 页面视图
│   │   ├── LandingView.vue        # 首页
│   │   ├── ChatView.vue           # 聊天主界面
│   │   ├── FeaturesView.vue       # 功能展示页
│   │   ├── DemoView.vue           # 演示页
│   │   ├── IntegrationsView.vue   # 技术集成页
│   │   ├── AdminDashboard.vue     # 管理员控制台
│   │   ├── UpgradeVIPView.vue     # VIP 升级页
│   │   ├── QuizView.vue           # 答题挑战页
│   │   ├── AuthorProfileView.vue  # 作者简介页
│   │   └── TeamView.vue           # 团队介绍页
│   ├── App.vue                    # 根组件
│   └── main.js                    # 应用入口
├── .dockerignore                  # Docker 忽略配置
├── .editorconfig                  # 编辑器配置
├── .env.development               # 开发环境变量
├── .env.production                # 生产环境变量
├── .gitignore                     # Git 忽略配置
├── .prettierrc.json               # Prettier 配置
├── Dockerfile                     # Docker 构建文件
├── docker-compose.yml             # Docker Compose 编排
├── Jenkinsfile                    # Jenkins CI/CD 流水线
├── eslint.config.js               # ESLint 配置
├── index.html                     # HTML 入口
├── jsconfig.json                  # JS 路径别名配置
├── nginx.conf                     # Nginx 配置（HTTPS + 反向代理）
├── package.json                   # 项目依赖与脚本
├── vite.config.js                 # Vite 构建配置
└── vitest.config.js               # Vitest 测试配置
```

## 路由表

| 路径 | 名称 | 组件 | 权限 |
|------|------|------|------|
| `/` | landing | LandingView | 公开 |
| `/chat` | chat | ChatView | 公开 |
| `/features` | features | FeaturesView | 公开 |
| `/demo` | demo | DemoView | 公开 |
| `/integrations` | integrations | IntegrationsView | 公开 |
| `/admin` | admin | AdminDashboard | 需登录 + ADMIN 角色 |
| `/upgrade-vip` | upgrade-vip | UpgradeVIPView | 公开 |
| `/quiz` | quiz | QuizView | 公开 |
| `/author` | author | AuthorProfileView | 公开 |
| `/team` | team | TeamView | 公开 |

## 快速开始

### 环境要求

- Node.js: `^20.19.0` 或 `>=22.12.0`
- 包管理器：Yarn 1.22.22+ 或 npm（随 Node.js 内置）

### 本地开发

```bash
# 克隆项目
git clone <repo-url>
cd liuhaizhu-ai-chat-frontend

# 安装依赖（Yarn / npm 二选一）
yarn install
# 或
npm install

# 启动开发服务器
yarn dev
# 或
npm run dev
```

开发服务器默认运行在 `http://localhost:5173`，并监听 `0.0.0.0` 允许局域网访问。

### 环境变量

| 变量名 | 说明 | 开发环境默认值 | 生产环境默认值 |
|--------|------|---------------|---------------|
| `VITE_API_BASE_URL` | 后端 API 地址 | `http://localhost:8000` | 空（由 Nginx 反向代理） |

### 可用脚本

```bash
# Yarn
yarn dev          # 启动开发服务器
yarn build        # 构建生产版本
yarn preview      # 预览生产构建
yarn test:unit    # 运行单元测试
yarn lint         # ESLint 代码检查 + 自动修复
yarn format       # Prettier 代码格式化

# npm
npm run dev          # 启动开发服务器
npm run build        # 构建生产版本
npm run preview      # 预览生产构建
npm run test:unit    # 运行单元测试
npm run lint         # ESLint 代码检查 + 自动修复
npm run format       # Prettier 代码格式化
```

## 构建与部署

### 构建产物

```bash
# Yarn
yarn build
# npm
npm run build
```

构建输出在 `dist/` 目录，包含手动分包（code-splitting）：
- `vue-vendor`：Vue + Vue Router + Pinia
- `element-plus`：Element Plus UI 库
- `gsap`：动画引擎
- `axios`：HTTP 客户端

静态资源按类型分类存放（js/css/images/fonts）。

### Docker 部署

```bash
# 1. 准备 SSL 证书
mkdir -p ssl
# 将你的证书文件放入 ssl/ 目录：
#   ssl/your-domain.com.pem  - SSL 证书
#   ssl/your-domain.com.key  - 私钥

# 2. 修改 docker-compose.yml 中的 BACKEND_HOST（后端服务器IP）
#    - Docker Desktop(Windows/Mac): 使用 host.docker.internal
#    - Linux: 使用宿主机局域网IP 或 Docker 默认网关 IP

# 3. 构建并启动
docker-compose up -d --build

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止
docker-compose down
```

容器暴露 80 和 443 端口（HTTP + HTTPS），通过 Nginx 配置 HTTPS 重定向和反向代理。

环境变量说明：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `BACKEND_HOST` | 后端 API 服务器地址 | `172.17.0.1` (Docker 默认网关) |

### Nginx 配置

生产环境使用 Nginx 作为 Web 服务器，配置了：
- HTTP → HTTPS 自动重定向
- SSL/TLS 证书配置
- `/api/*` 反向代理到后端服务
- `/api/sse` SSE 长连接代理（超时 600s）
- Gzip 压缩
- 静态资源强缓存（1 年）
- 安全头部（X-Frame-Options, X-Content-Type-Options 等）

### Jenkins CI/CD

项目包含完整的 Jenkins Pipeline 配置，自动化流程：

1. **拉取代码**：从 Git 仓库检出
2. **构建 Docker 镜像**：基于 Dockerfile 构建
3. **推送镜像**：推送至私有/公共镜像仓库
4. **部署到服务器**：通过 SSH 远程执行 `docker-compose up -d`
5. **健康检查**：验证服务状态

## API 接口概览

### 认证模块 (`/api/auth`)
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/send-register-code` - 发送注册验证码
- `POST /api/auth/refresh` - 刷新 Token
- `GET /api/auth/me` - 获取当前用户信息
- `POST /api/auth/logout` - 退出登录

### 聊天模块 (`/api/chat`, `/api/sse`)
- `GET /api/sse/connect` - SSE 流式连接
- `POST /api/chat/doChat` - 普通对话
- `POST /api/webSearch/query` - 联网搜索对话
- `POST /api/rag/search` - 知识库搜索对话
- `POST /api/fileChat/chat` - 文件上传对话
- `DELETE /api/chat/conversations/clear` - 清空会话消息

### 会话管理 (`/api/conversation`)
- `POST /api/conversation/create` - 创建会话
- `GET /api/conversation/list` - 获取会话列表
- `GET /api/conversation/detail` - 获取会话详情
- `DELETE /api/conversation/delete` - 删除会话
- `DELETE /api/conversation/clear` - 清空会话消息

### 知识库 (`/api/rag`)
- `POST /api/rag/upload` - 上传知识库文档
- `GET /api/rag/list` - 获取知识库文档列表
- `DELETE /api/rag/delete` - 删除知识库文档
- `POST /api/rag/rename` - 重命名知识库文档
- `POST /api/rag/search` - 搜索知识库

### 管理接口 (`/api/admin`)
- `GET /api/admin/users` - 获取用户列表
- `GET /api/admin/users/:id` - 获取用户详情
- `DELETE /api/admin/users/:id` - 删除用户
- `PUT /api/admin/users/:id/status` - 更新用户状态
- `PUT /api/admin/users/:id/role` - 更新用户角色
- `GET /api/admin/knowledge/list` - 获取知识库文档列表

## 架构设计

### 认证流程
1. 用户登录/注册后，后端返回 Access Token + Refresh Token
2. Token 经加密后存储在 localStorage
3. 每次请求通过 Axios 拦截器自动携带 `Authorization` 头和 `X-User-Id` 头
4. Access Token 过期时，拦截器自动使用 Refresh Token 刷新，请求队列机制确保并发请求不会重复刷新
5. App 根组件每 30 分钟自动刷新一次 Token

### 聊天流程
1. 页面加载时建立 SSE 长连接（通过 EventSource）
2. 用户发送消息 → 先添加到本地消息列表 → 调用 API 发送到后端
3. SSE 推送流式响应 → 逐字更新助手消息内容
4. 支持联网搜索 / 知识库搜索模式的切换，互斥模式确保同一时间只启用一种增强

### 页面转场
- Landing 页面使用卡片抽出动效切换到子页面
- 基于 `transitionStore` 管理动画状态
- GSAP 驱动动画，600ms 完成
- 支持正向/反向/反向卡片抽出三种动画模式

### 主题系统
- CSS 变量定义在 `src/styles/variables.css`
- 通过 `data-theme` 属性和 `.dark-mode` 类切换样式
- 主题偏好存储在 localStorage 中持久化

### 权限控制
- **路由守卫**：`router.beforeEach` 检查登录态和管理员角色
- **自定义指令**：`v-permission` 基于角色和权限的 DOM 元素显隐控制
- **工具函数**：`usePermission()` 提供 `hasPermission` / `hasAnyRole` 等权限校验方法

## 开发规范

### 代码风格
- 使用 ESLint + Prettier 统一代码风格
- Vue 组件采用 `<script setup>` Composition API
- Pinia Store 采用 Setup Store 语法（组合式）
- 路径别名 `@` 指向 `src/` 目录

### 命名约定
- **组件文件**：PascalCase（如 `ConversationList.vue`）
- **工具/API 文件**：camelCase（如 `helpers.js`）
- **Store 文件**：kebab-case（如 `auth.js`）
- **目录**：kebab-case（如 `knowledge-base/`）

### 提交前检查

```bash
# Yarn
yarn lint      # ESLint 检查 + 自动修复
yarn format    # Prettier 格式化
yarn test:unit # 运行测试

# npm
npm run lint      # ESLint 检查 + 自动修复
npm run format    # Prettier 格式化
npm run test:unit # 运行测试
```

## 设计理念

### 项目定位
打造一个代号"刘海柱"的 AI 个人智能助手，融合赛博朋克美学风格与现代 AI 对话技术，提供集联网搜索、知识库检索、文档分析于一体的全能 AI 对话平台。

### 设计原则
- **渐进增强**：从静态展示到交互动画，逐层增强用户体验
- **细粒度的加载状态**：loading 动画、流式打字、思考气泡等多种状态反馈
- **响应式优先**：移动端适配侧边栏切换、布局自适应

## License

[MIT License](LICENSE)

Copyright (c) 2025 aceFelix

本项目基于 MIT 协议开源，任何人可免费学习、使用、修改和分发。