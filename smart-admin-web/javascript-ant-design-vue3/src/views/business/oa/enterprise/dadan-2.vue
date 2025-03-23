<template>
  <a-form
    ref="formRef"
    :model="formData"
    name="basic"
    autocomplete="off"
    @finish="onFinish"
    @finishFailed="onFinishFailed"
    @keydown.enter.prevent="noOperation"
    layout="horizontal"
  >
    <!-- 第一行：地址、备注 -->
    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item name="address" label="地址" :rules="[{ required: true, message: '请输入地址' }]">
          <AddressSelect ref="addressSelect" placeholder="请输入地址" v-model:value="formData.address" />
        </a-form-item>
      </a-col>

      <a-col :span="8">
        <a-form-item label="备注">
          <a-input v-model:value="formData.remark" placeholder="请输入备注" />
        </a-form-item>
      </a-col>
    </a-row>

    <!-- 第二行：表格和统计 -->
    <a-row :gutter="16">
      <a-col :span="19">
        <a-table :dataSource="formData.tableData" :pagination="false" rowKey="id" border>
          <a-table-column title="编号" dataIndex="id" key="id" />
          <a-table-column title="规格" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input string-mode class="bold-microsoft-yahei" v-model:value="record.guige" @change="onInputChange" style="width: 100%" />
            </template>
          </a-table-column>
          <a-table-column title="数量" dataIndex="count" key="count">
            <template #default="{ record, index }">
              <a-input-number class="bold-microsoft-yahei" v-model:value="record.count" style="width: 100%" />
            </template>
          </a-table-column>
          <a-table-column title="单位" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input string-mode class="bold-microsoft-yahei" v-model:value="record.danwei" @change="onInputChange" style="width: 100%" />
            </template>
          </a-table-column>
        </a-table>
      </a-col>
    </a-row>

    <!-- 提交按钮 -->
    <a-form-item>
      <a-button type="primary" htmlType="submit"> 保存并打印（Ctr+P） </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup>
  import { ref, reactive, computed } from 'vue';
  import jsonp from 'fetch-jsonp';
  import qs from 'qs';
  import axios from 'axios';
  let timeout;
  let currentValue = '';
  import { useUserStore } from '/@/store/modules/system/user';
  import { message } from 'ant-design-vue';
  import { now } from 'lodash';
  import { orderApi } from '../../../../api/business/order/order-api';
  import { printT2 } from '/@/lib/smart-print.js';

  const formRef = ref(); // Create a reference to the form

  function orderPrint(time, orderId, orderIdStr) {
    const userStore = useUserStore(); // 使用你的 store
    let printData = {
      man: `经办人：${userStore.actualName}`, // 模板字符串用于结合变量和静态文本
      time: time,
      orderid: orderId,
      qrcodestr: orderIdStr,
      qrcodestr1: orderIdStr,
      address: formData.address,
      beizhu: formData.remark,
      table: formData.tableData.map((item) => ({
        id: String(item.id), // Convert id to string
        guige: item.guige === null ? '' : item.guige, // Replace null with an empty string
        count: item.count === null ? '' : item.count, // Replace null with an empty string
        danwei: item.danwei === null ? '' : item.danwei, // Replace null with an empty string
      })),
    };
    // 打印
    printT2(printData);
  }

  const formData = reactive({
    address: '',
    remark: '',
    tableData: Array.from({ length: 4 }, (_, i) => ({
      id: i + 1,
      guige: null,
      count: null,
      danwei: null,
    })),
  });

  const isSubmitting = ref(false); // 控制提交状态的变量

  async function saveAndPrint() {
    try {
      if (isSubmitting.value) {
        message.error(`提交太频繁，稍后重试`);
        return;
      }
      SmartLoading.show();
      isSubmitting.value = true; // 开始提交，设置状态为true以避免重复提交
      //检查打印机客户端是否连接上
      if (hiprint.hiwebSocket.opened) {
        console.log('已连接打印客户端');
      } else {
        message.error('打印客户端未连接，请启动桌面上打印客户端');
        return;
      }

      let payload = {
        address: formData.address,
        remark: formData.remark,
        guiges: [],
      };

      // 循环检查 formData.tableData 从第0到第9的元素
      for (let i = 0; i < Math.min(formData.tableData.length, 10); i++) {
        const item = formData.tableData[i];
        console.log(item);

        if (item && item.guige !== '' && item.count !== '' && item.danwei !== '') {
          // 转换为数字
          const countNum = Number(item.count);

          // 检查转换后的值是否为有效的数字
          if (!isNaN(countNum) && countNum >= 0 && item.guige !== null && item.guige !== '' && item.danwei !== null && item.danwei !== '') {
            // 如果满足条件，则将该对象推入 guiges 数组
            payload.guiges.push({
              guige: item.guige,
              count: countNum,
              danwei: item.danwei,
            });
          }
        }
      }

      const response = await orderApi.createSales(payload);

      console.log(response);

      if (response.code == 0) {
        const createTime = response.data.createTime;
        const orderId = response.data.orderId;
        const qrCode = response.data.qrCode;
        orderPrint(createTime, orderId, qrCode);
        message.info(`打印成功: ${orderId}`);
        console.log(`打印成功: ${orderId}`);
      } else {
        const { error } = response.data;
        message.error(`表单提交失败: ${error}`); // Corrected to use template literals
        console.error(error);
      }
    } catch (exception) {
      console.error('提交失败:', exception);
      // It's a good practice to check if exception.response exists and has data
      const errorMessage = exception.response?.data?.error || 'Unknown error';
      message.error(`表单提交失败: ${errorMessage}`); // Corrected to use template literals
    } finally {
      SmartLoading.hide();
      isSubmitting.value = false; // 无论成功或失败，重置提交状态
    }
  }

  function noOperation() {}

  function shortCutPressed() {
    formRef.value
      .validate()
      .then(() => {
        saveAndPrint();
      })
      .catch((err) => {
        onFinishFailed(err); // Handle validation errors
      });
  }
  const onFinish = (values) => {
    console.log('Success:', values);
    saveAndPrint();
  };

  import { onMounted, onBeforeUnmount } from 'vue';
  import { SmartLoading } from '/@/components/framework/smart-loading/index.js';
  import { activeTabStore } from '/@/store/modules/system/tab';
  import AddressSelect from '/@/components/system/address-select/index-no-deleted.vue';
  onMounted(() => {
    activeTabStore().setActive(window.location.hash);
    document.addEventListener('keydown', handleKeyPress);
  });

  onBeforeUnmount(() => {
    document.removeEventListener('keydown', handleKeyPress);
    activeTabStore().setActive(null);
  });

  function handleKeyPress(event) {
    if ('#/business/oa/enterprise/dadan_2' == activeTabStore().getActive() && (event.ctrlKey || event.metaKey) && event.key === 'p') {
      event.preventDefault(); // 阻止默认行为，如此处为打印操作
      shortCutPressed();
    }
  }
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  const onInputChange = () => {
    // 这个函数将在输入值更改后被触发，以便重新计算总量
    // 由于我们使用了computed属性，当输入改变会自动更新totalQuantity
  };
</script>

<style scoped>
  .static-data {
    font-size: 30px; /* 设置你需要的字体大小 */
  }

  .bold-microsoft-yahei {
    width: 100%;
    font-family: 'Microsoft YaHei', sans-serif;
    font-weight: 700; /* 或者 700 */
    font-size: 25px; /* 设置你需要的字体大小 */
  }
</style>
