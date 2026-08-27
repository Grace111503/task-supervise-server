<script lang="ts" setup>
  import { get } from '@iceywu/utils'

  const pageInfo = getCurrentPages()?.[getCurrentPages().length - 1]
  const objVal = get(pageInfo, ['$page', 'meta', 'navigationBar'])
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
      <view class="title">
        <slot name="navbar">
          <NavBar
            is-need-left
            isGoBack
            :base-props="{
              title: objVal?.titleText,
            }"
          />
        </slot>
      </view>
      <view class="page-content">
        <scroll-view
          class="app-container"
          :bounces="false"
          :enhanced="true"
          :scroll-y="true"
          :show-scrollbar="false"
        >
          <slot />
        </scroll-view>
      </view>
    </view>
  </wd-config-provider>
</template>

<style lang="scss" scoped>
.app-box {
  height: 100vh;
  box-sizing: border-box;
    // padding-bottom:calc( env(safe-area-inset-bottom) );
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
        background-color: #F5F5F5;
      }
    }
}
</style>
