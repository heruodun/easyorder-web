<!--
  * 订单  详情
  * 
-->
<template>
  <a-card style="margin-bottom: 15px; padding: 15px;"  :title="noticeDetail.order_id">
      <template #extra>
        <a-button type="primary" size="big" @click="onDelete(noticeDetail.order_id)" v-privilege="'oa:notice:delete'" danger>
          删除
        </a-button>
      </template>

    <a-descriptions :column="2" size="middle" bordered>
      <a-descriptions-item label="地址" span="2">{{ noticeDetail.address }}</a-descriptions-item>
      <a-descriptions-item label="货物" span="2">{{ noticeDetail.content }}</a-descriptions-item>
      <a-descriptions-item label="当前状态">
        <a-tag 
        :color="noticeDetail.cur_status === '打单' ? 'red' : 
                noticeDetail.cur_status === '对接' ? 'purple' :
                noticeDetail.cur_status === '配货' ? 'yellow' :
                noticeDetail.cur_status === '拣货' ? 'blue' :
                noticeDetail.cur_status === '送货' ? 'green' : 'black'">
          {{ noticeDetail.cur_status }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="当前处理时间">{{ noticeDetail.cur_time }}</a-descriptions-item>
      <a-descriptions-item label="订单轨迹" span="2">

        <div v-for="(item, index) in noticeDetail.order_trace_arr" :key="index" >
            <a-tag 
            :color="item.cur_status === '打单' ? 'red' : 
                    item.cur_status === '对接' ? 'purple' :
                    item.cur_status === '配货' ? 'yellow' :
                    item.cur_status === '拣货' ? 'blue' :
                    item.cur_status === '送货' ? 'green' : 'black'">
              {{ item.cur_status }}
            </a-tag>
              
              {{ item.person }}  &nbsp;  &nbsp; {{ item.time }}
            
          </div>
        
      </a-descriptions-item>
      <a-descriptions-item label="打单人">{{ noticeDetail.printer }}</a-descriptions-item>
      <a-descriptions-item label="打单时间">{{ noticeDetail.print_time }}</a-descriptions-item>
      <a-descriptions-item label="波次编号">{{ noticeDetail.wave_id }}</a-descriptions-item>
      <a-descriptions-item label="更新时间">{{ noticeDetail.update_time}}</a-descriptions-item>
     
    </a-descriptions>
  </a-card>


 

  <!-- 预览附件 -->
  <!-- <FilePreviewModal ref="filePreviewRef" /> -->
</template>

<script setup>
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import NoticeFormDrawer from './components/notice-form-drawer.vue';
  import NoticeViewRecordList from './components/notice-view-record-list.vue';
  import { noticeApi } from '/@/api/business/oa/notice-api';
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

  const noticeDetail = ref({});

  onMounted(() => {
    if (route.query.orderId) {
      queryNoticeDetail();
    }
  });


  // 查询详情
  async function queryNoticeDetail() {
    try {
      SmartLoading.show();
      const result = await noticeApi.getUpdateNoticeInfo(route.query.orderId);
      noticeDetail.value = result.data;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  // 点击编辑
  const noticeFormDrawerRef = ref();
  function onEdit() {
    noticeFormDrawerRef.value.showModal(noticeDetail.value.orderId);
  }


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
      queryNoticeDetail();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      tableLoading.value = false;
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
