<template>
  <a-popover
    trigger="click"
    placement="bottom"
    :autoAdjustOverflow="true"
    :arrowPointAtCenter="true"
    overlayClassName="header-notice-wrapper"
    @visibleChange="handleHoverChange"
    :overlayStyle="{ width: '300px', top: '50px' }"
  >
    <template v-slot:content>
      <a-spin :spinning="loadding">
        <a-tabs>
          <a-tab-pane :tab="msg1Title" key="1">
            <a-list>
              <a-list-item
                :key="index"
                v-for="(record, index) in announcement1"
              >
                <div style="margin-left: 5%; width: 80%">
                  <p>
                    <a @click="showAnnouncement(record)">{{
                      record.msgTitle
                    }}</a>
                  </p>
                  <p style="color: rgba(0, 0, 0, 0.45); margin-bottom: 0px">
                    {{ record.createTimeStr }}
                  </p>
                </div>
              </a-list-item>
              <div style="margin-top: 5px; text-align: center">
                <a-button @click="toMyAnnouncement()" type="dashed" block
                  >查看更多</a-button
                >
              </div>
            </a-list>
          </a-tab-pane>
        </a-tabs>
      </a-spin>
    </template>
    <span @click="fetchNotice" class="header-notice">
      <a-badge :count="msgTotal">
        <a-icon style="font-size: 16px; padding: 4px" type="bell" />
      </a-badge>
    </span>
    <show-announcement
      ref="ShowAnnouncement"
      @ok="modalFormOk"
    ></show-announcement>
    <dynamic-notice
      ref="showDynamNotice"
      :path="openPath"
      :formData="formData"
    />
    <msg-list ref="modalList" @close="modalListCancel()"></msg-list>
  </a-popover>
</template>

<script>
import { getAction, postAction } from '@/api/manage'
import ShowAnnouncement from './ShowAnnouncement'
import store from '@/store/'
import DynamicNotice from './DynamicNotice'
import MsgList from '@/views/system/MsgList'

export default {
  name: 'HeaderNotice',
  components: {
    DynamicNotice,
    ShowAnnouncement,
    MsgList,
  },
  data() {
    return {
      loadding: false,
      url: {
        getMsgByStatus: '/msg/getMsgByStatus',
        batchUpdateStatus: '/msg/batchUpdateStatus',
        queryById: '/sys/annountCement/queryById',
      },
      hovered: false,
      announcement1: [],
      msg1Count: '0',
      msg1Title: '通知(0)',
      stopTimer: false,
      websock: null,
      lockReconnect: false,
      heartCheck: null,
      formData: {},
      openPath: '',
    }
  },
  computed: {
    msgTotal() {
      return parseInt(this.msg1Count)
    },
  },
  mounted() {
    this.loadData()
    //this.timerFun();
    //this.initWebSocket(); //注释by jishenghua  2021年1月13日
    // this.heartCheckFun();
  },
  destroyed: function () {
    // 离开页面生命周期函数
    //this.websocketclose();
  },
  methods: {
    timerFun() {
      this.stopTimer = false
      let myTimer = setInterval(() => {
        // 停止定时器
        if (this.stopTimer == true) {
          clearInterval(myTimer)
          return
        }
        this.loadData()
      }, 6000)
    },
    loadData() {
      try {
        // 获取系统消息
        getAction(this.url.getMsgByStatus, { status: '1' })
          .then((res) => {
            if (res && res.code === 200) {
              this.announcement1 = res.data
              if (this.announcement1.length > 5) {
                this.announcement1 = this.announcement1.reverse()
                this.announcement1 = this.announcement1.slice(0, 5)
              }
              this.msg1Count = res.data.length
              this.msg1Title = '通知(' + res.data.length + ')'
            }
          })
          .catch((error) => {
            console.log('系统消息通知异常', error) //这行打印permissionName is undefined
            this.stopTimer = true
            console.log('清理timer')
          })
      } catch (err) {
        this.stopTimer = true
        console.log('通知异常', err)
      }
    },
    fetchNotice() {
      if (this.loadding) {
        this.loadding = false
        return
      }
      this.loadding = true
      setTimeout(() => {
        this.loadding = false
      }, 200)
    },
    showAnnouncement(record) {
      postAction(this.url.batchUpdateStatus, {
        ids: record.id,
        status: '2',
      }).then((res) => {
        if (res && res.code === 200) {
          this.loadData()
        }
      })
      this.hovered = false
      this.$refs.ShowAnnouncement.detail(record)
    },
    toMyAnnouncement() {
      this.$refs.modalList.handleDetail()
    },
    modalFormOk() {},
    modalListCancel() {
      this.loadData()
    },
    handleHoverChange(visible) {
      this.hovered = visible
    },

    initWebSocket: function () {
      // WebSocket与普通的请求所用协议有所不同，ws等同于http，wss等同于https
      var userId = store.getters.userInfo.id
      var url =
        window._CONFIG['domianURL']
          .replace('https://', 'wss://')
          .replace('http://', 'ws://') +
        '/websocket/' +
        userId
      console.log(url)
      this.websock = new WebSocket(url)
      this.websock.onopen = this.websocketOnopen
      this.websock.onerror = this.websocketOnerror
      this.websock.onmessage = this.websocketOnmessage
      this.websock.onclose = this.websocketOnclose
    },
    websocketOnopen: function () {
      console.log('WebSocket连接成功')
      //心跳检测重置
      //this.heartCheck.reset().start();
    },
    websocketOnerror: function (e) {
      console.log('WebSocket连接发生错误')
      this.reconnect()
    },
    websocketOnmessage: function (e) {
      console.log('-----接收消息-------', e.data)
      var data = eval('(' + e.data + ')') //解析对象
      if (data.cmd == 'topic') {
        //系统通知
        this.loadData()
      } else if (data.cmd == 'user') {
        //用户消息
        this.loadData()
      }
      //心跳检测重置
      //this.heartCheck.reset().start();
    },
    websocketOnclose: function (e) {
      console.log('connection closed (' + e.code + ')')
      this.reconnect()
    },
    websocketSend(text) {
      // 数据发送
      try {
        this.websock.send(text)
      } catch (err) {
        console.log('send failed (' + err.code + ')')
      }
    },

    openNotification(data) {
      var text = data.msgTxt
      const key = `open${Date.now()}`
      this.$notification.open({
        message: '消息提醒',
        placement: 'bottomRight',
        description: text,
        key,
        btn: (h) => {
          return h(
            'a-button',
            {
              props: {
                type: 'primary',
                size: 'small',
              },
              on: {
                click: () => this.showDetail(key, data),
              },
            },
            '查看详情'
          )
        },
      })
    },

    reconnect() {
      var that = this
      if (that.lockReconnect) return
      that.lockReconnect = true
      //没连接上会一直重连，设置延迟避免请求过多
      setTimeout(function () {
        console.info('尝试重连...')
        that.initWebSocket()
        that.lockReconnect = false
      }, 5000)
    },
    heartCheckFun() {
      var that = this
      //心跳检测,每20s心跳一次
      that.heartCheck = {
        timeout: 20000,
        timeoutObj: null,
        serverTimeoutObj: null,
        reset: function () {
          clearTimeout(this.timeoutObj)
          //clearTimeout(this.serverTimeoutObj);
          return this
        },
        start: function () {
          var self = this
          this.timeoutObj = setTimeout(function () {
            //这里发送一个心跳，后端收到后，返回一个心跳消息，
            //onmessage拿到返回的心跳就说明连接正常
            that.websocketSend('HeartBeat')
            console.info('客户端发送心跳')
            //self.serverTimeoutObj = setTimeout(function(){//如果超过一定时间还没重置，说明后端主动断开了
            //  that.websock.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
            //}, self.timeout)
          }, this.timeout)
        },
      }
    },

    showDetail(key, data) {
      this.$notification.close(key)
      var id = data.msgId
      getAction(this.url.queryById, { id: id }).then((res) => {
        if (res.success) {
          var record = res.result
          this.showAnnouncement(record)
        }
      })
    },
  },
}
</script>

<style lang="css">
.header-notice-wrapper {
  top: 50px !important;
}
</style>

<style lang="less" scoped>
.header-notice {
  display: inline-block;
  transition: all 0.3s;

  span {
    vertical-align: initial;
  }
}
</style>
