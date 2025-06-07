<template>
  <a-select
    :value="arrayValue"
    @change="onChange"
    mode="multiple"
    :placeholder="placeholder"
  >
    <a-select-option
      v-for="(item, index) in options"
      :key="index"
      :value="item.value"
    >
      {{ item.text || item.label }}
    </a-select-option>
  </a-select>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
export default {
  name: 'JSelectMultiple',
  props: {
    placeholder: {
      type: String,
      default: '',
      required: false,
    },
    value: {
      type: String,
      required: false,
    },
    readOnly: {
      type: Boolean,
      required: false,
      default: false,
    },
    options: {
      type: Array,
      required: true,
    },
    triggerChange: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  data() {
    return {
      arrayValue: !this.value ? [] : this.value.split(','),
    }
  },
  watch: {
    value(val) {
      if (!val) {
        this.arrayValue = []
      } else {
        this.arrayValue = this.value.split(',')
      }
    },
  },
  methods: {
    onChange(selectedValue) {
      if (this.triggerChange) {
        $emit(this, 'change', selectedValue.join(','))
      } else {
        $emit(this, 'update:value', selectedValue.join(','))
      }
    },
  },
  emits: ['change', 'update:value'],
}
</script>
