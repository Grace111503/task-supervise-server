import http from '@/utils/http'

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

  /** 获取文件下载URL */
  getDownloadUrl: (fileId: number) => `/api/v1/file/download/${fileId}`,

  /** 获取文件预览URL */
  getPreviewUrl: (fileId: number) => `/api/v1/file/preview/${fileId}`,

  /** 删除文件 */
  delete: (fileId: number) =>
    http.delete(`/file/${fileId}`),
}

export default feedbackApi