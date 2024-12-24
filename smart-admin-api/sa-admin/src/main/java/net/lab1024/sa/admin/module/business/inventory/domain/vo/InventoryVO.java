package net.lab1024.sa.admin.module.business.inventory.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 库存 列表VO
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
public class InventoryVO {


    @Schema(description = "编号")
    private Long id;

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "单位")
    private String danwei;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "库存状态")
    private Integer status;

    @Schema(description = "库存类型")
    private Integer type;

    @Schema(description = "入库人")
    private String inMan;

    @Schema(description = "入库时间")
    private LocalDateTime inTime;

    @Schema(description = "出库人")
    private String outMan;

    @Schema(description = "出库时间")
    private LocalDateTime outTime;

    @Schema(description = "订单号")
    private Long orderId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}