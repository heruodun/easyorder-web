package net.lab1024.sa.admin.module.business.inventory.controller;

import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryAddForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventorySummaryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryUpdateForm;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventorySummaryVO;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventoryVO;
import net.lab1024.sa.admin.module.business.inventory.service.InventoryService;
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
import java.util.List;

/**
 * 库存 Controller
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @Operation(summary = "查询剩余库存 @author dahang")
    @PostMapping("/inventory/inSummary/queryPage")
    public ResponseDTO<PageResult<InventorySummaryVO>> query(@RequestBody @Valid InventorySummaryQueryForm inventorySummaryQueryForm) {
        return ResponseDTO.ok(inventoryService.queryInSummary(inventorySummaryQueryForm));
    }

    @Operation(summary = "分页查询 @author dahang")
    @PostMapping("/inventory/queryPage")
    public ResponseDTO<PageResult<InventoryVO>> queryPage(@RequestBody @Valid InventoryQueryForm queryForm) {
        return ResponseDTO.ok(inventoryService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/inventory/add")
    public ResponseDTO<String> add(@RequestBody @Valid InventoryAddForm addForm) {
        return inventoryService.add(addForm);
    }

    @Operation(summary = "更新 @author dahang")
    @PostMapping("/inventory/update")
    public ResponseDTO<String> update(@RequestBody @Valid InventoryUpdateForm updateForm) {
        return inventoryService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author dahang")
    @PostMapping("/inventory/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return inventoryService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author dahang")
    @GetMapping("/inventory/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return inventoryService.delete(id);
    }
}
