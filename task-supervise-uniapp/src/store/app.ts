export const useAppStore = defineStore(
  'app',
  () => {
    // 应用配置
    const config = ref({
      // 是否首次打开
      isFirstOpen: true,
      // 当前语言
      language: 'zh',
      // 应用名称
      name: '任务督导',
      // 当前主题
      theme: 'light' as 'light' | 'dark' | 'system',
    })

    // 网络状态
    const networkStatus = ref({
      isConnected: true,
      networkType: 'wifi',
    })

    // 系统信息
    const systemInfo = ref({
      platform: 'ios',
      statusBarHeight: 0,
      windowHeight: 667,
      windowWidth: 375,
    })

    // 设置应用配置
    function setConfig(newConfig: Partial<typeof config.value>) {
      config.value = { ...config.value, ...newConfig }
    }

    // 设置首次打开状态
    function setFirstOpen(isFirst: boolean) {
      config.value.isFirstOpen = isFirst
    }

    // 设置主题
    function setTheme(theme: 'light' | 'dark' | 'system') {
      config.value.theme = theme
    }

    // 设置语言
    function setLanguage(lang: string) {
      config.value.language = lang
    }

    // 更新网络状态
    function updateNetworkStatus(status: typeof networkStatus.value) {
      networkStatus.value = status
    }

    // 更新系统信息
    function updateSystemInfo(info: Partial<typeof systemInfo.value>) {
      systemInfo.value = { ...systemInfo.value, ...info }
    }

    // 初始化系统信息
    function initSystemInfo() {
      try {
        const info = uni.getSystemInfoSync()
        systemInfo.value = {
          platform: info.platform || 'ios',
          statusBarHeight: info.statusBarHeight || 0,
          windowHeight: info.windowHeight || 667,
          windowWidth: info.windowWidth || 375,
        }
      } catch (error) {
        console.error('获取系统信息失败:', error)
      }
    }

    // 监听网络状态
    function watchNetworkStatus() {
      uni.onNetworkStatusChange((res) => {
        networkStatus.value = {
          isConnected: res.isConnected,
          networkType: res.networkType,
        }
      })
    }

    return {
      // 状态
      config,
      initSystemInfo,
      networkStatus,

      // 方法
      setConfig,
      setFirstOpen,
      setLanguage,
      setTheme,
      systemInfo,
      updateNetworkStatus,
      updateSystemInfo,
      watchNetworkStatus,
    }
  },
  {
    persist: {
      key: 'app-key',
      storage: {
        getItem: uni.getStorageSync,
        setItem: uni.setStorageSync,
      },
    },
  }
)

export default useAppStore
