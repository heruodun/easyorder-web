package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 订单扫码表单
 *
 * @Author dahang
 * @Copyright dahang
 */

@Data
public class OrderScanForm {

    @Schema(description = "订单编号")
    private Long orderId;

    @Schema(description = "操作，查询100，配货200，对接300，做货301，拣货400")
    private int operation;

    @Schema(description = "操作者", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "操作者 不能为空")
    private String operator;

}
