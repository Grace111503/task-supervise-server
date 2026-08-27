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
      // return (token && id);
    })
    const token = computed(() => userInfo.value.token)
    const userId = computed(() => userInfo.value.id)
    const setUserInfo = (data: User.UserInfo) => {
      userInfo.value = data
    }
    const handleLoginResponse = (res: any) => {
      const { code, result } = res
      if (code === 200 && result?.status !== 400) {
        const { admin, token } = result
        setUserInfo(admin)
        const TokenInfo: DataInfo<number> = {
          accessToken: token[AccessTokenKey],
          expires: token[ExpiresKey] * 1000,
          refreshToken: token[RefreshTokenKey],
          roles: [admin.name],
          username: admin.name,
        }
        setToken(TokenInfo)
        return true
      }
      return false
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
