package me.lovesasuna.bot.controller

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.service.KeyWordService
import me.lovesasuna.bot.service.impl.KeyWordServiceImpl
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.GroupMessageEvent
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
        event as GroupMessageEvent
        val senderID = event.sender.id
        val groupID = event.group.id
        when {
            message == "/debug" -> {
                event.reply("调试模式${
                    if (BotData.debug) {
                        "关闭"
                    } else {
                        "开启"
                    }
                }")
                BotData.debug = !BotData.debug
                return true
            }
            message == "/keyword list" && senderID == Config.data.admin -> {
                val builder = StringBuilder()
                builder.append("匹配规则  |  回复词  |  几率\n")
                builder.append("======================\n")
                var index = 0
                keyWordService.getKeyWordsByGroup(groupID).forEach {
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
            message.startsWith("/keyword remove ") && senderID == Config.data.admin -> {
                //tood 删除操作(Dao也并未设计)
//                val index = BasicUtil.extractInt(message)
//                if (index >= KeyWordFile.data.list.size) throw IllegalArgumentException()
//                KeyWordFile.data.list.removeAt(index)
//                event.reply("关键词删除成功")
                return true
            }
            message.startsWith("/keyword add ") && senderID == Config.data.admin -> {
                val parms = message.split(" ")
                keyWordService.addKeyWord(groupID, parms[2], parms[3], BasicUtil.extractInt(parms[4])).also {
                    if (it) {
                        event.reply("关键词添加成功")
                    } else {
                        event.reply("关键词添加失败")
                    }
                }
                return true
            }
        }

        keyWordService.getKeyWordsByGroup(groupID).forEach {
            val regex = Regex(it.wordRegex)
            val reply = it.reply
            val chance = it.chance ?: 0
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

    companion object {
        val keyWordService: KeyWordService = KeyWordServiceImpl
    }

}