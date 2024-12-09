
package net.lab1024.sa.admin.module.business.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.*;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import net.lab1024.sa.admin.module.business.order.service.WaveHttpService;
import net.lab1024.sa.admin.module.business.order.service.WaveInfoService;
import net.lab1024.sa.base.common.code.ErrorCode;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
    @PostMapping("/mobile/order/scan")
    public ResponseDTO<Boolean> scan(@RequestBody @Valid OrderScanForm orderScanForm) {
        boolean result =  WaveHttpService.operation(orderScanForm.getOrderId(),
                orderScanForm.getOperator(), orderScanForm.getOperation());
        if (result) {
            return ResponseDTO.ok(result);
        }
        return ResponseDTO.error(OrderErrorCode.PARAM_ERROR);
    }

}
