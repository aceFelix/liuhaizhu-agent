CREATE DATABASE liuhaizhu_ai_chat_backend;
USE liuhaizhu_ai_chat_backend;
-- 商品表
CREATE TABLE IF NOT EXISTS product
(
    product_id   VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '商品编号',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    brand        VARCHAR(128) NOT NULL COMMENT '商品品牌',
    price        INT          NOT NULL COMMENT '商品价格',
    stock        INT          NOT NULL COMMENT '商品库存',
    description  VARCHAR(500) NULL COMMENT '商品简介',
    status       VARCHAR(64)  NOT NULL COMMENT '商品状态（0-下架，1-上架，2-预售）',
    create_time  DATETIME     NOT NULL COMMENT '创建时间',
    update_time  DATETIME     NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 会话表
CREATE TABLE IF NOT EXISTS `conversation` (
  `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '会话ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `title` VARCHAR(255) NOT NULL DEFAULT '新对话' COMMENT '会话标题',
  `title_source` VARCHAR(16) NOT NULL DEFAULT 'AUTO' COMMENT '标题来源：AUTO-AI生成, MANUAL-手动修改',
  `title_generated_at` INT NOT NULL DEFAULT 0 COMMENT '标题生成时的对话轮数',
  `type` VARCHAR(32) NOT NULL DEFAULT 'normal' COMMENT '会话类型：normal-普通对话, rag-知识库对话, web_search-联网搜索',
  `pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-不置顶, 1-置顶',
  `pinned_time` DATETIME DEFAULT NULL COMMENT '置顶时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_update_time` (`update_time`),
  INDEX `idx_pinned_update` (`pinned` DESC, `update_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- 已有表添加字段（增量更新用）
-- ALTER TABLE conversation ADD COLUMN `title_source` VARCHAR(16) NOT NULL DEFAULT 'AUTO' COMMENT '标题来源：AUTO-AI生成, MANUAL-手动修改';
-- ALTER TABLE conversation ADD COLUMN `title_generated_at` INT NOT NULL DEFAULT 0 COMMENT '标题生成时的对话轮数';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '消息ID',
  `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `role` VARCHAR(16) NOT NULL COMMENT '消息角色：user-用户, assistant-助手',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `message_id` VARCHAR(64) COMMENT '消息ID（前端生成的唯一标识）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `token_count` INT DEFAULT 0 COMMENT 'Token消耗',
  INDEX `idx_conversation_id` (`conversation_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（可为空，用户名注册时可选填）',
  `avatar` VARCHAR(255) DEFAULT '/images/user-avatar.jpg' COMMENT '头像路径',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色：ADMIN-管理员, VIP-VIP用户, USER-普通用户',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用, 1-正常, 2-锁定',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认管理员用户（生产环境务必修改默认密码）
INSERT INTO `user` (`user_id`, `username`, `password`, `email`, `avatar`, `role`, `status`)
VALUES ('1', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', '/images/user-avatar.jpg', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 文件记录表（用于 x-file-storage）
CREATE TABLE IF NOT EXISTS `file_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '文件记录ID',
  `url` VARCHAR(500) NOT NULL COMMENT '文件访问地址',
  `size` BIGINT DEFAULT 0 COMMENT '文件大小，单位字节',
  `filename` VARCHAR(255) DEFAULT NULL COMMENT '文件名称',
  `original_filename` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `base_path` VARCHAR(255) DEFAULT NULL COMMENT '基础存储路径',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '存储路径',
  `ext` VARCHAR(32) DEFAULT NULL COMMENT '文件扩展名',
  `content_type` VARCHAR(128) DEFAULT NULL COMMENT 'MIME类型',
  `platform` VARCHAR(32) DEFAULT NULL COMMENT '存储平台',
  `th_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图访问路径',
  `th_filename` VARCHAR(255) DEFAULT NULL COMMENT '缩略图名称',
  `th_size` BIGINT DEFAULT NULL COMMENT '缩略图大小，单位字节',
  `th_content_type` VARCHAR(128) DEFAULT NULL COMMENT '缩略图MIME类型',
  `object_id` VARCHAR(64) DEFAULT NULL COMMENT '文件所属对象id',
  `object_type` VARCHAR(32) DEFAULT NULL COMMENT '文件所属对象类型',
  `attr` TEXT DEFAULT NULL COMMENT '附加属性',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_url` (`url`),
  INDEX `idx_platform` (`platform`),
  INDEX `idx_object_id` (`object_id`),
  INDEX `idx_object_type` (`object_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表（x-file-storage）';

