package net.lab1024.sa.admin.module.business.address.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.address.dao.AddressDao;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressAddForm;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressUpdateForm;
import net.lab1024.sa.admin.module.business.address.domain.vo.AddressVO;
import net.lab1024.sa.admin.module.business.category.service.CategoryQueryService;
import net.lab1024.sa.admin.module.business.address.domain.entity.AddressEntity;
import net.lab1024.sa.admin.module.business.address.domain.form.AddressQueryForm;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartSearchUtil;
import net.lab1024.sa.base.module.support.datatracer.constant.DataTracerTypeEnum;
import net.lab1024.sa.base.module.support.datatracer.service.DataTracerService;
import net.lab1024.sa.base.module.support.dict.service.DictCacheService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 地址
 *
 */
@Service
@Slf4j
public class AddressService implements InitializingBean {

    @Resource
    private AddressDao addressDao;

    @Resource
    private CategoryQueryService categoryQueryService;

    @Resource
    private DataTracerService dataTracerService;

    @Resource
    private DictCacheService dictCacheService;

    private ConcurrentHashMap<String, Long> addressMap = new ConcurrentHashMap();

    /**
     * 添加地址
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(AddressAddForm addForm) {
        addForm.setPlace(addForm.getPlace().trim());
        ResponseDTO<String> res = this.checkAddress(addForm);
        if (!res.getOk()) {
            return res;
        }
        AddressEntity addressEntity = SmartBeanUtil.copy(addForm, AddressEntity.class);
        addressEntity.setDeletedFlag(Boolean.FALSE);
        addressDao.insert(addressEntity);
        dataTracerService.insert(addressEntity.getAddressId(), DataTracerTypeEnum.ADDRESS);
        addressMap.put(addressEntity.getPlace(), addressEntity.getAddressId());
        return ResponseDTO.ok();
    }

    /**
     * 更新地址
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(AddressUpdateForm updateForm) {
        AddressEntity originEntity = addressDao.selectById(updateForm.getAddressId());
        if(updateForm.getPlace()!=null){
            updateForm.setPlace(updateForm.getPlace().trim());
        }
        AddressEntity addressEntity = SmartBeanUtil.copy(updateForm, AddressEntity.class);
        addressDao.updateById(addressEntity);
        dataTracerService.update(updateForm.getAddressId(), DataTracerTypeEnum.ADDRESS, originEntity, addressEntity);
        addressMap.remove(originEntity.getPlace());
        addressMap.put(addressEntity.getPlace(), addressEntity.getAddressId());
        return ResponseDTO.ok();
    }

    /**
     * 添加/更新 地址校验
     */
    private ResponseDTO<String> checkAddress(AddressAddForm addForm) {
        String place = addForm.getPlace();
        Map<String, Object> p = new HashMap<>();
        p.put("place", place);
        List<AddressEntity> list = addressDao.selectByMap(p);
        if(list != null && list.size() > 0){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "地址已存在~");
        }

        return ResponseDTO.ok();
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long goodsId) {
        AddressEntity addressEntity = addressDao.selectById(goodsId);
        if (addressEntity == null) {
            return ResponseDTO.userErrorParam("地址不存在");
        }

        batchDelete(Collections.singletonList(goodsId));
        dataTracerService.batchDelete(Collections.singletonList(goodsId), DataTracerTypeEnum.ADDRESS);
        addressMap.remove(addressEntity.getPlace());
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> goodsIdList) {
        if (CollectionUtils.isEmpty(goodsIdList)) {
            return ResponseDTO.ok();
        }

        addressDao.batchUpdateDeleted(goodsIdList, Boolean.TRUE);
        return ResponseDTO.ok();
    }


    /**
     * 分页查询
     */
    public ResponseDTO<PageResult<AddressVO>> query(AddressQueryForm queryForm) {
        queryForm.setDeletedFlag(false);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<AddressVO> list = addressDao.query(page, queryForm);
        PageResult<AddressVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }



    public Set<String> fquery(String keyword){
        Set<String> set = SmartSearchUtil.search(keyword, addressMap.keySet());
        return set;
    }

    public Long getAddressId(String place){
        return addressMap.get(place);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        refresh();
    }

    @Scheduled(fixedRate = 60000)
    public void refresh(){
        List<AddressEntity> list = addressDao.selectByMap(new HashMap<>());
        for(AddressEntity entity:list){
            addressMap.put(entity.getPlace().trim(), entity.getAddressId());
        }
    }


}
