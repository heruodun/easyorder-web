<!--
  * 地址列表
  *
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row" v-privilege="'address:query'">
      <a-form-item label="地址" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.searchWord" placeholder="地址关键词" />
      </a-form-item>

      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="onSearch" v-privilege="'address:query'">
          <template #icon>
            <ReloadOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10" v-privilege="'address:query'">
          <template #icon>
            <SearchOutlined />
          </template>
          重置
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="addAddress" type="primary" size="small" v-privilege="'address:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          新建
        </a-button>

        <a-button @click="confirmBatchDelete" danger size="small" :disabled="selectedRowKeyList.length === 0" v-privilege="'address:batchDelete'">
          <template #icon>
            <DeleteOutlined />
          </template>
          批量删除
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.GOODS" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="addressId"
      bordered
      :pagination="false"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="addAddress(record)" type="link" v-privilege="'address:update'">编辑</a-button>
            <a-button v-if="!record.deletedFlag" @click="deleteAddress(record)" danger type="link" v-privilege="'address:delete'">删除</a-button>
            <a-button v-else @click="recoverAddress(record)" type="link" v-privilege="'address:delete'">恢复</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryData"
        @showSizeChange="queryData"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <GoodsFormModal ref="formModal" @reloadList="queryData" />
  </a-card>
</template>
<script setup>
  import GoodsFormModal from './components/address-form-modal.vue';
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import CategoryTree from '/@/components/business/category-tree-select/index.vue';
  import { CATEGORY_TYPE_ENUM } from '/@/constants/business/erp/category-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import { GOODS_STATUS_ENUM } from '/@/constants/business/erp/goods-const';
  import DictSelect from '/@/components/support/dict-select/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import { FILE_FOLDER_TYPE_ENUM } from '/@/constants/support/file-const.js';
  import FileUpload from '/@/components/support/file-upload/index.vue';
  import { addressApi } from '/@/api/business/address/address-api';

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '编号',
      dataIndex: 'addressId',
    },

    {
      title: '地址',
      dataIndex: 'place',
    },
    {
      title: '价格',
      dataIndex: 'price',
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 150,
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      width: 150,
    },
    {
      title: '是否停用',
      dataIndex: 'deletedFlag',
      customRender: ({ text }) => (text ? '已停用' : '正常'),
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 100,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    searchWord: '',
    pageNum: 1,
    pageSize: 10,
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 表格加载loading
  const tableLoading = ref(false);
  // 表格数据
  const tableData = ref([]);
  // 总数
  const total = ref(0);

  // 重置查询条件
  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, queryFormState);
    queryForm.pageSize = pageSize;
    queryData();
  }

  // 搜索
  function onSearch() {
    queryForm.pageNum = 1;
    queryData();
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await addressApi.queryAddressList(queryForm);

      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(queryData);

  // ---------------------------- 添加/修改 ----------------------------
  const formModal = ref();

  function addAddress(addressData) {
    formModal.value.showDrawer(addressData);
  }
  // ---------------------------- 单个删除 ----------------------------

  function deleteAddress(addressData) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除【' + addressData.addressId + '】吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        singleDelete(addressData);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function singleDelete(addressData) {
    try {
      SmartLoading.show();
      await addressApi.deleteAddress(addressData.addressId);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  function recoverAddress(addressData) {
    Modal.confirm({
      title: '提示',
      content: '确定要恢复【' + addressData.addressId + '】吗?',
      okText: '恢复',
      okType: 'danger',
      onOk() {
        singleRecover(addressData);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function singleRecover(addressData) {
    try {
      SmartLoading.show();
      await addressApi.recoverAddress(addressData.addressId);
      message.success('恢复成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ---------------------------- 批量删除 ----------------------------

  // 选择表格行
  const selectedRowKeyList = ref([]);

  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // 批量删除
  function confirmBatchDelete() {
    Modal.confirm({
      title: '提示',
      content: '确定要删除选中商品吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        batchDelete();
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function batchDelete() {
    try {
      SmartLoading.show();
      await addressApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
</script>
