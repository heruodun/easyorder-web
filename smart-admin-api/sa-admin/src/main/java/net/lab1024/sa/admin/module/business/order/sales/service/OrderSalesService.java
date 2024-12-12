package net.lab1024.sa.admin.module.business.order.sales.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderSalesAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderSalesUpdateForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderSalesVO;
import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

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
