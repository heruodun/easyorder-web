<!--
  * 地址补全选择框
-->
<template>
  <a-auto-complete
    v-model="selectAddress"
    :options="options"
    :style="`width: ${width}`"
    :placeholder="props.placeholder"
    :allowClear="true"
    @select="onSelect"
    @search="onSearch"
    @change="handleChange"
  />
</template>

<script setup>
  import { ref, watch } from 'vue';
  import { orderApi } from '/@/api/business/oa/order-api.js';
  let timeout;
  let currentValue = '';
  const options = ref([]);

  // =========== 属性定义 和 事件方法暴露 =============

  const props = defineProps({
    value: String,
    placeholder: {
      type: String,
      default: '请选择地址',
    },
    width: {
      type: String,
      default: '100%',
    },
    size: {
      type: String,
      default: 'default',
    },
  });

  const emit = defineEmits(['update:value']);

  const onSearch = (searchText) => {
    console.log('onSearch ' + searchText);
    fetch(searchText, (d) => (options.value = d));
  };
  const onSelect = (value) => {
    console.log('onSelect ' + value);
    emit('update:value', value); // 跟上面类似，确保在选择时更新外部 v-model 绑定的值
  };

  const handleChange = (val) => {
    console.log('handleChange ' + val);
    fetch(val, (d) => (options.value = d));
    emit('update:value', val); // emit an event for v-model binding
  };

  async function fetch(value, callback) {
    if (timeout) {
      clearTimeout(timeout);
    }
    currentValue = value;

    async function fake() {
      try {
        // 构造请求URL参数
        let params = {
          code: 'utf-8',
          key: value,
        };

        // 发起请求并等待返回
        const response = await orderApi.searchAddress(params);
        const results = response.data.result || []; // 确保 results 不是 undefined

        // 转换数据格式
        const formattedResults = results.map((item) => ({ value: item }));

        // 检查当前值是否与请求发起时的值一致
        if (currentValue === value) {
          // 使用转换后的结果调用回调

          callback(formattedResults);
        }
      } catch (error) {
        // 处理错误
        console.error('Error fetching data:', error);
      }
    }

    // 设置延迟以防止过于频繁的请求
    timeout = setTimeout(fake, 300);
  }

  // =========== 选择 监听、事件 =============

  const selectAddress = ref(props.value);

  watch(
    () => props.value,
    (newValue) => {
      console.log(newValue + ' watch');
      selectAddress.value = newValue;
    }
  );

  function reset() {
    console.log('reset');
    emit('update:value', '');
  }

  defineExpose({
    reset,
  });
</script>
