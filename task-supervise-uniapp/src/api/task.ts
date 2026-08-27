import http from '@/utils/http'

/** 任务状态 */
export type TaskStatus = 'pending' | 'in_progress' | 'completed' | 'overdue'

/** 任务优先级 */
export type TaskPriority = 'high' | 'medium' | 'low'

/** 任务信息 */
export interface Task {
  assigneeId?: number
  assigneeName?: string
  attachments?: string[]
  createdAt: string
  creatorId?: number
  creatorName?: string
  deadline?: string
  description?: string
  id: number
  priority: TaskPriority
  remark?: string
  status: TaskStatus
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

/** 任务 API */
export const taskApi = {
  /** 指派任务 */
  assign: (id: number, assigneeId: number) =>
    http.post(`/task/${id}/assign`, { assigneeId }),

  /** 创建任务 */
  create: (params: CreateTaskParams) => http.post<Task>('/task', params),

  /** 删除任务 */
  delete: (id: number) => http.delete(`/task/${id}`),

  /** 获取任务详情 */
  getDetail: (id: number) => http.get<Task>(`/task/${id}`),
  /** 获取任务列表 */
  getList: (params?: TaskListParams) =>
    http.get<TaskListResult>('/task/list', params),

  /** 获取我的任务统计 */
  getStatistics: () => http.get('/task/statistics'),

  /** 更新任务 */
  update: (id: number, params: UpdateTaskParams) =>
    http.put<Task>(`/task/${id}`, params),

  /** 更新任务状态 */
  updateStatus: (id: number, status: TaskStatus) =>
    http.put(`/task/${id}/status`, { status }),
}

export default taskApi
