
package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 波次送货单数量
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoShipCountForm {

    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "波次别名")
    private String waveAlias;

    @Schema(description = "送货单数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "送货单数量不能为空")
    @PositiveOrZero(message = "送货单数量必须大于等于0")
    private Integer shipCount;

}
