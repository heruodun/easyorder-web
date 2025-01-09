package net.lab1024.sa.admin.module.business.order.task.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.SubTaskVO;

import java.util.List;

/**
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class SubTaskAddForm {
    /** 任务ID */
    @Schema(description = "任务列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SubTaskAddEle> subTasks;

}