<script lang="ts" setup>
  import type { TaskPriority } from '~/api/task'
  import { taskApi } from '~/api/task'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '编辑任务',
    },
  }))

  const isEdit = ref(false)
  const loading = ref(false)
  const taskId = ref(0)

  const form = reactive({
    deadline: '',
    description: '',
    priority: 'medium' as TaskPriority,
    remark: '',
    title: '',
  })

  const priorityOptions = [
    { label: '高', value: 'high' },
    { label: '中', value: 'medium' },
    { label: '低', value: 'low' },
  ]

  async function loadTask() {
    if (!taskId.value) return
    loading.value = true
    try {
      const res = await taskApi.getDetail(taskId.value)
      // 后端返回格式: { code, message, data: Task }
      const task = res?.data || res
      form.title = task.title
      form.description = task.description || ''
      form.priority = task.priority
      form.deadline = task.deadline || ''
      form.remark = task.remark || ''
    } catch (error) {
      console.error('加载任务失败:', error)
    } finally {
      loading.value = false
    }
  }

  async function handleSubmit() {
    if (!form.title.trim()) {
      uni.showToast({ icon: 'none', title: '请输入任务标题' })
      return
    }

    loading.value = true
    try {
      if (isEdit.value) {
        await taskApi.update(taskId.value, form)
        uni.showToast({ icon: 'success', title: '修改成功' })
      } else {
        await taskApi.create(form)
        uni.showToast({ icon: 'success', title: '创建成功' })
      }
      setTimeout(() => uni.navigateBack(), 1500)
    } catch (error) {
      uni.showToast({ icon: 'none', title: '操作失败' })
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1] as any
    const id = currentPage?.options?.id
    if (id) {
      isEdit.value = true
      taskId.value = Number(id)
      uni.setNavigationBarTitle({ title: '编辑任务' })
      loadTask()
    } else {
      uni.setNavigationBarTitle({ title: '创建任务' })
    }
  })
</script>

<template>
  <view class="edit-container">
    <wd-loading v-if="loading" />

    <view class="edit-form">
      <!-- 任务标题 -->
      <view class="form-item">
        <view class="item-label">
          任务标题
          <text class="required">*</text>
        </view>
        <wd-input
          clearable
          placeholder="请输入任务标题"
          v-model="form.title"
          :maxlength="50"
        />
      </view>

      <!-- 任务描述 -->
      <view class="form-item">
        <view class="item-label">任务描述</view>
        <wd-textarea
          autosize
          placeholder="请输入任务描述"
          v-model="form.description"
          :maxlength="500"
        />
      </view>

      <!-- 优先级 -->
      <view class="form-item">
        <view class="item-label">优先级</view>
        <wd-radio-group v-model="form.priority">
          <wd-radio
            v-for="option in priorityOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </wd-radio>
        </wd-radio-group>
      </view>

      <!-- 截止时间 -->
      <view class="form-item">
        <view class="item-label">截止时间</view>
        <wd-input
          clearable
          placeholder="请输入截止时间，如 2024-12-31"
          v-model="form.deadline"
        />
      </view>

      <!-- 备注 -->
      <view class="form-item">
        <view class="item-label">备注</view>
        <wd-textarea
          autosize
          placeholder="请输入备注信息"
          v-model="form.remark"
          :maxlength="200"
        />
      </view>

      <!-- 提交按钮 -->
      <view class="submit-btn" @click="handleSubmit">
        <wd-button block type="primary" :loading="loading">
          {{ isEdit ? '保存修改' : '创建任务' }}
        </wd-button>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .edit-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
  }

  .edit-form {
    padding: 30rpx;
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

  .submit-btn {
    margin-top: 40rpx;
  }
</style>
