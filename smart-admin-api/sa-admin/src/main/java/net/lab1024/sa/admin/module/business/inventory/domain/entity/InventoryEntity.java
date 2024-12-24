package net.lab1024.sa.admin.module.business.inventory.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存 实体类
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Data
@TableName("t_inventory")
public class InventoryEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规格
     */
    private String guige;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 单位
     */
    private String danwei;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库100，出库200
     */
    private Integer status;

    /**
     * @OrderTypeEnum
     * 库存类型
     */
    private Integer type;

    /**
     * 入库人
     */
    private String inMan;

    /**
     * 入库人id
     */
    private Integer inManId;

    /**
     * 入库时间
     */
    private LocalDateTime inTime;

    /**
     * 出库人
     */
    private String outMan;

    /**
     * 出库人id
     */
    private Integer outManId;


    /**
     * 出库时间
     */
    private LocalDateTime outTime;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Boolean deletedFlag;

}