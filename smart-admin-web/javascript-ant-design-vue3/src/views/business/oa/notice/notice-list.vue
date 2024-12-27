<!--
  * 通知  管理列表
  * 
  * @Author:    1024创新实验室-主任：卓大 
  * @Date:      2022-08-21 19:52:43 
  * @Wechat:    zhuda1024 
  * @Email:     lab1024@163.com 
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
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
        <AddressSelect ref="addressSelect" placeholder="请输入地址" width="250px" v-model:value="queryForm.keywords" />
      </a-form-item>

      <!-- <a-form-item label="文号" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.documentNumber" placeholder="文号" />
      </a-form-item>

      <a-form-item label="创建人" class="smart-query-form-item">
        <a-input style="width: 100px" v-model:value="queryForm.createUserId" placeholder="创建人" />
      </a-form-item>

      <a-form-item label="是否删除" class="smart-query-form-item">
        <SmartBooleanSelect v-model:value="queryForm.deletedFlag" style="width: 70px" />
      </a-form-item>

      <a-form-item label="发布时间" class="smart-query-form-item">
        <a-range-picker v-model:value="publishDate" :presets="defaultTimeRanges" @change="publishDateChange" style="width: 220px" />
      </a-form-item>

      <a-form-item label="创建时间" class="smart-query-form-item">
        <a-range-picker v-model:value="createDate" :presets="defaultTimeRanges" @change="createDateChange" style="width: 220px" />
      </a-form-item> -->

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
        <TableOperator v-model="tableColumns" :tableId="TABLE_ID_CONST.BUSINESS.OA.NOTICE" :refresh="queryNoticeList" />
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
        <template v-if="column.dataIndex === 'order_id'">
          <a @click="toDetail(record.order_id)">{{ text }}</a>
        </template>
        <template v-else-if="column.dataIndex === 'allVisibleFlag'"> {{ text ? '全部可见' : '部分可见' }} </template>
        <template v-else-if="column.dataIndex === 'publishFlag'">
          {{ text ? '已发布' : '待发布' }}
        </template>

        <template v-else-if="column.dataIndex === 'cur_status'">
          <a-tag
            :color="
              record.cur_status === '打单'
                ? 'red'
                : record.cur_status === '对接'
                ? 'purple'
                : record.cur_status === '配货'
                ? 'yellow'
                : record.cur_status === '拣货'
                ? 'blue'
                : record.cur_status === '送货'
                ? 'green'
                : 'black'
            "
          >
            {{ record.cur_status }}
          </a-tag>
        </template>

        <template v-else-if="column.dataIndex === 'order_trace'">
          <div v-for="(item, index) in record.order_trace_arr" :key="index">
            <a-tag
              :color="
                item.cur_status === '打单'
                  ? 'red'
                  : item.cur_status === '对接'
                  ? 'purple'
                  : item.cur_status === '配货'
                  ? 'yellow'
                  : item.cur_status === '拣货'
                  ? 'blue'
                  : item.cur_status === '送货'
                  ? 'green'
                  : 'black'
              "
            >
              {{ item.cur_status }}
            </a-tag>

            {{ item.person }}: {{ item.time }}
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
        @change="queryNoticeList"
        @showSizeChange="queryNoticeList"
        :show-total="(total) => `共${total}条`"
      />
    </div>
  </a-card>

  <NoticeFormDrawer ref="noticeFormDrawer" @reloadList="queryNoticeList" />
</template>

<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { useRouter } from 'vue-router';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import SmartBooleanSelect from '/@/components/framework/boolean-select/index.vue';
  import { noticeApi } from '/@/api/business/oa/notice-api';
  import NoticeFormDrawer from './components/notice-form-drawer.vue';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import AddressSelect from '/@/components/system/address-select/index.vue';

  const queryFormState = {
    noticeTypeId: undefined, //分类
    keywords: undefined, //标题、作者、来源
    documentNumber: '', //文号
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

  const tableColumns = ref([
    {
      title: `编号`,
      dataIndex: 'id',
      width: 60,
      ellipsis: true,
    },
    {
      title: `订单编号`,
      dataIndex: 'order_id',
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
      dataIndex: 'content',
      width: 200,
      ellipsis: true,
    },
    {
      title: `当前状态`,
      dataIndex: 'cur_status',
      width: 60,
      ellipsis: true,
    },

    {
      title: '当前处理人',
      dataIndex: 'cur_man',
      width: 70,
      ellipsis: true,
    },
    {
      title: '当前处理时间',
      dataIndex: 'cur_time',
      width: 90,
    },
    {
      title: '波次编号',
      dataIndex: 'wave_id',
      width: 80,
    },
    {
      title: '订单轨迹',
      dataIndex: 'order_trace',
      width: 230,
    },

    {
      title: '打单时间',
      dataIndex: 'print_time',
      width: 90,
    },
    {
      title: '打单人',
      dataIndex: 'printer',
      width: 70,
    },

    {
      title: '更新时间',
      dataIndex: 'update_time',
      width: 90,
    },
  ]);

  // ------------------ 通知分类 ------------------

  // 查询分类列表
  const noticeTypeList = ref([]);
  async function queryNoticeTypeList() {
    try {
      const result = await noticeApi.getAllNoticeTypeList();
      noticeTypeList.value = result.data;
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  // ------------------ 查询相关 ------------------

  const tableData = ref([]);
  const total = ref(0);
  const tableLoading = ref(false);

  onMounted(() => {
    queryNoticeTypeList();
    queryNoticeList();
  });

  // 查询列表
  async function queryNoticeList() {
    try {
      tableLoading.value = true;
      const result = await noticeApi.queryNotice(queryForm);
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
    queryNoticeList();
  }

  // 点击重置
  function onReload() {
    Object.assign(queryForm, queryFormState);
    publishDate.value = [];
    createDate.value = [];
    queryNoticeList();
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
        deleteNotice(orderId);
      },
    });
  }

  // 删除API
  async function deleteNotice(orderId) {
    try {
      tableLoading.value = true;
      await noticeApi.deleteNotice(orderId);
      message.success('删除成功');
      queryNoticeList();
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
      path: '/oa/notice/notice-detail',
      query: { orderId },
    });
  }
</script>

<style lang="less" scoped></style>
