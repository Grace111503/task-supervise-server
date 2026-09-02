import http from '@/utils/http'

/** 登录参数 */
export interface LoginParams {
  password: string
  username: string
}

/** 注册参数 */
export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
  name: string
  deptId: number
  position?: string
  email?: string
  phone?: string
}

/** 登录响应 */
export interface LoginResult {
  admin: {
    id: number
    name: string
    avatar?: string
    role?: string
    /** 角色描述（中文） */
    roleDesc?: string
    /** 所属部门ID */
    deptId?: number
    /** 职位 */
    position?: string
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
  roleDesc?: string
  deptId?: number
  position?: string
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

  /** 用户注册 */
  register: (params: RegisterParams) => http.post<void>('/auth/register', params),

  /** 邮箱登录 */
  loginByEmail: (params: { email: string; password: string }) =>
    http.post<LoginResult>('/auth/login/email', params),

  /** 退出登录 */
  logout: () => http.post('/auth/logout'),

  /** 更新用户信息 */
  updateUserInfo: (data: Partial<UserInfo>) =>
    http.put<UserInfo>('/user/info', data),

  /**
   * 上传头像
   * @param filePath 本地图片临时路径（uni.chooseImage 返回）
   * @returns 头像访问路径
   */
  uploadAvatar: (filePath: string): Promise<string> => {
    return new Promise((resolve, reject) => {
      // 直接从本地存储读取 token，避免循环依赖
      let accessToken = ''
      try {
        const raw = uni.getStorageSync('authorized-token')
        if (raw) {
          const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
          accessToken = parsed.accessToken || ''
        }
      } catch {}

      const baseUrl = 'http://localhost:8082/api/v1'

      // 通过 URL 参数传递 token（uni.uploadFile 的 header 在部分平台不生效）
      // 后端 AuthInterceptor 已支持从 query param "token" 读取
      const uploadUrl = accessToken
        ? `${baseUrl}/user/avatar?token=Bearer%20${accessToken}`
        : `${baseUrl}/user/avatar`

      uni.uploadFile({
        url: uploadUrl,
        filePath,
        name: 'file',
        success: (res) => {
          if (res.statusCode === 200) {
            try {
              const data = JSON.parse(res.data as string)
              if (data.code === 200) {
                resolve(data.data)
              } else {
                uni.showToast({ icon: 'none', title: data.message || '上传失败' })
                reject(new Error(data.message || '上传失败'))
              }
            } catch (e) {
              uni.showToast({ icon: 'none', title: '解析响应失败' })
              reject(e)
            }
          } else if (res.statusCode === 401) {
            uni.showToast({ icon: 'none', title: '登录已过期，请重新登录' })
            reject(new Error('未授权'))
          } else {
            uni.showToast({ icon: 'none', title: `上传失败(${res.statusCode})` })
            reject(new Error(`HTTP ${res.statusCode}`))
          }
        },
        fail: (err) => {
          uni.showToast({ icon: 'none', title: '网络错误，上传失败' })
          reject(err)
        },
      })
    })
  },
}

export default authApi
