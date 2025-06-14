<template>
  <div v-if="!reloading" class="j-area-linkage">
    <area-cascader
      v-bind="$attrs"
      v-if="_type === enums.type[0]"
      :value="innerValue"
      :data="pcaa"
      :level="1"
      :style="{ width }"
      v-on="_listeners"
      @change="handleChange"
    />
    <area-select
      v-bind="$attrs"
      v-else-if="_type === enums.type[1]"
      :value="innerValue"
      :data="pcaa"
      :level="2"
      v-on="_listeners"
      @change="handleChange"
    />
    <div v-else>
      <span style="color: red"> Bad type value: {{ _type }}</span>
    </div>
  </div>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
import { pcaa } from 'area-data'

export default {
  name: 'JAreaLinkage',
  props: {
    value: {
      type: String,
      required: false,
    },
    // 组件的类型，可选值：
    // select 下拉样式
    // cascader 级联样式（默认）
    type: {
      type: String,
      default: 'cascader',
    },
    width: {
      type: String,
      default: '100%',
    },
  },
  data() {
    return {
      pcaa,
      innerValue: [],
      usedListeners: ['change'],
      enums: {
        type: ['cascader', 'select'],
      },
      reloading: false,
      areaData: '',
    }
  },
  computed: {
    _listeners() {
      let listeners = { ...this.$listeners }
      // 去掉已使用的事件，防止冲突
      this.usedListeners.forEach((key) => {
        delete listeners[key]
      })
      return listeners
    },
    _type() {
      if (this.enums.type.includes(this.type)) {
        return this.type
      } else {
        console.error(
          `JAreaLinkage的type属性只能接收指定的值（${this.enums.type.join(
            '|'
          )}）`
        )
        return this.enums.type[0]
      }
    },
  },
  watch: {
    value: {
      deep: true,
      immediate: true,

      handler() {
        this.loadDataByValue(this.value)
      },
    },
  },
  created() {
    this.initAreaData()
  },
  methods: {
    /** 通过 value 反推 options */
    loadDataByValue(value) {
      if (!value || value.length == 0) {
        this.innerValue = []
        this.reloading = true
        setTimeout(() => {
          this.reloading = false
        }, 100)
      } else {
        this.initAreaData()
        let arr = this.areaData.getRealCode(value)
        this.innerValue = arr
      }
    },
    /** 通过地区code获取子级 */
    loadDataByCode(value) {
      let options = []
      let data = pcaa[value]
      if (data) {
        for (let key in data) {
          if (data.hasOwnProperty(key)) {
            options.push({ value: key, label: data[key] })
          }
        }
        return options
      } else {
        return []
      }
    },
    /** 判断是否有子节点 */
    hasChildren(options) {
      options.forEach((option) => {
        let data = this.loadDataByCode(option.value)
        option.isLeaf = data.length === 0
      })
    },
    handleChange(values) {
      let value = values[values.length - 1]
      $emit(this, 'change', value)
    },
    initAreaData() {
      if (!this.areaData) {
        this.areaData = new Area()
      }
    },
  },
  model: { prop: 'value', event: 'change' },
  emits: ['change', 'update:value'],
}
</script>

<style lang="less" scoped>
.j-area-linkage {
  height: 40px;
  /deep/ .area-cascader-wrap .area-select {
    width: 100%;
  }

  /deep/ .area-select .area-selected-trigger {
    line-height: 1.15;
  }
}
</style>
