// API 统一入口
// 按功能模块导出所有 API

// 基础配置
export { request } from './config'

// 认证相关
export {
  login,
  register,
  refreshToken,
  getCurrentUser,
  logout,
} from './auth'

// 聊天相关
export {
  connectSSE,
  doChat,
  webSearch,
  ragSearch,
} from './chat'

// 会话相关
export {
  createConversation,
  getConversationList,
  getConversationDetail,
  deleteConversation,
  clearConversationMessages,
  updateConversationTitle,
} from './conversation'

// 知识库相关
export {
  uploadRagDocument,
  getRagDocumentList,
  deleteRagDocument,
  renameRagDocument,
  searchRag,
} from './rag'
