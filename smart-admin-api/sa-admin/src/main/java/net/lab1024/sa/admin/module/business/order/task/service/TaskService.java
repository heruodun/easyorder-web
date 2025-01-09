package net.lab1024.sa.admin.module.business.order.task.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.constant.OrderUtil;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.dao.WaveInfoDao;
import net.lab1024.sa.admin.module.business.order.task.constant.TaskStatusEnum;
import net.lab1024.sa.admin.module.business.order.task.dao.SubTaskDao;
import net.lab1024.sa.admin.module.business.order.task.dao.TaskDao;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.SubTaskEntity;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.TaskEntity;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.TaskVO;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.config.domain.ConfigVO;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务 Service
 *
 * @Author dahang
 * @Date 2024-05-08 17:22:47
 * @Copyright dahang
 */

@Slf4j
@Service
public class TaskService {

    @Resource
    private TaskDao taskDao;
    @Resource
    private SubTaskDao subTaskDao;

    public ResponseDTO<Boolean> deleteByType(String orderIdQr, Integer type) {

        OrderTypeAndIdVO orderTypeAndIdVO = OrderUtil.parseOrderInfo(orderIdQr);
        if (orderTypeAndIdVO == null ) {
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        TaskEntity taskEntity = taskDao.queryByOrderId(orderTypeAndIdVO.getOrderId());
        //处理主任务
        if(taskEntity != null) {
            taskEntity.setMakeCount(0);
            taskDao.updateById(taskEntity);
        }

        subTaskDao.updateDeletedFlagByType(orderTypeAndIdVO.getOrderId(), type);

        return ResponseDTO.ok(true);
    }




    public int updateStatus(Long orderId, Integer status, Integer userId){
         int f = subTaskDao.updateStatus(status, orderId, userId);
         if(status == TaskStatusEnum.DONE.getStatus()) {
             new Thread(() -> {
                 List<SubTaskEntity> list = subTaskDao.queryByOrderId(orderId);
                 int taskStatus = TaskStatusEnum.UNDO.getStatus();
                 boolean hasUndo = false;
                 boolean hasDo = false;
                 for (SubTaskEntity subTask : list) {
                     if (subTask.getStatus() == TaskStatusEnum.UNDO.getStatus()) {
                         hasUndo = true;
                     }
                     if (subTask.getStatus() == TaskStatusEnum.DONE.getStatus()) {
                         hasDo = true;
                     }
                 }
                 if(hasDo && hasUndo){
                     taskStatus = TaskStatusEnum.DOING.getStatus();
                 }
                 else if(!hasUndo){
                     taskStatus = TaskStatusEnum.DONE.getStatus();
                 }
                 taskDao.updateStatus(taskStatus, orderId);
             }).start();
         }
         return f;
    }


    /**
     *
     * @return
     */
    public List<TaskVO> queryList(String curDate) {

        List<TaskVO> resultList = new ArrayList<>();
//        / 解析日期字符串
        LocalDate date = LocalDate.parse(curDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 计算当日0点
        LocalDateTime startOfDay = date.atStartOfDay();
        // 计算次日0点
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        // 转换为SQL可用的字符串格式（这里例子假设数据库使用的是DateTime格式）
        String startOfDayStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startOfDay);
        String startOfNextDayStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startOfNextDay);
        List<TaskEntity> list = taskDao.queryByTime(startOfDayStr, startOfNextDayStr);

        if(list.isEmpty()){
            return resultList;
        }
        resultList = SmartBeanUtil.copyList(list, TaskVO.class);

        return resultList;
    }


}