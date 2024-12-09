package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @Author dahang
 * @Copyright dahang
 */

@Data
public class WaveOrderAddDelForm {

    @Schema(description = "波次编号")
    private Integer waveId;


    @Schema(description = "操作类型 1：添加 -1：删除")
    private Integer operation;


    @Schema(description = "配货单编号 不能为空")
    private Long orderId;

    @Schema(description = "操作人 不能为空")
    private String operator;

}
