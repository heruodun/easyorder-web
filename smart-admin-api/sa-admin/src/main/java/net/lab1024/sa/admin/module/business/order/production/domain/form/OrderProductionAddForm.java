package net.lab1024.sa.admin.module.business.order.production.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 生产订单 新建表单
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Data
public class OrderProductionAddForm {
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
     * 类型
     */
    private Integer type;

    /**
     * 备注 温度
     */
    private String remark;

    /**
     * 打单个数
     */
    private int printCount;

}