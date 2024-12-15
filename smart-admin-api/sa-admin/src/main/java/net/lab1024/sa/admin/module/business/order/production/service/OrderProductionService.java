package net.lab1024.sa.admin.module.business.order.production.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.production.dao.OrderProductionDao;
import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionAddForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionUpdateForm;
import net.lab1024.sa.admin.module.business.order.production.domain.vo.OrderProductionVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

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

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<OrderProductionVO> queryPage(OrderProductionQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrderProductionVO> list = orderProductionDao.queryPage(page, queryForm);
        PageResult<OrderProductionVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrderProductionAddForm addForm) {
        OrderProductionEntity orderProductionEntity = SmartBeanUtil.copy(addForm, OrderProductionEntity.class);
        orderProductionDao.insert(orderProductionEntity);
        return ResponseDTO.ok();
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
