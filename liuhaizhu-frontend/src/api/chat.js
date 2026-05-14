import { request } from './config'

const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL
  if (envUrl !== undefined && envUrl !== null && envUrl !== '') {
    return envUrl
  }
  return import.meta.env.PROD ? '' : 'http://localhost:8000'
}

const BASE_URL = getBaseUrl()

/**
 * 建立SSE连接
 * 注意：EventSource API不支持自定义请求头，所以token通过URL参数传递
 */
export const connectSSE = (userId, token) => {
  const baseUrl = getBaseUrl()
  const sseUrl = baseUrl 
    ? `${baseUrl}/api/sse/connect` 
    : `${window.location.origin}/api/sse/connect`
  const url = new URL(sseUrl)
  url.searchParams.append('userId', userId)
  if (token) {
    url.searchParams.append('token', token)
  }
  return new EventSource(url.toString())
}

/**
 * 普通聊天接口
 */
export const doChat = (data) => {
  return request.post('/api/chat/doChat', data)
}

/**
 * 联网搜索聊天接口
 */
export const webSearch = (data) => {
  return request.post('/api/webSearch/query', data)
}

/**
 * 知识库搜索聊天接口
 */
export const ragSearch = (data) => {
  return request.post('/api/rag/search', data)
}

/**
 * 清空会话的所有消息
 * @param {string} conversationId - 会话ID
 * @param {string} userId - 用户ID
 */
export const clearConversationMessages = (conversationId, userId) => {
  return request.delete('/api/chat/conversations/clear', {
    params: { conversationId, userId }
  })
}

/**
 * 文件上传聊天接口
 * @param {FormData} formData - 包含文件和消息的表单数据
 */
export const fileChat = (formData) => {
  return request.post('/api/fileChat/chat', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
