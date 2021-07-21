package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.photo.source.PhotoSource
import me.lovesasuna.bot.controller.photo.source.Pixiv
import me.lovesasuna.bot.controller.photo.source.Random
import me.lovesasuna.bot.util.logger.debug
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import me.lovesasuna.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.*

object ColorPhoto : CompositeCommand(
    owner = Main,
    primaryName = "色图",
    description = "从多个图源中获取色图",
    parentPermission = registerDefaultPermission()
) {
    lateinit var photoSource: PhotoSource
    val bannotice = { sender: CommandSender ->
        Main.scheduler.asyncTask {
            sender.sendMessage("该图源已被禁用！")
            this
        }
    }
    var random = true
    var pixiv = true
    private var queue: Queue<String> = LinkedList()

    @SubCommand
    suspend fun CommandSender.pixiv(num : Int) {
        if (pixiv) {
            if (num > 5) sendMessage("一次最大只能同时获取5张图片")
            photoSource = Pixiv()
            if (queue.size >= 5) {
                sendMessage("队列中剩余${queue.size}张图片未发送")
                return
            }
            val urls = (photoSource as MultiPhoto).fetchData(num)
            queue.addAll(urls!!)
            coroutineScope {
                urls.forEach {
                    launch {
                        try {
                            withTimeout(15000) {
                                sendMessage(
                                    OkHttpUtil.getIs(OkHttpUtil[it]).run {
                                        val image = uploadAsImage(getGroupOrNull()!!)
                                        this.close()
                                        image
                                    }
                                )
                                if (queue.isNotEmpty()) queue.poll()
                                debug("获取成功，队列大小-1")
                            }
                        } catch (e: Exception) {
                            sendMessage("获取超时或发生IO错误")
                            if (queue.isNotEmpty()) queue.poll()
                            debug("获取超时或发生IO错误，队列大小-1")
                        }
                    }
                }
            }
        } else {
            bannotice.invoke(this)
        }
    }

    @SubCommand
    suspend fun CommandSender.queue() {
        val builder = StringBuilder()
        builder.append("队列中剩余${queue.size}张图片未发送")
        queue.forEach {
            builder.append("${it}\n")
        }
        sendMessage(builder.toString())
    }

    @SubCommand
    suspend fun CommandSender.random() {
        if (random) {
            photoSource = Random()
            sendMessage(
                photoSource.fetchData()?.let { OkHttpUtil.getIs(OkHttpUtil[it]) }!!.uploadAsImage(getGroupOrNull()!!)
            )
        } else {
            bannotice.invoke(this)
        }
    }

    @SubCommand
    suspend fun CommandSender.switch(type: String) {
        changeBanStatus(this, type)
    }

    private fun changeBanStatus(sender: CommandSender, type: String) {
        if (sender.hasPermission(registerPermission("photo.switch", "图源开关"))) {
            Main.scheduler.asyncTask {
                when (type) {
                    "pixiv" -> {
                        sender.sendMessage("已${if (pixiv) "禁用" else "解禁"}pixiv图源")
                        pixiv = !pixiv
                    }
                    "random" -> {
                        sender.sendMessage("已${if (random) "禁用" else "解禁"}random图源")
                        random = !random
                    }
                }

            }
        }
    }
}