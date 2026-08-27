/**
 * 主题色配置
 */

export interface ThemeColors {
  danger: string
  info: string
  primary: string
  success: string
  warning: string
}

/**
 * 默认主题色配置 (对齐 wot-ui v2 默认色)
 */
export const defaultThemeColors: ThemeColors = {
  danger: '#F14646',
  info: '#868A9C',
  primary: '#1C64FD',
  success: '#12B886',
  warning: '#F57F00',
}

/**
 * 模拟从服务端获取主题色配置
 */
export async function fetchThemeColors(): Promise<ThemeColors | null> {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 模拟服务端返回的主题色
      // resolve({
      //   primary: '#07c160',
      //   success: '#52c41a',
      //   warning: '#faad14',
      //   danger: '#f5222d',
      //   info: '#1890ff',
      // })

      // 返回 null 表示使用默认配置
      resolve(null)
    }, 300)
  })
}
