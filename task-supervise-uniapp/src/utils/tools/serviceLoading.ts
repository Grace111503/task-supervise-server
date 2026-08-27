let loadingCount = 0

export function showLoading(isLoading: boolean) {
  if (isLoading) {
    loadingCount += 1
    uni.showLoading({
      title: '加载中',
    })
  }
}

export function hideLoading() {
  if (loadingCount > 0) loadingCount -= 1
  if (loadingCount === 0) uni.hideLoading()
}
