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
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="请输入价格">
            <a-input placeholder="请输入价格" v-decorator.trim="['price', validatorRules.price]" />
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
    name: 'BatchSetPriceModal',
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
          price: {
            rules: [{ required: true, message: '请输入价格!' }],
          },
        },
      };
    },
    created() {},
    methods: {
      add(type) {
        this.batchType = type;
        if (type === 'purchase') {
          this.title = '采购价-批量设置';
        } else if (type === 'commodity') {
          this.title = '零售价-批量设置';
        } else if (type === 'wholesale') {
          this.title = '销售价-批量设置';
        } else if (type === 'low') {
          this.title = '最低售价-批量设置';
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
        let price = this.form.getFieldValue('price');
        $emit(this, 'ok', price, this.batchType);
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
