package me.lovesasuna.bot.function

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.file.KeyWordFile
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random


/**
 * @author LovesAsuna
 */
class Misc : FunctionListener {
    private val imagePath = "${Main.dataFolder.path}${File.separator}image${File.separator}"
    private val photoRegex = Regex("#\\{\\w+\\.(jpg|png|gif)}")
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when (message) {
            "/debug" -> {
                if (BotData.debug) {
                    event.reply("调试模式关闭")
                } else {
                    event.reply("调试模式开启")
                }
                BotData.debug = !BotData.debug
            }
        }
        val list = KeyWordFile.data.list
        list.forEach {
            val regex = Regex(it.wordRegex)
            val reply = it.reply
            val chance = it.chance
            var messageChain = messageChainOf()
            if (regex.matches(message) && canReply(chance)) {
                val sm = photoRegex.split(reply)
                var result = photoRegex.find(reply)
                sm.forEach { s ->
                    messageChain += PlainText(s)
                    result?.apply {
                        val value = this.value.replace("#{", "").replace("}", "")
                        messageChain += event.uploadImage(File(imagePath((value))))
                        result = result?.next()
                    }
                }
                event.reply(messageChain)
            }
        }

        return true
    }

    private fun imagePath(imageName: String): String {
        return "$imagePath$imageName"
    }

    private fun canReply(change: Int): Boolean {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(100) < change
    }

    init {
        if (!File(imagePath).exists()) {
            Files.createDirectory(Paths.get(imagePath))
        }
    }

    data class KeyWord(val wordRegex: String, val reply: String, val chance: Int)

    data class KeyWordChain(val list: MutableList<KeyWord> = arrayListOf())
}