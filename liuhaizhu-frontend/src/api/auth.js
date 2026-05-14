import { request } from './config'

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export const login = (data) => {
  return request.post('/api/auth/login', data)
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱（可选）
 * @param {string} data.registerMode - 注册方式：'email' | 'username'
 * @param {string} data.code - 邮箱验证码（邮箱注册时必填）
 * @returns {Promise}
 */
export const register = (data) => {
  return request.post('/api/auth/register', data)
}

/**
 * 发送注册验证码
 * @param {string} email - 邮箱
 * @returns {Promise}
 */
export const sendRegisterCode = (email) => {
  return request.post('/api/auth/send-register-code', { email })
}

/**
 * 刷新Token
 * @param {string} refreshToken - 刷新令牌
 * @returns {Promise}
 */
export const refreshToken = (refreshToken) => {
  return request.post('/api/auth/refresh', { refreshToken })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export const getCurrentUser = () => {
  return request.get('/api/auth/me')
}

/**
 * 退出登录
 * @returns {Promise}
 */
export const logout = () => {
  return request.post('/api/auth/logout')
}

/**
 * 注销账户（删除用户）
 * @returns {Promise}
 */
export const deleteAccount = () => {
  return request.delete('/api/auth/delete-account')
}

/**
 * 升级为VIP用户
 * @returns {Promise}
 */
export const upgradeToVip = () => {
  return request.post('/api/auth/upgrade-vip')
}
