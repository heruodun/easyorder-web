package net.lab1024.sa.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单级别的错误码（用户引起的错误返回码，可以不用关注）
 *
 */
@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    PARAM_ERROR(30001, "参数错误"),

    DATA_NOT_EXIST(30002, "左翻右翻，数据竟然找不到了~"),

    ALREADY_EXIST(30003, "数据已存在了呀~"),

    REPEAT_SUBMIT(30004, "亲~您操作的太快了，请稍等下再操作~"),

    NO_PERMISSION(30005, "对不起，您没有权限访问此内容哦~"),

    DEVELOPING(30006, "系統正在紧急开发中，敬请期待~"),

    LOGIN_STATE_INVALID(30007, "您还未登录或登录失效，请重新登录！"),

    FORM_REPEAT_SUBMIT(30009, "请勿重复提交"),

    FORM_SUBMIT_FAIL(30010, "提交失败，请重试"),
    ILLEGAL_ORDER_ID(30011, "非法订单号"),
    ;


    private final int code;

    private final String msg;

    private final String level;

    OrderErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_USER;
    }
}
