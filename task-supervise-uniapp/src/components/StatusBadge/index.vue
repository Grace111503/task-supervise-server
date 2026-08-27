<script lang="ts" setup>
  import type { TaskStatus } from '~/api/task'

  interface Props {
    size?: 'small' | 'medium' | 'large'
    status: TaskStatus
  }

  withDefaults(defineProps<Props>(), {
    size: 'medium',
  })

  const statusMap: Record<TaskStatus, { text: string; color: string }> = {
    completed: { color: '#07C160', text: '已完成' },
    in_progress: { color: '#2196f3', text: '进行中' },
    overdue: { color: '#f44336', text: '已逾期' },
    pending: { color: '#ff9800', text: '待处理' },
  }
</script>

<template>
  <view
    class="status-badge"
    :class="[`status-${status}`, `size-${size}`]"
    :style="{ backgroundColor: statusMap[status]?.color }"
  >
    {{ statusMap[status]?.text }}
  </view>
</template>

<style lang="scss" scoped>
  .status-badge {
    display: inline-block;
    padding: 6rpx 16rpx;
    font-size: 22rpx;
    color: #ffffff;
    border-radius: 8rpx;
    white-space: nowrap;

    &.size-small {
      padding: 4rpx 12rpx;
      font-size: 20rpx;
    }

    &.size-large {
      padding: 8rpx 20rpx;
      font-size: 24rpx;
    }
  }
</style>
