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
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="取消"
      okText="保存"
      style="top: 30%; height: 30%"
    >
      <template v-slot:footer>
        <a-button v-if="isReadOnly" @click="handleCancel"> 取消 </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="请输入数量">
            <a-input placeholder="请输入数量" v-decorator.trim="['number', validatorRules.number]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
  import { $on, $off, $once, $emit } from '../../../../utils/gogocodeTransfer';
  import { mixinDevice } from '/@/utils/mixin';
  import { Form, Input, Select, Button } from 'ant-design-vue';

  export default {
    name: 'BatchSetStockModal',
    mixins: [mixinDevice],
    data() {
      return {
        title: '批量设置',
        visible: false,
        isReadOnly: false,
        batchType: '',
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        // form: this.$form.createForm(this),
        form: Form.useForm(),
        validatorRules: {
          number: {
            rules: [{ required: true, message: '请输入数量!' }],
          },
        },
      };
    },
    created() {},
    methods: {
      add(type) {
        this.batchType = type;
        if (type === 'initStock') {
          this.title = '期初库存-批量设置';
        } else if (type === 'lowSafeStock') {
          this.title = '最低安全库存-批量设置';
        } else if (type === 'highSafeStock') {
          this.title = '最高安全库存-批量设置';
        }
        this.edit({});
      },
      edit(record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      close() {
        $emit(this, 'close');
        this.visible = false;
      },
      handleOk() {
        let number = this.form.getFieldValue('number');
        $emit(this, 'ok', number, this.batchType);
        this.visible = false;
      },
      handleCancel() {
        this.close();
      },
    },
    emits: ['ok', 'close'],
  };
</script>

<style scoped></style>
