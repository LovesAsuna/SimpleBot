package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.controller.photo.source.PhotoSource
import me.lovesasuna.bot.controller.photo.source.Pixiv
import me.lovesasuna.bot.controller.photo.source.Random
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.file.Config
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.message.data.PlainText

class Photo : FunctionListener {
    lateinit var photoSource: PhotoSource
    var random = true
    var pixiv = true
    override suspend fun execute(box: MessageBox): Boolean {
        val bannotice = { Main.scheduler.asyncTask { box.reply("该图源已被禁用！") } }
        if (box.text().startsWith("/色图")) {
            when (box.text().split(" ")[1]) {
                "pixiv" -> {
                    if (pixiv) {
                        photoSource = Pixiv()
                        val data = photoSource.fetchData()
                        val quota = data?.split("|")!![1]
                        if (quota == "0") {
                            box.reply("达到每日调用额度限制")
                        } else {
                            val url = data.split("|")[0]
                            box.reply(box.event.subject.uploadImage(NetWorkUtil[url]!!.second) + PlainText("\n剩余次数: $quota"))
                        }
                    } else {
                        bannotice.invoke()
                    }
                }
                "random" -> {
                    if (random) {
                        photoSource = Random()
                        box.reply(box.event.subject.uploadImage(photoSource.fetchData()?.let { NetWorkUtil[it] }!!.second))
                    } else {
                        bannotice.invoke()
                    }
                }
                "switch" -> {
                    changeBanStatus(box)
                }
            }
        }
        return true
    }

    private fun changeBanStatus(box: MessageBox) {
        if (Config.data.Admin.contains(box.event.sender.id)) {
            GlobalScope.async {
                when (box.text().split(" ")[2]) {
                    "pixiv" -> {
                        box.reply("已${if (pixiv) "禁用" else "解禁"}pixiv图源")
                        pixiv = !pixiv
                    }
                    "random" -> {
                        box.reply("已${if (random) "禁用" else "解禁"}random图源")
                        random = !random
                    }
                }
            }

        }
    }

}