export const isDark = useDark()
export function toggleDark() {
  isDark.value = !isDark.value
  uni.setStorageSync('darkMode', isDark.value)
}

/**
 * 获取当前系统是否为深色模式
 * @returns 是否为深色模式
 */
function useDark() {
  const darkMode = ref(false)
  const savedDarkMode = uni.getStorageSync('darkMode')
  const appBaseInfo = uni.getAppBaseInfo()

  if (savedDarkMode === null) darkMode.value = appBaseInfo?.theme === 'dark'
  else darkMode.value = savedDarkMode

  // #ifdef H5 || MP-WEIXIN
  uni.onThemeChange((res) => {
    darkMode.value = res.theme === 'dark'
    uni.setStorageSync('darkMode', darkMode.value)
  })
  // #endif

  return darkMode
}
