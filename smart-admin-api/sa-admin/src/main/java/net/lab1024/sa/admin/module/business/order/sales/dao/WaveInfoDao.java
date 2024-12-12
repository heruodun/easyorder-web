package net.lab1024.sa.admin.module.business.order.sales.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.WaveInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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


    List<WaveInfoEntity> queryByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    WaveInfoEntity queryById(@Param("waveId")Integer waveId);

    int insertWaveInfo(WaveInfoEntity waveInfoEntity);

    int updateWaveShip(@Param("waveId")Integer waveId, @Param("shipIds") String shipIds);

    int updateWaveInfo(@Param("waveId")Integer waveId, @Param("status")Integer status,
                       @Param("shipTime") String shipTime, @Param("shipMan") String shipMan);


    int updateWaveShipCount(@Param("waveId")Integer waveId, @Param("shipCount") int shipCount);
}
