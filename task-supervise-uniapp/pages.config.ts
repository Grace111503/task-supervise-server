import { defineUniPages } from '@uni-helper/vite-plugin-uni-pages'
import { APP_NAME } from './src/config/app'

export default defineUniPages({
  easycom: {
    autoscan: true,
    custom: {
      '^(?!z-paging-refresh|z-paging-load-more)z-paging(.*)':
        'z-paging/components/z-paging$1/z-paging$1.vue',
    },
  },
  globalStyle: {
    'app-plus': {
      bounce: 'none',
      titleNView: false, // 移除 H5、APP 顶部导航
    },
    backgroundColor: '@bgColor',
    backgroundColorBottom: '@bgColorBottom',
    backgroundColorTop: '@bgColorTop',
    backgroundTextStyle: '@bgTxtStyle',
    disableScroll: true,
    enablePullDownRefresh: false,
    navigationBarBackgroundColor: '@navBgColor',
    navigationBarTextStyle: '@navTxtStyle',
    navigationBarTitleText: APP_NAME,
    navigationStyle: 'custom',
  },
  pages: [],
  tabBar: {
    color: '#86909c',
    custom: true,
    list: [
      {
        iconPath: 'static/tabbar/placeholder.png',
        pagePath: 'pages/home/index',
        selectedIconPath: 'static/tabbar/placeholder.png',
        text: '首页',
      },
      {
        iconPath: 'static/tabbar/placeholder.png',
        pagePath: 'pages/mine/index',
        selectedIconPath: 'static/tabbar/placeholder.png',
        text: '我的',
      },
    ],
    selectedColor: '#07C160',
  },
})
