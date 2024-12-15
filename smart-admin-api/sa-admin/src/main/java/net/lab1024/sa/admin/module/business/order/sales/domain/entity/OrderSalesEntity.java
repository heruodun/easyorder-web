package net.lab1024.sa.admin.module.business.order.sales.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;

/**
 * 销售订单 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
@TableName("t_order_sales")
public class OrderSalesEntity extends OrderEntity {

    /**
     * 地址
     */
    private String address;

    /**
     * 地址id
     */
    private Integer addressId;

    /**
     * 波次编号
     */
    private Integer waveId;

}