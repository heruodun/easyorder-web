package net.lab1024.sa.admin.module.business.order.task.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.system.employee.domain.vo.EmployeeVO;

import java.util.Date;
import java.util.List;


@Data
public class TaskOrderVO {
    @Schema(description = "任务")
    private TaskVO task;
    @Schema(description = "订单详情")
    private OrderSalesVO order;
    @Schema(description = "对接工人")
    private List<EmployeeVO> duijieUsers;
}
