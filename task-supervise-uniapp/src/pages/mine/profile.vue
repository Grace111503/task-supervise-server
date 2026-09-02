<script lang="ts" setup>
  import { authApi } from '~/api/auth'
  import { useUserStore } from '~/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '个人信息',
    },
  }))

  const userStore = useUserStore()
  const { userInfo } = storeToRefs(userStore)

  const form = reactive({
    avatar: '',
    email: '',
    name: '',
    phone: '',
  })

  const loading = ref(false)
  const avatarUploading = ref(false)

  /** 拼接完整的头像访问URL（附带token参数用于鉴权） */
  function getFullAvatarUrl(path: string): string {
    if (!path) return ''
    if (path.startsWith('http://') || path.startsWith('https://')) return path
    const baseUrl = 'http://localhost:8082'
    let accessToken = ''
    try {
      const raw = uni.getStorageSync('authorized-token')
      if (raw) {
        const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
        accessToken = parsed.accessToken || ''
      }
    } catch {}
    const tokenParam = accessToken ? `?token=Bearer%20${accessToken}` : ''
    // 数据库存的是 "avatars/yyyy/MM/dd/uuid.jpg"
    // 后端 GET 接口是 /api/v1/user/avatar/{year}/{month}/{day}/{filename}
    const apiPath = path.replace('avatars/', 'user/avatar/')
    return `${baseUrl}/api/v1/${apiPath}${tokenParam}`
  }

  async function loadUserInfo() {
    loading.value = true
    try {
      const res: any = await authApi.getUserInfo()
      // 后端返回 Result<UserInfoVO>，需取 .data 拿到真正的 UserInfoVO
      const info = res?.data ?? res
      form.name = info.name || ''
      form.phone = info.phone || ''
      form.email = info.email || ''
      form.avatar = info.avatar || ''
    } catch (error) {
      console.error('获取用户信息失败:', error)
    } finally {
      loading.value = false
    }
  }

  /** 选择并上传头像 */
  function handleAvatarClick() {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFilePaths[0]
        if (!tempFilePath) return

        avatarUploading.value = true
        try {
          const avatarPath = await authApi.uploadAvatar(tempFilePath)
          form.avatar = avatarPath
          // 同步更新 store 中的头像
          userStore.setUserInfo({ ...userInfo.value, avatar: avatarPath })
          uni.showToast({ icon: 'success', title: '头像上传成功' })
        } catch (error) {
          console.error('头像上传失败:', error)
        } finally {
          avatarUploading.value = false
        }
      },
    })
  }

  async function handleSubmit() {
    if (!form.name.trim()) {
      uni.showToast({ icon: 'none', title: '请输入姓名' })
      return
    }

    loading.value = true
    try {
      const res: any = await authApi.updateUserInfo(form)
      // 后端返回 Result<UserInfoVO>，需取 .data 拿到真正的 UserInfoVO
      const updated = res?.data ?? res
      userStore.setUserInfo({ ...userInfo.value, ...updated })
      uni.showToast({ icon: 'success', title: '修改成功' })
    } catch (error) {
      uni.showToast({ icon: 'none', title: '修改失败' })
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    loadUserInfo()
  })
</script>

<template>
  <view class="profile-container">
    <wd-loading v-if="loading" />

    <view class="profile-form">
      <!-- 头像 -->
      <view class="avatar-section" @click="handleAvatarClick">
        <view class="avatar-wrapper">
          <image
            class="avatar-img"
            v-if="form.avatar"
            :src="getFullAvatarUrl(form.avatar)"
            mode="aspectFill"
          />
          <view class="avatar-placeholder" v-else>
            {{ userInfo?.name?.charAt(0) || '?' }}
          </view>
          <view class="avatar-loading" v-if="avatarUploading">
            <wd-loading />
          </view>
        </view>
        <view class="avatar-hint">点击更换头像</view>
      </view>

      <!-- 表单 -->
      <view class="form-section">
        <view class="form-item">
          <view class="item-label">姓名</view>
          <wd-input clearable placeholder="请输入姓名" v-model="form.name" />
        </view>

        <view class="form-item">
          <view class="item-label">手机号</view>
          <wd-input
            clearable
            placeholder="请输入手机号"
            type="number"
            v-model="form.phone"
          />
        </view>

        <view class="form-item">
          <view class="item-label">邮箱</view>
          <wd-input
            clearable
            placeholder="请输入邮箱"
            type="text"
            v-model="form.email"
          />
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-btn">
        <wd-button
          block
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          保存修改
        </wd-button>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .profile-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .profile-form {
    padding: 30rpx;
  }

  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 40rpx;
  }

  .avatar-wrapper {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    overflow: hidden;
    margin-bottom: 16rpx;
    position: relative;
  }

  .avatar-loading {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(0, 0, 0, 0.4);
    border-radius: 50%;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
  }

  .avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #07c160, #0ad168);
    color: #ffffff;
    font-size: 64rpx;
    font-weight: 600;
  }

  .avatar-hint {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .form-section {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    overflow: hidden;
  }

  .form-item {
    padding: 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);

    &:last-child {
      border-bottom: none;
    }
  }

  .item-label {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    margin-bottom: 16rpx;
  }

  .submit-btn {
    margin-top: 40rpx;
  }
</style>
