import http from '@/utils/http'

/** 站内消息 */
export interface InAppMessage {
  msgId: number
  userId: number
  title: string
  content: string
  level: number // 1-普通 2-重要 3-紧急
  msgType?: string // TASK-任务通知 WARN-预警 ACCEPT-验收通知
  relatedId?: number // 关联任务ID
  readStatus: number // 0-未读 1-已读
  createdAt: string
}

/** 消息 API */
export const messageApi = {
  /** 获取当前用户消息列表 */
  getMyMessages: (readStatus?: number) =>
    http.get<InAppMessage[]>('/warn/message/my', { isRead: readStatus }),

  /** 获取未读消息数 */
  getUnreadCount: () =>
    http.get<number>('/warn/message/unread-count'),

  /** 标记消息已读 */
  markAsRead: (msgId: number) =>
    http.put(`/warn/message/${msgId}/read`),

  /** 标记所有消息已读 */
  markAllAsRead: () =>
    http.put('/warn/message/read-all'),
}

export default messageApi