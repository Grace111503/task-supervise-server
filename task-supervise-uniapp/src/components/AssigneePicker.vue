<script lang="ts" setup>
  import { orgApi } from '~/api/org'
  import type { OrgUser } from '~/api/org'

  interface Props {
    visible: boolean
    multi?: boolean
  }

  const props = withDefaults(defineProps<Props>(), {
    multi: false,
  })

  const emit = defineEmits<{
    confirm: [users: OrgUser[], primaryId?: number]
    close: []
  }>()

  const keyword = ref('')
  const userList = ref<OrgUser[]>([])
  const loading = ref(false)
  const selectedUsers = ref<OrgUser[]>([])
  const primaryId = ref<number>()

  async function loadUsers() {
    loading.value = true
    try {
      const res = await orgApi.getUserList(1, 100, keyword.value || undefined)
      const result = (res as any)?.data || res || {}
      userList.value = result.list || result.records || (Array.isArray(result) ? result : [])
    } catch (error) {
      console.error('加载用户列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  function isSelected(user: OrgUser) {
    return selectedUsers.value.some(u => u.userId === user.userId)
  }

  function toggleUser(user: OrgUser) {
    if (props.multi) {
      const idx = selectedUsers.value.findIndex(u => u.userId === user.userId)
      if (idx === -1) {
        selectedUsers.value.push(user)
        if (!primaryId.value) primaryId.value = user.userId
      } else {
        selectedUsers.value.splice(idx, 1)
        if (primaryId.value === user.userId) {
          primaryId.value = selectedUsers.value[0]?.userId
        }
      }
    } else {
      selectedUsers.value = [user]
      primaryId.value = user.userId
    }
  }

  function setPrimary(userId: number) {
    primaryId.value = userId
  }

  function handleConfirm() {
    if (selectedUsers.value.length === 0) {
      uni.showToast({ icon: 'none', title: '请选择执行人' })
      return
    }
    emit('confirm', selectedUsers.value, primaryId.value)
    handleClose()
  }

  function handleClose() {
    keyword.value = ''
    selectedUsers.value = []
    primaryId.value = undefined
    emit('close')
  }

  function onSearch() {
    loadUsers()
  }

  watch(() => props.visible, (val) => {
    if (val) loadUsers()
  })
</script>

<template>
  <view class="picker-mask" v-if="visible" @click.self="handleClose">
    <view class="picker-popup">
      <view class="picker-header">
        <text class="picker-title">{{ multi ? '选择执行人（可多选）' : '选择执行人' }}</text>
        <view class="picker-close" @click="handleClose">✕</view>
      </view>

      <!-- 搜索栏 -->
      <view class="picker-search">
        <input
          class="search-input"
          placeholder="搜索姓名"
          v-model="keyword"
          @confirm="onSearch"
        />
        <view class="search-btn" @click="onSearch">搜索</view>
      </view>

      <!-- 已选人员 -->
      <view class="selected-area" v-if="multi && selectedUsers.length > 0">
        <view class="selected-label">已选 ({{ selectedUsers.length }})</view>
        <view class="selected-tags">
          <view
            class="selected-tag"
            v-for="user in selectedUsers"
            :key="user.userId"
            :class="{ primary: primaryId === user.userId }"
            @click="setPrimary(user.userId)"
          >
            {{ user.name }}
            <text class="primary-mark" v-if="primaryId === user.userId">⭐</text>
          </view>
        </view>
        <view class="primary-hint" v-if="multi">点击标签切换主负责人</view>
      </view>

      <!-- 用户列表 -->
      <scroll-view class="user-list" scroll-y>
        <view v-if="loading" class="loading-tip">加载中...</view>
        <view
          class="user-item"
          v-for="user in userList"
          :key="user.userId"
          :class="{ selected: isSelected(user) }"
          @click="toggleUser(user)"
        >
          <view class="user-avatar">{{ user.name?.charAt(0) || 'U' }}</view>
          <view class="user-info">
            <text class="user-name">{{ user.name }}</text>
            <text class="user-dept" v-if="user.deptName">{{ user.deptName }}</text>
          </view>
          <view class="user-check" v-if="isSelected(user)">
            <text class="check-icon">✓</text>
          </view>
        </view>
        <view v-if="!loading && userList.length === 0" class="empty-tip">暂无用户</view>
      </scroll-view>

      <!-- 底部按钮 -->
      <view class="picker-footer">
        <view class="footer-btn cancel" @click="handleClose">取消</view>
        <view class="footer-btn confirm" @click="handleConfirm">
          确认{{ multi && selectedUsers.length > 0 ? `(${selectedUsers.length})` : '' }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .picker-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1000;
    display: flex;
    align-items: flex-end;
  }

  .picker-popup {
    width: 100%;
    max-height: 80vh;
    background-color: #ffffff;
    border-radius: 24rpx 24rpx 0 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #e5e6eb;
  }

  .picker-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #1d2129;
  }

  .picker-close {
    font-size: 32rpx;
    color: #86909c;
    padding: 8rpx;
  }

  .picker-search {
    display: flex;
    align-items: center;
    padding: 20rpx 30rpx;
    gap: 16rpx;
    border-bottom: 1rpx solid #e5e6eb;
  }

  .search-input {
    flex: 1;
    height: 64rpx;
    padding: 0 20rpx;
    background-color: #f2f3f5;
    border-radius: 12rpx;
    font-size: 28rpx;
  }

  .search-btn {
    font-size: 28rpx;
    color: #07c160;
    font-weight: 500;
    white-space: nowrap;
  }

  .selected-area {
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #e5e6eb;
    background-color: #f7f8fa;
  }

  .selected-label {
    font-size: 24rpx;
    color: #86909c;
    margin-bottom: 12rpx;
  }

  .selected-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
  }

  .selected-tag {
    display: inline-flex;
    align-items: center;
    gap: 6rpx;
    padding: 8rpx 20rpx;
    font-size: 24rpx;
    background-color: #e6f7ff;
    color: #1890ff;
    border-radius: 24rpx;
    border: 2rpx solid #91d5ff;

    &.primary {
      background-color: #fff7e6;
      color: #fa8c16;
      border-color: #ffd591;
    }
  }

  .primary-mark {
    font-size: 20rpx;
  }

  .primary-hint {
    font-size: 22rpx;
    color: #bfbfbf;
    margin-top: 8rpx;
  }

  .user-list {
    flex: 1;
    max-height: 50vh;
    padding: 0 30rpx;
  }

  .user-item {
    display: flex;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    gap: 20rpx;

    &.selected {
      background-color: #f6ffed;
    }
  }

  .user-avatar {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    background-color: #e6f7ff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    font-weight: 600;
    color: #1890ff;
    flex-shrink: 0;
  }

  .user-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  .user-name {
    font-size: 28rpx;
    color: #1d2129;
    font-weight: 500;
  }

  .user-dept {
    font-size: 24rpx;
    color: #86909c;
  }

  .user-check {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    background-color: #07c160;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .check-icon {
    font-size: 24rpx;
    color: #ffffff;
  }

  .loading-tip,
  .empty-tip {
    text-align: center;
    padding: 60rpx 0;
    font-size: 28rpx;
    color: #86909c;
  }

  .picker-footer {
    display: flex;
    padding: 20rpx 30rpx;
    gap: 20rpx;
    border-top: 1rpx solid #e5e6eb;
    padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  }

  .footer-btn {
    flex: 1;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12rpx;
    font-size: 30rpx;
    font-weight: 500;

    &.cancel {
      background-color: #f2f3f5;
      color: #4e5969;
    }

    &.confirm {
      background-color: #07c160;
      color: #ffffff;
    }
  }
</style>