package com.github.liuxg.rpc;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;


/**
 * @author xinguai.liu
 */
public class ProxyUtils {

    public static <T> T createProxy(Class<T> clazz) {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                MessageHeader messageHeader = new MessageHeader(Math.abs(UUID.randomUUID().getLeastSignificantBits()), 0);
                MessageContent messageContent = new MessageContent();
                messageContent.setName(clazz.getName());
                messageContent.setMethodName(method.getName());
                messageContent.setArgs(args);
                messageContent.setReturnData("");
                return MessageSend.send(new PackageMessage(messageHeader,messageContent));
            }
        });
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            new Thread(()->{
                Car car = ProxyUtils.createProxy(Car.class);
                System.out.println(Thread.currentThread().getName()+":"+car.info("liuxg"));
            }).start();
        }
    }

}
