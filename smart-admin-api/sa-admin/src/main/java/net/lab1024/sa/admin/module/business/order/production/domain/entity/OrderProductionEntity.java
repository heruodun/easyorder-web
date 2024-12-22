package net.lab1024.sa.admin.module.business.order.production.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;

/**
 * 生产订单 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
@TableName(value = "t_order_production", autoResultMap = true)
public class OrderProductionEntity extends OrderEntity {

    /**
     * 规格
     */
    private String guige;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 单位
     */
    private String danwei;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 温度
     */
    private String temperature;



}