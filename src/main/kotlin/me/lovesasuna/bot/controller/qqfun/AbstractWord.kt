package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.IOException
import javax.script.ScriptEngineManager
import javax.script.ScriptException


/**
 * @author LovesAsuna
 **/
class AbstractWord : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (!message.startsWith("/抽象话 ")) {
            return false
        }
        val se = ScriptEngineManager().getEngineByName("JavaScript")
        try {
            val str = NetWorkUtil["https://share.kuku.me/189/kuku/chouxianghua.js"]!!.second.bufferedReader().readText()
            se.eval(str)
            val o = se.eval("chouxiang(\"" + message.split(" ")[1] + "\")")
            o.toString()
        } catch (e: ScriptException) {
            e.printStackTrace()
            box.reply("生成失败，请重试！！")
        } catch (e: IOException) {
            e.printStackTrace()
            box.reply("生成失败，请重试！！")
        }
        return true
    }
}