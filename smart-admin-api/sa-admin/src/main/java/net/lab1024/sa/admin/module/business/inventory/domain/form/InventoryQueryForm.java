package net.lab1024.sa.admin.module.business.inventory.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

/**
 * 库存 分页查询表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventoryQueryForm extends PageParam{

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "开始时间")
    private LocalDate createTimeBegin;

    @Schema(description = "开始时间")
    private LocalDate createTimeEnd;

}