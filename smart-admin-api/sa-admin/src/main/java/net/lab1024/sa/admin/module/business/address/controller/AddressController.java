package net.lab1024.sa.admin.module.business.address.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressAddForm;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressUpdateForm;
import net.lab1024.sa.admin.module.business.address.domain.vo.AddressVO;
import net.lab1024.sa.admin.module.business.address.service.AddressService;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressQueryForm;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 地址业务
 *
 * /erp/address/list
 * /business/erp/address/address-list.vue
 *
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.MANAGER_ADDRESS)
public class AddressController {

    @Resource
    private AddressService addressService;

    @Operation(summary = "分页查询")
    @PostMapping("/address/query")
    @SaCheckPermission("address:query")
    public ResponseDTO<PageResult<AddressVO>> query(@RequestBody @Valid AddressQueryForm queryForm) {
        return addressService.query(queryForm);
    }

    @Operation(summary = "模糊查询")
    @GetMapping("/address/fquery")
    @SaCheckPermission("address:fquery")
    public ResponseDTO<Map> fquery(@RequestParam String key, @RequestParam boolean containDeleted) {
        Set<String> set = addressService.fquery(key, containDeleted);
        Map map = new HashMap();
        map.put("result", set);
        return ResponseDTO.ok(map);

    }

    @Operation(summary = "添加地址")
    @PostMapping("/address/add")
    @SaCheckPermission("address:add")
    public ResponseDTO<String> add(@RequestBody @Valid AddressAddForm addForm) {
        return addressService.add(addForm);
    }

    @Operation(summary = "更新地址 @author 胡克")
    @PostMapping("/address/update")
    @SaCheckPermission("address:update")
    public ResponseDTO<String> update(@RequestBody @Valid AddressUpdateForm updateForm) {
        return addressService.update(updateForm);
    }

    @Operation(summary = "删除 @author 卓大")
    @GetMapping("/address/delete/{addressId}")
    @SaCheckPermission("address:delete")
    public ResponseDTO<String> delete(@PathVariable Long addressId) {
        return addressService.delete(addressId);
    }

    @Operation(summary = "删除 @author 卓大")
    @GetMapping("/address/recover/{addressId}")
    @SaCheckPermission("address:recover")
    public ResponseDTO<String> recover(@PathVariable Long addressId) {
        return addressService.recover(addressId);
    }

    @Operation(summary = "批量 @author 卓大")
    @PostMapping("/address/batchDelete")
    @SaCheckPermission("address:batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody @Valid ValidateList<Long> idList) {
        return addressService.batchDelete(idList);
    }

}
