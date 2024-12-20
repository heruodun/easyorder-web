package net.lab1024.sa.admin.module.business.order.sales.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 销售订单状态类型
 */
@AllArgsConstructor
@Getter
public enum OrderSalesStatusEnum {



    ;

    /**
     * 销售0，生产1
     */
    private final Integer type;

    private final String typeName;

    /**
     * 租户类型，1 一厂，2 二厂
     */
    private final Integer tenant;

    private final String tenantName;


}
