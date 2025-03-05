package com.study.netty.c6.enums;

import lombok.Getter;

/**
 * @author xuyong
 */

@Getter
public enum MsgTypeEnum {
    /**
     * 配置消息
     */
    CONFIG(1,"配置"),
    /**
     * 数据消息
     */
    DATA(2,"数据");

    private final int code;
    private final String desc;
    MsgTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
