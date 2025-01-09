package net.lab1024.sa.admin.module.business.order.task.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 销售订单 新建表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class SubTaskAddEle {
    /** 任务ID */
    @Schema(description = "任务id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long taskId;

    /** 用户ID */
    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userId;

    /** 订单ID */
    @Schema(description = "订单id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    /** 用户名 */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(description = "货物标识", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String mark;

    /** 数量 */
    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer count;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;


}