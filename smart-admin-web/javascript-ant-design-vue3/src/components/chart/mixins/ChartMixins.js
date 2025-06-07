import { $on, $off, $once, $emit } from '../../../utils/gogocodeTransfer'
export const ChartEventMixins = {
  methods: {
    handleClick(event, chart) {
      this.handleEvent('click', event, chart)
    },
    handleEvent(eventName, event, chart) {
      $emit(this, eventName, event, chart)
    },
  },
}
