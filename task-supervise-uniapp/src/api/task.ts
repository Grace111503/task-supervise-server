import http from '@/utils/http'

/** 任务状态 */
export type TaskStatus = 'pending' | 'in_progress' | 'pending_feedback' | 'pending_accept' | 'completed' | 'overdue'

/** 任务优先级 */
export type TaskPriority = 'high' | 'medium' | 'low'

/** 任务分派模式 */
export type AssigneeMode = 1 | 2 // 1-单人 2-多人协办

/** 任务信息 */
export interface Task {
  assigneeId?: number
  assigneeName?: string
  assigneeMode?: AssigneeMode
  multiAssigneeIds?: number[]
  attachments?: string[]
  createdAt: string
  creatorId?: number
  creatorName?: string
  deadline?: string
  description?: string
  groupId?: number
  id: number
  taskName?: string
  priority: TaskPriority
  remark?: string
  rejectRemark?: string
  rejectedAt?: string
  acceptResult?: number
  acceptRemark?: string
  acceptedAt?: string
  remainingDays?: number
  overdueDays?: number
  feedbackCount?: number
  status: TaskStatus
  templateId?: number
  title: string
  updatedAt: string
}

/** 任务列表查询参数 */
export interface TaskListParams {
  assigneeId?: number
  keyword?: string
  page?: number
  pageSize?: number
  priority?: TaskPriority
  status?: TaskStatus
}

/** 任务列表响应 */
export interface TaskListResult {
  list: Task[]
  page: number
  pageSize: number
  total: number
}

/** 创建任务参数 */
export interface CreateTaskParams {
  assigneeId?: number
  deadline?: string
  description?: string
  priority?: TaskPriority
  remark?: string
  title: string
}

/** 更新任务参数 */
export interface UpdateTaskParams extends Partial<CreateTaskParams> {
  status?: TaskStatus
}

/** 任务模板信息 */
export interface TaskTemplate {
  templateId: number
  templateName: string
  templateType: number
  description?: string
  defaultContent?: string
  defaultPriority?: number
  standardFeedbackReq?: string
  groupId?: number
  status: number
}

/** 任务指派人信息 */
export interface TaskAssignee {
  assigneeId: number
  taskId: number
  userId: number
  assigneeName: string
  assigneeType: number // 1-主负责人 2-协助人
  status: string
}

/** 任务 API */
export const taskApi = {
  /** 指派任务（单人模式） */
  assign: (id: number, assigneeId: number) =>
    http.post(`/task/${id}/assign`, { assigneeId }),

  /** 多人协办模式分派任务 */
  assignMulti: (taskId: number, assigneeIds: number[], primaryId?: number) =>
    http.post('/task/assign-multi', { taskId, assigneeIds, primaryId }),

  /** 批量分派任务 */
  batchAssign: (taskIds: number[], assigneeId: number) =>
    http.post('/task/batch-assign', { taskIds, assigneeId }),

  /** 根据模板创建任务 */
  createByTemplate: (templateId: number, params?: Partial<CreateTaskParams>) =>
    http.post('/task/create-by-template', { templateId, ...params }),

  /** 创建任务 */
  create: (params: CreateTaskParams) => http.post<Task>('/task', params),

  /** 删除任务 */
  delete: (id: number) => http.delete(`/task/${id}`),

  /** 获取任务详情 */
  getDetail: (id: number) => http.get<Task>(`/task/${id}`),

  /** 获取任务列表 */
  getList: (params?: TaskListParams) =>
    http.get<TaskListResult>('/task/list', params),

  /** 按分组查询任务列表 */
  getListByGroup: (groupId: number, page?: number, pageSize?: number) =>
    http.get<TaskListResult>(`/task/group/${groupId}`, { page, pageSize }),

  /** 获取我的任务统计 */
  getStatistics: () => http.get('/task/statistics'),

  /** 更新任务 */
  update: (id: number, params: UpdateTaskParams) =>
    http.put<Task>(`/task/${id}`, params),

  /** 更新任务状态 */
  updateStatus: (id: number, status: TaskStatus) =>
    http.put(`/task/${id}/status`, { status }),

  // ========== 模板相关接口 ==========

  /** 获取启用的模板列表 */
  getEnabledTemplates: (templateType?: number) =>
    http.get<TaskTemplate[]>('/task/template/enabled', { templateType }),

  /** 获取模板详情 */
  getTemplateDetail: (templateId: number) =>
    http.get<TaskTemplate>(`/task/template/${templateId}`),

  /** 获取模板统计 */
  getTemplateStatistics: () =>
    http.get('/task/template/statistics'),

  /** 驳回已完成的任务（管理员/主管） */
  reject: (id: number, rejectRemark: string) =>
    http.put(`/task/${id}/reject`, { rejectRemark }),

  /** 验收任务（管理员/主管） */
  accept: (id: number, acceptResult: number, acceptRemark: string) =>
    http.put(`/task/${id}/accept`, { acceptResult, acceptRemark }),

  /** 获取任务全流程时间线 */
  getTimeline: (id: number) =>
    http.get(`/task/${id}/timeline`),

  /** 获取逾期任务列表 */
  overdueList: (params?: { page?: number; limit?: number }) =>
    http.get('/task/overdue/list', params),

  /** 批量处置逾期任务 */
  batchOverdueAction: (taskIds: string[], action: string, remark: string) =>
    http.post('/task/batch-overdue-action', { taskIds, action, remark }),
}

/** 任务指派人 API */
export const taskAssigneeApi = {
  /** 获取任务指派人列表 */
  listByTaskId: (taskId: number) =>
    http.get<TaskAssignee[]>(`/task/assignee/task/${taskId}`),

  /** 批量添加指派人 */
  batchCreate: (taskId: number, userIds: number[], assigneeType?: number) =>
    http.post('/task/assignee/batch', { taskId, userIds, assigneeType }),

  /** 更新指派类型 */
  updateAssigneeType: (id: number, assigneeType: number) =>
    http.put(`/task/assignee/${id}/type`, { assigneeType }),

  /** 按任务ID和指派类型查询指派人列表 */
  listByTaskIdAndType: (taskId: number, assigneeType: number) =>
    http.get<TaskAssignee[]>(`/task/assignee/task/${taskId}/type/${assigneeType}`),
}

export default taskApi
