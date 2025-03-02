package net.lab1024.sa.admin.module.business.order.task.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.constant.OrderUtil;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.dao.WaveInfoDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.WaveInfoEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.*;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveDetailVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveInfoVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveVO;
import net.lab1024.sa.admin.module.business.order.sales.service.OrderESService;
import net.lab1024.sa.admin.module.business.order.sales.service.OrderSalesService;
import net.lab1024.sa.admin.module.business.order.task.constant.TaskStatusEnum;
import net.lab1024.sa.admin.module.business.order.task.dao.SubTaskDao;
import net.lab1024.sa.admin.module.business.order.task.dao.TaskDao;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.SubTaskEntity;
import net.lab1024.sa.admin.module.business.order.task.domain.entity.TaskEntity;
import net.lab1024.sa.admin.module.business.order.task.domain.form.SubTaskAddEle;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.SubTaskVO;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.TaskOrderVO;
import net.lab1024.sa.admin.module.business.order.task.domain.vo.TaskVO;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
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
 * 订单任务 Service
 *
 * @Author dahang
 * @Date 2024-05-08 17:22:47
 * @Copyright dahang
 */

@Slf4j
@Service
public class OrderTaskService {


    @Resource
    private OrderSalesService orderSalesService;
    @Resource
    private OrderSalesDao orderSalesDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private SubTaskDao subTaskDao;
    @Resource
    private OrderESService orderESService;

    public ResponseDTO<TaskOrderVO> queryByOrderIdQr(String orderIdQr) {

        OrderTypeAndIdVO orderTypeAndIdVO = OrderUtil.parseOrderInfo(orderIdQr);
        if (orderTypeAndIdVO == null ) {
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        Long orderId = orderTypeAndIdVO.getOrderId();
        //通过订单号查询波次id
        OrderSalesEntity orderSalesEntity = orderSalesService.getByOrderId(orderId);
        if (orderSalesEntity == null) {
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        TaskEntity taskEntity = taskDao.queryByOrderId(orderId);

        List<SubTaskEntity> subTaskEntityList = subTaskDao.queryByOrderId(orderId);

        TaskOrderVO taskOrderVO = new TaskOrderVO();

        TaskVO taskVO = SmartBeanUtil.copy(taskEntity, TaskVO.class);
        if(taskVO != null){
            taskVO.setSubTasks(SmartBeanUtil.copyList(subTaskEntityList, SubTaskVO.class));
        }
        taskOrderVO.setOrder(SmartBeanUtil.copy(orderSalesEntity, OrderSalesVO.class));
        taskOrderVO.setTask(taskVO);

        return ResponseDTO.ok(taskOrderVO);
    }


    public ResponseDTO<Boolean> addOrderTask(List<SubTaskAddEle> subTasks ) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();

        if (CollectionUtils.isEmpty(subTasks)) {
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR, "任务为空~");
        }

        Long orderId = subTasks.get(0).getOrderId();
        OrderSalesEntity orderSalesEntity = orderSalesService.getByOrderId(orderId);
        if(orderSalesEntity == null){
            return ResponseDTO.error(OrderErrorCode.DATA_NOT_EXIST, "非法订单号~");
        }

        ResponseDTO<Boolean>  responseDTO =  addTask(subTasks, orderSalesEntity);

        if(responseDTO.getOk()){
            new Thread(() -> {
                List<SubTaskEntity> list = subTaskDao.queryByOrderId(orderId);
                int makeCount = 0;
                for(SubTaskEntity subTask : list){
                    makeCount += subTask.getCount();
                }

                TaskEntity taskEntity = taskDao.queryByOrderId(subTasks.get(0).getOrderId());
                if(taskEntity != null) {
                    taskEntity.setMakeCount(makeCount);
                    taskDao.updateById(taskEntity);
                }

                String detail = getDetail(subTasks);
                orderSalesService.updateOrderAndUserOperation(LocalDateTime.now(), requestUser, "分单",
                        detail, orderSalesEntity);
                orderESService.asyncData(Arrays.asList(orderSalesEntity.getId()));

            }).start();

        }
        return responseDTO;

    }

    @NotNull
    private static String getDetail(List<SubTaskAddEle> subTasks) {
        String typeStr = "部分做货：";
        if(subTasks.get(0).getType() == 1){
            typeStr = "整单做货：";
        }

        List<SubTaskAddEle> noZeroSubTasks = subTasks.stream().filter(subTask -> subTask.getCount() > 0).collect(Collectors.toList());;

        int totalCount = 0;
        StringBuilder makeDetail = new StringBuilder();
        for(SubTaskAddEle subTask : noZeroSubTasks){
            totalCount += subTask.getCount();
            makeDetail.append(subTask.getUserName()+ "：" + subTask.getCount() + " 条，");
        }
        if(makeDetail.length() > 0) {
            makeDetail.deleteCharAt(makeDetail.length() - 1);
        }

        String detail = typeStr + subTasks.get(0).getMark() + " x " + totalCount + " 条（" + makeDetail.toString()+"）";
        return detail;
    }


    @Transactional
    public ResponseDTO<Boolean> addTask(List<SubTaskAddEle> subTasks, OrderSalesEntity orderSalesEntity ) {

        TaskEntity taskEntity = taskDao.queryByOrderId(subTasks.get(0).getOrderId());
        int count = 0;
        for(SubTaskAddEle subTask : subTasks){
            count += subTask.getCount();
        }
        //处理主任务
        if(taskEntity != null){
//            taskEntity.setMakeCount(count + taskEntity.getMakeCount());
            taskEntity.setType(subTasks.get(0).getType());
            taskDao.updateById(taskEntity);
        }else {

            taskEntity = new TaskEntity();
            taskEntity.setOrderId(subTasks.get(0).getOrderId());
            taskEntity.setAddress(orderSalesEntity.getAddress());
            taskEntity.setGuige(orderSalesEntity.getGuiges().get(0).getGuige());
            taskEntity.setMakeCount(count);
            taskEntity.setAllCount(orderSalesEntity.getGuiges().get(0).getCount());
            taskEntity.setType(subTasks.get(0).getType());
            taskEntity.setStatus(0);
            taskDao.insert(taskEntity);
        }

        doAddSubTask(subTasks);

        return ResponseDTO.ok(true);
    }


    private void doAddSubTask(List<SubTaskAddEle> subTasks) {
        Long orderId = subTasks.get(0).getOrderId(); // 获取订单ID，从第一个子任务中取
        String mark = subTasks.get(0).getMark();
        int type = subTasks.get(0).getType();
        // 更新deletedFlag
        subTaskDao.updateDeletedFlag(orderId, mark);
        if(type == 0){
            subTaskDao.updateDeletedFlagByType(orderId, 1);
        }
        else {
            subTaskDao.updateDeletedFlagByType(orderId, 0);
        }

        // 批量插入
        List<SubTaskEntity> entities = subTasks.stream()
                .filter(subTask -> subTask.getCount() > 0) // 仅保留 count > 0 的 subTask
                .map(subTask -> {
                    SubTaskEntity entity = new SubTaskEntity();
                    entity.setTaskId(subTask.getTaskId());
                    entity.setUserId(subTask.getUserId());
                    entity.setOrderId(subTask.getOrderId());
                    entity.setUserName(subTask.getUserName());
                    entity.setMark(subTask.getMark());
                    entity.setCount(subTask.getCount());
                    entity.setStatus(0); // 可以根据需要设置状态
                    entity.setType(subTask.getType());
                    entity.setDeletedFlag(false); // 新插入的任务
                    return entity;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entities)) {
            subTaskDao.batchInsertSubTasks(entities);
        }

    }

}