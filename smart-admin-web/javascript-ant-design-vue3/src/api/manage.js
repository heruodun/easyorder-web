import axios from 'axios';
import _ from 'lodash';
import { localClear, localRead } from '/@/utils/local-util';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

// token的消息头
const ERP_TOKEN_HEADER = 'x-erp-access-token';

// 创建axios对象
const smartAxios = axios.create({
  baseURL: import.meta.env.VITE_APP_ERP_API_URL + '/jshERP-boot',
  headers: {
    'Content-Type': 'application/json;charset=UTF-8', // 所有请求默认使用 JSON[10](@ref)
  },
});

// ================================= 请求拦截器 =================================
smartAxios.interceptors.request.use(
  (config) => {
    //todo临时方案 在发送请求之前消息头加入erp token token
    const erpToken = localRead(LocalStorageKeyConst.USER_ERP_TOKEN);
    if (erpToken) {
      config.headers[ERP_TOKEN_HEADER] = erpToken;
    } else {
      delete config.headers[ERP_TOKEN_HEADER];
    }

    return config;
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// ================================= 响应拦截器 =================================
const err = (error) => {
  if (error.response) {
    let data = error.response.data;
    const token = Vue.ls.get(ACCESS_TOKEN);
    switch (error.response.status) {
      case 403:
        notification.error({
          message: '系统提示',
          description: '拒绝访问',
          duration: 4,
        });
        break;
      case 500:
        if (token && data === 'loginOut') {
          Modal.error({
            title: '登录已过期',
            content: '很抱歉，登录已过期，请重新登录',
            okText: '重新登录',
            mask: false,
            onOk: () => {
              Vue.ls.remove(ACCESS_TOKEN);
              window.location.reload();
            },
          });
        }
        break;
      case 404:
        notification.error({
          message: '系统提示',
          description: '很抱歉，资源未找到!',
          duration: 4,
        });
        break;
      case 504:
        notification.error({ message: '系统提示', description: '网络超时' });
        break;
      case 401:
        notification.error({
          message: '系统提示',
          description: '未授权，请重新登录',
          duration: 4,
        });
        if (token) {
          store.dispatch('Logout').then(() => {
            setTimeout(() => {
              window.location.reload();
            }, 1500);
          });
        }
        break;
      default:
        notification.error({
          message: '系统提示',
          description: data.message,
          duration: 4,
        });
        break;
    }
  }
  return Promise.reject(error);
};

// 添加响应拦截器
smartAxios.interceptors.response.use((response) => {
  return response.data;
}, err);

const api = {
  user: '/api/user',
  role: '/api/role',
  service: '/api/service',
  permission: '/api/permission',
  permissionNoPager: '/api/permission/no-pager',
  exportExcelByParam: '/systemConfig/exportExcelByParam',
};

export default api;

//post
export function postAction(url, parameter) {
  return smartAxios({
    url: url,
    method: 'post',
    data: parameter,
  });
}

//post method= {post | put}
export function httpAction(url, parameter, method) {
  return smartAxios({
    url: url,
    method: method,
    data: parameter,
  });
}

//put
export function putAction(url, parameter) {
  return smartAxios({
    url: url,
    method: 'put',
    data: parameter,
  });
}

//get
export function getAction(url, parameter) {
  return smartAxios({
    url: url,
    method: 'get',
    params: parameter,
  });
}

//deleteAction
export function deleteAction(url, parameter) {
  return smartAxios({
    url: url,
    method: 'delete',
    params: parameter,
  });
}

export function getUserList(parameter) {
  return smartAxios({
    url: api.user,
    method: 'get',
    params: parameter,
  });
}

export function getRoleList(parameter) {
  return smartAxios({
    url: api.role,
    method: 'get',
    params: parameter,
  });
}

export function getServiceList(parameter) {
  return smartAxios({
    url: api.service,
    method: 'get',
    params: parameter,
  });
}

export function getPermissions(parameter) {
  return smartAxios({
    url: api.permissionNoPager,
    method: 'get',
    params: parameter,
  });
}

// id == 0 add     post
// id != 0 update  put
export function saveService(parameter) {
  return smartAxios({
    url: api.service,
    method: parameter.id == 0 ? 'post' : 'put',
    data: parameter,
  });
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFile(url, parameter) {
  return smartAxios({
    url: url,
    params: parameter,
    method: 'get',
    responseType: 'blob',
  });
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFilePost(parameter) {
  return smartAxios({
    url: api.exportExcelByParam,
    data: parameter,
    method: 'post',
    responseType: 'blob',
  });
}

/**
 * 文件上传 用于富文本上传图片
 * @param url
 * @param parameter
 * @returns {*}
 */
export function uploadAction(url, parameter) {
  return smartAxios({
    url: url,
    data: parameter,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data', // 文件上传
    },
  });
}

/**
 * 获取文件服务访问路径
 * @param avatar
 * @param subStr
 * @returns {*}
 */
export function getFileAccessHttpUrl(avatar, subStr) {
  if (!subStr) subStr = 'http';
  if (avatar && avatar.startsWith(subStr)) {
    return avatar;
  } else {
    if (avatar && avatar.length > 0 && avatar.indexOf('[') == -1) {
      return window._CONFIG['domianURL'] + '/' + avatar;
    }
  }
}
