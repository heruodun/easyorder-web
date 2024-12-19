package net.lab1024.sa.admin.module.business.order.sales.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 波次 列表VO
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveVO {


    @Schema(description = "波次编号")
    private Integer waveId;

    @Schema(description = "波次别名")
    private String waveAlias = "";

    @Schema(description = "创建者")
    private String createMan;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "第一次扫码时间")
    private LocalDateTime firstScanTime;

    @Schema(description = "最后一次扫码时间")
    private LocalDateTime lastScanTime;

    @Schema(description = "按照地址组织的订单列表")
    private List<WaveAddressVO> addressOrders;

    @Schema(description = "订单列表")
    private List<OrderSalesVO> orders;

    @Schema(description = "地址数量")
    private Integer addressCount;

    @Schema(description = "配货订单数量")
    private Integer orderCount;

    /**
     * 状态 0拣货 1送货
     */
    @Schema(description = "波次状态")
    private Integer status = 0;

    /**
     * 用逗号分割
     */
    @Schema(description = "送货单id列表")
    private String shipIds;

    /**
     * 送货单数量
     */
    @Schema(description = "送货单数量")
    private int shipCount;

    /**
     * 送货人
     */
    @Schema(description = "送货人")
    private String shipMan;

}