<!--
  * 库存  管理列表
  * 
-->

<template>
  <!-- 库存汇总 -->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="规格" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="summaryQueryForm.guige" placeholder="请输入完整规格" />
      </a-form-item>

      <a-form-item label="类型" class="smart-query-form-item">
        <SmartEnumSelect width="120px" v-model:value="summaryQueryForm.type" placeholder="请选择类型" enum-name="INVENTORY_TYPE_ENUM" />
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button-group>
          <a-button type="primary" @click="onSearchSummary" class="smart-margin-right10">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
          <a-button @click="onReloadSummary">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-button-group>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false">
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <span style="color: yellowgreen; font-weight: bold">库存汇总</span>
      </div>

      <div class="smart-table-setting-block">
        <TableOperator v-model="summaryTableColumns" :tableId="TABLE_ID_CONST.BUSINESS.INVENTORY.SUMMARY" :refresh="queryInventorySummary" />
      </div>
    </a-row>

    <a-table
      rowKey="id"
      :columns="summaryTableColumns"
      :dataSource="summaryTableData"
      :scroll="{ x: 1000 }"
      :pagination="false"
      :loading="summaryTableLoading"
      size="small"
      bordered
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'type'">
          <a-tag
            :color="
              record.type === 2
                ? 'green'
                : record.type === 3
                ? 'brown'
                : record.type === 4
                ? 'red'
                : record.type === 5
                ? 'purple'
                : record.type === 6
                ? 'indigo'
                : record.type === 7
                ? 'blue'
                : 'black'
            "
          >
            <template #icon>
              <component :is="getIconByType(record.type)" />
            </template>

            {{ $smartEnumPlugin.getDescByValue('INVENTORY_TYPE_ENUM', text) }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'guige'">
          <span style="color: green; font-weight: bold">{{ record.guige }}</span>
        </template>

        <template v-else-if="column.dataIndex === 'content'">
          <span style="color: red; font-weight: bold">{{ record.totalCount }}</span>
          {{ record.danwei }}
        </template>
      </template>
    </a-table>

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="summaryQueryForm.pageSize"
        v-model:current="summaryQueryForm.pageNum"
        v-model:pageSize="summaryQueryForm.pageSize"
        :total="summaryTotal"
        @change="queryInventorySummary"
        @showSizeChange="queryInventorySummary"
        :show-total="(total) => `共${total}条`"
      />
    </div>
  </a-card>

  <!-- 库存明细 -->

  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="规格" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="detailQueryForm.guige" placeholder="请输入完整规格" />
      </a-form-item>

      <a-form-item label="类型" class="smart-query-form-item">
        <SmartEnumSelect width="120px" v-model:value="detailQueryForm.type" placeholder="请选择类型" enum-name="INVENTORY_TYPE_ENUM" />
      </a-form-item>

      <a-form-item label="入库时间" class="smart-query-form-item">
        <a-range-picker v-model:value="createDate" :presets="defaultTimeRanges" @change="createDateChange" style="width: 250px" />
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button-group>
          <a-button type="primary" @click="onSearchDetail" class="smart-margin-right10">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
          <a-button @click="onReloadDetail">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-button-group>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false">
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <!-- <a-button type="primary" size="small" @click="addOrUpdate()" v-privilege="'oa:notice:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          新建
        </a-button> -->
        <span style="color: gray; font-weight: bold">库存明细</span>
      </div>

      <div class="smart-table-setting-block">
        <TableOperator v-model="tableColumns" :tableId="TABLE_ID_CONST.BUSINESS.INVENTORY.LIST" :refresh="queryInventoryList" />
      </div>
    </a-row>

    <a-table
      rowKey="orderId"
      :columns="tableColumns"
      :dataSource="tableData"
      :scroll="{ x: 1510 }"
      :pagination="false"
      :loading="tableLoading"
      size="small"
      bordered
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'orderId'">
          <a @click="toDetail(record.orderId)">{{ text }}</a>
        </template>

        <template v-else-if="column.dataIndex === 'type'">
          <a-tag
            :color="
              record.type === 2
                ? 'green'
                : record.type === 3
                ? 'brown'
                : record.type === 4
                ? 'red'
                : record.type === 5
                ? 'purple'
                : record.type === 6
                ? 'indigo'
                : record.type === 7
                ? 'blue'
                : 'black'
            "
          >
            <template #icon>
              <component :is="getIconByType(record.type)" />
            </template>

            {{ $smartEnumPlugin.getDescByValue('INVENTORY_TYPE_ENUM', text) }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="record.status === 100 ? 'red' : record.status === 200 ? 'green' : 'black'">
            {{ record.status === 100 ? '入库' : record.status === 200 ? '出库' : '异常' }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'guige'">
          <span style="color: green; font-weight: bold">{{ record.guige }}</span>
        </template>

        <template v-else-if="column.dataIndex === 'content'">
          <span style="color: red; font-weight: bold">{{ record.count }}</span>
          {{ record.danwei }}
        </template>

        <template v-else-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button type="link" @click="addOrUpdate(record.order)" v-privilege="'oa:notice:update'">编辑</a-button>
            <a-button type="link" @click="onDelete(record.orderId)" v-privilege="'oa:notice:delete'" danger>删除</a-button>
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
        :defaultPageSize="detailQueryForm.pageSize"
        v-model:current="detailQueryForm.pageNum"
        v-model:pageSize="detailQueryForm.pageSize"
        :total="detailTotal"
        @change="queryInventoryList"
        @showSizeChange="queryInventoryList"
        :show-total="(total) => `共${total}条`"
      />
    </div>
  </a-card>
</template>

<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { useRouter } from 'vue-router';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import SmartBooleanSelect from '/@/components/framework/boolean-select/index.vue';
  import { inventoryApi } from '/@/api/business/inventory/inventory-api';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import { INVENTORY_TYPE_ENUM } from '/@/constants/business/inventory/inventory-const';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import { RestOutlined, AppstoreOutlined, ChromeOutlined, NodeIndexOutlined, SendOutlined } from '@ant-design/icons-vue'; // 导入你所需要的图标

  // 为汇总表格和明细表格创建独立的分页状态
  const summaryQueryState = {
    type: undefined, //分类
    guige: undefined, //地址关键词
    status: undefined, //状态
    deletedFlag: undefined, //删除标识
    createTimeBegin: null, //创建-开始时间
    createTimeEnd: null, //创建-截止时间
    pageNum: 1,
    pageSize: PAGE_SIZE,
    // 这里可以加入其他查询参数，保持一致性
  };

  const detailQueryFormState = {
    type: undefined, //分类
    guige: undefined, //地址关键词
    status: undefined, //状态
    deletedFlag: undefined, //删除标识
    createTimeBegin: null, //创建-开始时间
    createTimeEnd: null, //创建-截止时间
    pageNum: 1,
    pageSize: PAGE_SIZE,
    // 这里可以加入其他查询参数，保持一致性
  };

  const summaryQueryForm = reactive({ ...summaryQueryState });
  const detailQueryForm = reactive({ ...detailQueryFormState });

  //dataIndex 和 http response的消息体的key相同
  const tableColumns = ref([
    {
      title: `编号`,
      dataIndex: 'id',
      width: 40,
      ellipsis: true,
    },
    {
      title: `订单编号`,
      dataIndex: 'orderId',
      width: 90,
      ellipsis: true,
    },
    //白桶
    {
      title: `库存类型`,
      dataIndex: 'type',
      width: 60,
      ellipsis: true,
    },
    // A28
    {
      title: `规格`,
      dataIndex: 'guige',
      width: 60,
      ellipsis: true,
    },

    //  1 包
    {
      title: `库存数量`,
      dataIndex: 'content',
      width: 40,
      ellipsis: true,
    },
    {
      title: `备注`,
      dataIndex: 'remark',
      width: 60,
      ellipsis: true,
    },
    // 入库
    {
      title: `出入库状态`,
      dataIndex: 'status',
      width: 50,
      ellipsis: true,
    },

    {
      title: '入库时间',
      dataIndex: 'inTime',
      width: 90,
    },
    {
      title: '入库人',
      dataIndex: 'inMan',
      width: 40,
    },
    {
      title: '出库时间',
      dataIndex: 'outTime',
      width: 90,
    },
    {
      title: '出库人',
      dataIndex: 'outMan',
      width: 40,
    },

    {
      title: '更新时间',
      dataIndex: 'updateTime',
      width: 90,
    },
  ]);

  const summaryTableColumns = ref([
    //白桶
    {
      title: `库存类型`,
      dataIndex: 'type',
      width: 60,
      ellipsis: true,
    },
    // A28
    {
      title: `规格`,
      dataIndex: 'guige',
      width: 60,
      ellipsis: true,
    },

    //  1 包
    {
      title: `库存数量`,
      dataIndex: 'content',
      width: 60,
      ellipsis: true,
    },
  ]);

  function getIconByType(type) {
    switch (type) {
      case 2:
        return RestOutlined;
      case 3:
        return AppstoreOutlined;
      case 4:
        return ChromeOutlined;
      case 5:
        return NodeIndexOutlined; // 替换为自定义图标
      case 6:
        return SendOutlined; // 替换为自定义图标
      case 7:
        return SendOutlined; // 替换为自定义图标
      default:
        return NodeIndexOutlined; // 替换为默认图标
    }
  }
  // ------------------ 查询相关 ------------------

  const summaryTableData = ref([]);
  const tableData = ref([]);
  const summaryTotal = ref(0);
  const detailTotal = ref(0);
  const tableLoading = ref(false);
  const summaryTableLoading = ref(false);

  onMounted(() => {
    queryInventoryList();
    queryInventorySummary();
  });

  // 查询汇总
  async function queryInventorySummary() {
    try {
      summaryTableLoading.value = true;
      const result = await inventoryApi.querySummary(summaryQueryForm);
      summaryTableData.value = result.data.list;
      summaryTotal.value = result.data.total;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      summaryTableLoading.value = false;
    }
  }

  // 查询列表
  async function queryInventoryList() {
    try {
      tableLoading.value = true;
      const result = await inventoryApi.queryPage(detailQueryForm);
      tableData.value = result.data.list;
      detailTotal.value = result.data.total;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      tableLoading.value = false;
    }
  }

  // 点击查询
  function onSearchDetail() {
    detailQueryForm.pageNum = 1;
    queryInventoryList();
  }

  // 点击重置
  function onReloadDetail() {
    Object.assign(detailQueryForm, detailQueryFormState);
    createDate.value = [];
    queryInventoryList();
  }

  // 点击查询
  function onSearchSummary() {
    summaryQueryForm.pageNum = 1;
    queryInventorySummary();
  }

  // 点击重置
  function onReloadSummary() {
    Object.assign(summaryQueryForm, summaryQueryState);
    queryInventorySummary();
  }

  // 入库日期选择
  const createDate = ref([]);
  function createDateChange(dates, dateStrings) {
    detailQueryForm.createTimeBegin = dateStrings[0];
    detailQueryForm.createTimeEnd = dateStrings[1];
  }

  // ------------------ 新建、编辑 ------------------

  // 新建、编辑
  const noticeFormDrawer = ref();
  function addOrUpdate(orderId) {
    noticeFormDrawer.value.showModal(orderId);
  }

  // ------------------ 删除 ------------------

  // 删除
  function onDelete(orderId) {
    Modal.confirm({
      title: '提示',
      content: '确认删除此数据吗?',
      onOk() {
        deleteSalesOrder(orderId);
      },
    });
  }

  // 删除API
  async function deleteSalesOrder(orderId) {
    try {
      tableLoading.value = true;
      await noticeApi.deleteNotice(orderId);
      message.success('删除成功');
      queryInventoryList();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      tableLoading.value = false;
    }
  }

  // ------------------ 详情 ------------------

  // 进入详情
  const router = useRouter();
  function toDetail(orderId) {
    router.push({
      path: '/order/production/order-detail',
      query: { orderId },
    });
  }
</script>

<style lang="less" scoped></style>
