package com.github.liuxg.nio;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author xinguai.liu
 */
public class Demo {

    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("liuxg".getBytes(StandardCharsets.UTF_8));
        System.out.println("limit : "+allocate.limit()+" , position : "+allocate.position());
        allocate.flip();
        System.out.println("limit : "+allocate.limit()+" , position : "+allocate.position());
    }

}
