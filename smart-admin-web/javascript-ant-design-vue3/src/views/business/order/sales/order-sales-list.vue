<!--
  * 销售订单  管理列表
  * 
-->

<template>
  <a-form class="smart-query-form" v-privilege="'oa:notice:query'">
    <a-row class="smart-query-form-row">
      <!-- <a-form-item label="分类" class="smart-query-form-item">
        <a-select v-model:value="queryForm.noticeTypeId" style="width: 100px" :showSearch="true" :allowClear="true" placeholder="分类">
          <a-select-option v-for="item in noticeTypeList" :key="item.noticeTypeId" :value="item.noticeTypeId">
            {{ item.noticeTypeName }}
          </a-select-option>
        </a-select>
      </a-form-item> -->

      <!-- <a-form-item label="地址" class="smart-query-form-item">
        <a-input style="width: 300px" v-model:value="queryForm.keywords" placeholder="请输入地址关键词" />
      </a-form-item> -->

      <a-form-item name="address" label="地址" class="smart-query-form-item">
        <AddressSelect ref="addressSelect" placeholder="请输入地址" width="250px" v-model:value="queryForm.address" />
      </a-form-item>

      <a-form-item label="规格" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.guige" placeholder="请输入完整规格" />
      </a-form-item>
      <!--
      <a-form-item label="创建人" class="smart-query-form-item">
        <a-input style="width: 100px" v-model:value="queryForm.createUserId" placeholder="创建人" />
      </a-form-item>

      <a-form-item label="是否删除" class="smart-query-form-item">
        <SmartBooleanSelect v-model:value="queryForm.deletedFlag" style="width: 70px" />
      </a-form-item>

      <a-form-item label="发布时间" class="smart-query-form-item">
        <a-range-picker v-model:value="publishDate" :presets="defaultTimeRanges" @change="publishDateChange" style="width: 220px" />
      </a-form-item>-->

      <a-form-item label="打单时间" class="smart-query-form-item">
        <a-range-picker v-model:value="createDate" :presets="defaultTimeRanges" @change="createDateChange" style="width: 250px" />
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button-group>
          <a-button type="primary" @click="onSearch" class="smart-margin-right10">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
          <a-button @click="onReload">
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
        <a-button type="primary" size="small" @click="addOrUpdate()" v-privilege="'oa:notice:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          新建
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="tableColumns" :tableId="TABLE_ID_CONST.BUSINESS.ORDER.SALES" :refresh="queryOrderSalesList" />
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
      :rowClassName="(record) => (record.deletedFlag ? 'row-deleted' : '')"
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'orderId'">
          <a @click="toDetail(record.id)">{{ text }}</a>
        </template>
        <template v-else-if="column.dataIndex === 'allVisibleFlag'"> {{ text ? '全部可见' : '部分可见' }} </template>
        <template v-else-if="column.dataIndex === 'publishFlag'">
          {{ text ? '已发布' : '待发布' }}
        </template>

        <template v-else-if="column.dataIndex === 'curStatus'">
          <a-tag
            :color="
              record.curStatus === '打单'
                ? 'red'
                : record.curStatus === '对接'
                ? 'purple'
                : record.curStatus === '配货'
                ? 'yellow'
                : record.curStatus === '拣货'
                ? 'blue'
                : record.curStatus === '送货'
                ? 'green'
                : 'black'
            "
          >
            {{ record.curStatus }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'guiges'">
          <div v-if="record.guiges && record.guiges.length > 0" v-for="(item, index) in record.guiges" :key="index">
            <span style="color: green; font-weight: bold">{{ item.guige }}</span
            >：
            <span style="color: red; font-weight: bold">{{ item.count }}</span>
            {{ item.danwei }}

            <a-tooltip
              v-if="item.tiaos && item.tiaos.length > 0"
              :title="item.tiaos.map((element) => `${element.length} x ${element.count}`).join('，')"
              color=""
            >
              <div>
                {{ item.tiaos.map((element) => `${element.length} x ${element.count}`).join('，') }}
              </div>
            </a-tooltip>
          </div>

          <span v-if="record.remark !== ''" style="color: grey; font-style: italic">备注：{{ record.remark }}</span>
        </template>

        <template v-else-if="column.dataIndex === 'trace'">
          <div v-for="(item, index) in record.trace" :key="index">
            <a-tag
              :color="
                item.operation === '打单'
                  ? 'red'
                  : item.operation === '对接'
                  ? 'purple'
                  : item.operation === '配货'
                  ? 'yellow'
                  : item.operation === '拣货'
                  ? 'blue'
                  : item.operation === '送货'
                  ? 'green'
                  : 'black'
              "
            >
              {{ item.operation }}
            </a-tag>

            {{ item.operator }}: {{ item.time }} {{ item.detail }}
          </div>
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
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryOrderSalesList"
        @showSizeChange="queryOrderSalesList"
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
  import { orderApi } from '/@/api/business/order/order-api';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import AddressSelect from '/@/components/system/address-select/index.vue';

  const queryFormState = {
    noticeTypeId: undefined, //分类
    address: undefined, //地址关键词
    guige: undefined, //地址关键词
    orderId: undefined, //订单号
    createUserId: undefined, //创建人
    deletedFlag: undefined, //删除标识
    createTimeBegin: null, //创建-开始时间
    createTimeEnd: null, //创建-截止时间
    publishTimeBegin: null, //发布-开始时间
    publishTimeEnd: null, //发布-截止时间
    pageNum: 1,
    pageSize: PAGE_SIZE,
  };
  const queryForm = reactive({ ...queryFormState });

  // 获取 AddressSelect 的引用
  const addressSelect = ref(null);
  // 清空地址方法
  const clearAddress = () => {
    if (addressSelect.value) {
      addressSelect.value.reset(); // 调用 AddressSelect 组件的 reset 方法
    }
  };

  //dataIndex 和 http response的消息体的key相同
  const tableColumns = ref([
    {
      title: `编号`,
      dataIndex: 'id',
      width: 60,
      ellipsis: true,
    },
    {
      title: `订单编号`,
      dataIndex: 'orderId',
      width: 130,
      ellipsis: true,
    },
    {
      title: '地址',
      dataIndex: 'address',
      width: 180,
      ellipsis: true,
    },
    {
      title: `货物`,
      dataIndex: 'guiges',
      width: 200,
      ellipsis: true,
    },
    {
      title: `当前状态`,
      dataIndex: 'curStatus',
      width: 60,
      ellipsis: true,
    },

    {
      title: '当前处理人',
      dataIndex: 'curOperator',
      width: 70,
      ellipsis: true,
    },
    {
      title: '当前处理时间',
      dataIndex: 'curTime',
      width: 90,
    },
    {
      title: '波次编号',
      dataIndex: 'waveId',
      width: 50,
    },
    {
      title: '订单轨迹',
      dataIndex: 'trace',
      width: 230,
    },

    {
      title: '打单时间',
      dataIndex: 'createTime',
      width: 90,
    },
    {
      title: '打单人',
      dataIndex: 'creator',
      width: 70,
    },

    {
      title: '更新时间',
      dataIndex: 'updateTime',
      width: 90,
    },
  ]);

  // ------------------ 查询相关 ------------------

  const tableData = ref([]);
  const total = ref(0);
  const tableLoading = ref(false);

  onMounted(() => {
    queryOrderSalesList();
  });

  // 查询列表
  async function queryOrderSalesList() {
    try {
      tableLoading.value = true;
      const result = await orderApi.queryPageSales(queryForm);
      tableData.value = result.data.list;
      total.value = result.data.total;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      tableLoading.value = false;
    }
  }

  // 点击查询
  function onSearch() {
    queryForm.pageNum = 1;
    queryOrderSalesList();
  }

  // 点击重置
  function onReload() {
    Object.assign(queryForm, queryFormState);
    publishDate.value = [];
    createDate.value = [];
    // clearAddress();
    queryOrderSalesList();
  }

  // 发布日期选择
  const publishDate = ref([]);
  function publishDateChange(dates, dateStrings) {
    queryForm.publishTimeBegin = dateStrings[0];
    queryForm.publishTimeEnd = dateStrings[1];
  }
  // 创建日期选择
  const createDate = ref([]);
  function createDateChange(dates, dateStrings) {
    queryForm.createTimeBegin = dateStrings[0];
    queryForm.createTimeEnd = dateStrings[1];
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
      queryOrderSalesList();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      tableLoading.value = false;
    }
  }

  // ------------------ 详情 ------------------

  // 进入详情
  const router = useRouter();
  function toDetail(id) {
    router.push({
      path: '/order/sales/order-detail',
      query: { id },
    });
  }
</script>

<style>
  .row-deleted {
    background-color: #f5f5f5; /* 设置灰色背景 */
    color: #999999; /* 设置字体颜色为灰色 */
    font-style: oblique;
    text-decoration: line-through;
  }
</style>
