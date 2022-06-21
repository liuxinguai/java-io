package com.github.liuxg.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

/**
 * @author xinguai.liu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageMessage implements Serializable {

    private MessageHeader header;

    public static final int HEADER_LEN = 106;

    private MessageContent content;

    public static byte[] serializateMessage(PackageMessage message) throws IOException {
        byte[] body = serializate(message.content);
        message.header.setContentLength(body.length);
        byte[] header = serializate(message.header);
        byte[] all = new byte[header.length + body.length];
        System.arraycopy(header,0,all,0,header.length);
        System.arraycopy(body,0,all,header.length,body.length);
        return all;
    }

    private static byte[] serializate(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos  = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    public static PackageMessage deserializate(byte[] data) throws IOException, ClassNotFoundException {
        PackageMessage packageMassage = new PackageMessage();
        packageMassage.setHeader((MessageHeader) deserializate(data,0,HEADER_LEN));
        packageMassage.setContent((MessageContent)deserializate(data,HEADER_LEN,data.length));
        return packageMassage;
    }

    public static Object deserializate(byte[] data,int start, int end) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data,start,end);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        PackageMessage packageMassage = new PackageMessage();
        MessageHeader header = new MessageHeader();
        packageMassage.setHeader(header);
        MessageContent messageContent = new MessageContent();
        messageContent.setName("CarServer");
        packageMassage.setContent(messageContent);

        byte[] bytes = serializate(header);
        System.out.println(bytes.length);
        System.out.println(((MessageHeader)deserializate(bytes,0,bytes.length)).getRequestId());


        byte[] serializate = serializateMessage(packageMassage);
        System.out.println(serializate.length);
        PackageMessage message = deserializate(serializate);
        System.out.println(message.content.getName());
        System.out.println(message.header.getRequestId());

    }

//    public static PackageMassage deserializate(byte[] bytes) {
//
//    }



}
