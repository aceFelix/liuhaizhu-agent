<template>
  <div v-if="visible" class="modal-overlay" :class="{ 'light-theme': settingsStore.theme === 'light' }" @click="handleClose">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>编辑个人信息</h3>
        <button class="btn-close" @click="handleClose">×</button>
      </div>

      <div class="modal-body">
        <!-- 头像选择 -->
        <div class="avatar-section">
          <div class="current-avatar">
            <img :src="form.avatar || '/images/user-avatar.jpg'" alt="Avatar" />
          </div>
          <p class="section-title">选择头像</p>
          <div class="avatar-grid">
            <div
              v-for="avatar in avatarList"
              :key="avatar"
              class="avatar-option"
              :class="{ active: form.avatar === avatar }"
              @click="selectAvatar(avatar)"
            >
              <img :src="avatar" alt="avatar" />
            </div>
          </div>
        </div>

        <!-- 基本信息表单 -->
        <div class="form-section">
          <div class="form-group">
            <label>用户名</label>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              maxlength="20"
            />
          </div>

          <!-- 邮箱绑定区域 -->
          <div class="form-group email-group">
            <label>
              邮箱
              <span v-if="user?.email" class="email-status bound">已绑定</span>
              <span v-else class="email-status unbound">未绑定</span>
            </label>
            <div v-if="!user?.email && !showBindEmailForm" class="bind-email-hint">
              <p class="hint-text">绑定邮箱后可升级为VIP用户</p>
              <button class="btn-bind-email" @click="showBindEmailForm = true">
                绑定邮箱
              </button>
            </div>
            <div v-else-if="showBindEmailForm" class="bind-email-form">
              <input
                v-model="bindEmailForm.email"
                type="email"
                placeholder="请输入邮箱"
              />
              <div class="verification-row">
                <input
                  v-model="bindEmailForm.code"
                  type="text"
                  placeholder="请输入验证码"
                  maxlength="6"
                />
                <button
                  class="btn-send-code"
                  :disabled="isSendingBindCode || bindCountdown > 0"
                  @click="handleSendBindEmailCode"
                >
                  {{ bindCountdown > 0 ? `${bindCountdown}s` : '获取验证码' }}
                </button>
              </div>
              <div class="bind-actions">
                <button class="btn-cancel-bind" @click="showBindEmailForm = false">取消</button>
                <button class="btn-confirm-bind" @click="handleBindEmail">确认绑定</button>
              </div>
            </div>
            <input
              v-else
              v-model="form.email"
              type="email"
              placeholder="请输入邮箱"
              disabled
            />
          </div>
        </div>

        <!-- 修改密码 -->
        <div class="password-section">
          <div class="section-header" @click="showPasswordForm = !showPasswordForm">
            <span class="section-title">修改密码</span>
            <span class="toggle-icon">{{ showPasswordForm ? '▼' : '▶' }}</span>
          </div>

          <div v-if="showPasswordForm" class="password-form">
            <div class="form-group">
              <label>当前密码</label>
              <input
                v-model="passwordForm.currentPassword"
                type="password"
                placeholder="请输入当前密码"
              />
            </div>

            <div class="form-group">
              <label>新密码</label>
              <input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少6位）"
              />
            </div>

            <div class="form-group">
              <label>确认新密码</label>
              <input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
              />
            </div>

            <div class="form-group verification-group">
              <label>邮箱验证码</label>
              <div class="verification-input">
                <input
                  v-model="passwordForm.verificationCode"
                  type="text"
                  placeholder="请输入验证码"
                  maxlength="6"
                />
                <button
                  class="btn-send-code"
                  :disabled="isSendingCode || countdown > 0"
                  @click="sendVerificationCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-save" :disabled="isSaving" @click="handleSave">
          <span v-if="isSaving">保存中...</span>
          <span v-else>保存</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { updateUserProfile, updatePassword, sendEmailVerificationCode, sendBindEmailCode, bindEmail } from '@/api/user'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  user: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:visible', 'success'])

const authStore = useAuthStore()
const settingsStore = useSettingsStore()

// 头像列表
const avatarList = [
  '/avatar/boy1.svg',
  '/avatar/boy2.svg',
  '/avatar/boy3.svg',
  '/avatar/boy4.svg',
  '/avatar/girl1.svg',
  '/avatar/girl2.svg',
  '/avatar/girl3.svg',
  '/avatar/girl4.svg',
  '/avatar/girl5.svg',
  '/avatar/刘海柱1.jpg',
  '/avatar/刘海柱2.jpg',
  '/avatar/刘海柱3.jpg',
  '/avatar/刘海柱4.jpg',
  '/avatar/神鹰1.png',
  '/avatar/神鹰2.png',
  '/avatar/神鹰3.jpg',
]

// 表单数据
const form = reactive({
  username: '',
  email: '',
  avatar: '',
})

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
  verificationCode: '',
})

const showPasswordForm = ref(false)
const isSaving = ref(false)
const isSendingCode = ref(false)
const countdown = ref(0)

// 邮箱绑定相关
const showBindEmailForm = ref(false)
const isSendingBindCode = ref(false)
const bindCountdown = ref(0)
const bindEmailForm = reactive({
  email: '',
  code: '',
})

// 监听用户数据变化
watch(() => props.user, (newUser) => {
  if (newUser) {
    form.username = newUser.username || ''
    form.email = newUser.email || ''
    form.avatar = newUser.avatar || ''
  }
}, { immediate: true })

// 监听弹窗显示
watch(() => props.visible, (newVal) => {
  if (newVal) {
    // 重置密码表单
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordForm.verificationCode = ''
    showPasswordForm.value = false
    // 禁用滚动吸附，确保弹窗显示在正确位置
    document.documentElement.style.scrollSnapType = 'none'
  } else {
    // 恢复滚动吸附
    document.documentElement.style.scrollSnapType = 'y proximity'
  }
})

// 选择头像
const selectAvatar = (avatar) => {
  form.avatar = avatar
}

// 发送验证码（发送到当前用户邮箱，用于修改密码验证）
const sendVerificationCode = async () => {
  // 使用原始邮箱发送验证码，而不是表单中可能修改后的邮箱
  const currentEmail = props.user?.email
  if (!currentEmail) {
    ElMessage.warning('用户邮箱信息缺失')
    return
  }

  isSendingCode.value = true
  try {
    const response = await sendEmailVerificationCode(currentEmail, 'password_reset')
    if (response.data.code === 200) {
      ElMessage.success('验证码已发送到当前邮箱')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败')
  } finally {
    isSendingCode.value = false
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 发送绑定邮箱验证码
const handleSendBindEmailCode = async () => {
  if (!bindEmailForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  // 邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(bindEmailForm.email)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }

  isSendingBindCode.value = true
  try {
    const response = await sendBindEmailCode({ email: bindEmailForm.email })
    if (response.data.code === 200) {
      ElMessage.success('验证码已发送到邮箱')
      bindCountdown.value = 60
      const timer = setInterval(() => {
        bindCountdown.value--
        if (bindCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.response?.data?.message || '发送验证码失败')
  } finally {
    isSendingBindCode.value = false
  }
}

// 确认绑定邮箱
const handleBindEmail = async () => {
  if (!bindEmailForm.email || !bindEmailForm.code) {
    ElMessage.warning('请填写邮箱和验证码')
    return
  }

  try {
    const response = await bindEmail({
      email: bindEmailForm.email,
      code: bindEmailForm.code,
    })

    if (response.data.code === 200) {
      ElMessage.success('邮箱绑定成功')
      form.email = bindEmailForm.email
      showBindEmailForm.value = false
      bindEmailForm.email = ''
      bindEmailForm.code = ''
      emit('success')
    } else {
      ElMessage.error(response.data.message || '绑定失败')
    }
  } catch (error) {
    console.error('绑定邮箱失败:', error)
    ElMessage.error(error.response?.data?.message || '绑定邮箱失败')
  }
}

// 保存
const handleSave = async () => {
  // 验证基本信息
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (!form.email.trim()) {
    ElMessage.warning('请输入邮箱')
    return
  }

  isSaving.value = true

  try {
    // 1. 保存基本信息
    const profileResponse = await updateUserProfile({
      username: form.username.trim(),
      email: form.email.trim(),
      avatar: form.avatar,
    })

    if (profileResponse.data.code !== 200) {
      throw new Error(profileResponse.data.message || '保存失败')
    }

    // 2. 如果填写了密码相关字段，则修改密码
    if (showPasswordForm.value && (passwordForm.currentPassword || passwordForm.newPassword)) {
      // 验证密码表单
      if (!passwordForm.currentPassword) {
        throw new Error('请输入当前密码')
      }
      if (!passwordForm.newPassword || passwordForm.newPassword.length < 6) {
        throw new Error('新密码至少6位')
      }
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        throw new Error('两次输入的新密码不一致')
      }
      if (!passwordForm.verificationCode) {
        throw new Error('请输入邮箱验证码')
      }

      const passwordResponse = await updatePassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
        verificationCode: passwordForm.verificationCode,
        email: props.user?.email, // 传递当前邮箱用于验证码验证
      })

      if (passwordResponse.data.code !== 200) {
        throw new Error(passwordResponse.data.message || '密码修改失败')
      }

      ElMessage.success('个人信息和密码已更新')
    } else {
      ElMessage.success('个人信息已更新')
    }

    // 更新本地用户信息（直接使用返回的数据，避免重新获取导致token失效）
    if (profileResponse.data.data) {
      authStore.user = profileResponse.data.data
    }

    emit('success')
    handleClose()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    isSaving.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: linear-gradient(145deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  width: 90%;
  max-width: 480px;
  max-height: 85vh;
  overflow-y: auto;
  border: 1px solid rgba(139, 92, 246, 0.3);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.2);
}

.modal-header h3 {
  margin: 0;
  color: #e0e0e0;
  font-size: 18px;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: rgba(139, 92, 246, 0.2);
  color: #e0e0e0;
}

.modal-body {
  padding: 24px;
}

/* 头像区域 */
.avatar-section {
  text-align: center;
  margin-bottom: 24px;
}

.current-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(139, 92, 246, 0.5);
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.3);
}

.current-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.section-title {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 12px;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
}

.avatar-option {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  position: relative;
}

.avatar-option:hover {
  transform: scale(1.1);
  border-color: rgba(139, 92, 246, 0.5);
}

.avatar-option.active {
  border-color: #8b5cf6;
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.5);
}

.avatar-option img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 表单区域 */
.form-section {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 6px;
}

/* 邮箱绑定区域 */
.email-group label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.email-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.email-status.bound {
  background: rgba(34, 197, 94, 0.2);
  color: #22c55e;
}

.email-status.unbound {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.bind-email-hint {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
}

.hint-text {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
}

.btn-bind-email {
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-bind-email:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.bind-email-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px;
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
}

.verification-row {
  display: flex;
  gap: 10px;
}

.verification-row input {
  flex: 1;
}

.bind-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-cancel-bind {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 6px;
  color: #94a3b8;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel-bind:hover {
  border-color: rgba(139, 92, 246, 0.6);
  color: #e0e0e0;
}

.btn-confirm-bind {
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-confirm-bind:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.form-group input {
  width: 100%;
  padding: 10px 14px;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.form-group input::placeholder {
  color: #64748b;
}

/* 密码区域 */
.password-section {
  border-top: 1px solid rgba(139, 92, 246, 0.2);
  padding-top: 16px;
}

.password-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
}

.password-section .section-title {
  margin: 0;
  color: #e0e0e0;
  font-weight: 500;
}

.toggle-icon {
  color: #94a3b8;
  font-size: 12px;
}

.password-form {
  padding-top: 16px;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.verification-group {
  margin-top: 8px;
}

.verification-input {
  display: flex;
  gap: 10px;
}

.verification-input input {
  flex: 1;
}

.btn-send-code {
  padding: 10px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-send-code:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.btn-send-code:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 底部按钮 */
.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid rgba(139, 92, 246, 0.2);
}

.modal-footer button {
  flex: 1;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.3);
  color: #94a3b8;
}

.btn-cancel:hover {
  background: rgba(148, 163, 184, 0.2);
}

.btn-save {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  color: white;
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.4);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 滚动条样式 */
.avatar-grid::-webkit-scrollbar {
  width: 6px;
}

.avatar-grid::-webkit-scrollbar-track {
  background: rgba(15, 23, 42, 0.3);
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.4);
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 92, 246, 0.6);
}

/* 浅色主题样式 */
.modal-overlay.light-theme .modal-content {
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
}

.modal-overlay.light-theme .modal-header {
  border-bottom: 1px solid rgba(139, 92, 246, 0.15);
}

.modal-overlay.light-theme .modal-header h3 {
  color: #1e293b;
}

.modal-overlay.light-theme .btn-close {
  color: #64748b;
}

.modal-overlay.light-theme .btn-close:hover {
  background: rgba(139, 92, 246, 0.1);
  color: #1e293b;
}

.modal-overlay.light-theme .section-title {
  color: #64748b;
}

.modal-overlay.light-theme .form-group label {
  color: #475569;
}

.modal-overlay.light-theme .form-group input {
  background: #ffffff;
  border: 1px solid rgba(139, 92, 246, 0.2);
  color: #1e293b;
}

.modal-overlay.light-theme .form-group input:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.modal-overlay.light-theme .form-group input::placeholder {
  color: #94a3b8;
}

.modal-overlay.light-theme .password-section {
  border-top: 1px solid rgba(139, 92, 246, 0.15);
}

.modal-overlay.light-theme .section-header:hover {
  background: rgba(139, 92, 246, 0.05);
}

.modal-overlay.light-theme .modal-footer {
  border-top: 1px solid rgba(139, 92, 246, 0.15);
}

.modal-overlay.light-theme .btn-cancel {
  background: #f1f5f9;
  border: 1px solid #cbd5e1;
  color: #64748b;
}

.modal-overlay.light-theme .btn-cancel:hover {
  background: #e2e8f0;
  color: #475569;
}

.modal-overlay.light-theme .avatar-grid::-webkit-scrollbar-track {
  background: rgba(241, 245, 249, 0.5);
}
</style>
