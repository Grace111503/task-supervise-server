// ... existing code ...

import { get } from '@iceywu/utils'
import { encode } from 'blurhash'
import dayjs from 'dayjs'

// 获取用户头像
export function getUserAvatar(data: any) {
  const oldAvatar = get(data, 'avatar')
  const baseAvatar = get(data, 'avatarInfo.url')
  return baseAvatar || oldAvatar
}

export function getDataUrlFromArr(arr, w, h, options = {}) {
  const { instance, imgId } = options
  if (typeof w === 'undefined' || typeof h === 'undefined') {
    w = h = Math.sqrt(arr.length / 4)
  }
  return new Promise((resolve, reject) => {
    const query = uni.createSelectorQuery().in(instance)

    query
      .select(`#${imgId}`)
      .fields({ node: true, size: true })
      .exec((res) => {
        const canvas = res[0]?.node
        if (!canvas) {
          reject(new Error('未找到 canvas 节点'))
          return
        }

        const ctx = canvas.getContext('2d')
        if (!ctx) {
          reject(new Error('无法获取 canvas 上下文'))
          return
        }

        // 重置 canvas 状态
        ctx.setTransform(1, 0, 0, 1, 0, 0)
        // #ifdef H5
        const dpr = uni.getWindowInfo().pixelRatio
        canvas.style.width = `${w / 4}px`
        canvas.style.height = `${h / 4}px`
        canvas.width = w * dpr
        canvas.height = h * dpr
        ctx.scale(dpr, dpr)
        // #endif
        // #ifdef MP-WEIXIN
        canvas.width = w
        canvas.height = h
        // #endif

        const imgData = ctx.createImageData(w, h)
        if (arr.length !== imgData.data.length) {
          reject(new Error('传入的像素数据长度与创建的 ImageData 长度不匹配'))
          return
        }
        imgData.data.set(arr)
        ctx.putImageData(imgData, 0, 0)

        uni.canvasToTempFilePath(
          {
            // #ifdef MP-WEIXIN
            canvas,
            // #endif
            // #ifdef H5
            canvasId: `${imgId}`,
            fail: (err) => {
              reject(err)
            },
            // #endif
            success: (res) => {
              const tempFilePath = res.tempFilePath
              resolve(tempFilePath)
            },
          },
          true
        )
      })
  })
} // 假设 blurhash 库已正确导入

// ... existing code ...

export function generateBlurhashFromFile(
  file: any,
  maxWidth = 200,
  quality = 0.7,
  options = {}
) {
  return new Promise((resolve, reject) => {
    const { instance } = options
    // 获取临时文件路径
    const tempFilePath = typeof file === 'string' ? file : file.tempFilePath
    if (!tempFilePath) {
      reject(new Error('未获取到有效的文件路径'))
      return
    }

    // 获取图片信息
    uni.getImageInfo({
      fail: (err) => {
        reject(err)
      },
      src: tempFilePath,
      success: (res) => {
        let width = res.width
        let height = res.height

        // 调整图片尺寸
        if (width > maxWidth) {
          const ratio = maxWidth / width
          width = maxWidth
          height = Math.ceil(height * ratio)
        }

        const query = uni.createSelectorQuery().in(instance)
        query
          .select('#blurhashCanvas')
          .fields({ node: true, size: true })
          .exec((res) => {
            const canvas = res[0]?.node
            if (!canvas) {
              reject(new Error('未找到 canvas 节点'))
              return
            }

            const ctx = canvas.getContext('2d')
            if (!ctx) {
              reject(new Error('无法获取 canvas 上下文'))
              return
            }

            // 重置 canvas 状态
            ctx.setTransform(1, 0, 0, 1, 0, 0)
            // #ifdef H5
            const dpr = uni.getWindowInfo().pixelRatio
            canvas.style.width = `${width}px`
            canvas.style.height = `${height}px`
            canvas.width = width * dpr
            canvas.height = height * dpr
            ctx.scale(dpr, dpr)
            // #endif
            // #ifdef MP-WEIXIN
            canvas.width = width
            canvas.height = height
            // #endif

            // 绘制图片到 canvas
            const img = canvas.createImage()
            img.src = tempFilePath
            img.onload = () => {
              ctx.drawImage(img, 0, 0, width, height)
              // 获取 canvas 图片数据
              uni.canvasGetImageData(
                {
                  canvas,
                  canvasId: 'blurhashCanvas',
                  fail: (err) => {
                    reject(err)
                  },
                  height,
                  success: (imageDataRes) => {
                    try {
                      // 生成模糊哈希值
                      const blurhash = encode(
                        imageDataRes.data,
                        width,
                        height,
                        4,
                        3
                      )
                      resolve(blurhash)
                    } catch (error) {
                      reject(error)
                    }
                  },
                  width,
                  x: 0,
                  y: 0,
                },
                instance
              )
            }
            img.onerror = () => {
              reject(new Error('图片加载失败'))
            }
          })
      },
    })
  })
}

// ... existing code ...

// ... existing code ...

// 格式化 wxuuid 函数
export function wxuuid() {
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

// 使用proxy转换为异步化的uni方法
export const uniAsync = new Proxy({} as any, {
  get(_, name) {
    return (obj: any) =>
      new Promise((resolve, reject) => {
        uni[name]({
          ...obj,
          fail: (err: any) => {
            reject(err)
          },
          success: (ret: any) => {
            resolve(ret)
          },
        })
      })
  },
})

export function getStyleVal(target: number | string) {
  if (isString(target)) return target

  return `${target}rpx`
}

// 时间格式化
export function formatTime(time: any, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!time) return ''
  if (time?.toString()?.length < 13) time = time * 1000

  return dayjs(time).format(format)
}
