package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.MessageUtil
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.flow.catch
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.*

object HPicture : CompositeCommand(
    owner = Main,
    primaryName = "色图",
    description = "从多个图源中获取色图",
    parentPermission = registerDefaultPermission()
) {
    private var queue: Queue<String> = LinkedList()

    @SubCommand
    suspend fun CommandSender.lolicon(num: Int) {
        multiPhoto(Lolicon(), num)
    }

    private suspend fun CommandSender.multiPhoto(source: MultiPictureSource, num: Int) {
        if (num > 5) {
            sendMessage("一次最大只能同时获取5张图片")
            return
        }
        if (queue.size >= 5) {
            sendMessage("队列中剩余${queue.size}张图片未发送")
            return
        }
        val messageList = mutableListOf<Message>()
        source.fetchData(num).catch { e ->
            Main.logger.error(e)
        }.collect { s ->
            Main.scheduler.withTimeOut({
                messageList.add(
                    Request.getIs(s).use {
                        it.uploadAsImage(getGroupOrNull()!!)
                    }
                )
                if (queue.isNotEmpty()) queue.poll()
                Main.logger.debug("获取成功，队列大小-1")
            }, 15000) {
                messageList.add(PlainText("获取超时或发生IO错误"))
                if (queue.isNotEmpty()) queue.poll()
                Main.logger.error("获取超时或发生IO错误，队列大小-1", it)
            }
        }
        sendMessage(MessageUtil.buildForwardsMessage(user!!, messageList))
    }

    @SubCommand
    suspend fun CommandSender.random() {
        val source = Misc()
        sendMessage(
            source.fetchData()?.let { Request.getIs(it) }!!.uploadAsImage(getGroupOrNull()!!)
        )
    }

    @SubCommand
    suspend fun CommandSender.girl() {
        val source = Girl()
        sendMessage(
            source.fetchData().let { Request.getIs(it) }.uploadAsImage(getGroupOrNull()!!)
        )
    }
}