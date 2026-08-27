# UniApp Starter Template

<!-- markdownlint-disable MD033 MD040 -->
<h5 align='center'>
<b>🚀 现代化的 UniApp 开发模板</b>
</h5>

<br/>

<p align='center'>
<b>简体中文</b> | <a href="./README.en-US.md">English</a>
</p>

<br/>

<p align='center'>
<a href="https://github.com/vuejs/vue"><img src="https://img.shields.io/badge/Vue-3.5.34-4FC08D?style=flat-square&logo=vue.js&logoColor=white" alt="Vue"></a>
<a href="https://github.com/microsoft/TypeScript"><img src="https://img.shields.io/badge/TypeScript-6.0.3-3178C6?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript"></a>
<a href="https://vitejs.dev/"><img src="https://img.shields.io/badge/Vite-5.2.8-646CFF?style=flat-square&logo=vite&logoColor=white" alt="Vite"></a>
<a href="https://unocss.dev/"><img src="https://img.shields.io/badge/UnoCSS-66.7.0-1896FF?style=flat-square&logo=unocss&logoColor=white" alt="UnoCSS"></a>
<a href="https://pinia.vuejs.org/"><img src="https://img.shields.io/badge/Pinia-3.0.4-yellow?style=flat-square&logo=vue.js&logoColor=white" alt="Pinia"></a>
<a href="https://v2.wot-ui.cn/"><img src="https://img.shields.io/badge/Wot_UI-2.0.8-4D80F0?style=flat-square" alt="Wot UI"></a>
</p>

<br/>

<p align='center'>
<a href="https://uniapp-starter.netlify.app">📱 在线预览</a>
</p>

<br/>

一个功能完备、开箱即用的 UniApp 开发模板,集成了现代化的开发工具链和最佳实践。支持全平台部署,提供卓越的开发体验。

> 🎯 **致力于提供最优雅的 UniApp 开发体验**

## ✨ 特性

### 🔧 开发体验

- ⚡️ [Vue 3](https://github.com/vuejs/core) + [Vite](https://github.com/vitejs/vite) + [TypeScript](https://www.typescriptlang.org/) - 现代化开发栈
- 🗂 [约定式路由](./src/pages) - 基于文件系统的自动路由生成，支持子包和嵌套路由
- 📦 [组件自动导入](./src/components) - 无需手动引入，支持第三方组件库
- 📑 [布局系统](./src/layouts) - 灵活的页面布局管理，支持多种布局模式
- 🔥 [`<script setup>`](https://github.com/vuejs/rfcs/pull/227) - 使用最新的 Vue 3 Composition API 语法
- 📥 [API 自动导入](./src/composables) - Composition API、工具函数等开箱即用
- 🛠 [Vite 插件生态](https://vitejs.dev/plugins/) - 丰富的插件支持，极速的热更新体验

### 🎨 样式与UI

- 🎨 [UnoCSS](https://unocss.dev/) - 即时原子化 CSS 引擎，灵活构建现代界面
- 😃 [Iconify](https://iconify.design) - 海量图标库支持，包含数十万个图标
- 🌙 **暗色模式** - 内置主题切换功能，支持跟随系统设置
- 📱 **响应式设计** - 完美适配各种屏幕尺寸和设备
- 💎 **[Wot UI v2](https://v2.wot-ui.cn/)** - 80+ 高质量 uni-app 组件，支持暗黑模式、国际化与主题定制
- 🎭 **主题定制** - 支持主题色彩配置和深度定制

### 🛠 工程化

- 🍍 [Pinia](https://pinia.vuejs.org) - Vue 3 官方推荐的状态管理解决方案
- 🌐 [Vue I18n](https://vue-i18n.intlify.dev/) - 完整的国际化解决方案
- 🔐 **HTTP 客户端** - 基于 `@uni-helper/uni-network` 的请求封装，支持拦截器和统一错误处理
- 📊 **数据持久化** - Pinia 状态持久化，支持多种存储方式
- 🔍 [Ultracite](https://www.npmjs.com/package/ultracite) + [Biome](https://biomejs.dev/) - 代码检查与格式化
- 🔒 [Lefthook](https://github.com/evilmartians/lefthook) - 提交前代码检查，确保代码质量
- 📦 **依赖管理** - 使用 pnpm 11 管理依赖，配置集中在 `pnpm-workspace.yaml`

### 📱 全平台支持

- 📱 **App** (Android/iOS) - 使用 uni-app 编译为原生应用
- 🌐 **H5** - 现代浏览器支持，PWA 就绪
- 🔧 **微信小程序** - 完整支持微信小程序特性
- 🔧 **支付宝小程序** - 支付宝生态应用开发
- 🔧 **百度小程序** - 百度智能小程序支持
- 🎵 **字节跳动小程序** - 抖音、今日头条小程序
- 🔧 **QQ 小程序** - QQ 生态应用
- ⚡ **快手小程序** - 快手平台小程序
- 🔧 **京东小程序** - 京东购物平台
- 🔧 **飞书小程序** - 企业级办公应用
- 🌈 **鸿蒙应用** - 华为鸿蒙系统支持
- 🔥 **小红书小程序** - 新增小红书平台支持

## 🧰 技术栈

### 核心框架

- **Vue 3** - 渐进式 JavaScript 框架
- **UniApp** - 跨平台应用开发框架
- **TypeScript** - JavaScript 的超集，提供类型安全

### 构建工具

- **Vite** - 下一代前端工具链
- **ESBuild** - 极速 JavaScript 打包器
- **pnpm 11** - 高性能包管理器

### 状态管理

- **Pinia** - Vue 3 官方推荐的状态管理库
- **pinia-plugin-persistedstate** - Pinia 状态持久化插件
- **pinia-auto-refs** - Pinia 自动引用插件

### 样式方案

- **UnoCSS** - 即时原子化 CSS 引擎
- **Sass** (modern-compiler API) - CSS 预处理器

### 网络请求

- **@uni-helper/uni-network** - UniApp 跨端网络请求库
- 自定义封装的请求拦截器和响应处理
- 支持请求/响应拦截和自动错误处理
- 自动 Token 管理和刷新机制

### 工具库

- **dayjs** - 轻量级日期处理库
- **@iceywu/utils** - 常用工具函数集合
- **js-cookie** - Cookie 管理工具
- **js-md5** - MD5 加密库
- **qs** - 查询字符串解析和序列化
- **blurhash** - 图片模糊哈希

### 开发工具

- **Ultracite** - 代码检查与修复工具
- **Biome** - 格式化与静态检查工具
- **Vue DevTools** - Vue 开发者工具
- **Lefthook** - Git 钩子工具

### 插件系统

- **unplugin-auto-import** - API 自动导入，支持 Vue、Pinia 等
- **unplugin-vue-components** - 组件自动导入和注册
- **@uni-helper/vite-plugin-uni-components** - UniApp 组件自动注册
- **@uni-helper/vite-plugin-uni-pages** - 基于文件的路由系统
- **@uni-helper/vite-plugin-uni-layouts** - 页面布局系统
- **@uni-helper/vite-plugin-uni-manifest** - manifest.json 自动生成
- **@uni-ku/bundle-optimizer** - UniApp 包体积优化

### 类型支持

- **@dcloudio/types** - UniApp 官方类型定义
- **@uni-helper/uni-app-types** 1.0.0 - 增强的 UniApp 类型定义
- **@uni-helper/uni-types** 1.0.0 - UniApp 通用类型
- **auto-import-types** - 自动导入类型生成
- 完整的 TypeScript 类型覆盖和智能提示

## 📁 项目结构

```text
uniapp-starter/
├── src/
│   ├── pages/ pages-sub/ sub-packages/   # 页面、分包和业务页面入口
│   ├── layouts/                          # 布局系统
│   ├── components/                       # 通用组件
│   ├── api/                              # 接口封装示例
│   ├── composables/ hooks/               # 组合式函数与页面 Hooks
│   ├── store/                            # Pinia 状态管理
│   ├── config/                           # 运行时配置与签名配置
│   ├── locale/                           # i18n 语言包
│   ├── styles/ static/                   # 全局样式与静态资源
│   ├── utils/ helper/                    # 通用工具与辅助逻辑
│   ├── @types/                           # 自动生成与手写类型声明
│   ├── App.vue / main.ts                 # 应用入口
│   └── manifest.json / pages.json        # 生成后的 uni-app 配置
├── auto/                                 # 页面脚手架脚本，如 addPage.ts
├── scripts/                              # 发布、微信 CI、skills 同步脚本
├── public/                               # H5 直接公开的静态资源
├── biome.jsonc                           # Biome / Ultracite 规则配置
├── manifest.config.ts                    # 生成 src/manifest.json 的源配置
├── pages.config.ts                       # 生成 src/pages.json 的页面与分包配置
├── uno.config.ts                         # UnoCSS 配置
├── vite.config.ts                        # Vite 与 uni 插件配置
├── tsconfig.json                         # TypeScript 配置
└── package.json                          # 脚本、依赖与工程入口
```

### 目录说明

- `src/pages`、`src/pages-sub`、`src/sub-packages` 用于页面和分包组织，页面路由最终由 `pages.config.ts` 统一生成。
- `src/layouts` 管理布局壳层；具体使用方式可参考 `src/layouts/README.md`。
- `src/@types/auto-imports.d.ts`、`src/@types/components.d.ts`、`src/@types/uni-pages.d.ts` 是自动生成文件，通常不需要手动编辑。
- `src/pages.json` 和 `src/manifest.json` 属于生成产物，优先修改根目录的 `pages.config.ts`、`manifest.config.ts`。
- `scripts/mini-ci.js` 负责微信小程序预览和上传，`scripts/release.js` 负责版本发布，`scripts/skills-add.js` 负责同步本地 agent skills。
- 当前代码质量链路基于 `biome.jsonc` + `ultracite`，项目中已经没有独立的 ESLint 配置文件。

## 🚀 快速开始

### 📋 环境要求

- **Node.js** >= 22.12.0
- **pnpm** >= 11.0.0
- **Git** >= 2.0.0

> 💡 推荐使用 [pnpm](https://pnpm.io/) 作为包管理器，当前仓库也已提供 `pnpm-lock.yaml`。pnpm 11 的配置方式有变化，项目级设置（如 `shamefullyHoist`）已迁移至 `pnpm-workspace.yaml`。

### ⚡ 安装

#### 方式一：使用模板创建（推荐）

```bash
# 使用 pnpm 创建项目
pnpm dlx degit your-org/uniapp-starter my-uniapp-project

# 进入项目目录
cd my-uniapp-project

# 安装依赖
pnpm install
```

#### 方式二：克隆仓库

```bash
# 克隆仓库
git clone https://github.com/your-org/uniapp-starter.git my-uniapp-project

# 进入项目目录
cd my-uniapp-project

# 安装依赖
pnpm install
```

安装依赖时会自动执行 prepare，完成 Git Hooks 安装，并通过仓库内脚本同步 IceyWu/skills。脚本会保留 VS Code/Copilot、Claude、Kiro、Trae 的项目本地 agent 目录，同时自动清理其余 skills CLI 生成目录；相关生成文件也已加入 git 忽略。

### 🔄 同步主仓库代码

如需同步主仓库最新模板代码，可执行：

```bash
pnpm syn_rpo
# 或
npm run syn_rpo
```

该命令会自动拉取 <https://github.com/IceyWu/uniapp-starter.git> main 分支的最新内容。

### 🛠 开发

#### H5 开发

```bash
# 启动 H5 开发服务器
pnpm dev
# 或
pnpm dev:h5

# 带 SSR 的 H5 开发
pnpm dev:h5:ssr
```

浏览器自动打开 `http://localhost:5173`

#### 小程序开发

```bash
# 微信小程序
pnpm dev:mp-weixin

# 支付宝小程序
pnpm dev:mp-alipay

# 百度小程序
pnpm dev:mp-baidu

# 字节跳动小程序
pnpm dev:mp-toutiao

# QQ 小程序
pnpm dev:mp-qq

# 快手小程序
pnpm dev:mp-kuaishou

# 京东小程序
pnpm dev:mp-jd

# 飞书小程序
pnpm dev:mp-lark
```

#### App 开发

```bash
# App 开发
pnpm dev:app

# Android 专用
pnpm dev:app-android

# iOS 专用
pnpm dev:app-ios
```

#### 快应用开发

```bash
# 快应用-联盟
pnpm dev:quickapp-webview-union

# 快应用-华为
pnpm dev:quickapp-webview-huawei
```

### 📦 构建

#### 生产构建

```bash
# H5 构建
pnpm build:h5

# H5 SSR 构建
pnpm build:h5:ssr

# 微信小程序构建
pnpm build:mp-weixin

# App 构建
pnpm build:app

# 自定义平台构建
pnpm build:custom [platform]
```

构建产物在 `dist/` 目录下

### 🤖 微信小程序 CI 预览与上传

项目已集成 `miniprogram-ci` 脚本，入口文件为 `scripts/mini-ci.js`，用于自动化生成微信小程序预览二维码和上传体验版。

#### 1. 先构建微信小程序产物

```bash
# 开发环境构建
pnpm build:mp-weixin:dev

# 测试环境构建
pnpm build:mp-weixin:test

# 生产环境构建
pnpm build:mp-weixin:prod
```

脚本会直接读取 `dist/build/mp-weixin` 下的构建产物和 `project.config.json`。

#### 2. 配置 AppID 和私钥

脚本会优先从 `src/config/app.ts` 读取：

- `APP_ID_WECHAT`：微信小程序 AppID
- `APP_ID`：兜底 AppID，当 `APP_ID_WECHAT` 为空时使用
- `APP_WECHAT_CI_ROBOT`：可选，上传/预览使用的机器人编号，范围 `1-30`
- `APP_WECHAT_CI_PRIVATE_KEY_PATH`：可选，私钥文件路径，相对项目根目录

也支持环境变量覆盖：

- `MINI_APPID`：微信小程序 AppID
- `MINI_PRIVATE_KEY`：私钥内容，换行需要写成 `\n`
- `MINI_CI_ROBOT`：可选，指定上传/预览使用的机器人编号，范围 `1-30`

未设置 `MINI_PRIVATE_KEY` 时，脚本会优先使用 `APP_WECHAT_CI_PRIVATE_KEY_PATH`，未配置时再按 appid 回退查找 `scripts/private.<appid>.key`。

#### 3. 生成预览二维码

```bash
# 默认 dev
pnpm preview

# test 环境
pnpm preview test

# prod 环境
pnpm preview prod
```

执行成功后会在 `dist/build/mp-weixin` 下生成：

- `preview-<env>.png`
- `preview-<env>.html`

#### 4. 上传体验版

```bash
# 默认 dev
pnpm upload

# test 环境
pnpm upload test

# prod 环境
pnpm upload prod
```

上传版本号取自 `package.json` 的 `version`，描述信息会附带环境、日期和当前 Git 短提交号。

### 🧪 类型检查

```bash
pnpm type-check
```

### 🔍 代码质量

```bash
# 运行代码检查
pnpm lint

# 自动修复问题
pnpm lint:fix

# 更新依赖到最新版本
pnpm up
```

### 🎯 首次运行清单

在开始开发前，建议完成以下配置：

1. **更新项目信息**

   ```bash
   # 修改 package.json 中的项目名称和描述
   # 更新 manifest.config.ts 中的 appid 和应用信息
   ```

2. **配置开发环境**

   ```bash
   # 配置 src/config/domain.ts 中的 API 地址
   # 修改 src/config/app.ts 中的应用配置
   ```

3. **替换资源文件**

   ```bash
   # 替换 public/favicon.svg
   # 更新 src/static/ 目录下的图片资源
   ```

4. **设置主题**

   ```bash
   # 配置 src/theme.json 中的主题色彩
   # 自定义 src/styles/main.css 中的样式
   ```

## ⚙️ 配置说明

### 环境配置

在 `src/config/` 目录下配置项目相关信息：

- `app.ts` - 应用基础配置
- `domain.ts` - API 域名配置

### 主题配置

在 `src/theme.json` 中配置应用主题色彩。

### 国际化配置

在 `src/locale/` 目录下添加语言文件：

- `zh.json` - 中文
- `en.json` - 英文

### HTTP 请求配置

在 `src/utils/http/` 目录下配置请求相关：

- `index.ts` - 请求封装
- `auth.ts` - 认证相关
- `tool.ts` - 工具方法

## 📖 使用指南

### 页面路由

使用约定式路由，在 `src/pages/` 目录下创建 `.vue` 文件即可自动生成路由。

```bash
src/pages/
├── index.vue          # /pages/index/index
├── about.vue          # /pages/about/index
└── user/
    └── profile.vue    # /pages/user/profile/index
```

### 布局系统

在 `src/layouts/` 目录下创建布局组件，在页面中使用：

```vue
<template>
  <div>页面内容</div>
</template>

<route lang="yaml">
meta:
  layout: default
</route>
```

### 组件开发

组件会自动导入，直接在页面中使用即可：

```vue
<!-- 自动导入 src/components/NavBar/index.vue -->
<template>
  <NavBar title="页面标题" />
</template>
```

### Pinia 状态管理

使用 Pinia 进行状态管理，支持 TypeScript 和持久化：

```typescript
// src/store/user.ts
export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>('')

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const setToken = (newToken: string) => {
    token.value = newToken
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
  }

  return {
    userInfo,
    token,
    setUserInfo,
    setToken,
    logout
  }
}, {
  // 启用持久化存储
  persist: {
    key: 'user-store',
    storage: 'localStorage', // H5 使用 localStorage，小程序使用 storage
    paths: ['userInfo', 'token'] // 指定需要持久化的字段
  }
})

// 在组件中使用
const userStore = useUserStore()
const { userInfo, token } = storeToRefs(userStore)
```

### HTTP 请求封装

HTTP 请求基于项目内的 `http` 封装，底层使用 `@uni-helper/uni-network`，支持拦截器和自动错误处理：

```typescript
// src/api/user.ts
import { http } from '~/utils/http'

// 定义接口类型
interface UserInfo {
  id: number
  name: string
  email: string
  avatar?: string
}

interface LoginParams {
  username: string
  password: string
}

// API 接口定义
export const userApi = {
  // 获取用户信息
  getUserInfo: (): Promise<UserInfo> => {
    return http.get('/api/user/info')
  },

  // 用户登录
  login: (params: LoginParams): Promise<{ token: string, userInfo: UserInfo }> => {
    return http.post('/api/auth/login', params)
  },

  // 更新用户信息
  updateUserInfo: (data: Partial<UserInfo>): Promise<UserInfo> => {
    return http.put('/api/user/info', data)
  },

  // 上传头像
  uploadAvatar: (file: File): Promise<{ url: string }> => {
    const formData = new FormData()
    formData.append('avatar', file)
    return http.post('/api/user/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// 在组件中使用
async function handleLogin() {
  try {
    const { token, userInfo } = await userApi.login({
      username: 'demo',
      password: '123456'
    })

    // 存储到 store
    const userStore = useUserStore()
    userStore.setToken(token)
    userStore.setUserInfo(userInfo)

    // 跳转到首页
    uni.switchTab({ url: '/pages/index/index' })
  }
  catch (error) {
    uni.showToast({
      title: '登录失败',
      icon: 'error'
    })
  }
}
```

## 🛠 开发工具

### 🎨 VS Code 扩展推荐

#### 必备扩展

- **Vue Language Features (Volar)** - Vue 3 语言支持和智能提示
- **TypeScript Vue Plugin (Volar)** - Vue 中的 TypeScript 支持
- **uni-app-schemas** - UniApp 配置文件智能提示
- **uni-app-snippets** - UniApp 代码片段

### 样式与 UI

- **UnoCSS IntelliSense** - UnoCSS 原子类提示与补全
- **Iconify IntelliSense** - 图标智能提示和预览
- **Color Highlight** - 颜色值高亮显示

#### 代码质量

- **Biome** - 使用 `biome.jsonc` 提供格式化、Lint 和编辑器集成
- **Ultracite CLI** - 通过 `pnpm lint` 和 `pnpm lint:fix` 统一执行检查与修复
- **Error Lens** - 内联显示错误和警告

#### 效率工具

- **Auto Rename Tag** - 自动重命名配对标签
- **Bracket Pair Colorizer** - 括号配对着色
- **GitLens** - Git 增强工具
- **Thunder Client** - API 测试工具

#### 主题推荐

- **One Dark Pro** - 流行的暗色主题
- **Material Theme** - Material Design 主题
- **GitHub Theme** - GitHub 风格主题

### 📱 调试工具

#### H5 调试

- **Chrome DevTools** - 浏览器开发者工具
- **Vue DevTools** - Vue 组件调试工具
- **Vite DevTools** - Vite 开发工具

#### 小程序调试

- **微信开发者工具** - 微信小程序官方调试工具
- **支付宝小程序开发者工具** - 支付宝小程序调试
- **百度智能小程序开发者工具** - 百度小程序调试

#### App 调试

- **HBuilderX** - UniApp 官方 IDE
- **Android Studio** - Android 应用调试
- **Xcode** - iOS 应用调试

### 🔧 命令行工具

```bash
# 全局安装推荐工具
pnpm add -g @dcloudio/uvm         # UniApp 版本管理
pnpm add -g typescript            # TypeScript 编译器
pnpm add -g @biomejs/biome        # Biome 代码检查与格式化
```

### 📊 性能分析

- **Lighthouse** - 网页性能分析
- **Bundle Analyzer** - 打包体积分析
- **Vue DevTools Profiler** - Vue 组件性能分析

## 📝 最佳实践

### 代码规范

- 使用 TypeScript 提供类型安全
- 遵循项目的 Ultracite / Biome 代码规范
- 使用 `<script setup>` 语法
- 组件名使用 PascalCase
- 文件名使用 kebab-case

### 性能优化

- 使用按需导入减少包体积
- 合理使用组件懒加载
- 优化图片资源大小
- 避免过度嵌套组件

### 项目管理

- 使用语义化的 commit 信息
- 及时更新依赖版本
- 完善项目文档

## 📋 部署清单

使用此模板创建新项目时，请按照以下清单逐步配置：

### 🏷️ 项目基本信息

- [ ] **修改项目名称和描述**

  ```bash
  # 编辑 package.json
  {
    "name": "your-project-name",
    "description": "你的项目描述",
    "version": "1.0.0"
  }
  ```

- [ ] **更新应用配置**

  ```typescript
  // 编辑 manifest.config.ts
  export default defineManifest({
    name: '你的应用名称',
    appid: '__UNI__YOUR_APP_ID__', // 替换为你的 AppID
    description: '应用描述',
    // ... 其他配置
  })
  ```

### 🎨 资源文件替换

- [ ] **替换应用图标**
  - 替换 `public/favicon.svg`
  - 更新 `src/static/logo.png`
  - 准备各尺寸的应用图标

- [ ] **更新启动图和引导页**
  - 设计符合品牌的启动页面
  - 配置各平台的启动图资源

### ⚙️ 配置文件设置

- [ ] **API 配置**

  ```typescript
  // 编辑 src/config/domain.ts
  export const API_BASE_URL = {
    development: 'https://dev-api.yoursite.com',
    production: 'https://api.yoursite.com'
  }
  ```

- [ ] **应用配置**

  ```typescript
  // 编辑 src/config/app.ts
  export const APP_CONFIG = {
    name: '你的应用名称',
    version: '1.0.0',
    // ... 其他配置
  }
  ```

- [ ] **主题配置**

  ```json
  // 编辑 src/theme.json
  {
    "primaryColor": "#007AFF",
    "backgroundColor": "#F8F8F8"
  }
  ```

### 🌐 国际化设置

- [ ] **配置语言包**

  ```json
  // 编辑 src/locale/zh.json
  {
    "app": {
      "name": "你的应用名称",
      "welcome": "欢迎使用"
    }
  }
  ```

- [ ] **添加新语言支持**
  - 创建对应的语言文件
  - 在 `useLanguage.ts` 中添加语言选项

### 🔐 安全配置

- [ ] **环境变量设置**

  ```bash
  # 创建 .env.local 文件
  VITE_API_BASE_URL=https://your-api-domain.com
  VITE_APP_SECRET=your-app-secret
  ```

- [ ] **API 密钥配置**
  - 配置第三方服务 API 密钥
  - 设置支付相关配置

### 📱 平台特定配置

- [ ] **微信小程序**
  - 配置 AppID 和 AppSecret
  - 设置服务器域名白名单
  - 配置支付相关参数

- [ ] **支付宝小程序**
  - 配置应用 ID
  - 设置服务器域名

- [ ] **App 配置**
  - 配置应用签名
  - 设置推送服务
  - 配置第三方 SDK

### 🚀 部署配置

- [ ] **CI/CD 流程**

  ```yaml
  # .github/workflows/deploy.yml
  name: Deploy
  on:
    push:
      branches: [main]
  # ... 部署脚本
  ```

- [ ] **构建脚本优化**

  ```json
  // package.json scripts
  {
    "build:prod": "pnpm build:h5",
    "deploy": "pnpm build:prod && your-deploy-command"
  }
  ```

### 🧹 清理工作

- [ ] **删除示例代码**
  - 清理不需要的示例页面
  - 删除不需要的组件和工具函数
  - 清理测试相关的示例代码

- [ ] **更新文档**
  - 修改 `README.md` 为你的项目文档
  - 更新 `LICENSE` 文件中的作者信息
  - 完善项目使用说明

### ✅ 测试验证

- [ ] **功能测试**

  ```bash
  # 类型检查
  pnpm type-check

  # 代码规范检查
  pnpm lint
  ```

- [ ] **构建测试**

  ```bash
  # 测试各平台构建
  pnpm build:h5
  pnpm build:mp-weixin
  pnpm build:app
  ```

- [ ] **兼容性测试**
  - 在不同设备和浏览器中测试
  - 验证各平台功能正常

### 📚 文档完善

- [ ] **API 文档**
  - 编写接口文档
  - 更新组件使用说明

- [ ] **部署文档**
  - 记录部署流程
  - 配置运维文档

## 🤝 贡献

我们欢迎所有形式的贡献！无论是报告 Bug、提出新功能建议，还是提交代码改进。

### 🐛 报告问题

如果你发现了 Bug 或有改进建议，请：

1. 在 [Issues](https://github.com/your-org/uniapp-starter/issues) 中搜索是否已有相关问题
2. 如果没有，请创建新的 Issue，详细描述问题或建议
3. 提供复现步骤、环境信息和相关截图

### 💡 提交代码

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交你的修改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

### 📝 代码规范

- 遵循项目的代码检查配置
- 使用 TypeScript 编写类型安全的代码
- 提交信息遵循 [Conventional Commits](https://conventionalcommits.org/) 规范
- 为新功能添加相应的测试用例

## 📄 许可证

本项目采用 [MIT](LICENSE) 许可证 - 查看 LICENSE 文件了解详情。

## 🙏 致谢

感谢以下优秀的开源项目：

- [Vue.js](https://vuejs.org/) - 渐进式 JavaScript 框架
- [UniApp](https://uniapp.dcloud.io/) - 跨平台应用开发框架
- [Vite](https://vitejs.dev/) - 下一代前端构建工具
- [UnoCSS](https://unocss.dev/) - 即时原子化 CSS 引擎
- [Pinia](https://pinia.vuejs.org/) - Vue 状态管理库
