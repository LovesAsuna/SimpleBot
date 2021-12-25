package com.hyosakura.bot.controller.bilibili

import com.fasterxml.jackson.databind.JsonNode
import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.DynamicService
import com.hyosakura.bot.service.LinkService
import com.hyosakura.bot.service.impl.DynamicServiceImpl
import com.hyosakura.bot.service.impl.LinkServiceImpl
import com.hyosakura.bot.util.BasicUtil
import com.hyosakura.bot.util.coroutine.PluginScheduler
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import com.hyosakura.bot.util.registerPermission
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

object Dynamic : CompositeCommand(
    owner = Main,
    primaryName = "dynamic",
    description = "B站Up动态订阅",
    parentPermission = registerDefaultPermission()
) {
    private var task: Pair<Job, PluginScheduler.RepeatTaskReceipt> = launchTask()
    private val dynamicService: DynamicService = DynamicServiceImpl
    private val linkService: LinkService = LinkServiceImpl
    private var intercept = false
    private var time = ""

    private fun launchTask(): Pair<Job, PluginScheduler.RepeatTaskReceipt> {
        return BasicUtil.scheduleWithFixedDelay({
            Main.scheduler.asyncTask {
                linkService.getUps().forEach {
                    with(it) {
                        Main.scheduler.withTimeOut({
                            read(it, 0)
                            time = "${Calendar.getInstance().time}"
                        }, 10000) {
                            linkService.getGroupByUp(it).forEach {
                                val group = Bot.instances[0].getGroup(it)
                                group?.sendMessage("查询${this}动态时超时!")
                            }
                        }
                    }
                    delay(15000)
                }
            }
        }, 0, 1, TimeUnit.MINUTES)
    }

    @SubCommand
    suspend fun CommandSender.run() {
        if (!task.second.cancelled) {
            sendMessage("已经有正在运行的任务！")
        } else {
            task = launchTask()
        }
    }

    @SubCommand
    suspend fun CommandSender.stop() {
        if (task.second.cancelled) {
            sendMessage("目前无正在运行的任务!")
            return
        }
        task.first.cancel()
        task.second.cancelled = true
    }

    @SubCommand
    suspend fun CommandSender.list() {
        sendMessage("当前订阅的up: ${linkService.getUpByGroup(getGroupOrNull()!!.id)}")
    }

    @SubCommand
    suspend fun CommandSender.add(upID: Long) {
        linkService.addLink(upID, getGroupOrNull()!!.id)
        sendMessage("up动态订阅成功!")
    }

    @SubCommand
    suspend fun CommandSender.remove(upID: Long) {
        linkService.deleteUpByGroup(upID, getGroupOrNull()!!.id)
        sendMessage("up动态取消订阅成功!")
    }

    @SubCommand
    suspend fun CommandSender.test(upID: Long, num: Int) {
        read(upID, num)
    }

    @SubCommand
    suspend fun CommandSender.push() {
        sendMessage("开始往订阅群推送消息！")
        Main.scheduler.asyncTask {
            linkService.getUps().forEach {
                runBlocking {
                    read(it, 0)
                    time = "${Calendar.getInstance().time}"
                    delay(15000)
                }
            }
            sendMessage("推送完成")
            this
        }
    }

    @SubCommand
    suspend fun CommandSender.debug() {
        if (hasPermission(registerPermission("admin", "管理员权限"))) {
            sendMessage("开始收集信息...")
            val builder = StringBuilder()
            builder.append("Task状态: \n")
            builder.append("Active: ${task.first.isActive}\n")
            builder.append("Completed: ${task.first.isCompleted}\n")
            builder.append("Cancelled: ${task.first.isCancelled}\n\n")
            builder.append("最后查询时间: $time\n\n")
            builder.append("是否被拦截: $intercept\n\n")
            builder.append("订阅的UP集合: ${linkService.getUps()}\n\n")
            builder.append("up与群的对应关系: \n")
            linkService.getUps().forEach {
                builder.append("UP=${it}: 群聊=${linkService.getGroupByUp(it)}\n")
            }
            builder.append("\n")
            builder.append("UP消息摘要: \n")
            linkService.getUps().forEach {
                builder.append("UP=$it: DynamicID=${dynamicService.getDynamicID(it)}\n")
            }
            sendMessage("Debug信息: ${BasicUtil.debug(builder.toString())}")
        }
    }

    private suspend fun read(uid: Long, num: Int) {
        val reader =
            Request.getIs("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?&host_uid=$uid")
                .bufferedReader()
        val root = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            BotData.objectMapper.readTree(reader.readLine())
        }
        if (root.toString().contains(Regex("拦截|错误"))) {
            if (!intercept) {
                Main.logger.error("B站动态api请求被拦截")
                linkService.getGroupByUp(uid).forEach {
                    Main.scheduler.asyncTask {
                        val group = Bot.instances[0].getGroup(it)
                        group?.sendMessage("B站动态api请求被拦截，请联系管理员!")
                        this
                    }
                }
            }
            intercept = true
            return
        }
        intercept = false
        val cards = root["data"]["cards"]
        val dynamicID = cards[num]["desc"]["dynamic_id_str"].asText()
        val card = dequate(cards[num]["card"])
        if (dynamicID != dynamicService.getDynamicID(uid)) {
            dynamicService.insertOrUpdate(uid, dynamicID)
            linkService.getGroupByUp(uid).forEach {
                Main.scheduler.asyncTask {
                    val group = Bot.instances[0].getGroup(it)
                    if (group != null) {
                        group.sendMessage(PlainText("${card["user"]["name"]?.asText() ?: card["user"]["uname"]?.asText()}发布了以下动态!"))
                        Main.scheduler.withTimeOut({
                            // todo 动态解析不完整
                            parse(group, card)
                        }, Duration.ofSeconds(60).toMillis()) {
                            group.sendMessage("解析动态详细信息超时！")
                        }
                    } else {
                        linkService.deleteGroup(it)
                    }
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
            messageChain += Request.getIs(pictures[i]["img_src"].asText()).uploadAsImage(group)
        }
        return messageChain
    }

    private fun videoParse(origin: JsonNode): Message {
        return makeCard(
            origin["title"].asText(),
            origin["desc"].asText(),
            "哔哩哔哩视频",
            origin["jump_url"].asText(),
            origin["pic"].asText()
        )
    }

    private fun articleParse(origin: JsonNode): Message {
        return makeCard(
            origin["title"].asText(),
            origin["summary"].asText(),
            "哔哩哔哩专栏",
            "https://www.bilibili.com/read/cv${origin["id"].asText()}",
            origin["banner_url"].asText()
        )
    }

    private fun makeCard(title: String, desc: String, tag: String, jumpURL: String, previewURL: String): LightApp {
        val string =
            "{\"app\":\"com.tencent.structmsg\",\"desc\":\"自定义卡片\",\"view\":\"news\",\"ver\":\"0.0.0.1\",\"prompt\":\"自定义提示\",\"meta\":{\"news\":{\"action\":\"\",\"android_pkg_name\":\"\",\"app_type\":1,\"appid\":100951776,\"desc\":\"$desc\",\"jumpUrl\":\"$jumpURL\",\"preview\":\"$previewURL\",\"source_icon\":\"\",\"source_url\":\"\",\"tag\":\"$tag\",\"title\":\"$title\"}},\"config\":{\"autosize\":true,\"ctime\":1592374968,\"forward\":true,\"token\":\"deaf213724ea32ea9c94ed8efdc09c13\",\"type\":\"normal\"}}"
        return LightApp((string))
    }

    private fun dequate(node: JsonNode): JsonNode {
        return BotData.objectMapper.readTree(node.asText())
    }
}
