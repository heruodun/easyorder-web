<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-tree-select
                    style="width: 100%"
                    :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
                    allow-clear
                    :treeData="categoryTree"
                    v-model:value="queryParam.categoryId"
                    placeholder="请选择类别"
                  >
                  </a-tree-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入条码、名称、助记码等查询" v-model:value="queryParam.materialParam"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入规格查询" v-model:value="queryParam.standard"></a-input>
                </a-form-item>
              </a-col>

              <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="searchQuery">
                  <template #icon>
                    <SearchOutlined />
                  </template>
                  查询
                </a-button>
                <a-button @click="searchReset" class="smart-margin-left10">
                  <template #icon>
                    <ReloadOutlined />
                  </template>
                  重置
                </a-button>
                <a @click="handleToggleSearch" style="margin-left: 8px">
                  {{ toggleSearchStatus ? '收起' : '展开' }}
                  <UpOutlined v-if="toggleSearchStatus" />
                  <DownOutlined v-else />
                </a>
              </a-form-item>
            </a-row>
            <template v-if="toggleSearchStatus">
              <a-row :gutter="24">
                <a-col :md="6" :sm="24">
                  <a-form-item label="型号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入型号查询" v-model:value="queryParam.model"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="颜色" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入颜色查询" v-model:value="queryParam.color"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="品牌" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入品牌查询" v-model:value="queryParam.brand"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="制造商" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入制造商查询" v-model:value="queryParam.mfrs"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item :label="queryTitle.mp1" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input :placeholder="'请输入' + queryTitle.mp1 + '查询'" v-model:value="queryParam.otherField1"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item :label="queryTitle.mp2" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input :placeholder="'请输入' + queryTitle.mp2 + '查询'" v-model:value="queryParam.otherField2"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item :label="queryTitle.mp3" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input :placeholder="'请输入' + queryTitle.mp3 + '查询'" v-model:value="queryParam.otherField3"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择状态" v-model:value="queryParam.enabled">
                      <a-select-option value="1">启用</a-select-option>
                      <a-select-option value="0">禁用</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="序列号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="有无序列号" v-model:value="queryParam.enableSerialNumber">
                      <a-select-option value="1">有</a-select-option>
                      <a-select-option value="0">无</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="批号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="有无批号" v-model:value="queryParam.enableBatchNumber">
                      <a-select-option value="1">有</a-select-option>
                      <a-select-option value="0">无</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="仓位货架" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input style="width: 100%" placeholder="请输入仓位货架查询" v-model:value="queryParam.position"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="基础重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input-number style="width: 100%" placeholder="请输入基础重量查询" v-model:value="queryParam.weight"></a-input-number>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="保质期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input-number style="width: 100%" placeholder="请输入保质期查询" v-model:value="queryParam.expiryNum"></a-input-number>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入备注查询" v-model:value="queryParam.remark"></a-input>
                  </a-form-item>
                </a-col>
              </a-row>
            </template>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <a-row class="smart-table-btn-block">
          <!-- <div class="table-operator" style="margin-top: 5px"> -->
          <div class="smart-table-operate-block">
            <!-- <a-button v-if="btnEnableList.indexOf(1) > -1" @click="handleAdd" type="primary" icon="plus">新增</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchDel" icon="delete">删除</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchSetStatus(true)" icon="check-square">启用</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchSetStatus(false)" icon="close-square">禁用</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="handleImportXls()" icon="import">导入</a-button>
            <a-button v-if="btnEnableList.indexOf(3) > -1" @click="handleExportXls('商品信息')" icon="download">导出</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchEdit()" icon="edit">批量编辑</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchSetMaterialCurrentStock()" icon="stock">修正库存</a-button>
            <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchSetMaterialCurrentUnitPrice()" icon="fund">修正成本</a-button> -->

            <a-button @click="handleAdd" type="primary"><PlusOutlined />新增</a-button>
            <a-button @click="batchDel"><DeleteOutlined />删除</a-button>
            <a-button @click="batchSetStatus(true)"><CheckSquareOutlined />启用</a-button>
            <a-button @click="batchSetStatus(false)"><CloseSquareOutlined />禁用</a-button>
            <a-button @click="handleImportXls()"><ImportOutlined />导入</a-button>
            <a-button @click="handleExportXls('商品信息')"><DownloadOutlined />导出</a-button>
            <a-button @click="batchEdit()"><EditOutlined />批量编辑</a-button>
            <a-button @click="batchSetMaterialCurrentStock()"><StockOutlined />修正库存</a-button>
            <a-button @click="batchSetMaterialCurrentUnitPrice()"><FundOutlined />修正成本</a-button>
          </div>
          <!-- <a-popover trigger="click" placement="right">
            <template v-slot:content>
              <a-checkbox-group @change="onColChange" v-model:value="settingDataIndex" :defaultValue="settingDataIndex">
                <a-row style="width: 500px">
                  <template v-for="(item, index) in defColumns">
                    <template>
                      <a-col :span="8">
                        <a-checkbox :value="item.dataIndex">
                          <j-ellipsis :value="item.title" :length="10"></j-ellipsis>
                        </a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
                <a-row style="padding-top: 10px">
                  <a-col> 恢复默认列配置：<a-button @click="handleRestDefault" type="link" size="small">恢复默认</a-button> </a-col>
                </a-row>
              </a-checkbox-group>
            </template>
            <a-button>
              <SettingOutlined />
              列设置</a-button
            >
          </a-popover> -->
          <div class="smart-table-setting-block">
            <!-- todo 暂时写死，不知道什么原因引入不进来 -->
            <!-- <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.GOODS_INFO" :refresh="loadData" /> -->
            <TableOperator v-model="columns" :tableId="40004" :refresh="loadData" />
          </div>
        </a-row>
        <!-- </div> -->
        <!-- table区域-begin -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :data-source="dataSource"
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{
              selectedRowKeys: selectedRowKeys,
              onChange: onSelectChange,
              columnWidth: '40px',
            }"
            @change="handleTableChange"
          >
            <template #bodyCell="{ text, column, record, index }">
              <template v-if="column.dataIndex === 'action'">
                <span>
                  <a @click="handleEdit(record)">编辑</a>
                  <a-divider v-if="btnEnableList.indexOf(1) > -1" type="vertical" />
                  <a v-if="btnEnableList.indexOf(1) > -1" @click="handleCopyAdd(record)">复制</a>
                </span>
              </template>

              <template v-else-if="column.dataIndex === 'pic'">
                <a-popover placement="right" trigger="click">
                  <template v-slot:content>
                    <img :src="getImgUrl(record.imgName, record.imgLarge)" width="500px" />
                  </template>
                  <div class="item-info" v-if="record.imgName">
                    <img v-if="record.imgName" :src="getImgUrl(record.imgName, record.imgSmall)" class="item-img" title="查看大图" />
                  </div>
                </a-popover>
              </template>

              <template v-else-if="column.dataIndex === 'name'">
                {{ record.name }}
                <a-tag v-if="record.enableSerialNumber == 1" color="orange">序</a-tag>
                <a-tag v-if="record.enableBatchNumber == 1" color="orange">批</a-tag>
              </template>

              <template v-else-if="column.dataIndex === 'initialStock'">
                <a-tooltip :title="record.bigUnitInitialStock">
                  {{ text }}
                </a-tooltip>
              </template>

              <template v-else-if="column.dataIndex === 'stock'">
                <a-tooltip :title="record.bigUnitStock">
                  {{ text }}
                </a-tooltip>
              </template>

              <template v-else-if="column.dataIndex === 'enabled'">
                <a-tag v-if="record.enabled" color="green">启用</a-tag>
                <a-tag v-if="!record.enabled" color="orange">禁用</a-tag>
              </template>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <material-modal ref="modalForm" @ok="modalFormOk"></material-modal>
        <import-file-modal ref="modalImportForm" @ok="modalFormOk"></import-file-modal>
        <batch-set-info-modal ref="batchSetInfoModalForm" @ok="modalFormOk"></batch-set-info-modal>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import MaterialModal from './modules/MaterialModal.vue';
  import ImportFileModal from '/@/components/tools/ImportFileModal.vue';
  import BatchSetInfoModal from './modules/BatchSetInfoModal.vue';
  import { queryMaterialCategoryTreeList } from '/@/api/api';
  import { postAction, getFileAccessHttpUrl } from '/@/api/manage';
  import { getMpListShort } from '/@/utils/util';
  import { JeecgListMixin } from '/@/mixins/JeecgListMixin';
  import JEllipsis from '/@/components/jeecg/JEllipsis.vue';
  import JDate from '/@/components/jeecg/JDate.vue';
  import * as Vue from 'vue';
  import { useMaterialStore } from '/@/store/modules/material';

  export default {
    name: 'MaterialList',
    mixins: [JeecgListMixin],
    components: {
      MaterialModal,
      ImportFileModal,
      BatchSetInfoModal,
      JEllipsis,
      JDate,
      TableOperator,
    },

    data() {
      return {
        categoryTree: [],
        mPropertyListShort: '',
        model: {},
        labelCol: {
          span: 5,
        },
        wrapperCol: {
          span: 18,
          offset: 1,
        },
        queryTitle: {
          mp1: '扩展1',
          mp2: '扩展2',
          mp3: '扩展3',
        },
        // 查询条件
        queryParam: {
          categoryId: undefined,
          materialParam: '',
          standard: '',
          model: '',
          color: '',
          brand: '',
          mfrs: '',
          otherField1: '',
          otherField2: '',
          otherField3: '',
          weight: '',
          expiryNum: '',
          enabled: undefined,
          enableSerialNumber: undefined,
          enableBatchNumber: undefined,
          position: '',
          remark: '',
          mpList: getMpListShort(useMaterialStore().getPropertyList), //扩展属性
        },

        // 实际索引
        settingDataIndex: [],
        // 默认列

        defColumns: [],
        // 默认索引
        defDataIndex: [
          'action',
          'mBarCode',
          'name',
          'standard',
          'model',
          'color',
          'categoryName',
          'unit',
          'stock',
          'purchaseDecimal',
          'commodityDecimal',
          'wholesaleDecimal',
          'lowDecimal',
          'enabled',
        ],

        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 40,
            align: 'center',
            customRender: ({ index }) => index + 1,
          },

          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            width: 100,
          },
          {
            title: '图片',
            dataIndex: 'pic',
            width: 60,
          },
          { title: '条码', dataIndex: 'mBarCode', width: 120 },
          {
            title: '名称',
            dataIndex: 'name',
            width: 160,
          },
          { title: '规格', dataIndex: 'standard', width: 120 },
          { title: '型号', dataIndex: 'model', width: 120 },
          { title: '颜色', dataIndex: 'color', width: 70, ellipsis: true },
          { title: '品牌', dataIndex: 'brand', width: 100, ellipsis: true },
          { title: '助记码', dataIndex: 'mnemonic', width: 80, ellipsis: true },
          {
            title: '类别',
            dataIndex: 'categoryName',
            width: 100,
            ellipsis: true,
          },
          {
            title: '扩展1',
            dataIndex: 'otherField1',
            width: 100,
            ellipsis: true,
          },
          {
            title: '扩展2',
            dataIndex: 'otherField2',
            width: 100,
            ellipsis: true,
          },
          {
            title: '扩展3',
            dataIndex: 'otherField3',
            width: 100,
            ellipsis: true,
          },
          {
            title: '单位',
            dataIndex: 'unit',
            width: 100,
            ellipsis: true,
            customRender: function (t, r, index) {
              if (r) {
                let name = t ? t : r.unitName;
                if (r.sku) {
                  return name + '[SKU]';
                } else {
                  return name;
                }
              }
            },
          },
          { title: '基础重量', dataIndex: 'weight', width: 80 },
          { title: '保质期', dataIndex: 'expiryNum', width: 60 },
          { title: '制造商', dataIndex: 'mfrs', width: 120, ellipsis: true },
          {
            title: '初始库存',
            dataIndex: 'initialStock',
            width: 80,
            scopedSlots: { customRender: 'customRenderInitialStock' },
          },
          {
            title: '库存',
            dataIndex: 'stock',
            width: 80,
          },
          { title: '采购价', dataIndex: 'purchaseDecimal', width: 80 },
          { title: '零售价', dataIndex: 'commodityDecimal', width: 80 },
          { title: '销售价', dataIndex: 'wholesaleDecimal', width: 80 },
          { title: '最低售价', dataIndex: 'lowDecimal', width: 80 },
          { title: '仓位货架', dataIndex: 'position', width: 80 },
          { title: '备注', dataIndex: 'remark', width: 80 },
          {
            title: '状态',
            dataIndex: 'enabled',
            align: 'center',
            width: 60,
          },
        ],
        url: {
          list: '/material/list',
          delete: '/material/delete',
          deleteBatch: '/material/deleteBatch',
          importExcelUrl: '/material/importExcel',
          exportXlsUrl: '/material/exportExcel',
          batchSetStatusUrl: '/material/batchSetStatus',
          batchSetMaterialCurrentStockUrl: '/material/batchSetMaterialCurrentStock',
          batchSetMaterialCurrentUnitPriceUrl: '/material/batchSetMaterialCurrentUnitPrice',
        },
      };
    },
    created() {
      this.model = Object.assign({}, {});
      // this.initColumnsSetting();
      this.loadTreeData();
      this.handleChangeOtherField(1);
    },

    mounted() {
      console.log('Columns:', JSON.stringify(this.columns)); // 检查是否为 undefined
    },

    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      },
    },
    methods: {
      //   // let columnsStr = Vue.ls.get('materialColumns');
      //   let columnsStr = useMaterialStore().getColumns;
      //   if (columnsStr && columnsStr.indexOf(',') > -1) {
      //     this.settingDataIndex = columnsStr.split(',');
      //   } else {
      //     this.settingDataIndex = this.defDataIndex;
      //   }
      //   this.columns = this.defColumns.filter((item) => {
      //     return this.settingDataIndex.includes(item.dataIndex);
      //   });
      // },
      // //列设置更改事件
      // onColChange(checkedValues) {
      //   this.columns = this.defColumns.filter((item) => {
      //     return checkedValues.includes(item.dataIndex);
      //   });
      //   let columnsStr = checkedValues.join();
      //   // Vue.ls.set('materialColumns', columnsStr);
      //   useMaterialStore().setColumns(columnsStr);
      // },
      // //恢复默认
      // handleRestDefault() {
      //   useMaterialStore().deleteColumns;
      //   // Vue.ls.remove('materialColumns');

      //   this.initColumnsSetting();
      // },
      loadTreeData() {
        let that = this;
        let params = {};
        params.id = '';
        queryMaterialCategoryTreeList(params).then((res) => {
          if (res) {
            that.categoryTree = [];
            for (let i = 0; i < res.length; i++) {
              let temp = res[i];
              that.categoryTree.push(temp);
            }
          }
        });
      },
      batchSetMaterialCurrentStock() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
        } else {
          let ids = '';
          for (let a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ',';
          }
          let that = this;
          this.$confirm({
            title: '确认操作',
            content: '是否操作选中数据?',
            onOk: function () {
              that.loading = true;
              postAction(that.url.batchSetMaterialCurrentStockUrl, { ids: ids })
                .then((res) => {
                  if (res.code === 200) {
                    that.$message.info('修正库存成功！');
                    that.loadData();
                    that.onClearSelected();
                  } else {
                    that.$message.warning(res.data.message);
                  }
                })
                .finally(() => {
                  that.loading = false;
                });
            },
          });
        }
      },
      batchSetMaterialCurrentUnitPrice() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
        } else {
          let ids = '';
          for (let a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ',';
          }
          let that = this;
          this.$confirm({
            title: '确认操作',
            content: '是否操作选中数据?',
            onOk: function () {
              that.loading = true;
              postAction(that.url.batchSetMaterialCurrentUnitPriceUrl, {
                ids: ids,
              })
                .then((res) => {
                  if (res.code === 200) {
                    that.$message.info('修正成本成功！');
                    that.loadData();
                    that.onClearSelected();
                  } else {
                    that.$message.warning(res.data.message);
                  }
                })
                .finally(() => {
                  that.loading = false;
                });
            },
          });
        }
      },
      batchEdit() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
        } else {
          let ids = '';
          for (let a = 0; a < this.selectedRowKeys.length; a++) {
            if (a === this.selectedRowKeys.length - 1) {
              ids += this.selectedRowKeys[a];
            } else {
              ids += this.selectedRowKeys[a] + ',';
            }
          }
          this.$refs.batchSetInfoModalForm.edit(ids);
          this.$refs.batchSetInfoModalForm.title = '批量编辑';
        }
      },
      handleAdd: function () {
        this.$refs.modalForm.action = 'add';
        this.$refs.modalForm.add();
        this.$refs.modalForm.title = '新增';
        this.$refs.modalForm.disableSubmit = false;
      },
      handleEdit: function (record) {
        this.$refs.modalForm.action = 'edit';
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = '编辑';
        this.$refs.modalForm.disableSubmit = false;
        if (this.btnEnableList.indexOf(1) === -1) {
          this.$refs.modalForm.showOkFlag = false;
        }
      },
      handleCopyAdd(record) {
        this.$refs.modalForm.action = 'copyAdd';
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = '复制新增';
        this.$refs.modalForm.disableSubmit = false;
      },
      getImgUrl(imgName, type) {
        if (imgName && imgName.split(',')) {
          type = type ? type + '/' : '';
          return getFileAccessHttpUrl('systemConfig/static/' + type + imgName.split(',')[0]);
        } else {
          return '';
        }
      },
      handleImportXls() {
        let importExcelUrl = this.url.importExcelUrl;
        let templateUrl = '/doc/goods_template.xls';
        let templateName = '商品Excel模板[下载]';
        this.$refs.modalImportForm.initModal(importExcelUrl, templateUrl, templateName);
        this.$refs.modalImportForm.title = '商品导入';
      },
      searchReset() {
        this.queryParam = {
          mpList: getMpListShort(useMaterialStore().getPropertyList), //扩展属性
        };
        this.loadData(1);
      },
    },
  };
</script>

<style scoped>
  @import '/src/assets/less/common.less';
</style>

<style>
  .item-info {
    float: left;
    width: 38px;
    height: 38px;
    margin-left: 6px;
  }
  .item-img {
    cursor: pointer;
    position: static;
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
</style>
// 添加临时样式检查表头是否被隐藏
<style scoped>
  ::v-deep .ant-table-thead {
    background-color: red !important; /* 调试用 */
  }
</style>
