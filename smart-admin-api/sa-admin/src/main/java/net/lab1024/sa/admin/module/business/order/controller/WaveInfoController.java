package net.lab1024.sa.admin.module.business.order.controller;

import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoUpdateForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import net.lab1024.sa.admin.module.business.order.service.WaveInfoService;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.*;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 波次 Controller
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class WaveInfoController {

    @Resource
    private WaveInfoService waveInfoService;



    @Operation(summary = "查询 @author dahang")
    @GetMapping("/mobile/waveInfo")
    public ResponseDTO<List<WaveInfoVO>> queryPage(@RequestParam @Valid String date) {
        return ResponseDTO.ok(waveInfoService.queryPage(date));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/mobile/waveInfo/add")
    public ResponseDTO<WaveInfoVO> add(@RequestBody @Valid WaveInfoAddForm addForm) {
        return waveInfoService.add(addForm);
    }

        @Operation(summary = "更新 @author dahang")
    @PostMapping("/waveInfo/update")
    public ResponseDTO<String> update(@RequestBody @Valid WaveInfoUpdateForm updateForm) {
        return waveInfoService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author dahang")
    @PostMapping("/waveInfo/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Integer> idList) {
        return waveInfoService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author dahang")
    @GetMapping("/waveInfo/delete/{waveId}")
    public ResponseDTO<String> batchDelete(@PathVariable Integer waveId) {
        return waveInfoService.delete(waveId);
    }
}
