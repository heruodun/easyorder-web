package net.lab1024.sa.admin.module.business.order.domain.entity;

import lombok.Data;

/**
 * 订单条数实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderTiaoEntity {
    /**
     * 长度
     */
    private String length;

    /**
     * 数量
     */
    private Integer count;

}