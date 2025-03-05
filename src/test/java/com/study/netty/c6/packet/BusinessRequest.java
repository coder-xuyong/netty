package com.study.netty.c6.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author xuyong
 * 业务请求
 */
@Getter
@Setter
@NoArgsConstructor
public class BusinessRequest extends BaseMessage {
    private String data;
    /**
     * 1 是数据，2 是配置
     */
    private Integer msgType;

    public BusinessRequest(String data, Integer msgType) {
        setType(2);
        setReqId(UUID.randomUUID().toString());
        this.data = data;
        this.msgType = msgType;

    }
}
