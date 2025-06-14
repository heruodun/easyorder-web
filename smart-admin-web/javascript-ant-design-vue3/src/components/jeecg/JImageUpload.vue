<template>
  <a-upload
    name="file"
    listType="picture-card"
    :multiple="isMultiple"
    :action="uploadAction"
    :headers="headers"
    :data="{ biz: bizPath }"
    :fileList="fileList"
    :beforeUpload="beforeUpload"
    :disabled="disabled"
    :isMultiple="isMultiple"
    :showUploadList="isMultiple"
    @change="handleChange"
    @preview="handlePreview"
  >
    <img v-if="!isMultiple && picUrl" :src="getAvatarView()" style="height: 104px; max-width: 300px" />
    <div v-else>
      <LoadingOutlined v-if="uploadLoading" />
      <PlusOutlined v-else />
      <div class="ant-upload-text">{{ text }}</div>
    </div>
    <a-modal :open="previewVisible" :width="1000" :footer="null" @cancel="handleCancel()">
      <img alt="预览图片" style="width: 100%" :src="previewImage" />
    </a-modal>
  </a-upload>
</template>

<script>
  import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer';
  import * as Vue from 'vue';
  import { ACCESS_TOKEN } from '/@/store/mutation-types';
  import { getFileAccessHttpUrl } from '/@/api/manage';
  import { fileSizeLimit } from '/@/api/api';
  import { useUserStore } from '/@/store/modules/system/user';

  const uidGenerator = () => {
    return '-' + parseInt(Math.random() * 10000 + 1, 10);
  };
  const getFileName = (path) => {
    if (path.lastIndexOf('\\') >= 0) {
      let reg = new RegExp('\\\\', 'g');
      path = path.replace(reg, '/');
    }
    return path.substring(path.lastIndexOf('/') + 1);
  };
  export default {
    name: 'JImageUpload',
    data() {
      return {
        uploadAction: import.meta.env.VITE_APP_ERP_API_URL + '/jshERP-boot/systemConfig/upload',
        uploadLoading: false,
        picUrl: false,
        headers: {},
        fileList: [],
        previewImage: '',
        previewVisible: false,
        sizeLimit: 0,
        uploadGoOn: true,
      };
    },
    props: {
      text: {
        type: String,
        required: false,
        default: '上传',
      },
      /*这个属性用于控制文件上传的业务路径*/
      bizPath: {
        type: String,
        required: false,
        default: 'temp',
      },
      value: {
        type: [String, Array],
        required: false,
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false,
      },
      isMultiple: {
        type: Boolean,
        required: false,
        default: false,
      },
    },
    watch: {
      value: {
        handler(val) {
          console.log('JImageUpload watch:', JSON.stringify(val));
          if (Array.isArray(val)) {
            this.initFileList(val.join(','));
          } else {
            this.initFileList(val);
          }
        },
        immediate: true, // 确保组件创建时立即执行
        deep: true, // 深度监听对象/数组变化
      },
    },
    created() {
      this.initFileSizeLimit();
      const token = useUserStore().getErpToken;
      this.headers = { 'X-Erp-Access-Token': token };
    },
    methods: {
      initFileSizeLimit() {
        fileSizeLimit().then((res) => {
          if (res.code === 200) {
            this.sizeLimit = res.data;
          }
        });
      },
      initFileList(paths) {
        console.log('JImageUpload value changed:', JSON.stringify(paths));
        if (!paths || paths.length == 0) {
          this.fileList = [];
          this.picUrl = false;
          return;
        }
        this.picUrl = true;
        let fileList = [];
        let arr = paths.split(',');
        for (var a = 0; a < arr.length; a++) {
          let url = getFileAccessHttpUrl('systemConfig/static/' + arr[a]);
          fileList.push({
            uid: uidGenerator(),
            name: getFileName(arr[a]),
            status: 'done',
            url: url,
            response: {
              code: 'history',
              data: arr[a],
            },
          });
        }
        this.fileList = fileList;
      },
      beforeUpload: function (file) {
        this.uploadGoOn = true;
        let fileType = file.type;
        let fileSize = file.size;
        if (fileType.indexOf('image') < 0) {
          this.$message.warning('请上传图片');
          this.uploadGoOn = false;
          return false;
        }
        //验证文件大小
        if (fileSize > this.sizeLimit / 10) {
          let parseSizeLimit = (this.sizeLimit / 1024 / 1024 / 10).toFixed(2);
          this.$message.warning('抱歉，图片大小不能超过' + parseSizeLimit + 'M');
          this.uploadGoOn = false;
          return false;
        }
        return true;
      },
      handleChange(info) {
        console.log('--文件列表改变--');
        if (!info.file.status && this.uploadGoOn === false) {
          info.fileList.pop();
        }
        this.picUrl = false;
        let fileList = info.fileList;
        if (info.file.status === 'done') {
          if (info.file.response.code === 200) {
            this.picUrl = true;
            fileList = fileList.map((file) => {
              if (file.response) {
                file.url = file.response.data;
              }
              return file;
            });
          }
          //this.$message.success(`${info.file.name} 上传成功!`);
        } else if (info.file.status === 'error') {
          this.$message.error(`${info.file.name} 上传失败.`);
        } else if (info.file.status === 'removed') {
          this.handleDelete(info.file);
        }
        this.fileList = fileList;
        if (info.file.status === 'done' || info.file.status === 'removed') {
          this.handlePathChange();
        }
      },
      // 预览
      handlePreview(file) {
        this.previewImage = file.url || file.thumbUrl;
        this.previewVisible = true;
      },
      getAvatarView() {
        if (this.fileList.length > 0) {
          let url = this.fileList[0].url;
          return url;
        }
      },
      handlePathChange() {
        let uploadFiles = this.fileList;
        let path = '';
        if (!uploadFiles || uploadFiles.length == 0) {
          path = '';
        }
        let arr = [];
        if (!this.isMultiple) {
          arr.push(uploadFiles[uploadFiles.length - 1].response.data);
        } else {
          for (var a = 0; a < uploadFiles.length; a++) {
            arr.push(uploadFiles[a].response.data);
          }
        }
        if (arr.length > 0) {
          path = arr.join(',');
        }
        console.log('New path:', path, 'Old value:', this.value);

        this.initFileList(path);
        this.$emit('change', path);
      },
      handleDelete(file) {
        //如有需要新增 删除逻辑
        console.log(file);
      },
      handleCancel() {
        this.close();
        this.previewVisible = false;
      },
      close() {},
    },
    model: {
      prop: 'value',
      event: 'change',
    },
    emits: ['change', 'update:value'],
  };
</script>

<style scoped></style>
