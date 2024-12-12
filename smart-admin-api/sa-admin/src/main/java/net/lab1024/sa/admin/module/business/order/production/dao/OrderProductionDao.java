package net.lab1024.sa.admin.module.business.order.production.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderProductionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 生产订单 Dao
 *
 * @Author dahang
 * @Date 2024-12-12 23:44:48
 * @Copyright dahang
 */

@Mapper
@Component
public interface OrderProductionDao extends BaseMapper<OrderProductionEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<OrderProductionVO> queryPage(Page page, @Param("queryForm") OrderProductionQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("${deletedFlag}")boolean deletedFlag);
    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("${deletedFlag}")boolean deletedFlag);

}
