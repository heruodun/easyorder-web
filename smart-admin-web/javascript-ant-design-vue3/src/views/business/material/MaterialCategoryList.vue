<template>
  <a-row :gutter="10">
    <a-col :md="12" :sm="24">
      <a-card :bordered="false">
        <!-- 按钮操作区域 -->
        <a-row style="margin-left: 14px">
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click="handleAdd" type="primary">添加类别</a-button>
          <a-button v-if="btnEnableList.indexOf(1) > -1" title="删除多条数据" @click="batchDel" type="default">批量删除</a-button>
          <a-button @click="refresh" type="default" icon="reload">刷新</a-button>
        </a-row>
        <div style="background: #fff; padding-left: 16px; height: 100%; margin-top: 5px">
          <a-alert type="info" :show-icon="true">
            <template #message>
              当前选择：<span v-if="currSelected.title">{{ getCurrSelectedTitle() }}</span>
              <a v-if="currSelected.title" style="margin-left: 10px" @click="onClearSelected">取消选择</a>
            </template>
          </a-alert>
          <!-- 树形控件 -->
          <a-col :md="10" :sm="24">
            <a-dropdown :trigger="[dropTrigger]" @visible-change="dropStatus">
              <span style="user-select: none">
                <a-tree
                  checkable
                  multiple
                  v-model:selectedKeys="selectedKeys"
                  v-model:checkedKeys="checkedKeys"
                  v-model:expandedKeys="iExpandedKeys"
                  :tree-data="categoryTree"
                  :checkStrictly="checkStrictly"
                  :autoExpandParent="true"
                  @select="onSelect"
                  @check="onCheck"
                  @rightClick="rightHandle"
                  @expand="onExpand"
                />
              </span>
            </a-dropdown>
          </a-col>
        </div>
      </a-card>
      <!-- 树操作按钮 -->
      <div class="drawer-bootom-button">
        <a-dropdown :trigger="['click']" placement="topCenter">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="switchCheckStrictly(1)">父子关联</a-menu-item>
              <a-menu-item key="2" @click="switchCheckStrictly(2)">取消关联</a-menu-item>
              <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
              <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
              <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
              <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
            </a-menu>
          </template>
          <a-button> 树操作 <a-icon type="up" /> </a-button>
        </a-dropdown>
      </div>
    </a-col>
    <a-col :md="12" :sm="24">
      <a-card :bordered="false" v-if="selectedKeys.length > 0">
        <a-form :model="formState" ref="formRef" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-item label="名称" name="name" :rules="[{ required: true, message: '请输入名称!' }, { validator: validateName }]">
            <a-input placeholder="请输入名称" v-model:value="formState.name" />
          </a-form-item>
          <a-form-item label="编号" name="serialNo" :rules="[{ required: true, message: '请输入编号!' }]">
            <a-input placeholder="请输入编号" v-model:value="formState.serialNo" />
          </a-form-item>
          <a-form-item label="上级目录" name="parentId">
            <a-tree-select
              style="width: 100%"
              :dropdown-style="{ maxHeight: '200px', overflow: 'auto' }"
              allow-clear
              :tree-default-expand-all="true"
              :tree-data="treeData"
              v-model:value="formState.parentId"
              placeholder="请选择上级目录"
            />
          </a-form-item>
          <a-form-item label="排序" name="sort">
            <a-input v-model:value="formState.sort" />
          </a-form-item>
          <a-form-item label="备注" name="remark">
            <a-textarea placeholder="请输入备注" :rows="2" v-model:value="formState.remark" />
          </a-form-item>
        </a-form>
        <div class="anty-form-btn">
          <a-button @click="emptyCurrForm" type="default" html-type="button" icon="sync">重置</a-button>
          <a-button @click="submitCurrForm" type="primary" html-type="button" icon="form">保存</a-button>
        </div>
      </a-card>
      <a-card v-else>
        <a-empty>
          <template #description> 请先选择一个类别! </template>
        </a-empty>
      </a-card>
    </a-col>
    <material-category-modal ref="materialCategoryModal" @ok="loadTree"></material-category-modal>
  </a-row>
</template>

<script>
  import { defineComponent, ref, reactive, onMounted, nextTick } from 'vue';
  import { useRoute } from 'vue-router';
  import { message, Modal } from 'ant-design-vue';
  import MaterialCategoryModal from '../material/modules/MaterialCategoryModal.vue';
  import pick from 'lodash.pick';
  import { queryMaterialCategoryTreeList, queryMaterialCategoryById, checkMaterialCategory } from '/@/api/api';
  import { httpAction, deleteAction } from '/@/api/manage';

  export default defineComponent({
    name: 'MaterialCategoryList',
    components: {
      MaterialCategoryModal,
    },
    setup() {
      const route = useRoute();
      const formRef = ref(null);
      const materialCategoryModal = ref(null);

      // 响应式状态
      const state = reactive({
        iExpandedKeys: [],
        loading: false,
        currFlowId: route.params.id || '',
        currFlowName: route.params.name || '',
        treeData: [],
        categoryTree: [],
        rightClickSelectedKey: '',
        dropTrigger: '',
        checkedKeys: [],
        selectedKeys: [],
        currSelected: {},
        allTreeKeys: [],
        checkStrictly: true,
        btnEnableList: [1], // 示例值，根据实际需求调整
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        url: {
          delete: '/materialCategory/delete',
          edit: '/materialCategory/update',
          deleteBatch: '/materialCategory/deleteBatch',
        },
      });

      // 表单状态
      const formState = reactive({
        name: '',
        serialNo: '',
        parentId: undefined,
        sort: undefined,
        remark: '',
      });

      // 加载树数据
      const loadTree = async () => {
        state.treeData = [];
        state.categoryTree = [];
        const params = { id: '' };
        state.loading = true;

        try {
          const res = await queryMaterialCategoryTreeList(params);
          if (res) {
            state.allTreeKeys = [];
            for (let i = 0; i < res.length; i++) {
              const temp = res[i];
              state.categoryTree.push(temp);
              setThisExpandedKeys(temp);
              getAllKeys(temp);
            }
          }
        } catch (error) {
          console.error('加载类别树失败:', error);
          message.error('加载类别树失败');
        } finally {
          state.loading = false;
        }
      };

      // 设置展开的节点
      const setThisExpandedKeys = (node) => {
        if (node.children && node.children.length > 0) {
          state.iExpandedKeys.push(node.key);
          for (let a = 0; a < node.children.length; a++) {
            setThisExpandedKeys(node.children[a]);
          }
        }
      };

      // 刷新树
      const refresh = () => {
        state.loading = true;
        loadTree();
      };

      // 右键操作
      const rightHandle = (info) => {
        state.dropTrigger = 'contextmenu';
        state.rightClickSelectedKey = info.node.key;
      };

      // 节点展开
      const onExpand = (expandedKeys) => {
        state.iExpandedKeys = expandedKeys;
      };

      // 下拉状态变化
      const dropStatus = (visible) => {
        if (!visible) {
          state.dropTrigger = '';
        }
      };

      // 批量删除
      const batchDel = () => {
        if (state.checkedKeys.length <= 0) {
          message.warning('请选择一条记录！');
          return;
        }

        let ids = state.checkedKeys.join(',');

        Modal.confirm({
          title: '确认删除',
          content: `确定要删除所选中的 ${state.checkedKeys.length} 条数据吗?`,
          onOk: async () => {
            try {
              const res = await deleteAction(state.url.deleteBatch, { ids });
              if (res.code == 200) {
                message.success(res.data.message);
                loadTree();
                onClearSelected();
              } else {
                message.warning(res.data.message);
              }
            } catch (error) {
              console.error('删除失败:', error);
              message.error('删除失败');
            }
          },
        });
      };

      // 节点选择
      const onSelect = async (selectedKeys, e) => {
        const record = e.node.dataRef;
        const params = { id: record.id };

        try {
          const res = await queryMaterialCategoryById(params);
          if (res && res.code == 200 && res.data) {
            const data = res.data;
            record.name = data.name;
            record.serialNo = data.serialNo;
            record.parentId = data.parentId;
            record.sort = data.sort;
            record.remark = data.remark;

            state.currSelected = { ...record };
            state.selectedKeys = [record.key];
            formState.parentId = record.parentId;
            setValuesToForm(record);
          }
        } catch (error) {
          console.error('加载类别详情失败:', error);
        }
      };

      // 节点勾选
      const onCheck = (checkedKeysValue, info) => {
        if (state.checkStrictly) {
          state.checkedKeys = checkedKeysValue.checked;
        } else {
          state.checkedKeys = checkedKeysValue;
        }
      };

      // 根据参数加载树
      const getTreeByParams = async (params) => {
        try {
          const res = await queryMaterialCategoryTreeList(params);
          if (res) {
            state.treeData = [...res];
          }
        } catch (error) {
          console.error('加载树数据失败:', error);
        }
      };

      // 设置表单值
      const setValuesToForm = (record) => {
        nextTick(() => {
          Object.assign(formState, pick(record, ['name', 'serialNo', 'parentId', 'sort', 'remark']));
        });
      };

      // 获取当前选中标题
      const getCurrSelectedTitle = () => {
        return state.currSelected.title || '';
      };

      // 清除选中
      const onClearSelected = () => {
        state.checkedKeys = [];
        state.currSelected = {};
        formRef.value?.resetFields();
        state.selectedKeys = [];
      };

      // 提交表单
      const submitCurrForm = async () => {
        try {
          await formRef.value.validate();

          if (!state.currSelected.id) {
            message.warning('请点击选择要修改类别!');
            return;
          }

          const formData = { ...state.currSelected, ...formState };

          const res = await httpAction(state.url.edit, formData, 'put');
          if (res.code == 200) {
            message.success('保存成功!');
            loadTree();
            getTreeByParams({ id: formData.id });
          } else {
            message.warning(res.data.message);
          }
        } catch (error) {
          console.error('表单验证失败:', error);
        }
      };

      // 重置表单
      const emptyCurrForm = () => {
        formRef.value?.resetFields();
      };

      // 名称验证
      const validateName = async (rule, value) => {
        if (!value) return Promise.reject('请输入名称');

        const params = {
          name: value,
          id: state.currSelected.id || 0,
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
      };

      // 添加类别
      const handleAdd = () => {
        if (materialCategoryModal.value) {
          materialCategoryModal.value.add();
          materialCategoryModal.value.title = '新增';
        }
      };

      // 树操作功能
      const expandAll = () => {
        state.iExpandedKeys = [...state.allTreeKeys];
      };

      const closeAll = () => {
        state.iExpandedKeys = [];
      };

      const checkALL = () => {
        state.checkStrictly = false;
        state.checkedKeys = [...state.allTreeKeys];
      };

      const cancelCheckALL = () => {
        state.checkedKeys = [];
      };

      const switchCheckStrictly = (v) => {
        state.checkStrictly = v === 2;
      };

      const getAllKeys = (node) => {
        state.allTreeKeys.push(node.key);
        if (node.children && node.children.length > 0) {
          for (let a = 0; a < node.children.length; a++) {
            getAllKeys(node.children[a]);
          }
        }
      };

      // 初始化
      onMounted(() => {
        loadTree();
      });

      return {
        ...state,
        formState,
        formRef,
        materialCategoryModal,
        loadTree,
        refresh,
        rightHandle,
        onExpand,
        dropStatus,
        batchDel,
        onSelect,
        onCheck,
        getTreeByParams,
        setValuesToForm,
        getCurrSelectedTitle,
        onClearSelected,
        submitCurrForm,
        emptyCurrForm,
        validateName,
        handleAdd,
        expandAll,
        closeAll,
        checkALL,
        cancelCheckALL,
        switchCheckStrictly,
        getAllKeys,
      };
    },
  });
</script>

<style scoped>
  .ant-card-body .table-operator {
    margin: 15px;
  }

  .anty-form-btn {
    width: 100%;
    text-align: center;
  }

  .anty-form-btn button {
    margin: 0 5px;
  }

  .anty-node-layout .ant-layout-header {
    padding-right: 0;
  }

  .header {
    padding: 0 8px;
  }

  .header button {
    margin: 0 3px;
  }

  .ant-modal-cust-warp {
    height: 100%;
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto;
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden;
  }

  /** Button按钮间距 */
  .ant-btn {
    margin-left: 3px;
  }

  .drawer-bootom-button {
    position: relative;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: left;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
    margin-top: 10px;
  }
</style>
