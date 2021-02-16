package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil
import java.net.URLEncoder

class Baike : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (message.startsWith("/baike ")) {
            val string = message.split(" ")[1]
            val url = "https://baike.baidu.com/item/${URLEncoder.encode(string, "UTF-8")}"
            val reader = OkHttpUtil.getIs(OkHttpUtil[url]).bufferedReader()
            for (i in 0 until 10) reader.readLine()
            val desc = reader.readLine()
            val args = desc.split("\"")
            if (args.size > 1) {
                box.reply(args[3].replace(Regex("...$"), ""))
            } else {
                box.reply("百度百科未收录此词条!")
            }
            return true
        }
        return false

    }

}