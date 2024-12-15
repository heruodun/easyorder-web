package net.lab1024.sa.admin.module.business.order.sales.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 波次明细
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveDetailVO {
    @Schema(description = "地址数量")
    private Integer addressCount;

    @Schema(description = "订单数量")
    private Integer totalCount;

    @Schema(description = "订单列表")
    private List<WaveAddressVO> orders;

}