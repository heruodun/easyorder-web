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
        <a-form-item name="address" label="地址" :rules="[{ required: true, message: '请输入地址', trigger: 'blur' }]">
          <AddressSelect ref="addressSelect" placeholder="请输入地址" v-model:value="formData.address" @update:value="onAddressSelect" />
        </a-form-item>
      </a-col>

      <a-col :span="8">
        <a-form-item name="spec" label="规格" :rules="[{ required: true, message: '请输入规格' }]">
          <a-input v-model:value="formData.spec" placeholder="请输入规格" />
        </a-form-item>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item name="remark" label="备注">
          <a-input v-model:value="formData.remark" placeholder="请输入备注" />
        </a-form-item>
      </a-col>

      <a-col :span="8" style="display: flex; align-items: center; justify-content: center">
        <a-statistic name="totalCount" title="总条数" :value="totalQuantity" :value-style="{ color: '#3f8600' }" />
      </a-col>
    </a-row>

    <!-- 第二行：表格和统计 -->
    <a-row :gutter="32">
      <a-col :span="15">
        <a-table ref="tableRef" :dataSource="formData.tableData" :pagination="false" rowKey="id" border>
          <a-table-column title="编号" dataIndex="id" key="id" />
          <a-table-column title="长度" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input-number string-mode v-model:value="record.length" @change="onInputChange" class="bold-microsoft-yahei" />
            </template>
          </a-table-column>
          <a-table-column title="条数" dataIndex="count" key="count">
            <template #default="{ record, index }">
              <a-input-number v-model:value="record.count" class="bold-number" />
            </template>
          </a-table-column>
        </a-table>
      </a-col>

      <a-col :span="9">
        <a-descriptions :title="`上一笔订单: ${orderDetail.orderId || ''}`" :column="2" size="middle" bordered>
          <!-- <template #extra>
      <a-button v-if="!orderDetail.publishFlag" type="primary" size="small" @click="onEdit">编辑</a-button>
    </template> -->
          <a-descriptions-item label="地址" span="2">{{ orderDetail.address }}</a-descriptions-item>
          <a-descriptions-item label="货物" span="2">
            <div v-if="orderDetail.guiges && orderDetail.guiges.length > 0" v-for="(item, index) in orderDetail.guiges" :key="index">
              <span style="color: green; font-weight: bold">{{ item.guige }}</span
              >：
              <span style="color: red; font-weight: bold">{{ item.count }}</span>
              {{ item.danwei }}

              <div
                v-if="item.tiaos && item.tiaos.length > 0"
                :title="item.tiaos.map((element) => `${element.length} x ${element.count}`).join('，')"
                color=""
              ></div>
              <div>
                {{ item.tiaos.map((element) => `${element.length} x ${element.count}`).join('，') }}
              </div>
            </div>

            <span v-if="orderDetail.remark !== ''" style="color: grey; font-style: italic">备注：{{ orderDetail.remark }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="当前状态">
            <a-tag
              :color="
                orderDetail.curStatus === '打单'
                  ? 'red'
                  : orderDetail.curStatus === '对接'
                  ? 'purple'
                  : orderDetail.curStatus === '配货'
                  ? 'yellow'
                  : orderDetail.curStatus === '拣货'
                  ? 'blue'
                  : orderDetail.curStatus === '送货'
                  ? 'green'
                  : 'black'
              "
            >
              {{ orderDetail.curStatus }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="当前处理时间">{{ orderDetail.curTime }}</a-descriptions-item>
          <a-descriptions-item label="订单轨迹" span="2">
            <div v-for="(item, index) in orderDetail.trace" :key="index">
              <a-tag
                :color="
                  item.operation === '打单'
                    ? 'red'
                    : item.operation === '对接'
                    ? 'purple'
                    : item.operation === '配货'
                    ? 'yellow'
                    : item.operation === '拣货'
                    ? 'blue'
                    : item.operation === '送货'
                    ? 'green'
                    : 'black'
                "
              >
                {{ item.operation }}
              </a-tag>

              {{ item.operator }} &nbsp; &nbsp; {{ item.time }}
            </div>
          </a-descriptions-item>
          <a-descriptions-item label="打单人">{{ orderDetail.creator }}</a-descriptions-item>
          <a-descriptions-item label="打单时间">{{ orderDetail.createTime }}</a-descriptions-item>
          <a-descriptions-item label="波次编号">{{ orderDetail.waveId }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ orderDetail.updateTime }}</a-descriptions-item>
        </a-descriptions>
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
  import AddressSelect from '/@/components/system/address-select/index.vue';
  import { message } from 'ant-design-vue';
  import { useUserStore } from '/@/store/modules/system/user';
  import { now } from 'lodash';
  import { orderApi } from '../../../../api/business/order/order-api';
  import { printT1 } from '/@/lib/smart-print.js';

  const formRef = ref(); // Create a reference to the form
  const tableRef = ref();
  const addressSelect = ref();

  const orderDetail = ref({});

  // 查询详情
  async function queryOrderDetail(value) {
    try {
      SmartLoading.show();
      let payload = {
        keywords: value,
        pageNum: 1,
        pageSize: 2,
        type: 1,
      };
      const result = await orderApi.queryPageSales(payload);
      console.log('queryOrderDetail ' + result.data);

      // 判空并取出第一个元素
      if (Array.isArray(result.data.list) && result.data.list.length > 0) {
        orderDetail.value = result.data.list[0]; // 取出第一个元素
      } else {
        orderDetail.value = {}; // 或者设定为其他适当的默认值
      }
      console.log('queryOrderDetail ' + orderDetail.value);
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  function orderPrint(time, orderId, orderIdStr) {
    const userStore = useUserStore(); // 使用你的 store
    let printData = {
      man: `经办人：${userStore.actualName}`, // 模板字符串用于结合变量和静态文本
      time: time,
      totalcount: `总条数：${totalQuantity.value}`, // 模板字符串用于结合变量和静态文本
      orderid: orderId,
      qrcodestr: orderIdStr,
      qrcodestr1: orderIdStr,
      address: formData.address,
      beizhu: formData.remark,
      guige: `规格：${formData.spec}`,
      table: formData.tableData.map((item) => ({
        id: String(item.id), // Convert id to string
        length: item.length === null ? '' : item.length, // Replace null with an empty string
        count: item.count === null ? '' : item.count, // Replace null with an empty string
      })),
    };
    // 打印
    printT1(printData);
  }

  const formData = reactive({
    address: '',
    spec: '',
    remark: '',
    tableData: Array.from({ length: 9 }, (_, i) => ({
      id: i + 1,
      length: null,
      count: null,
    })),
  });

  const onAddressSelect = (value) => {
    console.log('onAddressSelect ' + value);

    queryOrderDetail(value);
  };

  function resetForm() {
    // 重置简单表单字段
    addressSelect.value.reset();
    formData.spec = '';
    formData.remark = '';
    // 重置表格数据
    formData.tableData = formData.tableData.map((item, i) => ({
      id: i + 1,
      length: null,
      count: null,
    }));
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
        address: formData.address,
        remark: formData.remark,
        guiges: [
          {
            guige: formData.spec,
            count: totalQuantity.value,
            danwei: '条',
            tiaos: [],
          },
        ],
      };

      // 循环检查 formData.tableData 从第0到第9的元素
      for (let i = 0; i < Math.min(formData.tableData.length, 10); i++) {
        const item = formData.tableData[i];
        console.log(item);

        if (item && item.length !== '' && item.count !== '') {
          // 转换为数字
          const lengthNum = Number(item.length);
          const countNum = Number(item.count);

          // 检查转换后的值是否为有效的数字
          if (!isNaN(lengthNum) && !isNaN(countNum) && lengthNum > 0 && countNum > 0) {
            // 如果满足条件，则将该对象推入 tiaos 数组
            payload.guiges[0].tiaos.push({
              length: lengthNum,
              count: countNum,
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
        // 刷新上一次地址订单
        onAddressSelect(formData.address);
      } else {
        // This block will execute for any status code other than 201
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
    if ('#/business/oa/enterprise/dadan_1' == activeTabStore().getActive() && (event.ctrlKey || event.metaKey) && event.key === 'p') {
      console.log(currentUrl.value);
      event.preventDefault(); // 阻止默认行为，如此处为打印操作
      shortCutPressed();
    }
  }
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  const totalQuantity = computed(() => {
    return formData.tableData.reduce((acc, item) => acc + item.count, 0);
  });
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
