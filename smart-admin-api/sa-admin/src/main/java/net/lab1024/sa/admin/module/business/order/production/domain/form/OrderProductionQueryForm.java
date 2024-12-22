package net.lab1024.sa.admin.module.business.order.production.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 生产订单 分页查询表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderProductionQueryForm extends PageParam{
    @Schema(description = "规格")
    private String guige;

    @Schema(description = "创建开始时间")
    private LocalDate createTimeBegin;

    @Schema(description = "创建结束时间")
    private LocalDate createTimeEnd;

    @Schema(description = "类型")
    private Integer typeId;

}