<template>
  <div v-if="visible" class="dialog-overlay" @click="handleClose">
    <div class="dialog-content" @click.stop>
      <div class="dialog-header">
        <h3>添加知识库文档</h3>
        <button @click="handleClose" class="btn-close-small" :disabled="isUploading">×</button>
      </div>
      <div class="dialog-body">
        <!-- 上传中状态 -->
        <div v-if="isUploading" class="upload-progress-container">
          <div class="progress-animation">
            <div class="progress-circle">
              <svg viewBox="0 0 100 100" class="progress-ring">
                <circle
                  class="progress-ring-bg"
                  cx="50"
                  cy="50"
                  r="45"
                />
                <circle
                  class="progress-ring-fill"
                  cx="50"
                  cy="50"
                  r="45"
                  :style="{ strokeDashoffset: progressOffset }"
                />
              </svg>
              <div class="progress-text">
                <span class="progress-percent">{{ uploadProgress }}%</span>
              </div>
            </div>
          </div>
          <p class="upload-status">{{ uploadStatusText }}</p>
          <p class="upload-detail">{{ selectedFile?.name }}</p>
        </div>

        <!-- 正常上传界面 -->
        <template v-else>
          <div
            class="upload-area"
            @click="triggerFileInput"
            @dragover.prevent="isDragging = true"
            @dragleave.prevent="isDragging = false"
            @drop.prevent="handleDrop"
            :class="{ 'is-dragging': isDragging }"
          >
            <input
              type="file"
              ref="fileInput"
              @change="handleFileSelect"
              accept=".txt,.pdf,.doc,.docx,.md"
              style="display: none"
            />
            <div class="upload-icon">📤</div>
            <p class="upload-title">点击或拖拽文件到此处</p>
            <p class="upload-desc">支持 PDF、Word、TXT、Markdown 格式，单个文件不超过 10MB</p>
          </div>

          <!-- 文件预览 -->
          <div v-if="selectedFile" class="file-preview">
            <div class="preview-icon">{{ getFileIcon(getFileExtension(selectedFile.name)) }}</div>
            <div class="preview-info">
              <p class="preview-name">{{ selectedFile.name }}</p>
              <p class="preview-size">{{ formatFileSize(selectedFile.size) }}</p>
            </div>
            <button @click="clearSelectedFile" class="btn-clear">×</button>
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            <span class="error-icon">⚠️</span>
            {{ errorMessage }}
          </div>
        </template>
      </div>
      <div class="dialog-footer">
        <button @click="handleClose" :disabled="isUploading" class="btn-cancel">取消</button>
        <button
          @click="handleUpload"
          :disabled="!selectedFile || isUploading"
          class="btn-upload"
          :class="{ 'is-loading': isUploading }"
        >
          <span v-if="isUploading">
            <span class="btn-spinner"></span>
            上传中...
          </span>
          <span v-else>确认上传</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, onUnmounted } from 'vue'
import { uploadRagDocument, getUploadStatus } from '@/api/rag'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  userId: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:visible', 'upload', 'upload-success', 'upload-error'])

const fileInput = ref(null)
const selectedFile = ref(null)
const isDragging = ref(false)
const isUploading = ref(false)
const errorMessage = ref('')
const uploadProgress = ref(0)
const uploadStatusText = ref('准备上传...')
const pollingTimer = ref(null)

const progressOffset = computed(() => {
  const circumference = 2 * Math.PI * 45
  return circumference - (uploadProgress.value / 100) * circumference
})

watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetState()
  }
})

onUnmounted(() => {
  stopPolling()
})

const resetState = () => {
  selectedFile.value = null
  isDragging.value = false
  isUploading.value = false
  errorMessage.value = ''
  uploadProgress.value = 0
  uploadStatusText.value = '准备上传...'
  stopPolling()
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

const handleClose = () => {
  if (!isUploading.value) {
    emit('update:visible', false)
  }
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    validateAndSetFile(file)
  }
}

const handleDrop = (event) => {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    validateAndSetFile(file)
  }
}

const validateAndSetFile = (file) => {
  errorMessage.value = ''

  const allowedTypes = ['.txt', '.pdf', '.doc', '.docx', '.md']
  const ext = '.' + file.name.split('.').pop().toLowerCase()

  if (!allowedTypes.includes(ext)) {
    errorMessage.value = '不支持的文件格式，请上传 PDF、Word、TXT 或 Markdown 文件'
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    errorMessage.value = '文件大小超过 10MB 限制'
    return
  }

  selectedFile.value = file
}

const clearSelectedFile = () => {
  selectedFile.value = null
  errorMessage.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const getFileIcon = (fileType) => {
  const icons = {
    pdf: '📄',
    doc: '📝',
    docx: '📝',
    txt: '📃',
    md: '📑',
  }
  return icons[fileType.toLowerCase()] || '📎'
}

const getFileExtension = (filename) => {
  return filename.split('.').pop().toLowerCase()
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const pollUploadStatus = async (taskId) => {
  try {
    const response = await getUploadStatus(taskId, props.userId)
    const status = response.data.data
    
    if (!status) {
      return
    }

    uploadProgress.value = status.progress || 0
    uploadStatusText.value = status.message || '处理中...'

    if (status.status === 'COMPLETED') {
      stopPolling()
      uploadProgress.value = 100
      uploadStatusText.value = '上传完成！'
      
      setTimeout(() => {
        emit('upload-success', {
          fileName: selectedFile.value.name,
          message: '文档上传成功！'
        })
        emit('upload')
        emit('update:visible', false)
      }, 800)
    } else if (status.status === 'FAILED') {
      stopPolling()
      const errorMsg = status.errorMessage || status.message || '文档处理失败'
      errorMessage.value = errorMsg
      emit('upload-error', {
        fileName: selectedFile.value?.name,
        message: errorMsg
      })
      setTimeout(() => {
        isUploading.value = false
      }, 500)
    }
  } catch (error) {
    console.error('查询上传状态失败:', error)
  }
}

const handleUpload = async () => {
  if (!selectedFile.value) return
  if (!props.userId) {
    errorMessage.value = '用户ID未获取到，请重新登录后重试'
    return
  }

  isUploading.value = true
  errorMessage.value = ''
  uploadProgress.value = 0
  uploadStatusText.value = '正在上传文件...'

  try {
    const response = await uploadRagDocument(selectedFile.value, props.userId)

    if (response.data.code === 200) {
      const status = response.data.data
      
      if (status && status.taskId) {
        uploadProgress.value = status.progress || 5
        uploadStatusText.value = status.message || '文件已上传，正在处理...'
        
        pollingTimer.value = setInterval(() => {
          pollUploadStatus(status.taskId)
        }, 1000)
      } else {
        uploadProgress.value = 100
        uploadStatusText.value = '上传完成！'
        setTimeout(() => {
          emit('upload-success', {
            fileName: selectedFile.value.name,
            message: '文档上传成功！'
          })
          emit('upload')
          emit('update:visible', false)
        }, 800)
      }
    } else {
      throw new Error(response.data.message || '上传失败')
    }
  } catch (error) {
    console.error('上传文档失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '文档上传失败，请重试'
    errorMessage.value = errorMsg
    isUploading.value = false
    emit('upload-error', {
      fileName: selectedFile.value?.name,
      message: errorMsg
    })
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  animation: fadeIn 0.2s ease;
}

.dialog-content {
  background: var(--bg-tertiary);
  border-radius: 12px;
  width: 90%;
  max-width: 480px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
  animation: slideUp 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.btn-close-small {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close-small:hover:not(:disabled) {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-close-small:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.dialog-body {
  padding: 20px;
  min-height: 200px;
}

.dialog-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 上传进度容器 */
.upload-progress-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.progress-animation {
  margin-bottom: 20px;
}

.progress-circle {
  position: relative;
  width: 120px;
  height: 120px;
}

.progress-ring {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.progress-ring-bg {
  fill: none;
  stroke: var(--border-color);
  stroke-width: 6;
}

.progress-ring-fill {
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 6;
  stroke-linecap: round;
  stroke-dasharray: 283;
  transition: stroke-dashoffset 0.3s ease;
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.progress-percent {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
}

.upload-status {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.upload-detail {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 上传区域 */
.upload-area {
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover,
.upload-area.is-dragging {
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.05);
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.upload-title {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.upload-desc {
  color: var(--text-secondary);
  font-size: 12px;
}

/* 文件预览 */
.file-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding: 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
}

.preview-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 8px;
  font-size: 20px;
}

.preview-info {
  flex: 1;
  min-width: 0;
}

.preview-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-size {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.btn-clear {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(239, 68, 68, 0.1);
  border: none;
  border-radius: 4px;
  color: #ef4444;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.btn-clear:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding: 10px 12px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 8px;
  color: #ef4444;
  font-size: 13px;
}

.error-icon {
  font-size: 14px;
}

/* 按钮样式 */
.btn-cancel {
  padding: 8px 16px;
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-cancel:hover:not(:disabled) {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-cancel:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-upload {
  padding: 8px 16px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-upload:hover:not(:disabled) {
  background: var(--color-primary);
  transform: translateY(-1px);
}

.btn-upload:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-upload.is-loading {
  background: var(--color-primary-dark);
}

.btn-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
