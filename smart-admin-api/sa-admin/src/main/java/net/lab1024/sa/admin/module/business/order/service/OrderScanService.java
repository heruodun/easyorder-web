package net.lab1024.sa.admin.module.business.order.service;

import net.lab1024.sa.admin.module.business.order.constant.OrderTypeEnum;
import net.lab1024.sa.admin.module.business.order.constant.OrderUtil;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderScanForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderTypeAndIdVO;
import net.lab1024.sa.admin.module.business.order.sales.service.OrderSalesService;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 订单扫描
 */

@Service
public class OrderScanService {

    @Resource
    private OrderSalesService orderSalesService;


    public ResponseDTO<Boolean> scan(OrderScanForm orderScanForm) {
        //区分是哪种类型订单
        OrderTypeAndIdVO orderInfo = OrderUtil.parseOrderInfo(orderScanForm.getOrderIdQr());
        if(orderInfo == null){
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
        }
        String operation = orderScanForm.getOperation();
        //todo operation 校验
        if (operation.equals("")){
            return ResponseDTO.error(OrderErrorCode.NO_PERMISSION);
        }

        //分流处理，不同的订单类型到不同的服务
        if (orderInfo.getOrderType().equals(OrderTypeEnum.FACTORY_ONE_SALES)){
            return orderSalesService.scanOrder(orderScanForm, orderInfo);
        }else if (orderInfo.getOrderType().equals(OrderTypeEnum.FACTORY_ONE_PRODUCTION_BUCKET)){

        }else if (orderInfo.getOrderType().equals(OrderTypeEnum.FACTORY_ONE_PRODUCTION_BOX)){

        }else if (orderInfo.getOrderType().equals(OrderTypeEnum.FACTORY_ONE_PRODUCTION_BAG)){

        }else if (orderInfo.getOrderType().equals(OrderTypeEnum.FACTORY_ONE_PRODUCTION_DISK)){

        }else {
            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "二厂订单号暂不支持~");
        }
        return ResponseDTO.ok();
    }


}


