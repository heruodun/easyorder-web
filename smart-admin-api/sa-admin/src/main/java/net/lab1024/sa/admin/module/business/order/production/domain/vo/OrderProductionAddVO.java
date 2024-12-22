package net.lab1024.sa.admin.module.business.order.production.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;

/**
 * 生产订单 addVO
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderProductionAddVO extends OrderSalesVO {
    @Schema(description = "二维码")
    private String qrCode;

}