<!--
  * 订单  详情
  * 
-->
<template>
  <a-card style="margin-bottom: 15px; padding: 15px" :title="orderDetail.id">
    <template #extra v-if="orderDetail.deletedFlag === false">
      <a-button type="primary" size="big" @click="onDelete(orderDetail.id)" v-privilege="'oa:notice:delete'" danger> 删除 </a-button>
    </template>

    <template #extra v-if="orderDetail.deletedFlag === true">
      <span style="color: red"> 已删除 </span>
    </template>

    <a-descriptions :column="2" size="middle" bordered>
      <a-descriptions-item label="订单编号" span="1">
        {{ orderDetail.orderId }}
      </a-descriptions-item>

      <a-descriptions-item label="地址" span="2">{{ orderDetail.address }}</a-descriptions-item>
      <a-descriptions-item v-if="orderDetail.guiges && orderDetail.guiges.length > 0" label="货物" span="2">
        <div v-for="(item, index) in orderDetail.guiges" :key="index">
          <span style="color: green; font-weight: bold">{{ item.guige }}</span>
          ：
          <span style="color: red; font-weight: bold">{{ item.count }}</span>
          {{ item.danwei }}
          <div v-if="item.tiaos && item.tiaos.length > 0">{{ item.tiaos.map((element) => `${element.length} x ${element.count}`).join(', ') }}</div>
        </div>
        <div>备注：{{ orderDetail.remark }}</div>
      </a-descriptions-item>
      <a-descriptions-item label="当前状态">
        <a-tag
          :color="
            orderDetail.curStatus === '打单'
              ? 'red'
              : orderDetail.curStatus === '对接'
              ? 'purple'
              : orderDetail.curStatus === '配货'
              ? 'yellow'
              : orderDetail.curStatus === '拣货'
              ? 'blue'
              : orderDetail.curStatus === '送货'
              ? 'green'
              : 'black'
          "
        >
          {{ orderDetail.curStatus }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="当前处理时间">{{ orderDetail.curTime }}</a-descriptions-item>
      <a-descriptions-item label="当前处理人">{{ orderDetail.curOperator }}</a-descriptions-item>
      <a-descriptions-item label="订单轨迹" span="2">
        <div v-for="(item, index) in orderDetail.trace" :key="index">
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

          {{ item.operator }} &nbsp; &nbsp; {{ item.time }} &nbsp; {{ item.detail }}
        </div>
      </a-descriptions-item>
      <a-descriptions-item label="打单人">{{ orderDetail.creator }}</a-descriptions-item>
      <a-descriptions-item label="打单时间">{{ orderDetail.createTime }}</a-descriptions-item>
      <a-descriptions-item label="波次编号">{{ orderDetail.waveId }}</a-descriptions-item>
      <a-descriptions-item label="更新时间">{{ orderDetail.updateTime }}</a-descriptions-item>
    </a-descriptions>
    <a-qrcode :value="orderDetail.orderId + '$xiaowangniujin'" />
  </a-card>

  <!-- 预览附件 -->
  <!-- <FilePreviewModal ref="filePreviewRef" /> -->
</template>

<script setup>
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { orderApi } from '/@/api/business/order/order-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import FilePreviewModal from '/@/components/support/file-preview-modal/index.vue';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { message, Modal } from 'ant-design-vue';

  const route = useRoute();

  const props = defineProps({
    newsType: {
      type: Number,
    },
  });

  const activeKey = ref(1);

  const orderDetail = ref({});

  onMounted(() => {
    if (route.query.id) {
      queryOrderDetail();
    }
  });

  // 查询详情
  async function queryOrderDetail() {
    try {
      SmartLoading.show();
      const result = await orderApi.querySalesById(route.query.id);
      orderDetail.value = result.data;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  // 点击编辑
  const noticeFormDrawerRef = ref();
  function onEdit() {
    noticeFormDrawerRef.value.showModal(orderDetail.value.orderId);
  }

  // 删除
  function onDelete(id) {
    Modal.confirm({
      title: '提示',
      content: '确认删除此数据吗?',
      onOk() {
        deleteOrder(id);
      },
    });
  }

  // 删除API
  async function deleteOrder(id) {
    try {
      await orderApi.deleteSales(id);
      message.success('删除成功');
      queryOrderDetail();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
    }
  }

  // 预览附件
  const filePreviewRef = ref();
  function onPrevFile(fileItem) {
    filePreviewRef.value.showPreview(fileItem);
  }
</script>

<style lang="less" scoped>
  :deep(.ant-descriptions-item-content) {
    flex: 1;
    overflow: hidden;
  }
  .file-list {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    .file-item {
      display: block;
      margin-right: 10px;
    }
  }
  .visible-list {
    display: flex;
    flex-wrap: wrap;
    .visible-item {
      margin-right: 10px;
      color: #666;
    }
  }
  .content-html {
    img {
      max-width: 100%;
    }
  }
</style>
