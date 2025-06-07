// import Vue from 'vue'
import { mapState } from 'vuex';
import { useAppStore } from '/@/store/modules/system/app-config';

// const mixinsComputed = Vue.config.optionMergeStrategies.computed
// const mixinsMethods = Vue.config.optionMergeStrategies.methods

const mixin = {
  computed: {
    ...mapState({
      layoutMode: (state) => state.app.layout,
      navTheme: (state) => state.app.theme,
      primaryColor: (state) => state.app.color,
      colorWeak: (state) => state.app.weak,
      multipage: (state) => state.app.multipage, //多页签设置
      fixedHeader: (state) => state.app.fixedHeader,
      fixSiderbar: (state) => state.app.fixSiderbar,
      contentWidth: (state) => state.app.contentWidth,
      autoHideHeader: (state) => state.app.autoHideHeader,
      sidebarOpened: (state) => state.app.sidebar.opened,
    }),
  },
};

const mixinDevice = {
  computed: {
    ...mapState({
      device: (state) => state.app.device,
    }),
  },
  methods: {
    isMobile() {
      // return this.device === 'mobile';
      return useAppStore().isMobile;
    },
    isDesktop() {
      // console.log('mixin.js', typeof this);
      // console.log('mixin.js', this);
      // return this.device === 'desktop';
      return useAppStore().isDesktop;
    },
    wrapClassNameInfo() {
      return useAppStore().isDesktop ? 'ant-modal-cust-warp depot-mask' : 'ant-modal-cust-warp';
      // if (this.device === 'desktop') {
      //   return 'ant-modal-cust-warp depot-mask';
      // } else {
      //   return 'ant-modal-cust-warp';
      // }
    },
  },
};

export { mixin, mixinDevice };
