package net.lab1024.sa.admin.module.business.inventory.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.inventory.constant.InventoryOperationEnum;
import net.lab1024.sa.admin.module.business.inventory.dao.InventoryDao;
import net.lab1024.sa.admin.module.business.inventory.domain.entity.InventoryEntity;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryAddForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventorySummaryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryUpdateForm;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventorySummaryVO;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventoryVO;
import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionVO;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static net.lab1024.sa.admin.module.business.inventory.constant.InventoryOperationEnum.getInventoryOperation;
import static net.lab1024.sa.admin.module.business.inventory.constant.InventoryUtil.IN;
import static net.lab1024.sa.admin.module.business.inventory.constant.InventoryUtil.OUT;

/**
 * 库存 Service
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Slf4j
@Service
public class InventoryService {

    @Resource
    private InventoryDao inventoryDao;

    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 30,10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 异步写入库存，定时任务每晚更新校正一次
     * @param now
     * @param operator
     * @param operation
     * @return
     */
    public void updateInventory(LocalDateTime now, RequestUser operator, String operation, Integer type,
                                OrderProductionEntity orderProductionEntity){
//        poolExecutor.submit(new Runnable() {
//            @Override
//            public void run() {
        if(operation == null || !InventoryOperationEnum.operationCodeSet.contains(operation)){
            log.warn("operator= {}, operation = {} not invalid inventory operation", operator, operation);
            return;
        }

        List<InventoryOperationEnum> list = getInventoryOperation(operation, type);

        if(list.size() == 0){
            log.warn("operator= {}, operation = {}, type = {} not invalid inventory operation", operator,
                    operation, type);
            return;
        }

        for(InventoryOperationEnum inventoryOperationEnum : list) {
            int status = inventoryOperationEnum.getStatus();
            int inventoryType = inventoryOperationEnum.getType();

            InventoryEntity inventory = new InventoryEntity();
            inventory.setOrderId(orderProductionEntity.getOrderId());
            inventory.setType(inventoryType);
            inventory.setStatus(status);
            inventory.setCreateTime(now);
            inventory.setUpdateTime(now);
            inventory.setGuige(orderProductionEntity.getGuige());
            inventory.setCount(orderProductionEntity.getCount());
            inventory.setDanwei(orderProductionEntity.getDanwei());
            inventory.setRemark(orderProductionEntity.getRemark());
            int rowCount;
            if (status == IN) {
                inventory.setInMan(operator.getUserName());
                inventory.setInManId(Math.toIntExact(operator.getUserId()));
                inventory.setInTime(now);
                rowCount = inventoryDao.insertOrUpdateIn(inventory);

            } else {
                inventory.setOutMan(operator.getUserName());
                inventory.setOutManId(Math.toIntExact(operator.getUserId()));
                inventory.setOutTime(now);
                rowCount = inventoryDao.insertOrUpdateOut(inventory);
            }

            if (rowCount == 0) {
                log.warn("operator= {}, operation = {}, type = {}, orderId = {} ",
                        operator, operation, inventoryType, orderProductionEntity.getOrderId());
            }
        }
//            }
//        });

    }

    /**
     * 库存还有多少

     * @return
     */
    public PageResult<InventorySummaryVO> queryInSummary(InventorySummaryQueryForm queryForm) {

        int offset = (int) ((queryForm.getPageNum() - 1) * queryForm.getPageSize());
        int limit = Math.toIntExact(queryForm.getPageSize());

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        //guige搜索处理成大写
        if(queryForm.getGuige() != null) {
            queryForm.setGuige(queryForm.getGuige().toUpperCase().trim());
        }
        int status = IN;
        queryForm.setStatus(status);
        List<InventorySummaryVO> inventorySummaryVOS = inventoryDao.querySummaryPage(queryForm, limit, offset);

        List<InventorySummaryVO> list = SmartBeanUtil.copyList(inventorySummaryVOS, InventorySummaryVO.class);
        int count = inventoryDao.querySummarySize(queryForm);
        page.setTotal(count);
        PageResult<InventorySummaryVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;

    }


//    /**
//     * 出库了多少
//     * @param type
//     * @param guige
//     * @param startTime
//     * @param endTime
//     * @return
//     */
//    public List<InventorySummaryVO> queryOutSummary(Integer type, String guige,
//                                                   String startTime, String endTime) {
//        int status = OUT;
//        return inventoryDao.querySummaryPage(type, guige, status, startTime, endTime);
//    }


    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<InventoryVO> queryPage(InventoryQueryForm queryForm) {
        int offset = (int) ((queryForm.getPageNum() - 1) * queryForm.getPageSize());
        int limit = Math.toIntExact(queryForm.getPageSize());

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        //guige搜索处理成大写
        if(queryForm.getGuige() != null) {
            queryForm.setGuige(queryForm.getGuige().toUpperCase().trim());
        }
        List<InventoryEntity>  inventoryEntities = inventoryDao.queryPage(queryForm, limit, offset);
        List<InventoryVO> list = SmartBeanUtil.copyList(inventoryEntities, InventoryVO.class);
        int count = inventoryDao.querySize(queryForm);
        page.setTotal(count);
        PageResult<InventoryVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(InventoryAddForm addForm) {
        InventoryEntity inventoryEntity = SmartBeanUtil.copy(addForm, InventoryEntity.class);
        inventoryDao.insert(inventoryEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(InventoryUpdateForm updateForm) {
        InventoryEntity inventoryEntity = SmartBeanUtil.copy(updateForm, InventoryEntity.class);
        inventoryDao.updateById(inventoryEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        inventoryDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        inventoryDao.updateDeleted(id,true);
        return ResponseDTO.ok();
    }
}
