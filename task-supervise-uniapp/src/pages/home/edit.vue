<script lang="ts" setup>
  import type { AssigneeMode, TaskPriority } from '~/api/task'
  import { taskApi, taskAssigneeApi } from '~/api/task'
  import type { OrgUser } from '~/api/org'
  import { useUserStore } from '~/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '编辑任务',
    },
  }))

  const userStore = useUserStore()
  const isEdit = ref(false)
  const loading = ref(false)
  const taskId = ref(0)
  const showAssigneePicker = ref(false)
  const showDeadlinePicker = ref(false)
  const deadlineTimestamp = ref<number>(Date.now())

  const form = reactive({
    assigneeMode: 1 as AssigneeMode,
    deadline: '',
    description: '',
    priority: 'medium' as TaskPriority,
    remark: '',
    title: '',
  })

  // 分派模式
  const assigneeModeOptions = [
    { label: '单人', value: 1 },
    { label: '多人协办', value: 2 },
  ]

  // 执行人选择
  const selectedAssignees = ref<OrgUser[]>([])
  const primaryAssigneeId = ref<number>()

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
      const task = res?.data || res
      form.title = task.title
      form.description = task.description || ''
      form.priority = task.priority
      form.deadline = task.deadline || ''
      form.remark = task.remark || ''
      form.assigneeMode = task.assigneeMode || 1

      // 加载已有执行人
      try {
        const assigneeRes = await taskAssigneeApi.listByTaskId(taskId.value)
        const assigneeList = assigneeRes?.data || assigneeRes || []
        if (Array.isArray(assigneeList) && assigneeList.length > 0) {
          selectedAssignees.value = assigneeList.map((a: any) => ({
            userId: a.userId || a.assigneeId,
            name: a.assigneeName || '',
            userName: '',
          }))
          const primary = assigneeList.find((a: any) => a.assigneeType === 1)
          primaryAssigneeId.value = primary ? (primary.userId || primary.assigneeId) : selectedAssignees.value[0]?.userId
        }
      } catch (e) {
        // 忽略加载执行人失败
      }
    } catch (error) {
      console.error('加载任务失败:', error)
    } finally {
      loading.value = false
    }
  }

  function handleAssigneeConfirm(users: OrgUser[], primaryId?: number) {
    selectedAssignees.value = users
    primaryAssigneeId.value = primaryId || users[0]?.userId
    showAssigneePicker.value = false
  }

  function removeAssignee(userId: number) {
    selectedAssignees.value = selectedAssignees.value.filter(u => u.userId !== userId)
    if (primaryAssigneeId.value === userId) {
      primaryAssigneeId.value = selectedAssignees.value[0]?.userId
    }
  }

  /** 格式化日期显示 */
  function formatDeadlineDisplay(value: string): string {
    if (!value) return ''
    // 兼容 "yyyy-MM-dd" 和 "yyyy-MM-dd HH:mm:ss" 两种格式
    return value.substring(0, 10)
  }

  /** 日期选择确认 */
  function onDeadlineConfirm({ value }: { value: number }) {
    const d = new Date(value)
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    form.deadline = `${yyyy}-${mm}-${dd}`
    showDeadlinePicker.value = false
  }

  /** 打开日期选择器 */
  function openDeadlinePicker() {
    if (form.deadline) {
      // 将已有的日期字符串转为时间戳
      const d = new Date(form.deadline.substring(0, 10))
      if (!Number.isNaN(d.getTime())) {
        deadlineTimestamp.value = d.getTime()
      }
    } else {
      deadlineTimestamp.value = Date.now()
    }
    showDeadlinePicker.value = true
  }

  async function handleSubmit() {
    if (!form.title.trim()) {
      uni.showToast({ icon: 'none', title: '请输入任务标题' })
      return
    }

    loading.value = true
    try {
      const taskData: any = { ...form }
      // 确保 assigneeMode 是数字类型（radio 组件可能返回字符串）
      taskData.assigneeMode = Number(taskData.assigneeMode)
      const isMultiMode = taskData.assigneeMode === 2

      console.log('[handleSubmit] assigneeMode:', taskData.assigneeMode, 'isMultiMode:', isMultiMode, 'selectedAssignees:', selectedAssignees.value.length)

      // 单人模式直接带上 assigneeId
      if (!isMultiMode && selectedAssignees.value.length > 0) {
        taskData.assigneeId = selectedAssignees.value[0].userId
      }

      if (isEdit.value) {
        await taskApi.update(taskId.value, taskData)
        // 多人模式下更新执行人列表
        if (isMultiMode && selectedAssignees.value.length > 0) {
          await taskApi.assignMulti(taskId.value, selectedAssignees.value.map(u => u.userId), primaryAssigneeId.value)
        }
        uni.showToast({ icon: 'success', title: '修改成功' })
      } else {
        // 多人模式：将执行人信息随创建请求一起发送，避免两步操作的竞态条件
        if (isMultiMode && selectedAssignees.value.length > 0) {
          taskData.assigneeIds = selectedAssignees.value.map(u => u.userId)
          taskData.transientPrimaryId = primaryAssigneeId.value
        }
        await taskApi.create(taskData)
        console.log('[handleSubmit] 创建完成')
        uni.showToast({ icon: 'success', title: '创建成功' })
      }
      setTimeout(() => uni.navigateBack(), 1500)
    } catch (error) {
      console.error('[handleSubmit] 操作失败:', error)
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

      <!-- 分派模式（仅新建时可选） -->
      <view class="form-item" v-if="userStore.hasManagePermission">
        <view class="item-label">分派模式</view>
        <wd-radio-group v-model="form.assigneeMode">
          <wd-radio
            v-for="option in assigneeModeOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </wd-radio>
        </wd-radio-group>
      </view>

      <!-- 选择执行人（仅管理权限可见） -->
      <view class="form-item" v-if="userStore.hasManagePermission">
        <view class="item-label">
          {{ form.assigneeMode === 1 ? '指派执行人' : '指派多人协办' }}
        </view>

        <!-- 已选人员展示 -->
        <view class="selected-assignees" v-if="selectedAssignees.length > 0">
          <view
            class="assignee-tag"
            v-for="user in selectedAssignees"
            :key="user.userId"
            :class="{ primary: primaryAssigneeId === user.userId }"
          >
            {{ user.name }}
            <text class="primary-mark" v-if="form.assigneeMode === 2 && primaryAssigneeId === user.userId">⭐</text>
            <text class="remove-mark" @click.stop="removeAssignee(user.userId)">✕</text>
          </view>
        </view>

        <view class="picker-trigger" @click="showAssigneePicker = true">
          <text v-if="selectedAssignees.length > 0" class="picker-text">
            已选 {{ selectedAssignees.length }} 人，点击修改
          </text>
          <text v-else class="picker-placeholder">点击选择执行人</text>
          <text class="picker-arrow">›</text>
        </view>
      </view>

      <!-- 截止时间 -->
      <view class="form-item">
        <view class="item-label">截止时间</view>
        <view class="picker-trigger" @click="openDeadlinePicker">
          <text v-if="form.deadline" class="picker-text">
            {{ formatDeadlineDisplay(form.deadline) }}
          </text>
          <text v-else class="picker-placeholder">点击选择截止日期</text>
          <text class="picker-arrow">›</text>
        </view>
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

    <!-- 执行人选择弹窗 -->
    <AssigneePicker
      :visible="showAssigneePicker"
      :multi="form.assigneeMode === 2"
      @confirm="handleAssigneeConfirm"
      @close="showAssigneePicker = false"
    />

    <!-- 截止日期选择器 -->
    <wd-datetime-picker
      v-model="deadlineTimestamp"
      type="date"
      title="选择截止日期"
      :visible="showDeadlinePicker"
      @confirm="onDeadlineConfirm"
      @cancel="showDeadlinePicker = false"
    />
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

  .selected-assignees {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 16rpx;
  }

  .assignee-tag {
    display: inline-flex;
    align-items: center;
    gap: 6rpx;
    padding: 8rpx 20rpx;
    font-size: 24rpx;
    background-color: #e6f7ff;
    color: #1890ff;
    border-radius: 24rpx;
    border: 2rpx solid #91d5ff;

    &.primary {
      background-color: #fff7e6;
      color: #fa8c16;
      border-color: #ffd591;
    }
  }

  .primary-mark {
    font-size: 20rpx;
  }

  .remove-mark {
    font-size: 20rpx;
    color: #999;
    margin-left: 4rpx;
    padding: 0 4rpx;
  }

  .picker-trigger {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 24rpx;
    background-color: var(--wot-filled-content, #f2f3f5);
    border-radius: 12rpx;
    font-size: 28rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .picker-text {
    color: #1890ff;
  }

  .picker-placeholder {
    color: var(--wot-text-placeholder, #c0c4cc);
  }

  .picker-arrow {
    font-size: 36rpx;
    color: #999;
  }

  .submit-btn {
    margin-top: 40rpx;
  }
</style>