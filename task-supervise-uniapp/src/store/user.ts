import { authApi } from '~/api/auth'
import type { DataInfo } from '~/utils/http/auth'
import {
  AccessTokenKey,
  ExpiresKey,
  RefreshTokenKey,
  removeToken,
  setToken,
} from '~/utils/http/auth'

const { login, loginByEmail } = authApi

export const useUserStore = defineStore(
  'user',
  () => {
    const userInfo = ref<User.UserInfo>({} as User.UserInfo)
    const isLogin = computed(() => {
      const { id } = userInfo.value
      return !!id
    })
    const token = computed(() => userInfo.value.token)
    const userId = computed(() => userInfo.value.id)
    const userRole = computed(() => userInfo.value.role || 'user')
    const userRoleDesc = computed(() => userInfo.value.roleDesc || '普通执行人员')
    const userDeptId = computed(() => userInfo.value.deptId)
    const userPosition = computed(() => userInfo.value.position)

    /** 是否为督办管理员 */
    const isAdmin = computed(() => userRole.value === 'admin')
    /** 是否为部门主管 */
    const isManager = computed(() => userRole.value === 'manager')
    /** 是否为普通执行人员 */
    const isUser = computed(() => userRole.value === 'user')
    /** 是否有管理权限（管理员或主管） */
    const hasManagePermission = computed(() => isAdmin.value || isManager.value)

    const setUserInfo = (data: User.UserInfo) => {
      userInfo.value = data
    }
    const handleLoginResponse = (res: any) => {
      const { code, data, message } = res
      // 后端格式: { code: 200, data: { status, admin, token } }
      if (code === 200 && data?.status !== 400) {
        const { admin, token } = data
        setUserInfo(admin)
        const TokenInfo: DataInfo<number> = {
          accessToken: token[AccessTokenKey],
          expires: token[ExpiresKey] * 1000,
          refreshToken: token[RefreshTokenKey],
          roles: [admin.role || 'user'],
          username: admin.name,
        }
        setToken(TokenInfo)
        return true
      }
      // 登录失败时返回错误信息
      throw new Error(data?.message || message || '登录失败')
    }
    const loginFunc = (dataT: any) =>
      new Promise<any>((resolve, reject) => {
        login(dataT)
          .then((res: any) => {
            if (handleLoginResponse(res)) resolve(res)
            else reject(res)
          })
          .catch(reject)
      })
    const loginFuncByEmail = (dataT: any) =>
      new Promise<any>((resolve, reject) => {
        loginByEmail(dataT)
          .then((res: any) => {
            if (handleLoginResponse(res)) resolve(res)
            else reject(res)
          })
          .catch(reject)
      })
    const logOut = () => {
      userInfo.value = {} as User.UserInfo
      removeToken()
    }

    return {
      isLogin,
      isAdmin,
      isManager,
      isUser,
      hasManagePermission,
      userRole,
      userRoleDesc,
      userDeptId,
      userPosition,
      loginFunc,
      loginFuncByEmail,
      logOut,
      setUserInfo,
      token,
      userId,
      userInfo,
    }
  },
  {
    persist: {
      key: 'user-key',
      storage: {
        getItem: uni.getStorageSync,
        setItem: uni.setStorageSync,
      },
    },
  }
)

export default useUserStore
