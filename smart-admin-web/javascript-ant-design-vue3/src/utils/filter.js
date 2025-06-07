import * as Vue from 'vue'
import * as dayjs from 'dayjs'

;(
  window.$vueApp.config.globalProperties.$filters ||
  (window.$vueApp.config.globalProperties.$filters = {})
).NumberFormat = function (value) {
  if (!value) {
    return '0'
  }
  let intPartFormat = value.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,') //将整数部分逢三一断
  return intPartFormat
}

;(
  window.$vueApp.config.globalProperties.$filters ||
  (window.$vueApp.config.globalProperties.$filters = {})
).dayjs = function (dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(dataStr).format(pattern)
}

;(
  window.$vueApp.config.globalProperties.$filters ||
  (window.$vueApp.config.globalProperties.$filters = {})
).moment = function (dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(dataStr).format(pattern)
}

/** 字符串超长截取省略号显示 */
;(
  window.$vueApp.config.globalProperties.$filters ||
  (window.$vueApp.config.globalProperties.$filters = {})
).ellipsis = function (value, vlength = 25) {
  if (!value) {
    return ''
  }
  console.log('vlength: ' + vlength)
  if (value.length > vlength) {
    return value.slice(0, vlength) + '...'
  }
  return value
}
