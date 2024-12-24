package net.lab1024.sa.admin.module.business.inventory.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.inventory.domain.entity.InventoryEntity;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventorySummaryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventorySummaryVO;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.production.domain.entity.OrderProductionEntity;
import net.lab1024.sa.admin.module.business.order.production.domain.form.OrderProductionQueryForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 库存 Dao
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Mapper
@Component
public interface InventoryDao extends BaseMapper<InventoryEntity> {

    /**
     * 分页 查询
     *
     * @param queryForm
     * @return
     */
    List<InventoryEntity> queryPage(@Param("queryForm") InventoryQueryForm queryForm,
                                          @Param("limit") int limit, @Param("offset") int offset);

    int querySize(@Param("queryForm") InventoryQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("${deletedFlag}")boolean deletedFlag);
    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("${deletedFlag}")boolean deletedFlag);

    int inUpdate(InventoryEntity inventory);

    int outUpdate(InventoryEntity inventory);

    List<InventorySummaryVO> querySummaryPage(@Param("queryForm") InventorySummaryQueryForm queryForm,
                                              @Param("limit") int limit, @Param("offset") int offset);
    int  querySummarySize(@Param("queryForm") InventorySummaryQueryForm queryForm);

}
