<script lang="ts" setup>
  import { useUserStore } from '~/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '登录',
    },
  }))

  const userStore = useUserStore()

  const form = reactive({
    password: '',
    username: '',
  })

  const loading = ref(false)
  const rememberMe = ref(false)

  async function handleLogin() {
    if (!form.username.trim()) {
      uni.showToast({ icon: 'none', title: '请输入用户名' })
      return
    }

    if (!form.password.trim()) {
      uni.showToast({ icon: 'none', title: '请输入密码' })
      return
    }

    loading.value = true
    try {
      await userStore.loginFunc({
        password: form.password,
        username: form.username,
      })

      // 记住用户名
      if (rememberMe.value) {
        uni.setStorageSync('remembered-username', form.username)
      } else {
        uni.removeStorageSync('remembered-username')
      }

      uni.showToast({ icon: 'success', title: '登录成功' })

      // 跳转首页（登录页非 tabBar 页，无需调 showTabBar）
      setTimeout(() => {
        uni.switchTab({ url: '/pages/home/index' })
      }, 800)
    } catch (error: any) {
      const message = error?.message || error?.data?.message || '登录失败'
      uni.showToast({ icon: 'none', title: message })
    } finally {
      loading.value = false
    }
  }

  function goRegister() {
    uni.navigateTo({ url: '/pages/login/register' })
  }

  function goForgotPassword() {
    uni.showToast({ icon: 'none', title: '功能开发中' })
  }

  onShow(() => {
    // 登录页非tabBar页面，用try-catch防止报错
    try { uni.hideTabBar() } catch {}
  })

  onMounted(() => {
    // 检查是否记住用户名
    const savedUsername = uni.getStorageSync('remembered-username')
    if (savedUsername) {
      form.username = savedUsername
      rememberMe.value = true
    }
  })
</script>

<template>
  <view class="login-container">
    <!-- Logo -->
    <view class="login-header">
      <view class="login-logo">
        <image class="logo-img" src="/static/logo.png" />
      </view>
      <view class="login-title">任务督导</view>
      <view class="login-subtitle">Task Supervise</view>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <view class="item-icon">👤</view>
        <wd-input
          clearable
          placeholder="请输入用户名"
          v-model="form.username"
          :maxlength="20"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">🔒</view>
        <wd-input
          clearable
          placeholder="请输入密码"
          show-password
          v-model="form.password"
          :maxlength="20"
        />
      </view>

      <!-- 记住我 -->
      <view class="remember-row">
        <wd-checkbox shape="square" v-model="rememberMe">
          记住用户名
        </wd-checkbox>
        <view class="forgot-link" @click="goForgotPassword">忘记密码？</view>
      </view>

      <!-- 登录按钮 -->
      <view class="login-btn">
        <wd-button
          block
          size="large"
          type="primary"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </wd-button>
      </view>

      <!-- 注册入口 -->
      <view class="register-link" @click="goRegister">
        还没有账号？<text class="link-text">立即注册</text>
      </view>
    </view>

    <!-- 底部信息 -->
    <view class="login-footer">
      <view class="footer-text">任务督导管理平台</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .login-container {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    background-color: var(--wot-filled-bottom, #f7f8fa);
    padding: 0 60rpx;
  }

  .login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 160rpx;
    margin-bottom: 80rpx;
  }

  .login-logo {
    width: 160rpx;
    height: 160rpx;
    border-radius: 32rpx;
    overflow: hidden;
    margin-bottom: 30rpx;
    box-shadow: 0 12rpx 32rpx rgba(7, 193, 96, 0.3);
  }

  .logo-img {
    width: 100%;
    height: 100%;
  }

  .login-title {
    font-size: 48rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 8rpx;
  }

  .login-subtitle {
    font-size: 28rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .login-form {
    flex: 1;
  }

  .form-item {
    display: flex;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 16rpx 30rpx;
    margin-bottom: 24rpx;
    position: relative;
  }

  .item-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  /* 让wd-input占满剩余空间，清除/密码图标自然靠右 */
  .form-item :deep(.wd-input) {
    flex: 1;
    min-width: 0;
  }

  .form-item :deep(.wd-input__action-icon),
  .form-item :deep(.wd-input__password-icon) {
    margin-left: auto;
  }

  .remember-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
  }

  .forgot-link {
    font-size: 26rpx;
    color: #07c160;
  }

  .login-btn {
    margin-bottom: 20rpx;
  }

  .register-link {
    text-align: center;
    font-size: 28rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .link-text {
    color: #07c160;
    margin-left: 8rpx;
  }

  .login-footer {
    text-align: center;
    padding: 60rpx 0;
  }

  .footer-text {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }
</style>
