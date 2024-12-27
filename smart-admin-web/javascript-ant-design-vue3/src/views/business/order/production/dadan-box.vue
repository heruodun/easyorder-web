<!-- 框子打单 -->
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
    <!-- 第一行：地址、规格、备注 -->
    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item name="guige" label="规格" :rules="[{ required: true, message: '请输入规格' }]">
          <a-input v-model:value="formData.guige" placeholder="规格" />
        </a-form-item>
      </a-col>
      <a-col :span="8">
        <a-form-item name="temperature" label="温度" :rules="[{ required: true, message: '请输入温度' }]">
          <a-input-number v-model:value="formData.temperature" placeholder="温度" />
        </a-form-item>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item name="printCount" label="打印数量" :rules="[{ required: true, message: '请输入规格' }]">
          <!-- todo 判断下数量，一次性不能打印超过多少 -->
          <a-input-number v-model:value="formData.printCount" :min="1" placeholder="打印数量" />
        </a-form-item>
      </a-col>
    </a-row>

    <!-- 提交按钮 -->
    <a-form-item :wrapper-col="{ span: 14, offset: 4 }">
      <a-button type="primary" htmlType="submit">保存并打印（Ctr+P） </a-button>
      <a-button style="margin-left: 10px" @click="resetForm">一键清空</a-button>
    </a-form-item>
  </a-form>
</template>

<script setup>
  import { ref, reactive, computed } from 'vue';
  import { message } from 'ant-design-vue';
  import { useUserStore } from '/@/store/modules/system/user';
  import { now } from 'lodash';
  import { orderApi } from '../../../../api/business/order/order-api';
  import { printT1 } from '/@/lib/smart-print.js';
  const formRef = ref(); // Create a reference to the form
  import { printProductionBox } from '/@/lib/smart-print.js';

  function orderPrint(time, orderId, orderIdStr) {
    const userStore = useUserStore(); // 使用你的 store
    let printData = {
      orderid: orderId,
      qrcodestr: orderIdStr,
      orderid2: orderId,
      qrcodestr2: orderIdStr,
      remark: formData.temperature + '°',
      count: '框',
      guige: formData.guige,
    };
    // 打印
    printProductionBox(printData);
  }

  const formData = reactive({
    guige: '',
    temperature: '',
    workerNo: '',
    printCount: undefined,
  });

  function resetForm() {
    formData.guige = '';
    formData.temperature = '';
    formData.workerNo = '';
    formData.printCount = undefined;
  }

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
        remark: formData.temperature,
        guige: formData.guige,
        count: 1,
        danwei: '框',
        type: 3,
      };

      for (var i = 0; i < formData.printCount; i++) {
        const response = await orderApi.createProduction(payload);

        console.log(response);

        if (response.code == 0) {
          const createTime = response.data.createTime;
          const orderId = response.data.orderId;
          const qrCode = response.data.qrCode;
          orderPrint(createTime, orderId, qrCode);
          message.info(`打印成功: ${orderId}`);
          console.log(`打印成功: ${orderId}`);
        } else {
          // This block will execute for any status code other than 201
          const { error } = response.data;
          message.error(`表单提交失败: ${error}`); // Corrected to use template literals
          console.error(error);
        }
      }

      SmartLoading.hide();
      message.success(`${formData.printCount} 张全部打印成功！！`);
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
        console.log('validate ok');

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

  const currentUrl = ref('');

  onMounted(() => {
    activeTabStore().setActive(window.location.hash);
    document.addEventListener('keydown', handleKeyPress);
  });

  onBeforeUnmount(() => {
    document.removeEventListener('keydown', handleKeyPress);
    activeTabStore().setActive(null);
  });

  const isSubmitting = ref(false); // 控制提交状态的变量

  function handleKeyPress(event) {
    if ('#/business/order/production/dadan_box' == activeTabStore().getActive() && (event.ctrlKey || event.metaKey) && event.key === 'p') {
      console.log(currentUrl.value);
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
  .bold-microsoft-yahei {
    width: 100%;
    font-family: 'Microsoft YaHei', sans-serif;
    font-weight: 700; /* 或者 700 */
    font-size: 25px; /* 设置你需要的字体大小 */
  }

  .bold-number {
    width: 100%;
    font-weight: 700; /* 或者 700 */
    font-size: 25px; /* 设置你需要的字体大小 */
  }
</style>
