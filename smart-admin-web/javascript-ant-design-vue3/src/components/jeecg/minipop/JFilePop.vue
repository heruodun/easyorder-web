<template>
  <div>
    <a-modal title="文件上传" :width="width" :open="visible" @ok="ok" cancelText="取消" @cancel="close">
      <!--style="top: 20px;"-->
      <j-upload :file-type="fileType" :value="filePath" @change="handleChange" :disabled="disabled"></j-upload>
    </a-modal>
  </div>
</template>

<script>
  import { $on, $off, $once, $emit } from '../../../utils/gogocodeTransfer';
  import JUpload from '/@/components/jeecg/JUpload.vue';
  import { getFileAccessHttpUrl } from '/@/api/manage';

  const getFileName = (path) => {
    if (path.lastIndexOf('\\') >= 0) {
      let reg = new RegExp('\\\\', 'g');
      path = path.replace(reg, '/');
    }
    return path.substring(path.lastIndexOf('/') + 1);
  };

  export default {
    name: 'JFilePop',
    components: { JUpload },
    props: {
      title: {
        type: String,
        default: '',
        required: false,
      },
      position: {
        type: String,
        default: 'right',
        required: false,
      },
      height: {
        type: Number,
        default: 200,
        required: false,
      },
      width: {
        type: Number,
        default: 520,
        required: false,
      },

      popContainer: {
        type: String,
        default: '',
        required: false,
      },
      disabled: {
        type: Boolean,
        default: false,
        required: false,
      },
    },
    data() {
      return {
        visible: false,
        filePath: '',
        id: '',
        fileType: 'file',
      };
    },
    methods: {
      handleChange(value) {
        this.filePath = value;
      },
      show(id, value, flag) {
        this.id = id;
        this.filePath = value;
        this.visible = true;
        if (flag === 'img') {
          this.fileType = 'image';
        } else {
          this.fileType = 'file';
        }
      },
      ok() {
        if (!this.filePath) {
          this.$message.error('未上传任何文件');
          return false;
        }
        let arr = this.filePath.split(',');
        let obj = {
          name: getFileName(arr[0]),
          url: getFileAccessHttpUrl(arr[0]),
          path: this.filePath,
          status: 'done',
          id: this.id,
        };
        $emit(this, 'ok', obj);
        this.visible = false;
      },
      close() {
        this.visible = false;
      },
    },
    emits: ['ok'],
  };
</script>

<style scoped></style>
