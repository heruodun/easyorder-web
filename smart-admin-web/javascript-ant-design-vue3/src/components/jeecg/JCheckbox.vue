<template>
  <a-checkbox-group
    v-bind="$attrs"
    :options="options"
    :value="checkboxArray"
    @change="onChange"
  />
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
export default {
  name: 'JCheckbox',
  props: {
    value: {
      type: String,
      required: false,
    },
    /*label value*/
    options: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      checkboxArray: !this.value ? [] : this.value.split(','),
    }
  },
  watch: {
    value(val) {
      if (!val) {
        this.checkboxArray = []
      } else {
        this.checkboxArray = this.value.split(',')
      }
    },
  },
  methods: {
    onChange(checkedValues) {
      $emit(this, 'change', checkedValues.join(','))
    },
  },
  model: {
    prop: 'value',
    event: 'change',
  },
  emits: ['change', 'update:value'],
}
</script>
