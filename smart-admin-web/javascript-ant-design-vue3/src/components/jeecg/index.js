import JModal from './JModal'
import JFormContainer from './JFormContainer.vue'

export default {
  install(Vue) {
    window.$vueApp.component('JFormContainer', JFormContainer)
    window.$vueApp.component(JModal.name, JModal)
  },
}
