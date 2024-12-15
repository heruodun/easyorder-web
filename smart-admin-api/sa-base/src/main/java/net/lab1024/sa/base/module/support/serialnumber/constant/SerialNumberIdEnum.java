package net.lab1024.sa.base.module.support.serialnumber.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 单据序列号 枚举
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@AllArgsConstructor
@Getter
public enum SerialNumberIdEnum implements BaseEnum {

    SALES_ORDER(1, "销量订单id"),

    PRODUCTION_BUCKET_ORDER(2, "生产白桶订单id"),

    PRODUCTION_BOX_ORDER(3, "生产框子订单id"),

    PRODUCTION_BAG_ORDER(4, "生产摇摆订单id"),

    PRODUCTION_DISK_ORDER(5, "生产盘带订单id"),

    ;

    private final Integer serialNumberId;

    private final String desc;

    @Override
    public Integer getValue() {
        return serialNumberId;
    }

    @Override
    public String toString() {
        return "SerialNumberIdEnum{" +
                "serialNumberId=" + serialNumberId +
                ", desc='" + desc + '\'' +
                '}';
    }
}
