package com.study.netty.c6.packet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xuyong
 * 消息基类（抽象）
 */
@Getter
@Setter
public abstract class BaseMessage implements Serializable {
    /**
     * 消息类型（1:心跳, 2:业务请求, 3:业务响应）
     */
    private int type;
    /**
     * 请求唯一ID（用于异步响应匹配）
     */
    private String reqId;
}