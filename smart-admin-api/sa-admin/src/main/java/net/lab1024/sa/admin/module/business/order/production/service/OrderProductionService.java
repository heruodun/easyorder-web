package net.lab1024.sa.admin.module.business.order.production.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.lab1024.sa.admin.module.business.inventory.service.InventoryService;
import net.lab1024.sa.admin.module.business.order.constant.OrderTypeEnum;
import net.lab1024.sa.admin.module.business.order.constant.QrTypeEnum;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderTiaoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.production.dao.OrderProductionDao;
import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionAddForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionUpdateForm;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionAddVO;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesAddVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.user.service.UserOperationService;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleEntity;
import net.lab1024.sa.admin.module.system.role.service.RoleService;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.serialnumber.constant.SerialNumberIdEnum;
import net.lab1024.sa.base.module.support.serialnumber.service.SerialNumberService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 生产订单 Service
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Service
public class OrderProductionService {

    @Resource
    private OrderProductionDao orderProductionDao;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private UserOperationService userOperationService;
    @Resource
    private RoleService roleService;
    @Resource
    private InventoryService inventoryService;


    public OrderProductionEntity getById(Long id) {
        return orderProductionDao.selectById(id);
    }

    public OrderProductionEntity getByOrderId(Long orderId) {
        OrderProductionEntity orderProductionEntity = new OrderProductionEntity();
        orderProductionEntity.setOrderId(orderId);
        return orderProductionDao.selectOne(orderProductionEntity);
    }

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<OrderProductionVO> queryPage(OrderProductionQueryForm queryForm) {
        int offset = (int) ((queryForm.getPageNum() - 1) * queryForm.getPageSize());
        int limit = Math.toIntExact(queryForm.getPageSize());

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        //guige搜索处理成大写
        if(queryForm.getGuige() != null) {
            queryForm.setGuige(queryForm.getGuige().toUpperCase().trim());
        }
        List<OrderProductionEntity>  orderProductionEntities = orderProductionDao.queryPage(queryForm, limit, offset);
        List<OrderProductionVO> list = SmartBeanUtil.copyList(orderProductionEntities, OrderProductionVO.class);
        int count = orderProductionDao.querySize(queryForm);
        page.setTotal(count);
        PageResult<OrderProductionVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<OrderProductionAddVO> add(OrderProductionAddForm addForm) {
        String guige = addForm.getGuige();
        Integer type = addForm.getType();
        if(StringUtils.isEmpty(guige)){
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"无规格");
        }
        if(type == null){
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"无类型字段");
        }

        //预处理 规格改成大写
        guige = guige.toUpperCase().trim();
        addForm.setGuige(guige);

        RequestUser requestUser = SmartRequestUtil.getRequestUser();

        OrderProductionEntity orderProductionEntity = SmartBeanUtil.copy(addForm, OrderProductionEntity.class);

        LocalDateTime now = LocalDateTime.now();
        //订单号
        SerialNumberIdEnum serialNumberIdEnum;
        if(type == OrderTypeEnum.FACTORY_ONE_PRODUCTION_BUCKET.getType()){
            serialNumberIdEnum = SerialNumberIdEnum.PRODUCTION_BUCKET_ORDER;
        }
        else if(type == OrderTypeEnum.FACTORY_ONE_PRODUCTION_BAG.getType()){
            serialNumberIdEnum = SerialNumberIdEnum.PRODUCTION_BAG_ORDER;
        }
        else if(type == OrderTypeEnum.FACTORY_ONE_PRODUCTION_BOX.getType()){
            serialNumberIdEnum = SerialNumberIdEnum.PRODUCTION_BOX_ORDER;
        }
        else if(type == OrderTypeEnum.FACTORY_ONE_PRODUCTION_DISK.getType()){
            serialNumberIdEnum = SerialNumberIdEnum.PRODUCTION_DISK_ORDER;
        }
        else {
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"错误类型字段");
        }

        Long orderId = Long.valueOf(serialNumberService.generate(serialNumberIdEnum, 1).get(0));


        orderProductionEntity.setOrderId(orderId);
        orderProductionEntity.setCurStatus("打单");
        orderProductionEntity.setCurOperator(requestUser.getUserName());
        orderProductionEntity.setCurOperatorId(requestUser.getUserId());
        orderProductionEntity.setCreator(requestUser.getUserName());
        orderProductionEntity.setCreatorId(requestUser.getUserId());
        orderProductionEntity.setDeletedFlag(false);
        orderProductionEntity.setCurTime(now);
        orderProductionEntity.setCreateTime(now);
        orderProductionEntity.setUpdateTime(now);

        orderProductionEntity.setTrace(new ArrayList<>());
        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation("打单");
        traceElementEntity.setTime(now);
        traceElementEntity.setOperator(requestUser.getUserName());
        orderProductionEntity.getTrace().add(traceElementEntity);

        orderProductionEntity.setGuige(guige);


        int ok = orderProductionDao.insert(orderProductionEntity);

        int updateCount = userOperationService.updateUserOperation(now, requestUser, "打单",
                orderProductionEntity.getOrderId(), orderProductionEntity.getGuige(), 1, "单");
        if(ok > 0){
            OrderProductionAddVO orderProductionAddVO = SmartBeanUtil.copy(orderProductionEntity, OrderProductionAddVO.class);
            orderProductionAddVO.setQrCode(orderId + "$" + QrTypeEnum.V0.getVersion());
            return ResponseDTO.ok(orderProductionAddVO);
        }else {
            return ResponseDTO.error(OrderErrorCode.FORM_SUBMIT_FAIL);
        }

    }

    @Transactional
    public ResponseDTO<Boolean> scanOrder(OrderScanForm orderScanForm, OrderTypeAndIdVO orderInfo){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        RequestUser operator = requestUser;
        Long orderId = orderInfo.getOrderId();
        OrderProductionEntity orderProductionEntity = getByOrderId(orderId);
        if(orderProductionEntity == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        //operation 默认都走以下逻辑，其他case走特殊逻辑
        if(orderScanForm.getOperation().equals("chaxuan")){
            return ResponseDTO.error(OrderErrorCode.NO_PERMISSION, "非法扫码操作~");
        }

        else {
            LocalDateTime now = LocalDateTime.now();
            RoleEntity curRole = roleService.getRoleByRoleCode(orderScanForm.getOperation());
            int updateCount = updateOrderAndUserOperation(now, operator, curRole.getRoleName(), orderProductionEntity);

            int type = orderInfo.getOrderType().getType();

            inventoryService.updateInventory(now, operator, orderScanForm.getOperation(), type, orderProductionEntity);
            if (updateCount <= 0) {
                return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "扫码失败，请重试~");
            }
            return ResponseDTO.ok();
        }
    }

    @Transactional
    public int updateOrderAndUserOperation(LocalDateTime now, RequestUser operator, String operation,
                                           OrderProductionEntity orderProductionEntity){
        int updateOrder = updateScanInfo(now, operator, operation, orderProductionEntity);
        int updateUserOperation = userOperationService.updateUserOperation(now, operator, operation,
                orderProductionEntity.getOrderId(), orderProductionEntity.getGuige(), orderProductionEntity.getCount()
        ,orderProductionEntity.getDanwei());
        return updateOrder;
    }

    public int updateScanInfo(LocalDateTime now, RequestUser operator, String operation,
                              OrderProductionEntity orderProductionEntity){

        String userName = operator == null || operator.getUserName() == null ? "工 人" : operator.getUserName();
        if(operation == null){
            operation = "服务";
        }
        Long userId = operator == null || operator.getUserId() == null ? 0L : operator.getUserId();
        Long id = orderProductionEntity.getId();

        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation(operation);
        traceElementEntity.setTime(now);
        traceElementEntity.setOperator(userName);
        //加到头部，保证倒序
        orderProductionEntity.getTrace().add(0, traceElementEntity);

        int updateCount = orderProductionDao.updateScanInfo(operation, userName,
                userId, now, orderProductionEntity.getTrace(),id);
        return updateCount;
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(OrderProductionUpdateForm updateForm) {
        OrderProductionEntity orderProductionEntity = SmartBeanUtil.copy(updateForm, OrderProductionEntity.class);
        orderProductionDao.updateById(orderProductionEntity);
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

        orderProductionDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        orderProductionDao.updateDeleted(id,true);
        return ResponseDTO.ok();
    }
}
