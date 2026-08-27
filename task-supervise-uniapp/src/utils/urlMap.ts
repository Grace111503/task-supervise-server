// URL 映射配置
export const pagesMap = [
  { name: 'home', path: '/pages/home/index', type: 'tabBarPage' },
  { name: 'home-detail', path: '/pages/home/detail', type: 'page' },
  { name: 'home-edit', path: '/pages/home/edit', type: 'page' },
  { name: 'mine-index', path: '/pages/mine/index', type: 'tabBarPage' },
  { name: 'mine-profile', path: '/pages/mine/profile', type: 'page' },
  { name: 'mine-about', path: '/pages/mine/about', type: 'page' },
  { name: 'login', path: '/pages/login/index', type: 'page' },
] as const

export const h5HsqMap: readonly string[] = [
  // H5 环境下需要特殊处理的路由名称列表
] as const

export const needAuthPath = [
  '/pages/mine/index',
  '/pages/mine/profile',
] as const

export function getUrlType(url: string): 'page' | 'tabBar' | 'other' {
  // 判断 URL 类型的逻辑
  if (url.startsWith('/pages/')) {
    return 'page'
  }
  if (url.includes('tabBar')) {
    return 'tabBar'
  }
  return 'other'
}

export type PageItem = (typeof pagesMap)[number]
