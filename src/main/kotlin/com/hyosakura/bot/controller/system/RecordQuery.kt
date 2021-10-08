package com.hyosakura.bot.controller.system

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.MessageEntity
import com.hyosakura.bot.service.ServiceManager
import com.hyosakura.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import java.io.Closeable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author LovesAsuna
 **/
object RecordQuery : CompositeCommand(
    owner = Main,
    primaryName = "record",
    description = "聊天数据管理",
    parentPermission = registerPermission("admin", "管理员权限")
), Closeable {
    private val session = BotData.recordConfig.buildSessionFactory().openSession()
    private val timeFormatter = DateTimeFormatter.ofPattern("MM月dd日HH时mm分")
    private val zoneId = ZoneId.systemDefault()

    init {
        ServiceManager.registerService(this)
    }

    @SubCommand
    suspend fun CommandSender.query(target: Member, num: Int) {
        val group = this.subject!! as Group
        val messageChain = createMessageChain(
            target,
            "select m from MessageEntity as m where m.member.id = ${target.id}L and m.group.id = ${group.id}L order by m.time desc",
            num
        )
        sendMessage(messageChain)
    }

    @SubCommand
    suspend fun CommandSender.queryKeyWord(target: Member, keyword: String) {
        val group = this.subject!! as Group
        val messageChain = createMessageChain(
            target,
            "select m from MessageEntity as m where m.member.id = ${target.id}L and m.group.id = ${group.id}L and m.content like '%$keyword%' order by m.time desc",
            10
        )
        sendMessage(messageChain)
    }

    private fun CommandSender.createMessageChain(
        target: Member,
        hql: String,
        limit: Int
    ): MessageChain {
        val group = this.subject!! as Group
        val messageChain = buildMessageChain {
            val messages =
                session.createQuery(hql)
                    .setMaxResults(limit)
                    .list()
            val nick = group[target.id]?.nick
            +"${nick}在\n"
            +"=========================\n"
            for (message in messages) {
                message as MessageEntity
                +"${timeFormatter.format(LocalDateTime.ofInstant(message.time!!.toInstant(), zoneId))}说了"
                +message.content!!.deserializeMiraiCode()
                +"\n=========================\n"
            }
        }
        return messageChain
    }

    override fun close() = session.close()
}