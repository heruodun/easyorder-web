/*
 * 订单
 *
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const orderApi = {
  // 销售单创单
  createSales: (param) => {
    return postRequest('/orderSales/add', param);
  },

  //生产创单
  createProduction: (param) => {
    return postRequest('/orderProduction/add', param);
  },

  // 模糊查询地址
  searchAddress: (param) => {
    return getRequest('/address/fquery', param);
  },

  //销售单查询
  queryPageSales: (param) => {
    return postRequest('/orderSales/queryPage', param);
  },

  // 销售单查询
  querySalesById(id) {
    return getRequest(`/orderSales/queryById/${id}`);
  },

  //生产单查询
  queryPageProduction: (param) => {
    return postRequest('/orderProduction/queryPage', param);
  },

  // 生产单查询
  queryProductionById(id) {
    return getRequest(`/orderProduction/queryById/${id}`);
  },
};
