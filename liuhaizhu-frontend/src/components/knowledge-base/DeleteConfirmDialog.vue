<template>
  <div v-if="visible" class="dialog-overlay" @click="handleClose">
    <div class="dialog-content dialog-small" @click.stop>
      <div class="dialog-header">
        <h3>确认删除</h3>
      </div>
      <div class="dialog-body">
        <!-- 删除中状态 -->
        <div v-if="isDeleting" class="deleting-state">
          <div class="delete-animation">
            <div class="delete-spinner"></div>
            <div class="delete-icon">🗑️</div>
          </div>
          <p class="delete-status">正在删除...</p>
          <p class="delete-detail">{{ deleteStatusText }}</p>
        </div>

        <!-- 确认内容 -->
        <template v-else>
          <div class="confirm-icon">⚠️</div>
          <template v-if="target?.isBatch">
            <p class="confirm-text">确定要删除选中的 <strong>{{ target?.count }}</strong> 个文档吗？</p>
          </template>
          <template v-else>
            <p class="confirm-text">确定要删除文档 <strong>"{{ target?.name }}"</strong> 吗？</p>
          </template>
          <p class="confirm-hint">删除后无法恢复，相关向量索引也将被清除</p>
        </template>
      </div>
      <div class="dialog-footer">
        <button @click="handleClose" :disabled="isDeleting" class="btn-cancel">取消</button>
        <button @click="handleConfirm" :disabled="isDeleting" class="btn-delete-confirm">
          <span v-if="isDeleting">
            <span class="btn-spinner"></span>
            删除中...
          </span>
          <span v-else>确认删除</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  target: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:visible', 'confirm', 'delete-success', 'delete-error'])

const isDeleting = ref(false)
const deleteStatusText = ref('正在清理数据...')

const handleClose = () => {
  if (!isDeleting.value) {
    emit('update:visible', false)
  }
}

const handleConfirm = async () => {
  isDeleting.value = true
  deleteStatusText.value = '正在清理数据...'
  
  try {
    // 模拟删除进度
    setTimeout(() => {
      deleteStatusText.value = '正在删除向量索引...'
    }, 300)
    
    await emit('confirm')
    
    // 删除成功
    deleteStatusText.value = '删除完成！'
    
    setTimeout(() => {
      emit('delete-success', {
        isBatch: props.target?.isBatch,
        count: props.target?.count || 1,
        message: props.target?.isBatch 
          ? `成功删除 ${props.target?.count} 个文档` 
          : `成功删除文档 "${props.target?.name}"`
      })
      emit('update:visible', false)
    }, 500)
  } catch (error) {
    console.error('删除失败:', error)
    emit('delete-error', {
      isBatch: props.target?.isBatch,
      count: props.target?.count || 1,
      message: error.message || '删除失败，请重试'
    })
  } finally {
    isDeleting.value = false
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
  max-width: 360px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
  animation: slideUp 0.3s ease;
}

.dialog-small {
  max-width: 360px;
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
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.dialog-body {
  padding: 24px 20px;
  text-align: center;
}

.dialog-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 删除中状态 */
.deleting-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.delete-animation {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.delete-spinner {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 3px solid rgba(239, 68, 68, 0.2);
  border-top-color: #ef4444;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.delete-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 28px;
  animation: shake 0.5s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes shake {
  0%, 100% { transform: translate(-50%, -50%) rotate(0deg); }
  25% { transform: translate(-50%, -50%) rotate(-5deg); }
  75% { transform: translate(-50%, -50%) rotate(5deg); }
}

.delete-status {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.delete-detail {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

/* 确认内容 */
.confirm-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.confirm-text {
  color: var(--text-primary);
  font-size: 14px;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.confirm-text strong {
  color: #ef4444;
  font-weight: 600;
}

.confirm-hint {
  color: var(--text-secondary);
  font-size: 12px;
  margin: 0;
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

.btn-delete-confirm {
  padding: 8px 16px;
  background: #ef4444;
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

.btn-delete-confirm:hover:not(:disabled) {
  background: #dc2626;
  transform: translateY(-1px);
}

.btn-delete-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
</style>
