<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
      :visible="visible"
      :confirm-loading="confirmLoading"
      :get-container="() => containerRef"
      :mask-style="{ top: '93px', left: '154px' }"
      :wrap-class-name="wrapClassName"
      :mask="isDesktop"
      :mask-closable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      :cancel-text="'取消'"
      :ok-text="'保存'"
      style="top: 100px; height: 60%"
    >
      <template #footer>
        <a-button key="back" v-if="isReadOnly" @click="handleCancel"> 取消 </a-button>
      </template>
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
                <a-tag v-else style="background: #fff; borderstyle: dashed" @click="showInput"> <PlusOutlined /> 请输入属性值 </a-tag>
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

<script>
  import { reactive, ref, toRefs, computed, watch, nextTick } from 'vue';
  import { message } from 'ant-design-vue';
  import { addMaterialAttribute, checkMaterialAttribute, editMaterialAttribute } from '/@/api/api';
  import { useDevice } from '/@/utils/device-util'; // 引入设备工具函数

  export default {
    name: 'MaterialAttributeModal',

    setup(props, { emit }) {
      // 设备相关状态
      const device = useDevice();

      // DOM 引用
      const containerRef = ref(null);
      const inputRef = ref(null);
      const formRef = ref(null);

      // 状态管理
      const state = reactive({
        title: '操作',
        visible: false,
        model: {},
        isReadOnly: false,
        activeKey: '1',
        tags: [],
        inputVisible: false,
        inputValue: '',
        confirmLoading: false,
        formState: {
          attributeName: '',
          attributeValue: '',
        },
      });

      const labelCol = {
        xs: { span: 24 },
        sm: { span: 5 },
      };

      const wrapperCol = {
        xs: { span: 24 },
        sm: { span: 16 },
      };

      // 验证规则
      const validatorRules = {
        attributeName: [
          { required: true, message: '请输入属性名!' },
          { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' },
          { validator: validateAttributeName },
        ],
        attributeValue: [{ required: true, message: '请输入属性值!' }],
      };

      // 监听 activeKey 变化
      watch(
        () => state.activeKey,
        (newVal) => {
          if (newVal === '1') {
            state.tags = state.formState.attributeValue.split('|').filter((tag) => tag);
          } else {
            state.formState.attributeValue = state.tags.join('|');
          }
        }
      );

      // 添加方法
      function add() {
        edit({});
      }

      // 编辑方法
      function edit(record) {
        resetForm();
        state.model = { ...record };
        state.activeKey = '1';
        state.visible = true;

        if (state.model.attributeValue) {
          state.tags = state.model.attributeValue.split('|').filter((tag) => tag);
        } else {
          state.tags = [];
        }

        nextTick(() => {
          state.formState.attributeName = state.model.attributeName || '';
          state.formState.attributeValue = state.tags.join('|') || '';
        });
      }

      // 关闭模态框
      function close() {
        state.visible = false;
        emit('close');
      }

      // 处理确认
      async function handleOk() {
        try {
          await formRef.value.validateFields();
          state.confirmLoading = true;

          const formData = {
            ...state.model,
            ...state.formState,
          };

          const action = state.model.id ? editMaterialAttribute : addMaterialAttribute;
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
          state.confirmLoading = false;
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
          id: state.model.id || 0,
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
        state.tags = state.formState.attributeValue.split('|').filter((tag) => tag);
      }

      // 删除标签
      function handleClose(removedTag) {
        state.tags = state.tags.filter((tag) => tag !== removedTag);
        state.formState.attributeValue = state.tags.join('|');
      }

      // 显示输入框
      function showInput() {
        state.inputVisible = true;
        nextTick(() => {
          if (inputRef.value) {
            inputRef.value.focus();
          }
        });
      }

      // 确认输入
      function handleInputConfirm() {
        const inputValue = state.inputValue.trim();
        if (inputValue && !state.tags.includes(inputValue)) {
          state.tags.push(inputValue);
          state.formState.attributeValue = state.tags.join('|');
        }

        state.inputVisible = false;
        state.inputValue = '';
      }

      // 重置表单
      function resetForm() {
        if (formRef.value) {
          formRef.value.resetFields();
        }

        state.formState = {
          attributeName: '',
          attributeValue: '',
        };

        state.tags = [];
        state.inputValue = '';
        state.inputVisible = false;
      }

      // 返回需要暴露的数据和方法
      return {
        ...toRefs(state),
        containerRef,
        inputRef,
        formRef,
        labelCol,
        wrapperCol,
        wrapClassName: computed(() => device.wrapClassNameInfo.value),
        isDesktop: computed(() => device.isDesktop.value),
        add,
        edit,
        handleOk,
        handleCancel,
        handleClose,
        showInput,
        handleInputConfirm,
        handleValueChange,
        validateAttributeName,
      };
    },
  };
</script>

<style scoped>
  /* 可以添加一些自定义样式 */
</style>
