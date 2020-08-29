package me.lovesasuna.bot.util.protocol

import java.io.*
import java.net.Socket

object QueryUtil {
    @Throws(IOException::class)
    fun query(host: String, port: Int): String {
        val socket = Socket(host, port)
        socket.soTimeout = 10 * 1000
        val outputStream = socket.getOutputStream()
        val dataOutputStream = DataOutputStream(outputStream)
        val inputStream = socket.getInputStream()
        val inputStreamReader = InputStreamReader(inputStream)
        val b = ByteArrayOutputStream()
        val handshake = DataOutputStream(b)
        /*握手数据包id*/
        handshake.writeByte(0x00)
        /*协议版本*/
        writeVarInt(handshake, 578)
        /*主机地址长度*/
        writeVarInt(handshake, host.length)
        /*主机地址*/
        handshake.writeBytes(host)
        /*端口*/
        handshake.writeShort(25565)
        /*状态(握手是1)*/
        writeVarInt(handshake, 1)

        /*发送的握手数据包大小*/
        writeVarInt(dataOutputStream, b.size())
        /*发送握手数据包*/
        dataOutputStream.write(b.toByteArray())


        /*大小为1*/
        dataOutputStream.writeByte(0x01)
        /*ping的数据包id*/
        dataOutputStream.writeByte(0x00)
        val dataInputStream = DataInputStream(inputStream)
        /*返回的数据包大小*/
        readVarInt(dataInputStream)
        /*返回的数据包id*/
        var id = readVarInt(dataInputStream)
        if (id == -1) {
            throw IOException("数据流过早结束")
        }

        /*需要返回的状态*/if (id != 0x00) {
            throw IOException("无效的数据包id")
        }
        /*json字符串长度*/
        val length = readVarInt(dataInputStream)
        if (length == -1) {
            throw IOException("数据流过早结束")
        }
        if (length == 0) {
            throw IOException("无效的json字符串长度")
        }
        val `in` = ByteArray(length)
        /*读取json字符串*/
        dataInputStream.readFully(`in`)
        val json = String(`in`, Charsets.UTF_8)
        val now = System.currentTimeMillis()
        /*数据包大小*/
        dataOutputStream.writeByte(0x09)
        /*ping 0x01*/
        dataOutputStream.writeByte(0x01)
        /*时间*/
        dataOutputStream.writeLong(now)
        readVarInt(dataInputStream)
        id = readVarInt(dataInputStream)
        if (id == -1) {
            throw IOException("数据流过早结束")
        }
        if (id != 0x01) {
            throw IOException("无效的数据包id")
        }
        /*读取回应(pingtime)*/
        dataInputStream.readLong()
        dataOutputStream.close()
        outputStream.close()
        inputStreamReader.close()
        inputStream.close()
        socket.close()
        return json
    }

    @Throws(IOException::class)
    private fun writeVarInt(out: DataOutputStream, paramInt: Int) {
        var int = paramInt
        while (true) {
            if (int and -0x80 == 0) {
                out.writeByte(int)
                return
            }
            out.writeByte(int and 0x7F or 0x80)
            int = int ushr 7
        }
    }

    @Throws(IOException::class)
    fun readVarInt(`in`: DataInputStream): Int {
        var i = 0
        var j = 0
        while (true) {
            val k = `in`.readByte().toInt()
            i = i or (k and 0x7F shl j++ * 7)
            if (j > 5) {
                throw RuntimeException("VarInt too big")
            }
            if (k and 0x80 != 128) {
                break
            }
        }
        return i
    }
}