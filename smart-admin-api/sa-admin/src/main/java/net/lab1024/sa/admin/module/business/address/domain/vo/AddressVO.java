package net.lab1024.sa.admin.module.business.address.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 地址
 */
@Data
public class AddressVO {

    @Schema(description = "编号")
    private Long addressId;

    @Schema(description = "地址")
    private String place;

    @Schema(description = "价格")
    private String price;

    @Schema(description = "坐标")
    private String coordinates;


    @Schema(description = "备注|可选")
    private String remark;

    @Schema(description = "删除状态")
    private Boolean deletedFlag;


    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
