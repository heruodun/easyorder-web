package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 销售订单 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
@TableName("t_order_sales")
public class OrderSalesEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 地址
     */
    private String address;

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
     * 明细
     */
    private String detail;

    /**
     * 轨迹
     */
    private String trace;

    /**
     * 当前状态
     */
    private String curStatus;

    /**
     * 当前处理时间
     */
    private LocalDateTime curTime;

    /**
     * 当前处理人
     */
    private String curMan;

    /**
     * 创建人
     */
    private String createMan;

    /**
     * 波次编号
     */
    private Integer waveId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}