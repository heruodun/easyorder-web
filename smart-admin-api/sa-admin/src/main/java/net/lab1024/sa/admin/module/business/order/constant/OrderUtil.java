package net.lab1024.sa.admin.module.business.order.constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderUtil {
    public static Long getOrderId(String str) {
        try {
            // 找到$的位置
            int index = str.indexOf('$');
            // 提取$前面的子字符串
            String numberStr = str.substring(0, index);
            // 如果需要将字符串转为数字
            return Long.parseLong(numberStr);
        }catch (Exception e){
            log.error("getOrderId error , str : {}", str, e);
            return null;
        }
    }
}

