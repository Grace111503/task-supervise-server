<script lang="ts" setup>
  import { useUserStore } from '~/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '我的',
    },
  }))

  const userStore = useUserStore()
  const { userInfo, isLogin } = storeToRefs(userStore)

  /** 拼接完整的头像访问URL（附带token参数用于鉴权） */
  function getFullAvatarUrl(path: string): string {
    if (!path) return ''
    if (path.startsWith('http://') || path.startsWith('https://')) return path
    const baseUrl = 'http://localhost:8082'
    let accessToken = ''
    try {
      const raw = uni.getStorageSync('authorized-token')
      if (raw) {
        const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
        accessToken = parsed.accessToken || ''
      }
    } catch {}
    const tokenParam = accessToken ? `?token=Bearer%20${accessToken}` : ''
    // 数据库存的是 "avatars/yyyy/MM/dd/uuid.jpg"
    // 后端 GET 接口是 /api/v1/user/avatar/{year}/{month}/{day}/{filename}
    const apiPath = path.replace('avatars/', 'user/avatar/')
    return `${baseUrl}/api/v1/${apiPath}${tokenParam}`
  }

  /** 检查登录状态 */
  function checkAuth() {
    if (!isLogin.value) {
      uni.showToast({ icon: 'none', title: '请先登录' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/index' })
      }, 500)
      return false
    }
    return true
  }

  onShow(() => {
    checkAuth()
  })

  function goProfile() {
    if (!isLogin.value) {
      goLogin()
      return
    }
    uni.navigateTo({ url: '/pages/mine/profile' })
  }

  function goMyTasks() {
    if (!isLogin.value) {
      goLogin()
      return
    }
    uni.switchTab({ url: '/pages/home/index' })
  }

  function goLogin() {
    uni.navigateTo({ url: '/pages/login/index' })
  }

  function handleLogout() {
    uni.showModal({
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          userStore.logOut()
          uni.showToast({ icon: 'success', title: '已退出' })
          // 退出后隐藏tabBar并跳转登录页
          setTimeout(() => {
            try { uni.hideTabBar() } catch {}
            uni.navigateTo({ url: '/pages/login/index' })
          }, 800)
        }
      },
      title: '确认退出',
    })
  }

  function clearCache() {
    uni.showModal({
      content: '确定要清除缓存吗？',
      success: (res) => {
        if (res.confirm) {
          uni.clearStorageSync()
          uni.showToast({ icon: 'success', title: '缓存已清除' })
        }
      },
      title: '确认清除',
    })
  }

  function goAbout() {
    uni.navigateTo({ url: '/pages/mine/about' })
  }
</script>

<template>
  <view class="mine-container">
    <!-- 用户信息卡片 -->
    <view class="user-card" @click="goProfile">
      <view class="user-avatar">
        <image
          class="avatar-img"
          v-if="userInfo?.avatar"
          :src="getFullAvatarUrl(userInfo.avatar)"
          mode="aspectFill"
        />
        <view class="avatar-placeholder" v-else>
          {{ userInfo?.name?.charAt(0) || '?' }}
        </view>
      </view>
      <view class="user-info">
        <view class="user-name">{{ userInfo?.name || '未登录' }}</view>
        <view class="user-hint" v-if="!isLogin">点击登录账号</view>
        <view class="user-meta" v-else>
          <view class="role-tag" :class="'role-' + userStore.userRole">
            {{ userStore.userRoleDesc }}
          </view>
          <text class="user-dept" v-if="userStore.userPosition">
            {{ userStore.userPosition }}
          </text>
        </view>
      </view>
      <view class="user-arrow">›</view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-section">
      <view class="menu-item" @click="goMyTasks">
        <view class="item-icon">📋</view>
        <view class="item-text">我的任务</view>
        <view class="item-arrow">›</view>
      </view>

      <view class="menu-item" v-if="isLogin" @click="goProfile">
        <view class="item-icon">👤</view>
        <view class="item-text">个人信息</view>
        <view class="item-arrow">›</view>
      </view>

      <view class="menu-item" @click="clearCache">
        <view class="item-icon">🗑️</view>
        <view class="item-text">清除缓存</view>
        <view class="item-arrow">›</view>
      </view>

      <view class="menu-item" @click="goAbout">
        <view class="item-icon">ℹ️</view>
        <view class="item-text">关于我们</view>
        <view class="item-arrow">›</view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section" v-if="isLogin">
      <view class="logout-btn" @click="handleLogout">退出登录</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .mine-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
    padding: 30rpx;
  }

  .user-card {
    display: flex;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 40rpx 30rpx;
    margin-bottom: 30rpx;
    box-shadow: 0 4rpx 16rpx var(--wot-opac-2_04, rgba(0, 0, 0, 0.04));
  }

  .user-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 30rpx;
    flex-shrink: 0;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
  }

  .avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #07c160, #0ad168);
    color: #ffffff;
    font-size: 48rpx;
    font-weight: 600;
  }

  .user-info {
    flex: 1;
  }

  .user-name {
    font-size: 36rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 8rpx;
  }

  .user-hint {
    font-size: 26rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .user-meta {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 12rpx;
  }

  .role-tag {
    font-size: 22rpx;
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    color: #ffffff;
    background-color: #07c160;
  }

  .role-tag.role-admin {
    background-color: #f56c6c;
  }

  .role-tag.role-manager {
    background-color: #e6a23c;
  }

  .role-tag.role-user {
    background-color: #07c160;
  }

  .user-dept {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .user-arrow {
    font-size: 40rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .menu-section {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    overflow: hidden;
    margin-bottom: 30rpx;
  }

  .menu-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

    &:last-child {
      border-bottom: none;
    }
  }

  .item-icon {
    font-size: 40rpx;
    margin-right: 24rpx;
  }

  .item-text {
    flex: 1;
    font-size: 30rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .item-arrow {
    font-size: 36rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .logout-section {
    margin-top: 60rpx;
  }

  .logout-btn {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    text-align: center;
    font-size: 32rpx;
    color: #f44336;
    font-weight: 500;
  }
</style>
