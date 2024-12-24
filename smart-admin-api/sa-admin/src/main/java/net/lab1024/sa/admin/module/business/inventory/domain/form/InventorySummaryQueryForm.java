package net.lab1024.sa.admin.module.business.inventory.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 库存汇总  分页查询表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventorySummaryQueryForm extends PageParam{

    @Schema(description = "规格")
    private String guige;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "入库出库")
    private Integer status;
}