package net.lab1024.sa.admin.module.business.order.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.entity.WaveInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 波次 Dao
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Mapper
@Component
public interface WaveInfoDao extends BaseMapper<WaveInfoEntity> {


    List<WaveInfoEntity> queryPage(@Param("startTime") String startTime, @Param("endTime") String endTime);

    int insertWaveInfo(WaveInfoEntity waveInfoEntity);
}
