package net.lab1024.sa.admin.module.business.order;

import com.fasterxml.jackson.core.type.TypeReference;
import net.lab1024.sa.base.handler.JsonBasedTypeHandler;
import net.lab1024.sa.admin.module.business.order.domain.entity.TraceElementEntity;

import java.util.List;

public class TraceTypeHandler extends JsonBasedTypeHandler<List<TraceElementEntity>> {


    @Override
    protected TypeReference<List<TraceElementEntity>> specificType() {
        return new TypeReference<List<TraceElementEntity>>() {};
    }

}
