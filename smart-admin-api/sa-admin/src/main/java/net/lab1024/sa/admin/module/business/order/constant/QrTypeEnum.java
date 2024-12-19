package net.lab1024.sa.admin.module.business.order.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二维码类型，以后缀区分
 */
@AllArgsConstructor
@Getter
public enum QrTypeEnum {

    /**
     *
     */
    V0("xiaowangniujin"),

    /**
     *
     */
//    V1("dahangv1"),

   ;
    private final String version;

}
