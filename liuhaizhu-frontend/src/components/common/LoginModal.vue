<template>
  <!-- 登录注册弹窗 -->
  <div v-if="showLogin" class="modal-overlay" @click="closeLogin">
    <!-- 空间裂缝 -->
    <div class="space-rift" :class="{ 'closing': isClosing }">
      <div class="rift-glow"></div>
    </div>
    <div class="modal-content login-modal" :class="{ 'closing': isClosing }" @click.stop>
      <div class="modal-header">
        <h2>{{ isLoginMode ? '用户登录' : '用户注册' }}</h2>
        <button @click="closeLogin" class="btn-close">×</button>
      </div>
      <div class="modal-body">
        <div class="login-form">
          <!-- 登录模式：用户名/邮箱 -->
          <div v-if="isLoginMode" class="form-item">
            <label>用户名/邮箱</label>
            <input
              v-model="loginForm.username"
              type="text"
              placeholder="请输入用户名或邮箱"
              class="form-input"
            />
          </div>
          <!-- 注册模式：切换注册方式 -->
          <template v-else>
            <div class="register-tabs">
              <button
                class="tab-btn"
                :class="{ active: registerMode === 'email' }"
                @click="registerMode = 'email'"
              >
                邮箱注册
              </button>
              <button
                class="tab-btn"
                :class="{ active: registerMode === 'username' }"
                @click="registerMode = 'username'"
              >
                用户名注册
              </button>
            </div>
            <!-- 邮箱注册模式 -->
            <template v-if="registerMode === 'email'">
              <div class="form-item">
                <label>邮箱</label>
                <input
                  v-model="loginForm.email"
                  type="email"
                  placeholder="请输入邮箱"
                  class="form-input"
                />
              </div>
              <div class="form-item">
                <label>用户名</label>
                <input
                  v-model="loginForm.username"
                  type="text"
                  placeholder="请输入用户名（可选，默认使用邮箱前缀）"
                  class="form-input"
                />
              </div>
            </template>
            <!-- 用户名注册模式 -->
            <div v-else class="form-item">
              <label>用户名</label>
              <input
                v-model="loginForm.username"
                type="text"
                placeholder="请输入用户名"
                class="form-input"
              />
            </div>
          </template>
          <div class="form-item">
            <label>密码</label>
            <div class="password-input-wrapper">
              <input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入6-20位密码"
                class="form-input"
              />
              <button
                type="button"
                class="btn-eye"
                :class="{ active: showPassword }"
                @click="showPassword = !showPassword"
              >
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path
                    v-if="showPassword"
                    d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
                  />
                  <path
                    v-else
                    d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"
                  />
                </svg>
              </button>
            </div>
          </div>
          <!-- 注册模式：确认密码 -->
          <div v-if="!isLoginMode" class="form-item">
            <label>确认密码</label>
            <div class="password-input-wrapper">
              <input
                v-model="loginForm.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入密码"
                class="form-input"
              />
              <button
                type="button"
                class="btn-eye"
                :class="{ active: showConfirmPassword }"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path
                    v-if="showConfirmPassword"
                    d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
                  />
                  <path
                    v-else
                    d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"
                  />
                </svg>
              </button>
            </div>
          </div>
          <!-- 注册模式：邮箱验证码（仅邮箱注册需要） -->
          <div v-if="!isLoginMode && registerMode === 'email'" class="form-item">
            <label>邮箱验证码</label>
            <div class="code-input-wrapper">
              <input
                v-model="loginForm.emailCode"
                type="text"
                placeholder="请输入邮箱验证码"
                class="form-input"
              />
              <button type="button" class="btn-code" @click="handleSendEmailCode" :disabled="codeCountdown > 0">
                {{ codeCountdown > 0 ? `${codeCountdown}s后重试` : '获取验证码' }}
              </button>
            </div>
          </div>
          <!-- 注册模式：用户协议勾选 -->
          <div v-if="!isLoginMode" class="terms-checkbox">
            <label class="checkbox-label">
              <input type="checkbox" v-model="agreedToTerms" class="checkbox-input" />
              <span class="checkbox-custom"></span>
              <span class="checkbox-text">
                我已阅读并同意
                <a href="javascript:;" @click.prevent="showPrivacyPolicy = true" class="terms-link">《隐私政策》</a>
                和
                <a href="javascript:;" @click.prevent="showTermsOfService = true" class="terms-link">《服务条款》</a>
              </span>
            </label>
          </div>
        </div>
      </div>
      <div class="modal-footer login-footer">
        <button @click="handleAuth" class="btn-confirm" :disabled="isLoading">
          {{ isLoading ? '处理中...' : (isLoginMode ? '登录' : '注册') }}
        </button>
        <div class="switch-mode">
          <span>{{ isLoginMode ? '没有账号？' : '已有账号？' }}</span>
          <a @click="isLoginMode = !isLoginMode">
            {{ isLoginMode ? '立即注册' : '立即登录' }}
          </a>
          <span v-if="isLoginMode" class="divider">|</span>
          <a v-if="isLoginMode" @click="openResetPassword">忘记密码</a>
        </div>
      </div>
    </div>
  </div>

  <!-- 重置密码弹窗 -->
  <div v-if="showReset" class="modal-overlay" @click="closeReset">
    <div class="modal-content login-modal" @click.stop>
      <div class="modal-header">
        <h2>重置密码</h2>
        <button @click="closeReset" class="btn-close">×</button>
      </div>
      <div class="modal-body">
        <div class="login-form">
          <div class="form-item">
            <label>邮箱</label>
            <input
              v-model="resetForm.account"
              type="text"
              placeholder="请输入邮箱"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>新密码</label>
            <div class="password-input-wrapper">
              <input
                v-model="resetForm.newPassword"
                :type="showNewPassword ? 'text' : 'password'"
                placeholder="请输入6-20位新密码"
                class="form-input"
              />
              <button
                type="button"
                class="btn-eye"
                :class="{ active: showNewPassword }"
                @click="showNewPassword = !showNewPassword"
              >
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path
                    v-if="showNewPassword"
                    d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
                  />
                  <path
                    v-else
                    d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"
                  />
                </svg>
              </button>
            </div>
          </div>
          <div class="form-item">
            <label>确认新密码</label>
            <div class="password-input-wrapper">
              <input
                v-model="resetForm.confirmNewPassword"
                :type="showConfirmNewPassword ? 'text' : 'password'"
                placeholder="请再次输入新密码"
                class="form-input"
              />
              <button
                type="button"
                class="btn-eye"
                :class="{ active: showConfirmNewPassword }"
                @click="showConfirmNewPassword = !showConfirmNewPassword"
              >
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path
                    v-if="showConfirmNewPassword"
                    d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
                  />
                  <path
                    v-else
                    d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"
                  />
                </svg>
              </button>
            </div>
          </div>
          <div class="form-item">
            <label>邮箱验证码</label>
            <div class="code-input-wrapper">
              <input
                v-model="resetForm.code"
                type="text"
                placeholder="请输入邮箱验证码"
                class="form-input"
              />
              <button type="button" class="btn-code" @click="handleSendCode">获取验证码</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer login-footer">
        <button @click="handleResetPassword" class="btn-confirm">确认重置</button>
        <div class="switch-mode">
          <a @click="backToLogin">返回登录</a>
        </div>
      </div>
    </div>
  </div>

  <!-- 隐私政策弹框 -->
  <PrivacyPolicyModal v-model:visible="showPrivacyPolicy" />
  <!-- 服务条款弹框 -->
  <TermsOfServiceModal v-model:visible="showTermsOfService" />
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { sendRegisterCode } from '@/api/auth'
import PrivacyPolicyModal from '@/components/footing/PrivacyPolicyModal.vue'
import TermsOfServiceModal from '@/components/footing/TermsOfServiceModal.vue'

// Props
const props = defineProps({
  showLogin: {
    type: Boolean,
    default: false,
  },
  showReset: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['update:showLogin', 'update:showReset', 'login-success'])

const authStore = useAuthStore()

// 状态
const isLoginMode = ref(true)
const registerMode = ref('email') // 'email' | 'username'

// 监听弹窗打开，重置为登录模式
watch(() => props.showLogin, (newVal) => {
  if (newVal) {
    isLoginMode.value = true
    // 禁用滚动吸附，确保弹窗显示在正确位置
    document.documentElement.style.scrollSnapType = 'none'
  } else {
    // 恢复滚动吸附
    document.documentElement.style.scrollSnapType = 'y proximity'
  }
})

// 监听重置密码弹窗
watch(() => props.showReset, (newVal) => {
  if (newVal) {
    document.documentElement.style.scrollSnapType = 'none'
  } else {
    document.documentElement.style.scrollSnapType = 'y proximity'
  }
})
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmNewPassword = ref(false)
const isLoading = ref(false)
const agreedToTerms = ref(false)
const showPrivacyPolicy = ref(false)
const showTermsOfService = ref(false)

const loginForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  emailCode: '',
})

const codeCountdown = ref(0)
let countdownTimer = null

const resetForm = ref({
  account: '',
  code: '',
  newPassword: '',
  confirmNewPassword: '',
})

// 关闭动画状态
const isClosing = ref(false)

// 方法
const closeLogin = () => {
  isClosing.value = true
  setTimeout(() => {
    isClosing.value = false
    emit('update:showLogin', false)
  }, 400)
}

const closeReset = () => {
  emit('update:showReset', false)
}

const openResetPassword = () => {
  emit('update:showLogin', false)
  emit('update:showReset', true)
}

const backToLogin = () => {
  emit('update:showReset', false)
  emit('update:showLogin', true)
}

const handleAuth = async () => {
  if (isLoginMode.value) {
    // 登录模式
    if (!loginForm.value.username || !loginForm.value.password) {
      ElMessage.warning('请填写用户名和密码')
      return
    }
  } else {
    // 注册模式 - 检查是否勾选协议
    if (!agreedToTerms.value) {
      ElMessage.warning('请先阅读并同意隐私政策和服务条款')
      return
    }
      // 注册模式
      if (registerMode.value === 'email') {
        // 邮箱注册验证
        if (!loginForm.value.email || !loginForm.value.password || !loginForm.value.emailCode) {
          ElMessage.warning('请填写邮箱、密码和验证码')
          return
        }
        // 邮箱格式验证
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(loginForm.value.email)) {
          ElMessage.warning('请输入有效的邮箱地址')
          return
        }
        // 如果用户填写了用户名，验证格式
        if (loginForm.value.username?.trim()) {
          const usernameRegex = /^[\u4e00-\u9fa5a-zA-Z0-9_]{3,20}$/
          if (!usernameRegex.test(loginForm.value.username.trim())) {
            ElMessage.warning('用户名需为3-20位汉字、字母、数字或下划线')
            return
          }
        }
      } else {
      // 用户名注册验证
      if (!loginForm.value.username || !loginForm.value.password) {
        ElMessage.warning('请填写用户名和密码')
        return
      }
      // 用户名格式验证（汉字、字母、数字、下划线，3-20位）
      const usernameRegex = /^[\u4e00-\u9fa5a-zA-Z0-9_]{3,20}$/
      if (!usernameRegex.test(loginForm.value.username)) {
        ElMessage.warning('用户名需为3-20位汉字、字母、数字或下划线')
        return
      }
    }
    if (loginForm.value.password !== loginForm.value.confirmPassword) {
      ElMessage.warning('两次密码输入不一致')
      return
    }
  }

  isLoading.value = true
  try {
    let result
    if (isLoginMode.value) {
      result = await authStore.handleLogin({
        username: loginForm.value.username,
        password: loginForm.value.password,
      })
    } else {
      // 根据注册方式构建注册参数
      const registerParams = {
        password: loginForm.value.password,
        registerMode: registerMode.value,
      }

      if (registerMode.value === 'email') {
        // 邮箱注册：如果用户填写了用户名则使用，否则使用邮箱前缀
        let username = loginForm.value.username?.trim()
        if (!username) {
          // 使用邮箱前缀作为默认用户名
          username = loginForm.value.email.split('@')[0]
        }
        registerParams.username = username
        registerParams.email = loginForm.value.email
        registerParams.code = loginForm.value.emailCode
      } else {
        // 用户名注册：仅用户名和密码
        registerParams.username = loginForm.value.username
      }

      result = await authStore.handleRegister(registerParams)
    }

    if (result.success) {
      ElMessage.success(isLoginMode.value ? '登录成功' : '注册成功')
      emit('update:showLogin', false)
      emit('login-success', result.data)
      loginForm.value = { username: '', email: '', password: '', confirmPassword: '', emailCode: '' }
    } else {
      ElMessage.error(result.message || (isLoginMode.value ? '登录失败' : '注册失败'))
    }
  } catch (error) {
    console.error(isLoginMode.value ? '登录失败:' : '注册失败:', error)
    ElMessage.error(isLoginMode.value ? '登录失败，请稍后重试' : '注册失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const handleSendCode = () => {
  if (!resetForm.value.account) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  ElMessage.warning('重置密码功能暂未实现，请联系管理员重置密码')
}

const handleSendEmailCode = async () => {
  if (!loginForm.value.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  // 简单的邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(loginForm.value.email)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }

  try {
    const response = await sendRegisterCode(loginForm.value.email)
    if (response.data.code === 200) {
      // 开始倒计时
      codeCountdown.value = 60
      countdownTimer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) {
          clearInterval(countdownTimer)
        }
      }, 1000)
      ElMessage.success('验证码已发送到您的邮箱')
    } else {
      ElMessage.error(response.data.message || '发送验证码失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.response?.data?.message || '发送验证码失败')
  }
}

const handleResetPassword = () => {
  if (!resetForm.value.account || !resetForm.value.code || !resetForm.value.newPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (resetForm.value.newPassword !== resetForm.value.confirmNewPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  ElMessage.warning('重置密码功能暂未实现，请联系管理员重置密码')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.modal-content {
  background: var(--bg-tertiary);
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-color);
  position: relative;
}

/* 空间裂缝容器 */
.space-rift {
  position: fixed;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 0;
  background: linear-gradient(
    180deg,
    transparent 0%,
    rgba(139, 92, 246, 0.8) 20%,
    rgba(59, 130, 246, 1) 50%,
    rgba(139, 92, 246, 0.8) 80%,
    transparent 100%
  );
  box-shadow:
    0 0 20px rgba(139, 92, 246, 0.8),
    0 0 40px rgba(59, 130, 246, 0.6),
    0 0 60px rgba(139, 92, 246, 0.4);
  border-radius: 5px;
  animation: riftOpen 0.4s ease forwards;
  z-index: 1001;
}

.space-rift.closing {
  animation: riftClose 0.4s ease forwards;
}

@keyframes riftOpen {
  0% {
    height: 0;
    opacity: 0;
  }
  50% {
    height: 200px;
    opacity: 1;
  }
  100% {
    height: 400px;
    opacity: 1;
  }
}

@keyframes riftClose {
  0% {
    height: 400px;
    opacity: 1;
  }
  50% {
    height: 200px;
    opacity: 1;
  }
  100% {
    height: 0;
    opacity: 0;
  }
}

/* 裂缝内部光效 */
.rift-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  background: radial-gradient(
    ellipse at center,
    rgba(255, 255, 255, 0.8) 0%,
    rgba(139, 92, 246, 0.5) 30%,
    transparent 70%
  );
  animation: riftPulse 0.3s ease infinite alternate;
}

@keyframes riftPulse {
  from {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

/* 弹框从裂缝滑入 */
.modal-content {
  animation: slideFromRift 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  transform-origin: right center;
}

.modal-content.closing {
  animation: slideToRift 0.4s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes slideFromRift {
  0% {
    transform: translateX(100%) scaleX(0.1) skewY(-10deg);
    opacity: 0;
    filter: blur(10px);
  }
  60% {
    transform: translateX(-10%) scaleX(1.05) skewY(2deg);
    opacity: 1;
    filter: blur(2px);
  }
  100% {
    transform: translateX(0) scaleX(1) skewY(0deg);
    opacity: 1;
    filter: blur(0);
  }
}

@keyframes slideToRift {
  0% {
    transform: translateX(0) scaleX(1) skewY(0deg);
    opacity: 1;
    filter: blur(0);
  }
  40% {
    transform: translateX(-5%) scaleX(1.02) skewY(1deg);
    opacity: 1;
  }
  100% {
    transform: translateX(100%) scaleX(0.1) skewY(-10deg);
    opacity: 0;
    filter: blur(10px);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.btn-close {
  background: none;
  border: none;
  font-size: 28px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.modal-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  background: var(--bg-tertiary);
}

.btn-confirm {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

/* 登录弹窗样式 */
.login-modal {
  max-width: 400px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 注册方式切换标签 */
.register-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.tab-btn {
  flex: 1;
  padding: 10px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.tab-btn.active {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
  color: white;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-input {
  padding: 12px 16px;
  background: var(--input-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-primary);
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.form-input::placeholder {
  color: var(--text-tertiary);
}

.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input-wrapper .form-input {
  width: 100%;
  padding-right: 44px;
}

.btn-eye {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  color: var(--text-tertiary);
  transition: color 0.2s;
}

.btn-eye:hover {
  color: var(--text-secondary);
}

.btn-eye.active {
  color: var(--color-primary);
}

.login-footer {
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.login-footer .btn-confirm {
  width: 100%;
}

.switch-mode {
  font-size: 14px;
  color: var(--text-secondary);
}

.switch-mode a {
  color: var(--color-primary);
  cursor: pointer;
  margin-left: 4px;
}

.switch-mode a:hover {
  text-decoration: underline;
}

.switch-mode .divider {
  margin: 0 8px;
  color: var(--text-tertiary);
}

.code-input-wrapper {
  display: flex;
  gap: 12px;
}

.code-input-wrapper .form-input {
  flex: 1;
}

.btn-code {
  padding: 0 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
  transition: all 0.3s;
}

.btn-code:hover {
  opacity: 0.9;
}

.btn-code:disabled {
  background: #6b7280;
  cursor: not-allowed;
  opacity: 0.7;
}

.terms-checkbox {
  margin-top: 16px;
  padding: 12px 0;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.checkbox-input {
  display: none;
}

.checkbox-custom {
  width: 18px;
  height: 18px;
  min-width: 18px;
  border: 2px solid var(--border-color);
  border-radius: 4px;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  margin-top: 2px;
}

.checkbox-custom::after {
  content: '';
  width: 10px;
  height: 6px;
  border: 2px solid white;
  border-top: none;
  border-right: none;
  transform: rotate(-45deg) scale(0);
  transition: transform 0.2s ease;
}

.checkbox-input:checked + .checkbox-custom {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  border-color: #8b5cf6;
}

.checkbox-input:checked + .checkbox-custom::after {
  transform: rotate(-45deg) scale(1);
}

.checkbox-text {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.terms-link {
  color: #8b5cf6;
  text-decoration: none;
  transition: color 0.2s ease;
}

.terms-link:hover {
  color: #a78bfa;
  text-decoration: underline;
}
</style>
