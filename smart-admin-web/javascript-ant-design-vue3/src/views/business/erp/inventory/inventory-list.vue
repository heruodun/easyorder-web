<!--
  * 库存
  *
  * @Author:    dahang
  * @Date:      2024-12-12 23:48:08
  * @Copyright  dahang
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="规格" class="smart-query-form-item">
                <a-input style="width: 150px" v-model:value="queryForm.guige" placeholder="规格" />
            </a-form-item>
            <a-form-item label="开始时间" class="smart-query-form-item">
                <a-range-picker v-model:value="queryForm.createTime" :presets="defaultTimeRanges" style="width: 150px" @change="onChangeCreateTime" />
            </a-form-item>
            <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="queryData">
                    <template #icon>
                        <SearchOutlined />
                    </template>
                    查询
                </a-button>
                <a-button @click="resetQuery" class="smart-margin-left10">
                    <template #icon>
                        <ReloadOutlined />
                    </template>
                    重置
                </a-button>
            </a-form-item>
        </a-row>
    </a-form>
    <!---------- 查询表单form end ----------->

    <a-card size="small" :bordered="false" :hoverable="true">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <a-button @click="showForm" type="primary" size="small">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新建
                </a-button>
                <a-button @click="confirmBatchDelete" type="danger" size="small" :disabled="selectedRowKeyList.length == 0">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    批量删除
                </a-button>
            </div>
            <div class="smart-table-setting-block">
                <TableOperator v-model="columns" :tableId="null" :refresh="queryData" />
            </div>
        </a-row>
        <!---------- 表格操作行 end ----------->

        <!---------- 表格 begin ----------->
        <a-table
                size="small"
                :dataSource="tableData"
                :columns="columns"
                rowKey="id"
                bordered
                :loading="tableLoading"
                :pagination="false"
                :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
        >
            <template #bodyCell="{ text, record, column }">
                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link">编辑</a-button>
                        <a-button @click="onDelete(record)" danger type="link">删除</a-button>
                    </div>
                </template>
            </template>
        </a-table>
        <!---------- 表格 end ----------->

        <div class="smart-query-table-page">
            <a-pagination
                    showSizeChanger
                    showQuickJumper
                    show-less-items
                    :pageSizeOptions="PAGE_SIZE_OPTIONS"
                    :defaultPageSize="queryForm.pageSize"
                    v-model:current="queryForm.pageNum"
                    v-model:pageSize="queryForm.pageSize"
                    :total="total"
                    @change="queryData"
                    @showSizeChange="queryData"
                    :show-total="(total) => `共${total}条`"
            />
        </div>

        <InventoryForm  ref="formRef" @reloadList="queryData"/>

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { inventoryApi } from '/@/api/business/inventory/inventory-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import InventoryForm from './inventory-form.vue';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '编号',
            dataIndex: 'id',
            ellipsis: true,
        },
        {
            title: '规格',
            dataIndex: 'guige',
            ellipsis: true,
        },
        {
            title: '数量',
            dataIndex: 'count',
            ellipsis: true,
        },
        {
            title: '单位',
            dataIndex: 'danwei',
            ellipsis: true,
        },
        {
            title: '明细',
            dataIndex: 'detail',
            ellipsis: true,
        },
        {
            title: '当前状态',
            dataIndex: 'curStatus',
            ellipsis: true,
        },
        {
            title: '当前处理时间',
            dataIndex: 'curTime',
            ellipsis: true,
        },
        {
            title: '当前处理人',
            dataIndex: 'curMan',
            ellipsis: true,
        },
        {
            title: '入库时间',
            dataIndex: 'inTime',
            ellipsis: true,
        },
        {
            title: '出库人',
            dataIndex: 'outMan',
            ellipsis: true,
        },
        {
            title: '出库时间',
            dataIndex: 'outTime',
            ellipsis: true,
        },
        {
            title: '订单号',
            dataIndex: 'salesOrderId',
            ellipsis: true,
        },
        {
            title: '订单号',
            dataIndex: 'productionOrderId',
            ellipsis: true,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            ellipsis: true,
        },
        {
            title: '修改时间',
            dataIndex: 'updateTime',
            ellipsis: true,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 90,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        guige: undefined, //规格
        createTime: [], //开始时间
        createTimeBegin: undefined, //开始时间 开始
        createTimeEnd: undefined, //开始时间 结束
        pageNum: 1,
        pageSize: 10,
    };
    // 查询表单form
    const queryForm = reactive({ ...queryFormState });
    // 表格加载loading
    const tableLoading = ref(false);
    // 表格数据
    const tableData = ref([]);
    // 总数
    const total = ref(0);

    // 重置查询条件
    function resetQuery() {
        let pageSize = queryForm.pageSize;
        Object.assign(queryForm, queryFormState);
        queryForm.pageSize = pageSize;
        queryData();
    }

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await inventoryApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    function onChangeCreateTime(dates, dateStrings){
        queryForm.createTimeBegin = dateStrings[0];
        queryForm.createTimeEnd = dateStrings[1];
    }


    onMounted(queryData);

    // ---------------------------- 添加/修改 ----------------------------
    const formRef = ref();

    function showForm(data) {
        formRef.value.show(data);
    }

    // ---------------------------- 单个删除 ----------------------------
    //确认删除
    function onDelete(data){
        Modal.confirm({
            title: '提示',
            content: '确定要删除选吗?',
            okText: '删除',
            okType: 'danger',
            onOk() {
                requestDelete(data);
            },
            cancelText: '取消',
            onCancel() {},
        });
    }

    //请求删除
    async function requestDelete(data){
        SmartLoading.show();
        try {
            let deleteForm = {
                goodsIdList: selectedRowKeyList.value,
            };
            await inventoryApi.delete(data.id);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }

    // ---------------------------- 批量删除 ----------------------------

    // 选择表格行
    const selectedRowKeyList = ref([]);

    function onSelectChange(selectedRowKeys) {
        selectedRowKeyList.value = selectedRowKeys;
    }

    // 批量删除
    function confirmBatchDelete() {
        Modal.confirm({
            title: '提示',
            content: '确定要批量删除这些数据吗?',
            okText: '删除',
            okType: 'danger',
            onOk() {
                requestBatchDelete();
            },
            cancelText: '取消',
            onCancel() {},
        });
    }

    //请求批量删除
    async function requestBatchDelete() {
        try {
            SmartLoading.show();
            await inventoryApi.batchDelete(selectedRowKeyList.value);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }
</script>
