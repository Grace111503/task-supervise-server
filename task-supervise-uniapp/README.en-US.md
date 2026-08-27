# UniApp Starter Template

<!-- markdownlint-disable MD024 MD033 MD040 -->
<p align='center'>
<a href="https://github.com/vuejs/vue"><img src="https://img.shields.io/badge/Vue-3.5.33-4FC08D?style=flat-square&logo=vue.js&logoColor=white" alt="Vue"></a>
<a href="https://github.com/microsoft/TypeScript"><img src="https://img.shields.io/badge/TypeScript-6.0.3-3178C6?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript"></a>
<a href="https://vitejs.dev/"><img src="https://img.shields.io/badge/Vite-5.2.8-646CFF?style=flat-square&logo=vite&logoColor=white" alt="Vite"></a>
<a href="https://unocss.dev/"><img src="https://img.shields.io/badge/UnoCSS-66.6.8-1896FF?style=flat-square&logo=unocss&logoColor=white" alt="UnoCSS"></a>
<a href="https://pinia.vuejs.org/"><img src="https://img.shields.io/badge/Pinia-3.0.4-yellow?style=flat-square&logo=vue.js&logoColor=white" alt="Pinia"></a>
<a href="https://v2.wot-ui.cn/"><img src="https://img.shields.io/badge/Wot_UI-2.0.4-4D80F0?style=flat-square" alt="Wot UI"></a>
</p>

<p align='center'>
<a href="./README.md">简体中文</a> | <b>English</b>
</p>

<br/>

<p align='center'>
<a href="https://uniapp-starter.netlify.app">📱 Live Preview</a>
</p>

<br/>

A feature-rich, production-ready UniApp development template with modern toolchain and best practices. Supporting multi-platform deployment with excellent development experience.

## ✨ Features

### 🔧 Development Experience

- ⚡️ [Vue 3](https://github.com/vuejs/core) + [Vite](https://github.com/vitejs/vite) + [TypeScript](https://www.typescriptlang.org/) - Modern development stack
- 🗂 [File-based routing](./src/pages) - Automatic route generation based on file system
- 📦 [Auto-import components](./src/components) - No need to manually import components
- 📑 [Layout system](./src/layouts) - Flexible page layout management
- 🔥 [`<script setup>`](https://github.com/vuejs/rfcs/pull/227) - Latest Vue syntax
- 📥 [Auto-import APIs](./src/composables) - Direct use of Composition API

### 🎨 Styling & UI

- 🎨 [UnoCSS](https://unocss.dev/) - Instant atomic CSS engine for highly flexible UI development
- 😃 [Iconify](https://iconify.design) - Massive icon library support
- 🌙 Dark mode support - Built-in theme switching
- 📱 Responsive design - Perfect adaptation to various screen sizes
- 💎 **[Wot UI v2](https://v2.wot-ui.cn/)** - 80+ high-quality uni-app components with dark mode, i18n, and theme customization

### 🛠 Engineering

- 🍍 [Pinia](https://pinia.vuejs.org) - Modern state management
- 🌐 [Vue I18n](https://vue-i18n.intlify.dev/) - Internationalization support
- 🔐 HTTP client - Request wrapper based on `@uni-helper/uni-network`
- 📊 Data persistence - Pinia state persistence
- 📝 Ultracite + Biome - Code linting and formatting
- 🔒 Lefthook - Pre-commit code checking
- 📦 Dependency management - Uses pnpm

### � Cross-platform Support

- 📱 App (Android/iOS)
- 🌐 H5
- 🔧 WeChat Mini Program
- 🔧 Alipay Mini Program
- 🔧 Baidu Mini Program
- 🔧 ByteDance Mini Program
- 🔧 QQ Mini Program
- 🔧 Kuaishou Mini Program
- 🔧 JD Mini Program
- 🔧 Lark Mini Program

## 🧰 Tech Stack

### Core Frameworks

- **Vue 3** - Progressive JavaScript framework
- **UniApp** - Cross-platform application development framework
- **TypeScript** - Typed superset of JavaScript

### Build Tools

- **Vite** - Next generation frontend tooling
- **ESBuild** - Extremely fast JavaScript bundler
- **pnpm** - Fast package manager

### State Management

- **Pinia** - Vue 3 official state management library
- **pinia-plugin-persistedstate** - Pinia state persistence plugin
- **pinia-auto-refs** - Pinia auto-reference plugin

### Styling Solution

- **UnoCSS** - On-demand atomic CSS engine
- **Sass** - CSS preprocessor

### HTTP Client

- **@uni-helper/uni-network** - Cross-platform request library for UniApp
- Custom request interceptors
- Request/Response interception support
- Automatic token management

### Utility Libraries

- **dayjs** - Lightweight date manipulation library
- **@iceywu/utils** - Shared utility helpers
- **js-cookie** - Cookie management utility
- **js-md5** - MD5 hashing library
- **qs** - Query string parsing and stringifying
- **blurhash** - Image blur hash

### Development Tools

- **Ultracite** - Code linting and fixes
- **Biome** - Formatting and static analysis
- **Vue DevTools** - Vue developer tools
- **Lefthook** - Git hooks tool

### Plugin System

- **unplugin-auto-import** - API auto-import
- **unplugin-vue-components** - Component auto-import
- **@uni-helper/vite-plugin-uni-components** - UniApp component auto registration
- **@uni-helper/vite-plugin-uni-pages** - File-based routing system
- **@uni-helper/vite-plugin-uni-layouts** - Layout system
- **@uni-helper/vite-plugin-uni-manifest** - manifest.json generation
- **@uni-ku/bundle-optimizer** - Bundle optimization for UniApp

## 📁 Project Structure

```text
uniapp-starter/
├── src/
│   ├── pages/ pages-sub/ sub-packages/   # Pages, sub-packages, business modules
│   ├── layouts/                          # Layout system
│   ├── components/                       # Shared UI components
│   ├── api/                              # API wrapper examples
│   ├── composables/ hooks/               # Composables and page hooks
│   ├── store/                            # Pinia stores
│   ├── config/                           # Runtime and signing configuration
│   ├── locale/                           # i18n resources
│   ├── styles/ static/                   # Global styles and static assets
│   ├── utils/ helper/                    # Utilities and helper logic
│   ├── @types/                           # Generated and handwritten type declarations
│   ├── App.vue / main.ts                 # Application entry
│   └── manifest.json / pages.json        # Generated uni-app config output
├── auto/                                 # Scaffolding scripts such as addPage.ts
├── scripts/                              # Release, WeChat CI, and skills sync scripts
├── public/                               # Public assets for H5
├── biome.jsonc                           # Biome / Ultracite rule configuration
├── manifest.config.ts                    # Source config for src/manifest.json
├── pages.config.ts                       # Page and sub-package source config
├── uno.config.ts                         # UnoCSS configuration
├── vite.config.ts                        # Vite and uni plugin configuration
├── tsconfig.json                         # TypeScript configuration
└── package.json                          # Scripts, dependencies, and project entry
```

### Notes On The Layout

- `src/pages`, `src/pages-sub`, and `src/sub-packages` organize page entry points, while routes are generated from `pages.config.ts`.
- `src/layouts` contains the shell layout system; see `src/layouts/README.md` for usage details.
- `src/@types/auto-imports.d.ts`, `src/@types/components.d.ts`, and `src/@types/uni-pages.d.ts` are generated files and usually should not be edited manually.
- `src/pages.json` and `src/manifest.json` are generated outputs. Prefer editing `pages.config.ts` and `manifest.config.ts`.
- `scripts/mini-ci.js` handles WeChat Mini Program preview/upload, `scripts/release.js` handles release flow, and `scripts/skills-add.js` syncs local agent skills.
- The code quality toolchain is now based on `biome.jsonc` and `ultracite`; there is no standalone ESLint config in this repo.

## 🚀 Quick Start

### Requirements

- Node.js >= 18
- pnpm >= 8

### Installation

```bash
# Or clone repository
git clone <your-repo-url> my-uniapp-project
cd my-uniapp-project

# Install dependencies
pnpm install
```

Installing dependencies automatically runs prepare, which installs Git hooks and syncs IceyWu/skills through a project wrapper script. The wrapper keeps only the local VS Code/Copilot, Claude, Kiro, and Trae agent directories and removes the extra directories generated by the skills CLI; related generated files are already ignored by git.

### 🔄 Sync with Upstream Repository

To synchronize the latest template code from the upstream repository, run:

```bash
pnpm syn_rpo
# or
npm run syn_rpo
```

This command will automatically pull the latest content from <https://github.com/IceyWu/uniapp-starter.git> main branch.

### Development

```bash
# H5 development
pnpm dev
# or
pnpm dev:h5

# WeChat Mini Program development
pnpm dev:mp-weixin

# App development
pnpm dev:app

# Other platforms
pnpm dev:mp-alipay    # Alipay Mini Program
pnpm dev:mp-baidu     # Baidu Mini Program
pnpm dev:mp-toutiao   # ByteDance Mini Program
pnpm dev:mp-qq        # QQ Mini Program
```

### Build

```bash
# H5 build
pnpm build:h5

# WeChat Mini Program build
pnpm build:mp-weixin

# WeChat Mini Program build for development mode
pnpm build:mp-weixin:dev

# WeChat Mini Program build for test mode
pnpm build:mp-weixin:test

# WeChat Mini Program build for production mode
pnpm build:mp-weixin:prod

# App build
pnpm build:app

# Other platform builds
pnpm build:mp-alipay  # Alipay Mini Program
pnpm build:mp-baidu   # Baidu Mini Program
```

### WeChat Mini Program CI

The starter now includes a `miniprogram-ci` script at `scripts/mini-ci.js` for automated preview QR generation and experience-version uploads.

1. Build the WeChat Mini Program output first. The script reads files from `dist/build/mp-weixin`.

2. The script reads the appid from `src/config/app.ts` first:

- `APP_ID_WECHAT`: WeChat Mini Program appid
- `APP_ID`: fallback appid when `APP_ID_WECHAT` is empty
- `APP_WECHAT_CI_ROBOT`: optional CI robot id in the range `1` to `30`
- `APP_WECHAT_CI_PRIVATE_KEY_PATH`: optional private key file path relative to the project root

You can still override it and provide auth info with environment variables:

- `MINI_APPID`: WeChat Mini Program appid
- `MINI_PRIVATE_KEY`: private key content with line breaks escaped as `\n`
- `MINI_CI_ROBOT`: optional CI robot id from `1` to `30`

If `MINI_PRIVATE_KEY` is missing, the script uses `APP_WECHAT_CI_PRIVATE_KEY_PATH` first, then falls back to `scripts/private.<appid>.key`.

1. Generate preview QR code:

```bash
pnpm preview
pnpm preview test
pnpm preview prod
```

This writes `preview-<env>.png` and `preview-<env>.html` into `dist/build/mp-weixin`.

1. Upload an experience version:

```bash
pnpm upload
pnpm upload test
pnpm upload prod
```

The upload version comes from `package.json`, and the description includes env, build date, and the current short Git commit hash.

### Type Checking

```bash
pnpm type-check

# Code linting
pnpm lint

# Code formatting
pnpm lint:fix
```

## ⚙️ Configuration

### Environment Configuration

Configure project-related information in the `src/config/` directory:

- `app.ts` - Application basic configuration
- `domain.ts` - API domain configuration

### Theme Configuration

Configure application theme colors in `src/theme.json`.

### Internationalization Configuration

Add language files in the `src/locale/` directory:

- `zh.json` - Chinese
- `en.json` - English

### HTTP Request Configuration

Configure request-related settings in the `src/utils/http/` directory:

- `index.ts` - Request wrapper
- `auth.ts` - Authentication related
- `tool.ts` - Utility methods

## 📖 Usage Guide

### Page Routing

Use file-based routing by creating `.vue` files in the `src/pages/` directory to automatically generate routes.

```bash
src/pages/
├── index.vue          # /pages/index/index
├── about.vue          # /pages/about/index
└── user/
    └── profile.vue    # /pages/user/profile/index
```

### Layout System

Create layout components in the `src/layouts/` directory and use them in pages:

```vue
<template>
  <div>Page content</div>
</template>

<route lang="yaml">
meta:
  layout: default
</route>
```

### Component Development

Components are auto-imported, use them directly in pages:

```vue
<!-- Auto-imports src/components/NavBar/index.vue -->
<template>
  <NavBar title="Page Title" />
</template>
```

### State Management

Use Pinia for state management:

```typescript
// src/store/user.ts
export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)

  const setUserInfo = (info: any) => {
    userInfo.value = info
  }

  return { userInfo, setUserInfo }
}, {
  // Enable persistence
  persist: true
})
```

### API Calls

```typescript
// src/api/user.ts
export function getUserInfo() {
  return http.get('/api/user/info')
}

// Use in components
const { data } = await getUserInfo()
```

## 🛠 Development Tools

### VS Code Extensions Recommended

- Vue Language Features (Volar)
- TypeScript Vue Plugin (Volar)
- Biome
- UnoCSS IntelliSense

## 🙏 Acknowledgements

Thanks to the following awesome open source projects:

- [Vue.js](https://vuejs.org/) - Progressive JavaScript framework
- [UniApp](https://uniapp.dcloud.io/) - Cross-platform application development framework
- [Vite](https://vitejs.dev/) - Next generation frontend tooling
- [UnoCSS](https://unocss.dev/) - On-demand atomic CSS engine
- [Pinia](https://pinia.vuejs.org/) - State management for Vue
- [Wot UI](https://v2.wot-ui.cn/) - AI-friendly uni-app component library
- [Biome](https://biomejs.dev/) - Formatting and static analysis
- [Ultracite](https://www.npmjs.com/package/ultracite) - Unified linting workflow
- uni-app-schemas
- uni-app-snippets

### Debugging Tools

- Vue DevTools - Vue debugging
- WeChat Developer Tools - Mini Program debugging
- Chrome DevTools - H5 debugging

## 📝 Best Practices

### Code Standards

- Use TypeScript for type safety
- Follow the project's linting rules
- Use `<script setup>` syntax
- Component names use PascalCase
- File names use kebab-case

### Performance Optimization

- Use on-demand imports to reduce bundle size
- Proper use of component lazy loading
- Optimize image resource sizes
- Avoid excessive component nesting

### Project Management

- Use semantic commit messages
- Update dependencies in time
- Maintain project documentation

## 📋 Deployment Checklist

When using this template, please update project information according to the following checklist:

- [ ] Modify project name and description in `package.json`
- [ ] Update application information and appid in `manifest.config.ts`
- [ ] Replace icon files in the `public/` directory
- [ ] Configure related settings in the `src/config/` directory
- [ ] Update author information in the `LICENSE` file
- [ ] Clean up demo pages and components
- [ ] Configure CI/CD pipeline
- [ ] Set production environment variables

## 🤝 Contributing

Issues and Pull Requests are welcome!

## 📄 License

MIT License

---

If this template helps you, please give it a ⭐️ for support!
