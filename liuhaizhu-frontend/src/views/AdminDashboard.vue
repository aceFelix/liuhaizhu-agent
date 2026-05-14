<template>
  <div class="admin-dashboard" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <!-- 科幻背景效果 -->
    <CyberBackground />

    <!-- 顶部导航 -->
    <nav class="admin-nav">
      <div class="nav-content">
        <div class="nav-left">
          <Logo clickable @click="goToLanding" />
          <span class="nav-divider">|</span>
          <span class="nav-title">管理员控制台</span>
        </div>
        <div class="nav-right">
          <ThemeToggle />
          <button @click="navigateToUpgradeVIP" class="btn-upgrade-vip" title="升级VIP">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <span>会员</span>
          </button>
          <div class="user-info">
            <img :src="authStore.user?.avatar || '/images/user-avatar.jpg'" alt="Avatar" class="user-avatar" />
            <span class="user-name">{{ authStore.user?.username }}</span>
          </div>
          <button @click="handleLogout" class="btn-logout">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
            </svg>
            <span>退出</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <div class="admin-main">
      <!-- 侧边栏 -->
      <aside class="admin-sidebar">
        <div class="sidebar-menu">
          <div
            v-for="item in menuItems"
            :key="item.key"
            class="menu-item"
            :class="{ active: activeMenu === item.key }"
            @click="activeMenu = item.key"
          >
            <span class="menu-icon">
              <img v-if="item.isImage" :src="item.icon" alt="icon" class="menu-icon-img" />
              <span v-else>{{ item.icon }}</span>
            </span>
            <span class="menu-text">{{ item.label }}</span>
          </div>
        </div>
      </aside>

      <!-- 内容区 -->
      <main class="admin-content">
        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">用户管理</h2>
            <div class="section-actions">
              <div class="search-box">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                </svg>
                <input type="text" placeholder="搜索用户名或邮箱..." v-model="userSearchQuery" />
              </div>
              <button @click="showAddUserDialog = true" class="btn-add-user">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
                <span>添加用户</span>
              </button>
            </div>
          </div>

          <div class="data-table-wrapper">
            <div v-if="userLoading" class="loading-state">加载中...</div>
            <table v-else class="data-table">
              <thead>
                <tr>
                  <th>用户信息</th>
                  <th>角色</th>
                  <th>状态</th>
                  <th>注册时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.userId">
                  <td>
                    <div class="user-cell">
                      <img :src="user.avatar || '/images/user-avatar.jpg'" alt="Avatar" class="user-avatar-small" />
                      <div class="user-info-cell">
                        <div class="user-name-cell">{{ user.username }}</div>
                        <div class="user-email-cell">{{ user.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <select v-model="user.role" class="role-select" @change="updateUserRole(user)">
                      <option value="USER">普通用户</option>
                      <option value="VIP">VIP用户</option>
                      <option value="ADMIN">管理员</option>
                    </select>
                  </td>
                  <td>
                    <label class="switch">
                      <input type="checkbox" v-model="user.status" :true-value="1" :false-value="0" @change="updateUserStatus(user)" />
                      <span class="slider"></span>
                    </label>
                    <span class="status-text" :class="{ active: user.status === 1 }">
                      {{ user.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>{{ formatDate(user.createTime) }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="btn-action view" @click="viewUserDetail(user)" title="查看详情">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                          <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                        </svg>
                      </button>
                      <button class="btn-action delete" @click="deleteUser(user)" title="删除用户">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                          <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 分页 -->
          <div class="pagination">
            <button class="btn-page" :disabled="userPage <= 1" @click="userPage--">上一页</button>
            <span class="page-info">第 {{ userPage }} 页</span>
            <button class="btn-page" @click="userPage++">下一页</button>
          </div>
        </div>

        <!-- 知识库管理 -->
        <div v-if="activeMenu === 'knowledge'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">知识库管理</h2>
            <div class="section-actions">
              <div class="search-box">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                </svg>
                <input type="text" placeholder="搜索文档或用户名..." v-model="knowledgeSearchQuery" />
              </div>
              <button
                v-if="knowledgeDocs.length > 0"
                class="btn-batch-mode"
                :class="{ active: isBatchMode }"
                @click="toggleBatchMode"
              >
                <span>{{ isBatchMode ? '退出批量' : '批量管理' }}</span>
              </button>
              <button class="btn-add-user" @click="showUploadDialog = true">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
                <span>上传文档</span>
              </button>
            </div>
          </div>

          <!-- 批量操作工具栏 -->
          <div v-if="isBatchMode" class="batch-toolbar">
            <button @click="toggleSelectAll" class="btn-batch">
              {{ selectedDocs.size === paginatedKnowledgeDocs.length && paginatedKnowledgeDocs.length > 0 ? '取消全选' : '全选' }}
            </button>
            <span class="batch-count">{{ selectedDocs.size }}</span>
            <button @click="batchDeleteDocs" class="btn-batch-delete" :disabled="selectedDocs.size === 0">
              批量删除
            </button>
            <button @click="toggleBatchMode" class="btn-batch-cancel">取消</button>
          </div>

          <div class="data-table-wrapper" v-loading="knowledgeLoading">
            <table class="data-table">
              <thead>
                <tr>
                  <th v-if="isBatchMode" style="width: 50px;">
                    <div class="checkbox" :class="{ checked: selectedDocs.size === paginatedKnowledgeDocs.length && paginatedKnowledgeDocs.length > 0 }" @click="toggleSelectAll">
                      <span v-if="selectedDocs.size === paginatedKnowledgeDocs.length && paginatedKnowledgeDocs.length > 0">✓</span>
                    </div>
                  </th>
                  <th>文档信息</th>
                  <th>所属用户</th>
                  <th>文件大小</th>
                  <th>上传时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="doc in paginatedKnowledgeDocs" :key="`${doc.userId}_${doc.fileName}`">
                  <td v-if="isBatchMode">
                    <div class="checkbox" :class="{ checked: selectedDocs.has(`${doc.userId}_${doc.fileName}`) }" @click.stop="toggleDocSelect(doc)">
                      <span v-if="selectedDocs.has(`${doc.userId}_${doc.fileName}`)">✓</span>
                    </div>
                  </td>
                  <td>
                    <div class="doc-cell">
                      <div class="doc-icon">{{ getFileTypeIcon(doc.fileName) }}</div>
                      <div class="doc-info-cell">
                        <div class="doc-name-cell">{{ doc.fileName }}</div>
                        <div class="doc-type-cell">{{ getFileTypeName(doc.fileName) }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="owner-cell">
                      <img :src="doc.userAvatar || '/images/user-avatar.jpg'" alt="Avatar" class="owner-avatar" />
                      <span class="owner-name">{{ doc.username || doc.userId }}</span>
                    </div>
                  </td>
                  <td>{{ formatFileSize(doc.fileSize) }}</td>
                  <td>{{ formatDate(doc.uploadTime) }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="btn-action view" @click="viewDocDetail(doc)" title="查看详情">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                          <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                        </svg>
                      </button>
                      <button class="btn-action download" @click="downloadDoc(doc)" title="下载">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                          <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
                        </svg>
                      </button>
                      <button class="btn-action delete" @click="deleteDoc(doc)" title="删除">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                          <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>

            <!-- 空状态 -->
            <div v-if="paginatedKnowledgeDocs.length === 0" class="empty-state">
              <div class="empty-icon">📚</div>
              <p>暂无知识库文档</p>
              <p class="empty-hint">点击"上传文档"按钮添加文档</p>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="knowledgeTotalPages > 1" class="pagination">
            <button class="btn-page" :disabled="knowledgePage <= 1" @click="knowledgePage--">上一页</button>
            <span class="page-info">第 {{ knowledgePage }} / {{ knowledgeTotalPages }} 页</span>
            <button class="btn-page" :disabled="knowledgePage >= knowledgeTotalPages" @click="knowledgePage++">下一页</button>
          </div>
        </div>

        <!-- 系统概览 -->
        <div v-if="activeMenu === 'overview'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">系统概览</h2>
          </div>

          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon users">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon vip">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.vipUsers }}</div>
                <div class="stat-label">VIP用户</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon docs">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalDocs }}</div>
                <div class="stat-label">知识库文档</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon storage">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M2 20h20v-4H2v4zm2-3h2v2H4v-2zM2 4v4h20V4H2zm4 3H4V5h2v2zm-4 7h20v-4H2v4zm2-3h2v2H4v-2z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatFileSize(stats.totalStorage) }}</div>
                <div class="stat-label">存储空间</div>
              </div>
            </div>
          </div>

          <!-- 最近活动 -->
          <div class="recent-section">
            <h3 class="subsection-title">最近活动</h3>
            <div class="activity-list">
              <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
                <div class="activity-icon" :class="activity.type">
                  <svg v-if="activity.type === 'user'" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                  </svg>
                  <svg v-else-if="activity.type === 'doc'" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6z"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Token计量 -->
        <div v-if="activeMenu === 'tokens'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">Token消耗统计</h2>
          </div>

          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon tokens">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatTokenCount(tokenStats.totalTokens) }}</div>
                <div class="stat-label">系统总Token消耗</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon users">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ tokenStats.activeUserCount }}</div>
                <div class="stat-label">活跃用户数</div>
              </div>
            </div>
          </div>

          <div class="granularity-tabs">
            <div class="section-header">
              <h3 class="subsection-title">消耗趋势</h3>
            </div>
            <div class="tab-row">
              <button
                v-for="g in granularities"
                :key="g.key"
                class="tab-btn"
                :class="{ active: timeGranularity === g.key }"
                @click="switchGranularity(g.key)"
              >
                {{ g.label }}
              </button>
            </div>
            <div class="data-table-wrapper" v-loading="trendLoading">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>{{ timeGranularity === 'hourly' ? '小时' : timeGranularity === 'weekly' ? '周' : '日期' }}</th>
                    <th>Token消耗</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in trendData" :key="index">
                    <td>
                      <span class="time-slot">{{ item.timeSlot }}</span>
                    </td>
                    <td>
                      <div class="token-bar-cell">
                        <div
                          class="token-bar"
                          :style="{ width: getBarWidth(item.tokenCount) + '%' }"
                        ></div>
                        <span class="token-count">{{ formatTokenCount(item.tokenCount) }}</span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div v-if="trendData.length === 0 && !trendLoading" class="empty-state">
                <div class="empty-icon">📈</div>
                <p>暂无趋势数据</p>
              </div>
            </div>
          </div>

          <div class="data-table-wrapper" v-loading="tokenLoading">
            <div class="section-header">
              <h3 class="subsection-title">用户排行</h3>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>用户</th>
                  <th>邮箱</th>
                  <th>总Token消耗</th>
                  <th>活跃天数</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(userToken, index) in paginatedTokenUsers" :key="index">
                  <td>
                    <div class="user-cell">
                      <div class="user-info-cell">
                        <div class="user-name-cell">{{ userToken.username || '匿名用户' }}</div>
                        <div class="user-email-cell">{{ userToken.userId }}</div>
                      </div>
                    </div>
                  </td>
                  <td>{{ userToken.email || '-' }}</td>
                  <td>
                    <span class="token-count">{{ formatTokenCount(userToken.totalTokens) }}</span>
                  </td>
                  <td>{{ userToken.activeDays }} 天</td>
                  <td>
                    <button class="btn-action view" @click="viewTokenDetail(userToken)" title="查看Token明细">
                      <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                        <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                      </svg>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="tokenUsers.length === 0 && !tokenLoading" class="empty-state">
              <div class="empty-icon">💰</div>
              <p>暂无Token消耗数据</p>
              <p class="empty-hint">用户发送消息后将自动记录Token消耗</p>
            </div>
          </div>

          <div v-if="tokenTotalPages > 1" class="pagination">
            <button class="btn-page" :disabled="tokenPage <= 1" @click="tokenPage--">上一页</button>
            <span class="page-info">第 {{ tokenPage }} / {{ tokenTotalPages }} 页</span>
            <button class="btn-page" :disabled="tokenPage >= tokenTotalPages" @click="tokenPage++">下一页</button>
          </div>

          <div v-if="showTokenDetail" class="data-table-wrapper" style="margin-top: 32px;">
            <div class="section-header">
              <h3 class="subsection-title">
                Token消耗明细 - {{ tokenDetailUser.username || tokenDetailUser.userId }}
              </h3>
              <div class="tab-row">
                <button
                  v-for="g in granularities"
                  :key="g.key"
                  class="tab-btn tab-btn-sm"
                  :class="{ active: detailGranularity === g.key }"
                  @click="switchDetailGranularity(g.key)"
                >
                  {{ g.label }}
                </button>
              </div>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>{{ detailGranularity === 'hourly' ? '小时' : detailGranularity === 'weekly' ? '周' : '日期' }}</th>
                  <th>Token消耗</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(detail, index) in tokenDetails" :key="index">
                  <td>{{ detail.timeSlot || detail.usageDate }}</td>
                  <td>
                    <span class="token-count">{{ formatTokenCount(detail.tokenCount) }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>

    <!-- 添加用户弹窗 -->
    <div v-if="showAddUserDialog" class="modal-overlay" @click="showAddUserDialog = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">添加新用户</h3>
          <button class="btn-close" @click="showAddUserDialog = false">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">用户名 <span class="required">*</span></label>
            <input
              v-model="newUser.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              @keyup.enter="handleAddUser"
            />
          </div>
          <div class="form-group">
            <label class="form-label">邮箱 <span class="required">*</span></label>
            <input
              v-model="newUser.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱"
              @keyup.enter="handleAddUser"
            />
          </div>
          <div class="form-group">
            <label class="form-label">角色</label>
            <select v-model="newUser.role" class="form-select">
              <option value="USER">普通用户</option>
              <option value="VIP">VIP用户</option>
              <option value="ADMIN">管理员</option>
            </select>
          </div>
          <div class="form-tip">
            <span class="tip-icon">💡</span>
            <span>新用户默认密码为：123456</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddUserDialog = false">取消</button>
          <button class="btn-confirm" @click="handleAddUser" :disabled="addUserLoading">
            {{ addUserLoading ? '添加中...' : '确认添加' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 上传文档弹窗 -->
    <div v-if="showUploadDialog" class="modal-overlay" @click="showUploadDialog = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>上传知识库文档</h3>
          <button class="btn-close" @click="showUploadDialog = false">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>选择文件 <span class="required">*</span></label>
            <div class="upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleFileDrop">
              <input
                type="file"
                ref="fileInputRef"
                accept=".pdf,.doc,.docx,.md,.txt,.html,.htm"
                @change="handleFileSelect"
                class="file-input"
              />
              <div class="upload-hint">
                <div class="upload-icon">📁</div>
                <p>点击或拖拽文件到此处上传</p>
                <p class="upload-tip">支持 PDF、Word、Markdown、TXT、HTML 格式，单个文件不超过 10MB</p>
              </div>
            </div>
            <!-- 已选文件 -->
            <div v-if="selectedFile" class="selected-files">
              <div class="selected-file">
                <span class="file-icon">{{ getFileTypeIcon(selectedFile.name) }}</span>
                <span class="file-name">{{ selectedFile.name }}</span>
                <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
                <button class="btn-remove-file" @click="clearSelectedFile">✕</button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showUploadDialog = false">取消</button>
          <button class="btn-confirm" @click="handleUpload" :disabled="uploadLoading || !selectedFile">
            {{ uploadLoading ? '上传中...' : '确认上传' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 文档详情弹窗 -->
    <div v-if="showDocDetailDialog" class="modal-overlay" @click="showDocDetailDialog = false">
      <div class="modal-content modal-large" @click.stop>
        <div class="modal-header">
          <h3>文档详情</h3>
          <button class="btn-close" @click="showDocDetailDialog = false">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
        <div class="modal-body" v-if="currentDoc">
          <div class="doc-detail-info">
            <div class="detail-item">
              <span class="detail-label">文件名：</span>
              <span class="detail-value">{{ currentDoc.fileName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">文件类型：</span>
              <span class="detail-value">{{ getFileTypeName(currentDoc.fileName) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">文件大小：</span>
              <span class="detail-value">{{ formatFileSize(currentDoc.fileSize) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">所属用户：</span>
              <span class="detail-value">{{ currentDoc.username || currentDoc.userId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">上传时间：</span>
              <span class="detail-value">{{ formatDate(currentDoc.uploadTime) }}</span>
            </div>
            <div class="detail-item" v-if="currentDoc.docId">
              <span class="detail-label">文档ID：</span>
              <span class="detail-value doc-id">{{ currentDoc.docId }}</span>
            </div>
          </div>
          <div class="doc-preview" v-if="currentDoc.content">
            <h4>内容预览</h4>
            <div class="preview-content">{{ currentDoc.content.substring(0, 1000) }}...</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showDocDetailDialog = false">关闭</button>
          <button class="btn-confirm" @click="downloadDoc(currentDoc)">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
            </svg>
            下载文档
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useSettingsStore } from '../stores/settings'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import { getAllUsers, updateUserRole as apiUpdateUserRole, updateUserStatus as apiUpdateUserStatus, deleteUser as apiDeleteUser, createUserByAdmin, getTokenSummary, getUserTokenDetail, getDailyTokenSummary, getHourlyTokenSummary, getWeeklyTokenSummary, getUserHourlyTokenDetail, getUserWeeklyTokenDetail } from '@/api/user'
import { getAllKnowledgeDocs, deleteKnowledgeDoc, batchDeleteKnowledgeDocs, getKnowledgeStats, downloadKnowledgeDoc } from '@/api/knowledge'
import { uploadRagDocument } from '@/api/rag'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const settingsStore = useSettingsStore()

// 菜单
const menuItems = [
  { key: 'overview', label: '系统概览', icon: '📊' },
  { key: 'users', label: '用户管理', icon: '/images/Users.svg', isImage: true },
  { key: 'knowledge', label: '知识库管理', icon: '📚' },
  { key: 'tokens', label: 'Token计量', icon: '💰' },
]

const activeMenu = ref('overview')

// 用户管理
const userSearchQuery = ref('')
const userPage = ref(1)

// 用户数据
const users = ref([])
const userLoading = ref(false)

// 添加用户弹窗
const showAddUserDialog = ref(false)
const addUserLoading = ref(false)
const newUser = ref({
  username: '',
  email: '',
  role: 'USER'
})

const filteredUsers = computed(() => {
  if (!userSearchQuery.value) return users.value
  const query = userSearchQuery.value.toLowerCase()
  return users.value.filter(user =>
    user.username.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  )
})

// 加载用户列表
const loadUsers = async () => {
  userLoading.value = true
  try {
    const response = await getAllUsers()
    if (response.data.code === 200) {
      users.value = response.data.data || []
    } else {
      console.error('加载用户列表失败:', response.data.message)
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    userLoading.value = false
  }
}

// 知识库管理
const knowledgeSearchQuery = ref('')
const knowledgePage = ref(1)
const knowledgePageSize = ref(10)
const knowledgeLoading = ref(false)
const knowledgeDocs = ref([])
const knowledgeTotal = ref(0)

// 批量操作
const isBatchMode = ref(false)
const selectedDocs = ref(new Set())

// 上传弹窗
const showUploadDialog = ref(false)
const uploadLoading = ref(false)
const selectedFile = ref(null)
const fileInputRef = ref(null)

// 文档详情弹窗
const showDocDetailDialog = ref(false)
const currentDoc = ref(null)

// 过滤后的知识库文档
const filteredKnowledgeDocs = computed(() => {
  if (!knowledgeSearchQuery.value) return knowledgeDocs.value
  const query = knowledgeSearchQuery.value.toLowerCase()
  return knowledgeDocs.value.filter(doc =>
    doc.fileName?.toLowerCase().includes(query) ||
    doc.username?.toLowerCase().includes(query)
  )
})

// 分页后的文档
const paginatedKnowledgeDocs = computed(() => {
  const start = (knowledgePage.value - 1) * knowledgePageSize.value
  const end = start + knowledgePageSize.value
  return filteredKnowledgeDocs.value.slice(start, end)
})

// 总页数
const knowledgeTotalPages = computed(() => {
  return Math.ceil(filteredKnowledgeDocs.value.length / knowledgePageSize.value)
})

// 统计数据
const stats = ref({
  totalUsers: 128,
  vipUsers: 32,
  totalDocs: 456,
  totalStorage: 1024 * 1024 * 1024 * 2.5, // 2.5GB
})

// 最近活动
const recentActivities = ref([
  { type: 'user', text: '新用户 wangwu 注册成功', time: '5分钟前' },
  { type: 'doc', text: 'zhangsan 上传了文档 "产品手册.pdf"', time: '10分钟前' },
  { type: 'user', text: '用户 lisi 升级为 VIP', time: '30分钟前' },
  { type: 'doc', text: 'wangwu 删除了文档 "临时文件.txt"', time: '1小时前' },
])

// 方法
const goToLanding = () => {
  router.push('/')
}

const navigateToUpgradeVIP = () => {
  router.push('/upgrade-vip')
}

const handleLogout = () => {
  // 仅返回首页，保持登录状态
  router.push('/')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 用户操作
const updateUserRole = async (user) => {
  try {
    const response = await apiUpdateUserRole(user.userId, user.role)
    if (response.data.code === 200) {
      console.log('更新用户角色成功:', user.userId, user.role)
    } else {
      console.error('更新用户角色失败:', response.data.message)
      // 恢复原值
      await loadUsers()
    }
  } catch (error) {
    console.error('更新用户角色失败:', error)
    await loadUsers()
  }
}

const updateUserStatus = async (user) => {
  try {
    const response = await apiUpdateUserStatus(user.userId, user.status)
    if (response.data.code === 200) {
      console.log('更新用户状态成功:', user.userId, user.status)
    } else {
      console.error('更新用户状态失败:', response.data.message)
      // 恢复原值
      await loadUsers()
    }
  } catch (error) {
    console.error('更新用户状态失败:', error)
    await loadUsers()
  }
}

const viewUserDetail = (user) => {
  console.log('查看用户详情:', user.userId)
  // TODO: 打开详情弹窗
}

const deleteUser = async (user) => {
  if (confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`)) {
    try {
      const response = await apiDeleteUser(user.userId)
      if (response.data.code === 200) {
        console.log('删除用户成功:', user.userId)
        // 重新加载用户列表
        await loadUsers()
      } else {
        console.error('删除用户失败:', response.data.message)
        alert('删除用户失败: ' + response.data.message)
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      alert('删除用户失败，请稍后重试')
    }
  }
}

// 添加用户
const handleAddUser = async () => {
  // 表单验证
  if (!newUser.value.username.trim()) {
    alert('请输入用户名')
    return
  }
  if (!newUser.value.email.trim()) {
    alert('请输入邮箱')
    return
  }
  // 简单的邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(newUser.value.email.trim())) {
    alert('请输入有效的邮箱地址')
    return
  }

  addUserLoading.value = true
  try {
    const response = await createUserByAdmin({
      username: newUser.value.username.trim(),
      email: newUser.value.email.trim(),
      role: newUser.value.role
    })
    if (response.data.code === 200) {
      console.log('添加用户成功:', response.data.data)
      // 关闭弹窗
      showAddUserDialog.value = false
      // 重置表单
      newUser.value = { username: '', email: '', role: 'USER' }
      // 重新加载用户列表
      await loadUsers()
      alert('用户添加成功！默认密码为：123456')
    } else {
      console.error('添加用户失败:', response.data.message)
      alert('添加用户失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('添加用户失败:', error)
    alert('添加用户失败，请稍后重试')
  } finally {
    addUserLoading.value = false
  }
}

// 加载知识库文档列表
const loadKnowledgeDocs = async () => {
  knowledgeLoading.value = true
  try {
    const response = await getAllKnowledgeDocs()
    if (response.data.code === 200) {
      knowledgeDocs.value = response.data.data || []
      knowledgeTotal.value = knowledgeDocs.value.length
    } else {
      ElMessage.error(response.data.message || '加载知识库文档失败')
    }
  } catch (error) {
    console.error('加载知识库文档失败:', error)
    ElMessage.error('加载知识库文档失败')
  } finally {
    knowledgeLoading.value = false
  }
}

// 加载知识库统计
const loadKnowledgeStats = async () => {
  try {
    const response = await getKnowledgeStats()
    if (response.data.code === 200) {
      const data = response.data.data
      stats.value.totalDocs = data.totalDocs || 0
      stats.value.totalStorage = data.totalStorage || 0
    }
  } catch (error) {
    console.error('加载知识库统计失败:', error)
  }
}

// 切换批量模式
const toggleBatchMode = () => {
  isBatchMode.value = !isBatchMode.value
  selectedDocs.value.clear()
}

// 选择/取消选择文档
const toggleDocSelect = (doc) => {
  const key = `${doc.userId}_${doc.fileName}`
  if (selectedDocs.value.has(key)) {
    selectedDocs.value.delete(key)
  } else {
    selectedDocs.value.add(key)
  }
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (selectedDocs.value.size === paginatedKnowledgeDocs.value.length) {
    selectedDocs.value.clear()
  } else {
    paginatedKnowledgeDocs.value.forEach(doc => {
      selectedDocs.value.add(`${doc.userId}_${doc.fileName}`)
    })
  }
}

// 查看文档详情
const viewDocDetail = (doc) => {
  currentDoc.value = doc
  showDocDetailDialog.value = true
}

// 下载文档
const downloadDoc = async (doc) => {
  try {
    const response = await downloadKnowledgeDoc(doc.userId, doc.fileName)
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = doc.fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载文档失败:', error)
    ElMessage.error('下载文档失败')
  }
}

// 删除文档
const deleteDoc = async (doc) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文档 "${doc.fileName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteKnowledgeDoc(doc.userId, doc.fileName)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      await loadKnowledgeDocs()
      await loadKnowledgeStats()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文档失败:', error)
      ElMessage.error('删除文档失败')
    }
  }
}

// 批量删除
const batchDeleteDocs = async () => {
  if (selectedDocs.value.size === 0) {
    ElMessage.warning('请先选择要删除的文档')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedDocs.value.size} 个文档吗？此操作不可恢复！`,
      '确认批量删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const docsToDelete = []
    selectedDocs.value.forEach(key => {
      const [userId, fileName] = key.split('_')
      docsToDelete.push({ userId, fileName })
    })

    const response = await batchDeleteKnowledgeDocs(docsToDelete)
    if (response.data.code === 200) {
      ElMessage.success('批量删除成功')
      selectedDocs.value.clear()
      isBatchMode.value = false
      await loadKnowledgeDocs()
      await loadKnowledgeStats()
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 触发文件选择
const triggerFileInput = () => {
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    validateAndSetFile(file)
  }
}

// 处理文件拖拽
const handleFileDrop = (event) => {
  const file = event.dataTransfer.files[0]
  if (file) {
    validateAndSetFile(file)
  }
}

// 验证并设置文件
const validateAndSetFile = (file) => {
  const allowedTypes = ['.pdf', '.doc', '.docx', '.md', '.txt', '.html', '.htm']
  const ext = '.' + file.name.split('.').pop().toLowerCase()

  if (!allowedTypes.includes(ext)) {
    ElMessage.error('不支持的文件格式，请上传 PDF、Word、Markdown、TXT 或 HTML 文件')
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 10MB')
    return
  }

  selectedFile.value = file
}

// 清除选中文件
const clearSelectedFile = () => {
  selectedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

// 上传文档
const handleUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  const userId = authStore.user?.userId || authStore.user?.id
  if (!userId) {
    ElMessage.error('用户ID未获取到，请重新登录后重试')
    return
  }

  uploadLoading.value = true
  try {
    const response = await uploadRagDocument(selectedFile.value, userId)
    if (response.data.code === 200) {
      ElMessage.success('上传成功')
      showUploadDialog.value = false
      selectedFile.value = null
      await loadKnowledgeDocs()
      await loadKnowledgeStats()
    } else {
      throw new Error(response.data.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '上传失败')
  } finally {
    uploadLoading.value = false
  }
}

// 获取文件类型图标
const getFileTypeIcon = (fileName) => {
  const ext = fileName?.split('.').pop()?.toLowerCase()
  const iconMap = {
    pdf: '📄',
    doc: '📝',
    docx: '📝',
    md: '📑',
    txt: '📃',
    html: '🌐',
    htm: '🌐',
  }
  return iconMap[ext] || '📎'
}

// 获取文件类型名称
const getFileTypeName = (fileName) => {
  const ext = fileName?.split('.').pop()?.toLowerCase()
  const typeMap = {
    pdf: 'PDF',
    doc: 'Word',
    docx: 'Word',
    md: 'Markdown',
    txt: '文本',
    html: 'HTML',
    htm: 'HTML',
  }
  return typeMap[ext] || '未知类型'
}

// Token计量
const tokenUsers = ref([])
const tokenStats = ref({
  totalTokens: 0,
  activeUserCount: 0,
})
const tokenPage = ref(1)
const tokenPageSize = ref(10)
const tokenLoading = ref(false)
const showTokenDetail = ref(false)
const tokenDetailUser = ref(null)
const tokenDetails = ref([])

const granularities = [
  { key: 'daily', label: '按天' },
  { key: 'hourly', label: '按小时' },
  { key: 'weekly', label: '按周' },
]
const timeGranularity = ref('daily')
const detailGranularity = ref('daily')
const trendData = ref([])
const trendLoading = ref(false)
let maxTrendValue = 0

const paginatedTokenUsers = computed(() => {
  const start = (tokenPage.value - 1) * tokenPageSize.value
  return tokenUsers.value.slice(start, start + tokenPageSize.value)
})

const tokenTotalPages = computed(() => {
  return Math.ceil(tokenUsers.value.length / tokenPageSize.value)
})

const loadTokenSummary = async () => {
  tokenLoading.value = true
  try {
    const response = await getTokenSummary()
    if (response.data.code === 200) {
      const data = response.data.data
      tokenStats.value.totalTokens = data.totalTokens || 0
      tokenStats.value.activeUserCount = (data.userTokens || []).length
      tokenUsers.value = data.userTokens || []
    }
  } catch (error) {
    console.error('加载Token统计失败:', error)
  } finally {
    tokenLoading.value = false
  }
}

const loadTrendData = async () => {
  trendLoading.value = true
  try {
    let response
    if (timeGranularity.value === 'hourly') {
      response = await getHourlyTokenSummary()
    } else if (timeGranularity.value === 'weekly') {
      response = await getWeeklyTokenSummary()
    } else {
      response = await getDailyTokenSummary()
    }
    if (response.data.code === 200) {
      trendData.value = (response.data.data || []).reverse()
      maxTrendValue = Math.max(1, ...trendData.value.map(d => d.tokenCount || 0))
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  } finally {
    trendLoading.value = false
  }
}

const switchGranularity = (key) => {
  timeGranularity.value = key
  loadTrendData()
}

const switchDetailGranularity = async (key) => {
  detailGranularity.value = key
  if (!tokenDetailUser.value) return
  try {
    let response
    if (key === 'hourly') {
      response = await getUserHourlyTokenDetail(tokenDetailUser.value.userId)
    } else if (key === 'weekly') {
      response = await getUserWeeklyTokenDetail(tokenDetailUser.value.userId)
    } else {
      response = await getUserTokenDetail(tokenDetailUser.value.userId)
    }
    if (response.data.code === 200) {
      tokenDetails.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载Token明细失败:', error)
  }
}

const viewTokenDetail = async (userToken) => {
  tokenDetailUser.value = userToken
  showTokenDetail.value = true
  detailGranularity.value = 'daily'
  try {
    const response = await getUserTokenDetail(userToken.userId)
    if (response.data.code === 200) {
      tokenDetails.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载Token明细失败:', error)
  }
}

const getBarWidth = (count) => {
  if (!maxTrendValue || !count) return 0
  return Math.round((count / maxTrendValue) * 100)
}

const formatTokenCount = (count) => {
  if (!count) return '0'
  if (count >= 1000000) {
    return (count / 1000000).toFixed(1) + 'M'
  }
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'K'
  }
  return count.toLocaleString()
}

// 页面加载时获取用户列表
onMounted(() => {
  loadUsers()
  loadKnowledgeDocs()
  loadKnowledgeStats()
  loadTokenSummary()
  loadTrendData()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a0f 0%, #12121a 50%, #0d0d14 100%);
  color: #e2e8f0;
  position: relative;
  overflow-x: hidden;
}

/* 顶部导航 */
.admin-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(15, 15, 25, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
  z-index: 100;
}

.nav-content {
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}



.nav-divider {
  color: rgba(139, 92, 246, 0.3);
  font-weight: 300;
}

.nav-title {
  font-size: 14px;
  color: #9ca3af;
  font-weight: 500;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-upgrade-vip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
}

.btn-upgrade-vip:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(124, 58, 237, 0.4);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  color: #e2e8f0;
}

.btn-logout {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  color: #ef4444;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.5);
}

/* 主内容区 */
.admin-main {
  display: flex;
  padding-top: 64px;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

/* 侧边栏 */
.admin-sidebar {
  width: 220px;
  background: rgba(15, 15, 25, 0.6);
  border-right: 1px solid rgba(139, 92, 246, 0.1);
  padding: 24px 0;
  position: fixed;
  top: 64px;
  bottom: 0;
  left: 0;
}

.sidebar-menu {
  padding: 0 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 4px;
}

.menu-item:hover {
  background: rgba(139, 92, 246, 0.1);
}

.menu-item.active {
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
}

.menu-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.menu-icon-img {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.menu-text {
  font-size: 14px;
  color: #9ca3af;
  font-weight: 500;
}

.menu-item.active .menu-text {
  color: #e2e8f0;
}

/* 内容区 */
.admin-content {
  flex: 1;
  margin-left: 220px;
  padding: 32px;
  max-width: calc(100% - 220px);
}

.content-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 8px;
  min-width: 280px;
}

.search-box svg {
  color: #9ca3af;
  flex-shrink: 0;
}

.search-box input {
  background: transparent;
  border: none;
  outline: none;
  color: #e2e8f0;
  font-size: 14px;
  width: 100%;
}

.search-box input::placeholder {
  color: #6b7280;
}

/* 数据表格 */
.data-table-wrapper {
  background: rgba(15, 15, 25, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.loading-state {
  padding: 40px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  padding: 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: rgba(139, 92, 246, 0.05);
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.05);
  font-size: 14px;
  color: #e2e8f0;
}

.data-table tr:last-child td {
  border-bottom: none;
}

.data-table tr:hover td {
  background: rgba(139, 92, 246, 0.03);
}

/* 用户单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar-small {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name-cell {
  font-weight: 500;
  color: #fff;
}

.user-email-cell {
  font-size: 12px;
  color: #6b7280;
}

/* 角色选择 */
.role-select {
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  color: #e2e8f0;
  font-size: 13px;
  cursor: pointer;
  outline: none;
}

.role-select option {
  background: #1a1a2e;
  color: #e2e8f0;
}

/* 开关 */
.switch {
  position: relative;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  margin-right: 8px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: relative;
  width: 44px;
  height: 24px;
  background: rgba(100, 100, 120, 0.3);
  border-radius: 24px;
  transition: 0.3s;
}

.slider::before {
  content: '';
  position: absolute;
  width: 18px;
  height: 18px;
  background: #fff;
  border-radius: 50%;
  top: 3px;
  left: 3px;
  transition: 0.3s;
}

.switch input:checked + .slider {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
}

.switch input:checked + .slider::before {
  transform: translateX(20px);
}

.status-text {
  font-size: 13px;
  color: #6b7280;
}

.status-text.active {
  color: #10b981;
}

/* 操作按钮 */
.action-btns {
  display: flex;
  gap: 8px;
}

.btn-action {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.4);
  color: #e2e8f0;
}

.btn-action.delete:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  color: #ef4444;
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.btn-page {
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  color: #e2e8f0;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-page:hover:not(:disabled) {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.4);
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #9ca3af;
}

/* 文档单元格 */
.doc-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.doc-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 8px;
  color: #8b5cf6;
}

.doc-info-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.doc-name-cell {
  font-weight: 500;
  color: #fff;
}

.doc-type-cell {
  font-size: 12px;
  color: #6b7280;
}

/* 所有者单元格 */
.owner-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.owner-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.owner-name {
  font-size: 14px;
  color: #e2e8f0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: rgba(15, 15, 25, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-card:hover {
  border-color: rgba(139, 92, 246, 0.2);
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-icon.users {
  background: rgba(59, 130, 246, 0.1);
  color: var(--color-blue);
}

.stat-icon.vip {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.stat-icon.docs {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.stat-icon.storage {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.stat-icon.tokens {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

/* 最近活动 */
.recent-section {
  background: rgba(15, 15, 25, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 12px;
  padding: 24px;
}

.subsection-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 20px 0;
}

.granularity-tabs {
  margin-bottom: 24px;
}

.tab-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 6px 16px;
  background: rgba(139, 92, 246, 0.08);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  color: #9ca3af;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: rgba(139, 92, 246, 0.15);
  color: #c4b5fd;
}

.tab-btn.active {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
  color: #a78bfa;
}

.tab-btn-sm {
  padding: 4px 12px;
  font-size: 12px;
}

.time-slot {
  font-family: 'Courier New', monospace;
  color: #9ca3af;
  font-size: 13px;
}

.token-bar-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.token-bar {
  height: 6px;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  border-radius: 3px;
  min-width: 4px;
  transition: width 0.3s ease;
}

.token-count {
  color: #f59e0b;
  font-weight: 600;
  font-size: 13px;
  white-space: nowrap;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: rgba(139, 92, 246, 0.03);
  border-radius: 8px;
}

.activity-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  flex-shrink: 0;
}

.activity-icon.user {
  background: rgba(59, 130, 246, 0.1);
  color: var(--color-blue);
}

.activity-icon.doc {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.activity-icon.system {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #e2e8f0;
}

.activity-time {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* ===================
   浅色主题样式
   =================== */
.admin-dashboard.light-theme {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 50%, #e2e8f0 100%);
  color: #1e293b;
}

.light-theme .admin-nav {
  background: rgba(255, 255, 255, 0.9);
  border-bottom-color: rgba(139, 92, 246, 0.15);
}



.light-theme .nav-title {
  color: #64748b;
}

.light-theme .user-info {
  background: rgba(139, 92, 246, 0.08);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .user-name {
  color: #1e293b;
}

.light-theme .admin-sidebar {
  background: rgba(255, 255, 255, 0.8);
  border-right-color: rgba(139, 92, 246, 0.15);
}

.light-theme .menu-text {
  color: #64748b;
}

.light-theme .menu-item.active .menu-text {
  color: #1e293b;
}

.light-theme .section-title {
  color: #1e293b;
}

.light-theme .search-box {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .search-box input {
  color: #1e293b;
}

.light-theme .search-box input::placeholder {
  color: #94a3b8;
}

.light-theme .data-table-wrapper {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .data-table th {
  background: rgba(139, 92, 246, 0.05);
  color: #64748b;
  border-bottom-color: rgba(139, 92, 246, 0.15);
}

.light-theme .data-table td {
  color: #1e293b;
  border-bottom-color: rgba(139, 92, 246, 0.08);
}

.light-theme .user-name-cell,
.light-theme .doc-name-cell {
  color: #1e293b;
}

.light-theme .user-email-cell,
.light-theme .doc-type-cell {
  color: #64748b;
}

.light-theme .role-select {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
  color: #1e293b;
}

.light-theme .role-select option {
  background: #fff;
  color: #1e293b;
}

.light-theme .stat-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .stat-value {
  color: #1e293b;
}

.light-theme .stat-label {
  color: #64748b;
}

.light-theme .recent-section {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .subsection-title {
  color: #1e293b;
}

.light-theme .tab-btn {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
  color: #64748b;
}

.light-theme .tab-btn:hover {
  background: rgba(139, 92, 246, 0.1);
  color: #7c3aed;
}

.light-theme .tab-btn.active {
  background: rgba(139, 92, 246, 0.12);
  border-color: rgba(139, 92, 246, 0.4);
  color: #7c3aed;
}

.light-theme .time-slot {
  color: #64748b;
}

.light-theme .token-count {
  color: #d97706;
}

.light-theme .activity-text {
  color: #1e293b;
}

.light-theme .activity-time {
  color: #94a3b8;
}

.light-theme .btn-action {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
  color: #64748b;
}

.light-theme .btn-action:hover {
  background: rgba(139, 92, 246, 0.1);
  border-color: rgba(139, 92, 246, 0.3);
  color: #1e293b;
}

.light-theme .btn-page {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
  color: #1e293b;
}

.light-theme .page-info {
  color: #64748b;
}

.light-theme .owner-name {
  color: #1e293b;
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .nav-title {
    display: none;
  }

  .nav-divider {
    display: none;
  }
}

@media (max-width: 768px) {
  .admin-nav {
    padding: 0 12px;
  }

  .nav-right {
    gap: 8px;
  }

  .user-info {
    padding: 4px 8px;
  }

  .user-name {
    display: none;
  }

  .btn-logout span {
    display: none;
  }

  .admin-sidebar {
    width: 60px;
  }

  .menu-text {
    display: none;
  }

  .admin-content {
    margin-left: 60px;
    max-width: calc(100% - 60px);
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .section-title {
    font-size: 18px;
  }

  .search-box {
    width: 100%;
    min-width: auto;
  }

  .data-table {
    font-size: 13px;
  }

  .data-table th,
  .data-table td {
    padding: 10px 8px;
  }

  .user-avatar-small {
    width: 28px;
    height: 28px;
  }

  .btn-action {
    padding: 4px 8px;
    font-size: 12px;
  }

  .logs-grid {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .admin-sidebar {
    position: fixed;
    top: 64px;
    left: 0;
    right: auto;
    bottom: 0;
    width: 60px;
    height: auto;
    flex-direction: column;
    justify-content: flex-start;
    padding: 12px 0;
    z-index: 100;
    background: rgba(15, 15, 25, 0.95);
    border-right: 1px solid rgba(139, 92, 246, 0.1);
  }

  .sidebar-menu {
    flex-direction: column;
    width: 100%;
    padding: 0 8px;
  }

  .menu-item {
    flex: none;
    justify-content: center;
    padding: 12px 8px;
    border-left: 3px solid transparent;
    border-top: none;
    margin-bottom: 4px;
  }

  .menu-item.active {
    border-left-color: #8b5cf6;
    border-top-color: transparent;
  }

  .admin-content {
    margin-left: 60px;
    margin-bottom: 0;
    max-width: calc(100% - 60px);
    padding: 12px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card {
    padding: 12px;
  }

  .stat-value {
    font-size: 20px;
  }

  .stat-label {
    font-size: 12px;
  }

  .data-table {
    font-size: 12px;
  }

  .data-table th,
  .data-table td {
    padding: 8px 6px;
  }

  .role-badge,
  .status-badge {
    padding: 2px 6px;
    font-size: 11px;
  }

  .btn-add-user {
    padding: 6px 12px;
    font-size: 12px;
  }
}

/* 添加用户按钮 */
.btn-add-user {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add-user:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.4);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: linear-gradient(135deg, #1a1a2e 0%, #16162a 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #e2e8f0;
  margin: 0;
}

.btn-close {
  background: transparent;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-close:hover {
  color: #e2e8f0;
  background: rgba(139, 92, 246, 0.1);
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 8px;
}

.form-label .required {
  color: #ef4444;
}

.form-input,
.form-select {
  width: 100%;
  padding: 12px 16px;
  background: rgba(15, 15, 25, 0.8);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 10px;
  color: #e2e8f0;
  font-size: 14px;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.form-input::placeholder {
  color: #64748b;
}

.form-select {
  cursor: pointer;
}

.form-select option {
  background: #1a1a2e;
  color: #e2e8f0;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(251, 191, 36, 0.1);
  border: 1px solid rgba(251, 191, 36, 0.2);
  border-radius: 8px;
  font-size: 13px;
  color: #fbbf24;
}

.tip-icon {
  font-size: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 24px;
}

.btn-cancel {
  padding: 10px 20px;
  background: transparent;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  color: #94a3b8;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: rgba(139, 92, 246, 0.1);
  color: #e2e8f0;
}

.btn-confirm {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 浅色主题适配 */
.light-theme .modal-content {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .modal-title {
  color: #1e293b;
}

.light-theme .btn-close {
  color: #64748b;
}

.light-theme .btn-close:hover {
  color: #1e293b;
  background: rgba(139, 92, 246, 0.08);
}

.light-theme .form-label {
  color: #64748b;
}

.light-theme .form-input,
.light-theme .form-select {
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #1e293b;
}

.light-theme .form-input:focus,
.light-theme .form-select:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.light-theme .form-input::placeholder {
  color: #94a3b8;
}

.light-theme .form-select option {
  background: #fff;
  color: #1e293b;
}

.light-theme .btn-cancel {
  border-color: #e2e8f0;
  color: #64748b;
}

.light-theme .btn-cancel:hover {
  background: #f1f5f9;
  color: #1e293b;
}

/* 批量操作工具栏 */
.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 8px;
  margin-bottom: 16px;
}

.btn-batch {
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 6px;
  color: #a78bfa;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch:hover {
  background: rgba(139, 92, 246, 0.3);
}

.batch-count {
  min-width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 50%;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

.btn-batch-delete {
  padding: 6px 12px;
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 6px;
  color: #f87171;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch-delete:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.3);
}

.btn-batch-delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-batch-cancel {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid rgba(148, 163, 184, 0.3);
  border-radius: 6px;
  color: #94a3b8;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  margin-left: auto;
}

.btn-batch-cancel:hover {
  border-color: rgba(148, 163, 184, 0.5);
  color: #e2e8f0;
}

/* 复选框样式 */
.checkbox {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(139, 92, 246, 0.4);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #fff;
  font-size: 12px;
}

.checkbox:hover {
  border-color: rgba(139, 92, 246, 0.6);
}

.checkbox.checked {
  background: linear-gradient(135deg, #8b5cf6 0%, var(--color-primary) 100%);
  border-color: transparent;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #64748b;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 13px;
  color: #475569;
}

/* 批量管理模式按钮 */
.btn-batch-mode {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(59, 130, 246, 0.2);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 8px;
  color: #60a5fa;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-batch-mode:hover {
  background: rgba(59, 130, 246, 0.3);
}

.btn-batch-mode.active {
  background: rgba(59, 130, 246, 0.4);
  border-color: rgba(59, 130, 246, 0.5);
}

/* 上传区域 */
.upload-area {
  position: relative;
  border: 2px dashed rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.upload-area:hover {
  border-color: rgba(139, 92, 246, 0.5);
  background: rgba(139, 92, 246, 0.05);
}

.file-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.upload-hint {
  pointer-events: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.upload-hint p {
  color: #e2e8f0;
  font-size: 14px;
  margin-bottom: 4px;
}

.upload-tip {
  color: #64748b;
  font-size: 12px;
}

/* 已选文件列表 */
.selected-files {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 8px;
}

.file-icon {
  font-size: 20px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #e2e8f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #64748b;
}

.btn-remove-file {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(239, 68, 68, 0.2);
  border: none;
  border-radius: 4px;
  color: #f87171;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-file:hover {
  background: rgba(239, 68, 68, 0.3);
}

/* 文档详情 */
.modal-large {
  max-width: 600px;
}

.doc-detail-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  min-width: 80px;
  font-size: 14px;
  color: #94a3b8;
}

.detail-value {
  flex: 1;
  font-size: 14px;
  color: #e2e8f0;
}

.detail-value.doc-id {
  font-family: monospace;
  font-size: 12px;
  color: #64748b;
}

.doc-preview {
  border-top: 1px solid rgba(139, 92, 246, 0.2);
  padding-top: 20px;
}

.doc-preview h4 {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 12px;
}

.preview-content {
  padding: 16px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: #cbd5e1;
  max-height: 300px;
  overflow-y: auto;
}

/* 浅色主题适配 */
.light-theme .batch-toolbar {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .btn-batch {
  background: rgba(124, 58, 237, 0.1);
  border-color: rgba(124, 58, 237, 0.2);
  color: var(--color-primary);
}

.light-theme .btn-batch:hover {
  background: rgba(124, 58, 237, 0.2);
}

.light-theme .btn-batch-delete {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #dc2626;
}

.light-theme .btn-batch-delete:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.2);
}

.light-theme .btn-batch-cancel {
  border-color: rgba(148, 163, 184, 0.3);
  color: #64748b;
}

.light-theme .btn-batch-cancel:hover {
  border-color: rgba(148, 163, 184, 0.5);
  color: #475569;
}

.light-theme .checkbox {
  border-color: rgba(139, 92, 246, 0.3);
}

.light-theme .checkbox:hover {
  border-color: rgba(139, 92, 246, 0.5);
}

.light-theme .empty-state {
  color: #64748b;
}

.light-theme .empty-hint {
  color: #94a3b8;
}

.light-theme .btn-batch-mode {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #2563eb;
}

.light-theme .btn-batch-mode:hover {
  background: rgba(59, 130, 246, 0.2);
}

.light-theme .upload-area {
  border-color: rgba(139, 92, 246, 0.2);
}

.light-theme .upload-area:hover {
  border-color: rgba(139, 92, 246, 0.4);
  background: rgba(139, 92, 246, 0.03);
}

.light-theme .upload-hint p {
  color: #1e293b;
}

.light-theme .upload-tip {
  color: #64748b;
}

.light-theme .selected-file {
  background: rgba(139, 92, 246, 0.05);
  border-color: rgba(139, 92, 246, 0.15);
}

.light-theme .file-name {
  color: #1e293b;
}

.light-theme .detail-label {
  color: #64748b;
}

.light-theme .detail-value {
  color: #1e293b;
}

.light-theme .preview-content {
  background: #f8fafc;
  border-color: rgba(139, 92, 246, 0.1);
  color: #475569;
}
</style>
