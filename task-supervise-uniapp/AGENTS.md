# AGENTS.md

> AI 编码助手的项目上下文与操作指引。本文件是 README.md 的补充，专注于帮助 AI Agent 高效、安全地在本仓库工作。

## 项目概览

**uniapp-starter** 是一个现代化的 UniApp 开发模板，基于 Vue 3 + Vite + TypeScript，支持全平台部署（H5、微信/支付宝/百度等小程序、App、鸿蒙）。

核心技术栈：

| 层次 | 技术 |
|---|---|
| 框架 | Vue 3 (`<script setup>`) + uni-app |
| 构建 | Vite 5 + `@uni-helper/plugin-uni` |
| 语言 | TypeScript（严格模式） |
| 样式 | UnoCSS + SCSS |
| 状态 | Pinia + `pinia-plugin-persistedstate` |
| UI 组件库 | Wot UI v2 (`@wot-ui/ui`) + uni-ui-plus |
| HTTP | `@uni-helper/uni-network` |
| 国际化 | Vue I18n（`src/locale/zh.json` / `en.json`） |
| Lint/格式化 | Biome + Ultracite（**不用 ESLint**） |
| Git Hooks | Lefthook |
| 包管理 | pnpm 11 |

## 目录结构关键说明

```
src/
  pages/          # 约定式路由页面（每个页面对应目录 or .vue 文件）
  pages-sub/      # 分包页面入口
  sub-packages/   # 分包组件与工具
  layouts/        # 布局文件（default / home / custom / childPage）
  components/     # 公共组件（自动导入，无需手动引入）
  composables/    # Composition API hooks（自动导入）
  store/          # Pinia store（自动导入）
  api/            # API 请求封装
  utils/          # 工具函数
  locale/         # i18n 翻译文件
  config/         # 应用配置（app / domain / theme / sign）
  styles/         # 全局样式
  @types/         # TypeScript 类型声明

.agents/skills/   # AI Agent 技能文件（只读，由 pnpm skills:add 生成）
scripts/          # 发布、CI 脚本
```

路径别名：`~/` → `src/`（例：`import Foo from '~/components/Foo.vue'`）

## 环境准备

```bash
# 安装依赖
pnpm install
```

`prepare` 脚本会自动执行 `lefthook install`，无需手动配置 Git Hooks。

## 常用命令

### 开发

```bash
pnpm dev              # H5 开发服务器
pnpm dev:mp-weixin    # 微信小程序（输出到 dist/dev/mp-weixin，用 HBuilderX 或微信开发者工具打开）
pnpm dev:app          # App
```

### 构建

```bash
pnpm build:h5                  # H5 生产构建
pnpm build:mp-weixin           # 微信小程序生产包
pnpm build:mp-weixin:dev       # 微信小程序开发环境包
pnpm build:mp-weixin:test      # 微信小程序测试环境包
pnpm build:mp-alipay           # 支付宝小程序
```

### 质量检查

```bash
pnpm lint           # Biome + Ultracite 检查（只读）
pnpm lint:fix       # 自动修复可修复的问题
pnpm type-check     # vue-tsc 类型检查（无输出才算通过）
```

> **重要**：提交前必须通过 `pnpm lint` 和 `pnpm type-check`，Lefthook 会在 pre-commit 阶段自动运行 `ultracite fix`。

### 其他

```bash
pnpm clean          # 清理 dist 和 Vite 缓存
pnpm analyze        # 构建产物分析（微信小程序）
pnpm changelog      # 生成 CHANGELOG
```

## 代码风格规范

- **格式化**：缩进 2 空格，LF 换行，由 Biome 强制执行
- **组件**：使用 `<script setup lang="ts">` + Composition API，**禁止** Options API
- **组件命名**：文件名 PascalCase（`MyComponent.vue`），模板中同样使用 PascalCase
- **自动导入**：`src/components/`、`src/composables/` 中的导出无需手动 `import`；Vue/Pinia/uni-app API 同样自动导入，参见 `src/@types/auto-imports.d.ts` 和 `src/@types/components.d.ts`
- **类型**：启用 TypeScript strict 模式；优先用 `interface` 而非 `type` 声明对象形状
- **样式**：优先使用 UnoCSS 原子类；组件内样式用 `<style scoped>`；全局覆盖放 `src/styles/main.css`
- **路径**：内部模块一律使用 `~/` 别名，禁止相对路径 `../../`
- **平台差异**：使用 `uni.` API 而非 `window.` / `document.`；跨平台条件代码使用 `#ifdef MP-WEIXIN` 等条件编译注释

## 新增页面

在 `src/pages/<name>/index.vue` 创建文件即可（`@uni-helper/vite-plugin-uni-pages` 自动注册路由）。如需指定布局，在 SFC 文件顶部添加路由块：

```vue
<route lang="json5">
{
  layout: "home",  // default | home | custom | childPage
  style: { navigationBarTitleText: "页面标题" }
}
</route>
```

分包页面放在 `src/pages-sub/` 或 `src/sub-packages/`。

## UI 组件使用

项目使用 **Wot UI v2**（`@wot-ui/ui`）。组件前缀 `wd-`，在模板中直接使用 `<WdXxx>` 形式（自动导入）。

```vue
<!-- 正确用法 -->
<WdButton type="primary">确认</WdButton>
<WdCell title="标题" value="内容" />

<!-- 禁止使用 v1 旧包名 wot-design-uni -->
```

次级组件库 `uni-ui-plus` 使用 `<UpXxx>` 前缀，同样自动导入。

## HTTP 请求

在 `src/api/` 中创建请求文件，使用 `src/utils/http/` 中封装好的实例：

```ts
import { http } from '~/utils/http'

export function fetchSomething(params: Params) {
  return http.get<Response>('/endpoint', { params })
}
```

## 状态管理

Store 文件放在 `src/store/` 中，使用 `defineStore`。`pinia-auto-refs` 已配置，Store 中的 ref 会被自动解构为响应式引用。持久化通过 `pinia-plugin-persistedstate` 配置在 store 定义中。

## 提交规范

遵循 Conventional Commits，允许的类型：

| 类型 | 用途 |
|---|---|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 仅文档变更 |
| `refactor` | 代码重构（不含新功能/修复） |
| `test` | 测试相关 |
| `build` | 构建系统/依赖变更 |
| `ci` | CI 流程变更 |
| `chore` | 其他杂项（工具、脚本等） |
| `improvement` | 对已有功能的改进（无新增/修复） |
| `revert` | 回滚 |

示例：`feat: add dark mode toggle` / `fix(http): handle 401 redirect`

## 发布流程

```bash
pnpm release:patch   # 补丁版本 (x.y.Z)
pnpm release:minor   # 次版本 (x.Y.0)
pnpm release:major   # 主版本 (X.0.0)
```

脚本会自动更新版本号、生成 CHANGELOG 并创建 Git tag，**不要**手动修改 `package.json` 中的 version 字段。

## AI Agent 注意事项

- **不要**修改 `.agents/skills/` 目录，该目录由 `pnpm skills:add` 自动管理
- **不要**修改 `pnpm-lock.yaml`，依赖变更通过 `pnpm add/remove` 执行
- **不要**向 `src/@types/auto-imports.d.ts` 和 `src/@types/components.d.ts` 手动添加条目，它们由 Vite 插件自动生成
- 修改 Vite 配置后重新运行 `pnpm dev` 才能生效
- 如需在多平台间有差异行为，使用 uni-app 条件编译（`// #ifdef PLATFORM`），不要用运行时 `process.env` 判断平台
- Wot UI v2 文档：<https://v2.wot-ui.cn/>
- 项目在线预览：<https://uniapp-starter.netlify.app>

## 已安装的 Agent Skills

以下 skills 由 `pnpm skills:add` 自动安装到 `.agents/skills/`，AI Agent 在对应场景下会自动加载：

| Skill | 用途 | 触发场景 |
|---|---|---|
| `wot-ui-v2` | Wot UI v2 组件 API 查询、代码生成、问题排查 | 涉及 `wd-` 组件、ConfigProvider、useToast、Form、Popup 等 |
| `wot-ui-cli` | `@wot-ui/cli` 命令行工具使用 | MCP 接入、doctor 诊断、changelog 提取等 |
| `wot-ui-unocss-preset-guide` | `@wot-ui/unocss-preset` 安装与配置 | 预设接入、主题配置、常见问题 |
| `create-wot-ui-theme` | 生成 Wot UI 单文件主题 SCSS | 品牌主题定制、语义变量落地 |
| `migrate-v1-to-v2` | Wot UI v1 → v2 迁移 | 升级 `wot-design-uni` 到 `@wot-ui/ui`、替换旧 API |
| `openapi-lookup` | 查询后端 OpenAPI/Swagger 接口定义 | 对接接口、查询参数结构、返回结构 |
| `starter-cleaner` | 精简 wot-starter v2 模板 | 移除示例代码、演示分包、生成文件 |

## 分页列表页面设计规范

> 参考实现：`src/components/WaterfallsList.vue`

### 核心依赖

- **`z-paging`** — 分页容器组件，处理下拉刷新、上拉加载、空状态、加载骨架屏
- **`useRequest`**（`vue-hooks-pure`）— 请求管理 composable，自动处理 loading/error/data 状态
- **`@iceywu/utils`** — 工具函数，使用 `get()` 安全取值

### 标准模式

```vue
<script lang="ts" setup>
import { get } from '@iceywu/utils'
import { fetchListApi } from '~/api/xxx'

const pagingRef = ref()
const isRefreshing = ref(false)

const { onRefresh, onLoad: onLoadMore, result } = useRequest(fetchListApi, {
  getVal: (res: any) => res || [],           // 1. 转换接口返回数据
  listOptions: {
    defaultDataKey: 'list',                   // 2. 数据列表字段名
    defaultPage: 1,                           // 3. 起始页码
    defaultPageKey: 'page',                   // 4. 页码参数名
    defaultSizeKey: 'limit',                  // 5. 每页条数参数名
  },
  loadingDelay: 300,                          // 6. loading 延迟（避免闪烁）
  onRequestEnd: () => {
    const list = get(result.value, 'list', [])
    const finished = get(result.value, 'finished', false)
    pagingRef.value?.complete(list)           // 7. 将数据交给 z-paging
    if (finished) pagingRef.value?.complete([]) // 8. 标记加载完成
  },
  target: 'list',
})

function queryList(pageNo: number) {
  isRefreshing.value = pageNo === 1           // 9. pageNo=1 为下拉刷新
  pageNo === 1 ? onRefresh() : onLoadMore()   // 10. 分发刷新/加载更多
}

onMounted(() => {
  pagingRef.value?.reload()                   // 11. 首次进入自动请求
})
</script>

<template>
  <z-paging ref="pagingRef" :fixed="false" @query="queryList">
    <!-- 列表内容 -->
    <view v-for="item in get(result, 'list', [])" :key="item.id">
      ...
    </view>
    <!-- 底部加载骨架屏 -->
    <template #loadingMoreLoading>
      <view class="skeleton-wrap">
        <wd-skeleton animation="gradient" :row-col="skeletonRowCol" />
      </view>
    </template>
  </z-paging>
</template>
```

### 关键约定

1. **必须使用 `z-paging`** 作为分页容器，不要手写 scroll-view + 手动分页逻辑
2. **必须使用 `useRequest` 的 `listOptions`** 管理分页状态，不要手动维护 page/size
3. **`queryList(pageNo)` 是唯一回调**，`pageNo === 1` 为刷新，否则为加载更多
4. **`get()` 安全取值**，用 `@iceywu/utils` 的 `get()` 代替 `result.value?.list`，避免模板中可选链
5. **瀑布流用 `up-waterfall`**，普通列表直接 `v-for`
6. **骨架屏必须提供**，通过 `#loadingMoreLoading` 插槽实现
7. **暴露 `refresh`/`reload`** 方法供父组件调用：`defineExpose({ refresh, reload })`
