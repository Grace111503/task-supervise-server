/**
 * 站内消息 WebSocket 客户端
 * <p>
 * 登录后连接 /ws/message/{userId}，接收实时推送。
 * 断线自动重连（5秒间隔），收到消息后触发回调。
 *
 * @author grq
 * @date 2026-09-01
 */

interface WsMessage {
  type: string
  data: any
}

type MessageCallback = (msg: WsMessage) => void

let ws: UniApp.SocketTask | null = null
let userId: number | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null
let isConnecting = false
const callbacks: MessageCallback[] = []
let baseUrl = ''

/**
 * 初始化 WebSocket 基础地址
 * 从 HTTP API 地址推导 WS 地址
 */
function getWsUrl(uid: number): string {
  if (!baseUrl) {
    // 从环境配置获取 API 地址，推导 WS 地址
    // #ifdef H5
    const loc = window.location
    const protocol = loc.protocol === 'https:' ? 'wss:' : 'ws:'
    // 开发环境走 localhost:8082，生产环境走当前域名
    if (loc.hostname === 'localhost' || loc.hostname === '127.0.0.1') {
      return `${protocol}//localhost:8082/ws/message/${uid}`
    }
    return `${protocol}//${loc.host}/ws/message/${uid}`
    // #endif
    // #ifndef H5
    // 小程序/APP 环境使用固定地址
    return `ws://localhost:8082/ws/message/${uid}`
    // #endif
  }
  const wsBase = baseUrl.replace(/^http/, 'ws')
  return `${wsBase}/ws/message/${uid}`
}

/**
 * 注册消息回调
 */
export function onMessage(callback: MessageCallback) {
  callbacks.push(callback)
}

/**
 * 移除消息回调
 */
export function offMessage(callback: MessageCallback) {
  const index = callbacks.indexOf(callback)
  if (index !== -1) {
    callbacks.splice(index, 1)
  }
}

/**
 * 触发所有回调
 */
function emitMessage(msg: WsMessage) {
  callbacks.forEach(cb => {
    try {
      cb(msg)
    } catch (e) {
      console.error('[WebSocket] 回调执行失败:', e)
    }
  })
}

/**
 * 连接 WebSocket
 */
export function connectMessage(uid: number) {
  if (isConnecting || (ws && userId === uid)) return
  userId = uid
  isConnecting = true

  const url = getWsUrl(uid)
  console.log('[WebSocket] 连接:', url)

  // #ifdef H5
  ws = uni.connectSocket({
    url,
    success: () => {},
    fail: (err) => {
      console.error('[WebSocket] 连接失败:', err)
      isConnecting = false
      scheduleReconnect()
    },
  }) as any
  // #endif

  // #ifndef H5
  ws = uni.connectSocket({
    url,
    success: () => {},
    fail: (err) => {
      console.error('[WebSocket] 连接失败:', err)
      isConnecting = false
      scheduleReconnect()
    },
  }) as any
  // #endif

  uni.onSocketOpen(() => {
    console.log('[WebSocket] 连接成功')
    isConnecting = false
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
  })

  uni.onSocketMessage((res) => {
    try {
      const msg = JSON.parse(res.data as string) as WsMessage
      emitMessage(msg)
    } catch (e) {
      console.warn('[WebSocket] 消息解析失败:', res.data)
    }
  })

  uni.onSocketClose(() => {
    console.log('[WebSocket] 连接关闭')
    ws = null
    isConnecting = false
    scheduleReconnect()
  })

  uni.onSocketError((err) => {
    console.error('[WebSocket] 连接错误:', err)
    ws = null
    isConnecting = false
    scheduleReconnect()
  })
}

/**
 * 断线重连（5秒间隔）
 */
function scheduleReconnect() {
  if (reconnectTimer || !userId) return
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null
    if (userId) {
      console.log('[WebSocket] 尝试重连...')
      connectMessage(userId)
    }
  }, 5000)
}

/**
 * 断开连接
 */
export function disconnectMessage() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  userId = null
  isConnecting = false
  if (ws) {
    uni.closeSocket({ code: 1000, reason: '用户退出' })
    ws = null
  }
}

/**
 * 设置 API 基础地址（可选，用于非 H5 环境）
 */
export function setWsBaseUrl(url: string) {
  baseUrl = url
}