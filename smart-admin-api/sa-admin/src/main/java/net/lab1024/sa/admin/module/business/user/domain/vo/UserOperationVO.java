package net.lab1024.sa.admin.module.business.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * dahang 列表VO
 *
 * @Author dahang
 * @Date 2024-12-12 23:45:43
 * @Copyright dahang
 */

@Data
public class UserOperationVO {


    @Schema(description = "编号")
    private Long id;

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "单位")
    private String danwei;

    @Schema(description = "明细")
    private String detail;

    @Schema(description = "操作")
    private String operation;

    @Schema(description = "订单号")
    private Long salesOrderId;

    @Schema(description = "订单号")
    private Long productionOrderId;

    @Schema(description = "是否删除")
    private Integer deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}