<script lang="ts" setup>
  import type { Task, TaskStatus } from '~/api/task'
  import { taskApi } from '~/api/task'

  definePage(() => ({
    layout: 'home',
  }))

  const taskList = ref<Task[]>([])
  const loading = ref(false)
  const refreshing = ref(false)
  const currentPage = ref(1)
  const hasMore = ref(true)
  const activeTab = ref<TaskStatus | 'all'>('all')

  const tabs: Array<{ key: TaskStatus | 'all'; label: string }> = [
    { key: 'all', label: '全部' },
    { key: 'pending', label: '待处理' },
    { key: 'in_progress', label: '进行中' },
    { key: 'completed', label: '已完成' },
    { key: 'overdue', label: '已逾期' },
  ]

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
        params.status = activeTab.value
      }

      const res = await taskApi.getList(params)
      const { list = [], total = 0 } = res || {}

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

  function onTabChange(key: TaskStatus | 'all') {
    activeTab.value = key
    loadTasks(true)
  }

  function goDetail(task: Task) {
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
        taskList.value[index].status = status
      }
      uni.showToast({ icon: 'success', title: '状态已更新' })
    } catch (error) {
      uni.showToast({ icon: 'none', title: '更新失败' })
    }
  }

  function goCreate() {
    uni.navigateTo({ url: '/pages/home/edit' })
  }

  onMounted(() => {
    loadTasks(true)
  })
</script>

<template>
  <view class="home-container">
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
        <view class="nav-title">任务列表</view>
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
        hint="点击右下角按钮创建新任务"
        icon="📋"
        text="暂无任务"
        v-if="taskList.length === 0 && !loading"
      />

      <view class="task-items" v-else>
        <TaskCard
          show-actions
          v-for="item in taskList"
          :key="item.id"
          :task="item"
          @click="goDetail(item)"
          @delete="handleDelete(item)"
          @edit="goEdit(item)"
          @status-change="handleStatusChange(item, $event)"
        />

        <view class="loading-more" v-if="loading">
          <wd-loading size="24px">加载中...</wd-loading>
        </view>
      </view>
    </scroll-view>

    <!-- 新建按钮 -->
    <view class="fab-button" @click="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .home-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: var(--wot-filled-bottom, #f7f8fa);
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

  .task-list {
    flex: 1;
    overflow: hidden;
  }

  .task-items {
    padding: 24rpx;
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
