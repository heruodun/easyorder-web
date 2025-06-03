/*
 * 打单
 *
 */
import { getRequest } from '/@/lib/axios';

export const orderApi = {
  // 模糊查询地址
  searchAddress: (param) => {
    return getRequest('/address/fquery', param);
  },
};
