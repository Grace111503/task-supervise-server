<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'

  interface Props {
    showActions?: boolean
    task: Task
  }

  const props = withDefaults(defineProps<Props>(), {
    showActions: false,
  })

  const emit = defineEmits<{
    click: [task: Task]
    edit: [task: Task]
    delete: [task: Task]
    statusChange: [task: Task, status: TaskStatus]
  }>()

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

  /** 剩余工期文字 */
  const deadlineText = computed(() => {
    const task = props.task
    if (!task.deadline) return ''
    if (task.overdueDays && task.overdueDays > 0) {
      return `逾期 ${task.overdueDays} 天`
    }
    if (task.remainingDays !== undefined && task.remainingDays !== null) {
      if (task.remainingDays < 0) return `逾期 ${Math.abs(task.remainingDays)} 天`
      if (task.remainingDays === 0) return '今天到期'
      return `剩余 ${task.remainingDays} 天`
    }
    return ''
  })

  /** 工期颜色 */
  const deadlineColor = computed(() => {
    const task = props.task
    if (task.overdueDays && task.overdueDays > 0) return '#f5222d'
    if (task.remainingDays !== undefined && task.remainingDays !== null) {
      if (task.remainingDays <= 1) return '#f5222d'
      if (task.remainingDays <= 3) return '#fa8c16'
      return '#52c41a'
    }
    return '#8c8c8c'
  })

  function handleClick() {
    emit('click', props.task)
  }

  function handleEdit(e: Event) {
    e.stopPropagation()
    emit('edit', props.task)
  }

  function handleDelete(e: Event) {
    e.stopPropagation()
    emit('delete', props.task)
  }

  function handleStatusChange(status: TaskStatus) {
    emit('statusChange', props.task, status)
  }
</script>

<template>
  <view class="task-card" @click="handleClick">
    <view class="task-header">
      <view class="task-title">{{ task.taskName || task.title }}</view>
      <view
        class="task-status"
        :style="{ backgroundColor: statusMap[task.status]?.color }"
      >
        {{ statusMap[task.status]?.text }}
      </view>
    </view>

    <view class="task-desc" v-if="task.description">
      {{ task.description }}
    </view>

    <view class="task-footer">
      <view class="task-meta">
        <view
          class="task-priority"
          :style="{ color: priorityMap[task.priority]?.color }"
        >
          {{ priorityMap[task.priority]?.text }}优先级
        </view>
        <view class="task-deadline" v-if="deadlineText" :style="{ color: deadlineColor }">
          {{ deadlineText }}
        </view>
        <view class="task-feedback-count" v-if="task.feedbackCount && task.feedbackCount > 0">
          📝 {{ task.feedbackCount }}次反馈
        </view>
      </view>
      <view class="task-assignee" v-if="task.assigneeName">
        {{ task.assigneeName }}
      </view>
    </view>

    <view class="task-actions" v-if="showActions">
      <view
        class="action-btn success"
        v-if="task.status !== 'completed'"
        @click.stop="handleStatusChange('completed')"
      >
        完成
      </view>
      <view
        class="action-btn info"
        v-if="task.status === 'pending'"
        @click.stop="handleStatusChange('in_progress')"
      >
        开始
      </view>
      <view class="action-btn warning" @click.stop="handleEdit">编辑</view>
      <view class="action-btn danger" @click.stop="handleDelete">删除</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .task-card {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx var(--wot-opac-2_04, rgba(0, 0, 0, 0.04));
  }

  .task-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16rpx;
  }

  .task-title {
    flex: 1;
    font-size: 32rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-right: 16rpx;
  }

  .task-status {
    padding: 6rpx 16rpx;
    font-size: 22rpx;
    color: #ffffff;
    border-radius: 8rpx;
    white-space: nowrap;
  }

  .task-desc {
    font-size: 26rpx;
    color: var(--wot-text-secondary, #4e5969);
    margin-bottom: 16rpx;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .task-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .task-meta {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .task-priority {
    font-size: 24rpx;
    font-weight: 500;
  }

  .task-deadline {
    font-size: 24rpx;
    font-weight: 500;
  }

  .task-feedback-count {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .task-assignee {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .task-actions {
    display: flex;
    gap: 16rpx;
    margin-top: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .action-btn {
    padding: 10rpx 24rpx;
    font-size: 24rpx;
    border-radius: 8rpx;
    color: #ffffff;

    &.success {
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
</style>
