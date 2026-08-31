<script lang="ts" setup>
  import { messageApi } from '~/api/message'
  import type { InAppMessage } from '~/api/message'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '消息中心',
    },
  }))

  const messages = ref<InAppMessage[]>([])
  const loading = ref(false)
  const activeTab = ref<'unread' | 'all'>('unread')

  const filteredMessages = computed(() => {
    if (activeTab.value === 'unread') {
      return messages.value.filter(m => m.readStatus === 0)
    }
    return messages.value
  })

  async function loadMessages() {
    loading.value = true
    try {
      const readStatus = activeTab.value === 'unread' ? 0 : undefined
      const res = await messageApi.getMyMessages(readStatus)
      const data = res?.data || res || {}
      messages.value = Array.isArray(data) ? data : (data.list || [])
    } catch (error) {
      console.error('加载消息失败:', error)
    } finally {
      loading.value = false
    }
  }

  async function handleRead(msg: InAppMessage) {
    if (msg.readStatus === 0) {
      try {
        await messageApi.markAsRead(msg.msgId)
        msg.readStatus = 1
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    }
  }

  async function handleReadAll() {
    try {
      await messageApi.markAllAsRead()
      messages.value.forEach(m => m.readStatus = 1)
      uni.showToast({ icon: 'success', title: '全部已读' })
    } catch (error) {
      uni.showToast({ icon: 'none', title: '操作失败' })
    }
  }

  function getLevelColor(level: number): string {
    switch (level) {
      case 3: return '#f5222d'
      case 2: return '#fa8c16'
      default: return '#1890ff'
    }
  }

  function getLevelText(level: number): string {
    switch (level) {
      case 3: return '紧急'
      case 2: return '重要'
      default: return '普通'
    }
  }

  function formatTime(time: string): string {
    if (!time) return ''
    const dateStr = time.replace('T', ' ').replace(/\.\d+$/, '')
    const date = new Date(dateStr.replace(/-/g, '/'))
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const minutes = Math.floor(diff / 60000)
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    const hours = Math.floor(minutes / 60)
    if (hours < 24) return `${hours}小时前`
    const days = Math.floor(hours / 24)
    if (days < 7) return `${days}天前`
    return `${date.getMonth() + 1}/${date.getDate()}`
  }

  function onTabChange(tab: 'unread' | 'all') {
    activeTab.value = tab
    loadMessages()
  }

  onShow(() => { loadMessages() })
</script>

<template>
  <view class="message-container">
    <wd-loading v-if="loading" />

    <!-- 标签栏 -->
    <view class="tabs-bar">
      <view class="tab-item" :class="{ active: activeTab === 'unread' }" @click="onTabChange('unread')">
        未读消息
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'all' }" @click="onTabChange('all')">
        全部消息
      </view>
      <view class="read-all" @click="handleReadAll" v-if="activeTab === 'unread' && filteredMessages.length > 0">
        全部已读
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view class="message-list" scroll-y v-if="filteredMessages.length > 0">
      <view
        class="message-item"
        v-for="msg in filteredMessages"
        :key="msg.msgId"
        :class="{ unread: msg.readStatus === 0 }"
        @click="handleRead(msg)"
      >
        <view class="msg-header">
          <view class="msg-level" :style="{ backgroundColor: getLevelColor(msg.level) }">
            {{ getLevelText(msg.level) }}
          </view>
          <text class="msg-time">{{ formatTime(msg.createdAt) }}</text>
        </view>
        <view class="msg-title">{{ msg.title }}</view>
        <view class="msg-content">{{ msg.content }}</view>
        <view class="unread-dot" v-if="msg.readStatus === 0"></view>
      </view>
    </scroll-view>

    <!-- 空状态 -->
    <view class="empty-state" v-else-if="!loading">
      <text class="empty-icon">🔔</text>
      <text class="empty-text">暂无{{ activeTab === 'unread' ? '未读' : '' }}消息</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .message-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .tabs-bar {
    display: flex;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
    position: relative;
  }

  .tab-item {
    padding: 12rpx 32rpx;
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    border-radius: 32rpx;
    margin-right: 16rpx;
    background-color: var(--wot-filled-content, #f2f3f5);

    &.active {
      color: #ffffff;
      background-color: #07c160;
    }
  }

  .read-all {
    margin-left: auto;
    font-size: 26rpx;
    color: #07c160;
  }

  .message-list {
    height: calc(100vh - 200rpx);
    padding: 24rpx;
  }

  .message-item {
    position: relative;
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;

    &.unread {
      border-left: 6rpx solid #1890ff;
    }
  }

  .msg-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12rpx;
  }

  .msg-level {
    font-size: 22rpx;
    padding: 2rpx 16rpx;
    border-radius: 16rpx;
    color: #ffffff;
  }

  .msg-time {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .msg-title {
    font-size: 30rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 8rpx;
  }

  .msg-content {
    font-size: 26rpx;
    color: var(--wot-text-secondary, #4e5969);
    line-height: 1.5;
  }

  .unread-dot {
    position: absolute;
    top: 24rpx;
    right: 24rpx;
    width: 16rpx;
    height: 16rpx;
    border-radius: 50%;
    background-color: #f5222d;
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