export default () => {
  const { t, locale } = useI18n()
  const toggleLocale = () => {
    locale.value = locale.value === 'zh' ? 'en' : 'zh'
    // uni.setStorageSync("lang",locale.value)
    uni.setLocale(locale.value)
  }

  const language = computed(() => {
    switch (locale.value) {
      case 'zh':
        return '中文'
      case 'en':
        return 'English'
      default:
        return '未知'
    }
  })

  return { language, locale, t, toggleLocale }
}
