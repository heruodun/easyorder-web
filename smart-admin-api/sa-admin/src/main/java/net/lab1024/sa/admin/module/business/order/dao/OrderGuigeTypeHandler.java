package net.lab1024.sa.admin.module.business.order.dao;

import com.alibaba.fastjson.TypeReference;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderGuigeEntity;
import net.lab1024.sa.base.handler.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;
import java.util.List;

public class OrderGuigeTypeHandler extends ListTypeHandler<OrderGuigeEntity> {


    @Override
    protected TypeReference<List<OrderGuigeEntity>> specificType() {
        return new TypeReference<List<OrderGuigeEntity>>() {
        };

    }
}
