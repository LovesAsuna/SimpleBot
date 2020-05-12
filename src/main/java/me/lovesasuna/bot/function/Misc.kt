package me.lovesasuna.bot.function

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.File


/**
 * @author LovesAsuna
 */
class Misc : Listener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when {
            message == "/doc" -> {
                val text = """                                          BukkitAPI - Javadoc:
     1.7.10版(已过时): https://jd.bukkit.org/
     Chinese_Bukkit: 
        1.12.2版: http://docs.zoyn.top/bukkitapi/1.12.2/
        1.13+版: https://bukkit.windit.net/javadoc/
     Spigot: https://hub.spigotmc.org/javadocs/spigot/
     Paper: https://papermc.io/javadocs/paper/
     Sponge(不推荐): https://docs.spongepowered.org/stable/zh-CN/
     BungeeCord:
        API: https://ci.md-5.net/job/BungeeCord/ws/api/target/apidocs/overview-summary.html
        API-Chat: https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/overview-summary.html
     MCP Query: https://mcp.exz.me/
     NMS或ProtocolLib必要网站: https://wiki.vg ; https://wiki.vg/Protocol
     Java8: https://docs.oracle.com/javase/8/docs/api/overview-summary.html"""
                event.reply(text)
            }
            message.contains("怎么") -> {
                val path = StringBuilder()
                path.append(Main.instance.dataFolder.path).append(File.separator).append("image").append(File.separator).append("how.jpg")
                event.reply(event.uploadImage(File(path.toString())))
            }
        }
        return true
    }
}