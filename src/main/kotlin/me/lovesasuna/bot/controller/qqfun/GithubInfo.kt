package me.lovesasuna.bot.controller.qqfun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.errors.IOException
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup
import java.net.URL

/**
 * @author LovesAsuna
 **/
object GithubInfo : SimpleCommand(
    owner = Main,
    primaryName = "github",
    description = "获取github仓库信息",
    parentPermission = registerDefaultPermission()
) {
    @SimpleCommand.Handler
    suspend fun CommandSender.handle(url: String) {
        try {
            sendMessage(OkHttpUtil.getIs(OkHttpUtil[withContext(Dispatchers.IO) {
                Jsoup.parse(URL("https://hub.fastgit.org/$url"), 10000)
                    .head()
                    .getElementsByAttributeValue("property", "og:image")
                    .attr("content")
            }]).uploadAsImage(this.subject!!))
        } catch (e : IOException) {
            sendMessage("仓库不存在或连接超时！")
        }
    }
}