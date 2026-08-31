<script lang="ts" setup>
  import type { ProgressFeedback } from '~/api/feedback'
  import { feedbackApi, fileApi } from '~/api/feedback'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '反馈记录',
    },
  }))

  const loading = ref(true)
  const taskId = ref(0)
  const feedbackList = ref<ProgressFeedback[]>([])

  /** 加载反馈列表 */
  async function loadFeedbackList() {
    if (!taskId.value) return
    loading.value = true
    try {
      const res = await feedbackApi.listByTaskIdWithFiles(taskId.value)
      const data = res?.data || res || {}
      feedbackList.value = Array.isArray(data) ? data : (data.list || [])
    } catch (error) {
      console.error('加载反馈列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  /** 预览文件 */
  function previewFile(fileId: number) {
    const url = fileApi.getPreviewUrl(fileId)
    // #ifdef H5
    window.open(url, '_blank')
    // #endif
    // #ifdef MP-WEIXIN
    uni.downloadFile({
      url,
      success: (res) => {
        if (res.statusCode === 200) {
          uni.openDocument({
            filePath: res.tempFilePath,
            showMenu: true,
          })
        }
      },
    })
    // #endif
  }

  /** 下载文件 */
  function downloadFile(fileId: number, fileName: string) {
    const url = fileApi.getDownloadUrl(fileId)
    // #ifdef H5
    const a = document.createElement('a')
    a.href = url
    a.download = fileName
    a.click()
    // #endif
    // #ifdef MP-WEIXIN
    uni.downloadFile({
      url,
      success: (res) => {
        if (res.statusCode === 200) {
          uni.saveFile({
            tempFilePath: res.tempFilePath,
            success: (saveRes) => {
              uni.showToast({ icon: 'success', title: '保存成功' })
            },
          })
        }
      },
    })
    // #endif
  }

  /** 获取文件类型图标 */
  function getFileIcon(fileType: string): string {
    const type = fileType?.toLowerCase() || ''
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(type)) return '🖼️'
    if (['mp4', 'avi', 'mov', 'wmv'].includes(type)) return '🎬'
    if (['doc', 'docx'].includes(type)) return '📝'
    if (['xls', 'xlsx'].includes(type)) return '📊'
    if (['pdf'].includes(type)) return '📄'
    return '📎'
  }

  /** 格式化文件大小 */
  function formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B'
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  }

  /** 格式化时间 */
  function formatTime(time: string): string {
    if (!time) return ''
    const date = new Date(time)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  }

  onMounted(() => {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as any
    const id = currentPage?.options?.taskId
    if (id) {
      taskId.value = Number(id)
      loadFeedbackList()
    }
  })
</script>

<template>
  <view class="list-container">
    <wd-loading v-if="loading" />

    <view class="feedback-list" v-else-if="feedbackList.length > 0">
      <view class="feedback-item" v-for="feedback in feedbackList" :key="feedback.feedbackId">
        <!-- 反馈头部 -->
        <view class="feedback-header">
          <view class="feedback-user">
            <view class="user-avatar">{{ feedback.userName?.charAt(0) || 'U' }}</view>
            <view class="user-info">
              <text class="user-name">{{ feedback.userName || '未知用户' }}</text>
              <text class="feedback-time">{{ formatTime(feedback.feedbackTime) }}</text>
            </view>
          </view>
          <view class="feedback-stage">
            <text class="stage-badge">第{{ feedback.stage }}阶段</text>
            <text class="progress-badge">{{ feedback.progressPercent || 0 }}%</text>
          </view>
        </view>

        <!-- 完成内容 -->
        <view class="feedback-content" v-if="feedback.completedContent">
          <text class="content-label">完成内容</text>
          <text class="content-text">{{ feedback.completedContent }}</text>
        </view>

        <!-- 下一步计划 -->
        <view class="feedback-content" v-if="feedback.nextPlan">
          <text class="content-label">下一步计划</text>
          <text class="content-text">{{ feedback.nextPlan }}</text>
        </view>

        <!-- 关联文件 -->
        <view class="feedback-files" v-if="feedback.files && feedback.files.length > 0">
          <text class="files-label">成果材料 ({{ feedback.files.length }})</text>
          <view class="files-grid">
            <view
              class="file-item"
              v-for="file in feedback.files"
              :key="file.fileId"
              @click="previewFile(file.fileId)"
            >
              <text class="file-icon">{{ getFileIcon(file.fileType) }}</text>
              <view class="file-info">
                <text class="file-name">{{ file.originalName }}</text>
                <text class="file-size">{{ formatSize(file.fileSize) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">📋</view>
      <view class="empty-text">暂无反馈记录</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .list-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
    padding: 24rpx;
  }

  .feedback-item {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
  }

  .feedback-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;
  }

  .feedback-user {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .user-avatar {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #07c160 0%, #00a854 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    color: #ffffff;
    font-weight: 600;
  }

  .user-info {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  .user-name {
    font-size: 28rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
  }

  .feedback-time {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .feedback-stage {
    display: flex;
    gap: 12rpx;
  }

  .stage-badge {
    font-size: 22rpx;
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    background-color: #e6f7ff;
    color: #1890ff;
  }

  .progress-badge {
    font-size: 22rpx;
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    background-color: #f6ffed;
    color: #52c41a;
    font-weight: 500;
  }

  .feedback-content {
    margin-bottom: 16rpx;
  }

  .content-label {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-bottom: 8rpx;
    display: block;
  }

  .content-text {
    font-size: 28rpx;
    color: var(--wot-text-main, #1d2129);
    line-height: 1.6;
    display: block;
  }

  .feedback-files {
    margin-top: 16rpx;
    padding-top: 16rpx;
    border-top: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .files-label {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-bottom: 12rpx;
    display: block;
  }

  .files-grid {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
  }

  .file-item {
    display: flex;
    align-items: center;
    padding: 16rpx;
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 8rpx;

    &:active {
      background-color: var(--wot-filled-bottom, #f7f8fa);
    }
  }

  .file-icon {
    font-size: 36rpx;
    margin-right: 12rpx;
  }

  .file-info {
    flex: 1;
    overflow: hidden;
  }

  .file-name {
    font-size: 26rpx;
    color: var(--wot-text-main, #1d2129);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: block;
  }

  .file-size {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 200rpx 0;
  }

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 24rpx;
  }

  .empty-text {
    font-size: 32rpx;
    color: var(--wot-text-main, #1d2129);
  }
</style>