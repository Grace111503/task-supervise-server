<script lang="ts" setup>
  import { taskApi } from '~/api/task'
  import type { Task } from '~/api/task'
  import { accountabilityApi } from '~/api/accountability'
  import type { OverdueAccountability } from '~/api/accountability'
  import { useUserStore } from '@/store/user'

  definePage(() => ({
    layout: 'default',
    style: {
      navigationBarTitleText: '逾期处置',
    },
  }))

  const userStore = useUserStore()
  const taskId = ref(0)
  const task = ref<Task | null>(null)
  const accountability = ref<OverdueAccountability | null>(null)
  const loading = ref(true)
  const saving = ref(false)

  // 表单数据
  const reasonForm = reactive({
    reason: '',
    overdueDays: 0,
  })
  const dispositionForm = reactive({
    disposition: '',
  })

  /** 是否有管理权限（管理员/主管） */
  const canEdit = computed(() => userStore.hasManagePermission)

  const statusMap: Record<string, { text: string; color: string }> = {
    completed: { color: '#07C160', text: '已完成' },
    in_progress: { color: '#1890ff', text: '进行中' },
    overdue: { color: '#f5222d', text: '已逾期' },
    pending: { color: '#8c8c8c', text: '待接收' },
    pending_feedback: { color: '#fa8c16', text: '待反馈' },
    pending_accept: { color: '#722ed1', text: '待验收' },
  }

  function formatDeadline(deadline?: string): string {
    if (!deadline) return ''
    return deadline.replace('T', ' ').replace(/\.\d+$/, '').slice(0, 16)
  }

  function formatTime(time?: string): string {
    if (!time) return ''
    return time.replace('T', ' ').replace(/\.\d+$/, '').slice(0, 19)
  }

  /** 从 accountability 记录同步到表单 */
  function syncFormFromRecord() {
    if (accountability.value) {
      reasonForm.reason = accountability.value.reason || ''
      reasonForm.overdueDays = accountability.value.overdueDays || 0
      dispositionForm.disposition = accountability.value.disposition || ''
    }
  }

  async function loadData() {
    loading.value = true
    try {
      // 并行加载任务详情和问责记录
      const [taskRes, accountabilityRes] = await Promise.all([
        taskApi.getDetail(taskId.value),
        accountabilityApi.listByTaskId(taskId.value),
      ])

      task.value = taskRes?.data || taskRes
      const list = accountabilityRes?.data || accountabilityRes || []
      const records = Array.isArray(list) ? list : []
      accountability.value = records.length > 0 ? records[0] : null

      // 填充表单
      syncFormFromRecord()

      // 自动计算逾期天数
      if (task.value?.deadline) {
        const deadline = new Date(task.value.deadline.replace('T', ' ').replace(/\.\d+$/, ''))
        const now = new Date()
        const diff = Math.floor((now.getTime() - deadline.getTime()) / 86400000)
        if (diff > 0 && !accountability.value) {
          reasonForm.overdueDays = diff
        }
      }
    } catch (error) {
      console.error('加载逾期处置数据失败:', error)
      uni.showToast({ icon: 'none', title: '加载失败' })
    } finally {
      loading.value = false
    }
  }

  /** 重新加载问责记录并同步表单 */
  async function reloadAccountability() {
    const res = await accountabilityApi.listByTaskId(taskId.value)
    const list = res?.data || res || []
    const records = Array.isArray(list) ? list : []
    accountability.value = records.length > 0 ? records[0] : null
    syncFormFromRecord()
  }

  async function saveReason() {
    if (!reasonForm.reason.trim()) {
      uni.showToast({ icon: 'none', title: '请填写逾期原因' })
      return
    }
    saving.value = true
    try {
      await accountabilityApi.recordReason(taskId.value, reasonForm.reason, reasonForm.overdueDays)
      uni.showToast({ icon: 'success', title: '保存成功' })
      await reloadAccountability()
    } catch (error) {
      console.error('保存逾期原因失败:', error)
      uni.showToast({ icon: 'none', title: '保存失败' })
    } finally {
      saving.value = false
    }
  }

  async function saveDisposition() {
    if (!dispositionForm.disposition.trim()) {
      uni.showToast({ icon: 'none', title: '请填写处置措施' })
      return
    }
    saving.value = true
    try {
      await accountabilityApi.recordAccountability(taskId.value, dispositionForm.disposition, reasonForm.overdueDays)
      uni.showToast({ icon: 'success', title: '保存成功' })
      await reloadAccountability()
    } catch (error) {
      console.error('保存处置措施失败:', error)
      uni.showToast({ icon: 'none', title: '保存失败' })
    } finally {
      saving.value = false
    }
  }

  function goTaskDetail() {
    uni.navigateTo({ url: `/pages/home/detail?id=${taskId.value}` })
  }

  onLoad((options) => {
    taskId.value = Number(options?.taskId || options?.id || 0)
    if (taskId.value) {
      loadData()
    } else {
      loading.value = false
      uni.showToast({ icon: 'none', title: '缺少任务ID' })
    }
  })
</script>

<template>
  <view class="overdue-detail-container">
    <wd-loading v-if="loading" />

    <template v-if="!loading && task">
      <!-- 任务基本信息 -->
      <view class="task-card">
        <view class="task-header">
          <view class="task-title">{{ task.title }}</view>
          <view class="status-badge" :style="{ backgroundColor: (statusMap[task.status] || statusMap.overdue).color }">
            {{ (statusMap[task.status] || statusMap.overdue).text }}
          </view>
        </view>

        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">负责人</text>
            <text class="info-value">{{ task.assigneeName || '未指派' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">截止日期</text>
            <text class="info-value deadline">{{ formatDeadline(task.deadline) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">逾期天数</text>
            <text class="info-value overdue">{{ reasonForm.overdueDays }} 天</text>
          </view>
          <view class="info-item">
            <text class="info-label">创建人</text>
            <text class="info-value">{{ task.creatorName || '-' }}</text>
          </view>
        </view>

        <view class="go-detail" @click="goTaskDetail">
          <text>查看任务详情 ›</text>
        </view>
      </view>

      <!-- 逾期原因登记 -->
      <view class="section-card">
        <view class="section-header">
          <view class="section-title">
            <text class="section-icon">📝</text>
            <text>逾期客观原因</text>
          </view>
        </view>

        <view class="section-body">
          <wd-textarea
            v-if="canEdit"
            v-model="reasonForm.reason"
            placeholder="请登记逾期的客观原因（如：资源不足、需求变更、外部依赖等）"
            :maxlength="1000"
            show-word-limit
            :rows="4"
          />
          <view v-else class="readonly-text">
            {{ reasonForm.reason || '暂未登记' }}
          </view>

          <view class="overdue-days-row" v-if="canEdit">
            <text class="days-label">逾期天数:</text>
            <input
              class="days-input"
              type="number"
              v-model.number="reasonForm.overdueDays"
              placeholder="0"
            />
            <text class="days-unit">天</text>
          </view>

          <view class="archive-info" v-if="accountability?.reason">
            <text class="archive-label">已登记</text>
          </view>
        </view>

        <view class="section-footer" v-if="canEdit">
          <wd-button
            type="primary"
            size="small"
            :loading="saving"
            @click="saveReason"
          >
            保存原因
          </wd-button>
        </view>
      </view>

      <!-- 人员处置追责记录 -->
      <view class="section-card">
        <view class="section-header">
          <view class="section-title">
            <text class="section-icon">⚖️</text>
            <text>人员处置追责记录</text>
          </view>
          <view class="section-badge" v-if="canEdit">仅管理员可见</view>
        </view>

        <view class="section-body">
          <template v-if="canEdit">
            <wd-textarea
              v-model="dispositionForm.disposition"
              placeholder="请登记人员处置追责措施（如：通报批评、绩效扣分、书面检查等）"
              :maxlength="500"
              show-word-limit
              :rows="4"
            />
            <view class="archive-info" v-if="accountability?.disposition">
              <text class="archive-label">已登记</text>
            </view>
          </template>
          <template v-else>
            <view class="readonly-text restricted">
              🔒 仅管理员/主管可查看处置追责记录
            </view>
          </template>
        </view>

        <view class="section-footer" v-if="canEdit">
          <wd-button
            type="warning"
            size="small"
            :loading="saving"
            @click="saveDisposition"
          >
            保存处置
          </wd-button>
        </view>
      </view>

      <!-- 归档信息 -->
      <view class="section-card archive-card" v-if="accountability?.archiveTime">
        <view class="archive-row">
          <text class="archive-icon">📁</text>
          <text class="archive-text">归档时间：{{ formatTime(accountability.archiveTime) }}</text>
        </view>
        <view class="archive-hint">此记录永久留存，作为人员绩效考核依据</view>
      </view>
    </template>

    <!-- 空状态 -->
    <view class="empty-state" v-if="!loading && !task">
      <text class="empty-icon">📋</text>
      <text class="empty-text">任务不存在</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .overdue-detail-container {
    min-height: 100%;
    background-color: var(--wot-filled-bottom, #f7f8fa);
    padding: 24rpx;
    padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  }

  .task-card {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 24rpx;
    border-left: 8rpx solid #f5222d;
  }

  .task-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24rpx;
    gap: 16rpx;
  }

  .task-title {
    flex: 1;
    font-size: 34rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
    line-height: 1.4;
  }

  .status-badge {
    flex-shrink: 0;
    font-size: 22rpx;
    color: #ffffff;
    padding: 4rpx 16rpx;
    border-radius: 16rpx;
  }

  .info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16rpx;
    margin-bottom: 20rpx;
  }

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  .info-label {
    font-size: 24rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }

  .info-value {
    font-size: 28rpx;
    color: var(--wot-text-main, #1d2129);
    font-weight: 500;

    &.deadline {
      color: #f5222d;
    }

    &.overdue {
      color: #f5222d;
      font-weight: 600;
      font-size: 32rpx;
    }
  }

  .go-detail {
    text-align: right;
    font-size: 26rpx;
    color: #07c160;
    font-weight: 500;
    padding-top: 16rpx;
    border-top: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .section-card {
    background-color: var(--wot-filled-oppo, #ffffff);
    border-radius: 16rpx;
    margin-bottom: 24rpx;
    overflow: hidden;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 30rpx;
    border-bottom: 1rpx solid var(--wot-border-color, #e5e6eb);
    background-color: #fafafa;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 30rpx;
    font-weight: 600;
    color: var(--wot-text-main, #1d2129);
  }

  .section-icon {
    font-size: 36rpx;
  }

  .section-badge {
    font-size: 22rpx;
    color: #fa8c16;
    background-color: #fff7e6;
    padding: 4rpx 16rpx;
    border-radius: 16rpx;
    border: 1rpx solid #ffd591;
  }

  .section-body {
    padding: 24rpx 30rpx;
  }

  .readonly-text {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    line-height: 1.6;
    padding: 16rpx 0;
    min-height: 80rpx;

    &.restricted {
      text-align: center;
      color: var(--wot-text-auxiliary, #869a9c);
      padding: 40rpx 0;
    }
  }

  .overdue-days-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-top: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid var(--wot-border-color, #e5e6eb);
  }

  .days-label {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
    flex-shrink: 0;
  }

  .days-input {
    width: 120rpx;
    height: 64rpx;
    text-align: center;
    background-color: #f2f3f5;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .days-unit {
    font-size: 28rpx;
    color: var(--wot-text-secondary, #4e5969);
  }

  .archive-info {
    margin-top: 16rpx;
    display: flex;
    align-items: center;
    gap: 8rpx;
  }

  .archive-label {
    font-size: 22rpx;
    color: #07c160;
    background-color: #f6ffed;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    border: 1rpx solid #b7eb8f;
  }

  .section-footer {
    padding: 20rpx 30rpx;
    border-top: 1rpx solid var(--wot-border-color, #e5e6eb);
    display: flex;
    justify-content: flex-end;
  }

  .archive-card {
    padding: 24rpx 30rpx;
  }

  .archive-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 12rpx;
  }

  .archive-icon {
    font-size: 32rpx;
  }

  .archive-text {
    font-size: 26rpx;
    color: var(--wot-text-main, #1d2129);
  }

  .archive-hint {
    font-size: 22rpx;
    color: var(--wot-text-auxiliary, #869a9c);
    line-height: 1.5;
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
    font-size: 30rpx;
    color: var(--wot-text-auxiliary, #869a9c);
  }
</style>
