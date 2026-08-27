<script lang="ts" setup>
  import { APP_NAME, APP_VERSION } from '~/config/app'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '关于我们',
    },
  }))

  const appInfo = ref({
    description: '任务督导管理平台',
    developer: '快伟派科技',
    name: APP_NAME || '任务督导',
    version: APP_VERSION || '1.0.0',
  })

  function goFeedback() {
    uni.showToast({ icon: 'none', title: '功能开发中' })
  }

  function goPrivacy() {
    uni.showToast({ icon: 'none', title: '功能开发中' })
  }

  function goUserAgreement() {
    uni.showToast({ icon: 'none', title: '功能开发中' })
  }
</script>

<template>
  <view class="about-container">
    <!-- Logo 和应用信息 -->
    <view class="app-info">
      <view class="app-logo">
        <image class="logo-img" src="/static/logo.png" />
      </view>
      <view class="app-name">{{ appInfo.name }}</view>
      <view class="app-version">版本 {{ appInfo.version }}</view>
      <view class="app-desc">{{ appInfo.description }}</view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-section">
      <view class="menu-item" @click="goFeedback">
        <view class="item-text">意见反馈</view>
        <view class="item-arrow">›</view>
      </view>

      <view class="menu-item" @click="goPrivacy">
        <view class="item-text">隐私政策</view>
        <view class="item-arrow">›</view>
      </view>

      <view class="menu-item" @click="goUserAgreement">
        <view class="item-text">用户协议</view>
        <view class="item-arrow">›</view>
      </view>
    </view>

    <!-- 版权信息 -->
    <view class="copyright">
      <view class="copyright-text">© 2026 {{ appInfo.developer }}</view>
      <view class="copyright-text">All Rights Reserved</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .about-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .app-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 80rpx 30rpx 60rpx;
    background-color: var(--wot-filled-oppo, #ffffff);
    margin-bottom: 30rpx;
  }

  .app-logo {
    width: 160rpx;
    height: 160rpx;
    border-radius: 32rpx;
    overflow: hidden;
    margin-bottom: 24rpx;
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  }

  .logo-img {
    width: 100%;
    height: 100%;
  }

  .app-name {
    font-size: 40rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 12rpx;
  }

  .app-version {
    font-size: 26rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-bottom: 8rpx;
  }

  .app-desc {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
  }

  .menu-section {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    overflow: hidden;
    margin: 0 30rpx 30rpx;
  }

  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

    &:last-child {
      border-bottom: none;
    }
  }

  .item-text {
    font-size: 30rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .item-arrow {
    font-size: 36rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .copyright {
    text-align: center;
    padding: 60rpx 30rpx;
  }

  .copyright-text {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-bottom: 8rpx;
  }
</style>
