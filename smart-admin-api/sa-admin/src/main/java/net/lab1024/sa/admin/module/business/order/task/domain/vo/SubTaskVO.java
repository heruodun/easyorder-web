package net.lab1024.sa.admin.module.business.order.task.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SubTaskVO {
    @Schema(description = "子任务ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "子任务状态")
    private Integer status;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "标识")
    private String mark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    private Boolean deletedFlag;
}
