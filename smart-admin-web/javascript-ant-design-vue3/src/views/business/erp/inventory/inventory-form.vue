<!--
  * 库存
  *
  * @Author:    dahang
  * @Date:      2024-12-12 23:48:08
  * @Copyright  dahang
-->
<template>
    <a-modal
      :title="form.id ? '编辑' : '添加'"
      width="300"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
    >
        <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
            <a-row>
                    <a-form-item label="编号"  name="id">
                        <a-input-number style="width: 100%" v-model:value="form.id" placeholder="编号" />
                    </a-form-item>
                    <a-form-item label="规格"  name="guige">
                        <a-input style="width: 100%" v-model:value="form.guige" placeholder="规格" />
                    </a-form-item>
                    <a-form-item label="数量"  name="count">
                        <a-input-number style="width: 100%" v-model:value="form.count" placeholder="数量" />
                    </a-form-item>
                    <a-form-item label="单位"  name="danwei">
                        <a-input style="width: 100%" v-model:value="form.danwei" placeholder="单位" />
                    </a-form-item>
                    <a-form-item label="明细"  name="detail">
                        <a-input style="width: 100%" v-model:value="form.detail" placeholder="明细" />
                    </a-form-item>
                    <a-form-item label="当前状态"  name="curStatus">
                        <a-input-number style="width: 100%" v-model:value="form.curStatus" placeholder="当前状态" />
                    </a-form-item>
                    <a-form-item label="当前处理时间"  name="curTime">
                        <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.curTime" style="width: 100%" placeholder="当前处理时间" />
                    </a-form-item>
                    <a-form-item label="当前处理人"  name="curMan">
                        <a-input style="width: 100%" v-model:value="form.curMan" placeholder="当前处理人" />
                    </a-form-item>
                    <a-form-item label="入库时间"  name="inTime">
                        <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.inTime" style="width: 100%" placeholder="入库时间" />
                    </a-form-item>
                    <a-form-item label="出库人"  name="outMan">
                        <a-input style="width: 100%" v-model:value="form.outMan" placeholder="出库人" />
                    </a-form-item>
                    <a-form-item label="出库时间"  name="outTime">
                        <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.outTime" style="width: 100%" placeholder="出库时间" />
                    </a-form-item>
                    <a-form-item label="订单号"  name="salesOrderId">
                        <a-input-number style="width: 100%" v-model:value="form.salesOrderId" placeholder="订单号" />
                    </a-form-item>
                    <a-form-item label="订单号"  name="productionOrderId">
                        <a-input-number style="width: 100%" v-model:value="form.productionOrderId" placeholder="订单号" />
                    </a-form-item>
                    <a-form-item label="创建时间"  name="createTime">
                        <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" />
                    </a-form-item>
                    <a-form-item label="更新时间"  name="updateTime">
                        <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%" placeholder="更新时间" />
                    </a-form-item>
            </a-row>

        </a-form>

        <template #footer>
            <a-space>
                <a-button @click="onClose">取消</a-button>
                <a-button type="primary" @click="onSubmit">保存</a-button>
            </a-space>
        </template>
    </a-modal>
</template>
<script setup>
    import { reactive, ref, nextTick } from 'vue';
    import _ from 'lodash';
    import { message } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { inventoryApi } from '/@/api/business/inventory/inventory-api';
    import { smartSentry } from '/@/lib/smart-sentry';

    // ------------------------ 事件 ------------------------

    const emits = defineEmits(['reloadList']);

    // ------------------------ 显示与隐藏 ------------------------
    // 是否显示
    const visibleFlag = ref(false);

    function show(rowData) {
        Object.assign(form, formDefault);
        if (rowData && !_.isEmpty(rowData)) {
            Object.assign(form, rowData);
        }
        visibleFlag.value = true;
        nextTick(() => {
            formRef.value.clearValidate();
        });
    }

    function onClose() {
        Object.assign(form, formDefault);
        visibleFlag.value = false;
    }

    // ------------------------ 表单 ------------------------

    // 组件ref
    const formRef = ref();

    const formDefault = {
        id: undefined,
        id: undefined, //编号
        guige: undefined, //规格
        count: undefined, //数量
        danwei: undefined, //单位
        detail: undefined, //明细
        curStatus: undefined, //当前状态
        curTime: undefined, //当前处理时间
        curMan: undefined, //当前处理人
        inTime: undefined, //入库时间
        outMan: undefined, //出库人
        outTime: undefined, //出库时间
        salesOrderId: undefined, //订单号
        productionOrderId: undefined, //订单号
        createTime: undefined, //创建时间
        updateTime: undefined, //更新时间
    };

    let form = reactive({ ...formDefault });

    const rules = {
        id: [{ required: true, message: '编号 必填' }],
    };

    // 点击确定，验证表单
    async function onSubmit() {
        try {
            await formRef.value.validateFields();
            save();
        } catch (err) {
            message.error('参数验证错误，请仔细填写表单数据!');
        }
    }

    // 新建、编辑API
    async function save() {
        SmartLoading.show();
        try {
            if (form.id) {
                await inventoryApi.update(form);
            } else {
                await inventoryApi.add(form);
            }
            message.success('操作成功');
            emits('reloadList');
            onClose();
        } catch (err) {
            smartSentry.captureError(err);
        } finally {
            SmartLoading.hide();
        }
    }

    defineExpose({
        show,
    });
</script>
