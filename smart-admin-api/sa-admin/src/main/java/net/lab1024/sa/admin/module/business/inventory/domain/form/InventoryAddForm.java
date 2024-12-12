package net.lab1024.sa.admin.module.business.inventory.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存 新建表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventoryAddForm {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号 不能为空")
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