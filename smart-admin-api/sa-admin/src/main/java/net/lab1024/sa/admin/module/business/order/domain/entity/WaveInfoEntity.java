package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 波次 实体类
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Data
@TableName("t_wave_info")
public class WaveInfoEntity {

    /**
     * 波次编号
     */
    @TableId(type = IdType.AUTO)
    private Integer waveId;

    /**
     * 波次别名
     */
    private String waveAlias;

    /**
     * 创建者
     */
    private String createMan;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 第一次扫码时间
     */
    private LocalDateTime firstScanTime;

    /**
     * 最后一次扫码时间
     */
    private LocalDateTime lastScanTime;

}