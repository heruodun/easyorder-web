package net.lab1024.sa.admin.module.business.order.sales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 波次 更新表单
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoUpdateForm {

    @Schema(description = "波次编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "波次编号 不能为空")
    private Integer waveId;

}