

package net.lab1024.sa.admin.module.business.order.sales.service;

import net.lab1024.sa.admin.module.business.order.sales.dao.OrderSalesDao;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.repository.OrderSalesESRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderESService {
    @Resource
    private OrderSalesESRepository salesESRepository;
    @Resource
    private OrderSalesDao orderSalesDao;

    /**
     * 异步更新 order sale es
     *
     * @param ids
     */
    @Async
    public void asyncData(List<Long> ids) {
        List<OrderSalesEntity> orders = orderSalesDao.selectBatchIds(ids);
        for (OrderSalesEntity order : orders) {
            salesESRepository.save(order);
        }
    }

}

