import { deepClone, sortObj } from '@iceywu/utils'
import * as md5Module from 'js-md5'
import qs from 'qs'
import { secretKey } from '@/config/sign'

// js-md5 的导出在不同环境下可能是 default 或直接函数，为了避免 "类型没有调用签名" 的 TS 错误，做一层兼容适配
const md5: (msg: string) => string =
  (md5Module as any).default ?? (md5Module as any)

// nonce缓存管理
interface NonceCache {
  count: number // 引用计数
  nonce: string
}

const nonceMap = new Map<string, NonceCache>()

function wxuuid() {
  const hexDigits = '0123456789abcdef'
  const s = Array.from(
    { length: 36 },
    () => hexDigits[Math.floor(Math.random() * 16)]
  )
  s[14] = '4'
  s[19] = hexDigits[(Number.parseInt(s[19], 16) & 0x3) | 0x8]
  s[8] = s[13] = s[18] = s[23] = '-'
  return s.join('')
}

/**
 * 生成缓存key
 * @param url - 请求URL
 * @param data - 请求数据
 * @returns 缓存key
 */
function generateCacheKey(url: string, data: Record<string, any>): string {
  const dataStr = JSON.stringify(sortObj(data))
  return `${url}:${md5(dataStr)}`
}

/**
 * 获取或创建nonce
 * @param cacheKey - 缓存key
 * @param allowDuplicateNonce - 是否允许nonce复用
 * @returns nonce值
 */
function getOrCreateNonce(
  cacheKey: string,
  allowDuplicateNonce: boolean
): string {
  if (!allowDuplicateNonce) {
    return wxuuid()
  }

  const cached = nonceMap.get(cacheKey)
  if (cached) {
    cached.count++
    return cached.nonce
  }

  const nonce = wxuuid()
  nonceMap.set(cacheKey, { count: 1, nonce })
  return nonce
}

/**
 * 清理nonce缓存
 * @param cacheKey - 缓存key
 */
export function clearNonceCache(cacheKey: string) {
  const cached = nonceMap.get(cacheKey)
  if (cached) {
    cached.count--
    if (cached.count <= 0) {
      nonceMap.delete(cacheKey)
    }
  }
}

/**
 * 生成签名
 * @param {object} data - 需要签名的数据
 * @param {string} url - 请求URL
 * @param {boolean} allowDuplicateNonce - 是否允许nonce复用，默认true
 * @returns {object} - 包含 nonce、timestamp、sign、tempData 和 cacheKey 的对象
 */
export function encrypt(data = {}, url = '', allowDuplicateNonce = true) {
  const tempData = deepClone(data)
  const timestamp = new Date().getTime()

  // 生成缓存key
  const cacheKey = generateCacheKey(url, data)

  // 获取或创建nonce
  const nonce = getOrCreateNonce(cacheKey, allowDuplicateNonce)

  // 合并数据并排序
  const signData = sortObj({
    ...data,
    nonce,
    timestamp,
  })
  const newParams = deepClone(signData)
  for (const key in newParams) {
    if (Array.isArray(newParams[key])) {
      // newParams[key] = `[${newParams[key].join(',')}]`
      newParams[key] = JSON.stringify(newParams[key])
    }
  }
  // 生成签名
  const str = qs.stringify({ ...newParams, key: secretKey }, { encode: false })
  const sign = md5(str)

  return {
    cacheKey,
    nonce,
    sign,
    tempData,
    timestamp,
  }
}
