package net.lab1024.sa.admin.module.business.order.domain.vo;

import lombok.Data;
import net.lab1024.sa.admin.module.business.order.constant.OrderTypeEnum;

/**
 * 订单类型和id
 */
@Data
public class OrderTypeAndIdVO {

    private Long orderId;

    private OrderTypeEnum orderType;
}
