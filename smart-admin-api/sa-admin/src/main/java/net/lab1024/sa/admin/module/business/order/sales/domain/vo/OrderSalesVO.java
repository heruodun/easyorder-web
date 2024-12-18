package net.lab1024.sa.admin.module.business.order.sales.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;

/**
 * 销售订单 列表VO
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderSalesVO {


    @Schema(description = "编号")
    private Long id;

    @Schema(description = "订单编号")
    private Long orderId;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "地址id")
    private Integer addressId;

    @Schema(description = "规格")
    private List<OrderGuigeEntity> guiges;

    @Schema(description = "备注")
    private String remark;

    //兼容旧数据
    @Schema(description = "明细")
    private String detail;

    @Schema(description = "轨迹")
    private List<TraceElementEntity> trace;

    @Schema(description = "当前状态")
    private String curStatus;

    @Schema(description = "当前处理时间")
    private LocalDateTime curTime;

    @Schema(description = "当前处理人")
    private String curOperator;

    @Schema(description = "当前处理人id")
    private String curOperatorId;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人id")
    private Long creatorId;

    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}