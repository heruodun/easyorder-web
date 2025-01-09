package net.lab1024.sa.admin.module.business.order.task.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.WaveInfoEntity;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 任务 Dao
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Mapper
@Component
public interface TaskDao extends BaseMapper<TaskEntity> {

    List<TaskEntity> queryByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    TaskEntity queryByOrderId(@Param("orderId") Long orderId);

    int updateConditionById(@Param("taskEntity") TaskEntity task, @Param("id") Long id);

    int updateStatus(@Param("status") Integer status, @Param("orderId") Long orderId);

}
