package net.lab1024.sa.admin.module.business.order.sales.repository;

import lombok.Data;

/**
 * 数量条件
 */
@Data
public class CountCriteria {
    private String danwei; // 单位
    private Integer minCount; // 最小数量
    private Integer maxCount; // 最大数量
}
