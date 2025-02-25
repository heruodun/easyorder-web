package net.lab1024.sa.admin.module.business.order.sales.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderSalesSearchCriteria {
    private Long orderId;
    private String address;
    private String remark;
    private Integer waveId;
    private List<CountCriteria> countCriteria; // 三元组集合
    private List<String> curStatus; // 准确匹配
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String operator; // 模糊匹配

    private String query;

    private int pageNum;

    private int pageSize;
}
