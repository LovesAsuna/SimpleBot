package com.hyosakura.bot.controller.system

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.Message
import com.hyosakura.bot.entity.message.messages
import com.hyosakura.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import org.ktorm.dsl.and
import org.ktorm.dsl.desc
import org.ktorm.dsl.eq
import org.ktorm.dsl.like
import org.ktorm.entity.filter
import org.ktorm.entity.sortedBy
import org.ktorm.entity.toList

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
        val result = BotData.messageDatabase.messages.filter {
            (it.groupId eq group.id) and (it.memberId eq target.id)
        }.sortedBy {
            it.time.desc()
        }.toList()
        sendMessage(createMessageChain(target, result, num))
    }

    @SubCommand
    suspend fun CommandSender.queryKeyWord(target: Member, keyword: String) {
        val group = this.subject!! as Group
        val result = BotData.messageDatabase.messages.filter {
            (it.groupId eq group.id) and (it.memberId eq target.id) and (it.content.like(keyword))
        }.sortedBy {
            it.time.desc()
        }.toList()
        sendMessage(createMessageChain(target, result, 10))
    }

    private fun CommandSender.createMessageChain(
        target: Member,
        result: List<Message>,
        limit: Int
    ): MessageChain {
        val group = this.subject!! as Group
        val messageChain = buildMessageChain {
            val nick = group[target.id]?.nick
            +"${nick}在\n"
            +"=========================\n"
            for (message in  result.subList(0, limit)) {
                +"${message.time}说了"
                +message.content.deserializeMiraiCode()
                +"\n=========================\n"
            }
        }
        return messageChain
    }
}