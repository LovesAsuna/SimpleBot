package me.lovesasuna.bot.controller.bilibili.Danmu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.file.Config
import net.mamoe.mirai.message.MessageEvent
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object PacketManager {
    @Throws(IOException::class)
    fun sendJoinChannel(event: MessageEvent, roomID: Int, out: DataOutputStream, key: String) {
        val mapper = ObjectMapper()
        val objectNode: ObjectNode = mapper.createObjectNode()
            .put("roomid", roomID)
            .put("uid", 0)
            .put("key", key)
            .put("platform", "MC BC M/P")
            .put("protover", 2)
        sendPacket(out, 7, objectNode.toString())
        runBlocking { event.reply("成功连接直播间: $roomID") }
    }

    @Throws(IOException::class)
    fun sendHeartPacket(out: DataOutputStream) {
        sendPacket(out, 0, 16.toShort(), 1.toShort(), 2, 1, "")
    }

    @Throws(IOException::class)
    fun sendPacket(out: DataOutputStream, packetType: Int, body: String) {
        sendPacket(out, 0, 16.toShort(), 1.toShort(), packetType, 1, body)
    }

    @Throws(IOException::class)
    fun sendPacket(
        out: DataOutputStream, originPacketLength: Int, packetHeadLength: Short, version: Short, packetType: Int,
        magic: Int, body: String
    ) {
        var packetLength = originPacketLength
        val bodyData = body.toByteArray(StandardCharsets.UTF_8)
        if (packetLength == 0) {
            packetLength = bodyData.size + 16
        }
        out.writeInt(packetLength)
        out.writeShort(packetHeadLength.toInt())
        out.writeShort(version.toInt())
        out.writeInt(packetType)
        out.writeInt(magic)
        if (bodyData.size > 0) {
            out.write(bodyData)
        }
        out.flush()
    }

    fun sendDanmu(roomID: Int, text: String) {
        val url = URL("https://api.live.bilibili.com/msg/send")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("origin", "https://live.bilibili.com")
        conn.setRequestProperty("referer", "https://live.bilibili.com/$roomID")
        conn.setRequestProperty("cookie", Config.data.BilibiliCookie)
        conn.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"
        )
        conn.doOutput = true
        conn.connect()
        val outputStream = conn.outputStream


        val dataOutputStream = DataOutputStream(outputStream)
        val msg =
            "color=16777215&fontsize=25&mode=1&msg=${text}&rnd=1591590584&roomid=${roomID}&bubble=0&csrf_token=be130912bad65f05efa355ea02b67f7c&csrf=be130912bad65f05efa355ea02b67f7c"
        val bytes = msg.toByteArray(StandardCharsets.UTF_8)
        dataOutputStream.write(bytes)
        dataOutputStream.flush()
        conn.disconnect()
    }

    class Header(`in`: DataInputStream) {
        /**
         * 消息总长度 (协议头 + 数据长度)
         */
        var packetLength: Int

        /**
         * 头长度 固定16 Bytes
         */
        var packetHeadLength: Short
        var version: Short

        /**
         * 消息类型
         */
        var packetType: Int

        /**
         * 参数 固定为1
         */
        var magic: Int

        init {
            //4B
            packetLength = `in`.readInt()
            //2B
            packetHeadLength = `in`.readShort()
            //2B
            version = `in`.readShort()
            //4B
            packetType = `in`.readInt()
            //4B
            magic = `in`.readInt()
        }
    }
}