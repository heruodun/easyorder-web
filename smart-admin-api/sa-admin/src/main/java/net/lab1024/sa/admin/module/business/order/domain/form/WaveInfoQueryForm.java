package net.lab1024.sa.admin.module.business.order.domain.form;

import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 波次 分页查询表单
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveInfoQueryForm extends PageParam{
    private Long curTimeStamp;
}