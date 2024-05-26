package net.lab1024.sa.admin.module.business.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.alibaba.fastjson2.JSONObject;
import net.lab1024.sa.admin.module.business.order.dao.WaveInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.WaveInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoAddDelShipForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoShipForm;
import net.lab1024.sa.admin.module.business.order.domain.form.WaveInfoUpdateForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.WaveInfoVO;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
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
        List<WaveInfoEntity> list = waveInfoDao.queryByTime(startOfDayStr, startOfNextDayStr);
        int[] waveIds = new int[list.size()];
        int i = 0;
        for(WaveInfoEntity waveInfoVO : list){
            waveIds[i++] = waveInfoVO.getWaveId();
        }

        Map<Integer, Object> map = WaveHttpService.get(waveIds);
        for (WaveInfoEntity waveInfoEntity : list) {
            resultList.add(getWaveInfoVO(waveInfoEntity, map));
        }

        return resultList;
    }

    private WaveInfoVO getWaveInfoVO(WaveInfoEntity waveInfoEntity, Map<Integer, Object> map){
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
        return waveInfoVO;
    }


    public WaveInfoVO queryById(Integer waveId) {

        WaveInfoEntity waveInfoEntity = waveInfoDao.queryById(waveId);
        if(waveInfoEntity == null){
            return new WaveInfoVO();
        }

        int[] waveIds = new int[]{waveInfoEntity.getWaveId()};

        Map<Integer, Object> map = WaveHttpService.get(waveIds);

        return getWaveInfoVO(waveInfoEntity, map);
    }

    public WaveInfoVO queryByOrderId(Long orderId) {

        JSONObject jsonObject = WaveHttpService.getOrder(orderId);
        if(jsonObject == null){
            return new WaveInfoVO();
        }
        Integer waveId = jsonObject.getInteger("wave_id");
        if(waveId == null || waveId <= 0){
            return new WaveInfoVO();
        }
        WaveInfoEntity waveInfoEntity = waveInfoDao.queryById(waveId);
        if(waveInfoEntity == null){
            return new WaveInfoVO();
        }

        int[] waveIds = new int[]{waveInfoEntity.getWaveId()};

        Map<Integer, Object> map = WaveHttpService.get(waveIds);

        return getWaveInfoVO(waveInfoEntity, map);
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
     * 添加
     */
    public ResponseDTO<Boolean> addDelShip(WaveInfoAddDelShipForm addShipForm) {
        int waveId = addShipForm.getWaveId();
        String shipIds = addShipForm.getShipIds();

        String[] addShipIdArr = shipIds.split(",");

        WaveInfoEntity entity = waveInfoDao.selectById(waveId);

        if(entity == null){
            return ResponseDTO.error(OrderErrorCode.DATA_NOT_EXIST);
        }

        StringBuilder sb = new StringBuilder();
        String originalShipIds = entity.getShipIds();
        if(addShipForm.getType() == 1) {
            Set<String> uniqueShipIds = getShipIds(originalShipIds, addShipIdArr);
            // 构建裁剪无重复元素的StringBuilder
            for (String shipId : uniqueShipIds) {
                sb.append(shipId).append(",");
            }
        }
        //删除
        else {
            if (originalShipIds != null) {
                String[] shipIdArr = originalShipIds.split(",");
                for (String s : shipIdArr) {
                    boolean f = false;
                    for (String ss : addShipIdArr) {
                        if(s.equals(ss)){
                            f = true;
                            break;
                        }
                    }
                    if(!f) {
                        if(s.length() > 0) {
                            sb.append(s).append(",");
                        }
                    }

                }
            }
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        int ok = waveInfoDao.updateWaveShip(waveId, sb.toString());

        if(ok > 0){
            return ResponseDTO.ok(Boolean.TRUE);
        }

        return ResponseDTO.error(OrderErrorCode.FORM_SUBMIT_FAIL);
    }

    @NotNull
    private static Set<String> getShipIds(String originalShipIds, String[] addShipIdArr) {
        Set<String> uniqueShipIds = new HashSet<>();  // 使用HashSet来存储唯一的shipId
        if (originalShipIds != null) {
            String[] shipIdArr = originalShipIds.split(",");
            for (String s : shipIdArr) {
                if (s.length() > 0) {
                    uniqueShipIds.add(s);  // 添加到Set中以保持唯一性
                }
            }
        }
        for (String s : addShipIdArr) {
            if (s.length() > 0) {
                uniqueShipIds.add(s);  // 添加到Set中以保持唯一性
            }
        }
        return uniqueShipIds;
    }

    /**
     * 送货
     */
    public ResponseDTO<Boolean> ship(WaveInfoShipForm shipForm) {
        WaveInfoEntity waveInfoEntity = waveInfoDao.selectById(shipForm.getWaveId());
        if(waveInfoEntity == null){
            return ResponseDTO.error(OrderErrorCode.DATA_NOT_EXIST);
        }
        LocalDateTime now = LocalDateTime.now();
        String nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now);
        int ok = waveInfoDao.updateWaveInfo(shipForm.getWaveId(), 1, nowStr);

        if(ok > 0){
            //update remote
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    WaveHttpService.ship(shipForm.getWaveId(), shipForm.getOperator());
                }
            });
            thread.start();
            return ResponseDTO.ok(Boolean.TRUE);
        }

        // 返回带有新插入波次信息的ResponseDTO
        return ResponseDTO.error(OrderErrorCode.FORM_SUBMIT_FAIL);
    }

    /**

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