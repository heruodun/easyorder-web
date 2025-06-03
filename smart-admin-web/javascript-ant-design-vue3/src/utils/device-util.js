import { computed } from 'vue';
import { useAppStore } from '/@/store/modules/system/app-config';

export const useDevice = () => {
  const appStore = useAppStore();

  const isMobile = computed(() => appStore.device === 'mobile');
  const isDesktop = computed(() => appStore.device === 'desktop');
  const wrapClassNameInfo = computed(() => (isDesktop.value ? 'ant-modal-cust-warp depot-mask' : 'ant-modal-cust-warp'));

  return {
    device: computed(() => appStore.device),
    isMobile,
    isDesktop,
    wrapClassNameInfo,
  };
};
