<template>
  <a-form
      ref="formRef"

      :model="formData"
      name="basic"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      @keydown.enter.prevent="noOperation"
      layout="horizontal">

    <!-- 第一行：地址、规格、备注 -->
    <a-row :gutter="16">
      <a-col :span="8">
        <a-form-item
            name="address"
            label="地址"
            :rules="[{ required: true, message: '请输入地址',trigger: 'blur' }]">
          <AddressSelect ref="addressSelect"

                         placeholder="请输入地址"
                         v-model:value="formData.address" />
        </a-form-item>
      </a-col>

      <a-col :span="2">
        <div>
          <a target="_blank"
             href="https://c1um9dohzwz.feishu.cn/base/YfZ0bTG5pahNddsk3VLcTXVwn3o?table=tblGkLjcHmOfHG1Y&view=vewVGgt3EA">
            添加地址</a>
        </div>
      </a-col>
      <a-col :span="5" >
        <a-form-item
            name="spec"
            label="规格"
            :rules="[{ required: true, message: '请输入规格' }]">

          <a-input  v-model:value="formData.spec"  placeholder="请输入规格" />
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
              <a-input-number string-mode v-model:value="record.length" @change="onInputChange"
                              class="bold-microsoft-yahei"
                              />
            </template>
          </a-table-column>
          <a-table-column title="条数" dataIndex="count" key="count">
            <template #default="{ record, index }">
              <a-input-number v-model:value="record.count" class="bold-microsoft-yahei" />
            </template>
          </a-table-column>
        </a-table>
      </a-col>
      <a-col :span="5" style="display: flex; align-items: center; justify-content: center;">
        <a-statistic
            title="总条数"
            :value="totalQuantity"
            :value-style="{color: '#3f8600'}"/>
      </a-col>
    </a-row>

    <!-- 提交按钮 -->
    <a-form-item>
      <a-button
          type="primary"
          htmlType="submit">
        保存并打印（Ctr+P）
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup>
import { ref, reactive, computed} from 'vue';
import AddressSelect from '/@/components/system/address-select/index.vue';
import { message } from 'ant-design-vue';
import { useUserStore } from '/@/store/modules/system/user';
import {now} from "lodash";
import { orderApi } from '/src/api/business/oa/order-api';
import { printT1 } from '/@/lib/smart-print.js';

const formRef = ref(); // Create a reference to the form
const addressSelect = ref();

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
    guige:`规格：${formData.spec}`,
    table:formData.tableData.map(item => ({
      id: String(item.id), // Convert id to string
      length: item.length === null ? "" : item.length, // Replace null with an empty string
      count: item.count === null ? "" : item.count // Replace null with an empty string
    }))
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
    count: null
  }))
});

async function saveAndPrint() {
  try {
    SmartLoading.show();
    //检查打印机客户端是否连接上
    if (hiprint.hiwebSocket.opened) {
      console.log("已连接打印客户端")
    }
    else{
      message.error('打印客户端未连接，请启动桌面上打印客户端');
      return;
    }
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
          formData.spec,
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

    const response = await orderApi.create1(payload);
    console.log(response.data);
    if (response.status == 201) {
      const {create_time, order_id, qr_code} = response.data;
      orderPrint(create_time, order_id, qr_code);
      message.info(`打印成功: ${order_id}`)
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

function noOperation(){

}

function shortCutPressed() {
  formRef.value.validate().then(() => {
    console.log("validate ok");
    saveAndPrint()
  }).catch((err) => {
    onFinishFailed(err); // Handle validation errors
  });
}
const onFinish = values => {
  console.log('Success:', values);
  saveAndPrint()
};

import { onMounted, onBeforeUnmount } from 'vue';
import {SmartLoading} from "/@/components/framework/smart-loading/index.js";
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

function handleKeyPress(event) {
  if (('#/business/oa/enterprise/dadan_1' == activeTabStore().getActive()) && (event.ctrlKey || event.metaKey) && event.key === 'p') {
    console.log(currentUrl.value);
    event.preventDefault(); // 阻止默认行为，如此处为打印操作
    shortCutPressed()
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

<style scoped>
.static-data{
  font-size: 30px; /* 设置你需要的字体大小 */
}

.bold-microsoft-yahei {
  width: 100%;
  font-family: "Microsoft YaHei", sans-serif;
  font-weight: 700; /* 或者 700 */
  font-size: 25px; /* 设置你需要的字体大小 */
}

</style>