package net.lab1024.sa.admin.module.business.address.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.address.domain.entity.AddressEntity;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressQueryForm;
import net.lab1024.sa.admin.module.business.address.domain.vo.AddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 地址
 */
@Mapper
@Component
public interface AddressDao extends BaseMapper<AddressEntity> {

    /**
     * 分页 查询地址
     *
     */
    List<AddressVO> query(Page page, @Param("query") AddressQueryForm query);

    /**
     * 批量更新删除状态
     */

    void batchUpdateDeleted(@Param("addressIdList")List<Long> addressIdList,@Param("deletedFlag")Boolean deletedFlag);
}
