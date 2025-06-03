/*
 *  ajax请求
 */
import { message, Modal } from 'ant-design-vue';
import axios from 'axios';
import { localClear, localRead } from '/@/utils/local-util';
import { decryptData, encryptData } from './encrypt';
import { DATA_TYPE_ENUM } from '../constants/common-const';
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

// 添加响应拦截器
smartAxios4Erp.interceptors.response.use(
  (response) => {
    // 根据content-type ，判断是否为 json 数据
    let contentType = response.headers['content-type'] ? response.headers['content-type'] : response.headers['Content-Type'];
    if (contentType.indexOf('application/json') === -1) {
      return Promise.resolve(response);
    }

    // 如果是json数据
    if (response.data && response.data instanceof Blob) {
      return Promise.reject(response.data);
    }

    // 如果是加密数据
    if (response.data.dataType === DATA_TYPE_ENUM.ENCRYPT.value) {
      response.data.encryptData = response.data.data;
      let decryptStr = decryptData(response.data.data);
      if (decryptStr) {
        response.data.data = JSON.parse(decryptStr);
      }
    }

    const res = response.data;
    console.info('smartAxios4Erp-----');
    if (res.code && res.code !== 200) {
      // `token` 过期或者账号已在别处登录
      if (res.code === 30007 || res.code === 30008) {
        message.destroy();
        message.error('您没有登录，请重新登录');
        setTimeout(logout, 300);
        return Promise.reject(response);
      }

      // 等保安全的登录提醒
      if (res.code === 30010 || res.code === 30011) {
        Modal.error({
          title: '重要提醒',
          content: res.msg,
        });
        return Promise.reject(response);
      }

      // 长时间未操作系统，需要重新登录
      if (res.code === 30012) {
        Modal.error({
          title: '重要提醒',
          content: res.msg,
          onOk: logout,
        });
        setTimeout(logout, 3000);
        return Promise.reject(response);
      }
      message.destroy();
      message.error(res.msg);
      return Promise.reject(response);
    } else {
      return Promise.resolve(res);
    }
  },
  (error) => {
    // 对响应错误做点什么
    if (error.message.indexOf('timeout') !== -1) {
      message.destroy();
      message.error('网络超时');
    } else if (error.message === 'Network Error') {
      message.destroy();
      message.error('网络连接错误');
    } else if (error.message.indexOf('Request') !== -1) {
      message.destroy();
      message.error('网络发生错误');
    }
    return Promise.reject(error);
  }
);

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
