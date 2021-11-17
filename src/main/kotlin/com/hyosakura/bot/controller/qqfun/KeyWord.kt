package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.service.KeyWordService
import com.hyosakura.bot.service.impl.KeyWordServiceImpl
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random


/**
 * @author LovesAsuna
 */
// todo 添加全局关键词
object KeyWord : CompositeCommand(
    owner = Main,
    primaryName = "keyword",
    description = "关键词回复",
    parentPermission = registerDefaultPermission()
), FunctionListener {
    private val imagePath = "${Main.dataFolder.path}${File.separator}image${File.separator}"
    private val photoRegex = Regex("#\\{\\w+\\.(jpg|png|gif)}")
    private val keyWordService: KeyWordService = KeyWordServiceImpl

    @SubCommand
    suspend fun CommandSender.list() {
        val builder = StringBuilder()
        builder.append("匹配规则  |  回复词  |  几率\n")
        builder.append("======================\n")
        keyWordService.getKeyWordsByGroup(subject!!.id).forEach {
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

        sendMessage(builder.toString())
    }

    @SubCommand
    suspend fun CommandSender.remove(id: Int) {
        keyWordService.removeKeyWord(id).also {
            if (it) {
                sendMessage("关键词删除成功")
            } else {
                sendMessage("关键词删除失败")
            }
        }
    }

    @SubCommand
    suspend fun CommandSender.add(rule: String, reply: String, chance: Int) {
        keyWordService.addKeyWord(subject!!.id, rule, reply, chance).also {
            if (it) {
                sendMessage("关键词添加成功")
            } else {
                sendMessage("关键词添加失败")
            }
        }
    }

    override suspend fun execute(box: MessageBox): Boolean {
        val groupID = box.group!!.id
        val message = box.text()

        keyWordService.getKeyWordsByGroup(groupID).forEach {
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

}