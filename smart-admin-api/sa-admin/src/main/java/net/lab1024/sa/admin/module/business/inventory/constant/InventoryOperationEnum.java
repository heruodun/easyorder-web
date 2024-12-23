package net.lab1024.sa.admin.module.business.inventory.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * 库存操作类型
 */
@AllArgsConstructor
@Getter
public enum InventoryOperationEnum {



//    ------------------------------------白桶---------------------------------------------
    /**
     * 技术入库白桶
     */
    JISHU_IN_BUCKET("jishu", 100, 2),

    /**
     * 烫带出库白桶
     */
    TANGDAI_OUT_BUCKET("tangdai", 200, 2),

    /**
     * 盘带出库白桶
     */
    PANDAI_OUT_BUCKET("pandai", 200, 2),

    /**
     * 配货外 出库白桶
     */
    PEIHUOWAI_OUT_BUCKET("peihuowai", 200, 2),

//    ------------------------------------框子---------------------------------------------

    /**
     * 烫带入库框子
     */
    PANDAI_IN_BOX("pandai", 100, 3),

    /**
     * 盘带出库框子
     */
    PANDAI_OUT_BOX("pandai", 200, 3),

    /**
     * 对接出库框子
     */
    DUIJIE_OUT_BOX("duijie", 200, 3),

    /**
     * 摇摆出库框子
     */
    YAOBAI_OUT_BOX("yaobai", 200, 3),

    /**
     * 配货外 出库框子
     */
    PEIHUOWAI_OUT_BOX("peihuowai", 200, 3),

    // ------------------------------------摇摆包---------------------------------------------

    /**
     * 摇摆入库摇摆包
     */
    YAOBAI_IN_BAG("yaobai", 100, 4),

    /**
     * 配货外 出库摇摆包
     */
    PEIHUOWAI_OUT_BAG("peihuowai", 200, 4),

    //-------------------------------------盘装---------------------------------------------

    /**
     * 盘带入库盘装
     */
    PANDAI_IN_DISK("pandai", 100, 5),

    /**
     * 配货出库盘装
     */
    PEIHUO_OUT_DISK("peihuo", 200, 5),

    /**
     * 配货外 出库盘装
     */
    PEIHUOWAI_OUT_DISK("peihuowai", 200, 5),

    //    ------------------------------------外厂---------------------------------------------

    /**
     * 配货外 入库 外厂库存，此操作与上一个操作是一次扫码行为 ？？
     */
    PEIHUOWAI_IN_WANICHANG("peihuowai", 100, 6),
    /**
     * 外厂出库外厂库存
     */
    WAICHANG_OUT_WAICHANG("waichang", 200, 6),




    ;


    /**
     * 操作code
     */
    private final String operationCode;
    /**
     * 0其他，入库100，出库200
     */
    private final Integer status;

    /**
     * 库存类型
     */
    private final Integer type;


    public static final Set<String> operationCodeSet = new HashSet<>();

    static {
        for(InventoryOperationEnum inventoryOperationEnum : InventoryOperationEnum.values()){
            operationCodeSet.add(inventoryOperationEnum.operationCode);
        }
    }

    public static Integer getStatus(String operationCode, Integer type){
        for(InventoryOperationEnum inventoryOperationEnum : InventoryOperationEnum.values()){
            if(inventoryOperationEnum.getOperationCode().equals(operationCode)
                    && inventoryOperationEnum.getType() == type){
                return inventoryOperationEnum.getStatus();
            }
        }
        return -1;
    }

}
