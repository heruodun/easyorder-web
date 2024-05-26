package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoAddDelShipForm {

    @Schema(description = "波次编号")
    private Integer waveId;


    @Schema(description = "操作类型 1：添加 -1：删除")
    private Integer type;


    @Schema(description = "送货单编号列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "送货单编号 不能为空")
    private String shipIds;

}