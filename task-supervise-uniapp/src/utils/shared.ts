// 防止快速点击
let lastClickTime = 0

export function isFastClick(num = 1000) {
  const time = new Date().getTime()
  const isFast = time - lastClickTime <= num
  lastClickTime = time
  return isFast
}

// 解析 path
export function parseUrl(fullPath: string) {
  const [path, queryStr] = fullPath.split('?')
  const name = path.slice(path.lastIndexOf('/') + 1)
  const query: Record<string, string> = {}
  queryStr
    ?.split('&')
    .map((i) => i.split('='))
    .forEach(([k, v]) => {
      if (k) query[k] = v ?? ''
    })
  return {
    name,
    path,
    query,
  }
}

// 还原url
export function restoreUrl(path: string, query: Record<string, any>) {
  const qs = Object.entries(query)
    .map(([k, v]) => `${k}=${v}`)
    .join('&')
  return qs ? `${path}?${qs}` : path
}
