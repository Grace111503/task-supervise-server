import { isEmpty } from '@iceywu/utils'
import un from '@uni-helper/uni-network'
import apiServer from '@/config/domain'
import { useUserStore } from '@/store/user'
import { formatToken, getToken, removeToken } from '@/utils/http/auth'
import { clearNonceCache, encrypt } from '@/utils/http/sign'
import { hideLoading, showLoading } from '~/utils/tools/serviceLoading'

const CONTENT_TYPE_JSON = 'application/json'
const CONTENT_TYPE_JSON_UTF8 = 'application/json; charset=utf-8'

interface RequestConfig {
  _cacheKey?: string
  allowDuplicateNonce?: boolean
  data?: any
  headers?: Record<string, any>
  isNeedEncrypt?: boolean
  isNeedToken?: boolean
  isShowLoading?: boolean | string
  method?: string
  params?: Record<string, any>
  serverName?: string
  tokenRoleName?: string
  url?: string
  [key: string]: any
}

const _requests: Array<(token: string) => void> = []
let _isRefreshing = false
let _isNeedToken = true
let _isNeedEncrypt = false
let _tokenRoleName = ''
let _serverName = 'baseServer'
let _isApiError = false
const _errorCodes = [401]
let _isShowLoading: boolean | string = false
const _paramTypeList = ['get', 'delete', 'head', 'options']

const instance = un.create({
  headers: { 'content-type': CONTENT_TYPE_JSON },
})

instance.interceptors.request.use((config: any) => {
  const {
    isNeedToken = _isNeedToken,
    isShowLoading = _isShowLoading,
    isNeedEncrypt = _isNeedEncrypt,
    allowDuplicateNonce = false,
    headers = {},
    tokenRoleName = _tokenRoleName,
    serverName = _serverName,
  } = config

  _isShowLoading = isShowLoading

  if (_isShowLoading && !_isApiError) showLoading(Boolean(_isShowLoading))

  config.url =
    (apiServer as unknown as Record<string, string | number>)[
      serverName || _serverName
    ] + (config.url || '')

  config.headers = config.headers || {}
  if (!isEmpty(headers)) {
    for (const key in headers) config.headers[key] = headers[key]
  }

  if (isNeedEncrypt) {
    const { data, method, params, url } = config
    const safeMethod = (method || '').toLowerCase()
    const { tempData, nonce, timestamp, sign, cacheKey } = encrypt(
      _paramTypeList.includes(safeMethod) ? params : data,
      url || '',
      allowDuplicateNonce
    )
    config.headers.timestamp = timestamp
    config.headers.nonce = nonce
    config.headers.sign = sign
    config._cacheKey = cacheKey
    if (_paramTypeList.includes(safeMethod)) config.params = tempData
    else config.data = tempData
  }

  if (isNeedToken) {
    const token = tokenRoleName ? getToken(tokenRoleName) : getToken()
    const { accessToken, expires } = token || {}
    if (accessToken) {
      const now = Date.now()
      const expired = Number.parseInt(`${expires}`) - now <= 0
      if (expired) {
        uni.showToast({ icon: 'none', title: '登录expired' })
        if (!_isRefreshing) _isRefreshing = true
        return new Promise((resolve) => {
          _requests.push((newToken) => {
            config.headers = config.headers || {}
            config.headers.Authorization = formatToken(newToken, 'Bearer')
            resolve(config as any)
          })
        })
      }
      config.headers.Authorization = formatToken(accessToken, 'Bearer')
    }
  }

  // FormData 不能设置 Content-Type，浏览器会自动带上 boundary
  const isFormData = config.data instanceof FormData
  if (!isFormData) {
    config.headers['content-type'] =
      (config.method || '').toUpperCase() === 'GET'
        ? CONTENT_TYPE_JSON_UTF8
        : CONTENT_TYPE_JSON
  }

  return config
})

instance.interceptors.response.use(
  (response: any) => {
    if (_isShowLoading && !_isApiError) hideLoading()

    const cfg = response.config as RequestConfig
    if (cfg?._cacheKey && cfg?.allowDuplicateNonce !== false)
      clearNonceCache(cfg._cacheKey)

    const { code } = response?.data || {}
    if (_errorCodes.includes(code)) {
      _isApiError = true
      uni.showToast({ icon: 'none', title: 'API Error' })
      return Promise.reject(response)
    }

    if (response?.status === 401) {
      uni.showToast({ icon: 'none', title: 'Login expired' })
      useUserStore().setUserInfo({})
      removeToken()
      return Promise.reject(response)
    }

    return response?.data
  },
  (error: any) => {
    if (_isShowLoading) hideLoading()
    const cfg = error?.config as RequestConfig
    if (cfg?._cacheKey) clearNonceCache(cfg._cacheKey)
    return Promise.reject(error)
  }
)

class PureHttp {
  static get isNeedToken() {
    return _isNeedToken
  }
  static set isNeedToken(v: boolean) {
    _isNeedToken = v
  }
  static get isNeedEncrypt() {
    return _isNeedEncrypt
  }
  static set isNeedEncrypt(v: boolean) {
    _isNeedEncrypt = v
  }
  static get tokenRoleName() {
    return _tokenRoleName
  }
  static set tokenRoleName(v: string) {
    _tokenRoleName = v
  }
  static get serverName() {
    return _serverName
  }
  static set serverName(v: string) {
    _serverName = v
  }
  static get errorCodes() {
    return _errorCodes
  }
  static get isShowLoading() {
    return _isShowLoading
  }
  static set isShowLoading(v: boolean | string) {
    _isShowLoading = v
  }

  request<T = any>(
    method: string,
    url: string,
    param: Record<string, any> = {},
    config: Partial<RequestConfig> = {}
  ): Promise<T> {
    // 支持直接传 data 对象（如 login({ username, password })）
    const requestData = param.data ?? param.params ?? param
    return instance
      .request<T, T>({
        data: method.toUpperCase() === 'GET' ? undefined : requestData,
        method: method as any,
        params: method.toUpperCase() === 'GET' ? requestData : undefined,
        url,
        ...config,
      })
      .then((r: any) => r) as Promise<T>
  }

  get<T = any>(
    url: string,
    params?: Record<string, any>,
    config?: Partial<RequestConfig>
  ): Promise<T> {
    return this.request('GET', url, params, config)
  }

  post<T = any>(
    url: string,
    data?: Record<string, any>,
    config?: Partial<RequestConfig>
  ): Promise<T> {
    return this.request('POST', url, data, config)
  }

  put<T = any>(
    url: string,
    data?: Record<string, any>,
    config?: Partial<RequestConfig>
  ): Promise<T> {
    return this.request('PUT', url, data, config)
  }

  delete<T = any>(
    url: string,
    params?: Record<string, any>,
    config?: Partial<RequestConfig>
  ): Promise<T> {
    return this.request('DELETE', url, params, config)
  }
}

export const http = new PureHttp()
export default http
