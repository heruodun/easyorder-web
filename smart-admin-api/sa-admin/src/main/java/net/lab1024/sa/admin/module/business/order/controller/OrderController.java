package net.lab1024.sa.admin.module.business.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.sales.service.OrderQianyiService;
import net.lab1024.sa.admin.module.business.order.service.OrderScanService;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @Resource
    private OrderScanService orderScanService;
    @Resource
    private OrderQianyiService orderQianyiService;




    @Operation(summary = "扫码 @author dahang")
    @PostMapping("/app/order/scan")
    public ResponseDTO<Boolean> scan(@RequestBody @Valid OrderScanForm orderScanForm) {
        return   orderScanService.scan(orderScanForm);
    }


    @NoNeedLogin
    @Operation(summary = "扫码 @author dahang")
    @GetMapping("/app/order/qianyi")
    public ResponseDTO<Boolean> qianyi() {
        return   orderQianyiService.run();
    }

    @NoNeedLogin
    @GetMapping("/app/order/qianyiaddress")
    public ResponseDTO<Long> qianyiAddress() {
        return   orderQianyiService.runAddress();
    }


}


