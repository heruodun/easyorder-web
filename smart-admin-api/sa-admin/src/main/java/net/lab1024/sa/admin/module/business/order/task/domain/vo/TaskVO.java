package net.lab1024.sa.admin.module.business.order.task.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Data
public class TaskVO {
    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "任务状态")
    private Integer status;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "规格")
    private String guige;

    @Schema(description = "总制作数量")
    private Integer makeCount;

    @Schema(description = "总数量")
    private Integer allCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    private Boolean deletedFlag;

    @Schema(description = "子任务列表")
    private List<SubTaskVO> subTasks;


}
