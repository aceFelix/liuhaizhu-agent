import { request } from './config'

/**
 * 创建会话
 * @param {string} userId - 用户ID
 * @param {string} title - 会话标题
 * @param {string} type - 会话类型
 * @returns {Promise}
 */
export const createConversation = (userId, title = '新对话', type = 'normal') => {
  return request.post('/api/conversation/create', { userId, title, type })
}

/**
 * 获取会话列表
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getConversationList = (userId) => {
  return request.get('/api/conversation/list', { params: { userId } })
}

/**
 * 获取会话详情（含消息历史）
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getConversationDetail = (conversationId, userId) => {
  return request.get('/api/conversation/detail', { params: { conversationId, userId } })
}

/**
 * 删除会话
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const deleteConversation = (conversationId, userId) => {
  return request.delete('/api/conversation/delete', { params: { conversationId, userId } })
}

/**
 * 清空会话消息
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const clearConversationMessages = (conversationId, userId) => {
  return request.delete('/api/conversation/clear', { params: { conversationId, userId } })
}

/**
 * 更新会话标题
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 * @param {string} title - 新标题
 * @returns {Promise}
 */
export const updateConversationTitle = (conversationId, userId, title) => {
  return request.put('/api/conversation/updateTitle', { conversationId, userId, title })
}

/**
 * 置顶/取消置顶会话
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 * @param {boolean} pinned - 是否置顶
 * @returns {Promise}
 */
export const updateConversationPinned = (conversationId, userId, pinned) => {
  return request.put('/api/conversation/pin', { conversationId, userId, pinned })
}

/**
 * 批量删除会话
 * @param {Array<string>} conversationIds - 会话ID列表
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const batchDeleteConversations = (conversationIds, userId) => {
  return request.delete('/api/conversation/batchDelete', { data: { conversationIds, userId } })
}
