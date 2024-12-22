package net.lab1024.sa.admin.module.business.order.production.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;

/**
 * 生产订单 列表VO
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderProductionVO {


    @Schema(description = "编号")
    private Long id;

    @Schema(description = "订单编号")
    private Long orderId;

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "单位")
    private String danwei;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}