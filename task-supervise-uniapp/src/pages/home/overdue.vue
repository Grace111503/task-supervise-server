<script lang="ts" setup>
  import { taskApi } from '~/api/task'
  import type { Task } from '~/api/task'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '逾期任务管理',
    },
  }))

  const overdueTasks = ref<Task[]>([])
  const loading = ref(false)
  const page = ref(1)
  const hasMore = ref(true)
  const selectedTasks = ref<string[]>([])
  const isMultiSelect = ref(false)

  const selectAll = computed({
    get: () => selectedTasks.value.length === overdueTasks.value.length && overdueTasks.value.length > 0,
    set: (val: boolean) => {
      selectedTasks.value = val ? overdueTasks.value.map(t => String(t.id)) : []
    }
  })

  async function loadOverdueTasks() {
    if (page.value === 1) loading.value = true
    try {
      const res = await taskApi.overdueList({ page: page.value, limit: 20 })
      const data = res?.data || res || {}
      const list = Array.isArray(data) ? data : (data.list || [])

      if (page.value === 1) {
        overdueTasks.value = list
      } else {
        overdueTasks.value.push(...list)
      }

      hasMore.value = list.length >= 20
    } catch (error) {
      console.error('加载逾期任务失败:', error)
    } finally {
      loading.value = false
    }
  }

  function toggleSelect() {
    isMultiSelect.value = !isMultiSelect.value
    if (!isMultiSelect.value) selectedTasks.value = []
  }

  function toggleTask(taskId: string) {
    const index = selectedTasks.value.indexOf(taskId)
    if (index === -1) {
      selectedTasks.value.push(taskId)
    } else {
      selectedTasks.value.splice(index, 1)
    }
  }

  async function handleBatchAction(action: 'complete' | 'extend' | 'reassign') {
    if (selectedTasks.value.length === 0) {
      uni.showToast({ icon: 'none', title: '请先选择任务' })
      return
    }

    const actionText = { complete: '标记完成', extend: '延长时限', reassign: '重新指派' }[action]
    const remark = await new Promise<string | null>((resolve) => {
      uni.showModal({
        title: `批量${actionText}`,
        editable: action === 'extend',
        placeholderText: action === 'extend' ? '请输入新的截止日期(YYYY-MM-DD)' : '请输入备注',
        success: (res) => {
          if (res.confirm) {
            resolve(res.content || '')
          } else {
            resolve(null)
          }
        }
      })
    })

    if (remark === null) return

    try {
      await taskApi.batchOverdueAction(selectedTasks.value, action, remark)
      uni.showToast({ icon: 'success', title: '操作成功' })
      selectedTasks.value = []
      isMultiSelect.value = false
      page.value = 1
      loadOverdueTasks()
    } catch (error) {
      uni.showToast({ icon: 'none', title: '操作失败' })
    }
  }

  function formatDeadline(deadline?: string): string {
    if (!deadline) return ''
    return deadline.replace('T', ' ').replace(/\.\d+$/, '').slice(0, 16)
  }

  function loadMore() {
    if (hasMore.value && !loading.value) {
      page.value++
      loadOverdueTasks()
    }
  }

  function goToDetail(taskId: string) {
    uni.navigateTo({ url: `/pages/home/overdue-detail?taskId=${taskId}` })
  }

  onShow(() => { page.value = 1; loadOverdueTasks() })
</script>

<template>
  <view class="overdue-container">
    <!-- 操作栏 -->
    <view class="action-bar" v-if="overdueTasks.length > 0">
      <view class="select-info" v-if="isMultiSelect">
        <view class="select-all" @click="selectAll = !selectAll">
          <view class="checkbox" :class="{ checked: selectAll }">
            <text v-if="selectAll" class="check-mark">✓</text>
          </view>
          <text>全选 ({{ selectedTasks.length }}/{{ overdueTasks.length }})</text>
        </view>
      </view>
      <view class="btn-toggle" @click="toggleSelect">
        {{ isMultiSelect ? '取消' : '批量处理' }}
      </view>
    </view>

    <!-- 批量操作按钮 -->
    <view class="batch-actions" v-if="isMultiSelect && selectedTasks.length > 0">
      <view class="batch-btn complete" @click="handleBatchAction('complete')">
        <text class="batch-icon">✓</text>
        <text>标记完成</text>
      </view>
      <view class="batch-btn extend" @click="handleBatchAction('extend')">
        <text class="batch-icon">⏰</text>
        <text>延长时限</text>
      </view>
      <view class="batch-btn reassign" @click="handleBatchAction('reassign')">
        <text class="batch-icon">👤</text>
        <text>重新指派</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <scroll-view class="task-list" scroll-y @scrolltolower="loadMore">
      <view
        class="task-card overdue-card"
        v-for="task in overdueTasks"
        :key="task.id"
        @click="isMultiSelect ? toggleTask(String(task.id)) : goToDetail(String(task.id))"
      >
        <view class="card-top">
          <view class="select-box" v-if="isMultiSelect" @click.stop="toggleTask(String(task.id))">
            <view class="checkbox" :class="{ checked: selectedTasks.includes(String(task.id)) }">
              <text v-if="selectedTasks.includes(String(task.id))" class="check-mark">✓</text>
            </view>
          </view>
          <view class="overdue-badge">
            逾期 {{ task.overdueDays }} 天
          </view>
        </view>

        <view class="task-header">
          <view class="task-name">{{ task.title }}</view>
        </view>

        <view class="task-info">
          <view class="info-row">
            <text class="label">负责人:</text>
            <text class="value">{{ task.assigneeName }}</text>
          </view>
          <view class="info-row">
            <text class="label">截止日期:</text>
            <text class="deadline">{{ formatDeadline(task.deadline) }}</text>
          </view>
          <view class="info-row">
            <text class="label">当前阶段:</text>
            <text class="value">{{ task.currentStage }} / {{ task.totalStages }}</text>
          </view>
        </view>

        <view class="progress-bar" v-if="task.totalStages > 0">
          <view class="progress-inner">
            <view class="progress-fill" :style="{ width: (task.currentStage / task.totalStages * 100) + '%' }" />
          </view>
          <text class="progress-text">{{ Math.round(task.currentStage / task.totalStages * 100) }}%</text>
        </view>
      </view>

      <view class="load-more" v-if="hasMore && overdueTasks.length > 0">
        <text>加载更多...</text>
      </view>

      <view class="empty-state" v-if="overdueTasks.length === 0 && !loading">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无逾期任务</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
  .overdue-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .select-info {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .select-all {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 28rpx;
    color: var(--wot-text-main, #1d2129);
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

  .btn-toggle {
    font-size: 26rpx;
    color: #07c160;
    font-weight: 500;
  }

  .batch-actions {
    display: flex;
    justify-content: space-around;
    background-color: var(--wot-filled-oppo, #ffffff);
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .batch-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8rpx;
    padding: 16rpx 32rpx;
    border-radius: 12rpx;
    font-size: 24rpx;
    color: #ffffff;

    &.complete {
      background-color: #07c160;
    }

    &.extend {
      background-color: #fa8c16;
    }

    &.reassign {
      background-color: #1890ff;
    }
  }

  .batch-icon {
    font-size: 32rpx;
  }

  .task-list {
    height: calc(100vh - 200rpx);
    padding: 24rpx;
  }

  .task-card {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;

    &.overdue-card {
      border-left: 8rpx solid #f5222d;
    }
  }

  .card-top {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 16rpx;
  }

  .select-box {
    flex-shrink: 0;
  }

  .overdue-badge {
    font-size: 22rpx;
    color: #ffffff;
    background-color: #f5222d;
    padding: 4rpx 16rpx;
    border-radius: 16rpx;
  }

  .task-header {
    margin-bottom: 16rpx;
  }

  .task-name {
    font-size: 30rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
    line-height: 1.4;
  }

  .task-info {
    margin-bottom: 16rpx;
  }

  .info-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
    font-size: 26rpx;
  }

  .label {
    color: var(--wot-text-auxiliary, #869a9c);
    width: 140rpx;
    flex-shrink: 0;
  }

  .value {
    color: var(--wot-text-main, #1d2129);
  }

  .deadline {
    color: #f5222d;
    font-weight: 500;
  }

  .progress-bar {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .progress-inner {
    flex: 1;
    height: 12rpx;
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 6rpx;
    overflow: hidden;
  }

  .progress-fill {
    height: 100%;
    background-color: #f5222d;
    border-radius: 6rpx;
    transition: width 0.3s ease;
  }

  .progress-text {
    font-size: 24rpx;
    color: #f5222d;
    font-weight: 500;
  }

  .load-more {
    display: flex;
    justify-content: center;
    padding: 20rpx;
    font-size: 26rpx;
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
    font-size: 30rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }
</style>