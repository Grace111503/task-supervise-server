// API 相关的类型声明

declare namespace GetTest {
  interface params {
    id?: string | number
    limit?: number
    page?: number
  }

  interface data {
    code: number
    data: any[]
    message: string
  }
}

declare namespace PostTest {
  interface params {
    description?: string
    name: string
  }

  interface data {
    code: number
    data: {
      id: number
      name: string
      description?: string
    }
    message: string
  }
}

// 通用 API 响应类型
export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

// 分页相关类型
export interface PaginationParams {
  keyword?: string
  limit?: number
  page?: number
}

export interface PaginationResponse<T> {
  current: number
  list: T[]
  pageSize: number
  total: number
}

// 用户相关类型
export interface UserInfo {
  avatar?: string
  createTime?: string
  email?: string
  id: number
  name: string
  phone?: string
}

export interface LoginParams {
  code?: string
  password: string
  username: string
}

export interface LoginResponse {
  refreshToken: string
  token: string
  userInfo: UserInfo
}

// 上传相关类型
export interface UploadResponse {
  filename: string
  size: number
  url: string
}

// Topic 相关类型
export interface TopicItem {
  content: string
  createTime: string
  fileList?: Array<{
    url: string
    type: string
  }>
  id: number
  TopicTag?: Array<{
    id: number
    name: string
    color?: string
  }>
  title: string
  User?: UserInfo
  updateTime: string
  userId: number
}
