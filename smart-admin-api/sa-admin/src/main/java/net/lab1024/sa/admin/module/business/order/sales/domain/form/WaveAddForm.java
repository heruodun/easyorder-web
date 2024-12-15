package net.lab1024.sa.admin.module.business.order.sales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 波次 新建表单
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
public class WaveAddForm {

    @Schema(description = "波次别名")
    private String waveAlias;
}