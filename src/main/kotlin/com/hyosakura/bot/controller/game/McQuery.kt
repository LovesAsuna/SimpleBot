package com.hyosakura.bot.controller.game

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hyosakura.bot.Main
import com.hyosakura.bot.util.protocol.QueryUtil
import com.hyosakura.bot.util.protocol.SRVConvertUtil
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import java.io.IOException

object McQuery : SimpleCommand(
    owner = Main,
    primaryName = "mcquery",
    description = "MC服务器查询",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle(ip: String) {
        var status: Boolean

        // 如果不含:则默认为srv记录
        if (!ip.contains(":")) {
            status = query(this, "$ip:25565", false)
            if (!status) {
                sendMessage("正在尝试SRV解析")
                status = query(this, ip, true)
            }
        } else {
            status = query(this, ip, false)
        }
        if (!status) {
            sendMessage("ip地址不正确或无法直接获取结果!")
        }
    }

    private fun nodeProcess(node: JsonNode): String {
        var text: String? = ""
        val color = node["color"]
        val strikethrough = node["strikethrough"]
        val bold = node["bold"]
        if (color != null) {
            val colorText = color.asText()
            text += when (colorText) {
                "dark_gray" -> "§7"
                "gray" -> "§7"
                "aqua" -> "§5"
                "white" -> ""
                "green" -> "§20"
                "light_purple" -> ""
                "gold" -> ""
                "yellow" -> ""
                else -> ""
            }
        }
        if (strikethrough != null) {
            text += "§n"
        }
        if (bold != null) {
            text += "§l"
        }
        return node["text"].asText().let { text += it; text!! }
    }

    @Throws(IOException::class)
    private suspend fun query(sender: CommandSender, ipAndPort: String, SRV: Boolean): Boolean {
        val host: String
        val port: Int
        if (SRV) {
            val newIpAndport = SRVConvertUtil.convert(ipAndPort)
            host = newIpAndport ?: ipAndPort.split(":").toTypedArray()[0]
            port = (newIpAndport ?: ipAndPort).split(":").toTypedArray()[1].toInt()
        } else {
            host = ipAndPort.split(":").toTypedArray()[0]
            port = ipAndPort.split(":").toTypedArray()[1].toInt()
        }
        val json: String? = try {
            QueryUtil.query(host, port)
        } catch (e: IOException) {
            return false
        }
        val objectMapper = ObjectMapper()
        val mod = false
        val root = objectMapper.readTree(json)
        val version = root["version"]
        val players = root["players"]
        val description = root["description"]
        val modinfo = root["modinfo"]
        val text = description["text"]
        val extra = description["extra"]
        val translate = description["translate"]
        var texts = ""
        var mods = ""
        if (extra != null) {
            for (i in 0 until extra.size()) {
                val node = extra[i]
                texts += nodeProcess(node)
            }
        } else if (text != null) {
            texts = description["text"].asText()
        } else if (translate != null) {
            texts = description["translate"].asText()
        }
        if (modinfo != null) {
            val type = modinfo["type"]
            val modList = modinfo["modList"]
            mods += """
                
                服务器Mod类型: ${type.asText()}${modeProcess(modList)}
                """.trimIndent()
        }
        sender.sendMessage(
            """
                    服务器IP:  $host:$port
                    是否使用SRV域名解析:  $SRV
                    服务器版本:  ${version["name"].asText()}
                    是否为mod服务器: $mod
                    目标服务器协议号码:  ${version["protocol"].asText()}
                    最大在线人数:  ${players["max"].asText()}
                    当前在线人数:  ${players["online"].asText()}
                    MOTD:
                    $texts$mods
                    """.trimIndent()
        )
        return true
    }

    private fun modeProcess(modList: JsonNode): String {
        val size = modList.size()
        var mod = "(总计共" + (size - 1) + "个Mod)"
        for (i in 0 until size) {
            val node = modList[i]
            val modid = node["modid"].asText()
            val version = node["version"].asText()
            mod += "\n$modid: $version"
        }
        return mod
    }
}