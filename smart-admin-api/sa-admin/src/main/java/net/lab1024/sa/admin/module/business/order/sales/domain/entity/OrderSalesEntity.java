package net.lab1024.sa.admin.module.business.order.sales.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.dao.OrderGuigeTypeHandler;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

/**
 * 销售订单 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */
@Document(indexName = "order_sales")
@Data
@TableName(value = "t_order_sales", autoResultMap = true)
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

    /**
     * 规格情况
     */
    @TableField(typeHandler = OrderGuigeTypeHandler.class)
    private List<OrderGuigeEntity> guiges;

}