/*
 * @Description:
 * @Author: zhuoda
 * @Date: 2021-11-05
 * @LastEditTime: 2022-06-23
 * @LastEditors: zhuoda
 */
import {postRequest, getRequest, getDownload} from '/@/lib/axios';

export const addressApi = {
  // 添加商品 @author zhuoda
  addAddress: (param) => {
    return postRequest('/address/add', param);
  },
  // 删除 @author zhuoda
  deleteAddress: (addressId) => {
    return getRequest(`/address/delete/${addressId}`);
  },
  // 批量 @author zhuoda
  batchDelete: (addressIdList) => {
    return postRequest('/address/batchDelete', addressIdList);
  },
  // 分页查询 @author zhuoda
  queryAddressList: (param) => {
    return postRequest('/address/query', param);
  },
  // 更新商品 @author zhuoda
  updateAddress: (param) => {
    return postRequest('/address/update', param);
  },
};
