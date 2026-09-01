import http from '@/utils/http'
import apiServer from '@/config/domain'
import { getToken } from '@/utils/http/auth'

/** 进度反馈信息 */
export interface ProgressFeedback {
  feedbackId: number
  taskId: number
  userId: number
  userName?: string
  completedContent?: string
  nextPlan?: string
  progressPercent: number
  stage: number
  feedbackTime: string
  files?: TaskFile[]
}

/** 任务文件信息 */
export interface TaskFile {
  fileId: number
  taskId?: number
  feedbackId?: number
  originalName: string
  storedName?: string
  filePath: string
  fileSize: number
  fileType: string
  uploaderId?: number
  uploaderName?: string
  uploadTime: string
}

/** 添加反馈参数 */
export interface AddFeedbackParams {
  taskId: number
  completedContent?: string
  nextPlan?: string
  progressPercent?: number
  fileIds?: number[]
}

/** 执行人进度概览 */
export interface AssigneeProgress {
  userId: number
  userName: string
  assigneeType: number
  latestProgress: number
  stage: number
  latestContent?: string
  latestTime?: string
}

/** 进度反馈 API */
export const feedbackApi = {
  /** 按任务ID查询反馈列表（含文件） */
  listByTaskIdWithFiles: (taskId: number) =>
    http.get<ProgressFeedback[]>(`/feedback/progress/task/${taskId}/with-files`),

  /** 按任务ID查询反馈列表 */
  listByTaskId: (taskId: number) =>
    http.get<ProgressFeedback[]>(`/feedback/progress/task/${taskId}`),

  /** 获取反馈详情（含文件） */
  getDetailWithFiles: (feedbackId: number) =>
    http.get<ProgressFeedback>(`/feedback/progress/${feedbackId}/with-files`),

  /** 获取反馈详情 */
  getDetail: (feedbackId: number) =>
    http.get<ProgressFeedback>(`/feedback/progress/${feedbackId}`),

  /** 添加反馈并关联文件 */
  addWithFiles: (params: AddFeedbackParams) =>
    http.post<ProgressFeedback>('/feedback/progress/add-with-files', params),

  /** 添加反馈 */
  add: (params: { taskId: number; completedContent?: string; nextPlan?: string; progressPercent?: number }) =>
    http.post<ProgressFeedback>('/feedback/progress', params),

  /** 获取任务的下一个阶段号 */
  getNextStage: (taskId: number) =>
    http.get<{ stage: number }>(`/feedback/progress/task/${taskId}/next-stage`),

  /** 查询多人任务各执行人的进度概览 */
  getAssigneeProgress: (taskId: number) =>
    http.get<AssigneeProgress[]>(`/feedback/progress/task/${taskId}/assignee-progress`),
}

/** 文件 API */
export const fileApi = {
  /** 上传文件（支持 File 或 FormData） */
  upload: (fileOrFormData: File | FormData, taskId?: number, feedbackId?: number) => {
    let formData: FormData
    if (fileOrFormData instanceof FormData) {
      formData = fileOrFormData
    } else {
      formData = new FormData()
      formData.append('file', fileOrFormData)
      if (taskId) formData.append('taskId', String(taskId))
      if (feedbackId) formData.append('feedbackId', String(feedbackId))
    }
    return http.post<TaskFile>('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  /** 按任务ID查询文件列表 */
  listByTaskId: (taskId: number) =>
    http.get<TaskFile[]>(`/file/task/${taskId}`),

  /** 按反馈ID查询文件列表 */
  listByFeedbackId: (feedbackId: number) =>
    http.get<TaskFile[]>(`/file/feedback/${feedbackId}`),

  /** 获取文件下载URL（带 token，用于 window.open / 浏览器直接访问） */
  getDownloadUrl: (fileId: number) => {
    const token = getToken()?.accessToken || ''
    return `${(apiServer as any).baseServer}/file/download/${fileId}?token=Bearer ${encodeURIComponent(token)}`
  },

  /** 获取文件预览URL（带 token，用于 window.open / 浏览器直接访问） */
  getPreviewUrl: (fileId: number) => {
    const token = getToken()?.accessToken || ''
    return `${(apiServer as any).baseServer}/file/preview/${fileId}?token=Bearer ${encodeURIComponent(token)}`
  },

  /** 删除文件 */
  delete: (fileId: number) =>
    http.delete(`/file/${fileId}`),
}

export default feedbackApi