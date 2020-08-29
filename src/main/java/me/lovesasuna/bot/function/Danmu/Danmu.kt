package me.lovesasuna.bot.function.Danmu

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

class Danmu : FunctionListener {
    var closed: Boolean = false
    var roomID: Int = 0
    lateinit var conn: HttpURLConnection
    lateinit var socket: Socket
    lateinit var scheduledFuture: ScheduledFuture<*>

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when {
            message.startsWith("/直播 connect ") -> {
                closed = false
                roomID = message.split(" ")[2].toInt()
                GlobalScope.launch {
                    connect(event)
                }
            }
            message.startsWith("/直播 disconnect") -> {
                closed = true
                scheduledFuture.cancel(true)
                event.reply("与直播间主动断开连接!")
            }
            message.startsWith("/直播 send ") && event.sender.id == Config.data.admin -> {
                val roomID = message.split(" ")[2].toInt()
                PacketManager.sendDanmu(roomID, message.replaceFirst("/直播 send $roomID ", ""))
                event.reply("弹幕发送成功!")
            }
        }
        return true
    }

    fun connect(event: MessageEvent) {
        val url = URL("https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=$roomID")
        conn = url.openConnection() as HttpURLConnection
        conn.doInput = true
        conn.doOutput = true
        conn.readTimeout = 5000
        conn.allowUserInteraction = true
        conn.connect()
        val root = ObjectMapper().readTree(conn.inputStream)

        val token = root["data"]["token"].asText()
        val host = root["data"]["host"].asText()
        val port = root["data"]["port"].asText()

        socket = Socket(host, port.toInt())
        PacketManager.sendJoinChannel(event, roomID, DataOutputStream(socket.getOutputStream()), token)
        BasicUtil.scheduleWithFixedDelay(Runnable {
            try {
                if (!socket.isClosed && !closed) {
                    val out = DataOutputStream(socket.getOutputStream())
                    PacketManager.sendHeartPacket(out)
                } else {
                    GlobalScope.launch {
                        when {
                            socket.isClosed -> event.reply("socket is closed")
                            closed -> event.reply("live is closed")
                        }
                    }
                }
            } catch (e: IOException) {
                e.pushError()
                e.printStackTrace()
            }
        }, 0, 30, TimeUnit.SECONDS)

        while (!closed && !socket.isClosed) {
            try {
                val inputStream = DataInputStream(socket.getInputStream())
                socket.soTimeout = 0
                var header = PacketManager.Header(inputStream)
                require(header.packetLength >= 16) { "协议失败: (L:" + header.packetLength.toString() + ")" }
                val payloadLength: Int = header.packetLength - 16
                if (payloadLength == 0) {
                    continue
                }
                val buffer = ByteArray(payloadLength)
                var read = 0
                do {
                    read += inputStream.read(buffer, read, payloadLength - read)
                } while (read < payloadLength)
                if (header.version == 2.toShort() && header.packetType == 5) {
                    try {
                        InflaterInputStream(ByteArrayInputStream(buffer, 2, buffer.size - 2),
                                Inflater(true)).use { inflater ->
                            val dataInputStream = DataInputStream(inflater)
                            header = PacketManager.Header(dataInputStream)
                            process(event, header.packetType, dataInputStream)
                        }
                    } catch (e: IOException) {
                        e.pushError()
                        e.printStackTrace()
                    }
                } else {
                    process(event, header.packetType, DataInputStream(ByteArrayInputStream(buffer)))
                }
            } catch (e: RuntimeException) {
                e.pushError()
                e.printStackTrace()
            }
        }
        socket.close()
    }

    @Throws(IOException::class)
    private fun process(event: MessageEvent, packetType: Int, `in`: DataInputStream) {
        //3是人气回调 无视无视（
        when (packetType) {
            5 -> {
                val mapper = ObjectMapper()
                val jsonNode = mapper.readTree(`in`)
                try {
                    val bulletData = DanmuData(jsonNode, roomID, 2)
                    if (bulletData.type != null) {
                        GlobalScope.launch {
                            when (bulletData.type) {
                                DanmuData.COMMENT_TYPE -> {
                                    event.reply(bulletData.toString())
                                }
                                DanmuData.LIVE_START_TYPE -> {
                                    event.reply("${roomID}开启了直播")
                                }
                                DanmuData.LIVE_STOP_TYPE -> {
                                    event.reply("${roomID}关闭了直播")
                                }
                            }
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
            3, 8 -> {
            }
            else -> {
            }
        }
    }
}