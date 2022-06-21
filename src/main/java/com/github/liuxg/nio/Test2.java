package com.github.liuxg.nio;

import java.io.IOException;
import java.net.Socket;

public class Test2 {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);
    }
}
