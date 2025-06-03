import { ref, reactive, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { filterObj, getMpListShort, getNowFormatStr } from '/@/utils/util';
import { deleteRequest, getRequest, postRequest, downFile, downFilePost, getFileAccessHttpUrl } from '/@/lib/axios-erp';
import { useDevice } from '/@/utils/device-util';
import VueDraggableResizable from 'vue-draggable-resizable';
import { useUserStore } from '/@/store/modules/system/user';
import { columnSettingStore } from '/@/store/modules/system/column';

export function useJeecgList(options = {}) {
  // 引入设备相关功能
  const { device, isDesktop } = useDevice();
  // 响应式状态定义
  const tokenHeader = ref({ 'X-erp-Access-Token': useUserStore().getToken });
  const cardStyle = ref('');
  const queryParam = reactive({});
  const dataSource = ref([]);
  const columns = ref(options.columns || []);
  const settingDataIndex = ref([]);
  const defDataIndex = ref([...options.columns]); // 备份默认配置

  const ipagination = reactive({
    current: 1,
    pageSize: 10,
    pageSizeOptions: ['10', '20', '30', '50', '100'],
    showTotal: (total, range) => `${range[0]}-${range[1]} 共${total}条`,
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  });

  const scroll = ref({});
  const isorter = reactive({
    column: 'createTime',
    order: 'desc',
  });

  const filters = reactive({});
  const loading = ref(false);
  const selectedRowKeys = ref([]);
  const selectionRows = ref([]);
  const toggleSearchStatus = ref(false);
  const superQueryFlag = ref(false);
  const superQueryParams = ref('');
  const superQueryMatchType = ref('and');
  const disableMixinCreated = ref(options.disableMixinCreated || false);
  const btnEnableList = ref('');

  // 创建组件引用
  const modalForm = options.modalFormRef || ref(null);

  // 核心方法：加载数据
  const loadData = async (arg) => {
    if (!options.url?.list) {
      console.error('请设置url.list属性!');
      return;
    }

    console.info('loadData-------------------');

    if (arg === 1) {
      ipagination.current = 1;
    }

    const params = getQueryParams();
    loading.value = true;

    try {
      const res = await getRequest(options.url.list, params);
      if (res.code === 200) {
        dataSource.value = res.data.rows.map((item) => ({
          ...item,
          receivableAmount: Math.min(typeof item.receivableAmount === 'undefined' ? item.allNeed : item.receivableAmount, item.allNeed),
        }));
        ipagination.total = res.data.total;
        tableAddTotalRow(columns.value, dataSource.value);
      } else if (res.code === 510) {
        console.warn(res.data);
      } else {
        console.warn(res.data?.message || res.message);
      }

      console.info('loadData dataSource-------------------' + JSON.stringify(dataSource.value));
      console.info('loadData columns-------------------' + JSON.stringify(columns.value));
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      loading.value = false;
      onClearSelected();
    }
  };

  const initDictConfig = () => {
    //console.log("--这是一个假的方法!")
  };

  // 高级查询处理
  const handleSuperQuery = (params, matchType) => {
    if (!params) {
      superQueryParams.value = '';
      superQueryFlag.value = false;
    } else {
      superQueryFlag.value = true;
      superQueryParams.value = JSON.stringify(params);
      superQueryMatchType.value = matchType;
    }
    loadData(1);
  };

  // 获取查询参数
  const getQueryParams = () => {
    let sqp = {};
    if (superQueryParams.value) {
      sqp['superQueryParams'] = encodeURI(superQueryParams.value);
      sqp['superQueryMatchType'] = superQueryMatchType.value;
    }

    const param = Object.assign(sqp, { search: JSON.stringify(queryParam) }, isorter, filters);

    param.field = getQueryField();
    param.currentPage = ipagination.current;
    param.pageSize = ipagination.pageSize;

    return filterObj(param);
  };

  // 获取查询字段
  const getQueryField = () => {
    let str = 'id,';
    columns.value.forEach((value) => {
      str += `,${value.dataIndex}`;
    });
    return str;
  };

  const onSelectChange = (selectedRowKeys, selectionRows) => {
    selectedRowKeys.value = selectedRowKeys;
    selectionRows.value = selectionRows;
  };

  // 清空选择
  const onClearSelected = () => {
    selectedRowKeys.value = [];
    selectionRows.value = [];
  };

  const searchQuery = () => loadData(1);

  const searchReset = () => {
    Object.keys(queryParam).forEach((key) => delete queryParam[key]);
    loadData(1);
  };

  // 批量设置状态方法
  const batchSetStatus = (status) => {
    // 1. 检查 URL 配置
    if (!url.value.batchSetStatusUrl) {
      message.error('请设置url.batchSetStatusUrl属性!');
      return;
    }

    // 2. 检查选中记录
    if (selectedRowKeys.value.length <= 0) {
      message.warning('请选择一条记录！');
      return;
    }

    // 3. 准备数据
    const ids = selectedRowKeys.value.join(',');

    // 4. 弹出确认框
    Modal.confirm({
      title: '确认操作',
      content: '是否操作选中数据?',
      onOk: async () => {
        try {
          loading.value = true;

          // 5. 发送请求
          const res = await postRequest(url.value.batchSetStatusUrl, { status, ids });

          // 6. 处理响应
          if (res.code === 200) {
            message.success('操作成功');
            loadData(); // 重新加载数据
          } else {
            message.warning(res.data?.message || '操作失败');
          }
        } catch (error) {
          console.error('操作异常:', error);
          message.error('请求异常');
        } finally {
          loading.value = false;
        }
      },
    });
  };

  // 批量删除
  const batchDel = async () => {
    if (!options.url?.deleteBatch) {
      console.error('请设置url.deleteBatch属性!');
      return;
    }

    if (selectedRowKeys.value.length <= 0) {
      console.warn('请选择一条记录！');
      return;
    }

    const ids = selectedRowKeys.value.join(',');

    if (confirm('是否删除选中数据?')) {
      loading.value = true;
      try {
        const res = await deleteRequest(options.url.deleteBatch, { ids });
        if (res.code === 200) {
          loadData();
        } else {
          console.warn(res.data?.message || res.message);
        }
      } finally {
        loading.value = false;
      }
    }
  };

  // 编辑方法
  const handleEdit = (record) => {
    if (modalForm.value) {
      modalForm.value.edit(record); // 调用子组件方法
      modalForm.value.title = '编辑'; // 设置子组件属性
      modalForm.value.disableSubmit = false;
    }
  };

  // 新增方法
  const handleAdd = () => {
    if (modalForm.value) {
      modalForm.value.add();
      modalForm.value.title = '新增';
      modalForm.value.disableSubmit = false;
    }
  };

  const handleDelete = async (id) => {
    if (!options.url?.delete) {
      console.error('请设置url.delete属性!');
      return;
    }
    try {
      const res = await deleteRequest(options.url.delete, { id });
      if (res.code === 200) {
        loadData();
      } else {
        console.warn(res.data?.message || res.message);
      }
    } catch (error) {
      console.error('删除失败:', error);
    }
  };

  // 初始化函数
  const init = () => {
    if (isDesktop.value) {
      cardStyle.value = `height:${document.documentElement.clientHeight - 100}px`;
    }

    if (!disableMixinCreated.value) {
      loadData();
      initDictConfig();
      initActiveBtnStr();
    }

    initScroll();
  };

  // 生命周期钩子
  onMounted(init);

  const initActiveBtnStr = () => {
    // 初始化按钮权限
    const btnStrList = JSON.parse(localStorage.getItem('winBtnStrList') || '[]');
    const pathName = window.location.pathname;
    const pluginPath = pathName.includes('/plugins') ? '/system' + pathName : pathName;

    const btnConfig = btnStrList.find((item) => item.url === pluginPath);
    btnEnableList.value = btnConfig?.btnStr || '';
  };

  const initScroll = () => {
    if (useDevice().isMobile) {
      scroll.value.y = '';
    } else {
      let searchWrapperDomLen = 0;
      let operatorDomLen = 0;

      // 计算DOM高度
      const searchWrapperDom = document.querySelector('.table-page-search-wrapper');
      const operatorDom = document.querySelector('.table-operator');

      if (searchWrapperDom) searchWrapperDomLen = searchWrapperDom.offsetHeight;
      if (operatorDom) operatorDomLen = operatorDom.offsetHeight + 10;

      scroll.value.x = document.documentElement.clientWidth - 150 - 64;
      scroll.value.y = document.documentElement.clientHeight - searchWrapperDomLen - operatorDomLen - 274;
    }
  };

  const handleTableChange = (pagination, filters, sorter) => {
    if (Object.keys(sorter).length > 0) {
      if (sorter.order) {
        isorter.column = sorter.field;
        isorter.order = sorter.order === 'ascend' ? 'asc' : 'desc';
      } else {
        isorter.column = 'createTime';
        isorter.order = 'desc';
      }
    }

    if (pagination?.current) {
      Object.assign(ipagination, pagination);
    }

    loadData();
  };

  const handleToggleSearch = () => {
    toggleSearchStatus.value = !toggleSearchStatus.value;
  };

  // 给popup查询使用(查询区域不支持回填多个字段，限制只返回一个字段)
  const getPopupField = (fields) => {
    return fields.split(',')[0];
  };

  const modalFormOk = () => loadData();

  const modalFormClose = () => loadData();

  const handleChangeOtherField = (showQuery) => {
    const mpStr = getMpListShort(localStorage.getItem('materialPropertyList'));
    if (mpStr) {
      const mpArr = mpStr.split(',');
      if (mpArr.length === 3 && showQuery) {
        columns.value.forEach((col) => {
          if (col.dataIndex === 'otherField1') col.title = mpArr[0];
          if (col.dataIndex === 'otherField2') col.title = mpArr[1];
          if (col.dataIndex === 'otherField3') col.title = mpArr[2];
        });
      }
    }
  };
  const paginationChange = (page, pageSize) => {
    ipagination.current = page;
    ipagination.pageSize = pageSize;
    loadData();
  };
  const paginationShowSizeChange = (current, size) => {
    ipagination.current = current;
    ipagination.pageSize = size;
    loadData();
  };

  //恢复默认
  const handleRestDefault = () => {
    Vue.ls.remove(this.pageName);
    initColumnsSetting();
  };

  const visible = ref(false);
  const confirmLoading = ref(false);

  const handleImportExcel = (info) => {
    confirmLoading.value = true;

    if (info.file.status === 'done') {
      if (info.file.response) {
        if (info.file.response.code === 200) {
          message.success(info.file.response.data || `${info.file.name} 文件上传成功`);
        } else {
          message.warning(info.file.response.data, 8);
        }
        confirmLoading.value = false;
        visible.value = false;
        emit('ok'); // 使用 defineEmits 触发事件
      } else {
        message.error(`${info.file.name} ${info.file.response?.data || '未知错误'}`);
        confirmLoading.value = false;
      }
    } else if (info.file.status === 'error') {
      message.error(`文件上传失败: ${info.file.msg || '服务器错误'}`);
      confirmLoading.value = false;
    }
  };

  const getImgView = (text) => {
    if (!text) return '';
    const cleanText = text.split(',')[0]; // 取第一个路径
    return getFileAccessHttpUrl(cleanText);
  };

  const uploadFile = (text) => {
    if (!text) {
      message.warning('未知的文件');
      return;
    }

    const cleanText = text.split(',')[0]; // 取第一个路径
    const url = getFileAccessHttpUrl(cleanText);

    // 创建隐藏的iframe实现下载
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = url;
    document.body.appendChild(iframe);

    setTimeout(() => {
      document.body.removeChild(iframe);
    }, 3000);
  };

  const initColumnsSetting = () => {
    const setting = columnSettingStore().getColumnSetting();
    if (setting && setting.value.includes(',')) {
      settingDataIndex.value = storedColumns.value.split(',');
    } else {
      settingDataIndex.value = [...defDataIndex.value];
    }

    columns.value = defColumns.value.filter((item) => settingDataIndex.value.includes(item.dataIndex));
  };

  const onColChange = (checkedValues) => {
    columns.value = defColumns.value.filter((item) => checkedValues.includes(item.dataIndex));

    settingDataIndex.value = [...checkedValues];

    columnSettingStore().setColumnSetting(checkedValues.join(','));
  };

  // 响应式状态
  const exportLoading = ref(false);

  const handleExportXlsPost = async (fileName = '导出文件', title, head, tip, list) => {
    exportLoading.value = true;
    try {
      const paramObj = { title, head, tip, list };
      const data = await downFilePost(paramObj);

      if (!data) {
        message.warning('文件下载失败');
        return;
      }

      const blob = new Blob([data], { type: 'application/vnd.ms-excel' });
      const downloadUrl = URL.createObjectURL(blob);

      // 创建下载链接
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = `${fileName}_${getNowFormatStr()}.xls`;
      document.body.appendChild(link);
      link.click();

      // 清理
      setTimeout(() => {
        document.body.removeChild(link);
        URL.revokeObjectURL(downloadUrl);
        exportLoading.value = false;
      }, 100);
    } catch (error) {
      message.error(`导出失败: ${error.message}`);
      exportLoading.value = false;
    }
  };
  /* 导出 */
  const handleExportXls2 = () => {
    let paramsStr = encodeURI(JSON.stringify(this.getQueryParams()));
    let url = `${window._CONFIG['domianURL']}/${url.exportXlsUrl}?paramsStr=${paramsStr}`;
    window.location.href = url;
  };

  const handleExportXls = async (fileName = '导出文件') => {
    if (!options.url?.exportXlsUrl) {
      console.error('请设置url.exportXlsUrl属性!');
      return;
    }

    const param = { ...queryParam };
    if (selectedRowKeys.value.length > 0) {
      param.selections = selectedRowKeys.value.join(',');
    }

    try {
      const data = await downFile(options.url.exportXlsUrl, param);
      if (!data) {
        console.warn('文件下载失败');
        return;
      }

      const blob = new Blob([data], { type: 'application/vnd.ms-excel' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');

      link.href = url;
      link.download = `${fileName}_${getNowFormatStr()}.xls`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    } catch (error) {
      console.error('导出失败:', error);
    }
  };

  //拖拽组件
  const handleDrag = (column) => {
    return {
      header: {
        cell: (h, props, children) => {
          const { key, ...restProps } = props;
          // 父表格列宽拖拽逻辑
          const col = column.find((col) => {
            const k = col.dataIndex || col.key;
            return k === key;
          });
          if (!col || !col.width) {
            return h('th', { ...restProps }, children);
          }

          const dragProps = {
            key: col.dataIndex || col.key,
            class: 'table-draggable-handle',
            attrs: {
              w: 10,
              x: col.width,
              z: 1,
              axis: 'x',
              draggable: true,
              resizable: false,
            },
            on: {
              dragging: (x, y) => {
                col.width = Math.max(x, 1);
              },
            },
          };
          const drag = h(VueDraggableResizable, { ...dragProps });
          return h('th', { ...restProps, class: 'resize-table-th' }, [children, drag]);
        },
      },
    };
  };

  // 表格增加合计行
  const tableAddTotalRow = (columns, dataSource) => {
    if (dataSource.length > 0 && ipagination.pageSize % 10 === 1) {
      const numKey = 'rowIndex';
      const totalRow = { [numKey]: '合计' };
      const parseCols =
        'initialStock,currentStock,currentStockPrice,currentWeight,initialAmount,thisMonthAmount,currentAmount,inSum,inSumPrice,inOutSumPrice,outSum,outSumPrice,outInSumPrice,operNumber,allPrice,numSum,priceSum,prevSum,thisSum,thisAllPrice,changeAmount,allPrice,taxMoney,currentNumber,lowCritical,highCritical,preNeed,debtMoney,backMoney,allNeed,needDebt,realNeedDebt,finishDebt,debt,';

      columns.forEach((column) => {
        const { dataIndex } = column;
        if (dataIndex !== numKey && parseCols.includes(`${dataIndex},`)) {
          let total = 0;
          dataSource.forEach((data) => {
            const value = parseFloat(data[dataIndex]);
            total += isNaN(value) ? 0 : value;
          });
          totalRow[dataIndex] = total.toFixed(2);
        }
      });

      dataSource.push(totalRow);
      const size = Math.ceil(ipagination.total / (ipagination.pageSize - 1));
      ipagination.total += size;
    }
  };

  // 返回所有状态和方法
  return {
    tokenHeader,
    cardStyle,
    queryParam,
    dataSource,
    columns,
    settingDataIndex,
    ipagination,
    scroll,
    isorter,
    filters,
    loading,
    selectedRowKeys,
    selectionRows,
    toggleSearchStatus,
    superQueryFlag,
    superQueryParams,
    superQueryMatchType,
    btnEnableList,
    isDesktop,
    VueDraggableResizable,

    // 方法
    loadData,
    handleSuperQuery,
    getQueryParams,
    onClearSelected,
    batchDel,
    initDictConfig,
    initActiveBtnStr,
    initScroll,
    handleTableChange,
    handleExportXls,
    handleAdd,
    tableAddTotalRow,
    searchQuery,
    searchReset,
    handleDelete,
    modalFormOk,
    modalFormClose,
    handleChangeOtherField,
    paginationChange,
    paginationShowSizeChange,
  };
}
