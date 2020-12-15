package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.service.KeyWordService
import me.lovesasuna.bot.service.impl.KeyWordServiceImpl
import me.lovesasuna.bot.util.BasicUtil
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
    override suspend fun execute(box: MessageBox): Boolean {
        val senderID = box.sender.id
        val groupID = box.group!!.id
        val message = box.message()
        when {
            message == "/debug" -> {
                box.reply(
                    "调试模式${
                        if (BotData.debug) {
                            "关闭"
                        } else {
                            "开启"
                        }
                    }"
                )
                BotData.debug = !BotData.debug
                return true
            }
            message == "/keyword list" && Config.data.Admin.contains(senderID) -> {
                val builder = StringBuilder()
                builder.append("匹配规则  |  回复词  |  几率\n")
                builder.append("======================\n")
                keyWordService.getKeyWordsByGroup(groupID).forEach {
                    builder.append(
                        "${it.id}. ${it.wordRegex} | ${
                            it.reply.subSequence(
                                0, if (it.reply.length >= 10) {
                                    10
                                } else {
                                    it.reply.length
                                }
                            )
                        } | ${it.chance}\n"
                    )
                }

                box.reply(builder.toString())
                return true
            }
            message.startsWith("/keyword remove ") && Config.data.Admin.contains(senderID) -> {
                val id = BasicUtil.extractInt(message)
                keyWordService.removeKeyWord(id).also {
                    if (it) {
                        box.reply("关键词删除成功")
                    } else {
                        box.reply("关键词删除失败")
                    }
                }
                return true
            }
            message.startsWith("/keyword add ") && Config.data.Admin.contains(senderID) -> {
                val prams = message.split(" ")
                keyWordService.addKeyWord(groupID, prams[2], prams[3], BasicUtil.extractInt(prams[4])).also {
                    if (it) {
                        box.reply("关键词添加成功")
                    } else {
                        box.reply("关键词添加失败")
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
                        messageChain += box.uploadImage(File(imagePath((value))).inputStream())
                        result = result?.next()
                    }
                }
                box.reply(messageChain)
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