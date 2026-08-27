<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'
  import { taskApi } from '~/api/task'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '任务详情',
    },
  }))

  const task = ref<Task | null>(null)
  const loading = ref(true)
  const taskId = ref(0)

  const statusMap: Record<TaskStatus, { text: string; color: string }> = {
    completed: { color: '#07C160', text: '已完成' },
    in_progress: { color: '#2196f3', text: '进行中' },
    overdue: { color: '#f44336', text: '已逾期' },
    pending: { color: '#ff9800', text: '待处理' },
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
      task.value = await taskApi.getDetail(taskId.value)
    } catch (error) {
      console.error('加载任务详情失败:', error)
      uni.showToast({ icon: 'none', title: '加载失败' })
    } finally {
      loading.value = false
    }
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

  onLoad((options) => {
    taskId.value = Number(options?.id)
    loadTask()
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
          <view class="item-value">{{ task.deadline }}</view>
        </view>

        <view class="section-item" v-if="task.assigneeName">
          <view class="item-label">指派给</view>
          <view class="item-value">{{ task.assigneeName }}</view>
        </view>

        <view class="section-item">
          <view class="item-label">创建时间</view>
          <view class="item-value">{{ task.createdAt }}</view>
        </view>
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

      <!-- 操作按钮 -->
      <view class="detail-actions">
        <view
          class="action-btn primary"
          v-if="task.status !== 'completed'"
          @click="updateStatus('completed')"
        >
          标记完成
        </view>
        <view
          class="action-btn info"
          v-if="task.status === 'pending'"
          @click="updateStatus('in_progress')"
        >
          开始处理
        </view>
        <view class="action-btn warning" @click="goEdit">编辑</view>
        <view class="action-btn danger" @click="handleDelete">删除</view>
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

    &.info {
      background-color: #2196f3;
    }

    &.warning {
      background-color: #ff9800;
    }

    &.danger {
      background-color: #f44336;
    }
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
