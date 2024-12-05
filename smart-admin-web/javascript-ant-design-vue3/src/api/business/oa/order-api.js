/*
 * 打单
 *
 */
import { getRequest, postPythonRequest } from '/@/lib/axios';

export const orderApi = {
    // 模版1创单
    create1: (param) => {
        return postPythonRequest('/order1', param);
    },

    // 模版2创单
    create2: (param) => {
        return postPythonRequest('/order2', param);
    },

    // 模糊查询地址
    searchAddress: (param) => {
        return getRequest('/address/fquery', param);
    },

};

