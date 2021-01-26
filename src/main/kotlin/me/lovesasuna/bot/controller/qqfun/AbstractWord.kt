package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import javax.script.ScriptEngineManager


/**
 * @author LovesAsuna
 **/
object AbstractWord : SimpleCommand(
    owner = Main,
    primaryName = "抽象话",
    description = "抽象话翻译",
    parentPermission = registerDefaultPermission()
) {
    val se = ScriptEngineManager().getEngineByName("JavaScript")

    @Handler
    suspend fun CommandSender.handle(text: String) {
        try {
            val str = Main.javaClass.classLoader.getResourceAsStream("chouxianghua.js").bufferedReader().readText()
            se.eval(str)
            val o = se.eval("chouxiang(\"" + text + "\")")
            sendMessage(o.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            sendMessage("生成失败，请重试！！")
        }
    }
}