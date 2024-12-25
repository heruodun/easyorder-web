package net.lab1024.sa.admin.module.business.order.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单类型
 */
@AllArgsConstructor
@Getter
public enum OrderTypeEnum{

    /**
     * 一厂销售
     */
    FACTORY_ONE_SALES(1, "销售", 1, "一厂"),

    /**
     * 一厂生产白桶
     */
    FACTORY_ONE_PRODUCTION_BUCKET(2, "生产白桶", 1, "一厂"),

    /**
     * 一厂生产框子
     */
    FACTORY_ONE_PRODUCTION_BOX(3, "生产框子", 1, "一厂"),

    /**
     * 一厂生产摇摆
     */
    FACTORY_ONE_PRODUCTION_BAG(4, "生产摇摆", 1, "一厂"),

    /**
     * 一厂生产盘带
     */
    FACTORY_ONE_PRODUCTION_DISK(5, "生产盘带", 1, "一厂"),


    /**
     * 二厂销售
     */
    FACTORY_TWO_SALES(1, "销售", 2, "二厂"),

    /**
     * 二厂生产白桶
     */
    FACTORY_TWO_PRODUCTION_BUCKET(2, "生产白桶", 2, "二厂"),

    /**
     * 二厂生产框子
     */
    FACTORY_TWO_PRODUCTION_BOX(3, "生产框子", 2, "二厂"),

    /**
     * 二厂生产摇摆
     */
    FACTORY_TWO_PRODUCTION_BAG(4, "生产摇摆", 2, "二厂"),

    /**
     * 二厂生产盘带
     */
    FACTORY_TWO_PRODUCTION_DISK(5, "生产盘带", 2, "二厂");


    ;

    /**
     *
     */
    private final Integer type;

    private final String typeName;

    /**
     * 租户类型，1 一厂，2 二厂
     */
    private final Integer tenant;

    private final String tenantName;


}
