package net.lab1024.sa.admin.module.business.order.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 子任务 实体类
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
@TableName("t_order_sub_task")
public class SubTaskEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    /** 子任务ID */
    private Long id;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Integer userId;

    /** 订单ID */
    private Long orderId;

    /** 用户名 */
    private String userName;

    /** 数量 */
    private Integer count;

    /** 子任务状态 */
    private Integer status;

    /** 地址 */
    private String address;

    /** 规格 */
    private String mark;
    /**
     * 对接类型，部分0 全部1
     */
    private int type;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除标记 */
    private Boolean deletedFlag;

}