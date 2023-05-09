package com.itheida.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),
    LOGIN_ERROR(500100,"账号或密码错误"),
    MOBILE_ERROR(500200, "手机号码格式错误"),
    SESSION_ERROR(  500300,"请登录"),
    PASSWORD_UPDATE_ERROR(500400, "密码修改失败"),

    //商品信息
    EMPTY_STOCK(500301,"商品售罄"),
    REPEATE_ERROR(500302,"重复购买"),


    //订单信息
    OREDER_NOT_EXIST(500401, "订单信息不存在"),
    STATUS_ILLEGAL(500500, "商品状态异常");
    private final Integer code;
    private final String message;
}
