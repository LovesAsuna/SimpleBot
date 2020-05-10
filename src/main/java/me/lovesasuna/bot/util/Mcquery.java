package me.lovesasuna.bot.util;

import java.io.*;
import java.net.Socket;

public class Mcquery {
    public static String query(String host, int port) throws IOException{
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(10 * 1000);
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        ByteArrayOutputStream b = new ByteArrayOutputStream();

        DataOutputStream handshake = new DataOutputStream(b);
        /*握手数据包id*/
        handshake.writeByte(0x00);
        /*协议版本*/
        writeVarInt(handshake, 4);
        /*主机地址长度*/
        writeVarInt(handshake, host.length());
        /*主机地址*/
        handshake.writeBytes(host);
        /*端口*/
        handshake.writeShort(25565);
        /*状态(握手是1)*/
        writeVarInt(handshake, 1);

        /*发送的握手数据包大小*/
        writeVarInt(dataOutputStream, b.size());
        /*发送握手数据包*/
        dataOutputStream.write(b.toByteArray());


        /*大小为1*/
        dataOutputStream.writeByte(0x01);
        /*ping的数据包id*/
        dataOutputStream.writeByte(0x00);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        /*返回的数据包大小*/
        int size = readVarInt(dataInputStream);
        /*返回的数据包id*/
        int id = readVarInt(dataInputStream);

        if (id == -1) {
            throw new IOException("数据流过早结束");
        }

        /*需要返回的状态*/
        if (id != 0x00) {
            throw new IOException("无效的数据包id");
        }
        /*json字符串长度*/
        int length = readVarInt(dataInputStream);

        if (length == -1) {
            throw new IOException("数据流过早结束");
        }

        if (length == 0) {
            throw new IOException("无效的json字符串长度");
        }

        byte[] in = new byte[length];
        /*读取json字符串*/
        dataInputStream.readFully(in);
        String json = new String(in, "UTF-8");


        long now = System.currentTimeMillis();
        /*数据包大小*/
        dataOutputStream.writeByte(0x09);
        /*ping 0x01*/
        dataOutputStream.writeByte(0x01);
        /*时间*/
        dataOutputStream.writeLong(now);

        readVarInt(dataInputStream);
        id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("数据流过早结束");
        }

        if (id != 0x01) {
            throw new IOException("无效的数据包id");
        }
        /*读取回应*/
        long pingtime = dataInputStream.readLong();

        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();

        return json;
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((k & 0x80) != 128) {
                break;
            }
        }
        return i;
    }
}