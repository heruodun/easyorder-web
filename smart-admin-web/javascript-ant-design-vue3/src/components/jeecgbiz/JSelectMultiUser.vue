<template>
  <!-- 定义在这里的参数都是不可在外部覆盖的，防止出现问题 -->
  <j-select-biz-component
    v-bind="attrs"
    :value="value"
    :ellipsisLength="25"
    :listUrl="url.list"
    :columns="columns"
  />
</template>

<script>
import JSelectBizComponent from './JSelectBizComponent'

export default {
  name: 'JSelectMultiUser',
  components: { JSelectBizComponent },
  props: ['value'],
  data() {
    return {
      url: { list: '/sys/user/list' },
      columns: [
        {
          title: '姓名',
          align: 'center',
          width: '25%',
          widthRight: '70%',
          dataIndex: 'realname',
        },
        { title: '账号', align: 'center', width: '25%', dataIndex: 'username' },
        { title: '电话', align: 'center', width: '20%', dataIndex: 'phone' },
        {
          title: '出生日期',
          align: 'center',
          width: '20%',
          dataIndex: 'birthday',
        },
      ],
      // 定义在这里的参数都是可以在外部传递覆盖的，可以更灵活的定制化使用的组件
      default: {
        name: '用户',
        width: 1200,
        displayKey: 'realname',
        returnKeys: ['id', 'username'],
        queryParamText: '账号',
      },
    }
  },
  computed: {
    attrs() {
      return Object.assign(this.default, this.$attrs)
    },
  },
  emits: ['update:value'],
}
</script>
