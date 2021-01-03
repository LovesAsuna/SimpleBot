package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import javax.script.ScriptEngineManager


/**
 * @author LovesAsuna
 **/
class AbstractWord : FunctionListener {
    val se = ScriptEngineManager().getEngineByName("JavaScript")

    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (!message.startsWith("/抽象话 ")) {
            return false
        }
        try {
            val str =  Main.javaClass.classLoader.getResourceAsStream("chouxianghua.js").bufferedReader().readText()
            se.eval(str)
            val o = se.eval("chouxiang(\"" + message.split(" ")[1] + "\")")
            box.reply(o.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            box.reply("生成失败，请重试！！")
        }
        return true
    }
}