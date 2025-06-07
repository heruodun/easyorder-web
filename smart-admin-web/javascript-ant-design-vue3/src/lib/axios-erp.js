/*
 *  ajax请求
 */
import { message, Modal } from 'ant-design-vue';
import axios from 'axios';
import { localClear, localRead } from '/@/utils/local-util';
import _ from 'lodash';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

// token的消息头
const ERP_TOKEN_HEADER = 'x-erp-access-token';

// 创建axios对象
const smartAxios4Erp = axios.create({
  baseURL: import.meta.env.VITE_APP_ERP_API_URL + '/jshERP-boot',
  headers: {
    'Content-Type': 'application/json;charset=UTF-8', // 所有请求默认使用 JSON[10](@ref)
  },
});

const api = {
  user: '/api/user',
  role: '/api/role',
  service: '/api/service',
  permission: '/api/permission',
  permissionNoPager: '/api/permission/no-pager',
  exportExcelByParam: '/systemConfig/exportExcelByParam',
};

// 退出系统
function logout() {
  localClear();
  location.href = '/';
}

// ================================= todo 请求拦截器 =================================

smartAxios4Erp.interceptors.request.use(
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
smartAxios4Erp.interceptors.response.use((response) => {
  return response.data;
}, err);

// ================================= 对外提供请求方法：通用请求，get， post, 下载download等 =================================

/**
 * 通用请求封装
 * @param config
 */
export const request = (config) => {
  return smartAxios4Erp.request(config);
};

/**
 * get请求
 */
export const getRequest = (url, params) => {
  return request({ url, method: 'get', params });
};

/**
 * post请求
 */
export const postRequest = (url, data) => {
  return request({
    data,
    url,
    method: 'post',
  });
};

/**
 * put请求
 */
export const putRequest = (url, data) => {
  return smartAxios4Erp.put(url, data); // 正确传递参数[9,10](@ref)
};

/**
 * delete请求
 */
export const deleteRequest = (url, params) => {
  return request({
    params: params,
    url,
    method: 'delete',
  });
};

// ================================= 加密 =================================

/**
 * 加密请求参数的post请求
 */
export const postEncryptRequest = (url, data) => {
  return request({
    data: { encryptData: encryptData(data) },
    url,
    method: 'post',
  });
};

// ================================= 下载 =================================

export const postDownload = function (url, data) {
  request({
    method: 'post',
    url,
    data,
    responseType: 'blob',
  })
    .then((data) => {
      handleDownloadData(data);
    })
    .catch((error) => {
      handleDownloadError(error);
    });
};

/**
 * 文件下载
 */
export const getDownload = function (url, params) {
  request({
    method: 'get',
    url,
    params,
    responseType: 'blob',
  })
    .then((data) => {
      handleDownloadData(data);
    })
    .catch((error) => {
      handleDownloadError(error);
    });
};

function handleDownloadError(error) {
  if (error instanceof Blob) {
    const fileReader = new FileReader();
    fileReader.readAsText(error);
    fileReader.onload = () => {
      const msg = fileReader.result;
      const jsonMsg = JSON.parse(msg);
      message.destroy();
      message.error(jsonMsg.msg);
    };
  } else {
    message.destroy();
    message.error('网络发生错误', error);
  }
}

function handleDownloadData(response) {
  if (!response) {
    return;
  }

  // 获取返回类型
  let contentType = _.isUndefined(response.headers['content-type']) ? response.headers['Content-Type'] : response.headers['content-type'];

  // 构建下载数据
  let url = window.URL.createObjectURL(new Blob([response.data], { type: contentType }));
  let link = document.createElement('a');
  link.style.display = 'none';
  link.href = url;

  // 从消息头获取文件名
  let str = _.isUndefined(response.headers['content-disposition'])
    ? response.headers['Content-Disposition'].split(';')[1]
    : response.headers['content-disposition'].split(';')[1];

  let filename = _.isUndefined(str.split('fileName=')[1]) ? str.split('filename=')[1] : str.split('fileName=')[1];
  link.setAttribute('download', decodeURIComponent(filename));

  // 触发点击下载
  document.body.appendChild(link);
  link.click();

  // 下载完释放
  document.body.removeChild(link); // 下载完成移除元素
  window.URL.revokeObjectURL(url); // 释放掉blob对象
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFile(url, parameter) {
  return axios({
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
  return axios({
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
  return axios({
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
