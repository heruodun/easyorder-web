/*
 *  打印设置
 *
 */

import { hiPrintPlugin } from 'vue-plugin-hiprint';
import mb2 from './mb2.json';
import mb1 from './mb1.json';
import mb3 from './mb3.json';
import mb4 from './mb4.json';

hiprint.init();
hiprint.hiwebSocket.setHost('localhost:17521');
let hiPrintTemplate1 = new hiprint.PrintTemplate({ template: mb1 });
let hiPrintTemplate2 = new hiprint.PrintTemplate({ template: mb2 });
// 白桶模版
let productionBucketprintTemplate = new hiprint.PrintTemplate({ template: mb3 });
let productionBoxprintTemplate = new hiprint.PrintTemplate({ template: mb4 });
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
