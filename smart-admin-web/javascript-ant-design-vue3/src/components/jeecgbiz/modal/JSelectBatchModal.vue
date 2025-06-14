<template>
  <a-modal
    :width="modalWidth"
    :open="visible"
    :title="title"
    @ok="handleSubmit"
    @cancel="close"
    cancelText="关闭"
    style="top: 12%; height: 90%; overflow-y: hidden"
    wrapClassName="ant-modal-cust-warp"
  >
    <a-row :gutter="10" style="padding: 10px; margin: -10px">
      <a-col :md="24" :sm="24">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter="onSearch">
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="批号">
                  <a-input ref="name" placeholder="请输入批号" v-model:value="queryParam.name"></a-input>
                </a-form-item>
              </a-col>
              <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                <a-col :md="12" :sm="24">
                  <a-button type="primary" @click="loadData(1)">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset(1)">重置</a-button>
                  <span style="margin-left: 20px">提示：双击行可以直接选中</span>
                </a-col>
              </span>
            </a-row>
          </a-form>
          <a-table
            ref="table"
            :scroll="scrollTrigger"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :rowSelection="{
              selectedRowKeys: selectedRowKeys,
              onChange: onSelectChange,
              type: getType,
            }"
            :loading="loading"
            :customRow="rowAction"
          >
          </a-table>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
  import { $on, $off, $once, $emit } from '../../../utils/gogocodeTransfer';
  import { getAction } from '/@/api/manage';
  import { getBatchNumberList } from '/@/api/api';
  import { JeecgListMixin } from '/@/mixins/JeecgListMixin';
  import { Form, Input, Select, Button } from 'ant-design-vue';

  export default {
    name: 'JSelectBatchModal',
    mixins: [JeecgListMixin],
    components: {},
    props: ['rows', 'multi', 'barCode'],
    data() {
      return {
        modalWidth: 1100,
        queryParam: {
          name: '',
          depotItemId: '',
          depotId: '',
          barCode: '',
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        categoryTree: [],
        columns: [
          {
            dataIndex: 'batchNumber',
            title: '批号',
            width: 100,
            align: 'left',
            ellipsis: true,
          },
          { dataIndex: 'barCode', title: '条码', width: 100, ellipsis: true },
          { dataIndex: 'name', title: '名称', width: 100, ellipsis: true },
          { dataIndex: 'standard', title: '规格', width: 80, ellipsis: true },
          { dataIndex: 'model', title: '型号', width: 80, ellipsis: true },
          {
            dataIndex: 'commodityUnit',
            title: '单位',
            width: 60,
            ellipsis: true,
          },
          { dataIndex: 'expirationDateStr', title: '有效期至', width: 80 },
          { dataIndex: 'totalNum', title: '库存', width: 80 },
        ],
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectRows: [],
        selectIds: [],
        title: '选择批号',
        isorter: {
          column: 'createTime',
          order: 'desc',
        },
        departTree: [],
        depotList: [],
        visible: false,
        // form: this.$form.createForm(this),
        form: Form.useForm,
        loading: false,
        expandedKeys: [],
      };
    },
    computed: {
      // 计算属性的 getter
      getType: function () {
        return this.multi == true ? 'checkbox' : 'radio';
      },
    },
    watch: {
      barCode: {
        deep: true,
        immediate: true,

        handler() {
          this.initBarCode();
        },
      },
    },
    created() {
      this.loadData();
    },
    methods: {
      initBarCode() {
        if (this.barCode) {
          $emit(this, 'initComp', this.barCode);
        } else {
          // JSelectUserByDep组件bug issues/I16634
          $emit(this, 'initComp', '');
        }
      },
      loadData(arg) {
        if (this.rows) {
          if (JSON.parse(this.rows).depotId && JSON.parse(this.rows).barCode) {
            let isEdit = JSON.parse(this.rows).isEdit;
            if (isEdit) {
              //只有在保存之后的编辑页面下才获取明细id
              let depotItemId = JSON.parse(this.rows).id;
              if (depotItemId.length <= 19) {
                this.queryParam.depotItemId = depotItemId - 0;
              }
            }
            this.queryParam.depotId = JSON.parse(this.rows).depotId - 0;
            this.queryParam.barCode = JSON.parse(this.rows).barCode;
          }
        }
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true;
        let params = this.getQueryParams(); //查询条件
        getBatchNumberList(params)
          .then((res) => {
            if (res && res.code === 200) {
              this.dataSource = res.data.rows;
              this.ipagination.total = res.data.total;
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
      showModal() {
        this.visible = true;
        this.$nextTick(() => this.$refs.name.focus());
        this.loadData();
        this.form.resetFields();
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        return param;
      },
      getQueryField() {
        let str = 'id,';
        for (let a = 0; a < this.columns.length; a++) {
          str += ',' + this.columns[a].dataIndex;
        }
        return str;
      },
      searchReset(num) {
        let that = this;
        if (num !== 0) {
          if (this.rows) {
            this.queryParam.name = '';
            if (JSON.parse(this.rows).depotId && JSON.parse(this.rows).barCode) {
              let isEdit = JSON.parse(this.rows).isEdit;
              if (isEdit) {
                //只有在保存之后的编辑页面下才获取明细id
                let depotItemId = JSON.parse(this.rows).id;
                if (depotItemId.length <= 19) {
                  this.queryParam.depotItemId = depotItemId - 0;
                }
              }
              this.queryParam.depotId = JSON.parse(this.rows).depotId - 0;
              this.queryParam.barCode = JSON.parse(this.rows).barCode;
            }
          }
          that.loadData(1);
        }
        that.selectedRowKeys = [];
        that.selectIds = [];
      },
      close() {
        this.searchReset(0);
        this.visible = false;
      },
      handleSubmit() {
        let that = this;
        this.getSelectRows();
        $emit(that, 'ok', that.selectRows, that.selectIds);
        that.searchReset(0);
        that.close();
      },
      //获取选择信息
      getSelectRows(rowId) {
        let dataSource = this.dataSource;
        let ids = '';
        this.selectRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectRows.push(dataSource[i]);
            ids = ids + ',' + dataSource[i].batchNumber;
          }
        }
        this.selectIds = ids.substring(1);
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onSearch() {
        if (this.dataSource && this.dataSource.length === 1) {
          if (this.queryParam.name === this.dataSource[0].batchNumber) {
            let arr = [];
            arr.push(this.dataSource[0].id);
            this.selectedRowKeys = arr;
            this.handleSubmit();
          } else {
            this.loadData(1);
          }
        } else {
          this.loadData(1);
        }
      },
      modalFormOk() {
        this.loadData();
      },
      rowAction(record, index) {
        return {
          on: {
            click: () => {
              let arr = [];
              arr.push(record.id);
              this.selectedRowKeys = arr;
            },
            dblclick: () => {
              let arr = [];
              arr.push(record.id);
              this.selectedRowKeys = arr;
              this.handleSubmit();
            },
          },
        };
      },
    },
    emits: ['initComp', 'ok'],
  };
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
  }
  #components-layout-demo-custom-trigger .trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color 0.3s;
  }
</style>
