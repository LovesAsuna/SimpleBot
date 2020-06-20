package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.NetWorkUtil
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import java.io.Serializable
import java.net.URL
import java.util.concurrent.TimeUnit

class Dynamic : Listener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        event as GroupMessageEvent
        if (message.startsWith("/subscribe ")) {
            when (message.split(" ")[1]) {
                "list" -> {
                    val list = arrayListOf<Int>()
                    data.subscribeMap.keys.forEach {
                        if (data.subscribeMap[it]!!.contains(event.group.id)) {
                            list.add(it)
                        }
                    }
                    event.reply("当前订阅的up: $list")
                }
                "add" -> {
                    val upID = message.split(" ")[2].toInt()
                    data.upSet.add(upID)
                    data.subscribeMap.putIfAbsent(upID, hashSetOf())
                    data.subscribeMap[upID]?.add(event.group.id)
                    event.reply("up动态订阅成功!")
                }
                "remove" -> {
                    val upID = message.split(" ")[2].toInt()
                    data.subscribeMap[upID]?.remove(event.group.id)
                    event.reply("up动态取消订阅成功!")
                }
                "test" -> {
                    val upID = message.split(" ")[2].toInt()
                    val num = message.split(" ")[3].toInt()
                    read(upID, num)
                }
            }
        }
        return true
    }

    data class Data(var upSet: HashSet<Int>, var subscribeMap: HashMap<Int, HashSet<Long>>, var dynamicMap: HashMap<Int, String>) : Serializable

    companion object {
        var data = Data(hashSetOf(), hashMapOf(), hashMapOf())
        var intercept = false

        init {
            Main.instance.scheduler!!.async {
                BasicUtil.scheduleWithFixedDelay(Runnable {
                    data.upSet.forEach {
                        runBlocking {
                            read(it, 0)
                            delay(30 * 1000)
                        }
                    }
                }, 1, 1, TimeUnit.MINUTES)
            }
        }


        private suspend fun read(uid: Int, num: Int) {
            val reader = NetWorkUtil.fetch("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?&host_uid=$uid")!!.first.bufferedReader()
            val root = ObjectMapper().readTree(reader.readLine())
            if (root.toString().contains("拦截")) {
                if (!intercept) {
                    Main.instance.logger.error("B站动态api请求被拦截")
                    data.subscribeMap[uid]?.forEach {
                        Main.instance.scheduler!!.async {
                            val group = Bot.botInstances[0].getGroup(it)
                            group.sendMessage("B站动态api请求被拦截，请联系管理员!")
                        }
                    }
                }
                intercept = true
                return
            }
            intercept = false
            val cards = root["data"]["cards"]
            val card = dequate(cards[num]["card"])

            if (data.dynamicMap[uid] != card["item"]["rp_id"].toString()) {
                data.dynamicMap[uid] = card["item"]["rp_id"].toString()
                data.subscribeMap[uid]?.forEach {
                    Main.instance.scheduler!!.async {
                        val group = Bot.botInstances[0].getGroup(it)
                        group.sendMessage(PlainText("${card["user"]["uname"].asText()}发布了以下动态!"))
                        parse(group, card)
                    }
                }

            }
        }

        private suspend fun parse(group: Group, card: JsonNode) {
            val origin = card["origin"]
            if (origin == null) {
                val message = detailParse(group, card)
                group.sendMessage(message)
            } else {
                val originContent = dequate(origin)
                // 转发介绍
                val content = card["item"]["content"]
                group.sendMessage(PlainText(content.asText()))
                val message = detailParse(group, originContent)
                group.sendMessage(message)
            }
        }

        private suspend fun detailParse(group: Group, node: JsonNode): Message {
            return when {
                node["item"] != null -> {
                    if (node["item"]["pictures"] != null) {
                        pictureParse(group, node)
                    } else {
                        PlainText(node["item"]["content"].asText())
                    }
                }
                node["aid"] != null -> videoParse(node)
                node["category"] != null -> articleParse(node)
                else -> PlainText("")
            }
        }


        private suspend fun pictureParse(group: Group, origin: JsonNode): Message {
            val description = origin["item"]["description"]
            val pictures = origin["item"]["pictures"]
            var messageChain = messageChainOf(PlainText(description.asText() + "\n"))
            for (i in 0 until pictures.size()) {
                messageChain += URL(pictures[i]["img_src"].asText()).uploadAsImage(group)
            }
            return messageChain
        }

        private suspend fun videoParse(origin: JsonNode): Message {
            return makeCard(origin["title"].asText(), origin["desc"].asText(), "哔哩哔哩视频", origin["jump_url"].asText(), origin["pic"].asText())
        }

        private suspend fun articleParse(origin: JsonNode): Message {
            return makeCard(origin["title"].asText(), origin["summary"].asText(), "哔哩哔哩专栏", "https://www.bilibili.com/read/cv${origin["id"].asText()}", origin["banner_url"].asText())
        }

        private fun makeCard(title: String, desc: String, tag: String, jumpURL: String, previewURL: String): LightApp {
            val string = "{\"app\":\"com.tencent.structmsg\",\"desc\":\"自定义卡片\",\"view\":\"news\",\"ver\":\"0.0.0.1\",\"prompt\":\"自定义提示\",\"meta\":{\"news\":{\"action\":\"\",\"android_pkg_name\":\"\",\"app_type\":1,\"appid\":100951776,\"desc\":\"$desc\",\"jumpUrl\":\"$jumpURL\",\"preview\":\"$previewURL\",\"source_icon\":\"\",\"source_url\":\"\",\"tag\":\"$tag\",\"title\":\"$title\"}},\"config\":{\"autosize\":true,\"ctime\":1592374968,\"forward\":true,\"token\":\"deaf213724ea32ea9c94ed8efdc09c13\",\"type\":\"normal\"}}"
            return LightApp((string))
        }

        private fun dequate(node: JsonNode): JsonNode {
            return BotData.objectMapper!!.readTree(node.asText())
        }
    }
}
