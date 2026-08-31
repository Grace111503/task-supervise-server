<script lang="ts" setup>
  import { feedbackApi, fileApi } from '~/api/feedback'
  import apiServer from '@/config/domain'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '提交反馈',
    },
  }))

  const loading = ref(false)
  const uploading = ref(false)
  const taskId = ref(0)
  const currentStage = ref(1)

  const form = reactive({
    completedContent: '',
    nextPlan: '',
  })

  /** 进度百分比自动计算：每个阶段 10%，最高 100% */
  const progressPercent = computed(() => {
    return Math.min(currentStage.value * 10, 100)
  })

  const uploadedFiles = ref<any[]>([])

  /** 加载下一阶段号 */
  async function loadNextStage() {
    if (!taskId.value) return
    try {
      const res = await feedbackApi.getNextStage(taskId.value)
      const data = res?.data || res || {}
      currentStage.value = data.stage || 1
    } catch (error) {
      console.error('加载阶段号失败:', error)
    }
  }

  /** 选择文件（跨平台兼容） */
  async function chooseFile() {
    // #ifdef MP-WEIXIN
    uni.chooseMessageFile({
      count: 9,
      type: 'file',
      success: async (res) => {
        const files = res.tempFiles
        for (const file of files) {
          await uploadFileWx(file.path, file.name)
        }
      },
    })
    // #endif
    // #ifdef H5
    const input = document.createElement('input')
    input.type = 'file'
    input.multiple = true
    input.accept = '.doc,.docx,.xls,.xlsx,.pdf,.txt,.ppt,.pptx,.zip,.rar'
    input.onchange = async (e: Event) => {
      const target = e.target as HTMLInputElement
      const files = target.files
      if (files) {
        for (let i = 0; i < files.length; i++) {
          await uploadFileH5(files[i])
        }
      }
    }
    input.click()
    // #endif
  }

  /** H5 上传文件（File 对象，走 fetch + FormData） */
  async function uploadFileH5(file: File) {
    uploading.value = true
    try {
      // 从 cookie 中读取 token（和 http 拦截器一致）
      const tokenStr = document.cookie.split(';').find(c => c.trim().startsWith('authorized-token='))
      let token = ''
      if (tokenStr) {
        try {
          const parsed = JSON.parse(tokenStr.split('=').slice(1).join('='))
          token = parsed.accessToken || ''
        } catch {}
      }
      const formData = new FormData()
      formData.append('file', file)
      formData.append('taskId', String(taskId.value))

      const res = await fetch(`${apiServer.baseServer}/file/upload`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      })
      const json = await res.json()
      const result = json?.data || json
      if (result && result.fileId) {
        uploadedFiles.value.push(result)
        uni.showToast({ icon: 'success', title: '上传成功' })
      } else {
        uni.showToast({ icon: 'none', title: json?.message || '上传失败' })
      }
    } catch (error) {
      console.error('上传失败:', error)
      uni.showToast({ icon: 'none', title: '上传失败' })
    } finally {
      uploading.value = false
    }
  }

  /** 微信小程序上传文件（tempFilePath，走 uni.uploadFile） */
  function uploadFileWx(filePath: string, fileName: string) {
    uploading.value = true
    const token = uni.getStorageSync('accessToken') || ''
    uni.uploadFile({
      url: `${apiServer.baseServer}/api/v1/file/upload`,
      filePath,
      name: 'file',
      formData: { taskId: String(taskId.value) },
      header: { Authorization: `Bearer ${token}` },
      success: (res) => {
        const data = JSON.parse(res.data)
        const result = data?.data || data
        if (result && result.fileId) {
          uploadedFiles.value.push(result)
          uni.showToast({ icon: 'success', title: '上传成功' })
        }
      },
      fail: () => {
        uni.showToast({ icon: 'none', title: '上传失败' })
      },
      complete: () => {
        uploading.value = false
      },
    })
  }

  /** 选择图片（跨平台兼容） */
  async function chooseImage() {
    // #ifdef H5
    const input = document.createElement('input')
    input.type = 'file'
    input.multiple = true
    input.accept = 'image/*'
    input.onchange = async (e: Event) => {
      const target = e.target as HTMLInputElement
      const files = target.files
      if (files) {
        for (let i = 0; i < files.length; i++) {
          await uploadFileH5(files[i])
        }
      }
    }
    input.click()
    // #endif
    // #ifdef MP-WEIXIN
    uni.chooseImage({
      count: 9,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        for (const path of res.tempFilePaths) {
          const name = path.split('/').pop() || 'image.jpg'
          await uploadFileWx(path, name)
        }
      },
    })
    // #endif
  }

  /** 删除已上传文件 */
  function removeFile(index: number) {
    uploadedFiles.value.splice(index, 1)
  }

  /** 提交反馈 */
  async function handleSubmit() {
    if (!form.completedContent.trim()) {
      uni.showToast({ icon: 'none', title: '请输入当期完成内容' })
      return
    }

    loading.value = true
    try {
      const fileIds = uploadedFiles.value.map(f => f.fileId)
      await feedbackApi.addWithFiles({
        taskId: taskId.value,
        completedContent: form.completedContent,
        nextPlan: form.nextPlan,
        progressPercent: progressPercent.value,
        fileIds,
      })
      uni.showToast({ icon: 'success', title: '提交成功' })
      setTimeout(() => uni.navigateBack(), 1500)
    } catch (error) {
      console.error('提交失败:', error)
      uni.showToast({ icon: 'none', title: '提交失败' })
    } finally {
      loading.value = false
    }
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

  onMounted(() => {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as any
    const id = currentPage?.options?.taskId
    if (id) {
      taskId.value = Number(id)
      loadNextStage()
    }
  })
</script>

<template>
  <view class="submit-container">
    <wd-loading v-if="loading" />

    <view class="submit-form">
      <!-- 阶段信息 -->
      <view class="stage-banner">
        <text class="stage-label">当前阶段</text>
        <text class="stage-value">第 {{ currentStage }} 阶段</text>
      </view>

      <!-- 当期完成内容 -->
      <view class="form-item">
        <view class="item-label">
          当期完成内容
          <text class="required">*</text>
        </view>
        <wd-textarea
          autosize
          placeholder="请详细描述本期完成的工作内容..."
          v-model="form.completedContent"
          :maxlength="2000"
        />
      </view>

      <!-- 下一步计划 -->
      <view class="form-item">
        <view class="item-label">下一步工作计划</view>
        <wd-textarea
          autosize
          placeholder="请描述下一步的工作计划..."
          v-model="form.nextPlan"
          :maxlength="1000"
        />
      </view>

      <!-- 进度百分比（自动计算） -->
      <view class="form-item">
        <view class="item-label">进度百分比（自动计算）</view>
        <view class="progress-wrapper">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <text class="progress-value">{{ progressPercent }}%</text>
        </view>
        <text class="progress-tip">每提交一次反馈 +10%，最高 100%</text>
      </view>

      <!-- 附件上传 -->
      <view class="form-item">
        <view class="item-label">成果材料</view>
        <view class="upload-area">
          <view class="upload-btn" @click="chooseImage">
            <text class="upload-icon">📷</text>
            <text class="upload-text">图片</text>
          </view>
          <view class="upload-btn" @click="chooseFile">
            <text class="upload-icon">📎</text>
            <text class="upload-text">文件</text>
          </view>
        </view>

        <!-- 已上传文件列表 -->
        <view class="file-list" v-if="uploadedFiles.length > 0">
          <view class="file-item" v-for="(file, index) in uploadedFiles" :key="file.fileId">
            <text class="file-icon">{{ getFileIcon(file.fileType) }}</text>
            <view class="file-info">
              <text class="file-name">{{ file.originalName }}</text>
              <text class="file-size">{{ formatSize(file.fileSize) }}</text>
            </view>
            <text class="file-delete" @click="removeFile(index)">✕</text>
          </view>
        </view>

        <view class="uploading-tip" v-if="uploading">
          <wd-loading size="16px" />
          <text>上传中...</text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-btn">
        <wd-button block type="primary" :loading="loading" @click="handleSubmit">
          提交反馈
        </wd-button>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .submit-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .submit-form {
    padding: 30rpx;
  }

  .stage-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, #07c160 0%, #00a854 100%);
    padding: 30rpx;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    color: #ffffff;
  }

  .stage-label {
    font-size: 28rpx;
  }

  .stage-value {
    font-size: 32rpx;
    font-weight: 600;
  }

  .form-item {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
  }

  .item-label {
    font-size: 28rpx;
    font-weight: 500;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 16rpx;
  }

  .required {
    color: #f44336;
  }

  .progress-wrapper {
    display: flex;
    align-items: center;
    gap: 20rpx;
  }

  .progress-bar {
    flex: 1;
    height: 16rpx;
    background-color: #e5e6eb;
    border-radius: 8rpx;
    overflow: hidden;
  }

  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #07c160, #00a854);
    border-radius: 8rpx;
    transition: width 0.3s ease;
  }

  .progress-value {
    font-size: 28rpx;
    font-weight: 600;
    color: #07c160;
    min-width: 80rpx;
  }

  .progress-tip {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    margin-top: 8rpx;
  }

  .upload-area {
    display: flex;
    gap: 20rpx;
    margin-bottom: 20rpx;
  }

  .upload-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30rpx;
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 12rpx;
    border: 2rpx dashed var(--wot-border-color, #e5e6eb);

    &:active {
      background-color: var(--wot-filled-bottom, #f7f8fa);
    }
  }

  .upload-icon {
    font-size: 48rpx;
    margin-bottom: 8rpx;
  }

  .upload-text {
    font-size: 24rpx;
    color: var(--wot-text-secondary, #4e5969);
  }

  .file-list {
    margin-top: 16rpx;
  }

  .file-item {
    display: flex;
    align-items: center;
    padding: 16rpx;
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 8rpx;
    margin-bottom: 8rpx;
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

  .file-delete {
    font-size: 28rpx;
    color: #f44336;
    padding: 8rpx 16rpx;
  }

  .uploading-tip {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .submit-btn {
    margin-top: 40rpx;
  }
</style>