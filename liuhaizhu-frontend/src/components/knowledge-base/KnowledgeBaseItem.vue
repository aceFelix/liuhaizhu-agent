<template>
  <div class="knowledge-item" :class="{ 'is-editing': isEditing }">
    <div class="knowledge-info">
      <div class="knowledge-icon">
        <span>{{ getFileIcon(item.fileType) }}</span>
      </div>
      <div class="knowledge-details">
        <div v-if="isEditing" class="edit-name">
          <input
            :value="editingName"
            @input="$emit('update:editing-name', $event.target.value)"
            @blur="handleSave"
            @keyup.enter="handleSave"
            @keyup.esc="$emit('cancel')"
            ref="editInput"
            type="text"
            class="edit-input"
          />
        </div>
        <template v-else>
          <p class="knowledge-name" :title="item.name">{{ item.name }}</p>
          <p class="knowledge-meta">
            <span class="file-type">{{ item.fileType.toUpperCase() }}</span>
            <span class="file-size">{{ formatFileSize(item.size) }}</span>
            <span class="upload-time">{{ formatTime(item.createdAt) }}</span>
          </p>
        </template>
      </div>
    </div>
    <div v-if="!isEditing" class="knowledge-actions">
      <button
        @click="$emit('edit', item)"
        class="btn-action btn-edit"
        title="重命名"
      >
        ✏️
      </button>
      <button
        @click="$emit('delete', item)"
        class="btn-action btn-delete"
        title="删除"
      >
        🗑️
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  isEditing: {
    type: Boolean,
    default: false,
  },
  editingName: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['edit', 'save', 'cancel', 'delete', 'update:editing-name'])

const editInput = ref(null)

// 监听编辑状态，自动聚焦
watch(() => props.isEditing, (newVal) => {
  if (newVal) {
    nextTick(() => {
      editInput.value?.focus()
      editInput.value?.select()
    })
  }
})

// 文件图标
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

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 处理保存
const handleSave = () => {
  emit('save', props.item)
}
</script>

<style scoped>
.knowledge-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  transition: all 0.3s;
}

.knowledge-item:hover {
  border-color: rgba(139, 92, 246, 0.3);
  background: rgba(139, 92, 246, 0.05);
}

.knowledge-item.is-editing {
  border-color: var(--color-primary);
}

.knowledge-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.knowledge-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 8px;
  font-size: 20px;
  flex-shrink: 0;
}

.knowledge-details {
  flex: 1;
  min-width: 0;
}

.knowledge-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.file-type {
  background: rgba(124, 58, 237, 0.15);
  color: var(--color-primary);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.knowledge-actions {
  display: flex;
  gap: 4px;
}

.btn-action {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-edit:hover {
  background: rgba(139, 92, 246, 0.1);
}

.btn-delete:hover {
  background: rgba(239, 68, 68, 0.1);
}

/* 编辑输入框 */
.edit-name {
  width: 100%;
}

.edit-input {
  width: 100%;
  padding: 6px 10px;
  background: var(--bg-tertiary);
  border: 1px solid var(--color-primary);
  border-radius: 6px;
  color: var(--text-primary);
  font-size: 14px;
  outline: none;
}

.edit-input:focus {
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.2);
}
</style>
