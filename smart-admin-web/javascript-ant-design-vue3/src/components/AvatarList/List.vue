<script>
import * as Vue from 'vue'
import Avatar from 'ant-design-vue/es/avatar'
import AvatarItem from './Item'

export default {
  AvatarItem,
  name: 'AvatarList',
  components: {
    Avatar,
    AvatarItem,
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-avatar-list',
    },
    /**
     * 头像大小 类型: large、small 、mini, default
     * 默认值: default
     */
    size: {
      type: [String, Number],
      default: 'default',
    },
    /**
     * 要显示的最大项目
     */
    maxLength: {
      type: Number,
      default: 0,
    },
    /**
     * 多余的项目风格
     */
    excessItemsStyle: {
      type: Object,
      default: () => {
        return {
          color: '#f56a00',
          backgroundColor: '#fde3cf',
        }
      },
    },
  },
  data() {
    return {}
  },
  methods: {
    getItems(items) {
      const classString = {
        [`${this.prefixCls}-item`]: true,
        [`${this.size}`]: true,
      }

      if (this.maxLength > 0) {
        items = items.slice(0, this.maxLength)
        items.push(
          <Avatar
            size={this.size}
            style={this.excessItemsStyle}
          >{`+${this.maxLength}`}</Avatar>
        )
      }
      const itemList = items.map((item) => <li class={classString}>{item}</li>)
      return itemList
    },
  },
  render() {
    const { prefixCls, size } = this.$props
    const classString = {
      [`${prefixCls}`]: true,
      [`${size}`]: true,
    }
  },
}
</script>
