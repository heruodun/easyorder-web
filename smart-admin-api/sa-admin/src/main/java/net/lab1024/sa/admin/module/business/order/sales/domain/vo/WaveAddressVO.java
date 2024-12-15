package net.lab1024.sa.admin.module.business.order.sales.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 按照地址分组的订单列表VO
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveAddressVO {
    @Schema(description = "地址")
    private String address;

    @Schema(description = "订单数量")
    private Integer orderCount;

    @Schema(description = "订单列表")
    private List<OrderSalesVO> orders;

}