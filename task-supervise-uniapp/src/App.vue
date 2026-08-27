<script lang="ts" setup>
  import { onLaunch } from '@dcloudio/uni-app'
  import { useAppStore } from '~/store/app'

  onLaunch(() => {
    // 初始化主题色
    initThemeColors()

    // 初始化应用 Store
    const appStore = useAppStore()
    appStore.initSystemInfo()
    appStore.watchNetworkStatus()
  })
</script>

<style lang="scss">
@use 'uni-ui-plus/styles/theme/index.scss' as uniUiPlusTheme;
@use '@wot-ui/ui/styles/theme/index.scss' as wotTheme;
@import './styles/main.css';
</style>
