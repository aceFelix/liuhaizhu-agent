import { request } from './config'

/**
 * 获取所有知识库文档列表（管理员）
 * @returns {Promise}
 */
export const getAllKnowledgeDocs = () => {
  return request({
    url: '/api/admin/knowledge/list',
    method: 'get',
  })
}

/**
 * 获取指定用户的知识库文档列表
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getUserKnowledgeDocs = (userId) => {
  return request({
    url: '/api/rag/list',
    method: 'get',
    params: { userId },
  })
}

/**
 * 上传知识库文档
 * @param {string} userId - 用户ID
 * @param {File} file - 文件对象
 * @returns {Promise}
 */
export const uploadKnowledgeDoc = (userId, file) => {
  const formData = new FormData()
  formData.append('userId', userId)
  formData.append('file', file)

  return request({
    url: '/api/rag/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

/**
 * 删除知识库文档
 * @param {string} userId - 用户ID
 * @param {string} fileName - 文件名
 * @returns {Promise}
 */
export const deleteKnowledgeDoc = (userId, fileName) => {
  return request({
    url: `/api/rag/delete/${encodeURIComponent(fileName)}`,
    method: 'delete',
    params: { userId },
  })
}

/**
 * 批量删除知识库文档
 * @param {Array} docs - 文档列表 [{userId, fileName}]
 * @returns {Promise}
 */
export const batchDeleteKnowledgeDocs = (docs) => {
  return request({
    url: '/api/admin/knowledge/batch-delete',
    method: 'post',
    data: { docs },
  })
}

/**
 * 重命名知识库文档
 * @param {string} userId - 用户ID
 * @param {string} oldName - 旧文件名
 * @param {string} newName - 新文件名
 * @returns {Promise}
 */
export const renameKnowledgeDoc = (userId, oldName, newName) => {
  return request({
    url: '/api/rag/rename',
    method: 'put',
    params: {
      userId,
      oldName,
      newName,
    },
  })
}

/**
 * 获取知识库统计信息（管理员）
 * @returns {Promise}
 */
export const getKnowledgeStats = () => {
  return request({
    url: '/api/admin/knowledge/stats',
    method: 'get',
  })
}

/**
 * 下载知识库文档
 * @param {string} userId - 用户ID
 * @param {string} fileName - 文件名
 * @returns {Promise}
 */
export const downloadKnowledgeDoc = (userId, fileName) => {
  return request({
    url: '/api/admin/knowledge/download',
    method: 'get',
    params: { userId, fileName },
    responseType: 'blob',
  })
}

/**
 * 预览知识库文档
 * @param {string} userId - 用户ID
 * @param {string} fileName - 文件名
 * @returns {Promise}
 */
export const previewKnowledgeDoc = (userId, fileName) => {
  return request({
    url: '/api/admin/knowledge/preview',
    method: 'get',
    params: { userId, fileName },
  })
}
