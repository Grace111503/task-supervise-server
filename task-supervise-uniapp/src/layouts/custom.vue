<script lang="ts" setup>
  const { themeVars } = useTheme()
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
      <view class="page-content"><slot /></view>
    </view>
  </wd-config-provider>
</template>

<style lang="scss" scoped>
.app-box {
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  .page-content {
    flex: 1;
  }
}
</style>
