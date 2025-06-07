<template>
  <global-layout>
    <router-view v-slot="{ Component }" v-else>
      <transition name="page-transition">
        <component :is="Component"></component>
      </transition>
    </router-view>
  </global-layout>
</template>

<script>
import GlobalLayout from '@/components/page/GlobalLayout'

export default {
  name: 'BasicLayout',
  components: {
    GlobalLayout,
  },
  data() {
    return {}
  },
  computed: {
    keepAlive() {
      return this.$route.meta.keepAlive
    },
  },
  methods: {},
}
</script>

<style lang="less">
.page-transition-enter-from {
  opacity: 0;
}
.page-transition-leave-active {
  opacity: 0;
}
.page-transition-enter-from .page-transition-container,
.page-transition-leave-active .page-transition-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>
