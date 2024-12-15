package net.lab1024.sa.admin.module.business.order.production.controller;

import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionAddForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionUpdateForm;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionVO;
import net.lab1024.sa.admin.module.business.order.production.service.OrderProductionService;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 生产订单 Controller
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class OrderProductionController {

    @Resource
    private OrderProductionService orderProductionService;

    @Operation(summary = "分页查询 @author dahang")
    @PostMapping("/orderProduction/queryPage")
    public ResponseDTO<PageResult<OrderProductionVO>> queryPage(@RequestBody @Valid OrderProductionQueryForm queryForm) {
        return ResponseDTO.ok(orderProductionService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/orderProduction/add")
    public ResponseDTO<String> add(@RequestBody @Valid OrderProductionAddForm addForm) {
        return orderProductionService.add(addForm);
    }

    @Operation(summary = "更新 @author dahang")
    @PostMapping("/orderProduction/update")
    public ResponseDTO<String> update(@RequestBody @Valid OrderProductionUpdateForm updateForm) {
        return orderProductionService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author dahang")
    @PostMapping("/orderProduction/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return orderProductionService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author dahang")
    @GetMapping("/orderProduction/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return orderProductionService.delete(id);
    }
}
