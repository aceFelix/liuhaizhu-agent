<template>
  <div class="knowledge-base-manager">
    <!-- Toast 通知 -->
    <ToastNotification ref="toastRef" />

    <!-- VIP 提示 -->
    <div v-if="!canAccess" class="vip-notice">
      <div class="vip-icon">👑</div>
      <p class="vip-text">知识库功能仅限 VIP 用户使用</p>
      <button class="btn-vip" @click="handleUpgrade">升级 VIP</button>
    </div>

    <!-- 知识库列表 -->
    <div v-else class="knowledge-base-section">
      <div class="section-header">
        <h3>我的知识库</h3>
        <div class="header-actions">
          <button
            v-if="knowledgeBases.length > 0"
            class="btn-batch-mode"
            :class="{ active: isBatchMode }"
            @click="toggleBatchMode"
          >
            <span>{{ isBatchMode ? '退出批量' : '批量管理' }}</span>
          </button>
          <button @click="openAddDialog" class="btn-add">
            <span>+</span>
            <span>添加文档</span>
          </button>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="isBatchMode && knowledgeBases.length > 0" class="batch-actions-bar">
        <label class="select-all">
          <input
            type="checkbox"
            :checked="isAllSelected"
            @change="toggleSelectAll"
          />
          <span>全选 ({{ selectedItems.length }}/{{ knowledgeBases.length }})</span>
        </label>
        <button
          class="btn-batch-delete"
          :disabled="selectedItems.length === 0"
          @click="confirmBatchDelete"
        >
          <span>🗑️</span>
          <span>删除选中 ({{ selectedItems.length }})</span>
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="knowledgeBases.length === 0" class="empty-state">
        <div class="empty-icon">📚</div>
        <p class="empty-title">暂无知识库文档</p>
        <p class="empty-desc">添加文档到知识库，让 AI 基于您的知识回答问题</p>
        <button @click="openAddDialog" class="btn-add-empty">添加第一个文档</button>
      </div>

      <!-- 知识库列表 -->
      <div v-else class="knowledge-list">
        <div
          v-for="item in knowledgeBases"
          :key="item.id"
          class="knowledge-item-wrapper"
          :class="{ 'batch-mode': isBatchMode, selected: selectedItems.includes(item.id) }"
        >
          <!-- 批量选择复选框 -->
          <label v-if="isBatchMode" class="item-checkbox">
            <input
              type="checkbox"
              :value="item.id"
              v-model="selectedItems"
            />
          </label>
          <KnowledgeBaseItem
            :item="item"
            :is-editing="editingId === item.id"
            :editing-name="editingName"
            @edit="startEdit"
            @save="saveEdit"
            @cancel="cancelEdit"
            @delete="confirmDelete"
            @update:editing-name="editingName = $event"
          />
        </div>
      </div>
    </div>

    <!-- 添加文档弹窗 -->
    <AddDocumentDialog
      v-model:visible="showAddDialog"
      :user-id="userId"
      @upload="handleUpload"
      @upload-success="handleUploadSuccess"
      @upload-error="handleUploadError"
    />

    <!-- 删除确认弹窗 -->
    <DeleteConfirmDialog
      v-model:visible="showDeleteConfirm"
      :target="deleteTarget"
      @confirm="executeDelete"
      @delete-success="handleDeleteSuccess"
      @delete-error="handleDeleteError"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { usePermission } from '@/utils/permission'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { getRagDocumentList, deleteRagDocument, renameRagDocument, batchDeleteRagDocuments } from '@/api/rag'
import KnowledgeBaseItem from './KnowledgeBaseItem.vue'
import AddDocumentDialog from './AddDocumentDialog.vue'
import DeleteConfirmDialog from './DeleteConfirmDialog.vue'
import ToastNotification from '@/components/common/ToastNotification.vue'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
})

// Auth store
const authStore = useAuthStore()
const chatStore = useChatStore()
const userId = computed(() => authStore.user?.userId || authStore.user?.id)

// Emits
const emit = defineEmits(['upload-success', 'delete-success', 'upgrade-vip'])

// 权限
const { isVip, isAdmin } = usePermission()
const canAccess = computed(() => isVip.value || isAdmin.value)

// 状态
const isLoading = ref(false)
const knowledgeBases = ref([])
const toastRef = ref(null)

// 添加文档相关
const showAddDialog = ref(false)

// 编辑相关
const editingId = ref(null)
const editingName = ref('')

// 删除相关
const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)

// 批量删除相关
const isBatchMode = ref(false)
const selectedItems = ref([])
const isAllSelected = computed(() => {
  return knowledgeBases.value.length > 0 && selectedItems.value.length === knowledgeBases.value.length
})

// 加载知识库列表
const loadKnowledgeBaseList = async () => {
  if (!canAccess.value) return
  if (!userId.value) {
    console.error('用户ID未获取到，无法加载知识库列表')
    return
  }

  isLoading.value = true
  try {
    const response = await getRagDocumentList(userId.value)
    if (response.data.code === 200 && response.data.data) {
      knowledgeBases.value = response.data.data.map((item, index) => ({
        id: index,
        name: item.fileName || item.name,
        fileType: (item.fileType || item.fileName?.split('.').pop() || 'unknown').toLowerCase(),
        size: item.size || 0,
        createdAt: item.createdAt || Date.now(),
      }))
      // 更新知识库状态
      chatStore.setHasKnowledgeBase(knowledgeBases.value.length > 0)
    } else {
      knowledgeBases.value = []
      chatStore.setHasKnowledgeBase(false)
    }
  } catch (error) {
    console.error('加载知识库列表失败:', error)
    knowledgeBases.value = []
    chatStore.setHasKnowledgeBase(false)
  } finally {
    isLoading.value = false
  }
}

// 打开添加对话框
const openAddDialog = () => {
  showAddDialog.value = true
}

// 处理上传成功
const handleUpload = () => {
  loadKnowledgeBaseList()
  emit('upload-success')
}

// 处理上传成功事件
const handleUploadSuccess = (data) => {
  toastRef.value?.success('上传成功', data.message || `文档 "${data.fileName}" 上传成功！`)
  emit('upload-success')
}

// 处理上传失败事件
const handleUploadError = (data) => {
  toastRef.value?.error('上传失败', data.message || '文档上传失败，请重试')
}

// 开始编辑
const startEdit = (item) => {
  editingId.value = item.id
  editingName.value = item.name
}

// 保存编辑
const saveEdit = async (item) => {
  if (editingName.value.trim() && editingName.value !== item.name) {
    try {
      const response = await renameRagDocument(item.name, editingName.value.trim(), userId.value)
      if (response.data.code === 200) {
        item.name = editingName.value.trim()
        emit('upload-success') // 触发更新事件
      } else {
        throw new Error(response.data.message || '重命名失败')
      }
    } catch (error) {
      console.error('重命名文档失败:', error)
      alert(error.response?.data?.message || error.message || '重命名失败，请重试')
    }
  }
  editingId.value = null
  editingName.value = ''
}

// 取消编辑
const cancelEdit = () => {
  editingId.value = null
  editingName.value = ''
}

// 确认删除
const confirmDelete = (item) => {
  deleteTarget.value = item
  showDeleteConfirm.value = true
}

// 执行删除
const executeDelete = async () => {
  if (!deleteTarget.value) return

  // 如果是批量删除
  if (deleteTarget.value.isBatch) {
    await executeBatchDelete()
    deleteTarget.value = null
    return
  }

  // 单条删除
  try {
    const response = await deleteRagDocument(deleteTarget.value.name, userId.value)
    if (response.data.code === 200) {
      const index = knowledgeBases.value.findIndex(item => item.id === deleteTarget.value.id)
      if (index > -1) {
        knowledgeBases.value.splice(index, 1)
      }
      emit('delete-success')
    }
  } catch (error) {
    console.error('删除文档失败:', error)
    alert(error.response?.data?.message || error.message || '文档删除失败，请重试')
  } finally {
    deleteTarget.value = null
  }
}

// 升级VIP
const handleUpgrade = () => {
  emit('upgrade-vip')
}

// 切换批量模式
const toggleBatchMode = () => {
  isBatchMode.value = !isBatchMode.value
  selectedItems.value = []
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedItems.value = []
  } else {
    selectedItems.value = knowledgeBases.value.map(item => item.id)
  }
}

// 确认批量删除
const confirmBatchDelete = () => {
  if (selectedItems.value.length === 0) return
  deleteTarget.value = { isBatch: true, count: selectedItems.value.length }
  showDeleteConfirm.value = true
}

// 执行批量删除
const executeBatchDelete = async () => {
  const selectedFileNames = knowledgeBases.value
    .filter(item => selectedItems.value.includes(item.id))
    .map(item => item.name)

  try {
    const response = await batchDeleteRagDocuments(selectedFileNames, userId.value)
    if (response.data.code === 200) {
      // 从列表中移除已删除的项
      knowledgeBases.value = knowledgeBases.value.filter(
        item => !selectedItems.value.includes(item.id)
      )
      selectedItems.value = []
      isBatchMode.value = false
      emit('delete-success')
    } else {
      throw new Error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    console.error('批量删除文档失败:', error)
    throw error
  }
}

// 处理删除成功事件
const handleDeleteSuccess = (data) => {
  toastRef.value?.success('删除成功', data.message)
  loadKnowledgeBaseList()
  emit('delete-success')
}

// 处理删除失败事件
const handleDeleteError = (data) => {
  toastRef.value?.error('删除失败', data.message || '文档删除失败，请重试')
}

// 监听可见性变化
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadKnowledgeBaseList()
  }
})

// 组件挂载时加载数据
onMounted(() => {
  if (props.visible) {
    loadKnowledgeBaseList()
  }
})

// 暴露方法给父组件
defineExpose({
  loadKnowledgeBaseList,
  knowledgeBases,
})
</script>

<style scoped>
.knowledge-base-manager {
  width: 100%;
}

/* VIP 提示 */
.vip-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.vip-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.vip-text {
  color: var(--text-secondary);
  font-size: 16px;
  margin-bottom: 20px;
}

.btn-vip {
  padding: 12px 32px;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-vip:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

/* 知识库管理 */
.knowledge-base-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

/* 头部操作按钮组 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 批量模式按钮 */
.btn-batch-mode {
  padding: 8px 16px;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-batch-mode:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-batch-mode.active {
  background: rgba(124, 58, 237, 0.1);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* 批量操作栏 */
.batch-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  margin-bottom: 8px;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-primary);
  font-size: 14px;
}

.select-all input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--color-primary);
}

.btn-batch-delete {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-batch-delete:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.2);
  border-color: #ef4444;
}

.btn-batch-delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-title {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.empty-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}

.btn-add-empty {
  padding: 10px 24px;
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-add-empty:hover {
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.1);
}

/* 知识库列表 */
.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 知识库项包装器 */
.knowledge-item-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s;
}

.knowledge-item-wrapper.batch-mode {
  padding: 8px 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
}

.knowledge-item-wrapper.batch-mode.selected {
  border-color: var(--color-primary);
  background: rgba(124, 58, 237, 0.05);
}

.knowledge-item-wrapper :deep(.knowledge-item) {
  flex: 1;
}

/* 批量选择复选框 */
.item-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.item-checkbox input[type="checkbox"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: var(--color-primary);
}
</style>
