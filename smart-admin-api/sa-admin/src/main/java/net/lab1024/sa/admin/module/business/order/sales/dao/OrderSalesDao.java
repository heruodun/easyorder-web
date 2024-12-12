package net.lab1024.sa.admin.module.business.order.sales.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderSalesVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 销售订单 Dao
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Mapper
@Component
public interface OrderSalesDao extends BaseMapper<OrderSalesEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<OrderSalesVO> queryPage(Page page, @Param("queryForm") OrderSalesQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("${deletedFlag}")boolean deletedFlag);
    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("${deletedFlag}")boolean deletedFlag);

}
