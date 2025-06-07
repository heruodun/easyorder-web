<template>
  <a-menu
    :style="style"
    class="contextmenu"
    v-show="visible"
    @click="handleClick"
    :selectedKeys="selectedKeys"
  >
    <a-menu-item :key="item.key" v-for="item in itemList">
      <a-icon role="menuitemicon" v-if="item.icon" :type="item.icon" />{{
        item.text
      }}
    </a-menu-item>
  </a-menu>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
export default {
  name: 'Contextmenu',
  props: {
    visible: {
      type: Boolean,
      required: false,
      default: false,
    },
    itemList: {
      type: Array,
      required: true,
      default: () => [],
    },
  },
  data() {
    return {
      left: 0,
      top: 0,
      target: null,
      selectedKeys: [],
    }
  },
  computed: {
    style() {
      return {
        left: this.left + 'px',
        top: this.top + 'px',
      }
    },
  },
  created() {
    window.addEventListener('mousedown', (e) => this.closeMenu(e))
    window.addEventListener('contextmenu', (e) => this.setPosition(e))
  },
  methods: {
    closeMenu(e) {
      if (
        this.visible === true &&
        ['menuitemicon', 'menuitem'].indexOf(e.target.getAttribute('role')) < 0
      ) {
        $emit(this, 'update:visible', false)
      }
    },
    setPosition(e) {
      this.left = e.clientX
      this.top = e.clientY
      this.target = e.target
    },
    handleClick({ key }) {
      $emit(this, 'select', key, this.target)
      $emit(this, 'update:visible', false)
    },
  },
  emits: ['update:visible', 'select'],
}
</script>

<style lang="less" scoped>
.contextmenu {
  position: fixed;
  z-index: 1;
  border: 1px solid #9e9e9e;
  border-radius: 4px;
  box-shadow: 2px 2px 10px #aaaaaa !important;
}
</style>
