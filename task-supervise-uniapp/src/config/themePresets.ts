import type { ThemeColors } from './theme'

export interface ThemePreset {
  colors: ThemeColors
  name: string
}

export const themePresets: ThemePreset[] = [
  {
    colors: {
      danger: '#F14646',
      info: '#868A9C',
      primary: '#1C64FD',
      success: '#12B886',
      warning: '#F57F00',
    },
    name: '默认蓝',
  },
  {
    colors: {
      danger: '#C06060',
      info: '#9E9E9E',
      primary: '#B0B0B0',
      success: '#8FAF92',
      warning: '#C9A96E',
    },
    name: '水墨',
  },
  {
    colors: {
      danger: '#E62C3B',
      info: '#8590A6',
      primary: '#F77234',
      success: '#00B42A',
      warning: '#FAAD14',
    },
    name: '薄暮橙',
  },
  {
    colors: {
      danger: '#F5222D',
      info: '#8C8C8C',
      primary: '#F7BA1E',
      success: '#52C41A',
      warning: '#FA8C16',
    },
    name: '日暮黄',
  },
  {
    colors: {
      danger: '#F53F3F',
      info: '#86909C',
      primary: '#14C9C9',
      success: '#00B42A',
      warning: '#FF7D00',
    },
    name: '明青',
  },
  {
    colors: {
      danger: '#F5222D',
      info: '#8C8C8C',
      primary: '#12B886',
      success: '#52C41A',
      warning: '#FAAD14',
    },
    name: '极光绿',
  },
  {
    colors: {
      danger: '#F5222D',
      info: '#8C8C8C',
      primary: '#722ED1',
      success: '#13C2C2',
      warning: '#FAAD14',
    },
    name: '酱紫',
  },
  {
    colors: {
      danger: '#F5222D',
      info: '#8C8C8C',
      primary: '#D91AD9',
      success: '#13C2C2',
      warning: '#FA8C16',
    },
    name: '品红',
  },
]
