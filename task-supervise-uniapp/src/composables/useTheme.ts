/**
 * 主题配置 - 基于 wot-ui
 * 文档: https://v2.wot-ui.cn/component/config-provider.html
 */
import type { ConfigProviderThemeVars } from '@wot-ui/ui'

import type { ThemeColors } from '@/config/theme'
import { defaultThemeColors, fetchThemeColors } from '@/config/theme'

// 存储键名
const THEME_STORAGE_KEY = 'app-theme-colors'

// 从本地存储读取主题色
function getStoredThemeColors(): ThemeColors | null {
  try {
    const stored = uni.getStorageSync(THEME_STORAGE_KEY)
    return stored ? JSON.parse(stored) : null
  } catch (error) {
    console.error('读取主题色失败:', error)
    return null
  }
}

// 保存主题色到本地存储
function saveThemeColors(colors: ThemeColors) {
  try {
    uni.setStorageSync(THEME_STORAGE_KEY, JSON.stringify(colors))
  } catch (error) {
    console.error('保存主题色失败:', error)
  }
}

// 主题色 - 优先使用本地存储的值
const themeColors = ref<ThemeColors>(
  getStoredThemeColors() || defaultThemeColors
)

// 主题变量
const themeVars = computed<ConfigProviderThemeVars>(() => ({
  // 语义色
  dangerMain: themeColors.value.danger,
  // 主色 (v2 色阶体系，primary6 为主色)
  primary6: themeColors.value.primary,
  successMain: themeColors.value.success,
  warningMain: themeColors.value.warning,
}))

/**
 * 计算主题辅助色（浅色变体）
 * 用于背景、悬停等场景
 */
const themeVarsExtended = computed(() => {
  /**
   * 将 hex 颜色转换为 rgba 格式
   */
  function hexToRgba(hex: string, alpha: number): string {
    // 移除 # 号
    const cleanHex = hex.replace('#', '')

    // 处理 3 位和 6 位 hex
    let r: number, g: number, b: number
    if (cleanHex.length === 3) {
      r = Number.parseInt(cleanHex[0] + cleanHex[0], 16)
      g = Number.parseInt(cleanHex[1] + cleanHex[1], 16)
      b = Number.parseInt(cleanHex[2] + cleanHex[2], 16)
    } else {
      r = Number.parseInt(cleanHex.slice(0, 2), 16)
      g = Number.parseInt(cleanHex.slice(2, 4), 16)
      b = Number.parseInt(cleanHex.slice(4, 6), 16)
    }

    return `rgba(${r}, ${g}, ${b}, ${alpha})`
  }

  return {
    // 危险色及其变体
    danger: themeColors.value.danger,
    dangerLight: hexToRgba(themeColors.value.danger, 0.1),

    // 信息色及其变体
    info: themeColors.value.info,
    infoLight: hexToRgba(themeColors.value.info, 0.1),
    // 主色及其变体
    primary: themeColors.value.primary,
    primaryLight: hexToRgba(themeColors.value.primary, 0.1),
    primaryLighter: hexToRgba(themeColors.value.primary, 0.05),
    primaryShadow: hexToRgba(themeColors.value.primary, 0.4),

    // 成功色及其变体
    success: themeColors.value.success,
    successLight: hexToRgba(themeColors.value.success, 0.1),

    // 警告色及其变体
    warning: themeColors.value.warning,
    warningLight: hexToRgba(themeColors.value.warning, 0.1),
  }
})

/**
 * 初始化主题色（从服务端获取）
 */
export async function initThemeColors() {
  // 先尝试从服务端获取
  const colors = await fetchThemeColors()
  if (colors) {
    themeColors.value = colors
    saveThemeColors(colors)
  }
  // 如果服务端没有返回，但本地也没有存储，则保存默认主题色
  else if (!getStoredThemeColors()) {
    saveThemeColors(themeColors.value)
  }
}

/**
 * 使用主题配置
 */
export function useTheme() {
  /**
   * 更新主题色
   */
  function updateThemeColors(colors: Partial<ThemeColors>) {
    themeColors.value = { ...themeColors.value, ...colors }
    // 保存到本地存储
    saveThemeColors(themeColors.value)
  }

  return {
    initThemeColors,
    themeColors: computed(() => themeColors.value),
    themeVars,
    themeVarsExtended,
    updateThemeColors,
  }
}
