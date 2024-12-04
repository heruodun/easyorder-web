package net.lab1024.sa.admin.module.business.address.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址 实体类
 */
@Data
@TableName("t_address")
public class AddressEntity {

    @TableId(type = IdType.AUTO)
    private Long addressId;

    /**
     * 地址
     */
    private String place;

    /**
     * 坐标
     */
    private String coordinates;

    /**
     * 价格
     */
    private String price;


    /**
     * 备注
     */
    private String remark;

    /**
     * 删除状态
     */
    private Boolean deletedFlag;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
