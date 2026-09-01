import http from '@/utils/http'

/** 后端 SysUser 字段（JSON 序列化后的 key） */
export interface OrgUser {
  userId: number
  userName: string
  name: string
  deptId?: number
  deptName?: string
  roleCode?: string
  position?: string
}

export const orgApi = {
  /** 分页查询用户列表 */
  getUserList: (page = 1, pageSize = 50, keyword?: string) =>
    http.get<{ list: OrgUser[]; total: number }>('/org/user/page', { page, pageSize, keyword }),
}