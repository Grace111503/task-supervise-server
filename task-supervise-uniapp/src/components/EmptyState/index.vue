<script lang="ts" setup>
  interface Props {
    hint?: string
    icon?: string
    text?: string
  }

  withDefaults(defineProps<Props>(), {
    hint: '',
    icon: '📋',
    text: '暂无数据',
  })
</script>

<template>
  <view class="empty-state">
    <view class="empty-icon">{{ icon }}</view>
    <view class="empty-text">{{ text }}</view>
    <view class="empty-hint" v-if="hint">{{ hint }}</view>
  </view>
</template>

<style lang="scss" scoped>
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
    font-size: 32rpx;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 12rpx;
  }

  .empty-hint {
    font-size: 26rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }
</style>
