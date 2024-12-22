/*
 * 订单
 *
 */

export const ORDER_TYPE_ENUM = {
  PRODUCTION_BUCKET: {
    value: 2,
    desc: '白桶订单',
  },
  PRODUCTION_BOX: {
    value: 3,
    desc: '框子订单',
  },

  PRODUCTION_BAG: {
    value: 4,
    desc: '摇摆订单',
  },

  PRODUCTION_DISK: {
    value: 5,
    desc: '盘带订单',
  },
};

export default {
  ORDER_TYPE_ENUM,
};
