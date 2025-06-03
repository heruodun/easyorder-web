import { isURL } from '/@/utils/validate';
import { downFilePost } from '/@/lib/axios-erp';
import introJs from 'intro.js';

export function timeFix() {
  const time = new Date();
  const hour = time.getHours();
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour < 20 ? '下午好' : '晚上好';
}

export function welcome() {
  const arr = ['休息一会儿吧', '准备吃什么呢?', '要不要打一把 DOTA', '我猜你可能累了'];
  let index = Math.floor(Math.random() * arr.length);
  return arr[index];
}

/**
 * 触发 window.resize
 */
export function triggerWindowResizeEvent() {
  const event = new Event('resize', { bubbles: true, cancelable: true });
  window.dispatchEvent(event);
}

/**
 * 过滤对象中为空的属性
 */
export function filterObj(obj) {
  if (typeof obj !== 'object') return obj;

  return Object.fromEntries(Object.entries(obj).filter(([_, value]) => value !== null && value !== undefined && value !== ''));
}

/**
 * 时间格式化
 */
export function formatDate(value, fmt) {
  if (!value) return '';

  const date = new Date(value);
  if (isNaN(date.getTime())) return value.substring(0, fmt.length);

  const o = {
    'y+': date.getFullYear(),
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    S: date.getMilliseconds(),
  };

  let formatted = fmt;
  for (const [k, v] of Object.entries(o)) {
    const regex = new RegExp(`(${k})`);
    if (regex.test(formatted)) {
      formatted = formatted.replace(
        regex,
        k === 'y+' ? v.toString().substring(4 - RegExp.$1.length) : RegExp.$1.length === 1 ? v : v.toString().padStart(2, '0')
      );
    }
  }
  return formatted;
}

/**
 * 深度克隆对象
 */
export function cloneObject(obj) {
  return structuredClone(obj);
}

/**
 * 随机生成数字
 */
export function randomNumber(min, max) {
  if (arguments.length === 1) {
    return Array.from({ length: min }, () => Math.floor(Math.random() * 10)).join('');
  }
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * 随机生成字符串
 */
export function randomString(length = 16, chars = '0123456789abcdefghijklmnopqrstuvwxyz') {
  return Array.from({ length }, () => chars.charAt(Math.floor(Math.random() * chars.length))).join('');
}

/**
 * 随机生成UUID
 */
export function randomUUID() {
  return crypto.randomUUID();
}

/**
 * 下划线转驼峰
 */
export function underLine2CamelCase(string) {
  return string.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}

/**
 * 增强CSS
 */
export function cssExpand(css, id) {
  const style = document.createElement('style');
  style.textContent = css;

  if (id) {
    const existing = document.getElementById(id);
    if (existing) existing.remove();
    style.id = id;
  }

  document.head.appendChild(style);
}

/**
 * 如果值不存在就 push 进数组
 */
export function pushIfNotExist(array, value, key) {
  const exists = key ? array.some((item) => item[key] === value[key]) : array.includes(value);

  if (!exists) array.push(value);
  return !exists;
}

/**
 * 防抖方法
 */
export function simpleDebounce(fn, delay = 100) {
  let timer = null;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

/**
 * 替换所有值
 */
export function replaceAll(text, checker, replacer) {
  return text.split(checker).join(replacer);
}

/**
 * 获取当前日期时间
 */
export function getNowFormatDateTime() {
  const date = new Date();
  return formatDate(date, 'yyyy-MM-dd hh:mm:ss');
}

/**
 * 导出Excel
 */
export async function exportXlsPost(fileName = '导出文件', title, head, tip, list) {
  try {
    const paramObj = { title, head, tip, list };
    const data = await downFilePost(paramObj);

    if (!data) throw new Error('文件下载失败');

    const blob = new Blob([data], { type: 'application/vnd.ms-excel' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');

    link.href = url;
    link.download = `${fileName}_${getNowFormatStr()}.xls`;
    link.style.display = 'none';

    document.body.appendChild(link);
    link.click();

    setTimeout(() => {
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    }, 100);

    return true;
  } catch (error) {
    console.error('导出失败:', error);
    return false;
  }
}

/**
 * 新手引导
 */
export function handleIntroJs(module, cur_version) {
  const storageKey = `intro_cache_${module}`;

  if (localStorage.getItem(storageKey) === cur_version) return;

  const intro = introJs(module !== 'indexChart' ? `#${module}` : undefined);

  intro.setOptions({
    prevLabel: '← 上一步',
    nextLabel: '下一步 →',
    doneLabel: '知道了',
    exitOnOverlayClick: false,
  });

  intro.oncomplete(() => {
    localStorage.setItem(storageKey, cur_version);
  });

  intro.onexit(() => {
    localStorage.setItem(storageKey, cur_version);
  });

  intro.start();
}

/**
 * 回车跳转下一个输入框
 */
export function autoJumpNextInput(containerId) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const inputs = Array.from(container.querySelectorAll('input'));
  let currentIndex = 0;

  inputs.forEach((input, index) => {
    input.addEventListener('click', () => (currentIndex = index));
  });

  container.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      currentIndex = (currentIndex + 1) % inputs.length;
      inputs[currentIndex].focus();
    }
  });

  if (inputs.length > 0) inputs[0].focus();
}

/**
 * js获取当前时间， 格式“yyyyMMddHHMMSS”
 */
export function getNowFormatStr() {
  let date = new Date();
  let year = date.getFullYear();
  let month = date.getMonth() + 1;
  let strDate = date.getDate();
  let strHours = date.getHours();
  let strMinutes = date.getMinutes();
  let strSeconds = date.getSeconds();
  if (month >= 1 && month <= 9) {
    month = '0' + month;
  }
  if (strDate >= 0 && strDate <= 9) {
    strDate = '0' + strDate;
  }
  if (strHours >= 0 && strHours <= 9) {
    strHours = '0' + strHours;
  }
  if (strMinutes >= 0 && strMinutes <= 9) {
    strMinutes = '0' + strMinutes;
  }
  if (strSeconds >= 0 && strSeconds <= 9) {
    strSeconds = '0' + strSeconds;
  }
  return year + '' + month + '' + strDate + '' + strHours + '' + strMinutes + '' + strSeconds;
}

/**
 * 转换商品扩展字段的格式
 * @param thisRows
 * @param checker
 * @param replacer
 * @returns {string}
 */
export function getMpListShort(thisRows, checker, replacer) {
  let mPropertyListShort = '';
  let anotherNameStr = '';
  for (let i = 0; i < thisRows.length; i++) {
    anotherNameStr += thisRows[i].anotherName + ',';
  }
  if (anotherNameStr) {
    mPropertyListShort = anotherNameStr.substring(0, anotherNameStr.length - 1);
  }
  return mPropertyListShort;
}
