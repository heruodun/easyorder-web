package net.lab1024.sa.admin.module.business.address.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.json.deserializer.DictValueVoDeserializer;
import javax.validation.constraints.NotBlank;

/**
 * 地址 添加表单
 */
@Data
public class AddressAddForm {
    @Schema(description = "地址")
    @NotBlank(message = "地址不能为空 ")
    @JsonDeserialize(using = DictValueVoDeserializer.class)
    private String place;

    @Schema(description = "商品价格")
    private String price;

    @Schema(description = "备注|可选")
    private String remark;
}
