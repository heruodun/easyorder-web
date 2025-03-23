/*
 *  打印设置
 *
 */

import { hiPrintPlugin } from 'vue-plugin-hiprint';

// 根据环境变量动态获取模板配置
const env = import.meta.env.VITE_APP_MODE; // 输出 'outerproduction'

// 定义环境与模板的映射，示例根据实际需求调整
const templateMapping = {
  innerproduction: {
    t1: 'mb1',
    t2: 'mb2',
    bucket: 'mb3',
    box: 'mb4',
  },
  outerproduction: {
    t1: 'mb1',
    t2: 'mb2',
    bucket: 'mb3',
    box: 'mb4',
  },
  outerproductiondongyang: {
    t1: 'mb1',
    t2: 'mb2',
    bucket: 'mb3',
    box: 'mb4',
  },
  development: {
    t1: 'mb1',
    t2: 'mb2',
    bucket: 'mb3',
    box: 'mb4',
  },

  // 其他环境配置...
};

// 动态导入所有mb*.json文件
const modules = import.meta.glob('./mb*.json', { eager: true });
const getTemplateData = (name) => modules[`./${name}.json`].default;

// 获取当前环境对应的模板配置
const currentEnvTemplates = templateMapping[env];

// 初始化模板实例
let hiPrintTemplate1, hiPrintTemplate2, productionBucketprintTemplate, productionBoxprintTemplate;

const initTemplates = () => {
  hiPrintTemplate1 = new hiprint.PrintTemplate({ template: getTemplateData(currentEnvTemplates.t1) });
  hiPrintTemplate2 = new hiprint.PrintTemplate({ template: getTemplateData(currentEnvTemplates.t2) });
  productionBucketprintTemplate = new hiprint.PrintTemplate({ template: getTemplateData(currentEnvTemplates.bucket) });
  productionBoxprintTemplate = new hiprint.PrintTemplate({ template: getTemplateData(currentEnvTemplates.box) });
};

hiprint.init();
initTemplates(); // 调用初始化

// 打印
export function printT1(printData) {
  // 打印
  hiPrintTemplate1.print2(printData);
}

export function printT2(printData) {
  // 打印
  hiPrintTemplate2.print2(printData);
}

export function printProductionBucket(printData) {
  // 打印
  productionBucketprintTemplate.print2(printData);
}

export function printProductionBox(printData) {
  // 打印
  productionBoxprintTemplate.print2(printData);
}
