<script lang="ts" setup>
  const { themeVars } = useTheme()

  function onScrollViewScroll(e: any) {
    uni.$emit('pageScroll', e.detail?.scrollTop ?? 0)
  }
</script>

<template>
  <wd-config-provider
    :theme="isDark ? 'dark' : 'light'"
    :theme-vars="themeVars"
  >
    <view
      class="app-box"
      :class="{
        dark: isDark,
      }"
    >
      <view class="page-content">
        <scroll-view
          class="app-container"
          :bounces="false"
          :enhanced="true"
          :scroll-y="true"
          :show-scrollbar="false"
          @scroll="onScrollViewScroll"
        >
          <slot />
        </scroll-view>
      </view>
      <TabBar />
    </view>
  </wd-config-provider>
</template>

<style lang="scss" scoped>
.app-box {
  height: 100vh;
  box-sizing: border-box;
  // padding-bottom: calc(env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  .page-content {
    flex: 1;
    overflow: hidden;
    .app-container {
      height: 100%;
    }
  }
}
</style>
