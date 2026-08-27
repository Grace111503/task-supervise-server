export default {
  contributors: true,
  newVersion: false, // 禁用自动版本更新，由发布脚本控制
  titles: {
    breakingChanges: '💥 Breaking Changes | 破坏性更改',
  },
  types: {
    build: { title: '📦 Build System | 构建' },
    chore: { title: '🔧 Chores | 其他更改' },
    ci: { title: '👷 Continuous Integration | CI' },
    docs: { title: '📚 Documentation | 文档' },
    feat: { title: '✨ Features | 新功能' },
    fix: { title: '🐛 Bug Fixes | 问题修复' },
    perf: { title: '⚡ Performance Improvements | 性能优化' },
    refactor: { title: '♻️ Code Refactoring | 代码重构' },
    revert: { title: '⏪ Reverts | 回滚' },
    style: { title: '💄 Styles | 样式' },
    test: { title: '✅ Tests | 测试' },
  },
}
