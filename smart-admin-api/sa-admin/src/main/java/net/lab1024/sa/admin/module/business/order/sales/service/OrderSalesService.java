package net.lab1024.sa.admin.module.business.order.sales.service;

import java.time.LocalDateTime;
import java.util.*;

import com.alibaba.fastjson2.JSONArray;
import net.lab1024.sa.admin.module.business.address.service.AddressService;
import net.lab1024.sa.admin.module.business.inventory.dao.InventoryDao;
import net.lab1024.sa.admin.module.business.inventory.domain.entity.InventoryEntity;
import net.lab1024.sa.admin.module.business.order.constant.OrderTypeEnum;
import net.lab1024.sa.admin.module.business.order.constant.QrTypeEnum;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderTiaoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.constant.OrderSalesUtil;
import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesAddForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesUpdateForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesAddVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveAddressVO;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveDetailVO;
import net.lab1024.sa.admin.module.business.order.sales.repository.OrderSalesESRepository;
import net.lab1024.sa.admin.module.business.order.task.constant.TaskStatusEnum;
import net.lab1024.sa.admin.module.business.order.task.service.TaskService;
import net.lab1024.sa.admin.module.business.user.dao.UserOperationDao;
import net.lab1024.sa.admin.module.business.user.domain.entity.UserOperationEntity;
import net.lab1024.sa.admin.module.business.user.service.UserOperationService;
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
import net.lab1024.sa.base.module.support.serialnumber.constant.SerialNumberIdEnum;
import net.lab1024.sa.base.module.support.serialnumber.service.SerialNumberService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
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
    private UserOperationService userOperationService;
    @Resource
    private InventoryDao inventoryDao;
    @Resource
    private RoleService roleService;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private AddressService addressService;
    @Resource
    private TaskService taskService;
    @Resource
    private OrderESService orderESService;


    public OrderSalesEntity getByOrderId(Long orderId) {
        OrderSalesEntity orderSalesEntity = new OrderSalesEntity();
        orderSalesEntity.setOrderId(orderId);
        return orderSalesDao.selectOne(orderSalesEntity);
    }

    public OrderSalesEntity getById(Long id) {
        return orderSalesDao.selectById(id);
    }


    public ResponseDTO<Boolean> scanOrder(OrderScanForm orderScanForm, OrderTypeAndIdVO orderInfo){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        RequestUser operator = requestUser;
        Long orderId = orderInfo.getOrderId();
        OrderSalesEntity orderSalesEntity = getByOrderId(orderId);
        if(orderSalesEntity == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }

        //operation 默认都走以下逻辑，其他case走特殊逻辑
        if(orderScanForm.getOperation().equals("chaxuan")){
            return ResponseDTO.error(OrderErrorCode.NO_PERMISSION, "非法扫码操作~");
        }

        else {
            LocalDateTime now = LocalDateTime.now();
            RoleEntity curRole = roleService.getRoleByRoleCode(orderScanForm.getOperation());
            int updateCount = updateOrderAndUserOperation(now, operator, curRole.getRoleName(),  null, orderSalesEntity);


            if (updateCount <= 0) {
                return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "扫码失败，请重试~");
            }

            if(curRole.getRoleCode().equals("duijie")){
                taskService.updateStatus(orderId, TaskStatusEnum.DONE.getStatus(), Math.toIntExact(operator.getUserId()));
            }

            //添加ES

            orderESService.asyncData(Arrays.asList(orderSalesEntity.getId()));

            return ResponseDTO.ok();
        }
    }


    @Transactional
    public int updateOrderAndUserOperation(LocalDateTime now, RequestUser operator, String operationStr,
                                           String detail, OrderSalesEntity orderSalesEntity){
            int updateOrder = updateScanInfo(now, operator, operationStr, detail, orderSalesEntity);
            int updateUserOperation = userOperationService.updateUserOperation(now, operator, operationStr,
                    orderSalesEntity.getOrderId(), "", 1, "单");
            return updateOrder;
    }

    @Transactional
    public int updateOrderAndUserOperationWithWave(LocalDateTime now, RequestUser operator, String operation,
                                                   Integer waveId,String detail, OrderSalesEntity orderSalesEntity){
        int updateOrder = updateScanInfoWithWave(now, operator, operation,waveId, detail, orderSalesEntity);
        int updateUserOperation = userOperationService.updateUserOperation(now, operator, operation,
                orderSalesEntity.getOrderId(), "", 1,"单");
        return updateOrder;
    }





    private int updateScanInfo(LocalDateTime now, RequestUser operator, String operationStr,String detail,
                               OrderSalesEntity orderSalesEntity){

        String userName = operator == null || operator.getUserName() == null ? "工 人" : operator.getUserName();
        if(operationStr == null){
            operationStr = "服务";
        }
        Long userId = operator == null || operator.getUserId() == null ? 0L : operator.getUserId();
        Long id = orderSalesEntity.getId();

        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation(operationStr);
        traceElementEntity.setTime(now);
        if(detail != null) {
            traceElementEntity.setDetail(detail);
        }
        traceElementEntity.setOperator(userName);
        //加到头部，保证倒序
        orderSalesEntity.getTrace().add(0, traceElementEntity);

        int updateCount = orderSalesDao.updateScanInfo(operationStr, userName,
                userId, now, orderSalesEntity.getTrace(),null, id);
        return updateCount;
    }

    public int updateScanInfoWithWave(LocalDateTime now, RequestUser operator, String operation,Integer waveId,
                                      String detail, OrderSalesEntity orderSalesEntity){

        String userName = operator == null || operator.getUserName() == null ? "工 人" : operator.getUserName();
        if(operation == null){
            operation = "服务";
        }
        Long userId = operator == null || operator.getUserId() == null ? 0L : operator.getUserId();
        Long id = orderSalesEntity.getId();

        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation(operation);
        traceElementEntity.setTime(now);
        traceElementEntity.setOperator(userName);
        traceElementEntity.setDetail(detail);
        //加到头部，保证倒序
        orderSalesEntity.getTrace().add(0, traceElementEntity);

        int updateCount = orderSalesDao.updateScanInfo(operation, userName,
                userId, now, orderSalesEntity.getTrace(),waveId, id);
        return updateCount;
    }

    //不同操作码，对应不同的逻辑


    public List<OrderSalesVO> queryByWaveId(Integer waveId) {
        List<OrderSalesEntity> list = orderSalesDao.queryByWaveId(waveId);
        List<OrderSalesVO> listVO = SmartBeanUtil.copyList(list, OrderSalesVO.class);
        return listVO;
    }

    private Map<String, WaveAddressVO> groupOrdersByAddress(List<OrderSalesVO> orderSalesList) {
        Map<String, WaveAddressVO> addressMap = new HashMap<>();

        for (OrderSalesVO order : orderSalesList) {
            String address = order.getAddress(); // 获取订单地址

            // 如果地址不存在于 map 中，创建新的 WaveAddressVO
            if (!addressMap.containsKey(address)) {
                WaveAddressVO waveAddressVO = new WaveAddressVO();
                waveAddressVO.setAddress(address);
                waveAddressVO.setOrderCount(0); // 初始化订单数量
                waveAddressVO.setOrders(new ArrayList<>()); // 初始化订单列表
                addressMap.put(address, waveAddressVO); // 将新的 WaveAddressVO 添加到 map
            }

            // 更新对应 address 的 WaveAddressVO
            WaveAddressVO existingWaveAddress = addressMap.get(address);
            existingWaveAddress.setOrderCount(existingWaveAddress.getOrderCount() + 1); // 增加订单数量
            existingWaveAddress.getOrders().add(order); // 添加订单到列表
        }

        return addressMap;
    }



    public Map<Integer, WaveDetailVO> queryByWaveIdsOrderByAddress(int[] waveIds) {
        Map<Integer, List<OrderSalesVO>> map = queryByWaveIds(waveIds);
        Map<Integer, WaveDetailVO> resultMap = new HashMap<>();

        for (Integer key : map.keySet()) {
            List<OrderSalesVO> value = map.get(key);
            Map<String, WaveAddressVO>  addressMap = groupOrdersByAddress(value);
            WaveDetailVO waveDetailVO = new WaveDetailVO();
            waveDetailVO.setAddressCount(addressMap.size());
            waveDetailVO.setTotalCount(value.size());

            List<WaveAddressVO> list = new ArrayList<>();
            for(String address : addressMap.keySet()){
                WaveAddressVO waveAddressVO = addressMap.get(address);
                list.add(waveAddressVO);
            }

            waveDetailVO.setOrders(list);
            resultMap.put(key, waveDetailVO);
        }
        return resultMap;
    }

    public Map<Integer, List<OrderSalesVO>> queryByWaveIds(int[] waveIds) {
        List<OrderSalesEntity> list = orderSalesDao.queryByWaveIds(waveIds);
        List<OrderSalesVO> orderSalesVOList = SmartBeanUtil.copyList(list, OrderSalesVO.class);
        Map<Integer, List<OrderSalesVO>> map = new HashMap<>();
        for (OrderSalesVO orderSalesVO : orderSalesVOList) {
            String detail = OrderSalesUtil.getDetail(orderSalesVO);
            orderSalesVO.setDetail(detail);
            Integer waveId = orderSalesVO.getWaveId();
            List<OrderSalesVO> waveDetailVOList = map.get(waveId);
            if (waveDetailVOList == null) {
                waveDetailVOList = new ArrayList<>();
                waveDetailVOList.add(orderSalesVO);
                map.put(waveId, waveDetailVOList);
            }
            else {
                waveDetailVOList.add(orderSalesVO);
            }
        }
        return map;
    }


    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<OrderSalesVO> queryPage(OrderSalesQueryForm queryForm) {
        int offset = (int) ((queryForm.getPageNum() - 1) * queryForm.getPageSize());
        int limit = Math.toIntExact(queryForm.getPageSize());

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        //guige搜索处理成大写
        if(queryForm.getGuige() != null) {
            queryForm.setGuige(queryForm.getGuige().toUpperCase().trim());
        }

        String place = queryForm.getAddress();
        if(StringUtils.isNotBlank(place)){
            Long addressId = addressService.getAddressId(place.trim());
            if(addressId == null) {
                //无地址id 默认-1
                addressId = -1L;
            }

            queryForm.setAddressId(Math.toIntExact(addressId));
        }

        List<OrderSalesEntity>  orderSalesEntities = orderSalesDao.queryPage(queryForm, limit, offset);
        List<OrderSalesVO> list = SmartBeanUtil.copyList(orderSalesEntities, OrderSalesVO.class);
        int count = orderSalesDao.querySize(queryForm);
        page.setTotal(count);
        PageResult<OrderSalesVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }


    public ResponseDTO<OrderSalesAddVO> addOrder(OrderSalesAddForm addForm) {
        ResponseDTO<OrderSalesAddVO> responseDTO = add(addForm);
        if(responseDTO.getOk()) {
           //add es
            OrderSalesAddVO orderSalesAddVO = responseDTO.getData();
            orderESService.asyncData(Arrays.asList(orderSalesAddVO.getId()));
        }
        return responseDTO;
    }

    /**
     * 添加
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<OrderSalesAddVO> add(OrderSalesAddForm addForm) {
        List<OrderGuigeEntity> orderGuigeEntityList = addForm.getGuiges();
        if(CollectionUtils.isEmpty(orderGuigeEntityList)){
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"无规格~");
        }

        for(OrderGuigeEntity orderGuigeEntity: orderGuigeEntityList){
            String danwei = orderGuigeEntity.getDanwei();
            Integer totalCount = orderGuigeEntity.getCount();
            if("条".equals(danwei)){
                List<OrderTiaoEntity> tiaos = orderGuigeEntity.getTiaos();
                if(CollectionUtils.isEmpty(tiaos)){
                    return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"无条数~");
                }
                int count = 0;
                Set<String> lengthSet = new HashSet<>();
                for(OrderTiaoEntity orderTiaoEntity: tiaos){
                    String length = orderTiaoEntity.getLength();
                    Integer count1 = orderTiaoEntity.getCount();
                    if(length == null || length.trim().length() == 0 || count1 == null){
                        return ResponseDTO.error(OrderErrorCode.PARAM_ERROR, "长度或数量为空~");
                    }

                    if(lengthSet.contains(length)){
                        return ResponseDTO.error(OrderErrorCode.PARAM_ERROR, "长度重复~");
                    }
                    else {
                        lengthSet.add(length.trim());
                    }

                    count += count1;
                }
                if(count != totalCount){
                    return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"长度或数量不对~");
                }

            }

        }

        //预处理 规格改成大写
        for(OrderGuigeEntity orderGuigeEntity: orderGuigeEntityList){
            orderGuigeEntity.setGuige(orderGuigeEntity.getGuige().toUpperCase().trim());
        }

        addForm.setAddress(addForm.getAddress().trim());

        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        OrderSalesEntity orderSalesEntity = SmartBeanUtil.copy(addForm, OrderSalesEntity.class);
        LocalDateTime now = LocalDateTime.now();
        //订单号
        Long orderId = Long.valueOf(serialNumberService.generate(SerialNumberIdEnum.SALES_ORDER, 1).get(0));
        orderSalesEntity.setOrderId(orderId);
        //address
        Long addressId = addressService.getAddressId(addForm.getAddress());
        if(addressId == null){
            return ResponseDTO.error(OrderErrorCode.PARAM_ERROR,"地址未添加到地址库~");
        }
        orderSalesEntity.setAddressId(Math.toIntExact(addressId));
        orderSalesEntity.setCurStatus("打单");
        orderSalesEntity.setCurOperator(requestUser.getUserName());
        orderSalesEntity.setCurOperatorId(requestUser.getUserId());
        orderSalesEntity.setCreator(requestUser.getUserName());
        orderSalesEntity.setCreatorId(requestUser.getUserId());
        orderSalesEntity.setDeletedFlag(false);
        orderSalesEntity.setCurTime(now);
        orderSalesEntity.setCreateTime(now);
        orderSalesEntity.setUpdateTime(now);

        orderSalesEntity.setTrace(new ArrayList<>());
        TraceElementEntity traceElementEntity = new TraceElementEntity();
        traceElementEntity.setOperation("打单");
        traceElementEntity.setTime(now);
        traceElementEntity.setOperator(requestUser.getUserName());
        orderSalesEntity.getTrace().add(traceElementEntity);

        orderSalesEntity.setGuiges(addForm.getGuiges());

        int ok = orderSalesDao.insert(orderSalesEntity);

        int updateCount = userOperationService.updateUserOperation(now, requestUser, "打单",
                orderSalesEntity.getOrderId(), "", 1, "单");
        if(ok > 0){
            OrderSalesAddVO orderSalesAddVO = SmartBeanUtil.copy(orderSalesEntity, OrderSalesAddVO.class);
            orderSalesAddVO.setQrCode(orderId + "$" + QrTypeEnum.V0.getVersion());
            return ResponseDTO.ok(orderSalesAddVO);
        }else {
            return ResponseDTO.error(OrderErrorCode.FORM_SUBMIT_FAIL);
        }

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
     * todo 添加轨迹
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        orderSalesDao.updateDeleted(id,true);

        orderESService.asyncData(Arrays.asList(id));
        return ResponseDTO.ok();
    }
}
