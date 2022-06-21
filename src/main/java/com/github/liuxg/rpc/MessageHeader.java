package com.github.liuxg.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xinguai.liu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageHeader implements Serializable {

    private int flag = 0;

    private long requestId;

    private int contentLength;

    public MessageHeader(long requestId, int contentLength) {
        this.requestId = requestId;
        this.contentLength = contentLength;
    }

}
