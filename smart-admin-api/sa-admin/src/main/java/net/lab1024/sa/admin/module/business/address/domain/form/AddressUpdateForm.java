package net.lab1024.sa.admin.module.business.address.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 地址 更新表单
 */
@Data
public class AddressUpdateForm extends AddressAddForm {

    @Schema(description = "地址id")
    @NotNull(message = "地址id不能为空")
    private Long addressId;
}
