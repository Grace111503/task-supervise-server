import { presetUni } from '@uni-helper/unocss-preset-uni'
import presetIcons from '@unocss/preset-icons'
import presetLegacyCompat from '@unocss/preset-legacy-compat'
import {
  defineConfig,
  transformerDirectives,
  transformerVariantGroup,
} from 'unocss'

export default defineConfig({
  presets: [
    // 由 Iconify 提供支持的纯 CSS 图标解决方案
    presetIcons({
      extraProperties: {
        display: 'inline-block',
        'vertical-align': 'middle',
      },
      scale: 1.2,
      warn: false,
    }),
    /**
     * unocss uni-app 小程序预设
     * @see https://github.com/uni-helper/unocss-preset-uni
     */
    presetUni({
      remRpx: false,
      // attributify: {
      //   prefixedOnly: true,
      // },
    }),
    presetLegacyCompat({
      commaStyleColorFunction: true,
      legacyColorSpace: true,
    }),
  ],
  safelist: [
    'i-carbon-home',
    'i-carbon-user',
    'i-carbon-sun',
    'i-carbon-moon',
    'i-carbon-campsite',
    'i-carbon-sub-volume',
    'i-carbon-logo-github',
  ],
  /**
   * 自定义快捷语句
   * @see https://github.com/unocss/unocss#shortcuts
   */
  shortcuts: [
    ['fcc', 'flex justify-center items-center'],
    [
      'icon-btn',
      'text-[0.9em] inline-block cursor-pointer select-none opacity-75 transition duration-200 ease-in-out hover:opacity-100 hover:text-teal-600 !outline-none',
    ],
    [
      'btn',
      'px-4 py-1 rounded inline-block bg-btn-primary text-white cursor-pointer hover:bg-btn-primaryOne disabled:cursor-default disabled:bg-btn-primaryTwo disabled:opacity-50',
    ],
    ['switch-animation', 'transition duration-300'],
    ['bg-base', 'bg-[#EFF0F4] dark:bg-[#050505]  switch-animation'],
    ['card-base', 'bg-[#ffffff] dark:bg-[#17181C] switch-animation'],
    ['text-base', 'text-[#000]  dark:text-[#fff]'],
  ],
  theme: {
    colors: {
      btn: {
        error: '#f44336',
        info: '#2196f3',
        primary: '#07C160',
        primaryOne: '#0AD168',
        primaryTwo: '#3BD87D',
        secondary: '#03a9f4',
        success: '#4caf50',
        warning: '#ff9800',
      },
      danger: 'var(--wot-danger-main, #F14646)',
      error: 'var(--wot-danger-main, #F14646)', // 别名
      info: 'var(--wot-text-auxiliary, #868A9C)',
      // 使用 CSS 变量，支持动态主题色切换
      // 由 wot-ui 的 wd-config-provider 提供
      primary: 'var(--wot-primary-6, #07C160)',
      secondary: '#03a9f4',
      success: 'var(--wot-success-main, #12B886)',
      warning: 'var(--wot-warning-main, #F57F00)',
    },

    fontFamily: {
      mono: ['"Courier New"', 'monospace'],
      sans: ['"Helvetica Neue"', 'Arial', 'sans-serif'],
    },
  },
  transformers: [
    transformerDirectives(), // 启用 @apply 功能
    transformerVariantGroup(), // 启用 () 分组功能
  ],
})
