<template>
  <!-- 查询区域 -->
  <a-form class="smart-query-form" layout="inline" @submit.prevent="searchQuery">
    <a-row class="smart-query-form-row">
      <a-form-item label="属性名" class="smart-query-form-item">
        <a-input placeholder="请输入属性名查询" v-model:value="queryParam.attributeName" />
      </a-form-item>

      <a-form-item label="属性值" class="smart-query-form-item">
        <a-input placeholder="请输入属性值查询" v-model:value="queryParam.attributeValue" />
      </a-form-item>

      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="searchQuery">
          <template #icon>
            <ReloadOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="searchReset" class="smart-margin-left10">
          <template #icon>
            <SearchOutlined />
          </template>
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
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.GOODS_ATTRIBUTE" :refresh="loadData" />
      </div>
    </a-row>

    <!-- table区域 -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </template>

          <template v-else-if="column.dataIndex === 'attributeValue'">
            <a-tag v-for="(item, index) in getTagArr(record.attributeValue)" :key="index" color="blue">
              {{ item }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 表单区域 -->
    <material-attribute-modal ref="modalForm" @ok="modalFormOk"></material-attribute-modal>
  </a-card>
</template>

<script setup>
  import { ref, reactive, computed, onMounted } from 'vue';
  import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
  import MaterialAttributeModal from '/@/views/business/material/modules/MaterialAttributeModal.vue';
  import { useJeecgList } from '/@/jeecg/list';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import TableOperator from '/@/components/support/table-operator/index.vue';

  const modalForm = ref(null);
  // 使用JeecgList组合式API
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
    handleAdd,
  } = useJeecgList({
    url: {
      list: '/materialAttribute/list',
      delete: '/materialAttribute/delete',
      deleteBatch: '/materialAttribute/deleteBatch',
    },
    columns: [
      {
        title: '#',
        dataIndex: '',
        key: 'rowIndex',
        width: 40,
        align: 'center',
        customRender: ({ index }) => index + 1,
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: 100,
        align: 'center',
      },
      {
        title: '属性名',
        dataIndex: 'attributeName',
        width: 150,
      },
      {
        title: '属性值',
        dataIndex: 'attributeValue',
        width: 750,
      },
    ],
    modalFormRef: modalForm, // 传入引用
  });

  // 初始化加载数据
  onMounted(() => {
    loadData(1);
  });

  // 修改 getTagArr 方法
  const getTagArr = (attributeValue) => {
    if (!attributeValue) return ['-']; // 空值显示占位符
    return attributeValue.split('|');
  };

  // 编辑方法
  const handleEdit = (record) => {
    if (modalForm.value) {
      try {
        modalForm.value.edit(record);
        modalForm.value.title = '编辑';
        modalForm.value.disableSubmit = false;

        if (btnEnableList.value.indexOf(1) === -1) {
          modalForm.value.isReadOnly = true;
        }
        console.info('调用 List 方法:');
      } catch (error) {
        console.error('调用 modalForm 方法失败:', error);
      }
    }
  };
</script>

<style scoped>
  @import '/src/assets/less/common.less';
</style>
