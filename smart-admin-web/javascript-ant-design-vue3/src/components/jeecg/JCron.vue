<template>
  <div class="components-input-demo-presuffix">
    <a-input
      @click="openModal"
      placeholder="corn表达式"
      v-model:value="cron"
      @change="handleOK"
    >
      <template v-slot:prefix>
        <a-icon type="schedule" title="corn控件" />
      </template>
      <template v-slot:suffix>
        <a-icon
          v-if="cron"
          type="close-circle"
          @click="handleEmpty"
          title="清空"
        />
      </template>
    </a-input>
    <JCronModal ref="innerVueCron" :data="cron" @ok="handleOK"></JCronModal>
  </div>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
import JCronModal from './modal/JCronModal'
export default {
  name: 'JCron',
  components: {
    JCronModal,
  },
  props: {
    value: {
      required: false,
      type: String,
    },
  },
  data() {
    return {
      cron: this.value,
    }
  },
  watch: {
    value(val) {
      this.cron = val
    },
  },
  methods: {
    openModal() {
      this.$refs.innerVueCron.show()
    },
    handleOK(val) {
      this.cron = val
      $emit(this, 'change', this.cron)
      //this.$emit("change", Object.assign({},  this.cron));
    },
    handleEmpty() {
      this.handleOK('')
    },
  },
  model: {
    prop: 'value',
    event: 'change',
  },
  emits: ['change', 'update:value'],
}
</script>

<style scoped>
.components-input-demo-presuffix .anticon-close-circle {
  cursor: pointer;
  color: #ccc;
  transition: color 0.3s;
  font-size: 12px;
}
.components-input-demo-presuffix .anticon-close-circle:hover {
  color: #f5222d;
}
.components-input-demo-presuffix .anticon-close-circle:active {
  color: #666;
}
</style>
