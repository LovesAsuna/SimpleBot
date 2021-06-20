package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.photo.source.PhotoSource
import me.lovesasuna.bot.controller.photo.source.Pixiv
import me.lovesasuna.bot.controller.photo.source.Random
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

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

    @SubCommand
    suspend fun CommandSender.pixiv(num: Int = 1) {
        if (pixiv) {
            photoSource = Pixiv()
            val urls = (photoSource as MultiPhoto).fetchData(num)
            urls?.forEach {
                sendMessage(
                    OkHttpUtil.getIs(OkHttpUtil[it]).uploadAsImage(getGroupOrNull()!!)
                )
            }
        } else {
            bannotice.invoke(this)
        }
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
        if (sender.hasPermission(PermissionId("photo", "switch"))) {
            GlobalScope.async {
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