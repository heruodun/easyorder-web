<template>
  <!-- 模板部分保持不变 -->
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
      :open="visible"
      :confirm-loading="confirmLoading"
      :get-container="() => container"
      :mask-style="{ top: '0px', left: '0px' }"
      :wrap-class-name="wrapClassName"
      :mask="isDesktop"
      :mask-closable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      :cancel-text="'取消'"
      :ok-text="'保存'"
      style="top: 100px; height: 100%"
    >
      <a-spin :spinning="confirmLoading">
        <a-form ref="formRef" :model="formState" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-item label="属性名" name="attributeName">
            <a-input placeholder="请输入属性名" v-model:value="formState.attributeName" @blur="validateField('attributeName')" />
          </a-form-item>
          <a-form-item label="属性值">
            <a-tabs v-model:activeKey="activeKey" size="small">
              <a-tab-pane key="1" tab="标签模式" force-render>
                <template v-for="(tag, index) in tags" :key="index">
                  <a-tag color="blue" style="margin-bottom: 10px" :closable="true" @close="() => handleClose(tag)">
                    {{ tag }}
                  </a-tag>
                </template>
                <a-input
                  v-if="inputVisible"
                  ref="inputRef"
                  type="text"
                  size="small"
                  :style="{ width: '150px' }"
                  v-model:value="inputValue"
                  @blur="handleInputConfirm"
                  @keyup.enter="handleInputConfirm"
                />
                <a-tag v-else style="background: #fff" @click="showInput"> <PlusOutlined /> 请输入属性值 </a-tag>
              </a-tab-pane>
              <a-tab-pane key="2" tab="文字模式" force-render>
                <a-textarea :rows="8" placeholder="请输入属性值" v-model:value="formState.attributeValue" @change="handleValueChange" />
                注意：属性值请用竖线隔开，比如：红色|橙色|黄色|绿色
              </a-tab-pane>
            </a-tabs>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script setup>
  import { reactive, ref, computed, watch, nextTick, toRefs } from 'vue';
  import { message } from 'ant-design-vue';
  import { addMaterialAttribute, checkMaterialAttribute, editMaterialAttribute } from '/@/api/api';
  import { useDevice } from '/@/utils/device-util';

  // 定义组件名称
  defineOptions({
    name: 'MaterialAttributeModal',
  });

  // 定义 emit 事件
  const emit = defineEmits(['close', 'ok']);

  // 设备相关状态
  const device = useDevice();

  // DOM 引用
  const container = ref(null);
  const inputRef = ref(null);
  const formRef = ref(null);

  // 响应式状态
  const title = ref('操作');
  const visible = ref(false);
  const model = ref({});
  const isReadOnly = ref(false);
  const activeKey = ref('1');
  const tags = ref([]);
  const inputVisible = ref(false);
  const inputValue = ref('');
  const confirmLoading = ref(false);
  const formState = reactive({
    attributeName: '',
    attributeValue: '',
  });

  // 布局配置
  const labelCol = {
    xs: { span: 24 },
    sm: { span: 5 },
  };

  const wrapperCol = {
    xs: { span: 24 },
    sm: { span: 16 },
  };

  // 计算属性
  const wrapClassName = computed(() => device.wrapClassNameInfo.value);
  const isDesktop = computed(() => device.isDesktop.value);

  // 监听 activeKey 变化
  watch(activeKey, (newVal) => {
    if (newVal === '1') {
      tags.value = formState.attributeValue.split('|').filter((tag) => tag);
    } else {
      formState.attributeValue = tags.value.join('|');
    }
  });

  // 添加方法
  function add() {
    edit({});
  }

  // 编辑方法
  function edit(record) {
    resetForm();
    model.value = { ...record };
    activeKey.value = '1';
    visible.value = true;
    console.info('调用 modalForm 方法:' + JSON.stringify(record));
    if (model.value.attributeValue) {
      tags.value = model.value.attributeValue.split('|').filter((tag) => tag);
    } else {
      tags.value = [];
    }

    nextTick(() => {
      formState.attributeName = model.value.attributeName || '';
      formState.attributeValue = tags.value.join('|') || '';
    });
  }

  // 关闭模态框
  function close() {
    visible.value = false;
    emit('close');
  }

  // 处理确认
  async function handleOk() {
    try {
      await formRef.value.validateFields();
      confirmLoading.value = true;

      const formData = {
        ...model.value,
        ...formState,
      };

      const action = model.value.id ? editMaterialAttribute : addMaterialAttribute;
      const res = await action(formData);

      if (res.code === 200) {
        message.success('操作成功');
        emit('ok');
      } else {
        message.warning(res.data.message || res.message);
      }
    } catch (error) {
      console.error('验证错误:', error);
    } finally {
      confirmLoading.value = false;
      close();
    }
  }

  // 处理取消
  function handleCancel() {
    close();
  }

  // 验证字段
  async function validateField(fieldName) {
    try {
      await formRef.value.validateFields([fieldName]);
    } catch (error) {
      console.log('验证失败:', error);
    }
  }

  // 验证属性名
  async function validateAttributeName(rule, value) {
    if (!value) return Promise.reject();

    const params = {
      name: value,
      id: model.value.id || 0,
    };

    try {
      const res = await checkMaterialAttribute(params);
      if (res && res.code === 200) {
        return res.data.status ? Promise.reject('名称已经存在') : Promise.resolve();
      } else {
        return Promise.reject(res.data || '验证失败');
      }
    } catch (error) {
      return Promise.reject('验证异常');
    }
  }

  // 处理属性值变更
  function handleValueChange() {
    tags.value = formState.attributeValue.split('|').filter((tag) => tag);
  }

  // 删除标签
  function handleClose(removedTag) {
    tags.value = tags.value.filter((tag) => tag !== removedTag);
    formState.attributeValue = tags.value.join('|');
  }

  // 显示输入框
  function showInput() {
    inputVisible.value = true;
    nextTick(() => {
      if (inputRef.value) {
        inputRef.value.focus();
      }
    });
  }

  // 确认输入
  function handleInputConfirm() {
    const value = inputValue.value.trim();
    if (value && !tags.value.includes(value)) {
      tags.value.push(value);
      formState.attributeValue = tags.value.join('|');
    }

    inputVisible.value = false;
    inputValue.value = '';
  }

  // 重置表单
  function resetForm() {
    if (formRef.value) {
      formRef.value.resetFields();
    }

    formState.attributeName = '';
    formState.attributeValue = '';

    tags.value = [];
    inputValue.value = '';
    inputVisible.value = false;
  }

  // 暴露组件方法
  defineExpose({
    add,
    edit,
  });
</script>

<style scoped>
  /* 可以添加一些自定义样式 */
</style>
