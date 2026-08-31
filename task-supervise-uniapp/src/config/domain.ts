interface ServerConfig {
  baseServer: string
  otherServer: string
  picsumServer: string
  uploadServer: string
}

interface EnvMap {
  [key: string]: ServerConfig
}

const envMap: EnvMap = {
  dev: {
    baseServer: 'http://localhost:8082/api/v1',
    otherServer: 'http://x.x.x.x/section',
    picsumServer: 'https://picsum.photos',
    uploadServer: 'https://xxx/alioss/uploadBase',
  },
  prod: {
    baseServer: 'https://your-production-api.com',
    otherServer: 'http://x.x.x.x/section',
    picsumServer: 'https://picsum.photos',
    uploadServer: 'https://your-production-api.com/alioss/uploadBase',
  },
}
type ApiEnv = keyof typeof envMap
type ServerName = keyof ServerConfig

type EnvConfig<T extends ApiEnv> = {
  env: T
} & (typeof envMap)[T]

function createEnv<T extends ApiEnv>(env: T): EnvConfig<T> {
  return { env, ...envMap[env] }
}

// 读取环境变量，默认 'dev'
const serverChoose = (import.meta.env.VITE_APP_API_SERVER as ApiEnv) || 'dev'

const env = createEnv(serverChoose)

export default env

export type { ApiEnv, EnvConfig, ServerConfig, ServerName }
