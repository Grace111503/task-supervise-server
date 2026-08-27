#!/usr/bin/env node

import { execSync } from 'node:child_process'
import {
  existsSync,
  mkdirSync,
  readdirSync,
  readFileSync,
  writeFileSync,
} from 'node:fs'
import path from 'node:path'
import process from 'node:process'
import ci from 'miniprogram-ci'

const ENV_CONFIG = {
  allow: ['dev', 'test', 'prod'],
  default: 'dev',
}

const PROJECT_ROOT = process.cwd()
const APP_CONFIG_PATH = path.resolve(PROJECT_ROOT, 'src/config/app.ts')
const MINIPROGRAM_CI_NODE_HOOK_PATH = path.resolve(
  PROJECT_ROOT,
  'scripts/miniprogram-ci-node-hook.cjs'
)
const PNPM_STORE_DIR = path.resolve(PROJECT_ROOT, 'node_modules/.pnpm')
const MODE = process.argv[2]
const ENV_ARG = process.argv[3]
const ENV = resolveEnv(ENV_ARG)
const ESCAPED_NEWLINE_RE = /\\n/g
const SEMVER_RE = /\d+\.\d+\.\d+/
const SETTING = {
  autoPrefixWXSS: true,
  disableUseStrict: false,
  es6: true,
  minify: true,
  useProjectConfig: true,
}

function resolveEnv(value) {
  const input = (
    value ||
    process.env.MINI_ENV ||
    ENV_CONFIG.default
  ).toLowerCase()
  return ENV_CONFIG.allow.includes(input) ? input : ENV_CONFIG.default
}

function fail(message) {
  console.error(`❌ ${message}`)
  process.exit(1)
}

function ensureLatestMiniprogramCiCompatibility() {
  if (!existsSync(PNPM_STORE_DIR)) return

  const targetDirs = readdirSync(PNPM_STORE_DIR)
    .filter((name) => name.startsWith('miniprogram-ci@2.1.31'))
    .map((name) =>
      path.resolve(
        PNPM_STORE_DIR,
        name,
        'node_modules/@babel/helper-compilation-targets/lib/index.js'
      )
    )
    .filter((filePath) => existsSync(filePath))

  for (const filePath of targetDirs) {
    const source = readFileSync(filePath, 'utf8')
    if (
      source.includes(
        'const LruCacheCtor = typeof _lruCache === "function" ? _lruCache : _lruCache.LRUCache;'
      )
    )
      continue

    const patched = source.replace(
      'const targetsCache = new _lruCache({\n  max: 64\n});',
      'const LruCacheCtor = typeof _lruCache === "function" ? _lruCache : _lruCache.LRUCache;\nconst targetsCache = new LruCacheCtor({\n  max: 64\n});'
    )

    if (patched !== source) {
      writeFileSync(filePath, patched, 'utf8')
      console.log(
        `🩹 已应用 miniprogram-ci 2.1.31 兼容补丁: ${path.relative(PROJECT_ROOT, filePath)}`
      )
    }
  }
}

function ensureMiniprogramCiNodeHook() {
  const hookPath = MINIPROGRAM_CI_NODE_HOOK_PATH.replace(/\\/g, '/')
  const requireOption = `--require=${hookPath}`
  const nodeOptions = process.env.NODE_OPTIONS?.trim()

  if (!nodeOptions) {
    process.env.NODE_OPTIONS = requireOption
    return
  }

  if (nodeOptions.includes(MINIPROGRAM_CI_NODE_HOOK_PATH)) return

  process.env.NODE_OPTIONS = `${nodeOptions} ${requireOption}`
}

function readExportedString(source, key) {
  const match = source.match(
    new RegExp(String.raw`export\s+const\s+${key}\s*=\s*(["'\x60])([\s\S]*?)\1`)
  )
  return match?.[2]?.trim() || ''
}

function readExportedNumber(source, key) {
  const match = source.match(
    new RegExp(String.raw`export\s+const\s+${key}\s*=\s*(\d+)`)
  )
  if (!match) return

  return Number(match[1])
}

function readAppConfigSource() {
  if (!existsSync(APP_CONFIG_PATH)) {
    fail(
      '找不到 src/config/app.ts，请在该文件中配置微信小程序 CI 参数，或改用环境变量'
    )
  }

  return readFileSync(APP_CONFIG_PATH, 'utf8')
}

function resolveBuildDir(env) {
  const candidates = [
    path.resolve(PROJECT_ROOT, 'dist/build/mp-weixin'),
    path.resolve(PROJECT_ROOT, 'dist/dev/mp-weixin'),
  ]

  const buildDir = candidates.find((candidate) => existsSync(candidate))
  if (!buildDir) {
    fail('找不到微信小程序构建产物目录，请先执行对应的 build:mp-weixin 脚本')
  }

  const projectConfigPath = path.resolve(buildDir, 'project.config.json')
  if (!existsSync(projectConfigPath)) {
    fail(
      `找不到 ${path.relative(PROJECT_ROOT, projectConfigPath)}，请确认 uni-app 微信小程序构建已成功完成`
    )
  }

  return {
    buildDir,
    projectConfigPath,
  }
}

function validateProjectAppId(projectConfigPath, expectedAppId) {
  const projectConfig = JSON.parse(readFileSync(projectConfigPath, 'utf8'))
  const actualAppId =
    typeof projectConfig.appid === 'string' ? projectConfig.appid.trim() : ''

  if (!actualAppId) {
    fail(
      `构建产物中的 appid 为空，请检查 ${path.relative(PROJECT_ROOT, projectConfigPath)}`
    )
  }

  if (actualAppId !== expectedAppId) {
    fail(
      `构建产物中的 appid (${actualAppId}) 与 src/config/app.ts 中的配置 (${expectedAppId}) 不一致，请先用真实 appid 重新构建微信小程序`
    )
  }
}

function resolveAppId() {
  const envAppid = process.env.MINI_APPID?.trim()
  if (envAppid) {
    console.log('📌 使用环境变量 MINI_APPID')
    return envAppid
  }

  const source = readAppConfigSource()
  const wechatAppid = readExportedString(source, 'APP_ID_WECHAT')
  const defaultAppid = readExportedString(source, 'APP_ID')
  const configAppid = wechatAppid || defaultAppid

  if (!configAppid) {
    fail(
      '未找到微信小程序 AppID，请在 src/config/app.ts 中配置 APP_ID_WECHAT，或设置 MINI_APPID 环境变量'
    )
  }

  console.log(
    `📌 使用 src/config/app.ts 中的 ${wechatAppid ? 'APP_ID_WECHAT' : 'APP_ID'}`
  )
  return configAppid
}

function resolveRobot() {
  const envRobot = process.env.MINI_CI_ROBOT
  if (envRobot) {
    const robot = validateRobotValue(envRobot)
    if (robot !== undefined) {
      console.log('🤖 使用环境变量 MINI_CI_ROBOT')
      return robot
    }

    return
  }

  const source = readAppConfigSource()
  const configRobot = readExportedNumber(source, 'APP_WECHAT_CI_ROBOT')
  if (configRobot === undefined) return

  const robot = validateRobotValue(configRobot)
  if (robot !== undefined) {
    console.log('🤖 使用 src/config/app.ts 中的 APP_WECHAT_CI_ROBOT')
    return robot
  }
}

function validateRobotValue(value) {
  if (value === undefined || value === null || value === '') return

  const robot = Number(value)
  if (Number.isInteger(robot) && robot >= 1 && robot <= 30) return robot

  console.warn(`⚠️ 已忽略无效的机器人编号 ${value}，有效范围为 1-30`)
}

function resolvePrivateKeyPath(appid) {
  const source = readAppConfigSource()
  const configuredPath = readExportedString(
    source,
    'APP_WECHAT_CI_PRIVATE_KEY_PATH'
  )
  if (configuredPath) {
    const normalizedPath = path.resolve(PROJECT_ROOT, configuredPath)
    if (!existsSync(normalizedPath)) {
      fail(`src/config/app.ts 中配置的私钥文件不存在: ${configuredPath}`)
    }

    console.log('🔑 使用 src/config/app.ts 中的 APP_WECHAT_CI_PRIVATE_KEY_PATH')
    return {
      keyFile: configuredPath,
      privateKeyPath: normalizedPath,
    }
  }

  const fallbackFile = `scripts/private.${appid}.key`
  const fallbackPath = path.resolve(PROJECT_ROOT, fallbackFile)
  if (!existsSync(fallbackPath)) {
    fail(`未找到私钥文件 ${fallbackFile}，或请改用 MINI_PRIVATE_KEY 环境变量`)
  }

  return {
    keyFile: fallbackFile,
    privateKeyPath: fallbackPath,
  }
}

function getAuthInfo() {
  const appid = resolveAppId()
  const privateKey = process.env.MINI_PRIVATE_KEY?.replace(
    ESCAPED_NEWLINE_RE,
    '\n'
  ).trim()

  if (privateKey) {
    console.log('🔑 使用环境变量 MINI_PRIVATE_KEY')
    return { appid, privateKey }
  }

  const scriptsDir = path.resolve(PROJECT_ROOT, 'scripts')
  if (!existsSync(scriptsDir)) {
    fail('未找到 scripts 目录，且未设置 MINI_PRIVATE_KEY')
  }

  const { keyFile, privateKeyPath } = resolvePrivateKeyPath(appid)

  console.log(`🔑 使用私钥文件: ${keyFile}`)

  return {
    appid,
    privateKeyPath,
  }
}

function getVersionInfo(env) {
  const packageJson = JSON.parse(
    readFileSync(path.resolve(PROJECT_ROOT, 'package.json'), 'utf8')
  )
  const rawVersion =
    typeof packageJson.version === 'string'
      ? packageJson.version.trim()
      : '0.1.0'
  const uploadVersion = rawVersion.match(SEMVER_RE)?.[0] || '0.1.0'
  const date = new Date()
  const ymd = `${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}${String(date.getDate()).padStart(2, '0')}`

  try {
    const commit = execSync('git rev-parse --short HEAD', {
      cwd: PROJECT_ROOT,
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'ignore'],
    }).trim()

    return {
      desc: `[${env}] ${rawVersion} (Build ${ymd}-${commit})`,
      version: uploadVersion,
    }
  } catch {
    return {
      desc: `[${env}] ${rawVersion} (Build ${ymd})`,
      version: uploadVersion,
    }
  }
}

function createProject(buildDir) {
  const authInfo = getAuthInfo()

  return new ci.Project({
    appid: authInfo.appid,
    ignores: ['node_modules/**/*', '.git/**/*', 'unpackage/**/*'],
    projectPath: buildDir,
    type: 'miniProgram',
    ...(authInfo.privateKey
      ? { privateKey: authInfo.privateKey }
      : { privateKeyPath: authInfo.privateKeyPath }),
  })
}

function writeQRCodeHtml(env, buildDir) {
  const fileName = `preview-${env}.png`
  const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Mini Program Preview</title>
  </head>
  <body>
    <h2>微信小程序预览环境：${env}</h2>
    <img src="${fileName}" alt="preview qrcode" style="width: 240px; height: 240px;" />
  </body>
</html>
`

  const htmlPath = path.resolve(buildDir, `preview-${env}.html`)
  writeFileSync(htmlPath, html, 'utf8')
  console.log(`📰 已输出二维码 HTML: ${htmlPath}`)
}

async function run() {
  ensureMiniprogramCiNodeHook()
  ensureLatestMiniprogramCiCompatibility()
  const { buildDir, projectConfigPath } = resolveBuildDir(ENV)
  mkdirSync(buildDir, { recursive: true })
  const robot = resolveRobot()
  const appid = resolveAppId()

  validateProjectAppId(projectConfigPath, appid)

  console.log(`📌 当前目标环境: ${ENV}`)
  console.log(`📦 当前构建目录: ${path.relative(PROJECT_ROOT, buildDir)}`)

  const project = createProject(buildDir)
  const versionInfo = getVersionInfo(ENV)

  if (MODE === 'preview') {
    const qrcodePath = path.resolve(buildDir, `preview-${ENV}.png`)
    console.log(`📡 开始生成预览二维码，描述: ${versionInfo.desc}`)

    const result = await ci.preview({
      desc: versionInfo.desc,
      project,
      qrcodeFormat: 'image',
      qrcodeOutputDest: qrcodePath,
      setting: SETTING,
      ...(robot ? { robot } : {}),
    })

    console.log('📦 PREVIEW RESULT:\n', JSON.stringify(result, null, 2))
    writeQRCodeHtml(ENV, buildDir)
    return
  }

  if (MODE === 'upload') {
    console.log(`⬆️ 开始上传体验版，version=${versionInfo.version}，env=${ENV}`)

    const result = await ci.upload({
      desc: versionInfo.desc,
      project,
      setting: SETTING,
      version: versionInfo.version,
      ...(robot ? { robot } : {}),
    })

    console.log('📦 UPLOAD RESULT:\n', JSON.stringify(result, null, 2))
    return
  }

  fail('未识别命令，只支持 preview 或 upload')
}

run().catch((error) => {
  console.error('❌ miniprogram-ci 执行失败：')
  console.error(error?.stack || error)
  process.exit(1)
})
