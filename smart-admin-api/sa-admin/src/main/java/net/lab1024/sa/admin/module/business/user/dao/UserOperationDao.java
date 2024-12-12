package net.lab1024.sa.admin.module.business.user.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.user.domain.entity.UserOperationEntity;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationQueryForm;
import net.lab1024.sa.admin.module.business.user.domain.vo.UserOperationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * dahang Dao
 *
 * @Author dahang
 * @Date 2024-12-12 23:45:43
 * @Copyright dahang
 */

@Mapper
@Component
public interface UserOperationDao extends BaseMapper<UserOperationEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<UserOperationVO> queryPage(Page page, @Param("queryForm") UserOperationQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("${deletedFlag}")boolean deletedFlag);
    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("${deletedFlag}")boolean deletedFlag);

}
