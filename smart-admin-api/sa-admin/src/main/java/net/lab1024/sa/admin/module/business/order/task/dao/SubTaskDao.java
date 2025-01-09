package net.lab1024.sa.admin.module.business.order.task.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.SubTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 子任务 Dao
 *
 * @Author dahang
 * @Date 2024-05-10 10:51:52
 * @Copyright dahang
 */

@Mapper
@Component
public interface SubTaskDao extends BaseMapper<SubTaskEntity> {

    List<SubTaskEntity> queryByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    int insertSubTask(SubTaskEntity subTaskEntity);

    List<SubTaskEntity> queryByOrderId(@Param("orderId") Long orderId);

    List<SubTaskEntity> queryByTaskId(@Param("taskId") Long taskId);


    void updateDeletedFlag(@Param("orderId") Long orderId, @Param("mark") String mark);
    void updateDeletedFlagByType(@Param("orderId") Long orderId, @Param("type") Integer type);

    void batchInsertSubTasks(@Param("subTasks") List<SubTaskEntity> subTasks);

    int updateStatus(@Param("status") Integer status, @Param("orderId") Long orderId, @Param("userId") Integer userId);

}
