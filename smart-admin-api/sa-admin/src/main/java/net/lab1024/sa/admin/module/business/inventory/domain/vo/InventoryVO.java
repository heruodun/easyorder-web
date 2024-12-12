package net.lab1024.sa.admin.module.business.inventory.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 库存 列表VO
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventoryVO {


    @Schema(description = "编号")
    private Long id;

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "单位")
    private String danwei;

    @Schema(description = "明细")
    private String detail;

    @Schema(description = "当前状态")
    private Integer curStatus;

    @Schema(description = "当前处理时间")
    private LocalDateTime curTime;

    @Schema(description = "当前处理人")
    private String curMan;

    @Schema(description = "入库时间")
    private LocalDateTime inTime;

    @Schema(description = "出库人")
    private String outMan;

    @Schema(description = "出库时间")
    private LocalDateTime outTime;

    @Schema(description = "订单号")
    private Long salesOrderId;

    @Schema(description = "订单号")
    private Long productionOrderId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}