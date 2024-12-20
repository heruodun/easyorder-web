package net.lab1024.sa.admin.module.business.order.dao;

import com.alibaba.fastjson.TypeReference;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;
import net.lab1024.sa.base.handler.ListTypeHandler;

import java.util.List;

public class TraceTypeHandler extends ListTypeHandler<TraceElementEntity> {


    @Override
    protected TypeReference<List<TraceElementEntity>> specificType() {
        return new TypeReference<List<TraceElementEntity>>() {
        };

    }
}
