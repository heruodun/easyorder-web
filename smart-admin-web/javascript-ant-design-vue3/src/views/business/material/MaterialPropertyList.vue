<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- table区域-begin -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            @change="handleTableChange"
          >
            <template v-slot:action="text, record">
              <span>
                <a @click="handleEdit(record)">编辑</a>
              </span>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <material-property-modal ref="modalForm" @ok="modalFormOk"></material-property-modal>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
  import MaterialPropertyModal from './modules/MaterialPropertyModal';
  import { JeecgListMixin } from '/@/mixins/JeecgListMixin';
  import JDate from '/@/components/jeecg/JDate.vue';
  import { getAction } from '/@/api/manage';
  import * as Vue from 'vue';
  import { useMaterialStore } from '/@/store/modules/material';

  export default {
    name: 'MaterialPropertyList',
    mixins: [JeecgListMixin],
    components: {
      MaterialPropertyModal,
      JDate,
    },
    data() {
      return {
        labelCol: {
          span: 5,
        },
        wrapperCol: {
          span: 18,
          offset: 1,
        },
        // 查询条件
        queryParam: { name: '', type: '' },
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 40,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            },
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: 100,
            align: 'center',
            scopedSlots: { customRender: 'action' },
          },
          { title: '名称', dataIndex: 'nativeName', width: 100 },
          { title: '别名', dataIndex: 'anotherName', width: 100 },
        ],
        url: {
          list: '/materialProperty/list',
          delete: '/materialProperty/delete',
          deleteBatch: '/materialProperty/deleteBatch',
        },
      };
    },
    computed: {},
    methods: {
      loadData(arg) {
        let params = this.getQueryParams(); //查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.code === 200) {
            this.dataSource = res.data.rows;
            this.ipagination.total = res.data.total;
            // Vue.ls.set('materialPropertyList', res.data.rows, 7 * 24 * 60 * 60 * 1000);

            useMaterialStore().setPropertyList(res.data.rows);
          } else if (res.code === 510) {
            this.$message.warning(res.data);
          } else {
            this.$message.warning(res.data.message);
          }
          this.loading = false;
          this.onClearSelected();
        });
      },
    },
  };
</script>

<style scoped>
  @import '~@assets/less/common.less';
</style>
