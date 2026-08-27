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
    email: '',
    name: '',
    phone: '',
  })

  const loading = ref(false)

  async function loadUserInfo() {
    loading.value = true
    try {
      const info = await authApi.getUserInfo()
      form.name = info.name || ''
      form.phone = info.phone || ''
      form.email = info.email || ''
    } catch (error) {
      console.error('获取用户信息失败:', error)
    } finally {
      loading.value = false
    }
  }

  async function handleSubmit() {
    if (!form.name.trim()) {
      uni.showToast({ icon: 'none', title: '请输入姓名' })
      return
    }

    loading.value = true
    try {
      const updated = await authApi.updateUserInfo(form)
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
      <view class="avatar-section">
        <view class="avatar-wrapper">
          <image
            class="avatar-img"
            v-if="userInfo?.avatar"
            :src="userInfo.avatar"
          />
          <view class="avatar-placeholder" v-else>
            {{ userInfo?.name?.charAt(0) || '?' }}
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
