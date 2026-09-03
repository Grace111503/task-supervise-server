import http from '@/utils/http'

/** 逾期问责记录 */
export interface OverdueAccountability {
  accountabilityId: number
  taskId: number
  overdueDays?: number
  reason?: string
  disposition?: string
  archiveTime?: string
}

/** 逾期问责 API */
export const accountabilityApi = {
  /** 分页查询逾期问责列表 */
  page: (pageNo = 1, pageSize = 10) =>
    http.get<{ list: OverdueAccountability[]; total: number }>('/acceptance/accountability/page', { pageNo, pageSize }),

  /** 获取逾期问责详情 */
  getDetail: (accountabilityId: number) =>
    http.get<OverdueAccountability>(`/acceptance/accountability/${accountabilityId}`),

  /** 新增逾期问责 */
  add: (data: Partial<OverdueAccountability>) =>
    http.post<OverdueAccountability>('/acceptance/accountability', data),

  /** 更新逾期问责 */
  update: (data: Partial<OverdueAccountability>) =>
    http.put<OverdueAccountability>('/acceptance/accountability', data),

  /** 删除逾期问责 */
  delete: (accountabilityId: number) =>
    http.delete(`/acceptance/accountability/${accountabilityId}`),

  /** 按 taskId 查询逾期问责记录 */
  listByTaskId: (taskId: number) =>
    http.get<OverdueAccountability[]>(`/acceptance/accountability/task/${taskId}`),

  /** 登记逾期原因 */
  recordReason: (taskId: number, reason: string, overdueDays?: number) =>
    http.post<OverdueAccountability>(`/acceptance/accountability/overdue/${taskId}/reason`, { reason, overdueDays }),

  /** 登记追责处置 */
  recordAccountability: (taskId: number, disposition: string, overdueDays?: number) =>
    http.post<OverdueAccountability>(`/acceptance/accountability/overdue/${taskId}/accountability`, { disposition, overdueDays }),
}

export default accountabilityApi
