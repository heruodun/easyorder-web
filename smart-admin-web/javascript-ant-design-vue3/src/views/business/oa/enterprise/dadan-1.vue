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

    <!-- 第一行：地址、规格、备注 -->
    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item
            name="address"
            label="地址"
            :rules="[{ required: true, message: '请选择地址' }]">
          <a-select
              v-model:value="formData.address"
              show-search
              placeholder="请输入地址"
              style="width: 200px"
              :default-active-first-option="false"
              :show-arrow="false"
              :filter-option="false"
              :not-found-content="null"
              :options="data"
              @search="handleSearch"
              @change="handleChange"
          ></a-select>

        </a-form-item>
      </a-col>
      <a-col :span="8">
        <a-form-item
            name="spec"
            label="规格"
            :rules="[{ required: true, message: '请输入规格' }]">
          <a-input v-model:value="formData.spec" placeholder="请输入规格" />
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
          <a-table-column title="长度" dataIndex="length" key="length">
            <template #default="{ record, index }">
              <a-input-number string-mode v-model:value="record.length" @change="onInputChange" style="width: 100%;" />
            </template>
          </a-table-column>
          <a-table-column title="条数" dataIndex="count" key="count">
            <template #default="{ record, index }">
              <a-input-number v-model:value="record.count" style="width: 100%;" />
            </template>
          </a-table-column>
        </a-table>
      </a-col>
      <a-col :span="5" style="display: flex; align-items: center; justify-content: center;">
        <a-statistic
            title="总条数"
            :value="totalQuantity"
            :value-style="{ color: '#3f8600' }"/>
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
import {hiPrintPlugin } from 'vue-plugin-hiprint';
import mb1 from './mb1.json';
import { useUserStore } from '/@/store/modules/system/user';
import {now} from "lodash";

const formRef = ref(); // Create a reference to the form
hiprint.init();
hiprint.hiwebSocket.setHost("localhost:17521")

function orderPrint(time, orderId, orderIdStr){
  const userStore = useUserStore(); // 使用你的 store
  let printData = {
    man: `经办人：${userStore.actualName}`, // 模板字符串用于结合变量和静态文本
    time: time,
    totalcount: `总条数：${totalQuantity.value}`, // 模板字符串用于结合变量和静态文本
    orderid:orderId,
    qrcodestr:orderIdStr,
    qrcodestr1:orderIdStr,
    address:formData.address,
    beizhu:formData.remark,
    guige:formData.spec,
    table:formData.tableData.map(item => ({
      id: String(item.id), // Convert id to string
      length: item.length === null ? "" : item.length, // Replace null with an empty string
      count: item.count === null ? "" : item.count // Replace null with an empty string
    }))
  };
  let hiPrintTemplate = new hiprint.PrintTemplate({ template: mb1});
// 打印
  hiPrintTemplate.print2(printData);
}
// hiPrintPlugin.disAutoConnect();  //取消自动打印直接连接客户端


function fetch(value, callback) {
  if (timeout) {
    clearTimeout(timeout);
    timeout = null;
  }
  currentValue = value;
  function fake() {
    const str = qs.stringify({
      code: 'utf-8',
      q: value,
    });
    jsonp(`https://suggest.taobao.com/sug?${str}`)
        .then(response => response.json())
        .then(d => {
          if (currentValue === value) {
            const result = d.result;
            const data = [];
            result.forEach(r => {
              data.push({
                value: r[0],
                label: r[0],
              });
            });
            callback(data);
          }
        });
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
  spec: '',
  remark: '',
  tableData: Array.from({ length: 9 }, (_, i) => ({
    id: i + 1,
    length: null,
    count: null
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
          "",
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
          "规格",
          "序号",
          "长度",
          "",
          "条数",
          "备注",
          "",
          ""
        ],
        [
          "3.5",
          "1",
          formData.tableData[0].length,
          "",
          formData.tableData[0].count,
          formData.remark,
          "",
          ""
        ],
        [
          "",
          "2",
          formData.tableData[1].length,
          "",
          formData.tableData[1].count,
          "",
          "",
          ""
        ],
        [
          "",
          "3",
          formData.tableData[2].length,
          "",
          formData.tableData[2].count,
          "",
          "",
          ""
        ],
        [
          "",
          "4",
          formData.tableData[3].length,
          "",
          formData.tableData[3].count,
          "",
          "",
          ""
        ],
        [
          "",
          "5",
          formData.tableData[4].length,
          "",
          formData.tableData[4].count,
          "",
          "",
          ""
        ],
        [
          "",
          "6",
          formData.tableData[5].length,
          "",
          formData.tableData[5].count,
          "",
          "",
          ""
        ],
        [
          "",
          "7.",
          formData.tableData[6].length,
          "",
          formData.tableData[6].count,
          "",
          "",
          ""
        ],
        [
          "",
          "8.",
          formData.tableData[7].length,
          "",
          formData.tableData[7].count,
          "",
          "",
          ""
        ],
        [
          "",
          "9.",
          formData.tableData[8].length,
          "",
          formData.tableData[8].count,
          "",
          "",
          ""
        ],
        [
          "总条数",
          totalQuantity.value,
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
          "",
          "",
          ""
        ]
      ]
    }
    const response = await axios.post('http://127.0.0.1:5000/order1', payload, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log(response.data);
    if (response.status == 201) {
      const {create_time, order_id, qr_code} = response.data;
      orderPrint(create_time, order_id, qr_code);
      console.log("print success");
    } else {
      // This block will execute for any status code other than 201
      const {error} = response.data;
      alert(`表单提交失败: ${error}`); // Corrected to use template literals
      console.error(error);
    }

  } catch (exception) {
    console.error('提交失败:', exception);
    // It's a good practice to check if exception.response exists and has data
    const errorMessage = exception.response?.data?.error || 'Unknown error';
    alert(`表单提交失败: ${errorMessage}`); // Corrected to use template literals
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

onMounted(() => {
  document.addEventListener('keydown', handleKeyPress);
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeyPress);
});

function handleKeyPress(event) {
  if ((event.ctrlKey || event.metaKey) && event.key === 'p') {
    event.preventDefault(); // 阻止默认行为，如此处为打印操作
    //add formdata submit validation
    handleSubmit()
  }
}
const onFinishFailed = errorInfo => {
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
