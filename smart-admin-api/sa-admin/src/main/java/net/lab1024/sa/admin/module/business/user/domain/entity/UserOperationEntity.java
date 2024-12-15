package net.lab1024.sa.admin.module.business.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * dahang 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:45:43
 * @Copyright dahang
 */

@Data
@TableName("t_user_operation")
public class UserOperationEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 操作
     */
    private String operation;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作人id
     */
    private Long operatorId;

    /**
     * 订单号,可能是生产订单也可能是销售订单
     */
    private Long orderId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Boolean deletedFlag;


}