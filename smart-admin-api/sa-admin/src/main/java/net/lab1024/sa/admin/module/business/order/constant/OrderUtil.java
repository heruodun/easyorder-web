package net.lab1024.sa.admin.module.business.order.constant;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import org.aspectj.weaver.ast.Or;

@Slf4j
public class OrderUtil {

    /**
     * 迁移平台日期
     */
    public static String QIANYI_DATE_KEY = "qianyi";
    /**
     * 从给定字符串中提取订单 ID 和订单类型
     * @param str 例如：20241210000010101$xiaowangniujin
     * @return OrderTypeAndIdVO 包含订单ID和订单类型
     */
    public static OrderTypeAndIdVO parseOrderInfo(String str) {
        if (str == null) {
            log.error("parseOrderInfo error, str: null");
            //todo 异常处理
            return null;
        }

        OrderTypeAndIdVO orderTypeAndIdVO = new OrderTypeAndIdVO();

        try {
            // 找到$的位置
            int index = str.indexOf('$');
            if(index == -1){
                log.error("parseOrderInfo error, str: {}", str);
                return null;
            }

            // 提取$前面的子字符串
            String numberStr = str.substring(0, index);
            String qrVersion = str.substring(index + 1);

            if(QrTypeEnum.V0.getVersion().equals(qrVersion)){
                // 将字符串转为数字（订单 ID）
                Long orderId = Long.parseLong(numberStr);
                orderTypeAndIdVO.setOrderId(orderId);
                orderTypeAndIdVO.setOrderType(OrderTypeEnum.FACTORY_ONE_SALES);
                orderTypeAndIdVO.setQrType(QrTypeEnum.V0);
            }
            else {

                // 将字符串转为数字（订单 ID）
                Long orderId = Long.parseLong(numberStr);
                orderTypeAndIdVO.setOrderId(orderId);

                // 解析订单类型
                OrderTypeEnum orderType = getOrderType(numberStr);
                orderTypeAndIdVO.setOrderType(orderType);
                orderTypeAndIdVO.setQrType(QrTypeEnum.V1);
            }

            return orderTypeAndIdVO;

        } catch (Exception e) {
            log.error("parseOrderInfo error, str: {}", str, e);
            return null;
        }
    }

    private static OrderTypeEnum getOrderType(String orderId) {
        if (orderId.length() < 4) {
            log.error("getOrderType error, insufficient length for str: {}", orderId);
            return null;
        }

        try {
            // 获取最后四位字符
            String typeTenantCode = orderId.substring(orderId.length() - 4);

            // 提取前两位作为 type，后两位作为 tenant
            Integer tenant = Integer.parseInt(typeTenantCode.substring(0, 2));
            Integer type = Integer.parseInt(typeTenantCode.substring(2, 4));

            // 遍历所有的枚举值
            for (OrderTypeEnum orderType : OrderTypeEnum.values()) {
                // 判断当前 enum 的 type 和 tenant 是否匹配
                if (orderType.getType().equals(type) && orderType.getTenant().equals(tenant)) {
                    return orderType; // 返回匹配的 OrderTypeEnum
                }
            }
        } catch (Exception e) {
            log.error("getOrderType error, str: {}", orderId, e);
            return null;
        }

        return null; // 找不到匹配的枚举
    }

}

