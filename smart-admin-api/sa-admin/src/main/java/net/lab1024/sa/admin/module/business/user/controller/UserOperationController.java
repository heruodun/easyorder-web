package net.lab1024.sa.admin.module.business.user.controller;

import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationAddForm;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationQueryForm;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationUpdateForm;
import net.lab1024.sa.admin.module.business.user.domain.vo.UserOperationVO;
import net.lab1024.sa.admin.module.business.user.service.UserOperationService;
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
 * dahang Controller
 *
 * @Author dahang
 * @Date 2024-12-12 23:45:43
 * @Copyright dahang
 */

@RestController
@Tag(name = "")
public class UserOperationController {

    @Resource
    private UserOperationService userOperationService;

    @Operation(summary = "分页查询 @author dahang")
    @PostMapping("/userOperation/queryPage")
    public ResponseDTO<PageResult<UserOperationVO>> queryPage(@RequestBody @Valid UserOperationQueryForm queryForm) {
        return ResponseDTO.ok(userOperationService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author dahang")
    @PostMapping("/userOperation/add")
    public ResponseDTO<String> add(@RequestBody @Valid UserOperationAddForm addForm) {
        return userOperationService.add(addForm);
    }

    @Operation(summary = "更新 @author dahang")
    @PostMapping("/userOperation/update")
    public ResponseDTO<String> update(@RequestBody @Valid UserOperationUpdateForm updateForm) {
        return userOperationService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author dahang")
    @PostMapping("/userOperation/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return userOperationService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author dahang")
    @GetMapping("/userOperation/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return userOperationService.delete(id);
    }
}
