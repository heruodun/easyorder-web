<template>
  <div>
    <a-input-search
      v-model:value="userNames"
      placeholder="请先选择用户"
      readOnly
      unselectable="on"
      @search="onSearchDepUser"
    >
      <template v-slot:enterButton>
        <a-button :disabled="disabled">选择用户</a-button>
      </template>
    </a-input-search>
    <j-select-user-by-dep-modal
      ref="selectModal"
      :modal-width="modalWidth"
      :multi="multi"
      @ok="selectOK"
      :user-ids="value"
      @initComp="initComp"
    />
  </div>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
import JSelectUserByDepModal from './modal/JSelectUserByDepModal'

export default {
  name: 'JSelectUserByDep',
  components: { JSelectUserByDepModal },
  props: {
    modalWidth: {
      type: Number,
      default: 1250,
      required: false,
    },
    value: {
      type: String,
      required: false,
    },
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
    multi: {
      type: Boolean,
      default: true,
      required: false,
    },
  },
  data() {
    return {
      userIds: '',
      userNames: '',
    }
  },
  mounted() {
    this.userIds = this.value
  },
  watch: {
    value(val) {
      this.userIds = val
    },
  },
  model: {
    prop: 'value',
    event: 'change',
  },
  methods: {
    initComp(userNames) {
      this.userNames = userNames
    },
    onSearchDepUser() {
      this.$refs.selectModal.showModal()
    },
    selectOK(rows, idstr) {
      console.log('当前选中用户', rows)
      console.log('当前选中用户ID', idstr)
      if (!rows) {
        this.userNames = ''
        this.userIds = ''
      } else {
        let temp = ''
        for (let item of rows) {
          temp += ',' + item.realname
        }
        this.userNames = temp.substring(1)
        this.userIds = idstr
      }
      $emit(this, 'change', this.userIds)
    },
  },
  emits: ['change', 'update:value'],
}
</script>

<style scoped></style>
