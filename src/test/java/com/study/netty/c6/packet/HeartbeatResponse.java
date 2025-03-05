package com.study.netty.c6.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xuyong
 * 心跳响应
 */
@Setter
@Getter
@NoArgsConstructor
public class HeartbeatResponse extends BaseMessage {
    public HeartbeatResponse(String reqId) {
        setType(1);
        setReqId(reqId);
    }
}
