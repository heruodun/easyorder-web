package net.lab1024.sa.admin.module.business.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.*;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import net.lab1024.sa.admin.module.business.order.service.WaveHttpService;
import net.lab1024.sa.admin.module.business.order.service.WaveInfoService;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 波次 Controller
 *
 * @Author dahang
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class WaveController {

    @Resource
    private WaveInfoService waveInfoService;

    @Operation(summary = "查询列表 @author dahang")
    @GetMapping("/app/order/wave/list")
    public ResponseDTO<List<WaveInfoVO>> queryPage(@RequestParam @Valid String date) {
        return ResponseDTO.ok(waveInfoService.queryPage(date));
    }

    @Operation(summary = "查询单个 @author dahang")
    @GetMapping("/app/order/wave/get/{waveId}")
    public ResponseDTO<WaveInfoVO> queryById(@PathVariable @Valid Integer waveId) {
        return ResponseDTO.ok(waveInfoService.queryById(waveId));
    }

    @Operation(summary = "查询 @author dahang")
    @GetMapping("/app/order/wave/queryByOrder/{orderId}")
    public ResponseDTO<WaveInfoVO> queryByOrderId(@PathVariable @Valid Long orderId) {
        return ResponseDTO.ok(waveInfoService.queryByOrderId(orderId));
    }


    @Operation(summary = "添加 @author dahang")
    @PostMapping("/app/order/wave/create")
    public ResponseDTO<WaveInfoVO> create(@RequestBody @Valid WaveInfoAddForm addForm) {
        return waveInfoService.add(addForm);
    }

    @Operation(summary = "开始送货 @author dahang")
    @PostMapping("/app/order/wave/ship")
    public ResponseDTO<Boolean> ship(@RequestBody @Valid WaveInfoShipForm shipForm) {
        return waveInfoService.ship(shipForm);
    }

    @Operation(summary = "修改送货单数量 @author dahang")
    @PostMapping("/app/order/wave/shipCount/update")
    public ResponseDTO<Boolean> updateShipCount(@RequestBody @Valid WaveInfoShipCountForm shipForm) {
        return waveInfoService.updateShipCount(shipForm);
    }

    @Operation(summary = "添加/删除配货单 @author dahang")
    @PostMapping("/app/order/wave/order/addOrDel")
    public ResponseDTO<Boolean> addOrDelWaveOrder(@RequestBody @Valid WaveOrderAddDelForm waveInfoAddShipForm) {
        boolean isOk = WaveHttpService.operation2(waveInfoAddShipForm.getOrderId(), waveInfoAddShipForm.getWaveId(),
                waveInfoAddShipForm.getOperator(), waveInfoAddShipForm.getOperation());
        if(isOk){
            return ResponseDTO.ok(true);
        }else {
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR);
        }
    }

}

