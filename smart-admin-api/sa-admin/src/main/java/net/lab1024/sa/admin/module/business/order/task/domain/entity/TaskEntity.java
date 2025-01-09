package net.lab1024.sa.admin.module.business.order.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务 实体类
 */
@Data
@TableName("t_order_task")
public class TaskEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    /** 任务ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 任务状态 */
    private Integer status;

    /** 类型 */
    private Integer type;

    /** 地址 */
    private String address;

    /** 规格 */
    private String guige;

    /** 总制作数量 */
    private Integer makeCount;

    /** 总数量 */
    private Integer allCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标记 */
    private Boolean deletedFlag;

}