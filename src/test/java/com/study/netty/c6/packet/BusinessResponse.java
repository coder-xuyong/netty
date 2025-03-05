package com.study.netty.c6.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xuyong
 * 业务响应
 */
@Setter
@Getter
@NoArgsConstructor
public class BusinessResponse extends BaseMessage {
    private boolean success;
    private String result;

    public BusinessResponse(String reqId, boolean success, String result) {
        setType(3);
        setReqId(reqId);
        this.success = success;
        this.result = result;
    }
}
