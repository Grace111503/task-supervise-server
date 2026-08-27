import http from '@/utils/http'

/** 登录参数 */
export interface LoginParams {
  password: string
  username: string
}

/** 登录响应 */
export interface LoginResult {
  admin: {
    id: number
    name: string
    avatar?: string
    role?: string
  }
  token: {
    access_token: string
    refresh_token: string
    expires_in: number
  }
}

/** 用户信息 */
export interface UserInfo {
  avatar?: string
  email?: string
  id: number
  name: string
  phone?: string
  role?: string
}

/** 认证 API */
export const authApi = {
  /** 修改密码 */
  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    http.post('/user/password', data),

  /** 获取用户信息 */
  getUserInfo: () => http.get<UserInfo>('/user/info'),
  /** 用户登录 */
  login: (params: LoginParams) => http.post<LoginResult>('/auth/login', params),

  /** 邮箱登录 */
  loginByEmail: (params: { email: string; password: string }) =>
    http.post<LoginResult>('/auth/login/email', params),

  /** 退出登录 */
  logout: () => http.post('/auth/logout'),

  /** 更新用户信息 */
  updateUserInfo: (data: Partial<UserInfo>) =>
    http.put<UserInfo>('/user/info', data),
}

export default authApi
