package net.lab1024.sa.admin.module.business.order.sales.constant;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderTiaoEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Slf4j
public class OrderSalesUtil {

    public static String getDetail(OrderSalesVO orderSalesVO){
        StringBuilder sb = new StringBuilder();
        List<OrderGuigeEntity> guiges = orderSalesVO.getGuiges();
        if(CollectionUtils.isNotEmpty(guiges)){

            for(OrderGuigeEntity orderGuige : guiges){
                sb.append("规格：").append(orderGuige.getGuige()).append("    ");
                sb.append("总数：").append(orderGuige.getCount()).append(" ").append(orderGuige.getDanwei()).append("\n\n");
                List<OrderTiaoEntity> tiaos = orderGuige.getTiaos();
                if(CollectionUtils.isNotEmpty(tiaos)){
                    for(OrderTiaoEntity orderTiaoEntity : tiaos){
                        sb.append(orderTiaoEntity.getLength()).append(" x ").append(orderTiaoEntity.getCount())
                                .append("，");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        }
        if(Strings.isNotBlank(orderSalesVO.getRemark())){
            if(sb.length() > 0) {
                if (sb.charAt(sb.length() - 1) == '\n') {
                    sb.append("\n");
                } else {
                    sb.append("\n\n");
                }
            }
            sb.append("备注：").append(orderSalesVO.getRemark());
        }
        return sb.toString();
    }

}

