package net.lab1024.sa.admin.module.business.order.sales.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lab1024.sa.admin.module.business.inventory.dao.InventoryDao;
import net.lab1024.sa.admin.module.business.inventory.domain.entity.InventoryEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesAddForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesUpdateForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.user.dao.UserOperationDao;
import net.lab1024.sa.admin.module.business.user.domain.entity.UserOperationEntity;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleEntity;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleService;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 销售订单 Service
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Service
public class OrderSalesService {

    @Resource
    private OrderSalesDao orderSalesDao;
    @Resource
    private UserOperationDao userOperationDao;
    @Resource
    private InventoryDao inventoryDao;
    @Resource
    private RoleService roleService;


    public OrderSalesEntity getByOrderId(Long orderId) {
        OrderSalesEntity orderSalesEntity = new OrderSalesEntity();
        orderSalesEntity.setOrderId(orderId);
        return orderSalesDao.selectOne(orderSalesEntity);
    }

    @Transactional
    public ResponseDTO<Boolean> scanOrder(OrderScanForm orderScanForm, OrderTypeAndIdVO orderInfo){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        RequestUser operator = requestUser;
        Long orderId = orderInfo.getOrderId();
        OrderSalesEntity orderSalesEntity = getByOrderId(orderId);
        if(orderSalesEntity == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        RoleEntity curRole = roleService.getRoleByRoleCode(orderScanForm.getOperation());
        LocalDateTime now = LocalDateTime.now();
        String userName = operator == null || operator.getUserName() == null ? "管理人" : operator.getUserName();
        Long userId = operator == null || operator.getUserId() == null ? 1L : operator.getUserId();
        Long id = orderSalesEntity.getId();

        String roleName = curRole.getRoleName();

        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation(orderScanForm.getOperation());
        traceElementEntity.setTime(now);
        traceElementEntity.setOperator(userName);

        orderSalesEntity.getTrace().add(traceElementEntity);

        int updateCount = orderSalesDao.updateScanInfo(roleName, userName,
                userId, now, orderSalesEntity.getTrace(), id);

        Map map = new HashMap<String, Object>();
        map.put("order_id", orderId);
        map.put("operator_id", userId);
        map.put("operation", roleName);

        List<UserOperationEntity> list = userOperationDao.selectByMap(map);
        if(list == null || list.size() == 0) {
            UserOperationEntity userOperationEntity = new UserOperationEntity();
            userOperationEntity.setOperation(roleName);
            userOperationEntity.setCreateTime(now);
            userOperationEntity.setOrderId(orderId);
            userOperationEntity.setCount(orderSalesEntity.getCount());
            userOperationEntity.setGuige(orderSalesEntity.getGuige());
            userOperationEntity.setDanwei(orderSalesEntity.getDanwei());
            userOperationEntity.setDetail(orderSalesEntity.getDetail());
            userOperationEntity.setOperator(userName);
            userOperationEntity.setOperatorId(userId);
            int insertCount = userOperationDao.insert(userOperationEntity);
        }

        //todo 异步入库出库

        InventoryEntity inventory = new InventoryEntity();
        inventory.setCount(orderSalesEntity.getCount());
        inventory.setGuige(orderSalesEntity.getGuige());
        inventory.setDanwei(orderSalesEntity.getDanwei());
        inventory.setDetail(orderSalesEntity.getDetail());
        inventory.setOrderId(orderId);
        inventory.setType(1);
        inventory.setOperator(userName);
        inventory.setOperatorId(userId);

        inventoryDao.insertOrUpdate(inventory);

        if(updateCount <= 0){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "扫码失败，请重试~");
        }
        return ResponseDTO.ok();
    }

    //不同操作码，对应不同的逻辑


    public List<OrderSalesVO> queryByWaveId(Integer waveId) {
        List<OrderSalesVO> list = orderSalesDao.queryByWaveId(waveId);
        return list;
    }

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<OrderSalesVO> queryPage(OrderSalesQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrderSalesVO> list = orderSalesDao.queryPage(page, queryForm);
        PageResult<OrderSalesVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrderSalesAddForm addForm) {
        OrderSalesEntity orderSalesEntity = SmartBeanUtil.copy(addForm, OrderSalesEntity.class);
        orderSalesDao.insert(orderSalesEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(OrderSalesUpdateForm updateForm) {
        OrderSalesEntity orderSalesEntity = SmartBeanUtil.copy(updateForm, OrderSalesEntity.class);
        orderSalesDao.updateById(orderSalesEntity);
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

        orderSalesDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        orderSalesDao.updateDeleted(id,true);
        return ResponseDTO.ok();
    }
}
