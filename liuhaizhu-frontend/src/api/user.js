import { request } from './config'

/**
 * 获取所有用户列表（管理员）
 * @returns {Promise}
 */
export const getAllUsers = () => {
  return request.get('/api/admin/users')
}

/**
 * 根据ID获取用户信息（管理员）
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getUserById = (userId) => {
  return request.get(`/api/admin/users/${userId}`)
}

/**
 * 删除用户（管理员）
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const deleteUser = (userId) => {
  return request.delete(`/api/admin/users/${userId}`)
}

/**
 * 更新用户状态（管理员）
 * @param {string} userId - 用户ID
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise}
 */
export const updateUserStatus = (userId, status) => {
  return request.put(`/api/admin/users/${userId}/status?status=${status}`)
}

/**
 * 更新用户角色（管理员）
 * @param {string} userId - 用户ID
 * @param {string} role - 角色（USER, VIP, ADMIN）
 * @returns {Promise}
 */
export const updateUserRole = (userId, role) => {
  return request.put(`/api/admin/users/${userId}/role?role=${role}`)
}

/**
 * 获取当前登录用户的个人信息
 * @returns {Promise}
 */
export const getUserProfile = () => {
  return request.get('/api/user/profile')
}

/**
 * 更新当前登录用户的个人信息
 * @param {Object} data - 更新数据
 * @param {string} data.username - 用户名
 * @param {string} data.avatar - 头像
 * @returns {Promise}
 */
export const updateUserProfile = (data) => {
  return request.put('/api/user/profile', data)
}

/**
 * 修改当前登录用户的密码
 * @param {Object} data - 密码数据
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise}
 */
export const updatePassword = (data) => {
  return request.put('/api/user/profile/password', data)
}

/**
 * 发送邮箱验证码
 * @param {Object} data - 验证码请求
 * @param {string} data.email - 邮箱
 * @param {string} data.type - 验证码类型
 * @returns {Promise}
 */
export const sendEmailVerificationCode = (data) => {
  return request.post('/api/user/profile/send-verification-code', data)
}

/**
 * 发送邮箱绑定验证码
 * @param {Object} data - 验证码请求
 * @param {string} data.email - 邮箱
 * @returns {Promise}
 */
export const sendBindEmailCode = (data) => {
  return request.post('/api/user/profile/send-bind-email-code', data)
}

/**
 * 绑定邮箱
 * @param {Object} data - 绑定邮箱请求
 * @param {string} data.email - 邮箱
 * @param {string} data.code - 验证码
 * @returns {Promise}
 */
export const bindEmail = (data) => {
  return request.post('/api/user/profile/bind-email', data)
}

/**
 * 管理员创建用户（无需密码和邮箱验证码，默认密码123456）
 * @param {Object} data - 用户数据
 * @param {string} data.username - 用户名
 * @param {string} data.email - 邮箱
 * @param {string} data.role - 角色（USER, VIP, ADMIN）
 * @returns {Promise}
 */
export const createUserByAdmin = (data) => {
  return request.post('/api/admin/users', data)
}

/**
 * 获取Token消耗统计摘要（管理员）
 * @returns {Promise}
 */
export const getTokenSummary = () => {
  return request.get('/api/admin/tokens/summary')
}

/**
 * 获取指定用户的Token消耗明细（管理员）
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getUserTokenDetail = (userId) => {
  return request.get(`/api/admin/tokens/detail/${userId}`)
}

/**
 * 获取按天汇总Token消耗（管理员）
 * @returns {Promise}
 */
export const getDailyTokenSummary = () => {
  return request.get('/api/admin/tokens/daily-summary')
}

/**
 * 获取按小时汇总Token消耗（管理员）
 * @returns {Promise}
 */
export const getHourlyTokenSummary = () => {
  return request.get('/api/admin/tokens/hourly-summary')
}

/**
 * 获取按周汇总Token消耗（管理员）
 * @returns {Promise}
 */
export const getWeeklyTokenSummary = () => {
  return request.get('/api/admin/tokens/weekly-summary')
}

/**
 * 获取指定用户的按小时Token消耗明细（管理员）
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getUserHourlyTokenDetail = (userId) => {
  return request.get(`/api/admin/tokens/hourly-detail/${userId}`)
}

/**
 * 获取指定用户的按周Token消耗明细（管理员）
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getUserWeeklyTokenDetail = (userId) => {
  return request.get(`/api/admin/tokens/weekly-detail/${userId}`)
}
