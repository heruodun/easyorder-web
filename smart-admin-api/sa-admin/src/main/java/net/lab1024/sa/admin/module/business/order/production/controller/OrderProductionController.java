package net.lab1024.sa.admin.module.business.order.production.controller;

import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionAddForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionUpdateForm;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionAddVO;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionVO;
import net.lab1024.sa.admin.module.business.order.production.service.OrderProductionService;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
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

    @Operation(summary = "查询订单详情 @author dahang")
    @GetMapping("/orderProduction/queryByOrderId/{orderId}")
    public ResponseDTO<OrderProductionVO> queryByOrderId(@PathVariable Long orderId) {
        OrderProductionEntity orderProductionEntity = orderProductionService.getByOrderId(orderId);
        if(orderProductionEntity == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }
        return  ResponseDTO.ok(SmartBeanUtil.copy(orderProductionEntity, OrderProductionVO.class));
    }

    @Operation(summary = "查询订单详情 @author dahang")
    @GetMapping("/orderProduction/queryById/{id}")
    public ResponseDTO<OrderProductionVO> queryById(@PathVariable Long id) {

        OrderProductionEntity orderProductionEntity = orderProductionService.getById(id);
        if(orderProductionEntity == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }
        return  ResponseDTO.ok(SmartBeanUtil.copy(orderProductionEntity, OrderProductionVO.class));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/orderProduction/add")
    public ResponseDTO<OrderProductionAddVO> add(@RequestBody @Valid OrderProductionAddForm addForm) {
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
