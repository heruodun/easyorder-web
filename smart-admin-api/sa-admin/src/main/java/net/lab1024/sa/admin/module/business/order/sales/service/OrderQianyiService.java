package net.lab1024.sa.admin.module.business.order.sales.service;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.address.service.AddressService;
import net.lab1024.sa.admin.module.business.order.constant.OrderTypeEnum;
import net.lab1024.sa.admin.module.business.order.constant.OrderUtil;
import net.lab1024.sa.admin.module.business.order.constant.QrTypeEnum;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderTiaoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.form.OrderSalesQueryForm;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveVO;
import net.lab1024.sa.admin.module.business.order.sales.repository.OrderSalesESRepository;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单迁移
 */

@Slf4j
@Service
public class OrderQianyiService {

    @Resource
    private OrderSalesDao orderSalesDao;
    @Resource
    private OrderSalesService orderSalesService;
    @Resource
    private AddressService addressService;
    @Resource
    private OrderESService orderESService;

    @Resource
    private OrderSalesESRepository orderSalesESRepository;


    // 每天凌晨4点执行
    @Scheduled(cron = "0 0 4 * * ?")
    public ResponseDTO<Long> syncData() {
        log.info("start sync es data...");
        int count = orderSalesDao.querySize(new OrderSalesQueryForm());
        log.info("start sync es data, data count is {}", count);
        long i = 1;
        for(; i <= count + 100; i++) {
            List<Long> ids = new ArrayList<>();
            ids.add(Long.valueOf(i));
            List<OrderSalesEntity> orders = orderSalesDao.selectBatchIds(ids);
            if(orders == null || orders.size() == 0) {
                continue;
            }
            for (OrderSalesEntity order : orders) {
                orderSalesESRepository.save(order);
            }
        }

        log.info("end sync es data, data count is {}", count);
        return ResponseDTO.ok(i);
    }


    /**
     * id INTEGER PRIMARY KEY,
     * order_id INTEGER,    末位添加0101
     * address TEXT,    不变
     * content TEXT,    加入detail
     *
     * cur_status TEXT, 不变
     * cur_man TEXT,  加入cur_operator
     * cur_time DATETIME, 不变
     *
     * printer TEXT, 加入creator
     * print_time TIMESTAMP, 加入create_time
     *
     * order_trace TEXT, 加入trace
     * update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  加入update_time
     * wave_id INTEGER 加入wave_id
     * sync_status INTEGER)''')
     *
     * @return
     */

    public static List<OrderGuigeEntity> parseContent(String content, OrderSalesEntity orderSalesEntity) {
        List<OrderGuigeEntity> orderList = new ArrayList<>();

        if (content.contains("条数")) {
            // 处理 content1
            OrderGuigeEntity order = new OrderGuigeEntity();

            // 提取规格
            Matcher specMatcher1 = Pattern.compile("总条数：(.+?)\n").matcher(content);
            if (specMatcher1.find()) {
                String count = specMatcher1.group(1).trim();
                try {
                    order.setCount(Integer.valueOf(count));
                }catch (Exception e){
                    log.error(orderSalesEntity.getOrderId() + " 总条数出错 [" + count + "]", e);
                    order.setCount(0);
                }
            }

            // 提取规格
            Matcher specMatcher = Pattern.compile("规格：(.+?)\n").matcher(content);
            if (specMatcher.find()) {
                order.setGuige(specMatcher.group(1).trim().toUpperCase());
            }

            order.setDanwei("条");

            // 解析长度和条数
            Matcher lengthMatcher = Pattern.compile("长度和条数：(.+?)\n").matcher(content);
            if (lengthMatcher.find()) {
                String[] lengthDetails = lengthMatcher.group(1).split("，");
                List<OrderTiaoEntity> tiaos = new ArrayList<>();
                for (String detail : lengthDetails) {
                    String[] parts = detail.trim().split(" x ");
                    if (parts.length == 2) {
                        OrderTiaoEntity tiao = new OrderTiaoEntity();
                        tiao.setLength(parts[0].trim());
                        int count1 = 0;
                        try{
                         count1 = Integer.parseInt(parts[1].trim());
                        }catch (Exception e){
                            log.error(orderSalesEntity.getOrderId() + " 条数出错 [" + count1 + "]", e);
                        }

                        tiao.setCount(count1);
                        tiaos.add(tiao);
                    }
                }
                order.setTiaos(tiaos);
            }


            // 添加到列表
            orderList.add(order);
        } else {
            // 处理 content2
            String[] parts = content.split("\n\n")[0].replace("规格和数量：", "").split("，");
            for (String part : parts) {
                OrderGuigeEntity order = new OrderGuigeEntity();
                String[] detail = part.trim().split(" X "); // 解析规格和数量
                if (detail.length == 2) {
                    order.setGuige(detail[0].trim().toUpperCase());

                    if("打样".equals(detail[1])){
                        order.setCount(0);
                        order.setDanwei("打样");

                    }

                    else if("盘".equals(detail[1].trim())){
                        order.setCount(0);
                        order.setDanwei("盘");

                    }
                    else if("包".equals(detail[1].trim())){
                        order.setCount(0);
                        order.setDanwei("盘");

                    }

                    else {

                        int count2 = 0;
                        try {
                            count2 = Integer.parseInt(detail[1].replaceAll("[^\\d]", "").trim());
                        }catch (Exception e){
                            log.error(orderSalesEntity.getOrderId() + "出错 [" + count2 + "]", e);
                        }

                        order.setCount(count2); // 只取数字部分

                        // Extract the unit from the original part
                        Matcher unitMatcher = Pattern.compile("[\\u4e00-\\u9fa5]+$").matcher(part);
                        if (unitMatcher.find()) {
                            order.setDanwei(unitMatcher.group().trim()); // Set the extracted unit
                        }
                    }
                }
                orderList.add(order);
            }
        }

        Matcher lengthMatcher = Pattern.compile("备注：(.+?)").matcher(content);
        if (lengthMatcher.find()) {
            String detail = lengthMatcher.group(1).trim();
            orderSalesEntity.setRemark(detail);
        }

        orderSalesEntity.setGuiges(orderList);

        return orderList;
}

    public static List<TraceElementEntity> parseOrderTrace(String orderTrace) {
        List<TraceElementEntity> traceList = new ArrayList<>();
        String[] lines = orderTrace.split("\n\n");

        Pattern pattern = Pattern.compile("(.+?)：(.+?)，(.+?)：(.+)");


        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String operator = matcher.group(1).trim();
                String operatorName = matcher.group(2).trim();
                String timeLabel = matcher.group(3).trim();
                String timeValue = matcher.group(4).trim();

                TraceElementEntity entity = new TraceElementEntity();
                entity.setOperator( operatorName);
                String newStr = operator.substring(0, operator.length() - 1);

                entity.setOperation( newStr); // 根据要求构建operation字段

                    String time = timeValue.replace("时间：", "");

                    // 创建日期时间格式器
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    // 将字符串转化为 LocalDateTime
                    LocalDateTime updateTime = LocalDateTime.parse(time, formatter);
                    entity.setTime(updateTime);


                // Handle detail field for 拣货人
                if (operator.equals("拣货人")) {
                    String detail = timeLabel.substring(0, timeLabel.length() - 2);
                    entity.setDetail(detail);
                }

                traceList.add(entity);
            }
        }

        return traceList;
    }

    public ResponseDTO<Long> runAddress() {
        long i;
        for (i = 1; i <= 100000; i++) {
            OrderSalesEntity orderSalesEntity = orderSalesDao.selectById(i);
            if (orderSalesEntity != null) {
                String place = orderSalesEntity.getAddress();
                if (place != null) {
                    Long addressId = addressService.getAddressId(place);
                    if (addressId != null) {
                        orderSalesDao.updateAddressId(Math.toIntExact(addressId), orderSalesEntity.getId());
                    }
                }

            }
        }
        return ResponseDTO.ok(i);
    }


}


