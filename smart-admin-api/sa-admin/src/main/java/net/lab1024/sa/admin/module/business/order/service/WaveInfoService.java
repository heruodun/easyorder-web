package net.lab1024.sa.admin.module.business.order.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.alibaba.fastjson2.JSONObject;
import net.lab1024.sa.admin.module.business.order.dao.WaveInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.WaveInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoUpdateForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 波次 Service
 *
 * @Author dahang
 * @Date 2024-05-08 17:22:47
 * @Copyright dahang
 */

@Service
public class WaveInfoService {

    @Resource
    private WaveInfoDao waveInfoDao;

    /**
     *
     * @return
     */
    public List<WaveInfoVO> queryPage(String curDate) {

        List<WaveInfoVO> resultList = new ArrayList<>();
//        / 解析日期字符串
        LocalDate date = LocalDate.parse(curDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 计算当日0点
        LocalDateTime startOfDay = date.atStartOfDay();
        // 计算次日0点
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        // 转换为SQL可用的字符串格式（这里例子假设数据库使用的是DateTime格式）
        String startOfDayStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startOfDay);
        String startOfNextDayStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startOfNextDay);
        List<WaveInfoEntity> list = waveInfoDao.queryPage(startOfDayStr, startOfNextDayStr);
        int[] waveIds = new int[list.size()];
        int i = 0;
        for(WaveInfoEntity waveInfoVO : list){
            waveIds[i++] = waveInfoVO.getWaveId();
        }

        Map<Integer, Object> map = WaveHttpService.get(waveIds);
        for (WaveInfoEntity waveInfoEntity : list) {
            WaveInfoVO waveInfoVO = SmartBeanUtil.copy(waveInfoEntity, WaveInfoVO.class);

            if(map != null && map.get(waveInfoEntity.getWaveId()) != null) {
                waveInfoVO.setWaveDetail( map.get(waveInfoEntity.getWaveId()));
            }
            else {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("addresses", new ArrayList<>());
                map1.put("totalCount", 0);
                map1.put("addressCount", 0);
                waveInfoVO.setWaveDetail(map1);
            }
            resultList.add(waveInfoVO);
        }

        return resultList;
    }


    /**
     * 添加
     */
    public ResponseDTO<WaveInfoVO> add(WaveInfoAddForm addForm) {
        // 构造波次别名为当前时间的日期+时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmm");
        String waveAlias = LocalDateTime.now().format(formatter);

        // 把表单数据复制到实体对象中
        WaveInfoEntity waveInfoEntity = SmartBeanUtil.copy(addForm, WaveInfoEntity.class);
        // 设置波次别名
        waveInfoEntity.setWaveAlias(waveAlias);

        // 插入数据库操作
        waveInfoDao.insertWaveInfo(waveInfoEntity);

        WaveInfoEntity insertedEntity = waveInfoDao.selectById(waveInfoEntity.getWaveId());

        // 将实体对象转换为VO对象
        WaveInfoVO waveInfoVO = SmartBeanUtil.copy(insertedEntity, WaveInfoVO.class);

        // 返回带有新插入波次信息的ResponseDTO
        return ResponseDTO.ok(waveInfoVO);
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(WaveInfoUpdateForm updateForm) {
        WaveInfoEntity waveInfoEntity = SmartBeanUtil.copy(updateForm, WaveInfoEntity.class);
        waveInfoDao.updateById(waveInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        waveInfoDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer waveId) {
        if (null == waveId){
            return ResponseDTO.ok();
        }

        waveInfoDao.deleteById(waveId);
        return ResponseDTO.ok();
    }
}