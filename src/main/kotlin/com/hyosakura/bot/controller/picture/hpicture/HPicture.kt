package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.*

object HPicture : CompositeCommand(
    owner = Main,
    primaryName = "色图",
    description = "从多个图源中获取色图",
    parentPermission = registerDefaultPermission()
) {
    lateinit var source: SinglePictureSource
    private var queue: Queue<String> = LinkedList()

    @SubCommand
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.lolicon(num : Int) {
        if (num > 5) {
            sendMessage("一次最大只能同时获取5张图片")
            return
        }
        source = Lolicon()
        if (queue.size >= 5) {
            sendMessage("队列中剩余${queue.size}张图片未发送")
            return
        }
        val urls = (source as MultiPictureSource).fetchData(num)
        queue.addAll(urls)
        withContext(this.coroutineContext) {
            urls.forEach {
                launch {
                    runCatching {
                        withTimeout(15000) {
                            sendMessage(
                                OkHttpUtil.getIs(OkHttpUtil[it]).run {
                                    val image = uploadAsImage(getGroupOrNull()!!)
                                    withContext(Dispatchers.IO) {
                                        this@run.close()
                                    }
                                    image
                                }
                            )
                            if (queue.isNotEmpty()) queue.poll()
                            Main.logger.debug("获取成功，队列大小-1")
                        }
                    }.onFailure {
                        sendMessage("获取超时或发生IO错误")
                        if (queue.isNotEmpty()) queue.poll()
                        Main.logger.error("获取超时或发生IO错误，队列大小-1", it)
                    }
                }
            }
        }
    }

    @SubCommand
    suspend fun CommandSender.random() {
        source = Misc()
        sendMessage(
            source.fetchData()?.let { OkHttpUtil.getIs(OkHttpUtil[it]) }!!.uploadAsImage(getGroupOrNull()!!)
        )
    }

    @SubCommand
    suspend fun CommandSender.girl() {
        source = Girl()
        sendMessage(
            source.fetchData()?.let { OkHttpUtil.getIs(OkHttpUtil[it]) }!!.uploadAsImage(getGroupOrNull()!!)
        )
    }
}