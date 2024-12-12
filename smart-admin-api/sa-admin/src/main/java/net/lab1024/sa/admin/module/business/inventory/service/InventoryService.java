package net.lab1024.sa.admin.module.business.inventory.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.inventory.dao.InventoryDao;
import net.lab1024.sa.admin.module.business.inventory.domain.entity.InventoryEntity;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryAddForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryQueryForm;
import net.lab1024.sa.admin.module.business.inventory.domain.form.InventoryUpdateForm;
import net.lab1024.sa.admin.module.business.inventory.domain.vo.InventoryVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 库存 Service
 *
 * @Author dahang
 * @Date 2024-12-12 23:48:08
 * @Copyright dahang
 */

@Service
public class InventoryService {

    @Resource
    private InventoryDao inventoryDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<InventoryVO> queryPage(InventoryQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<InventoryVO> list = inventoryDao.queryPage(page, queryForm);
        PageResult<InventoryVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(InventoryAddForm addForm) {
        InventoryEntity inventoryEntity = SmartBeanUtil.copy(addForm, InventoryEntity.class);
        inventoryDao.insert(inventoryEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(InventoryUpdateForm updateForm) {
        InventoryEntity inventoryEntity = SmartBeanUtil.copy(updateForm, InventoryEntity.class);
        inventoryDao.updateById(inventoryEntity);
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

        inventoryDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        inventoryDao.updateDeleted(id,true);
        return ResponseDTO.ok();
    }
}
