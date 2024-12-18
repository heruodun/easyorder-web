package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.dao.OrderGuigeTypeHandler;
import net.lab1024.sa.admin.module.business.order.dao.TraceTypeHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderEntity {

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
     * 规格情况
     */
    @TableField(typeHandler = OrderGuigeTypeHandler.class)
   private List<OrderGuigeEntity> guiges;

   /**
     * 备注
     */
   private String remark;

    /**
     * 轨迹
     */
    @TableField(typeHandler = TraceTypeHandler.class)
    private List<TraceElementEntity> trace;

    /**
     * 当前状态
     */
    private String curStatus;

    /**
     * 当前时间
     */
    private LocalDateTime curTime;

    /**
     * 当前处理人
     */
    private String curOperator;

    /**
     * 当前处理人id
     */
    private Long curOperatorId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 删除标记
     */
    private Boolean deletedFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}