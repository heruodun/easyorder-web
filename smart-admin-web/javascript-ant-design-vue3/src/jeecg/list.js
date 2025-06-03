import { ref, reactive, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { filterObj, getMpListShort, getNowFormatStr } from '@/utils/util';
import { deleteRequest, getRequest, postAction, downFile, getFileAccessHttpUrl } from '/@/lib/axios-erp';

export function useJeecgList(options = {}) {
  const store = useStore();

  // 状态定义
  const tokenHeader = ref({ 'X-Access-Token': localStorage.getItem('ACCESS_TOKEN') });
  const cardStyle = ref('');
  const queryParam = reactive({});
  const dataSource = ref([]);
  const columns = ref([]);
  const settingDataIndex = ref([]);

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

  // 计算属性
  const isDesktop = computed(() => window.innerWidth > 768);

  // 方法
  const loadData = async (arg) => {
    if (!options.url?.list) {
      console.error('请设置url.list属性!');
      return;
    }

    if (arg === 1) {
      ipagination.current = 1;
    }

    const params = getQueryParams();
    loading.value = true;

    try {
      const res = await getAction(options.url.list, params);
      if (res.code === 200) {
        dataSource.value = res.data.rows.map((item) => ({
          ...item,
          receivableAmount: Math.min(item.receivableAmount ?? item.allNeed, item.allNeed),
        }));
        ipagination.total = res.data.total;
        tableAddTotalRow(columns.value, dataSource.value);
      } else if (res.code === 510) {
        console.warn(res.data);
      } else {
        console.warn(res.data?.message);
      }
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      loading.value = false;
      onClearSelected();
    }
  };

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

  const getQueryParams = () => {
    let sqp = {};
    if (superQueryParams.value) {
      sqp['superQueryParams'] = encodeURI(superQueryParams.value);
      sqp['superQueryMatchType'] = superQueryMatchType.value;
    }

    const searchObj = { search: JSON.stringify(queryParam) };
    const param = Object.assign(sqp, searchObj, isorter, filters);
    param.field = getQueryField();
    param.currentPage = ipagination.current;
    param.pageSize = ipagination.pageSize;

    return filterObj(param);
  };

  const getQueryField = () => {
    let str = 'id,';
    columns.value.forEach((value) => {
      str += `,${value.dataIndex}`;
    });
    return str;
  };

  const onClearSelected = () => {
    selectedRowKeys.value = [];
    selectionRows.value = [];
  };

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
        const res = await deleteAction(options.url.deleteBatch, { ids });
        if (res.code === 200) {
          loadData();
        } else {
          console.warn(res.data?.message);
        }
      } catch (error) {
        console.error('删除失败:', error);
      } finally {
        loading.value = false;
      }
    }
  };

  // 初始化函数
  const init = () => {
    if (isDesktop.value) {
      cardStyle.value = `height:${document.documentElement.clientHeight - 100}px`;
    }

    if (!disableMixinCreated.value) {
      loadData();
      initActiveBtnStr();
    }

    initScroll();
  };

  // 生命周期钩子
  onMounted(init);

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
    loadData,
    handleSuperQuery,
    getQueryParams,
    onClearSelected,
    batchDel,
    // 其他方法...
  };
}
