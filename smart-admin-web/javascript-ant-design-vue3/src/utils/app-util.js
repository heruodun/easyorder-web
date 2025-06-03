import { computed } from 'vue';
import { useAppStore } from '/@/store/modules/system/app-config';

export const useAppSettings = () => {
  const appStore = useAppStore();

  return {
    layoutMode: computed(() => appStore.layout),
    navTheme: computed(() => appStore.theme),
    primaryColor: computed(() => appStore.color),
    colorWeak: computed(() => appStore.weak),
    multipage: computed(() => appStore.multipage),
    fixedHeader: computed(() => appStore.fixedHeader),
    fixSiderbar: computed(() => appStore.fixSiderbar),
    contentWidth: computed(() => appStore.contentWidth),
    autoHideHeader: computed(() => appStore.autoHideHeader),
    sidebarOpened: computed(() => appStore.sidebarOpened),
  };
};
