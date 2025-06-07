<template>
  <j-select-biz-component
    v-bind="$attrs"
    :value="value"
    name="角色"
    displayKey="roleName"
    :returnKeys="returnKeys"
    :listUrl="url.list"
    :columns="columns"
    queryParamText="角色编码"
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
      returnKeys: ['id', 'roleCode'],
      url: { list: '/sys/role/list' },
      columns: [
        {
          title: '角色名称',
          dataIndex: 'roleName',
          align: 'center',
          width: 120,
        },
        {
          title: '角色编码',
          dataIndex: 'roleCode',
          align: 'center',
          width: 120,
        },
      ],
    }
  },
  emits: ['update:value'],
}
</script>
