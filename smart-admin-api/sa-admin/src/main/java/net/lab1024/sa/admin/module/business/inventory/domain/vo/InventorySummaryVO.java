package net.lab1024.sa.admin.module.business.inventory.domain.vo;

import lombok.Data;

@Data
public class InventorySummaryVO {
    private int type;
    private String guige;
    private String danwei;
    private int totalCount;
}