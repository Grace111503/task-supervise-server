<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'
  import { useUserStore } from '~/store/user'

  const userStore = useUserStore()

  interface Props {
    showActions?: boolean
    selectable?: boolean
    selected?: boolean
    task: Task
  }

  const props = withDefaults(defineProps<Props>(), {
    showActions: false,
    selectable: false,
    selected: false,
  })

  const emit = defineEmits<{
    click: [task: Task]
    edit: [task: Task]
    delete: [task: Task]
    select: [task: Task]
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

  /** 当前用户是否为该任务的创建人 */
  const isCreator = computed(() => userStore.userId === props.task.creatorId)
  /** 当前用户是否为该任务的执行人（单人模式 + 多人协办模式） */
  const isAssignee = computed(() => {
    if (userStore.userId === props.task.assigneeId) return true
    // 多人协办模式：检查是否在 multiAssigneeIds 列表中
    if (props.task.multiAssigneeIds && props.task.multiAssigneeIds.length > 0) {
      return props.task.multiAssigneeIds.includes(userStore.userId)
    }
    return false
  })

  /** 是否显示「开始」按钮：pending 状态，且当前用户是执行人（非创建人） */
  const canStart = computed(() =>
    userStore.isUser && props.task.status === 'pending' && isAssignee.value && !isCreator.value,
  )
  /** 是否显示「完成」按钮：非已完成，且管理员/主管 或 任务创建人 */
  const canComplete = computed(() => {
    if (props.task.status === 'completed') return false
    return userStore.hasManagePermission || isCreator.value
  })
  /** 是否显示「编辑」按钮：管理员/主管 或 任务创建人 */
  const canEdit = computed(() => userStore.hasManagePermission || isCreator.value)
  /** 是否显示「删除」按钮 */
  const canDelete = computed(() => {
    if (userStore.hasManagePermission) return true
    // 普通执行人：自己创建的随时可删；分配给自己的仅已完成可删
    if (isCreator.value) return true
    if (isAssignee.value && props.task.status === 'completed') return true
    return false
  })

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
    if (props.selectable) {
      emit('select', props.task)
    } else {
      emit('click', props.task)
    }
  }

  function handleSelect(e: Event) {
    e.stopPropagation()
    emit('select', props.task)
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
  <view class="task-card" :class="{ selectable }" @click="handleClick">
    <view class="card-select" v-if="selectable" @click.stop="handleSelect">
      <view class="checkbox" :class="{ checked: selected }">
        <text v-if="selected" class="check-mark">✓</text>
      </view>
    </view>
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

    <view class="task-actions" v-if="showActions && (canStart || canComplete || canEdit || canDelete)">
      <view
        class="action-btn success"
        v-if="canComplete"
        @click.stop="handleStatusChange('completed')"
      >
        完成
      </view>
      <view
        class="action-btn info"
        v-if="canStart"
        @click.stop="handleStatusChange('in_progress')"
      >
        开始
      </view>
      <view class="action-btn warning" v-if="canEdit" @click.stop="handleEdit">编辑</view>
      <view class="action-btn danger" v-if="canDelete" @click.stop="handleDelete">删除</view>
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
    position: relative;

    &.selectable {
      padding-left: 80rpx;
    }
  }

  .card-select {
    position: absolute;
    left: 24rpx;
    top: 34rpx;
  }

  .checkbox {
    width: 40rpx;
    height: 40rpx;
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
    font-size: 24rpx;
    color: #ffffff;
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
