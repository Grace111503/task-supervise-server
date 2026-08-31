<script lang="ts" setup>
  import { authApi } from '~/api/auth'
  import { http } from '~/utils/http'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '注册',
    },
  }))

  const form = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    name: '',
    deptId: null as number | null,
    position: '',
    email: '',
    phone: '',
  })

  const loading = ref(false)
  const deptList = ref<{ id: number; name: string; path: string }[]>([])
  const deptPickerIndex = ref(-1)

  const roleHints = [
    { icon: '👤', title: '普通执行人员', desc: '查看自身任务、提交进度反馈' },
    { icon: '👔', title: '部门主管', desc: '创建分派部门任务、验收、查看部门统计' },
    { icon: '🛡️', title: '督办管理员', desc: '全公司任务查看、全局督办、系统配置' },
  ]

  async function loadDeptList() {
    try {
      const res = await http.get('/org/dept/tree')
      const data = (res as any)?.data ?? (res as any)?.result ?? res
      const flat: { id: number; name: string; path: string }[] = []
      // 递归扁平化树结构，过滤根节点，子部门显示完整路径
      function walk(nodes: any[], parentPath = '') {
        if (!nodes) return
        for (const node of nodes) {
          const id = node.deptId ?? node.id
          const name = node.deptName ?? node.name
          const nodePath = parentPath ? `${parentPath} / ${name}` : name
          // 过滤根节点（parentId=0），只显示可选择的部门
          if (node.parentId !== 0 && node.parentId !== undefined) {
            flat.push({ id, name, path: nodePath })
          }
          if (node.children?.length) walk(node.children, nodePath)
        }
      }
      if (Array.isArray(data)) walk(data)
      deptList.value = flat
    } catch {
      // Fallback: 硬编码部门列表
      deptList.value = [
        { id: 2, name: '技术部', path: '技术部' },
        { id: 5, name: '前端组', path: '技术部 / 前端组' },
        { id: 6, name: '后端组', path: '技术部 / 后端组' },
        { id: 7, name: '测试组', path: '技术部 / 测试组' },
        { id: 3, name: '运营部', path: '运营部' },
        { id: 8, name: '内容运营组', path: '运营部 / 内容运营组' },
        { id: 9, name: '用户运营组', path: '运营部 / 用户运营组' },
        { id: 4, name: '市场部', path: '市场部' },
        { id: 10, name: '品牌推广组', path: '市场部 / 品牌推广组' },
        { id: 11, name: '渠道合作组', path: '市场部 / 渠道合作组' },
      ]
    }
  }

  function onDeptChange(e: any) {
    const idx = Number(e.detail.value)
    deptPickerIndex.value = idx
    form.deptId = deptList.value[idx]?.id ?? null
  }

  async function handleRegister() {
    if (!form.username.trim()) {
      uni.showToast({ icon: 'none', title: '请输入用户名' })
      return
    }
    if (form.username.length < 3 || form.username.length > 20) {
      uni.showToast({ icon: 'none', title: '用户名长度3-20位' })
      return
    }
    if (!form.password.trim()) {
      uni.showToast({ icon: 'none', title: '请输入密码' })
      return
    }
    if (form.password.length < 6) {
      uni.showToast({ icon: 'none', title: '密码至少6位' })
      return
    }
    if (form.password !== form.confirmPassword) {
      uni.showToast({ icon: 'none', title: '两次密码不一致' })
      return
    }
    if (!form.name.trim()) {
      uni.showToast({ icon: 'none', title: '请输入姓名' })
      return
    }
    if (form.deptId === null) {
      uni.showToast({ icon: 'none', title: '请选择所属部门' })
      return
    }

    loading.value = true
    try {
      const res = await authApi.register({
        username: form.username,
        password: form.password,
        confirmPassword: form.confirmPassword,
        name: form.name,
        deptId: form.deptId!,
        position: form.position || undefined,
        email: form.email || undefined,
        phone: form.phone || undefined,
      })

      if (res && (res as any).code !== undefined && (res as any).code !== 200) {
        throw new Error((res as any).message || '注册失败')
      }

      uni.showToast({ icon: 'success', title: '注册成功，请登录' })
      setTimeout(() => {
        uni.redirectTo({ url: '/pages/login/index' })
      }, 1500)
    } catch (error: any) {
      const message = error?.message || '注册失败，请重试'
      uni.showToast({ icon: 'none', title: message })
    } finally {
      loading.value = false
    }
  }

  function goLogin() {
    uni.navigateBack()
  }

  onShow(() => {
    try { uni.hideTabBar() } catch {}
  })

  onMounted(() => {
    loadDeptList()
  })
</script>

<template>
  <view class="register-container">
    <!-- Logo -->
    <view class="register-header">
      <view class="register-logo">
        <image class="logo-img" src="/static/logo.png" />
      </view>
      <view class="register-title">注册账号</view>
      <view class="register-subtitle">加入任务督导平台</view>
    </view>

    <!-- 角色说明卡片 -->
    <view class="role-hint-card">
      <view class="role-hint-title">三级权限体系</view>
      <view class="role-hint-list">
        <view v-for="r in roleHints" :key="r.title" class="role-hint-item">
          <text class="role-icon">{{ r.icon }}</text>
          <view class="role-text">
            <text class="role-name">{{ r.title }}</text>
            <text class="role-desc">{{ r.desc }}</text>
          </view>
        </view>
      </view>
      <view class="role-hint-tip">💡 注册默认为"普通执行人员"，角色可由管理员后台调整</view>
    </view>

    <!-- 注册表单 -->
    <view class="register-form">
      <view class="form-item">
        <view class="item-icon">👤</view>
        <wd-input
          clearable
          placeholder="用户名(3-20位)"
          v-model="form.username"
          :maxlength="20"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">🔒</view>
        <wd-input
          clearable
          placeholder="密码(至少6位)"
          show-password
          v-model="form.password"
          :maxlength="20"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">🔒</view>
        <wd-input
          clearable
          placeholder="确认密码"
          show-password
          v-model="form.confirmPassword"
          :maxlength="20"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">📝</view>
        <wd-input
          clearable
          placeholder="姓名 *"
          v-model="form.name"
          :maxlength="50"
        />
      </view>

      <!-- 部门选择 -->
      <view class="form-item" @click.stop>
        <view class="item-icon">🏢</view>
        <picker
          mode="selector"
          :range="deptList.map(d => d.path)"
          :value="deptPickerIndex"
          @change="onDeptChange"
          class="dept-picker"
        >
          <view class="dept-picker-text" :class="{ placeholder: deptPickerIndex < 0 }">
            {{ deptPickerIndex >= 0 ? deptList[deptPickerIndex].path : '所属部门 *' }}
          </view>
          <text class="picker-arrow">›</text>
        </picker>
      </view>

      <view class="form-item">
        <view class="item-icon">💼</view>
        <wd-input
          clearable
          placeholder="职位(可选)"
          v-model="form.position"
          :maxlength="50"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">📧</view>
        <wd-input
          clearable
          placeholder="邮箱(可选)"
          v-model="form.email"
          type="text"
          :maxlength="50"
        />
      </view>

      <view class="form-item">
        <view class="item-icon">📱</view>
        <wd-input
          clearable
          placeholder="手机号(可选)"
          v-model="form.phone"
          type="text"
          :maxlength="20"
        />
      </view>

      <!-- 注册按钮 -->
      <view class="register-btn">
        <wd-button
          block
          size="large"
          type="primary"
          :loading="loading"
          @click="handleRegister"
        >
          注册
        </wd-button>
      </view>

      <!-- 返回登录 -->
      <view class="login-link" @click="goLogin">
        已有账号？<text class="link-text">去登录</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .register-container {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    background-color: var(--wot-filled-bottom, #f7f8fa);
    padding: 0 60rpx 60rpx;
  }

  .register-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 80rpx;
    margin-bottom: 40rpx;
  }

  .register-logo {
    width: 120rpx;
    height: 120rpx;
    border-radius: 24rpx;
    overflow: hidden;
    margin-bottom: 24rpx;
    box-shadow: 0 12rpx 32rpx rgba(7, 193, 96, 0.3);
  }

  .logo-img {
    width: 100%;
    height: 100%;
  }

  .register-title {
    font-size: 44rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    margin-bottom: 8rpx;
  }

  .register-subtitle {
    font-size: 28rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  /* 角色说明卡片 */
  .role-hint-card {
    background: linear-gradient(135deg, #e8f5e9 0%, #e3f2fd 100%);
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 40rpx;
  }

  .role-hint-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #1d2129;
    margin-bottom: 20rpx;
  }

  .role-hint-list {
    display: flex;
    flex-direction: column;
    gap: 16rpx;
  }

  .role-hint-item {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
  }

  .role-icon {
    font-size: 32rpx;
    flex-shrink: 0;
  }

  .role-text {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
    flex: 1;
  }

  .role-name {
    font-size: 26rpx;
    font-weight: 500;
    color: #1d2129;
  }

  .role-desc {
    font-size: 24rpx;
    color: #4e5969;
  }

  .role-hint-tip {
    margin-top: 20rpx;
    font-size: 22rpx;
    color: #869a9c;
    background: rgba(255, 255, 255, 0.6);
    padding: 12rpx 20rpx;
    border-radius: 12rpx;
  }

  .register-form {
    flex: 1;
  }

  .form-item {
    display: flex;
    align-items: center;
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 16rpx 30rpx;
    margin-bottom: 24rpx;
    position: relative;
  }

  .item-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  .form-item :deep(.wd-input) {
    flex: 1;
    min-width: 0;
  }

  .form-item :deep(.wd-input__action-icon),
  .form-item :deep(.wd-input__password-icon) {
    margin-left: auto;
  }

  /* 部门选择器 */
  .dept-picker {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .dept-picker-text {
    font-size: 30rpx;
    color: #1d2129;
  }

  .dept-picker-text.placeholder {
    color: #c9cdd4;
  }

  .picker-arrow {
    font-size: 36rpx;
    color: #c9cdd4;
    margin-left: auto;
  }

  .register-btn {
    margin: 40rpx 0 30rpx;
  }

  .login-link {
    text-align: center;
    font-size: 28rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .link-text {
    color: #07c160;
    margin-left: 8rpx;
  }
</style>
