package net.lab1024.sa.admin.module.business.order.sales.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.service.WaveHttpService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单 Controller
 *
 * @Author dahang
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class OrderController {

    @Operation(summary = "扫码 @author dahang")
    @PostMapping("/app/order/scan")
    public ResponseDTO<Boolean> scan(@RequestBody @Valid OrderScanForm orderScanForm) {
        return   WaveHttpService.operation(orderScanForm.getOrderIdQr(),
                orderScanForm.getOperator(), orderScanForm.getOperation());
    }

}


