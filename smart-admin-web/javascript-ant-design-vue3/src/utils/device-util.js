import { computed } from 'vue';
import { useStore } from 'vuex';

// 设备相关功能
export const useDevice = () => {
  const store = useStore();

  // 设备状态计算属性
  const device = computed(() => store.state.app.device);

  // 设备检测方法
  const isMobile = () => device.value === 'mobile';
  const isDesktop = () => device.value === 'desktop';

  // 模态框类名生成
  const wrapClassNameInfo = computed(() => {
    return isDesktop() ? 'ant-modal-cust-warp depot-mask' : 'ant-modal-cust-warp';
  });

  return {
    device,
    isMobile,
    isDesktop,
    wrapClassNameInfo,
  };
};
