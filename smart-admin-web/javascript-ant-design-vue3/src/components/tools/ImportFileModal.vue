<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="500"
      :open="visible"
      :confirm-loading="confirmLoading"
      :getContainer="() => $refs.container"
      :maskStyle="{ top: '93px', left: '154px' }"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @cancel="handleCancel"
      style="top: 20%; height: 55%"
    >
      <template v-slot:footer>
        <a-button key="back" @click="handleCancel">取消</a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-row class="form-row" :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="第一步：">
              <a target="_blank" :href="templateUrl"
                ><b>{{ templateName }}</b></a
              >
              <p>提示：模板中的第一行请勿删除</p>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="第二步：">
              <a-upload
                name="file"
                :showUploadList="false"
                :multiple="false"
                :headers="tokenHeader"
                :action="importExcelUrl"
                @change="handleImportExcel"
              >
                <a-button type="primary" icon="import">导入</a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
  import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer';
  import { JeecgListMixin } from '/@/mixins/JeecgListMixin';
  import { mixinDevice } from '/@/utils/mixin';
  export default {
    name: 'ImportFileModal',
    mixins: [JeecgListMixin, mixinDevice],
    data() {
      return {
        title: '',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
        confirmLoading: false,
        disableMixinCreated: true,
        templateUrl: '',
        templateName: '',
        url: {
          importExcelUrl: '',
        },
      };
    },
    created() {},
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initModal(apiUrl, templateUrl, templateName) {
        this.url.importExcelUrl = apiUrl;
        this.templateUrl = templateUrl;
        this.templateName = templateName;
        this.visible = true;
      },
      close() {
        $emit(this, 'close');
        this.visible = false;
      },
      handleCancel() {
        this.close();
      },
    },
    emits: ['close'],
  };
</script>

<style scoped></style>
