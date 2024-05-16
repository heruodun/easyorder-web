package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 送货 波次
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoShipForm {

    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "波次别名")
    private String waveAlias;

    @Schema(description = "操作者", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "操作者 不能为空")
    private String operator;

}