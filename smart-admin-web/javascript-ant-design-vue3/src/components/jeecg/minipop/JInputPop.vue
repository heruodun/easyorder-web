<template>
  <a-popover
    trigger="contextmenu"
    v-model:value="visible"
    :placement="position"
  >
    <!--"(node) => node.parentNode.parentNode"-->
    <template v-slot:title>
      <div>
        <span>{{ title }}</span>
        <span style="float: right" title="关闭">
          <a-icon type="close" @click="visible = false" />
        </span>
      </div>
    </template>
    <a-input
      :value="inputContent"
      @change="handleInputChange"
      :placeholder="placeholder"
    >
      <template v-slot:suffix>
        <a-icon type="fullscreen" @click.stop="pop" />
      </template>
    </a-input>
    <template v-slot:content>
      <div>
        <textarea
          :value="inputContent"
          @input="handleInputChange"
          :placeholder="placeholder"
          :style="{ height: height + 'px', width: width + 'px' }"
        ></textarea>
      </div>
    </template>
  </a-popover>
</template>

<script>
import { $on, $off, $once, $emit } from '../../../utils/gogocodeTransfer'
export default {
  name: 'JInputPop',
  props: {
    title: {
      type: String,
      default: '',
      required: false,
    },
    position: {
      type: String,
      default: 'right',
      required: false,
    },
    height: {
      type: Number,
      default: 200,
      required: false,
    },
    width: {
      type: Number,
      default: 150,
      required: false,
    },
    value: {
      type: String,
      required: false,
    },
    popContainer: {
      type: String,
      default: '',
      required: false,
    },
    placeholder: {
      type: String,
      required: false,
    },
  },
  data() {
    return {
      visible: false,
      inputContent: '',
    }
  },
  watch: {
    value: {
      deep: true,
      immediate: true,

      handler: function () {
        if (this.value && this.value.length > 0) {
          this.inputContent = this.value
        }
      },
    },
  },
  model: {
    prop: 'value',
    event: 'change',
  },
  methods: {
    handleInputChange(event) {
      this.inputContent = event.target.value
      $emit(this, 'change', this.inputContent)
    },
    pop() {
      this.visible = true
    },
    getPopupContainer(node) {
      if (!this.popContainer) {
        return node.parentNode
      } else {
        return document.getElementById(this.popContainer)
      }
    },
  },
  emits: ['change', 'update:value'],
}
</script>

<style scoped></style>
