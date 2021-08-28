package me.lovesasuna.bot.controller.system

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

/**
 * @author LovesAsuna
 **/
object DataBaseManager : RawCommand(
    owner = Main,
    primaryName = "db",
    description = "数据库管理",
    parentPermission = registerPermission("admin", "管理员权限")
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        val command = args[0].content
        val scope = args[1].content
        val maxSize = args[2].content.toInt()
        if (command == "query") {
            val session = when (scope) {
                "function" -> BotData.functionConfig.buildSessionFactory().openSession()
                "record" -> BotData.recordConfig.buildSessionFactory().openSession()
                else -> {
                    sendMessage("scope doesn't exist")
                    return
                }
            }
            val query = StringBuilder().apply {
                val size = args.size
                for (i in 3 until size) {
                    this.append(args[i].content).append(" ")
                }
            }.toString()
            session.use { s ->
                val result = s.createQuery(query).setMaxResults(maxSize).list()
                result.forEach {
                    sendMessage(it.toString().deserializeMiraiCode())
                }
            }
        }
    }
}