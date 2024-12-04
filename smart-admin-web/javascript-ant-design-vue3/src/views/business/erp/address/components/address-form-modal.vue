<!--
  * 地址表单
-->
<template>
  <a-drawer :title="form.addressId ? '编辑' : '添加'" :width="500" :open="visible" :body-style="{ paddingBottom: '80px' }" @close="onClose">
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }">
     
      <a-form-item label="地址" name="place">
        <a-input v-model:value="form.place" placeholder="请输入地址名称" />
      </a-form-item>

      <a-form-item label="价格" name="price">
        <a-input style="width: 100%" placeholder="请输入商品价格" v-model:value="form.price" />
      </a-form-item>

      <a-form-item label="备注" name="remark">
        <a-input style="width: 100%" placeholder="请输入备注" v-model:value="form.remark" />
      </a-form-item>
    </a-form>
    <div
      :style="{
        position: 'absolute',
        right: 0,
        bottom: 0,
        width: '100%',
        borderTop: '1px solid #e9e9e9',
        padding: '10px 16px',
        background: '#fff',
        textAlign: 'right',
        zIndex: 1,
      }"
    >
      <a-button style="margin-right: 8px" @click="onClose">取消</a-button>
      <a-button type="primary" @click="onSubmit">提交</a-button>
    </div>
  </a-drawer>
</template>
<script setup>
  import { ref, nextTick, reactive } from 'vue';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import _ from 'lodash';
  import { addressApi } from '/@/api/business/address/address-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import DictSelect from '/@/components/support/dict-select/index.vue';

  // emit
  const emit = defineEmits(['reloadList']);

  // 组件ref
  const formRef = ref();

  const formDefault = {
    //地址
    place: undefined,
    //价格
    price: undefined,
    //备注
    remark: '',
    //地址id
    addressId: undefined,
  };
  let form = reactive({ ...formDefault });
  const rules = {
    place: [{ required: true, message: '地址不能为空' }],
  };
  // 是否展示抽屉
  const visible = ref(false);

  function showDrawer(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    if (form.place && form.place.length > 0) {
      form.place = form.place[0].valueCode;
    }
    console.log(form);
    visible.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visible.value = false;
  }

  function onSubmit() {
    formRef.value
      .validate()
      .then(async () => {
        SmartLoading.show();
        try {
          let params = _.cloneDeep(form);
          if (params.place && Array.isArray(params.place) && params.place.length > 0) {
            params.place = params.place[0].valueCode;
          }
          if (form.addressId) {
            await addressApi.updateAddress(params);
          } else {
            await addressApi.addAddress(params);
          }
          message.success(`${form.addressId ? '修改' : '添加'}成功`);
          onClose();
          emit('reloadList');
        } catch (error) {
          smartSentry.captureError(error);
        } finally {
          SmartLoading.hide();
        }
      })
      .catch((error) => {
        console.log('error', error);
        message.error('参数验证错误，请仔细填写表单数据!');
      });
  }

  defineExpose({
    showDrawer,
  });
</script>
