import { computed } from 'vue';
import { useStore } from 'vuex';

// 布局相关功能
export const useLayout = () => {
  const store = useStore();

  // 布局相关状态计算属性
  return {
    layoutMode: computed(() => store.state.app.layout),
    navTheme: computed(() => store.state.app.theme),
    primaryColor: computed(() => store.state.app.color),
    colorWeak: computed(() => store.state.app.weak),
    multipage: computed(() => store.state.app.multipage),
    fixedHeader: computed(() => store.state.app.fixedHeader),
    fixSiderbar: computed(() => store.state.app.fixSiderbar),
    contentWidth: computed(() => store.state.app.contentWidth),
    autoHideHeader: computed(() => store.state.app.autoHideHeader),
    sidebarOpened: computed(() => store.state.app.sidebar.opened),
  };
};
