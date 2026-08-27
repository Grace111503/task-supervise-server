import type { ServerName } from '@/config/domain'

declare namespace Http {
  type Response<T> = Promise<{
    data?: T
  }>
  interface config {
    isNeedToken?: boolean
    isShowLoading?: boolean
    serverName?: ServerName
    tokenRoleName?: string
  }
}

declare namespace GetTest {
  interface params {
    a: number
  }
  interface data {
    age: number
    name: string
  }
}

declare namespace PostTest {
  interface params {
    a: number
  }
  interface data {
    val: string
  }
}

declare namespace GetParams {
  type params = {}
}
