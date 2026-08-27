interface ProxyStorage {
  clear: () => void

  getItem: <T>(k: string) => T

  removeItem: (k: string) => void
  setItem: <T>(k: string, v: T) => void
}

export const storageSession: () => ProxyStorage = () => ({
  clear(): void {
    uni.clearStorageSync()
  },
  getItem<T>(k: string): T {
    return uni.getStorageSync(k)
  },
  removeItem(k: string): void {
    uni.removeStorageSync(k)
  },
  setItem<T>(k: string, v: T): void {
    uni.setStorageSync(k, v)
  },
})
