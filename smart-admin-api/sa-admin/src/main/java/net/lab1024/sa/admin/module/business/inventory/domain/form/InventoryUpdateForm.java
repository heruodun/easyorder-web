package net.lab1024.sa.admin.module.business.inventory.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存 更新表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventoryUpdateForm {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号 不能为空")
    private Long id;

}