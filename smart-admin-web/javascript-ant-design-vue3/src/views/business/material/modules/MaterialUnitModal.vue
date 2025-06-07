<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="700"
      v-model:open="visible"
      :confirm-loading="confirmLoading"
      :get-container="() => container"
      :mask-style="{ top: '0px', left: '0px' }"
      :wrap-class-name="wrapClassName"
      :mask="isDesktop"
      :mask-closable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancel-text="取消"
      ok-text="保存"
      style="top: 100px; height: 55%"
    >
      <template #footer>
        <a-button v-if="isReadOnly" @click="handleCancel">取消</a-button>
        <template v-else>
          <a-button key="cancel" @click="handleCancel">取消</a-button>
          <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">保存</a-button>
        </template>
      </template>

      <a-spin :spinning="confirmLoading">
        <a-form ref="formRef" :model="formState" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-item label="基本单位" name="basicUnit" :rules="validatorRules.basicUnit">
            <a-input
              v-model:value="formState.basicUnit"
              placeholder="请输入基本单位(小单位)"
              @keydown.enter="autoJumpNextInput('basicUnit', 'otherUnit')"
            />
          </a-form-item>

          <a-form-item label="副单位">
            <div style="display: flex; gap: 8px; width: 100%">
              <a-input
                v-model:value="formState.otherUnit"
                placeholder="请输入副单位(大单位)"
                style="flex: 1"
                @keydown.enter="autoJumpNextInput('otherUnit', 'ratio')"
              />
              <span style="flex: none">=</span>
              <a-input
                v-model:value="formState.ratio"
                placeholder="请输入比例"
                suffix="基本单位"
                style="flex: 1"
                @keydown.enter="autoJumpNextInput('ratio', 'otherUnitTwo')"
              />
            </div>
          </a-form-item>

          <a-form-item label="副单位2">
            <div style="display: flex; gap: 8px; width: 100%">
              <a-input
                v-model:value="formState.otherUnitTwo"
                placeholder="请输入副单位2(大单位)"
                style="flex: 1"
                @keydown.enter="autoJumpNextInput('otherUnitTwo', 'ratioTwo')"
              />
              <span style="flex: none">=</span>
              <a-input
                v-model:value="formState.ratioTwo"
                placeholder="请输入比例2"
                suffix="基本单位"
                style="flex: 1"
                @keydown.enter="autoJumpNextInput('ratioTwo', 'otherUnitThree')"
              />
            </div>
          </a-form-item>

          <a-form-item label="副单位3">
            <div style="display: flex; gap: 8px; width: 100%">
              <a-input
                v-model:value="formState.otherUnitThree"
                placeholder="请输入副单位3(大单位)"
                style="flex: 1"
                @keydown.enter="autoJumpNextInput('otherUnitThree', 'ratioThree')"
              />
              <span style="flex: none">=</span>
              <a-input v-model:value="formState.ratioThree" placeholder="请输入比例3" suffix="基本单位" style="flex: 1" />
            </div>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script setup>
  import { ref, reactive, computed, nextTick } from 'vue';
  import { Modal as AModal, Spin as ASpin, Form as AForm, FormItem as AFormItem, Input as AInput, Button as AButton, message } from 'ant-design-vue';
  import { addUnit, editUnit } from '/@/api/api';
  import { autoJumpNextInput } from '/@/utils/util';
  import { isDecimalThree } from '/@/utils/validate';
  import { useDevice } from '/@/utils/device-util';
  import pick from 'lodash.pick';

  const props = defineProps({
    isReadOnly: Boolean,
  });

  const emit = defineEmits(['close', 'ok']);

  // 设备检测
  const { isDesktop } = useDevice().isDesktop;
  // 设备相关状态
  const device = useDevice();

  // 响应式数据
  const container = ref(null);
  const visible = ref(false);
  const confirmLoading = ref(false);
  const formRef = ref();
  const model = ref({});

  const title = computed(() => (model.value.id ? '编辑单位' : '新建单位'));
  const wrapClassName = computed(() => device.wrapClassNameInfo.value);

  const formState = reactive({
    basicUnit: '',
    otherUnit: '',
    ratio: '',
    otherUnitTwo: '',
    ratioTwo: '',
    otherUnitThree: '',
    ratioThree: '',
  });

  const labelCol = { span: 5 };
  const wrapperCol = { span: 16 };

  const add = () => {
    edit({});
  };
  const edit = (record) => {
    if (formRef.value) {
      formRef.value.resetFields();
    }
    model.value = { ...record };
    visible.value = true;
    nextTick(() => {
      formState.basicUnit = model.value.basicUnit;
      formState.otherUnit = model.value.otherUnit;
      formState.ratio = model.value.ratio;
      formState.otherUnitTwo = model.value.otherUnitTwo;
      formState.ratioTwo = model.value.ratioTwo;
      formState.otherUnitThree = model.value.otherUnitThree;
      formState.ratioThree = model.value.ratioThree;
      console.log('formState' + JSON.stringify(formState));
      autoJumpNextInput('unitModal');
    });
  };

  // 方法
  const validateRatio = (_, value) => {
    if (value && parseFloat(value) > 1) return Promise.resolve();
    return Promise.reject('比例必须大于1');
  };

  const validatorRules = {
    basicUnit: [
      { required: true, message: '请输入基本单位!' },
      { min: 1, max: 10, message: '长度在1到10个字符', trigger: 'blur' },
    ],
    ratio: [{ required: true, message: '请输入比例!' }, { validator: validateRatio }],
  };

  const show = (record) => {
    visible.value = true;
    Object.assign(formState, {
      basicUnit: '',
      otherUnit: '',
      ratio: '',
      otherUnitTwo: '',
      ratioTwo: '',
      otherUnitThree: '',
      ratioThree: '',
    });

    if (record) {
      model.value = { ...record };
      Object.assign(formState, record);
    }

    nextTick(() => {
      if (formRef.value) {
        formRef.value.clearValidate();
      }
    });
  };

  const handleOk = async () => {
    try {
      await formRef.value.validate();

      if (!validateForm()) return;

      confirmLoading.value = true;
      const formData = { ...model.value, ...formState };
      const apiMethod = formData.id ? editUnit : addUnit;

      const res = await apiMethod(formData);
      if (res.code === 200) {
        message.success('操作成功');
        emit('ok');
        close();
      } else {
        message.warning(res.data.message || '操作失败');
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    } finally {
      confirmLoading.value = false;
    }
  };

  const validateForm = () => {
    // 验证副单位链式关系
    if (formState.otherUnitThree && !formState.otherUnitTwo) {
      message.warning('需要先输入副单位2再输入副单位3！');
      return false;
    }

    // 验证单位重复
    const units = [formState.basicUnit, formState.otherUnit, formState.otherUnitTwo, formState.otherUnitThree].filter(Boolean);

    if (new Set(units).size !== units.length) {
      message.warning('基本单位与副单位不能重复！');
      return false;
    }

    // 验证比例格式
    const ratios = [formState.ratio, formState.ratioTwo, formState.ratioThree].filter(Boolean);

    for (const ratio of ratios) {
      if (!isDecimalThree(ratio)) {
        message.warning('比例只能为数字，最多三位小数！');
        return false;
      }
    }

    return true;
  };

  const handleCancel = () => {
    close();
  };

  const close = () => {
    visible.value = false;
    emit('close');
  };

  // 暴露方法给父组件
  defineExpose({ show, add, edit });
</script>

<style scoped>
  /* 保持原有样式 */
  .ant-form-item {
    margin-bottom: 16px;
  }
</style>
