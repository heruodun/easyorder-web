package net.lab1024.sa.admin.module.business.order.sales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 销售订单 新建表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderSalesAddForm {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号 不能为空")
    private Long id;

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单编号 不能为空")
    private Long orderId;

    @Schema(description = "地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "地址 不能为空")
    private String address;

    @Schema(description = "规格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规格 不能为空")
    private String guige;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "单位")
    private String danwei;

    @Schema(description = "明细")
    private String detail;

    @Schema(description = "轨迹")
    private String trace;

    @Schema(description = "当前状态")
    private String curStatus;

    @Schema(description = "当前处理时间")
    private LocalDateTime curTime;

    @Schema(description = "当前处理人")
    private String curMan;

    @Schema(description = "创建人")
    private String createMan;

    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}