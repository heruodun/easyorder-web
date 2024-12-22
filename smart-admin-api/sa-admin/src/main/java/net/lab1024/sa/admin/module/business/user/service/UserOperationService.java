package net.lab1024.sa.admin.module.business.user.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lab1024.sa.admin.module.business.user.dao.UserOperationDao;
import net.lab1024.sa.admin.module.business.user.domain.entity.UserOperationEntity;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationAddForm;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationQueryForm;
import net.lab1024.sa.admin.module.business.user.domain.form.UserOperationUpdateForm;
import net.lab1024.sa.admin.module.business.user.domain.vo.UserOperationVO;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * dahang Service
 *
 * @Author dahang
 * @Date 2024-12-12 23:45:43
 * @Copyright dahang
 */

@Service
public class UserOperationService {

    @Resource
    private UserOperationDao userOperationDao;


    public int updateUserOperation(LocalDateTime now, RequestUser operator, String operationStr,
                                   Long orderId, String guige, Integer count, String danwei){
        Long userId = operator.getUserId();
        String userName = operator.getUserName();
        Map map = new HashMap<String, Object>();
        map.put("order_id", orderId);
        map.put("operator_id", userId);
        map.put("operation", operationStr);

        List<UserOperationEntity> list = userOperationDao.selectByMap(map);
        if (list == null || list.size() == 0) {
            UserOperationEntity userOperationEntity = new UserOperationEntity();
            userOperationEntity.setOperation(operationStr);
            userOperationEntity.setCreateTime(now);
            userOperationEntity.setUpdateTime(now);
            userOperationEntity.setOrderId(orderId);
            userOperationEntity.setGuige(guige);
            userOperationEntity.setDanwei(danwei);
            userOperationEntity.setCount(count);

            userOperationEntity.setOperator(userName);
            userOperationEntity.setOperatorId(userId);
            int insertCount = 0;
            try {
                insertCount = userOperationDao.insert(userOperationEntity);
            }catch (Exception e){
                //todo 异常处理
            }
            return insertCount;
        }

        return 0;
    }

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<UserOperationVO> queryPage(UserOperationQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<UserOperationVO> list = userOperationDao.queryPage(page, queryForm);
        PageResult<UserOperationVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(UserOperationAddForm addForm) {
        UserOperationEntity userOperationEntity = SmartBeanUtil.copy(addForm, UserOperationEntity.class);
        userOperationDao.insert(userOperationEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(UserOperationUpdateForm updateForm) {
        UserOperationEntity userOperationEntity = SmartBeanUtil.copy(updateForm, UserOperationEntity.class);
        userOperationDao.updateById(userOperationEntity);
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

        userOperationDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        userOperationDao.updateDeleted(id,true);
        return ResponseDTO.ok();
    }
}
