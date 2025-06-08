import JModal from './JModal/index.vue';
import JFormContainer from './JFormContainer.vue';

export default {
  install(app) {
    // Vue 3 接收 app 实例作为参数
    // 注册全局组件
    app.component('JFormContainer', JFormContainer);
    app.component(JModal.name, JModal);
  },
};
