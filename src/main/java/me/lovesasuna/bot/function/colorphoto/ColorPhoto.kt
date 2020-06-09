package me.lovesasuna.bot.function.colorphoto

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText

class ColorPhoto : Listener {
    lateinit var source: Source
    var random = true
    var pixiv = true
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val bannotice = { GlobalScope.async { event.reply("该图源已被禁用！") } }
        if (message.startsWith("/色图")) {
            when (message.split(" ")[1]) {
                "pixiv" -> {
                    if (pixiv) {
                        source = Pixiv()
                        val data = source.fetchData()
                        val url = data?.split("|")?.get(0)
                        val quota = data?.split("|")?.get(1)
                        event.reply(event.uploadImage(NetWorkUtil.fetch(url)!!.first) + PlainText("\n剩余次数: $quota"))
                    } else {
                        bannotice.invoke()
                    }

                }
                "random" -> {
                    if (random) {
                        source = Random()
                        event.reply(event.uploadImage(NetWorkUtil.fetch(source.fetchData())!!.first))
                    } else {
                        bannotice.invoke()
                    }
                }
                "ban" -> {
                    changeBanStatus(event, message)
                }
                "unban" -> {
                    changeBanStatus(event, message)
                }
            }
        }
        return true
    }

    private fun changeBanStatus(event: MessageEvent, message: String) {
        if (event.sender.id == 625924077L) {
            GlobalScope.async {
                when (message.split(" ")[2]) {
                    "pixiv" -> {
                        if (pixiv) {
                            event.reply("已禁用pixiv图源")
                        } else {
                            event.reply("已解禁pixiv图源")
                        }
                        pixiv = !pixiv
                    }
                    "random" -> {
                        if (random) {
                            event.reply("已禁用random图源")
                        } else {
                            event.reply("已解禁random图源")
                        }
                        random = !random
                    }
                }
            }

        }
    }

}