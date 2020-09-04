package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.string.StringUtil
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class Dynamic : FunctionListener {
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
                "debug" -> {
                    if (event.sender.id == Config.data.admin) {
                        event.reply("开始收集信息...")
                        val builder = StringBuilder()
                        builder.append("Task状态: \n")
                        builder.append("Active: ${task.isActive}\n")
                        builder.append("Completed: ${task.isCompleted}\n")
                        builder.append("Cancelled: ${task.isCancelled}\n\n")
                        builder.append("最后查询时间: ${data.time}\n\n")
                        builder.append("是否被拦截: ${data.intercept}\n\n")
                        builder.append("订阅的UP集合: ${data.upSet}\n\n")
                        builder.append("up与群的对应关系: \n")
                        data.subscribeMap.entries.forEach {
                            builder.append("UP=${it.key}: 群聊=${it.value}\n")
                        }
                        builder.append("\n")
                        builder.append("UP消息摘要: \n")
                        data.dynamicMap.entries.forEach {
                            builder.append("UP=${it.key}: 摘要=${it.value}\n")
                        }
                        event.reply("debug信息: ${BasicUtil.debug(builder.toString())}")
                    }
                }
                "push" -> {
                    event.reply("开始往订阅群推送消息！")
                    Main.scheduler.asyncTask {
                        data.upSet.forEach {
                            runBlocking {
                                read(it, 0, true)
                                data.time = "${Calendar.getInstance().time}"
                                delay(15 * 1000)
                            }
                        }
                        event.reply("推送完成")
                        this
                    }
                }
            }
        }
        return true
    }

    data class Data(var upSet: HashSet<Int>, var subscribeMap: HashMap<Int, HashSet<Long>>, var dynamicMap: HashMap<Int, String>, var time: String, var intercept: Boolean) : Serializable

    companion object {
        var data = Data(hashSetOf(), hashMapOf(), hashMapOf(), "", false)
        private var task: Job

        init {
            task = BasicUtil.scheduleWithFixedDelay({
                data.upSet.forEach {
                    runBlocking {
                        with(it) {
                            try {
                                val result = GlobalScope.async {
                                    read(it, 0)
                                    data.time = "${Calendar.getInstance().time}"
                                    true
                                }
                                delay(10 * 1000)
                                if (!result.isCompleted) {
                                    throw TimeoutException()
                                }
                            } catch (e: TimeoutException) {
                                e.pushError()
                                data.subscribeMap[it]?.forEach {
                                    val group = Bot.botInstances[0].getGroup(it)
                                    group.sendMessage("查询${this}动态时超时!")
                                }
                            }
                            delay(15 * 1000)
                        }

                    }
                }
            }, 0, 1, TimeUnit.MINUTES).first
        }

        private suspend fun read(uid: Int, num: Int, push: Boolean = false) {
            val reader = NetWorkUtil.get("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?&host_uid=$uid")!!.second.bufferedReader()
            val root = ObjectMapper().readTree(reader.readLine())
            if (root.toString().contains("拦截")) {
                if (!data.intercept) {
                    Main.logger!!.error("B站动态api请求被拦截")
                    data.subscribeMap[uid]?.forEach {
                        Main.scheduler.asyncTask {
                            val group = Bot.botInstances[0].getGroup(it)
                            group.sendMessage("B站动态api请求被拦截，请联系管理员!")
                            this
                        }
                    }
                }
                data.intercept = true
                return
            }
            data.intercept = false
            val cards = root["data"]["cards"]
            val card = dequate(cards[num]["card"])
            if (push || StringUtil.getSimilarityRatio(data.dynamicMap[uid]!!, card.toString().substring(50..100)) < 90) {
                data.dynamicMap[uid] = card.toString().substring(50..100)
                data.subscribeMap[uid]?.forEach {
                    Main.scheduler.asyncTask {
                        val group = Bot.botInstances[0].getGroup(it)
                        group.sendMessage(PlainText("${card["user"]["name"]?.asText() ?: card["user"]["uname"]?.asText()}发布了以下动态!"))
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
                else -> PlainText("未解析的消息类型")
            }
        }


        private suspend fun pictureParse(group: Group, origin: JsonNode): Message {
            val description = origin["item"]["description"]
            val pictures = origin["item"]["pictures"]
            var messageChain = messageChainOf(PlainText(description.asText() + "\n"))
            for (i in 0 until pictures.size()) {
                messageChain += NetWorkUtil[pictures[i]["img_src"].asText()]!!.second.uploadAsImage(group)
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
