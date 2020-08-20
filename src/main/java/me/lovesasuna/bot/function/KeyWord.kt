package me.lovesasuna.bot.function

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.file.KeyWordFile
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random


/**
 * @author LovesAsuna
 */
class KeyWord : FunctionListener {
    private val imagePath = "${Main.dataFolder.path}${File.separator}image${File.separator}"
    private val photoRegex = Regex("#\\{\\w+\\.(jpg|png|gif)}")
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val sender = event.sender.id
        when {
            message == "/debug" -> {
                if (BotData.debug) {
                    event.reply("调试模式关闭")
                } else {
                    event.reply("调试模式开启")
                }
                BotData.debug = !BotData.debug
                return true
            }
            message == "/keyword list" && sender == Config.data.admin -> {
                val builder = StringBuilder()
                builder.append("匹配规则  |  回复词  |  几率\n")
                builder.append("======================\n")
                var index = 0
                KeyWordFile.data.list.forEach {
                    builder.append("$index. ${it.wordRegex} | ${
                        it.reply.subSequence(0, if (it.reply.length >= 10) {
                            10
                        } else {
                            it.reply.length
                        })
                    } | ${it.chance}\n")
                    index++
                }
                event.reply(builder.toString())
                return true
            }
            message.startsWith("/keyword remove ") && sender == Config.data.admin -> {
                val index = BasicUtil.extractInt(message)
                if (index >= KeyWordFile.data.list.size) throw IllegalArgumentException()
                KeyWordFile.data.list.removeAt(index)
                event.reply("关键词删除成功")
                return true
            }
            message.startsWith("/keyword add ") && sender == Config.data.admin -> {
                val parms = message.split(" ")
                val keyWord = KeyWord(parms[2], parms[3], BasicUtil.extractInt(parms[4]))
                KeyWordFile.data.list.add(keyWord)
                event.reply("关键词添加成功")
                return true
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