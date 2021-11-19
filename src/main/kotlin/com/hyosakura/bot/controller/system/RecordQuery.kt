package com.hyosakura.bot.controller.system

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.Message
import com.hyosakura.bot.entity.message.Messages
import com.hyosakura.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author LovesAsuna
 **/
object RecordQuery : CompositeCommand(
    owner = Main,
    primaryName = "record",
    description = "聊天数据管理",
    parentPermission = registerPermission("admin", "管理员权限")
) {

    @SubCommand
    suspend fun CommandSender.query(target: Member, num: Int) {
        val group = this.subject!! as Group
        val result = transaction(BotData.messageDatabase) {
            Messages.select {
                (Messages.group eq group.id) and (Messages.member eq target.id)
            }.orderBy(
                Messages.time to SortOrder.DESC
            ).limit(num, 0).map {
                Message.wrapRow(it)
            }
        }
        sendMessage(createMessageChain(target, result))
    }

    @SubCommand
    suspend fun CommandSender.queryKeyWord(target: Member, keyword: String) {
        val group = this.subject!! as Group
        val result = transaction(BotData.messageDatabase) {
            Messages.select {
                (Messages.group eq group.id) and (Messages.member eq target.id) and (Messages.content.like("%${keyword}%"))
            }.orderBy(
                Messages.time to SortOrder.DESC
            ).limit(10, 0).map {
                Message.wrapRow(it)
            }
        }
        sendMessage(createMessageChain(target, result))
    }

    private fun CommandSender.createMessageChain(
        target: Member,
        result: List<Message>,
    ): MessageChain {
        val group = this.subject!! as Group
        val messageChain = buildMessageChain {
            val nick = group[target.id]?.nick
            +"${nick}在\n"
            +"=========================\n"
            for (message in result) {
                +"${message.time}说了"
                +message.content.deserializeMiraiCode()
                +"\n=========================\n"
            }
        }
        return messageChain
    }
}