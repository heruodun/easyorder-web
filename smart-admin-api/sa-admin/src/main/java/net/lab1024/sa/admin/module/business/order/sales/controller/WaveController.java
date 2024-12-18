package net.lab1024.sa.admin.module.business.order.sales.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveInfoVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.WaveInfoAddForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.WaveInfoShipCountForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.WaveInfoShipForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.WaveOrderAddDelForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveVO;
import net.lab1024.sa.admin.module.business.order.service.WaveHttpService;
import net.lab1024.sa.admin.module.business.order.service.WaveInfoService;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
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
    public ResponseDTO<List<WaveVO>> queryPage(@RequestParam @Valid String date) {
        return waveInfoService.queryPageWave(date);
    }

    @Operation(summary = "查询单个 @author dahang")
    @GetMapping("/app/order/wave/get/{waveId}")
    public ResponseDTO<WaveVO> queryById(@PathVariable @Valid Integer waveId) {
        return waveInfoService.queryByWaveId(waveId);
    }

    @Operation(summary = "查询 @author dahang")
    @GetMapping("/app/order/wave/queryByOrder/{orderIdQr}")
    public ResponseDTO<WaveVO> queryByOrderIdQr(@PathVariable @Valid String orderIdQr) {
        return waveInfoService.queryByOrderIdQr(orderIdQr);
    }


    @Operation(summary = "添加 @author dahang")
    @PostMapping("/app/order/wave/create")
    public ResponseDTO<WaveInfoVO> create(@RequestBody @Valid WaveInfoAddForm addForm) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        addForm.setCreateMan(requestUser.getUserName());
        return waveInfoService.add(addForm);
    }

    @Operation(summary = "开始送货 @author dahang")
    @PostMapping("/app/order/wave/ship")
    public ResponseDTO<Boolean> ship(@RequestBody @Valid WaveInfoShipForm shipForm) {
        return waveInfoService.shipWave(shipForm);
    }

    @Operation(summary = "修改送货单数量 @author dahang")
    @PostMapping("/app/order/wave/shipCount/update")
    public ResponseDTO<Boolean> updateShipCount(@RequestBody @Valid WaveInfoShipCountForm shipForm) {
        return waveInfoService.updateShipCount(shipForm);
    }

    @Operation(summary = "添加/删除配货单 @author dahang")
    @PostMapping("/app/order/wave/order/addOrDel")
    public ResponseDTO<Boolean> addOrDelWaveOrder(@RequestBody @Valid WaveOrderAddDelForm waveInfoAddShipForm) {
        return waveInfoService.addOrDelWaveOrder(waveInfoAddShipForm);
    }

}

