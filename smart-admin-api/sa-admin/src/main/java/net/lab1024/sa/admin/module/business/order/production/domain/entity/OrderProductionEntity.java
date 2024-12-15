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
@TableName("t_order_production")
public class OrderProductionEntity extends OrderEntity {

}