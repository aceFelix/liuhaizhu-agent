<template>
  <div
    v-if="user"
    class="user-info-wrapper"
    @mouseenter="isHover = true"
    @mouseleave="isHover = false"
  >
    <img
      :src="user.avatar || '/images/user-avatar.jpg'"
      alt="Avatar"
      class="user-avatar-trigger"
      :class="{ 'clickable': showEditHint }"
      @click="handleAvatarClick"
      :title="showEditHint ? '点击编辑个人信息' : ''"
    />
    <!-- 悬浮用户信息卡片 -->
    <div v-if="isHover" class="user-info-card">
      <div class="user-info-header">
        <img
          :src="user.avatar || '/images/user-avatar.jpg'"
          alt="Avatar"
          class="user-info-avatar"
        />
        <div class="user-info-main">
          <div class="user-info-name">{{ user.username }}</div>
          <div class="user-info-email">{{ user.email }}</div>
        </div>
        <span class="user-info-role">{{ user.role }}</span>
      </div>
      <div class="user-info-meta">
        <div class="meta-item">
          <span class="meta-label">注册时间</span>
          <span class="meta-value">{{ formatDate(user.createTime) }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">账号状态</span>
          <span class="meta-value" :class="{ active: user.status === 1 }">
            {{ user.status === 1 ? '正常' : '已禁用' }}
          </span>
        </div>
      </div>
      <div class="user-info-actions">
        <button class="user-info-logout" @click.stop="handleLogout">
          <span>退出登录</span>
        </button>
        <button class="user-info-delete" @click.stop="handleDeleteAccount">
          <span>账户注销</span>
        </button>
      </div>
    </div>
  </div>
  <button v-else @click="handleLoginClick" class="btn-login">
    <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
      <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
    </svg>
    <span>登录</span>
  </button>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAccount } from '@/api/auth'

const props = defineProps({
  user: {
    type: Object,
    default: null
  },
  showEditHint: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['logout', 'delete-account', 'edit-profile', 'login'])

const isHover = ref(false)

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleAvatarClick = () => {
  if (props.showEditHint) {
    emit('edit-profile')
  }
}

const handleLogout = () => {
  isHover.value = false
  emit('logout')
}

const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要注销账户吗？此操作不可恢复，您的所有数据将被永久删除！',
      '账户注销确认',
      {
        confirmButtonText: '确定注销',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteAccount()
    if (response.data.code === 200) {
      isHover.value = false
      emit('delete-account')
    } else {
      ElMessage.error(response.data.message || '注销失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('注销账户失败:', error)
      ElMessage.error('注销失败，请稍后重试')
    }
  }
}

const handleLoginClick = () => {
  emit('login')
}
</script>

<style scoped>
.user-info-wrapper {
  position: relative;
}

.user-avatar-trigger {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(139, 92, 246, 0.3);
  transition: all 0.3s;
}

.user-avatar-trigger.clickable {
  cursor: pointer;
}

.user-avatar-trigger:hover {
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.3);
}

/* 用户信息卡片 */
.user-info-card {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  background: linear-gradient(135deg, rgba(30, 30, 45, 0.95) 0%, rgba(25, 25, 40, 0.98) 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
  z-index: 100;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-info-card::before {
  content: '';
  position: absolute;
  top: -6px;
  right: 12px;
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, rgba(30, 30, 45, 0.95) 0%, rgba(25, 25, 40, 0.98) 100%);
  border-left: 1px solid rgba(139, 92, 246, 0.2);
  border-top: 1px solid rgba(139, 92, 246, 0.2);
  transform: rotate(45deg);
}

.user-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.user-info-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(139, 92, 246, 0.3);
}

.user-info-main {
  flex: 1;
  min-width: 0;
}

.user-info-name {
  font-size: 15px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info-email {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info-role {
  padding: 2px 8px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 999px;
  font-size: 10px;
  font-weight: 600;
  color: #fff;
  text-transform: uppercase;
}

.user-info-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-label {
  font-size: 12px;
  color: #64748b;
}

.meta-value {
  font-size: 12px;
  color: #e5e7eb;
}

.meta-value.active {
  color: #4ade80;
}

.user-info-actions {
  display: flex;
  gap: 8px;
}

.user-info-logout,
.user-info-delete {
  flex: 1;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.18s ease-out;
  border: none;
}

.user-info-logout {
  background: radial-gradient(circle at top, rgba(248, 113, 113, 0.35), rgba(127, 29, 29, 0.9));
  color: #fee2e2;
  border: 1px solid rgba(248, 113, 113, 0.7);
}

.user-info-logout:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(248, 113, 113, 0.4);
}

.user-info-delete {
  background: radial-gradient(circle at top, rgba(239, 68, 68, 0.35), rgba(153, 27, 27, 0.9));
  color: #fee2e2;
  border: 1px solid rgba(239, 68, 68, 0.7);
}

.user-info-delete:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(239, 68, 68, 0.4);
}

/* 登录按钮 */
.btn-login {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.15);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  color: #a78bfa;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-login:hover {
  background: rgba(139, 92, 246, 0.25);
  border-color: rgba(139, 92, 246, 0.5);
}

/* 浅色主题适配 */
:global(.light-theme) .user-info-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
  border-color: rgba(139, 92, 246, 0.15);
}

:global(.light-theme) .user-info-card::before {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
  border-left-color: rgba(139, 92, 246, 0.15);
  border-top-color: rgba(139, 92, 246, 0.15);
}

:global(.light-theme) .user-info-name {
  color: #1e293b;
}

:global(.light-theme) .user-info-email {
  color: #64748b;
}

:global(.light-theme) .meta-label {
  color: #64748b;
}

:global(.light-theme) .meta-value {
  color: #334155;
}

:global(.light-theme) .btn-login {
  background: rgba(124, 58, 237, 0.1);
  color: var(--color-primary);
}

:global(.light-theme) .btn-login:hover {
  background: rgba(124, 58, 237, 0.2);
}

/* 移动端适配 */
@media (max-width: 480px) {
  .btn-login {
    padding: 6px 10px;
    font-size: 12px;
    gap: 4px;
  }

  .btn-login svg {
    width: 14px;
    height: 14px;
  }

  .user-avatar-trigger {
    width: 32px;
    height: 32px;
  }
}
</style>
