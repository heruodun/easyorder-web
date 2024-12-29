package net.lab1024.sa.admin.module.business.order.sales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;

/**
 * 销售订单 新建表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderSalesAddForm {

    @Schema(description = "地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "地址不能为空~")
    private String address;

    @Schema(description = "地址id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer addressId;

    @Schema(description = "规格", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderGuigeEntity> guiges;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}