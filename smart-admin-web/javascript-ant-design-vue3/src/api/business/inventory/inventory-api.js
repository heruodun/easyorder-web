/**
 * 库存 api 封装
 *
 * @Author:    dahang
 * @Date:      2024-12-12 23:48:08
 * @Copyright  dahang
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const inventoryApi = {

  /**
   * 分页查询  @author  dahang
   */
  queryPage : (param) => {
    return postRequest('/inventory/queryPage', param);
  },

  /**
   * 增加  @author  dahang
   */
  add: (param) => {
      return postRequest('/inventory/add', param);
  },

  /**
   * 修改  @author  dahang
   */
  update: (param) => {
      return postRequest('/inventory/update', param);
  },


  /**
   * 删除  @author  dahang
   */
  delete: (id) => {
      return getRequest(`/inventory/delete/${id}`);
  },

  /**
   * 批量删除  @author  dahang
   */
  batchDelete: (idList) => {
      return postRequest('/inventory/batchDelete', idList);
  },

};
