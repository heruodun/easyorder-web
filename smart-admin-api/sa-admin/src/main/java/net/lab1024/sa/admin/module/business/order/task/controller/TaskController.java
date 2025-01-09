package net.lab1024.sa.admin.module.business.order.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.task.domain.form.SubTaskAddForm;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.TaskOrderVO;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.TaskVO;
import net.lab1024.sa.admin.module.business.order.task.service.OrderTaskService;
import net.lab1024.sa.admin.module.business.order.task.service.TaskService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 任务 Controller
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class TaskController {

    @Resource
    private TaskService taskService;
    @Resource
    private OrderTaskService orderTaskService;



    @Operation(summary = "查询 @author dahang")
    @GetMapping("/app/order/task/list")
    public ResponseDTO<List<TaskVO>> queryPage(@RequestParam @Valid String date) {
        return ResponseDTO.ok(taskService.queryList(date));
    }

//    @Operation(summary = "查询 @author dahang")
//    @GetMapping("/mobile/waveInfo")
//    public ResponseDTO<WaveInfoVO> queryById(@RequestParam @Valid Integer waveId) {
//        return ResponseDTO.ok(waveInfoService.queryById(waveId));
//    }

    @Operation(summary = "查询 @author dahang")
    @GetMapping("/app/order/task")
    public ResponseDTO<TaskOrderVO> queryByOrderIdQr(@RequestParam @Valid String orderIdQr) {
        return orderTaskService.queryByOrderIdQr(orderIdQr);
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/app/order/task/add")
    public ResponseDTO<Boolean> addTask(@RequestBody @Valid SubTaskAddForm subTaskAddForm) {
        return orderTaskService.addOrderTask(subTaskAddForm.getSubTasks());
    }

    @Operation(summary = "删除 @author dahang")
    @GetMapping("/app/order/task/delete")
    public ResponseDTO<Boolean> deleteByType(@RequestParam @Valid String orderIdQr,
                                                 @RequestParam @Valid Integer type) {
        return taskService.deleteByType(orderIdQr, type);
    }

}
