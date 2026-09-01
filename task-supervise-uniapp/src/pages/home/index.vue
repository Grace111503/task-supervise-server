<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'
  import { taskApi } from '~/api/task'
  import { messageApi } from '~/api/message'
  import { useUserStore } from '~/store/user'
  import type { OrgUser } from '~/api/org'
  import { getToken } from '~/utils/http/auth'
  import { onMessage, offMessage, connectMessage } from '~/utils/websocket'
  definePage(() => ({
    layout: 'home',
  }))

  const userStore = useUserStore()
  const taskList = ref<Task[]>([])
  const loading = ref(false)
  const refreshing = ref(false)
  const currentPage = ref(1)
  const hasMore = ref(true)
  const activeTab = ref<string>('all')
  const stats = ref({ total: 0, pendingReceive: 0, inProgress: 0, pendingFeedback: 0, pendingAccept: 0, completed: 0, overdue: 0 })
  const unreadCount = ref(0)

  // 批量操作相关
  const isMultiSelect = ref(false)
  const selectedTasks = ref<number[]>([])
  const showAssigneePicker = ref(false)

  const selectAll = computed({
    get: () => selectedTasks.value.length === taskList.value.length && taskList.value.length > 0,
    set: (val: boolean) => {
      selectedTasks.value = val ? taskList.value.map(t => t.id) : []
    },
  })

  // 六状态筛选项
  const tabs: Array<{ key: string; label: string }> = [
    { key: 'all', label: '全部' },
    { key: '1', label: '待接收' },
    { key: '2', label: '进行中' },
    { key: '3', label: '待反馈' },
    { key: '4', label: '待验收' },
    { key: '5', label: '已完成' },
    { key: '6', label: '已逾期' },
  ]

  /** 根据角色显示不同标题 */
  const pageTitle = computed(() => {
    if (userStore.isAdmin) return '督办看板'
    if (userStore.isManager) return '部门任务'
    return '我的任务'
  })

  /** 空状态文案 */
  const emptyHint = computed(() => {
    if (userStore.hasManagePermission) return '点击右下角按钮创建新任务'
    return '暂无分配任务，请耐心等待'
  })

  const emptyText = computed(() => {
    if (userStore.isAdmin) return '公司暂无任务'
    if (userStore.isManager) return '部门暂无任务'
    return '暂无任务'
  })

  /** 检查登录状态 */
  function checkAuth() {
    if (!userStore.isLogin) {
      uni.showToast({ icon: 'none', title: '请先登录' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/index' })
      }, 500)
      return false
    }
    return true
  }

  async function loadTasks(isRefresh = false) {
    if (loading.value) return
    loading.value = true

    try {
      if (isRefresh) {
        currentPage.value = 1
        hasMore.value = true
      }

      if (!hasMore.value) return

      const params: any = {
        page: currentPage.value,
        pageSize: 10,
      }

      if (activeTab.value !== 'all') {
        // 将字符串状态码转换为数字传递给后端
        params.status = parseInt(activeTab.value, 10)
      }

      const res = await taskApi.getList(params)
      // 后端返回格式: { code, message, data: { list, page, pageSize, total } }
      const { list = [], total = 0 } = res?.data || res || {}

      if (isRefresh) {
        taskList.value = list
      } else {
        taskList.value.push(...list)
      }

      hasMore.value = taskList.value.length < total
      currentPage.value++
    } catch (error) {
      console.error('加载任务列表失败:', error)
    } finally {
      loading.value = false
      refreshing.value = false
    }
  }

  function onRefresh() {
    refreshing.value = true
    loadTasks(true)
  }

  function onReachBottom() {
    loadTasks()
  }

  function onTabChange(key: string) {
    activeTab.value = key
    loadTasks(true)
  }

  function goDetail(task: Task) {
    if (isMultiSelect.value) {
      toggleTask(task.id)
      return
    }
    uni.navigateTo({ url: `/pages/home/detail?id=${task.id}` })
  }

  function goEdit(task: Task) {
    uni.navigateTo({ url: `/pages/home/edit?id=${task.id}` })
  }

  async function handleDelete(task: Task) {
    uni.showModal({
      content: `确定要删除任务"${task.title}"吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await taskApi.delete(task.id)
            taskList.value = taskList.value.filter((t) => t.id !== task.id)
            uni.showToast({ icon: 'success', title: '已删除' })
          } catch (error) {
            uni.showToast({ icon: 'none', title: '删除失败' })
          }
        }
      },
      title: '确认删除',
    })
  }

  async function handleStatusChange(task: Task, status: TaskStatus) {
    try {
      await taskApi.updateStatus(task.id, status)
      const index = taskList.value.findIndex((t) => t.id === task.id)
      if (index !== -1) {
        taskList.value[index].status = status as TaskStatus
      }
      uni.showToast({ icon: 'success', title: '状态已更新' })
    } catch (error) {
      uni.showToast({ icon: 'none', title: '更新失败' })
    }
  }

  function goCreate() {
    uni.navigateTo({ url: '/pages/home/edit' })
  }

  /** 切换批量选择模式 */
  function toggleSelect() {
    isMultiSelect.value = !isMultiSelect.value
    if (!isMultiSelect.value) selectedTasks.value = []
  }

  /** 切换单个任务选中 */
  function toggleTask(taskId: number) {
    const index = selectedTasks.value.indexOf(taskId)
    if (index === -1) {
      selectedTasks.value.push(taskId)
    } else {
      selectedTasks.value.splice(index, 1)
    }
  }

  /** 批量分派确认 */
  function onBatchAssignConfirm(users: OrgUser[]) {
    if (!users.length || selectedTasks.value.length === 0) return
    showAssigneePicker.value = false

    uni.showModal({
      title: '批量分派',
      content: `确定将 ${selectedTasks.value.length} 个任务分派给 ${users[0].name} 吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await taskApi.batchAssign(selectedTasks.value, users[0].userId)
            uni.showToast({ icon: 'success', title: '分派成功' })
            selectedTasks.value = []
            isMultiSelect.value = false
            loadTasks(true)
            loadStats()
          } catch (error) {
            uni.showToast({ icon: 'none', title: '分派失败' })
          }
        }
      },
    })
  }

  /** Excel 批量导入 */
  function handleExcelImport() {
    // #ifdef H5
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = '.xlsx,.xls'
    input.onchange = async () => {
      const file = input.files?.[0]
      if (!file) return

      uni.showLoading({ title: '导入中...' })
      try {
        const tokenData = getToken()
        const formData = new FormData()
        formData.append('file', file)
        const response = await fetch('http://localhost:8082/api/v1/task/batch-import', {
          method: 'POST',
          headers: { Authorization: `Bearer ${tokenData?.accessToken || ''}` },
          body: formData,
        })
        const result = await response.json()
        if (result.code === 200 || result.code === 0) {
          const data = result.data || {}
          uni.showToast({ icon: 'success', title: `成功${data.success || 0}条，失败${data.fail || 0}条` })
        } else {
          uni.showToast({ icon: 'none', title: result.message || '导入失败' })
        }
        loadTasks(true)
        loadStats()
      } catch (error: any) {
        uni.showToast({ icon: 'none', title: error.message || '导入失败' })
      } finally {
        uni.hideLoading()
      }
    }
    input.click()
    // #endif
    // #ifndef H5
    uni.chooseFile({
      type: 'all' as any,
      extension: ['.xlsx', '.xls'],
      success: async (res) => {
        const file = (res as any).tempFiles?.[0] || (res as any).tempFilePaths?.[0]
        if (!file) return

        uni.showLoading({ title: '导入中...' })
        try {
          const formData = new FormData()
          formData.append('file', file as any)

          // 使用 uni.uploadFile 上传
          const tokenData = getToken()
          await new Promise<void>((resolve, reject) => {
            uni.uploadFile({
              url: 'http://localhost:8082/api/v1/task/batch-import',
              filePath: file.path || (file as any).url,
              name: 'file',
              header: {
                Authorization: `Bearer ${tokenData?.accessToken || ''}`,
              },
              success: (uploadRes) => {
                if (uploadRes.statusCode === 200) {
                  const data = JSON.parse(uploadRes.data)
                  if (data.code === 200 || data.code === 0) {
                    const result = data.data || {}
                    uni.showToast({
                      icon: 'success',
                      title: `成功${result.success || 0}条，失败${result.fail || 0}条`,
                    })
                    resolve()
                  } else {
                    reject(new Error(data.message || '导入失败'))
                  }
                } else {
                  reject(new Error('上传失败'))
                }
              },
              fail: (err) => reject(err),
            })
          })

          loadTasks(true)
          loadStats()
        } catch (error: any) {
          uni.showToast({ icon: 'none', title: error.message || '导入失败' })
        } finally {
          uni.hideLoading()
        }
      },
    })
    // #endif
  }

  function goMessage() {
    uni.navigateTo({ url: '/pages/message/index' })
  }

  function goOverdue() {
    uni.navigateTo({ url: '/pages/home/overdue' })
  }

  /** 加载未读消息数 */
  async function loadUnreadCount() {
    try {
      const res = await messageApi.getUnreadCount()
      const data = res?.data || res || {}
      unreadCount.value = data.count || data || 0
    } catch (error) {
      console.error('加载未读消息数失败:', error)
    }
  }

  /** 加载任务统计 */
  async function loadStats() {
    try {
      const res = await taskApi.getStatistics()
      // 后端返回格式: { code, message, data: { total, pending, inProgress, completed, overdue } }
      const data = res?.data || res || {}
      if (data) {
        stats.value = {
          total: data.total || 0,
          pendingReceive: data.pendingReceive || 0,
          inProgress: data.inProgress || 0,
          pendingFeedback: data.pendingFeedback || 0,
          pendingAccept: data.pendingAccept || 0,
          completed: data.completed || 0,
          overdue: data.overdue || 0,
        }
      }
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  }

  onShow(() => {
    if (checkAuth()) {
      loadTasks(true)
      loadStats()
      loadUnreadCount()
    }
  })

  /** WebSocket 消息回调：实时更新未读数 */
  function handleWsMessage(msg: { type: string; data: any }) {
    if (msg.type === 'new_message') {
      unreadCount.value++
      // 显示 toast 提示
      const data = msg.data || {}
      uni.showToast({
        icon: 'none',
        title: data.title || '您有新消息',
        duration: 2000,
      })
    }
  }

  onMounted(() => {
    if (checkAuth()) {
      loadTasks(true)
      loadStats()
      loadUnreadCount()
      // 确保 WebSocket 已连接并监听
      if (userStore.userId) {
        connectMessage(userStore.userId)
      }
      onMessage(handleWsMessage)
    }
  })

  onUnmounted(() => {
    offMessage(handleWsMessage)
  })
</script>

<template>
  <view class="home-container">
    <!-- 顶部用户角色横幅 -->
    <view class="role-banner" v-if="userStore.isLogin">
      <view class="role-banner-left">
        <view class="role-avatar">{{ userStore.userInfo.name?.charAt(0) || 'U' }}</view>
        <view class="role-info">
          <text class="role-name">{{ userStore.userInfo.name }}</text>
          <view class="role-tags">
            <text class="role-tag" :class="'tag-' + userStore.userRole">
              {{ userStore.userRoleDesc }}
            </text>
            <text class="position-tag" v-if="userStore.userPosition">
              {{ userStore.userPosition }}
            </text>
          </view>
        </view>
      </view>
      <view class="role-banner-right">
        <view class="message-bell" @click="goMessage">
          <text class="bell-icon">🔔</text>
          <view class="unread-badge" v-if="unreadCount > 0">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </view>
        </view>
        <text class="role-count">{{ stats.total }}</text>
        <text class="role-count-label">任务</text>
      </view>
    </view>

    <!-- 六状态统计卡片 -->
    <scroll-view class="stats-cards" scroll-x v-if="userStore.isLogin">
      <view class="stats-cards-inner">
        <view class="stat-card stat-pending" @click="onTabChange('1')">
          <text class="stat-value">{{ stats.pendingReceive }}</text>
          <text class="stat-label">待接收</text>
        </view>
        <view class="stat-card stat-progress" @click="onTabChange('2')">
          <text class="stat-value">{{ stats.inProgress }}</text>
          <text class="stat-label">进行中</text>
        </view>
        <view class="stat-card stat-feedback" @click="onTabChange('3')">
          <text class="stat-value">{{ stats.pendingFeedback }}</text>
          <text class="stat-label">待反馈</text>
        </view>
        <view class="stat-card stat-accept" @click="onTabChange('4')">
          <text class="stat-value">{{ stats.pendingAccept }}</text>
          <text class="stat-label">待验收</text>
        </view>
        <view class="stat-card stat-completed" @click="onTabChange('5')">
          <text class="stat-value">{{ stats.completed }}</text>
          <text class="stat-label">已完成</text>
        </view>
        <view class="stat-card stat-overdue" @click="onTabChange('6')">
          <text class="stat-value">{{ stats.overdue }}</text>
          <text class="stat-label">已逾期</text>
        </view>
        <view class="stat-card stat-overdue" @click="goOverdue" v-if="stats.overdue > 0">
          <text class="stat-value">📋</text>
          <text class="stat-label">逾期处置</text>
        </view>
      </view>
    </scroll-view>

    <!-- 顶部导航 -->
    <NavBar
      is-need-left
      :base-props="{
        backgroundColor: 'transparent',
        border: false,
        fixed: false,
      }"
    >
      <template #left>
        <view class="nav-title">{{ pageTitle }}</view>
      </template>
    </NavBar>

    <!-- 标签筛选 -->
    <view class="tabs-wrapper">
      <scroll-view class="tabs-scroll" scroll-x>
        <view class="tabs">
          <view
            class="tab-item"
            v-for="tab in tabs"
            :key="tab.key"
            :class="{ active: activeTab === tab.key }"
            @click="onTabChange(tab.key)"
          >
            {{ tab.label }}
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 批量操作工具栏（管理权限可见） -->
    <view class="batch-bar" v-if="userStore.hasManagePermission && taskList.length > 0">
      <view class="batch-bar-left" v-if="isMultiSelect">
        <view class="select-all" @click="selectAll = !selectAll">
          <view class="checkbox" :class="{ checked: selectAll }">
            <text v-if="selectAll" class="check-mark">✓</text>
          </view>
          <text>全选 ({{ selectedTasks.length }}/{{ taskList.length }})</text>
        </view>
      </view>
      <view class="batch-bar-right">
        <view class="batch-toggle" @click="toggleSelect">
          {{ isMultiSelect ? '取消' : '批量选择' }}
        </view>
        <view class="batch-import-btn" @click="handleExcelImport" v-if="!isMultiSelect">
          📥 Excel导入
        </view>
      </view>
    </view>

    <!-- 批量分派操作栏 -->
    <view class="batch-assign-bar" v-if="isMultiSelect && selectedTasks.length > 0">
      <view class="batch-assign-btn" @click="showAssigneePicker = true">
        <text class="batch-assign-icon">👤</text>
        <text>批量分派 ({{ selectedTasks.length }})</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <scroll-view
      class="task-list"
      refresher-enabled
      scroll-y
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onReachBottom"
    >
      <EmptyState
        :hint="emptyHint"
        icon="📋"
        :text="emptyText"
        v-if="taskList.length === 0 && !loading"
      />

      <view class="task-items" v-else>
        <view
          class="task-item-wrapper"
          v-for="item in taskList"
          :key="item.id"
        >
          <view
            class="batch-checkbox"
            v-if="isMultiSelect"
            @click.stop="toggleTask(item.id)"
          >
            <view class="checkbox" :class="{ checked: selectedTasks.includes(item.id) }">
              <text v-if="selectedTasks.includes(item.id)" class="check-mark">✓</text>
            </view>
          </view>

          <TaskCard
            show-actions
            :task="item"
            :class="{ 'batch-selected': isMultiSelect && selectedTasks.includes(item.id) }"
            @click="goDetail(item)"
            @delete="handleDelete(item)"
            @edit="goEdit(item)"
            @status-change="(t, s) => handleStatusChange(t, s)"
          />
        </view>

        <view class="loading-more" v-if="loading">
          <wd-loading size="24px">加载中...</wd-loading>
        </view>
      </view>
    </scroll-view>

    <!-- 新建按钮：仅主管和管理员可见 -->
    <view class="fab-button" v-if="userStore.hasManagePermission && !isMultiSelect" @click="goCreate">
      <text class="fab-icon">+</text>
    </view>

    <!-- 执行人选择弹窗 -->
    <AssigneePicker
      :visible="showAssigneePicker"
      @confirm="onBatchAssignConfirm"
      @close="showAssigneePicker = false"
    />
  </view>
</template>

<style lang="scss" scoped>
  .home-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  /* 角色横幅 */
  .role-banner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #07c160 0%, #00a854 100%);
    padding: 24rpx 30rpx;
    color: #ffffff;
  }

  .role-banner-left {
    display: flex;
    align-items: center;
    gap: 20rpx;
  }

  .role-avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.25);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36rpx;
    font-weight: 600;
    color: #ffffff;
  }

  .role-info {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
  }

  .role-name {
    font-size: 32rpx;
    font-weight: 600;
    color: #ffffff;
  }

  .role-tags {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  .role-tag {
    font-size: 22rpx;
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    color: #ffffff;
    background-color: rgba(255, 255, 255, 0.25);

    &.tag-admin {
      background-color: rgba(255, 87, 87, 0.8);
    }

    &.tag-manager {
      background-color: rgba(255, 152, 0, 0.8);
    }

    &.tag-user {
      background-color: rgba(255, 255, 255, 0.25);
    }
  }

  .position-tag {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.85);
  }

  .role-banner-right {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8rpx;
  }

  .message-bell {
    position: relative;
    padding: 8rpx;
  }

  .bell-icon {
    font-size: 40rpx;
  }

  .unread-badge {
    position: absolute;
    top: 0;
    right: -4rpx;
    min-width: 28rpx;
    height: 28rpx;
    background-color: #f5222d;
    color: #ffffff;
    font-size: 18rpx;
    font-weight: 500;
    border-radius: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 6rpx;
  }

  .role-count {
    font-size: 40rpx;
    font-weight: 700;
    color: #ffffff;
  }

  .role-count-label {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.8);
  }

  /* 统计卡片 */
  .stats-cards {
    background-color: #ffffff;
    white-space: nowrap;
  }

  .stats-cards-inner {
    display: inline-flex;
    padding: 20rpx 24rpx;
    gap: 16rpx;
  }

  .stat-card {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20rpx 16rpx;
    min-width: 140rpx;
    border-radius: 16rpx;
    background-color: #f7f8fa;
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.95);
    }

    .stat-value {
      font-size: 40rpx;
      font-weight: 700;
      color: #333;
    }

    .stat-label {
      font-size: 22rpx;
      color: #666;
      margin-top: 4rpx;
    }

    &.stat-pending {
      background: linear-gradient(135deg, #f0f0f0 0%, #d9d9d9 100%);
      .stat-value { color: #8c8c8c; }
    }

    &.stat-progress {
      background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
      .stat-value { color: #1890ff; }
    }

    &.stat-feedback {
      background: linear-gradient(135deg, #fff7e6 0%, #ffe4b5 100%);
      .stat-value { color: #fa8c16; }
    }

    &.stat-accept {
      background: linear-gradient(135deg, #f9f0ff 0%, #d3adf7 100%);
      .stat-value { color: #722ed1; }
    }

    &.stat-completed {
      background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
      .stat-value { color: #52c41a; }
    }

    &.stat-overdue {
      background: linear-gradient(135deg, #fff1f0 0%, #ffccc7 100%);
      .stat-value { color: #f5222d; }
    }
  }

  .nav-title {
    font-size: 36rpx;
    font-weight: 600;
  }

  .tabs-wrapper {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .tabs-scroll {
    white-space: nowrap;
  }

  .tabs {
    display: inline-flex;
    padding: 20rpx 24rpx;
    gap: 24rpx;
  }

  .tab-item {
    display: inline-block;
    padding: 12rpx 32rpx;
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 32rpx;
    transition: all 0.3s ease;

    &.active {
      color: #ffffff;
      background-color: #07c160;
    }
  }

  .batch-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    padding: 16rpx 24rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .batch-bar-left {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  .batch-bar-right {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .select-all {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 26rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .checkbox {
    width: 36rpx;
    height: 36rpx;
    border: 2rpx solid var(--wot-border-color, #e5e6eb);
    border-radius: 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--wot-filled-oppo, #ffffff);

    &.checked {
      background-color: #07c160;
      border-color: #07c160;
    }
  }

  .check-mark {
    font-size: 22rpx;
    color: #ffffff;
  }

  .batch-toggle {
    font-size: 26rpx;
    color: #07c160;
    font-weight: 500;
    padding: 8rpx 16rpx;
  }

  .batch-import-btn {
    font-size: 24rpx;
    color: #1890ff;
    font-weight: 500;
    padding: 8rpx 16rpx;
    background-color: #e6f7ff;
    border-radius: 8rpx;
  }

  .batch-assign-bar {
    background-color: var(--wot-filled-oppo, #ffffff);
    padding: 16rpx 24rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .batch-assign-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    padding: 20rpx 0;
    background-color: #07c160;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #ffffff;
    font-weight: 500;
  }

  .batch-assign-icon {
    font-size: 32rpx;
  }

  .task-list {
    flex: 1;
    overflow: hidden;
  }

  .task-items {
    padding: 24rpx;
  }

  .task-item-wrapper {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
  }

  .batch-checkbox {
    flex-shrink: 0;
    padding-top: 30rpx;
  }

  .task-item-wrapper :deep(.task-card) {
    flex: 1;
  }

  .batch-selected {
    border: 2rpx solid #07c160;
  }

  .loading-more {
    display: flex;
    justify-content: center;
    padding: 30rpx 0;
  }

  .fab-button {
    position: fixed;
    right: 40rpx;
    bottom: 200rpx;
    width: 100rpx;
    height: 100rpx;
    background-color: #07c160;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.4);
  }

  .fab-icon {
    font-size: 48rpx;
    color: #ffffff;
    font-weight: 300;
  }
</style>
