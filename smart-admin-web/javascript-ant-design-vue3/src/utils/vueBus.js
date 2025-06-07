import * as Bus from 'vue'
let install = function (Vue) {
  window.$vueApp.config.globalProperties.$bus = new Bus()
}
export default { install }
