<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'
  import { taskApi } from '~/api/task'
  import { feedbackApi } from '~/api/feedback'
  import type { ProgressFeedback, AssigneeProgress } from '~/api/feedback'
  import { useUserStore } from '@/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '任务详情',
    },
  }))

  const userStore = useUserStore()

  const task = ref<Task | null>(null)
  const loading = ref(true)
  const taskId = ref(0)
  const feedbackList = ref<ProgressFeedback[]>([])
  const loadingFeedback = ref(false)
  const timeline = ref<any[]>([])
  const loadingTimeline = ref(false)
  const assigneeProgressList = ref<AssigneeProgress[]>([])
  const loadingAssigneeProgress = ref(false)

  /** 是否为多人协办模式 */
  const isMultiMode = computed(() => {
    return task.value?.assigneeMode === 2 && (task.value?.multiAssigneeIds?.length ?? 0) > 0
  })

  /** 是否可以编辑（创建人 / 管理员 / 主管） */
  const canEdit = computed(() => {
    if (userStore.hasManagePermission) return true
    return task.value?.creatorId === userStore.userId
  })

  /** 是否可以删除（创建人 / 管理员 / 主管） */
  const canDelete = computed(() => {
    if (userStore.hasManagePermission) return true
    return task.value?.creatorId === userStore.userId
  })

  /** 是否可以标记完成（创建人 / 管理员 / 主管） */
  const canComplete = computed(() => {
    if (userStore.hasManagePermission) return true
    return task.value?.creatorId === userStore.userId
  })

  /** 是否为任务执行人（单人 + 多人协办，且不是创建人） */
  const isAssignee = computed(() => {
    if (!task.value) return false
    if (task.value.creatorId === userStore.userId) return false
    // 单人模式
    if (task.value.assigneeId === userStore.userId) return true
    // 多人协办模式
    if (task.value.multiAssigneeIds && task.value.multiAssigneeIds.length > 0) {
      return task.value.multiAssigneeIds.includes(userStore.userId)
    }
    return false
  })

  /** 是否可以提交反馈（仅执行人） */
  const canSubmitFeedback = computed(() => isAssignee.value)

  const statusMap: Record<string, { text: string; color: string }> = {
    completed: { color: '#07C160', text: '已完成' },
    in_progress: { color: '#1890ff', text: '进行中' },
    overdue: { color: '#f5222d', text: '已逾期' },
    pending: { color: '#8c8c8c', text: '待接收' },
    pending_feedback: { color: '#fa8c16', text: '待反馈' },
    pending_accept: { color: '#722ed1', text: '待验收' },
  }

  const priorityMap: Record<string, { text: string; color: string }> = {
    high: { color: '#f44336', text: '高' },
    low: { color: '#4caf50', text: '低' },
    medium: { color: '#ff9800', text: '中' },
  }

  async function loadTask() {
    if (!taskId.value) return
    loading.value = true
    try {
      const res = await taskApi.getDetail(taskId.value)
      // 后端返回格式: { code, message, data: Task }
      task.value = res?.data || res
    } catch (error) {
      console.error('加载任务详情失败:', error)
      uni.showToast({ icon: 'none', title: '加载失败' })
    } finally {
      loading.value = false
    }
  }

  async function loadFeedbackList() {
    if (!taskId.value) return
    loadingFeedback.value = true
    try {
      const res = await feedbackApi.listByTaskIdWithFiles(taskId.value)
      const data = res?.data || res || {}
      feedbackList.value = Array.isArray(data) ? data : (data.list || [])
    } catch (error) {
      console.error('加载反馈列表失败:', error)
    } finally {
      loadingFeedback.value = false
    }
  }

  async function loadTimeline() {
    if (!taskId.value) return
    loadingTimeline.value = true
    try {
      const res = await taskApi.getTimeline(taskId.value)
      const data = res?.data || res || {}
      timeline.value = Array.isArray(data) ? data : []
    } catch (error) {
      console.error('加载时间线失败:', error)
    } finally {
      loadingTimeline.value = false
    }
  }

  async function loadAssigneeProgress() {
    if (!taskId.value) return
    loadingAssigneeProgress.value = true
    try {
      const res = await feedbackApi.getAssigneeProgress(taskId.value)
      const data = res?.data || res || []
      assigneeProgressList.value = Array.isArray(data) ? data : []
    } catch (error) {
      console.error('加载执行人进度失败:', error)
    } finally {
      loadingAssigneeProgress.value = false
    }
  }

  /** 验收任务 */
  function handleAccept() {
    uni.showActionSheet({
      itemList: ['验收通过', '验收驳回'],
      success: (res) => {
        const acceptResult = res.tapIndex === 0 ? 1 : 2
        const title = acceptResult === 1 ? '验收通过' : '验收驳回'
        uni.showModal({
          title,
          editable: true,
          placeholderText: '请输入验收意见',
          success: async (modalRes) => {
            if (modalRes.confirm && task.value) {
              const remark = modalRes.content || ''
              try {
                await taskApi.accept(task.value.id, acceptResult, remark)
                if (acceptResult === 1) {
                  task.value.status = 'completed'
                } else {
                  task.value.status = 'in_progress'
                  task.value.rejectRemark = remark
                }
                task.value.acceptResult = acceptResult
                task.value.acceptRemark = remark
                uni.showToast({ icon: 'success', title: '验收完成' })
              } catch (error) {
                uni.showToast({ icon: 'none', title: '验收失败' })
              }
            }
          },
        })
      },
    })
  }

  async function updateStatus(status: TaskStatus) {
    if (!task.value) return
    try {
      await taskApi.updateStatus(task.value.id, status)
      task.value.status = status
      uni.showToast({ icon: 'success', title: '状态已更新' })
    } catch (error) {
      uni.showToast({ icon: 'none', title: '更新失败' })
    }
  }

  function goEdit() {
    uni.navigateTo({ url: `/pages/home/edit?id=${taskId.value}` })
  }

  function goSubmitFeedback() {
    uni.navigateTo({ url: `/pages/feedback/submit?taskId=${taskId.value}` })
  }

  function goFeedbackList() {
    uni.navigateTo({ url: `/pages/feedback/list?taskId=${taskId.value}` })
  }

  /** 驳回任务（管理员/主管） */
  function handleReject() {
    uni.showModal({
      title: '驳回任务',
      editable: true,
      placeholderText: '请输入驳回原因',
      success: async (res) => {
        if (res.confirm && task.value) {
          const remark = res.content || ''
          if (!remark.trim()) {
            uni.showToast({ icon: 'none', title: '请填写驳回原因' })
            return
          }
          try {
            await taskApi.reject(task.value.id, remark)
            task.value.status = 'in_progress'
            task.value.rejectRemark = remark
            task.value.acceptResult = 2
            uni.showToast({ icon: 'success', title: '已驳回' })
          } catch (error) {
            uni.showToast({ icon: 'none', title: '驳回失败' })
          }
        }
      },
    })
  }

  async function handleDelete() {
    uni.showModal({
      content: '确定要删除此任务吗？',
      success: async (res) => {
        if (res.confirm && task.value) {
          try {
            await taskApi.delete(task.value.id)
            uni.showToast({ icon: 'success', title: '已删除' })
            setTimeout(() => uni.navigateBack(), 1500)
          } catch (error) {
            uni.showToast({ icon: 'none', title: '删除失败' })
          }
        }
      },
      title: '确认删除',
    })
  }

  /** 格式化时间 */
  function formatTime(time: string): string {
    if (!time) return ''
    // 处理 "2026-09-18T18:00:00" 和 "2026-09-18 18:00:00" 两种格式
    const dateStr = time.replace('T', ' ')
    const date = new Date(dateStr.replace(/-/g, '/'))
    return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  }

  /** 格式化日期（详情页用） */
  function formatDate(time: string): string {
    if (!time) return ''
    return time.replace('T', ' ').replace(/\.\d+$/, '')
  }

  onShow(() => {
    if (taskId.value) {
      loadFeedbackList()
      loadAssigneeProgress()
    }
  })

  onLoad((options) => {
    taskId.value = Number(options?.id)
    loadTask()
    loadFeedbackList()
    loadTimeline()
    loadAssigneeProgress()
  })
</script>

<template>
  <view class="detail-container">
    <wd-loading v-if="loading" />

    <view class="detail-content" v-else-if="task">
      <!-- 任务标题和状态 -->
      <view class="detail-header">
        <view class="detail-title">{{ task.title }}</view>
        <view
          class="detail-status"
          :style="{ backgroundColor: statusMap[task.status]?.color }"
        >
          {{ statusMap[task.status]?.text }}
        </view>
      </view>

      <!-- 任务信息 -->
      <view class="detail-section">
        <view class="section-item">
          <view class="item-label">优先级</view>
          <view
            class="item-value"
            :style="{ color: priorityMap[task.priority]?.color }"
          >
            {{ priorityMap[task.priority]?.text }}
          </view>
        </view>

        <view class="section-item" v-if="task.deadline">
          <view class="item-label">截止时间</view>
          <view class="item-value">
            {{ formatDate(task.deadline) }}
            <text v-if="task.remainingDays !== null && task.remainingDays !== undefined"
                  :style="{ color: task.remainingDays <= 1 ? '#f5222d' : task.remainingDays <= 3 ? '#fa8c16' : '#52c41a', marginLeft: '12rpx', fontSize: '24rpx' }">
              {{ task.overdueDays && task.overdueDays > 0 ? '(逾期' + task.overdueDays + '天)' : task.remainingDays <= 0 ? '(今天到期)' : '(剩余' + task.remainingDays + '天)' }}
            </text>
          </view>
        </view>

        <view class="section-item" v-if="task.assigneeName">
          <view class="item-label">指派给</view>
          <view class="item-value">{{ task.assigneeName }}</view>
        </view>

        <view class="section-item">
          <view class="item-label">创建时间</view>
          <view class="item-value">{{ formatDate(task.createdAt) }}</view>
        </view>
      </view>

      <!-- 驳回标记 -->
      <view class="detail-section reject-remark" v-if="task.rejectRemark">
        <view class="section-title">⚠️ 驳回原因</view>
        <view class="section-desc reject-text">{{ task.rejectRemark }}</view>
        <view class="reject-time" v-if="task.rejectedAt">驳回时间：{{ formatDate(task.rejectedAt) }}</view>
      </view>

      <!-- 验收信息 -->
      <view class="detail-section accept-info" v-if="task.acceptResult && task.acceptResult !== 0">
        <view class="section-title">{{ task.acceptResult === 1 ? '✅ 验收通过' : '❌ 验收驳回' }}</view>
        <view class="section-desc" v-if="task.acceptRemark">{{ task.acceptRemark }}</view>
        <view class="reject-time" v-if="task.acceptedAt">验收时间：{{ formatDate(task.acceptedAt) }}</view>
      </view>

      <!-- 任务描述 -->
      <view class="detail-section" v-if="task.description">
        <view class="section-title">任务描述</view>
        <view class="section-desc">{{ task.description }}</view>
      </view>

      <!-- 备注 -->
      <view class="detail-section" v-if="task.remark">
        <view class="section-title">备注</view>
        <view class="section-desc">{{ task.remark }}</view>
      </view>

      <!-- 进度反馈记录 -->
      <view class="detail-section feedback-section">
        <view class="section-header">
          <view class="section-title">进度反馈 ({{ feedbackList.length }})</view>
          <view class="section-more" @click="goFeedbackList" v-if="feedbackList.length > 0">
            查看全部 >
          </view>
        </view>

        <view class="feedback-list" v-if="feedbackList.length > 0">
          <view class="feedback-item" v-for="item in feedbackList.slice(0, 3)" :key="item.feedbackId">
            <view class="feedback-header">
              <text class="feedback-user">{{ item.userName || '未知' }}</text>
              <text class="feedback-stage">第{{ item.stage }}阶段</text>
            </view>
            <text class="feedback-content">{{ item.completedContent }}</text>
            <text class="feedback-time">{{ formatTime(item.feedbackTime) }}</text>
          </view>
        </view>

        <view class="empty-feedback" v-else>
          <text>暂无反馈记录</text>
        </view>
      </view>

      <!-- 多人协办 - 执行人进度概览 -->
      <view class="detail-section" v-if="isMultiMode && assigneeProgressList.length > 0">
        <view class="section-title">👥 协办人员进度</view>
        <view class="assignee-progress-list">
          <view class="assignee-progress-item" v-for="item in assigneeProgressList" :key="item.userId">
            <view class="assignee-info">
              <view class="assignee-name">
                <text>{{ item.userName }}</text>
                <text class="assignee-type-tag" :class="item.assigneeType === 1 ? 'primary-tag' : 'normal-tag'">
                  {{ item.assigneeType === 1 ? '主责' : '协办' }}
                </text>
              </view>
              <text class="assignee-progress-value">{{ item.latestProgress ?? 0 }}%</text>
            </view>
            <view class="progress-bar-wrapper">
              <view
                class="progress-bar-fill"
                :style="{
                  width: (item.latestProgress ?? 0) + '%',
                  backgroundColor: (item.latestProgress ?? 0) >= 100 ? '#07C160' : (item.latestProgress ?? 0) >= 50 ? '#1890ff' : '#fa8c16'
                }"
              ></view>
            </view>
            <view class="assignee-latest" v-if="item.latestContent">
              <text class="latest-label">最新：</text>
              <text class="latest-content">{{ item.latestContent }}</text>
            </view>
            <view class="assignee-latest-time" v-if="item.latestTime">
              {{ formatTime(item.latestTime) }}
            </view>
          </view>
        </view>
      </view>

      <!-- 全流程时间线 -->
      <view class="detail-section" v-if="timeline.length > 0">
        <view class="section-title">📋 全流程记录</view>
        <view class="timeline">
          <view class="timeline-item" v-for="(item, index) in timeline" :key="index">
            <view class="timeline-dot" :class="'dot-' + item.type"></view>
            <view class="timeline-line" v-if="index < timeline.length - 1"></view>
            <view class="timeline-content">
              <view class="timeline-header">
                <text class="timeline-title">{{ item.title }}</text>
                <text class="timeline-time" v-if="item.time">{{ formatDate(item.time) }}</text>
              </view>
              <text class="timeline-desc" v-if="item.content">{{ item.content }}</text>
              <text class="timeline-operator" v-if="item.operator">{{ item.operator }}</text>
              <text class="timeline-progress" v-if="item.progress !== undefined">进度 {{ item.progress }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="detail-actions">
        <view
          class="action-btn success"
          v-if="task.status !== 'completed' && canSubmitFeedback"
          @click="goSubmitFeedback"
        >
          提交反馈
        </view>
        <view
          class="action-btn primary"
          v-if="task.status !== 'completed' && canComplete && !isAssignee"
          @click="updateStatus('completed')"
        >
          标记完成
        </view>
        <view
          class="action-btn info"
          v-if="task.status === 'pending' && isAssignee"
          @click="updateStatus('in_progress')"
        >
          开始处理
        </view>
        <view
          class="action-btn accept"
          v-if="task.status === 'pending_accept' && userStore.hasManagePermission"
          @click="handleAccept"
        >
          验收
        </view>
        <view
          class="action-btn reject"
          v-if="(task.status === 'completed' || task.status === 'pending_accept') && userStore.hasManagePermission"
          @click="handleReject"
        >
          驳回
        </view>
        <view class="action-btn warning" v-if="canEdit" @click="goEdit">编辑</view>
        <view class="action-btn danger" v-if="canDelete" @click="handleDelete">删除</view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">📋</view>
      <view class="empty-text">任务不存在</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .detail-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .detail-content {
    padding: 30rpx;
  }

  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 30rpx;
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
  }

  .detail-title {
    flex: 1;
    font-size: 36rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-right: 20rpx;
  }

  .detail-status {
    padding: 8rpx 20rpx;
    font-size: 24rpx;
    color: #ffffff;
    border-radius: 8rpx;
    white-space: nowrap;
  }

  .detail-section {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
  }

  .section-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

    &:last-child {
      border-bottom: none;
    }
  }

  .item-label {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
  }

  .item-value {
    font-size: 28rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
  }

  .section-title {
    font-size: 30rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 16rpx;
  }

  .section-desc {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    line-height: 1.6;
  }

  .detail-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
    margin-top: 30rpx;
  }

  .action-btn {
    flex: 1;
    min-width: calc(50% - 10rpx);
    padding: 24rpx 0;
    text-align: center;
    font-size: 30rpx;
    border-radius: 12rpx;
    color: #ffffff;

    &.primary {
      background-color: #07c160;
    }

    &.success {
      background-color: #52c41a;
    }

    &.info {
      background-color: #2196f3;
    }

    &.warning {
      background-color: #ff9800;
    }

    &.danger {
      background-color: #f44336;
    }

    &.reject {
      background-color: #ff7d00;
    }

    &.accept {
      background-color: #722ed1;
    }
  }

  /* 驳回标记样式 */
  .reject-remark {
    background-color: #fff7e6;
    border-left: 6rpx solid #ff7d00;
  }

  .reject-text {
    color: #d46b08;
  }

  .reject-time {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-top: 8rpx;
  }

  /* 反馈部分样式 */
  .feedback-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;
    }

    .section-title {
      margin-bottom: 0;
    }

    .section-more {
      font-size: 24rpx;
      color: #07c160;
    }
  }

  .feedback-list {
    .feedback-item {
      padding: 16rpx 0;
      border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

      &:last-child {
        border-bottom: none;
      }
    }

    .feedback-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8rpx;
    }

    .feedback-user {
      font-size: 26rpx;
      font-weight: 500;
      color: var(--wot-text-main, #1d2129);
    }

    .feedback-stage {
      font-size: 22rpx;
      padding: 2rpx 12rpx;
      border-radius: 16rpx;
      background-color: #e6f7ff;
      color: #1890ff;
    }

    .feedback-content {
      font-size: 26rpx;
      color: var(--wot-text-secondary, #4e5969);
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 8rpx;
    }

    .feedback-time {
      font-size: 22rpx;
      color: var(--wot-text-auxiliary, #869a9c);
    }
  }

  .empty-feedback {
    text-align: center;
    padding: 40rpx 0;
    font-size: 26rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  /* 验收信息 */
  .accept-info {
    background-color: #f6ffed;
    border-left: 6rpx solid #52c41a;
  }

  /* 时间线 */
  .timeline {
    padding: 16rpx 0 0 0;
  }

  .timeline-item {
    position: relative;
    padding-left: 40rpx;
    padding-bottom: 24rpx;
  }

  .timeline-dot {
    position: absolute;
    left: 0;
    top: 8rpx;
    width: 16rpx;
    height: 16rpx;
    border-radius: 50%;
    background-color: #1890ff;
    z-index: 1;

    &.dot-create { background-color: #52c41a; }
    &.dot-assign { background-color: #1890ff; }
    &.dot-feedback { background-color: #fa8c16; }
    &.dot-reject { background-color: #f5222d; }
    &.dot-accept { background-color: #722ed1; }
  }

  .timeline-line {
    position: absolute;
    left: 7rpx;
    top: 24rpx;
    bottom: 0;
    width: 2rpx;
    background-color: #e5e6eb;
  }

  .timeline-content {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  .timeline-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .timeline-title {
    font-size: 28rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
  }

  .timeline-time {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .timeline-desc {
    font-size: 26rpx;
    color: var(--wot-text-secondary, #4e5969);
    line-height: 1.5;
  }

  .timeline-operator {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .timeline-progress {
    font-size: 22rpx;
    color: #fa8c16;
    font-weight: 500;
  }

  /* 执行人进度概览 */
  .assignee-progress-list {
    margin-top: 16rpx;
  }

  .assignee-progress-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

    &:last-child {
      border-bottom: none;
    }
  }

  .assignee-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12rpx;
  }

  .assignee-name {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 28rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
  }

  .assignee-type-tag {
    font-size: 20rpx;
    padding: 2rpx 12rpx;
    border-radius: 8rpx;

    &.primary-tag {
      background-color: #fff1f0;
      color: #f5222d;
    }

    &.normal-tag {
      background-color: #e6f7ff;
      color: #1890ff;
    }
  }

  .assignee-progress-value {
    font-size: 28rpx;
    font-weight: 600;
    color: #1890ff;
  }

  .progress-bar-wrapper {
    height: 12rpx;
    background-color: #f0f0f0;
    border-radius: 6rpx;
    overflow: hidden;
    margin-bottom: 12rpx;
  }

  .progress-bar-fill {
    height: 100%;
    border-radius: 6rpx;
    transition: width 0.3s ease;
  }

  .assignee-latest {
    display: flex;
    align-items: flex-start;
    gap: 8rpx;
    margin-bottom: 8rpx;
  }

  .latest-label {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    flex-shrink: 0;
  }

  .latest-content {
    font-size: 24rpx;
    color: var(--wot-text-secondary, #4e5969);
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .assignee-latest-time {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 200rpx 0;
  }

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 24rpx;
  }

  .empty-text {
    font-size: 32rpx;
    color: var(--wot-text-main, #1d2129);
  }
</style>
