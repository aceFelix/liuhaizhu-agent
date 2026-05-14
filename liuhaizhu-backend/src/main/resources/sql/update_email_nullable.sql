-- 修改用户表 email 字段允许为空，支持用户名注册时不填写邮箱

-- 1. 修改 email 字段允许为空
ALTER TABLE `user` MODIFY COLUMN `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（可为空，用户名注册时可选填）';

-- 2. 删除原有的唯一索引（如果存在）
-- 注意：MySQL 中 NULL 值在唯一索引中可以有多个，所以保留唯一索引也是可以的
-- 但如果业务需要允许多个用户都没有邮箱，则需要删除唯一约束
-- 这里我们保留唯一索引，因为业务逻辑上绑定了邮箱的用户邮箱必须唯一

-- 3. 重新创建索引（普通索引，非唯一）
-- ALTER TABLE `user` DROP INDEX `idx_email`;
-- ALTER TABLE `user` ADD INDEX `idx_email` (`email`);

-- 说明：
-- - 用户名注册时 email 为 NULL
-- - 用户后续可以在个人信息页面绑定邮箱
-- - 绑定邮箱时需要验证邮箱唯一性（通过代码逻辑控制）
