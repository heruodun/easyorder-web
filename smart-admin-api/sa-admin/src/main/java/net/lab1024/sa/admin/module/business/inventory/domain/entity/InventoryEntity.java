package net.lab1024.sa.admin.module.business.inventory.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
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
     * 明细
     */
    private String detail;

    /**
     * 当前状态
     */
    private Integer curStatus;

    /**
     * 当前处理时间
     */
    private LocalDateTime curTime;

    /**
     * 当前处理人
     */
    private String curMan;

    /**
     * 入库时间
     */
    private LocalDateTime inTime;

    /**
     * 出库人
     */
    private String outMan;

    /**
     * 出库时间
     */
    private LocalDateTime outTime;

    /**
     * 订单号
     */
    private Long salesOrderId;

    /**
     * 订单号
     */
    private Long productionOrderId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}