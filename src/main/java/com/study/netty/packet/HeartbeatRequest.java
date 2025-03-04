package com.study.netty.packet;

import java.util.UUID;

/**
 * @author xuyong
 * 心跳请求
 */
public class HeartbeatRequest extends BaseMessage {
    public HeartbeatRequest() {
        setType(1);
        setReqId(UUID.randomUUID().toString());
    }
}

