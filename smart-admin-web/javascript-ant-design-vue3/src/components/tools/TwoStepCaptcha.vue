<template>
  <!-- 两步验证 -->
  <a-modal
    centered
    v-model:value="visible"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <template v-slot:title>
      <div :style="{ textAlign: 'center' }">两步验证</div>
    </template>
    <template v-slot:footer>
      <div :style="{ textAlign: 'center' }">
        <a-button key="back" @click="handleCancel">返回</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="stepLoading"
          @click="handleStepOk"
        >
          继续
        </a-button>
      </div>
    </template>

    <a-spin :spinning="stepLoading">
      <a-form
        layout="vertical"
        :auto-form-create="
          (form) => {
            this.form = form
          }
        "
      >
        <div class="step-form-wrapper">
          <p style="text-align: center" v-if="!stepLoading">
            请在手机中打开 Google Authenticator 或两步验证 APP<br />输入 6
            位动态码
          </p>
          <p style="text-align: center" v-else>正在验证..<br />请稍后</p>
          <a-form-item
            :style="{ textAlign: 'center' }"
            hasFeedback
            fieldDecoratorId="stepCode"
            :fieldDecoratorOptions="{
              rules: [
                {
                  required: true,
                  message: '请输入 6 位动态码!',
                  pattern: /^\d{6}$/,
                  len: 6,
                },
              ],
            }"
          >
            <a-input
              :style="{ textAlign: 'center' }"
              @keyup.enter="handleStepOk"
              placeholder="000000"
            />
          </a-form-item>
          <p style="text-align: center">
            <a @click="onForgeStepCode">遗失手机?</a>
          </p>
        </div>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
export default {
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      stepLoading: false,

      form: null,
    }
  },
  methods: {
    handleStepOk() {
      const vm = this
      this.stepLoading = true
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log('values', values)
          setTimeout(() => {
            vm.stepLoading = false
            $emit(vm, 'success', { values })
          }, 2000)
          return
        }
        this.stepLoading = false
        $emit(this, 'error', { err })
      })
    },
    handleCancel() {
      this.visible = false
      $emit(this, 'cancel')
    },
    onForgeStepCode() {},
  },
  emits: ['success', 'error', 'cancel'],
}
</script>

<style lang="less" scoped>
.step-form-wrapper {
  margin: 0 auto;
  width: 80%;
  max-width: 400px;
}
</style>
