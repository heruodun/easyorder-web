<template>
  <a-form class="smart-query-form" layout="inline" @submit.prevent="searchQuery">
    <a-row class="smart-query-form-row">
      <a-form-item label="单位名称" class="smart-query-form-item">
        <a-input placeholder="请输入单位名称查询" v-model:value="queryParam.name" @keyup.enter="searchQuery" />
      </a-form-item>

      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="searchQuery">
          <SearchOutlined />
          查询
        </a-button>
        <a-button @click="searchReset" class="smart-margin-left10">
          <ReloadOutlined />
          重置
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true">
    <!-- 操作按钮区域 -->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="handleAdd" type="primary">
          <PlusOutlined />
          新增
        </a-button>
        <a-button @click="batchDel" danger>
          <template #icon><DeleteOutlined /></template>
          删除
        </a-button>

        <a-button @click="batchSetStatus(true)">
          <template #icon><CheckOutlined /></template>
          启用
        </a-button>
        <a-button @click="batchSetStatus(false)">
          <template #icon><CloseOutlined /></template>
          禁用
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.GOODS_UNIT" :refresh="loadData" />
      </div>
    </a-row>

    <!-- table区域 -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        row-key="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :scroll="scroll"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'rowIndex'">
            {{ index + 1 }}
          </template>

          <template v-else-if="column.dataIndex === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />

            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </template>

          <template v-else-if="column.dataIndex === 'enabled'">
            <a-tag v-if="record.enabled" color="green">启用</a-tag>
            <a-tag v-else color="orange">禁用</a-tag>
          </template>

          <template v-else-if="column.dataIndex === 'otherUnit'">
            <span v-if="record.otherUnit"> {{ record.otherUnit }}={{ record.ratio }}{{ record.basicUnit }} </span>
          </template>

          <template v-else-if="column.dataIndex === 'otherUnitTwo'">
            <span v-if="record.otherUnitTwo"> {{ record.otherUnitTwo }}={{ record.ratioTwo }}{{ record.basicUnit }} </span>
          </template>

          <template v-else-if="column.dataIndex === 'otherUnitThree'">
            <span v-if="record.otherUnitThree"> {{ record.otherUnitThree }}={{ record.ratioThree }}{{ record.basicUnit }} </span>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 表单区域 -->
    <material-unit-modal ref="modalForm" @ok="modalFormOk" />
  </a-card>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { PlusOutlined, DeleteOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons-vue';
  import MaterialUnitModal from './modules/MaterialUnitModal.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { useJeecgList } from '/@/jeecg/list';

  const modalForm = ref(null);

  // 列表相关配置
  const {
    cardStyle,
    queryParam,
    dataSource,
    columns,
    ipagination,
    loading,
    selectedRowKeys,
    btnEnableList,
    loadData,
    searchQuery,
    searchReset,
    onSelectChange,
    handleTableChange,
    batchDel,
    handleDelete,
    modalFormOk,
    batchSetStatus,
    scroll,
  } = useJeecgList({
    url: {
      list: '/unit/list',
      delete: '/unit/delete',
      deleteBatch: '/unit/deleteBatch',
      batchSetStatusUrl: '/unit/batchSetStatus',
    },
    columns: reactive([
      {
        title: '#',
        dataIndex: 'rowIndex',
        width: 40,
        align: 'center',
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: 100,
        align: 'center',
      },
      {
        title: '单位名称',
        align: 'left',
        dataIndex: 'name',
        width: 200,
      },
      {
        title: '基本单位',
        align: 'left',
        dataIndex: 'basicUnit',
        width: 80,
      },
      {
        title: '副单位',
        align: 'left',
        dataIndex: 'otherUnit',
        width: 100,
      },
      {
        title: '副单位2',
        align: 'left',
        dataIndex: 'otherUnitTwo',
        width: 100,
      },
      {
        title: '副单位3',
        align: 'left',
        dataIndex: 'otherUnitThree',
        width: 100,
      },
      {
        title: '状态',
        dataIndex: 'enabled',
        width: 60,
        align: 'center',
      },
    ]),
    modalFormRef: modalForm,
  });

  // 布局配置
  const labelCol = reactive({ span: 5 });
  const wrapperCol = reactive({
    span: 18,
    offset: 1,
  });

  // 初始化加载
  onMounted(() => {
    loadData();
  });

  // 新增处理
  const handleAdd = () => {
    modalForm.value?.add();
    modalForm.value.title = '新增';
    modalForm.value.disableSubmit = false;
  };

  // 编辑处理
  const handleEdit = (record) => {
    modalForm.value?.edit(record);
    modalForm.value.title = '编辑';
    modalForm.value.disableSubmit = false;

    if (btnEnableList.value.indexOf(1) === -1) {
      modalForm.value.isReadOnly = true;
    }
  };
</script>

<style scoped>
  @import '/src/assets/less/common.less';
</style>
