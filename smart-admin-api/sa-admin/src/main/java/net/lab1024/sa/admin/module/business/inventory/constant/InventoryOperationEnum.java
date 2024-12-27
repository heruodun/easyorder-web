package net.lab1024.sa.admin.module.business.inventory.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.lab1024.sa.admin.module.business.inventory.constant.InventoryUtil.IN;
import static net.lab1024.sa.admin.module.business.inventory.constant.InventoryUtil.OUT;


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
    JISHU_IN_BUCKET("jishu", IN, 2),

    /**
     * 烫带出库白桶
     */
    TANGDAI_OUT_BUCKET("tangdai", OUT, 2),

    /**
     * 盘带出库白桶
     */
    PANDAI_OUT_BUCKET("pandai", OUT, 2),

    /**
     * 配货外1 出库白桶
     */
    PEIHUOWAI1_OUT_BUCKET("peihuowai1", OUT, 2),

    /**
     * 配货外2 出库白桶
     */
    PEIHUOWAI2_OUT_BUCKET("peihuowai2", OUT, 2),

//    ------------------------------------框子---------------------------------------------

    /**
     * 烫带入库框子
     */
    TANGDAI_IN_BOX("tangdai", IN, 3),

    /**
     * 盘带出库框子
     */
    PANDAI_OUT_BOX("pandai", OUT, 3),

    /**
     * 对接出库框子
     */
    DUIJIE_OUT_BOX("duijie", OUT, 3),

    /**
     * 摇摆出库框子
     */
    YAOBAI_OUT_BOX("yaobai", OUT, 3),

    /**
     * 配货外1 出库框子
     */
    PEIHUOWAI1_OUT_BOX("peihuowai1", OUT, 3),

    /**
     * 配货外2 出库框子
     */
    PEIHUOWAI2_OUT_BOX("peihuowai2", OUT, 3),


    // ------------------------------------摇摆包---------------------------------------------

    /**
     * 摇摆入库摇摆包
     */
    YAOBAI_IN_BAG("yaobai", IN, 4),
    /**
     * 配货 出库摇摆包
     */
    PEIHUO_OUT_BAG("peihuo", OUT, 4),

    /**
     * 对接 出库摇摆包
     */
    DUIJIE_OUT_BAG("duijie", OUT, 4),

    /**
     * 盘带 出库摇摆包
     */
    PANDAI_OUT_BAG("pandai", OUT, 4),

    /**
     * 配货外1 出库摇摆包
     */
    PEIHUOWAI1_OUT_BAG("peihuowai1", OUT, 4),

    /**
     * 配货外2 出库摇摆包
     */
    PEIHUOWAI2_OUT_BAG("peihuowai2", OUT, 4),

    //-------------------------------------盘装---------------------------------------------

    /**
     * 盘带入库盘装
     */
    PANDAI_IN_DISK("pandai", IN, 5),

    /**
     * 配货出库盘装
     */
    PEIHUO_OUT_DISK("peihuo", OUT, 5),

    /**
     * 配货外1 出库盘装
     */
    PEIHUOWAI1_OUT_DISK("peihuowai1", OUT, 5),

    /**
     * 配货外2 出库盘装
     */
    PEIHUOWAI2_OUT_DISK("peihuowai2", OUT, 5),

    //    ------------------------------------外厂1---------------------------------------------

    /**
     * 配货外1 入库 外厂库存
     */
    PEIHUOWAI1_IN_WANICHANG("peihuowai1", IN, 6),


    /**
     * 出货1 出库外厂1库存
     */
    CHUHUOWAI1_OUT_WAICHANG("chuhuowai1", OUT, 6),


    //    ------------------------------------外厂2---------------------------------------------
    /**
     * 出货2 出库外厂2库存
     */
    CHUHUOWAI2_OUT_WAICHANG("chuhuowai2", OUT, 7),

    /**
     * 配货外2 入库 外厂2库存
     */
    PEIHUOWAI2_IN_WANICHANG("peihuowai2", IN, 7),


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

    /**
     * 使用订单类型 和 库存类型 进行匹配 获取出入库类型
     * @param operationCode
     * @param type
     * @return
     */

    public static List<InventoryOperationEnum> getInventoryOperation(String operationCode, Integer type){
        List<InventoryOperationEnum> list = new ArrayList<>();
        for(InventoryOperationEnum inventoryOperationEnum : InventoryOperationEnum.values()){
            if(inventoryOperationEnum.getOperationCode().equals(operationCode)
                    && inventoryOperationEnum.getType() == type){
                list.add(inventoryOperationEnum);

                // 配货外1 配货外2 均入库 外厂1 外厂2；出货1 出货2 均出库 外厂1 外厂2

                if(inventoryOperationEnum.getOperationCode().equals("peihuowai1")){
                    list.add(PEIHUOWAI1_IN_WANICHANG);
                }

                if(inventoryOperationEnum.getOperationCode().equals("peihuowai2")){
                    list.add(PEIHUOWAI2_IN_WANICHANG);
                }

                if(inventoryOperationEnum.getOperationCode().equals("chuhuo1")){
                    list.add(CHUHUOWAI1_OUT_WAICHANG);
                }

                if(inventoryOperationEnum.getOperationCode().equals("chuhuo2")){
                    list.add(CHUHUOWAI2_OUT_WAICHANG);
                }

            }
        }
        return list;
    }

}