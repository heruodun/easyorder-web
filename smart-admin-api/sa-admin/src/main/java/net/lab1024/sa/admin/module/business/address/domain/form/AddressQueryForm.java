package net.lab1024.sa.admin.module.business.address.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import org.hibernate.validator.constraints.Length;

/**
 * 地址 分页查询
 *
 */
@Data
public class AddressQueryForm extends PageParam {

    @Schema(description = "搜索词")
    @Length(max = 30, message = "搜索词最多30字符")
    private String searchWord;


    @Schema(hidden = true)
    private Boolean deletedFlag;
}
