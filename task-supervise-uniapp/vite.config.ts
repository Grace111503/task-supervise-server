import { resolve } from 'node:path'
import Uni from '@uni-helper/plugin-uni'
import Components, { kebabCase } from '@uni-helper/vite-plugin-uni-components'
import UniLayouts from '@uni-helper/vite-plugin-uni-layouts'
import UniManifest from '@uni-helper/vite-plugin-uni-manifest'
import UniPages from '@uni-helper/vite-plugin-uni-pages'
import Optimization from '@uni-ku/bundle-optimizer'
import AutoImportTypes from 'auto-import-types'
import PiniaAutoRefs from 'pinia-auto-refs'
import { visualizer } from 'rollup-plugin-visualizer'
import UnoCSS from 'unocss/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { defineConfig } from 'vite'
import UniPolyfill from 'vite-plugin-uni-polyfill'

// Wot UI v2 自定义 Resolver
const WD_RE = /^Wd[A-Z]/
function WotResolver() {
  return {
    resolve: (name: string) => {
      if (name.match(WD_RE)) {
        const compName = kebabCase(name)
        return {
          from: `@wot-ui/ui/components/${compName}/${compName}.vue`,
          name,
        }
      }
    },
    type: 'component' as const,
  }
}
// UpResolver 自定义 Resolver
const UP_RE = /^Up[A-Z]/
function UpResolver() {
  return {
    resolve: (name: string) => {
      if (name.match(UP_RE)) {
        const compName = kebabCase(name)
        return {
          from: `uni-ui-plus/components/${compName}/${compName}.vue`,
          name,
        }
      }
    },
    type: 'component' as const,
  }
}

// https://vitejs.dev/config/
export default defineConfig({
  css: {
    preprocessorOptions: {
      sass: {
        api: 'modern-compiler',
        silenceDeprecations: ['legacy-js-api'],
      },
      scss: {
        api: 'modern-compiler',
        silenceDeprecations: ['legacy-js-api'],
      },
    },
  },
  plugins: [
    AutoImportTypes(),
    PiniaAutoRefs(),
    UniManifest(),
    /**
     * vite-plugin-uni-pages
     * @see https://github.com/uni-helper/vite-plugin-uni-pages
     */
    UniPages({
      dts: 'src/@types/uni-pages.d.ts',
      exclude: ['**/components/**/*.*'],
      homePage: 'pages/home/index',
      subPackages: ['src/pages-sub', 'src/sub-packages'],
    }),

    /**
     * vite-plugin-uni-layouts
     * @see https://github.com/uni-helper/vite-plugin-uni-layouts
     */
    UniLayouts(),

    /**
     * unplugin-auto-import 按需 import
     * @see https://github.com/antfu/unplugin-auto-import
     */
    AutoImport({
      dirs: [
        './src/composables',
        './src/api',
        './src/store',
        './src/utils/http',
        './src/hooks',
      ],
      dts: 'src/@types/auto-imports.d.ts',
      imports: [
        'vue',
        'uni-app',
        'pinia',
        'vue-i18n',
        {
          '@iceywu/utils': [
            'to',
            'list',
            'sleep',
            'consolePlus',
            'isArray',
            'isEmpty',
            'isObject',
            'isNumber',
            'isString',
            'deepClone',
            'customDestr',
            'toPro',
          ],
        },
        {
          'vue-hooks-pure': ['useRequest'],
        },
      ],
      vueTemplate: true,
    }),

    /**
     * unplugin-vue-components 按需引入组件
     * 注意：需注册至 uni 之前，否则不会生效
     * @see https://github.com/antfu/vite-plugin-components
     */
    Components({
      dts: 'src/@types/components.d.ts',
      resolvers: [WotResolver(), UpResolver()],
    }),

    UniPolyfill(),

    Optimization({
      enable: {
        'async-component': true,
        'async-import': true,
        optimization: true,
      },
      logger: false,
    }),
    Uni(),
    UnoCSS({
      mode: 'per-module',
    }),

    /**
     * rollup-plugin-visualizer 打包体积分析
     * 执行 build 命令时设置 ANALYZE=true 可生成 stats.html 分析报告
     * 例: ANALYZE=true pnpm build:mp-weixin
     * @see https://github.com/btd/rollup-plugin-visualizer
     */
    process.env.ANALYZE
      ? visualizer({
          brotliSize: true,
          filename: 'stats.html',
          gzipSize: true,
          open: true,
        })
      : null,
  ].filter(Boolean),
  resolve: {
    alias: {
      '~/': `${resolve(import.meta.dirname, 'src')}/`,
    },
  },
})
