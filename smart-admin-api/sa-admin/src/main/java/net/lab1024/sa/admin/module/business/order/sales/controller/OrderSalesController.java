package net.lab1024.sa.admin.module.business.order.sales.controller;

import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesAddForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesUpdateForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.order.sales.service.OrderSalesService;
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
 * 销售订单 Controller
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class OrderSalesController {

    @Resource
    private OrderSalesService orderSalesService;

    @Operation(summary = "分页查询 @author dahang")
    @PostMapping("/orderSales/queryPage")
    public ResponseDTO<PageResult<OrderSalesVO>> queryPage(@RequestBody @Valid OrderSalesQueryForm queryForm) {
        return ResponseDTO.ok(orderSalesService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/orderSales/add")
    public ResponseDTO<String> add(@RequestBody @Valid OrderSalesAddForm addForm) {
        return orderSalesService.add(addForm);
    }

    @Operation(summary = "更新 @author dahang")
    @PostMapping("/orderSales/update")
    public ResponseDTO<String> update(@RequestBody @Valid OrderSalesUpdateForm updateForm) {
        return orderSalesService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author dahang")
    @PostMapping("/orderSales/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return orderSalesService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author dahang")
    @GetMapping("/orderSales/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return orderSalesService.delete(id);
    }
}
