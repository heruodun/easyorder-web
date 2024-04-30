
<template>
  <a-form
      ref="formRef"
      :model="formData"
      name="basic"
      autocomplete="off"
      @finish="onFinish"
      @submit="handleSubmit"
      @finishFailed="onFinishFailed"
      layout="horizontal">

    <!-- 第一行：地址、备注 -->
    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item
            name="address"
            label="地址"
            :rules="[{ required: true, message: '请输入地址' }]">
          <!--          <a-select-->
          <!--              v-model:value="formData.address"-->
          <!--              show-search-->
          <!--              placeholder="请输入地址"-->
          <!--              style="width: 200px"-->
          <!--              :default-active-first-option="false"-->
          <!--              :show-arrow="false"-->
          <!--              :filter-option="false"-->
          <!--              :not-found-content="null"-->
          <!--              :options="data"-->
          <!--              @search="handleSearch"-->
          <!--              @change="handleChange"-->
          <!--          ></a-select>-->
          <a-input v-model:value="formData.address" placeholder="请输入地址" />

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
        <a-table
            :dataSource="formData.tableData"
            :pagination="false"
            rowKey="id"
            border>
          <a-table-column title="编号" dataIndex="id" key="id" />
          <a-table-column title="规格" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input string-mode v-model:value="record.guige" @change="onInputChange" style="width: 100%;" />
            </template>
          </a-table-column>
          <a-table-column title="数量" dataIndex="count" key="count">
            <template #default="{ record, index }">
              <a-input-number v-model:value="record.count" style="width: 100%;" />
            </template>
          </a-table-column>
          <a-table-column title="单位" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input string-mode v-model:value="record.danwei" @change="onInputChange" style="width: 100%;" />
            </template>
          </a-table-column>
        </a-table>
      </a-col>
    </a-row>

    <!-- 提交按钮 -->
    <a-form-item>
      <a-button
          type="primary"
          htmlType="submit">
        保存并打印
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup>
import { ref, reactive, computed} from 'vue';
import jsonp from 'fetch-jsonp';
import qs from 'qs';
import axios from "axios";
let timeout;
let currentValue = '';
import { useUserStore } from '/@/store/modules/system/user';
import {now} from "lodash";
import { orderApi } from '/src/api/business/oa/order-api';
import { printT2 } from '/@/lib/smart-print.js';


const formRef = ref(); // Create a reference to the form

function orderPrint(time, orderId, orderIdStr){
  const userStore = useUserStore(); // 使用你的 store
  let printData = {
    man: `经办人：${userStore.actualName}`, // 模板字符串用于结合变量和静态文本
    time: time,
    orderid:orderId,
    qrcodestr:orderIdStr,
    qrcodestr1:orderIdStr,
    address:`地址：${formData.address}`,
    beizhu:formData.remark,
    table:formData.tableData.map(item => ({
      id: String(item.id), // Convert id to string
      guige: item.guige === null ? "" : item.guige, // Replace null with an empty string
      count: item.count === null ? "" : item.count, // Replace null with an empty string
      danwei: item.danwei === null ? "" : item.danwei // Replace null with an empty string
    }))
  };
// 打印
  printT2(printData)
}

async function fetch(value, callback) {
  if (timeout) {
    clearTimeout(timeout);
  }

  currentValue = value;

  async function fake() {
    try {
      // 构造请求URL参数
      const params = qs.stringify({
        code: 'utf-8',
        q: value,
      });

      // 发起请求并等待返回
      const response = await orderApi.searchAddress(params);
      const results = response.data.result;

      // 检查当前值是否与请求发起时的值一致
      if (currentValue === value) {
        // 映射结果到我们想要的格式
        const data = results.map(r => ({
          value: r[0],
          label: r[0],
        }));

        callback(data);
      }
    } catch (error) {
      // 处理错误
      console.error('Error fetching data:', error);
    }
  }

  timeout = setTimeout(fake, 300);
}


const data = ref([]);
const value = ref();
const handleSearch = val => {
  fetch(val, d => (data.value = d));
};
const handleChange = val => {
  console.log(val);
  value.value = val;
  fetch(val, d => (data.value = d));
};

const formData = reactive({
  address: '',
  remark: '',
  tableData: Array.from({ length: 4 }, (_, i) => ({
    id: i + 1,
    guige: null,
    count: null,
    danwei: null
  }))
});

async function saveAndPrint() {
  try {
    SmartLoading.show();
    const userName = useUserStore().actualName;
    const time = now()

    let payload = {
      "data": [
        [
          "地址",
          formData.address,
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "序号",
          "规格",
          "",
          "",
          "数量",
          "",
          "单位",
          ""
        ],
        [
          "1",
          formData.tableData[0].guige,
          "",
          "",
          formData.tableData[0].count,
          "",
          formData.tableData[0].danwei,
          ""
        ],
        [
          "2",
          formData.tableData[1].guige,
          "",
          "",
          formData.tableData[1].count,
          "",
          formData.tableData[1].danwei,
          ""
        ],
        [
          "3",
          formData.tableData[2].guige,
          "",
          "",
          formData.tableData[2].count,
          "",
          formData.tableData[2].danwei,
          ""
        ],
        [
          "4",
          formData.tableData[3].guige,
          "",
          "",
          formData.tableData[3].count,
          "",
          formData.tableData[3].danwei,
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "备注",
          formData.remark,
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          ""
        ],
        [
          "经办人",
          userName,
          "",
          time,
          "",
          "20240409000020",
          "",
          ""
        ]
      ]
    }
    const response = await orderApi.create2(payload);
    console.log(response.data);
    if (response.status == 201) {
      const {create_time, order_id, qr_code} = response.data;
      orderPrint(create_time, order_id, qr_code);
      console.log(`打印成功: ${order_id}`)
    } else {
      // This block will execute for any status code other than 201
      const {error} = response.data;
      message.error(`表单提交失败: ${error}`); // Corrected to use template literals
      console.error(error);
    }

  } catch (exception) {
    console.error('提交失败:', exception);
    // It's a good practice to check if exception.response exists and has data
    const errorMessage = exception.response?.data?.error || 'Unknown error';
    message.error(`表单提交失败: ${errorMessage}`); // Corrected to use template literals
  }finally {
    SmartLoading.hide();
  }
}

function handleSubmit() {
  // This function can be used to programmatically trigger form submission
  // You might need to manually call validation here
  formRef.value.validate().then(() => {
    onFinish(formData); // Directly calling onFinish assuming validation is successful
  }).catch((err) => {
    console.error("Validation error:", err);
    onFinishFailed(err); // Handle validation errors
  });
}
const onFinish = async values => {
  console.log('Success:', values);
  await saveAndPrint();
};

import { onMounted, onBeforeUnmount } from 'vue';
import {SmartLoading} from "/@/components/framework/smart-loading/index.js";

const isActive = ref(false);
onMounted(() => {
  isActive.value = true;
  document.addEventListener('keydown', handleKeyPress);
  console.log("dadan2 mounted");
});

onBeforeUnmount(() => {
  isActive.value = false;
  console.log("dadan2 unmounted B");

  document.removeEventListener('keydown', handleKeyPress);
  console.log("dadan2 unmounted");

});

function handleKeyPress(event) {
  if (isActive.value && (event.ctrlKey || event.metaKey) && event.key === 'p') {
    event.preventDefault(); // 阻止默认行为，如此处为打印操作
    //add formdata submit validation
    handleSubmit()
  }
}
const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo);
};


const onInputChange = () => {
  // 这个函数将在输入值更改后被触发，以便重新计算总量
  // 由于我们使用了computed属性，当输入改变会自动更新totalQuantity
};
</script>