package net.lab1024.sa.admin.module.business.order.sales.dao;

import java.time.LocalDateTime;
import java.util.List;

import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.base.common.domain.PageResult;
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
     * @param queryForm
     * @return
     */
    List<OrderSalesEntity> queryPage(@Param("queryForm") OrderSalesQueryForm queryForm,
                                       @Param("limit") int limit, @Param("offset") int offset);

    int querySize(@Param("queryForm") OrderSalesQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("${deletedFlag}")boolean deletedFlag);
    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("${deletedFlag}")boolean deletedFlag);

    /**
     * 查看订单 具体条件 看sql
     */
    OrderSalesEntity selectOne(OrderSalesEntity entity);

    int updateScanInfo(@Param("curStatus") String curStatus,
                       @Param("curOperator") String curOperator,
                       @Param("curOperatorId") Long curOperatorId,
                       @Param("curTime") LocalDateTime curTime,
                       @Param("trace") List<TraceElementEntity> trace,
                       @Param("waveId") Integer waveId,
                       @Param("id") Long id);

    int updateWaveId(@Param("waveId") Integer waveId, @Param("id") Long id);

    List<OrderSalesEntity> queryByWaveId(@Param("waveId") Integer waveId);

    List<OrderSalesEntity> queryByWaveIds(@Param("waveIds") int[] waveIds);

    int updateAddressId(@Param("addressId") Integer addressId, @Param("id") Long id);


}
