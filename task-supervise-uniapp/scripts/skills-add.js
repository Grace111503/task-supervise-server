#!/usr/bin/env node
// @ts-check
/**
 * Skill 添加脚本
 * ------------------------------------------------------------
 * 用法：pnpm skills:add
 *
 * 在下面的 SKILL_SOURCES 数组里维护你想要添加的 skill 来源。
 * 每个元素支持两种写法：
 *   1) 字符串：整个仓库的所有 skill
 *        "IceyWu/iceywu-devkit"
 *        => npx -y skills add IceyWu/iceywu-devkit -s '*' -a github-copilot --copy -y
 *   2) 对象：只添加指定的一个或多个 skill
 *        { repo: "IceyWu/iceywu-devkit", skills: ["design-md"] }
 *        => npx -y skills add IceyWu/iceywu-devkit -s design-md -a github-copilot --copy -y
 *        { repo: "IceyWu/iceywu-devkit", skills: ["design-md", "code-review"] }
 *        => npx -y skills add IceyWu/iceywu-devkit -s design-md -s code-review -a github-copilot --copy -y
 *
 * 只安装到 GitHub Copilot (.agents) 目录，不会生成其他 agent 的临时目录。
 */

import { spawnSync } from 'node:child_process'
import process from 'node:process'

// ============ 在这里维护你的 skill 来源 ============
/** @typedef {{ repo: string; skills?: string[] }} SkillSource */
/** @type {(string | SkillSource)[]} */
const SKILL_SOURCES = [
  { repo: 'IceyWu/iceywu-devkit', skills: ['openapi-lookup'] },
  'wot-ui/open-wot',
]
// ==================================================

const isWindows = process.platform === 'win32'

function getNpxCommand() {
  return isWindows ? 'npx.cmd' : 'npx'
}

/**
 * 归一化条目为 { repo, skills }
 * @param {string | SkillSource} entry
 */
function normalize(entry) {
  if (typeof entry === 'string') {
    return { repo: entry.trim(), skills: [] }
  }
  return {
    repo: (entry.repo || '').trim(),
    skills: Array.isArray(entry.skills) ? entry.skills.filter(Boolean) : [],
  }
}

/**
 * 添加单个 skill 来源，返回 spawn 结果
 * @param {string | SkillSource} entry
 */
function addSkill(entry) {
  const { repo, skills } = normalize(entry)
  // 指定了 skills 就逐个 -s，否则 -s '*'
  const skillArgs = skills.length
    ? skills.flatMap((name) => ['-s', name])
    : ['-s', '*']

  const args = [
    '-y',
    'skills',
    'add',
    repo,
    ...skillArgs,
    '-a',
    'github-copilot',
    '--copy',
    '-y',
  ]
  const label = skills.length
    ? `${repo} [${skills.join(', ')}]`
    : `${repo} (all)`
  console.log(`\n[skills:add] adding ${label} ...`)

  return spawnSync(getNpxCommand(), args, {
    cwd: process.cwd(),
    shell: isWindows,
    stdio: 'inherit',
  })
}

function run() {
  if (SKILL_SOURCES.length === 0) {
    console.log(
      '⚠️  SKILL_SOURCES 数组为空。请在 scripts/skills-add.js 顶部添加 skill 仓库后重试。'
    )
    return
  }

  const results = SKILL_SOURCES.map((entry) => addSkill(entry))

  // 抛出第一个执行错误
  const errored = results.find((r) => r.error)
  if (errored?.error) {
    throw errored.error
  }

  // 以第一个非零退出码结束
  const failedStatus = results.map((r) => r.status || 0).find((s) => s !== 0)
  process.exit(failedStatus || 0)
}

run()
