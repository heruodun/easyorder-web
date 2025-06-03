<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
      :ok="false"
      :open="visible"
      :confirm-loading="confirmLoading"
      :ok-button-props="{ disabled: disableSubmit }"
      :get-container="() => container"
      :mask-style="{ top: '93px', left: '154px' }"
      :wrap-class-name="wrapClassNameInfo"
      :mask="isDesktop"
      :mask-closable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      style="top: 100px; height: 50%"
      cancel-text="取消"
      ok-text="保存"
    >
      <a-spin :spinning="confirmLoading">
        <a-form :model="formState" ref="formRef" layout="vertical">
          <a-form-item
            :label-col="labelCol"
            :wrapper-col="wrapperCol"
            label="名称"
            name="name"
            :rules="[{ required: true, message: '请输入名称!' }, { validator: validateName }]"
          >
            <a-input placeholder="请输入名称" v-model:value="formState.name" />
          </a-form-item>
          <a-form-item
            :label-col="labelCol"
            :wrapper-col="wrapperCol"
            label="编号"
            name="serialNo"
            :rules="[{ required: true, message: '请输入编号!' }]"
          >
            <a-input placeholder="请输入编号" v-model:value="formState.serialNo" />
          </a-form-item>
          <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="上级目录" name="parentId">
            <a-tree-select
              style="width: 100%"
              :dropdown-style="{ maxHeight: '200px', overflow: 'auto' }"
              allow-clear
              :tree-default-expand-all="true"
              :tree-data="categoryTree"
              v-model:value="formState.parentId"
              placeholder="请选择上级目录"
            >
            </a-tree-select>
          </a-form-item>
          <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="排序" name="sort">
            <a-input v-model:value="formState.sort" />
          </a-form-item>
          <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="备注" name="remark">
            <a-textarea placeholder="请输入备注" :rows="2" v-model:value="formState.remark" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, ref, reactive, getCurrentInstance, nextTick, computed } from 'vue';
  import { queryMaterialCategoryTreeList, checkMaterialCategory, addMaterialCategory } from '/@/api/api';
  import { useDevice } from '/@/utils/device-util'; // 引入设备工具函数
  import pick from 'lodash.pick';

  export default defineComponent({
    name: 'MaterialCategoryModal',
    components: {},
    setup() {
      const container = ref(null);
      const formRef = ref(null);
      const formState = reactive({
        name: '',
        serialNo: '',
        parentId: undefined,
        sort: undefined,
        remark: '',
      });

      // 引入设备相关功能
      const { device, isMobile, isDesktop, wrapClassNameInfo } = useDevice();

      const categoryTree = ref([]);
      const orgTypeData = ref([]);
      const phoneWarning = ref('');
      const departName = ref('');
      const title = ref('操作');
      const visible = ref(false);
      const disableSubmit = ref(false);
      const menuhidden = ref(false);
      const menuusing = ref(true);
      const confirmLoading = ref(false);

      const labelCol = reactive({
        xs: { span: 24 },
        sm: { span: 5 },
      });

      const wrapperCol = reactive({
        xs: { span: 24 },
        sm: { span: 16 },
      });

      const instance = getCurrentInstance();

      return {
        container,
        formRef,
        formState,
        categoryTree,
        orgTypeData,
        phoneWarning,
        departName,
        title,
        visible,
        disableSubmit,
        menuhidden,
        menuusing,
        confirmLoading,
        labelCol,
        wrapperCol,
        device,
        isMobile,
        isDesktop,
        wrapClassNameInfo,
        instance,
      };
    },
    methods: {
      async loadTreeData() {
        const params = {};
        params.id = '';
        try {
          const res = await queryMaterialCategoryTreeList(params);
          if (res) {
            this.categoryTree = [];
            for (let i = 0; i < res.length; i++) {
              let temp = res[i];
              this.categoryTree.push(temp);
            }
          }
        } catch (error) {
          console.error('加载目录树失败:', error);
        }
      },
      add() {
        this.edit({});
      },
      edit(record) {
        // 重置表单
        if (this.formRef) {
          this.formRef.resetFields();
        }

        // 初始化表单状态
        Object.keys(this.formState).forEach((key) => {
          this.formState[key] = record[key] || '';
        });

        this.visible = true;
        this.loadTreeData();

        // 手动设置表单值
        nextTick(() => {
          const formValues = pick(record, ['name', 'serialNo', 'parentId', 'sort', 'remark']);
          Object.assign(this.formState, formValues);
        });
      },
      close() {
        this.$emit('close');
        this.disableSubmit = false;
        this.visible = false;
      },
      async handleOk() {
        try {
          // 表单验证
          await this.formRef.validate();

          this.confirmLoading = true;
          const formData = { ...this.formState };

          const res = await addMaterialCategory(formData);

          if (res.code == 200) {
            this.$message.success(res.data.message);
            this.loadTreeData();
            this.$emit('ok');
          } else {
            this.$message.warning(res.data.message);
          }
        } catch (error) {
          console.error('表单验证失败:', error);
        } finally {
          this.confirmLoading = false;
          this.close();
        }
      },
      handleCancel() {
        this.close();
      },
      async validateName(rule, value) {
        if (!value) return Promise.reject('请输入名称');

        const params = {
          name: value,
          id: this.formState.id || 0,
        };

        try {
          const res = await checkMaterialCategory(params);
          if (res && res.code === 200) {
            if (res.data.status) {
              return Promise.reject('名称已经存在');
            } else {
              return Promise.resolve();
            }
          }
          return Promise.reject('验证失败，请重试');
        } catch (error) {
          return Promise.reject('名称验证失败');
        }
      },
    },
  });
</script>

<style scoped></style>
