import { request } from './config'

const MAX_FILE_SIZE = 10 * 1024 * 1024

export const uploadRagDocument = (file, userId) => {
  if (!file) {
    return Promise.reject(new Error('请选择文件'))
  }

  if (file.size > MAX_FILE_SIZE) {
    return Promise.reject(new Error('文件大小不能超过10MB'))
  }

  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId)

  return request.post('/api/rag/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      console.log(`上传进度: ${percentCompleted}%`)
    },
  })
}

export const getUploadStatus = (taskId, userId) => {
  return request.get(`/api/rag/upload/status/${taskId}`, {
    params: { userId }
  })
}

/**
 * 获取知识库文档列表
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const getRagDocumentList = (userId) => {
  return request.get('/api/rag/list', {
    params: { userId }
  })
}

/**
 * 删除知识库文档
 * @param {string} fileName - 文件名
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const deleteRagDocument = (fileName, userId) => {
  return request.delete(`/api/rag/delete/${encodeURIComponent(fileName)}`, {
    params: { userId }
  })
}

/**
 * 重命名知识库文档
 * @param {string} oldName - 原文件名
 * @param {string} newName - 新文件名
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const renameRagDocument = (oldName, newName, userId) => {
  return request.put('/api/rag/rename', null, {
    params: {
      oldName,
      newName,
      userId,
    }
  })
}

/**
 * 批量删除知识库文档
 * @param {string[]} fileNames - 文件名列表
 * @param {string} userId - 用户ID
 * @returns {Promise}
 */
export const batchDeleteRagDocuments = (fileNames, userId) => {
  return request.post('/api/rag/batch-delete', {
    fileNames
  }, {
    params: { userId }
  })
}

/**
 * 知识库搜索（用于聊天）
 * @param {Object} data - 搜索参数
 * @param {string} data.query - 搜索查询
 * @param {string} data.userId - 用户ID
 * @returns {Promise}
 */
export const searchRag = (data) => {
  return request.post('/api/rag/search', data)
}
