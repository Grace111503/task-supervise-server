<script lang="ts" setup>
  import { get } from '@iceywu/utils'

  const pageInfo = getCurrentPages()?.[getCurrentPages().length - 1]
  const objVal = get(pageInfo, ['$page', 'meta', 'navigationBar'])
  const { themeVars, themeVarsExtended } = useTheme()

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
      <view class="title">
        <slot name="navbar">
          <NavBar
            is-need-left
            :base-props="{
              title: objVal?.titleText,
            }"
          >
            <template #left>
              <div
                class="icon-btn"
                title="Toggle dark mode"
                :style="{ color: themeVarsExtended.primary }"
                @click="toggleDark()"
              >
                <div :class="isDark ? 'i-carbon-moon' : 'i-carbon-sun'" />
              </div>
            </template>
          </NavBar>
        </slot>
      </view>
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
  .title {
    flex-shrink: 0;
  }
  .page-content {
    flex: 1;
    overflow: hidden;
    .app-container {
      height: 100%;
    }
  }
}
</style>
