<template>
  <j-modal
    :title="title"
    :width="modelStyle.width"
    :visible="visible"
    :bodyStyle="bodyStyle"
    :switchFullscreen="switchFullscreen"
    @cancel="handleCancel"
    style="top: 15%; height: 60%"
  >
    <template v-slot:footer>
      <a-button key="back" @click="handleCancel">关闭</a-button>
      <a-button v-if="record.openType === 'url'" type="primary" @click="toHandle">去处理</a-button>
    </template>
    <a-card class="daily-article" :loading="loading">
      <span style="font-size: 18px">{{ record.msgTitle }}</span>
      <span style="font-size: 14px; padding-left: 30px; color: grey">通知日期：{{ record.createTimeStr }}</span>
      <a-divider />
      <span v-html="record.msgContent" class="article-content"></span>
    </a-card>
  </j-modal>
</template>

<script>
  import JModal from '/@/components/jeecg/JModal/index.vue';
  export default {
    name: 'SysAnnouncementModal',
    components: {},
    data() {
      return {
        title: '通知消息',
        record: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        visible: false,
        switchFullscreen: true,
        loading: false,
        bodyStyle: {
          padding: '0',
          height: window.innerHeight * 0.6 + 'px',
          'overflow-y': 'auto',
        },
        modelStyle: {
          width: '60%',
          style: { top: '20px' },
          fullScreen: false,
        },
      };
    },
    created() {},
    methods: {
      detail(record) {
        this.visible = true;
        this.record = record;
      },
      handleCancel() {
        this.visible = false;
      },
      /** 切换全屏显示 */
      handleClickToggleFullScreen() {
        let mode = !this.modelStyle.fullScreen;
        if (mode) {
          this.modelStyle.width = '100%';
          this.modelStyle.style.top = '20px';
        } else {
          this.modelStyle.width = '60%';
          this.modelStyle.style.top = '50px';
        }
        this.modelStyle.fullScreen = mode;
      },
      toHandle() {
        if (this.record.openType === 'url') {
          this.visible = false;
          //链接跳转
          this.$router.push({ path: this.record.openPage });
        }
      },
    },
  };
</script>

<style lang="less">
  .announcementCustomModal {
    .ant-modal-header {
      border: none;
      display: inline-block;
      position: absolute;
      z-index: 1;
      right: 56px;
      padding: 0;
      .ant-modal-title {
        .custom-btn {
          width: 56px;
          height: 56px;
          border: none;
          box-shadow: none;
        }
      }
    }
    .daily-article {
      border-bottom: 0;
    }
  }
</style>

<style lang="less" scoped>
  .daily-article {
    .article-button {
      font-size: 1.2rem !important;
    }
    .ant-card-body {
      padding: 18px !important;
    }
    .ant-card-head {
      padding: 0 1rem;
    }
    .ant-card-meta {
      margin-bottom: 1rem;
    }
    .article-content {
      p {
        word-wrap: break-word;
        word-break: break-all;
        text-overflow: initial;
        white-space: normal;
        font-size: 0.9rem !important;
        margin-bottom: 0.8rem;
      }
    }
  }
</style>
