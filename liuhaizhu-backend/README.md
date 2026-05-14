# LHZ 个人智能助手 - 后端项目

基于 Spring Boot 3 + Spring AI 构建的 AI 智能聊天助手后端服务，集成大模型对话、联网搜索、知识库检索（RAG）、MCP 工具调用等核心能力。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.5.8 |
| JDK | Java | 17 |
| 安全框架 | Spring Security + JWT (auth0) | 6.x / 4.5.0 |
| ORM | MyBatis Plus | 3.5.12 |
| 数据库 | MySQL | - |
| 缓存 / 向量存储 | Redis Stack | - |
| AI 框架 | Spring AI (含 DashScope 适配) | 1.0.3 |
| 大模型 | 通义千问 Qwen3-Max | - |
| MCP 协议 | Spring AI MCP | 1.0.3 |
| 搜索引擎 | SearXNG | - |
| HTTP 客户端 | OkHttp | 4.12.0 |
| 文件存储 | x-file-storage (MinIO / Aliyun OSS) | 2.3.0 |
| 邮件 | Spring Boot Mail (Jakarta Mail) | 3.5.8 |
| Markdown 解析 | Flexmark | 0.64.8 |
| 文档解析 | Apache Tika (Spring AI Tika Reader) | 1.0.3 |
| 工具包 | Hutool | 5.8.42 |
| 环境变量 | dotenv-java | 3.2.0 |
| 容器化 | Docker + Docker Compose | - |
| CI/CD | Jenkins | - |
| 构建工具 | Maven Wrapper | - |

## 核心功能

### 🤖 AI 智能对话
- 基于 **Qwen3-Max** 模型的智能对话，支持结构化输出和联网搜索
- **SSE (Server-Sent Events)** 流式推送，字符级实时响应
- 对话上下文管理：基于 `MessageWindowChatMemory`，保留最近 30 轮对话
- 自定义系统提示词：高度定制"刘海柱"人格（东北糙汉风），提示词文件化可配置

### 🌐 联网搜索 (Web Search)
- 集成 **SearXNG** 开源搜索引擎，聚合多源搜索结果
- 搜索结果作为上下文注入 LLM 对话，使回答紧跟时事
- 基于 OkHttp 发送搜索请求，结果结构化排序

### 📚 知识库检索 (RAG)
- 基于 **Spring AI** 文档解析管道 + **Redis Vector Store** 向量存储
- 支持多种文档格式（Tika Reader 解析 PDF、Word、TXT 等）
- 自定义文本分割器（`RecursiveTextSplitter`）实现智能分片
- **异步处理**：文件先上传至 OSS，后台异步解析分片并向量化
- 进度追踪：Redis 记录处理状态，支持实时查询任务进度
- 文件大小限制：10MB

### 📄 文件对话 (File Chat)
- 上传文件后，基于文件内容进行实时问答
- 支持 Tika 解析多种格式，提取文本作为对话上下文

### 🔧 MCP 工具调用 (Model Context Protocol)
- **内置工具**（基于 `@Tool` 注解）：
  - `DateTool`：获取北京时间 / 指定时区时间
  - `EmailTool`：发送邮件（支持纯文本、Markdown、HTML 格式）
  - `ProductTool`：商品 CRUD 管理（增删改查、条件筛选、排序）
- **外部 MCP 集成**：支持通过 JSON 配置文件加载外部 MCP Server（如高德地图等）
- 工具权限过滤：根据用户角色动态过滤可用工具
- 异步 MCP Client + SSE 通信

### 👤 用户系统
- **注册**：用户名 / 邮箱两种注册方式，邮箱注册需验证码
- **登录**：基于 Spring Security + JWT 认证
- **双 Token 机制**：
  - Access Token（24h 有效期）
  - Refresh Token（7 天有效期，用于无感刷新）
- BCrypt 密码加密
- 用户资料管理：修改密码、绑定邮箱、编辑个人资料
- 账户注销（软删除）

### 🔐 权限控制
- **Spring Security**：全局安全配置，无状态 Session 管理
- **JWT 认证过滤器**（`JwtAuthenticationFilter`）：解析 Token 并设置安全上下文
- **自定义注解**（`@RequirePermission`）：
  - 角色匹配（`allowedRoles` / `excludedRoles`）
  - 管理员验证（`admin = true`）
  - VIP 验证（`vip = true`）
- **AOP 切面**（`PermissionAspect`）：在方法执行前进行权限校验

### 🛡️ 管理后台
- 用户管理：列表查询、详情查看、删除、状态启用/禁用、角色变更
- 知识库管理：查看和管理所有用户的知识库文档
- Token 用量统计：用户 Token 消耗汇总、每日统计

### ⚡ 高级特性
- **请求限流**（`@RateLimit`）：支持令牌桶、滑动窗口、固定窗口三种算法，基于 Redis Lua 脚本实现
- **分布式锁**（`@DistributedLock`）：基于 Redis 的分布式锁，防止并发问题
- **日志切面**（`LogAspect`）：自动记录方法耗时
- **请求日志拦截器**（`RequestLogInterceptor`）：记录每个请求的 IP、耗时、状态，慢请求告警（>3s）
- **全局异常处理**（`GlobalExceptionHandler`）：统一异常拦截和友好错误响应
- **会话数量控制**：每个用户最多 50 个会话，超出自动删除最早会话
- **异步线程安全**：`SecurityContextHolder` 配置 `MODE_INHERITABLETHREADLOCAL`

### 🔍 健康监控
- Spring Boot Actuator 集成
- 暴露端点：`/actuator/health`、`/actuator/info`、`/actuator/metrics`
- Redis 和数据库健康检查

## 项目结构

```
liuhaizhu-ai-chat-backend/
├── src/main/java/com/itfelix/liuhaizhuaichat/
│   ├── annotation/                    # 自定义注解
│   │   ├── DistributedLock.java       # 分布式锁注解
│   │   ├── RateLimit.java             # 限流注解
│   │   └── RequirePermission.java     # 权限注解
│   ├── aspect/                        # AOP 切面
│   │   ├── DistributedLockAspect.java # 分布式锁切面
│   │   ├── LogAspect.java             # 日志切面（耗时记录）
│   │   ├── PermissionAspect.java      # 权限校验切面
│   │   └── RateLimitAspect.java       # 限流切面
│   ├── config/                        # 配置类
│   │   ├── AsyncConfig.java           # 异步任务配置
│   │   ├── CacheConfig.java           # 缓存配置
│   │   ├── FileRecorderImpl.java      # 文件记录器实现
│   │   ├── HealthCheckConfig.java     # 健康检查配置
│   │   ├── LLMconfig.java             # LLM / ChatClient 配置
│   │   ├── OkHttpConfig.java          # OkHttp 客户端配置
│   │   ├── SecurityConfig.java        # Spring Security 配置
│   │   └── SseHeartbeatConfig.java    # SSE 心跳配置
│   ├── controller/                    # 控制器层
│   │   ├── admin/                     # 管理端控制器
│   │   │   ├── AdminRAGController.java    # 管理员知识库管理
│   │   │   ├── AdminTokenController.java  # Token 用量统计
│   │   │   └── AdminUserController.java   # 用户管理
│   │   ├── AuthController.java        # 认证（登录/注册/刷新Token）
│   │   ├── ChatController.java        # 普通聊天
│   │   ├── ConversationController.java# 会话管理
│   │   ├── FileChatController.java    # 文件上传聊天
│   │   ├── RAGController.java         # 知识库管理
│   │   ├── SSEController.java         # SSE 长连接
│   │   ├── UserProfileController.java # 用户个人信息
│   │   └── WebSearchController.java   # 联网搜索
│   ├── enums/                         # 枚举类
│   │   ├── PermissionType.java        # 权限类型枚举
│   │   ├── SSEMessageType.java        # SSE 消息类型
│   │   └── UserRoleEnum.java          # 用户角色（ADMIN/VIP/USER）
│   ├── exception/                     # 异常处理
│   │   └── GlobalExceptionHandler.java# 全局异常处理器
│   ├── handler/                       # TypeHandler
│   │   └── UserRoleEnumTypeHandler.java
│   ├── initializer/                   # 初始化器
│   │   └── DataInitializer.java       # 默认管理员初始化
│   ├── interceptor/                   # 拦截器
│   │   └── RequestLogInterceptor.java # 请求日志拦截
│   ├── logback/                       # 日志配置
│   │   └── MyColorConverter.java      # 日志颜色转换
│   ├── mapper/                        # MyBatis Mapper
│   │   ├── ChatMessageMapper.java
│   │   ├── ConversationMapper.java
│   │   ├── FileRecordMapper.java
│   │   ├── ProductMapper.java
│   │   ├── TokenUsageMapper.java
│   │   └── UserMapper.java
│   ├── mcp/                           # MCP 工具模块
│   │   ├── bean/                      # MCP 工具请求参数
│   │   │   ├── AddProductRequest.java
│   │   │   ├── EmailRequest.java
│   │   │   ├── ModifyProductRequest.java
│   │   │   └── QueryProductRequest.java
│   │   ├── config/
│   │   │   └── McpConfig.java         # MCP 客户端配置
│   │   ├── enums/                     # MCP 工具枚举
│   │   │   ├── ListSortEnum.java
│   │   │   ├── PriceCompareEnum.java
│   │   │   └── ProductStatusEnum.java
│   │   ├── tool/                      # MCP 工具实现
│   │   │   ├── DateTool.java          # 时间工具
│   │   │   ├── EmailTool.java         # 邮件工具
│   │   │   └── ProductTool.java       # 商品管理工具
│   │   ├── utils/
│   │   │   └── DocumentFormatConversion.java# 文档格式转换
│   │   └── MyToolCallbackProvider.java# 工具注册与权限过滤
│   ├── pojo/                          # 数据对象
│   │   ├── dto/                       # 数据传输对象
│   │   ├── entity/                    # 数据库实体
│   │   │   ├── ChatMessage.java       # 聊天消息
│   │   │   ├── Conversation.java      # 会话
│   │   │   ├── FileRecord.java        # 文件记录
│   │   │   ├── Product.java           # 商品
│   │   │   ├── TokenUsage.java        # Token 用量
│   │   │   └── User.java              # 用户
│   │   └── vo/                        # 视图对象
│   │       ├── ConversationVO.java
│   │       ├── LoginResponse.java
│   │       ├── RefreshTokenRequest.java
│   │       └── UserVO.java
│   ├── records/                       # Java Record
│   │   ├── Fool.java
│   │   └── OssUploadResult.java
│   ├── security/                      # 安全模块
│   │   ├── CustomUserDetails.java     # 自定义用户详情
│   │   ├── CustomUserDetailsService.java# 用户加载服务
│   │   └── JwtAuthenticationFilter.java# JWT 认证过滤器
│   ├── service/                       # 服务接口
│   │   ├── AdminUserService.java
│   │   ├── AuthService.java
│   │   ├── ChatService.java
│   │   ├── ConversationService.java
│   │   ├── PermissionService.java
│   │   ├── RAGService.java
│   │   ├── SearXngService.java
│   │   ├── TitleGenerationService.java
│   │   ├── UserProfileService.java
│   │   └── impl/                      # 服务实现
│   │       ├── AdminUserServiceImpl.java
│   │       ├── AuthServiceImpl.java
│   │       ├── ChatServiceImpl.java
│   │       ├── ConversationServiceImpl.java
│   │       ├── PermissionServiceImpl.java
│   │       ├── RAGServiceImpl.java
│   │       ├── SearXngServiceImpl.java
│   │       ├── TitleGenerationServiceImpl.java
│   │       └── UserProfileServiceImpl.java
│   ├── utils/                         # 工具类
│   │   ├── AceResult.java             # 统一响应体
│   │   ├── DistributedLockUtil.java   # Redis 分布式锁
│   │   ├── JwtUtil.java               # JWT 工具
│   │   ├── MyTokenTextSplitter.java   # 自定义文本分割器
│   │   ├── RateLimiterUtil.java       # Redis 限流器
│   │   ├── RecursiveTextSplitter.java # 递归文本分割器
│   │   ├── SSEServerUtil.java         # SSE 连接管理
│   │   └── UserContext.java           # 用户上下文
│   └── LiuHaizhuAiChatApplication.java# Spring Boot 启动类
├── src/main/resources/
│   ├── docker/                        # Docker 运行脚本
│   ├── mapper/                        # MyBatis XML 映射
│   ├── mcp/                           # MCP Server 配置模板
│   │   └── mcp-server-template.json
│   ├── sql/                           # 数据库脚本
│   │   ├── liuhaizhu.sql              # 建表 + 初始数据
│   │   ├── add_token_usage.sql        # Token 用量表
│   │   └── update_email_nullable.sql  # 邮箱字段升级
│   ├── systemPrompt/                  # 系统提示词
│   │   └── liuhaizhu.txt              # 刘海柱人设提示词
│   ├── .env-template                  # 环境变量模板
│   ├── application.yml                # 主配置文件
│   └── logback.xml                    # 日志配置
├── Dockerfile                         # Docker 构建文件
├── docker-compose.yml                 # Docker Compose 编排
├── Jenkinsfile                        # Jenkins CI/CD 流水线
└── pom.xml                            # Maven 项目配置
```

## 数据库表结构

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `user` | 用户表 | user_id, username, password(BCrypt), email, avatar, role(ADMIN/VIP/USER), status |
| `conversation` | 会话表 | id, user_id, title, type(normal/rag/web_search), pinned, deleted |
| `chat_message` | 聊天消息表 | id, conversation_id, user_id, role(user/assistant), content, token_count |
| `product` | 商品表（MCP 工具用） | product_id, product_name, brand, price, stock, status |
| `file_record` | 文件记录表（x-file-storage） | id, url, size, filename, original_filename |
| `token_usage` | Token 用量表 | id, user_id, model, prompt_tokens, completion_tokens, total_tokens |

## API 接口

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 认证模块 (`/api/auth`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | 公开 |
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/send-register-code` | 发送注册验证码 | 公开 |
| POST | `/api/auth/refresh` | 刷新 Token | 公开 |
| GET | `/api/auth/me` | 获取当前用户信息 | 需登录 |
| POST | `/api/auth/logout` | 退出登录 | 需登录 |

### 聊天模块 (`/api/chat`, `/api/sse`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/sse/connect` | 建立 SSE 连接 | 公开（Token 通过 URL 参数） |
| POST | `/api/chat/doChat` | 普通对话（SSE 流式） | 公开（限流 5 QPS） |
| POST | `/api/webSearch/query` | 联网搜索对话 | 公开 |
| POST | `/api/rag/search` | 知识库搜索对话 | 需登录 + VIP |
| POST | `/api/fileChat/chat` | 文件上传对话 | 需登录 |
| DELETE | `/api/chat/conversations/clear` | 清空会话消息 | 需登录 |

### 会话管理 (`/api/conversation`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/conversation/create` | 创建会话 | 需登录 |
| GET | `/api/conversation/list` | 获取会话列表 | 需登录 |
| GET | `/api/conversation/detail` | 获取会话详情（含消息） | 需登录 |
| DELETE | `/api/conversation/delete` | 删除会话 | 需登录 |
| DELETE | `/api/conversation/clear` | 清空会话消息 | 需登录 |

### 知识库 (`/api/rag`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/rag/upload` | 上传知识库文档 | 需登录 + VIP |
| GET | `/api/rag/upload/status/{taskId}` | 查询上传处理进度 | 需登录 + VIP |
| GET | `/api/rag/list` | 获取知识库文档列表 | 需登录 + VIP |
| DELETE | `/api/rag/delete` | 删除知识库文档 | 需登录 + VIP |
| POST | `/api/rag/rename` | 重命名知识库文档 | 需登录 + VIP |
| POST | `/api/rag/search` | 知识库搜索 | 需登录 + VIP |

### 用户个人信息 (`/api/user/profile`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/user/profile` | 获取个人信息 | 需登录 |
| PUT | `/api/user/profile` | 更新个人信息 | 需登录 |
| PUT | `/api/user/profile/password` | 修改密码 | 需登录 |
| POST | `/api/user/profile/bind-email` | 绑定邮箱 | 需登录 |
| DELETE | `/api/user/profile` | 注销账户 | 需登录 |

### 管理接口 (`/api/admin`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/admin/users` | 获取用户列表 | ADMIN |
| GET | `/api/admin/users/{userId}` | 获取用户详情 | ADMIN |
| DELETE | `/api/admin/users/{userId}` | 删除用户 | ADMIN |
| PUT | `/api/admin/users/{userId}/status` | 更新用户状态 | ADMIN |
| PUT | `/api/admin/users/{userId}/role` | 更新用户角色 | ADMIN |
| GET | `/api/admin/knowledge/list` | 知识库文档列表 | ADMIN |
| GET | `/api/admin/knowledge/delete` | 删除知识库文档 | ADMIN |
| GET | `/api/admin/tokens/summary` | Token 用量汇总 | ADMIN |
| GET | `/api/admin/tokens/detail/{userId}` | 用户 Token 详情 | ADMIN |
| GET | `/api/admin/tokens/daily-summary` | 每日 Token 统计 | ADMIN |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis Stack（含向量搜索模块）
- SearXNG（联网搜索需要）

### 本地开发

```bash
# 克隆项目
git clone <repo-url>
cd liuhaizhu-ai-chat-backend

# 配置环境变量
cp src/main/resources/.env-template .env
# 编辑 .env 文件，填入实际配置

# 初始化数据库
# 执行 src/main/resources/sql/liuhaizhu.sql

# 编译运行
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

服务默认运行在 `http://localhost:8000`。

### 环境变量 / 配置项

| 类别 | 变量 / 配置项 | 说明 |
|------|-------------|------|
| 数据库 | `spring.datasource.host` | MySQL 主机地址 |
| | `spring.datasource.port` | MySQL 端口 (3306) |
| | `spring.datasource.database` | 数据库名 (liuhaizhu) |
| | `spring.datasource.username` | 数据库用户名 |
| | `spring.datasource.password` | 数据库密码 |
| Redis | `spring.data.redis.host` | Redis 主机地址 |
| | `spring.data.redis.port` | Redis 端口 (6379) |
| | `spring.data.redis.password` | Redis 密码 |
| AI | `DASHSCOPE_API_KEY` | 阿里云 DashScope API Key |
| 邮件 | `spring.mail.host` | SMTP 服务器地址 |
| | `spring.mail.port` | SMTP 端口 (465) |
| | `spring.mail.username` | 发件邮箱 |
| | `spring.mail.password` | 邮箱密码 / 授权码 |
| OSS | `dromara.x-file-storage.aliyun-oss[0].access-key` | OSS Access Key |
| | `dromara.x-file-storage.aliyun-oss[0].secret-key` | OSS Secret Key |
| | `dromara.x-file-storage.aliyun-oss[0].end-point` | OSS Endpoint |
| | `dromara.x-file-storage.aliyun-oss[0].bucket-name` | OSS Bucket 名称 |
| | `dromara.x-file-storage.aliyun-oss[0].domain` | OSS 访问域名 |
| 站点 | `website.domain` | 前端站点域名（CORS 用） |
| 搜索 | `websearch.searxng.url` | SearXNG 服务地址 |
| MCP | `AMAP_MAPS_API_KEY` | 高德地图 API Key |
| JWT | `jwt.secret` | JWT 签名密钥 |

### 可用的 Maven 命令

```bash
./mvnw spring-boot:run       # 启动开发服务器
./mvnw clean package          # 构建 JAR 包
./mvnw test                   # 运行测试
```

## 构建与部署

### 构建 JAR

```bash
./mvnw clean package -DskipTests
```

构建产物位于 `target/liuhaizhu-ai-chat-backend-0.0.1-SNAPSHOT.jar`。

### Docker 部署

```bash
# 构建镜像
docker build -t liuhaizhu-backend:latest .

# 编辑 docker-compose.yml 中的环境变量，然后启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down
```

容器暴露 8000 端口，JVM 针对 2GB 内存服务器做了优化：
- 初始堆内存：256MB
- 最大堆内存：512MB
- 垃圾收集器：G1GC
- 最大 GC 暂停时间：200ms
- 支持容器感知（`-XX:+UseContainerSupport`）

### Jenkins CI/CD

项目包含 Jenkins Pipeline 配置，自动化流程：

1. **拉取代码**：从 Git 仓库检出
2. **构建 Docker 镜像**：基于 Dockerfile 构建
3. **推送镜像**：支持推送到私有/公共镜像仓库
4. **部署到服务器**：通过 SSH 远程执行 `docker-compose up -d --build`
5. **健康检查**：验证服务状态

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                     Frontend (Vue 3)                      │
└───────────┬─────────────────────────────────────────────┘
            │ HTTP / SSE
            ▼
┌─────────────────────────────────────────────────────────┐
│                  Spring Boot Backend                      │
│  ┌─────────────┐  ┌─────────────┐  ┌────────────────┐   │
│  │  Controller │  │   Security  │  │  AOP Aspects   │   │
│  │    Layer    │  │   (JWT)     │  │  (限流/锁/权限) │   │
│  └──────┬──────┘  └─────────────┘  └────────────────┘   │
│         │                                                 │
│  ┌──────┴──────┐  ┌─────────────┐  ┌────────────────┐   │
│  │   Service   │  │  Spring AI  │  │  MCP Tools     │   │
│  │    Layer    │  │  (Chat/Embed)│  │  (Date/Email/  │   │
│  └──────┬──────┘  └──────┬──────┘  │   Product)     │   │
│         │                │         └────────────────┘   │
│  ┌──────┴──────┐  ┌──────┴──────┐                        │
│  │  MyBatis    │  │  外部服务    │                       │
│  │  Plus Mapper│  │  DashScope  │                        │
│  └──────┬──────┘  │  SearXNG   │                        │
│         │         │  OSS/MinIO │                        │
└─────────┼─────────┴──────┬──────┴────────────────────────┘
          │                │
          ▼                ▼
   ┌──────────┐   ┌──────────────┐
   │  MySQL   │   │  Redis Stack  │
   └──────────┘   │  (缓存+向量)   │
                  └──────────────┘
```

### 认证流程

1. 用户登录 → Spring Security 认证 → 生成 Access Token + Refresh Token
2. Token 通过 `LoginResponse` 返回给前端
3. 后续请求通过 `Authorization: Bearer <token>` 携带
4. `JwtAuthenticationFilter` 在请求到达 Controller 前验证 Token，设置 SecurityContext
5. Access Token 过期后，前端调用 `/api/auth/refresh` 用 Refresh Token 换新
6. SecurityContext 配置 `MODE_INHERITABLETHREADLOCAL` 确保异步线程可获取用户信息

### 聊天流程

1. 前端建立 SSE 连接 → `SSEServerUtil.connect(userId)` 注册 SseEmitter
2. 用户发送消息 → Controller 接收 → Service 调用 ChatClient
3. ChatClient 调用 DashScope API，获取 `Flux<ChatResponse>`
4. 流式推送：每个 token 实时通过 SSE 推送到前端
5. 对话记录存储到 `chat_message` 表，支持历史回溯
6. 三种对话模式：普通对话、RAG 增强、联网搜索增强

### RAG 流程

1. 用户上传文件 → OSS 存储 → 返回文件 URL
2. 异步任务：TikaReader 解析文档 → RecursiveTextSplitter 分片 → DashScope Embedding 向量化 → Redis VectorStore 存储
3. 检索时：用户问题向量化 → Redis 向量相似度搜索 → 返回 Top-K 文档片段 → 注入 LLM 对话上下文

### 限流机制

- 基于 Redis Lua 脚本实现，原子性保证
- 三种算法：令牌桶（默认）、滑动窗口、固定窗口
- 使用 `@RateLimit` 注解声明，SpEL 解析动态 Key
- 聊天接口 5 QPS，RAG 搜索 3 QPS

### 权限控制层级

```
用户请求 → SecurityConfig（URL 级别放行）
         → JwtAuthenticationFilter（Token 验证 + 设置 SecurityContext）
         → PermissionAspect（@RequirePermission 方法级检查）
              ├── allowedRoles：允许角色列表
              ├── excludedRoles：排除角色列表
              ├── admin：是否仅管理员
              ├── vip：是否仅 VIP
              └── permissions：细粒度权限
```

## 开发规范

### 代码风格
- Controller 层：只负责接收请求和返回响应，业务逻辑委托给 Service
- Service 层：核心业务逻辑，接口-实现分离
- Mapper 层：MyBatis Plus BaseMapper，复杂查询通过 XML 映射
- 统一响应体：使用 `AceResult<T>` 封装（code: 200 成功, 500 失败）
- 构造函数注入（`@RequiredArgsConstructor`），避免字段注入

### 命名约定
- **Controller**：`*Controller`，如 `AuthController`
- **Service 接口**：`*Service`，如 `ChatService`
- **Service 实现**：`*ServiceImpl`，如 `ChatServiceImpl`
- **Mapper**：`*Mapper`，如 `UserMapper`
- **Entity**：与表名对应，如 `User`、`ChatMessage`
- **DTO**：`*Request` / `*DTO`，如 `LoginRequest`、`ChatDTO`
- **VO**：`*Response` / `*VO`，如 `LoginResponse`、`UserVO`

### 数据库规范
- 主键策略：ASSIGN_ID（雪花算法）
- 下划线转驼峰：`map-underscore-to-camel-case: true`
- 逻辑删除：`deleted` 字段（0-未删除，1-已删除）
- 时间字段自动填充：`create_time`、`update_time`

## 设计理念

### 项目定位
"刘海柱"AI 智能助手的后端服务，以 Spring AI 为核心框架，整合大模型对话、RAG 知识库检索、联网搜索和 MCP 工具调用四大 AI 能力，为前端提供高性能、可扩展的 API 服务。

### 设计原则
- **安全优先**：Spring Security + JWT 双 Token 认证，细粒度权限控制
- **高可用**：分布式锁、请求限流、异步处理保障服务稳定
- **可观测性**：全局日志记录、慢请求告警、Actuator 健康监控
- **可扩展**：接口-实现分离、注解驱动（AOP）、外部配置驱动

## 默认管理员

系统启动时通过 `DataInitializer` 自动创建默认管理员：

| 字段 | 值 |
|------|-----|
| 用户名 | `admin` |
| 密码 | `123456`（BCrypt 加密存储） |
| 角色 | ADMIN |

## License

[MIT License](LICENSE)

Copyright (c) 2025 aceFelix

本项目基于 MIT 协议开源，任何人可免费学习、使用、修改和分发。