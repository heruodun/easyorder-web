package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 波次 新建表单
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoAddForm {

    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "波次别名")
    private String waveAlias;

    @Schema(description = "创建者", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "创建者 不能为空")
    private String createMan;

}