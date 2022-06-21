package com.github.liuxg.rpc;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xinguai.liu
 */
@Data
public class MessageContent implements Serializable {

    private String name;

    private Object[] args;

    private String methodName;

    private String returnData;

}
