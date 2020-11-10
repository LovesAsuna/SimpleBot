package me.lovesasuna.bot.controller

import me.lovesasuna.bot.service.NoticeService
import me.lovesasuna.bot.service.impl.NoticeServiceImpl
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import org.hibernate.Hibernate
import java.util.*

class Notice : FunctionListener {
    private val calendar = Calendar.getInstance()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        event as GroupMessageEvent
        val groupID = event.group.id
        val senderID = event.sender.id
        noticeService.getMatchMessage(groupID, senderID)?.let {
            event.reply(it)
            noticeService.removeNotice(groupID, senderID)
            return true
        }

        if (message.startsWith("/notice @")) {
            val at = event.message[At]
            if (at != null) {
                var messageChain = messageChainOf(PlainText(event.message[3].contentToString().replaceFirst(" ", "")))
                event.message.listIterator(4).forEach {
                    messageChain += it
                }
                noticeService.addNotice(groupID, at.target, at + PlainText("\n${event.senderName}($senderID) ${getTime(Calendar.HOUR_OF_DAY)}:${getTime(Calendar.MINUTE)}:${getTime(Calendar.SECOND)}\n") + messageChain)
                event.reply(At(event.group[senderID]) + "此留言将在该用户下次说话时发送！")
            }
            return true
        }
        return false
    }

    private fun getTime(filed: Int): Int {
        return calendar.get(filed)
    }

    companion object {
        var noticeService: NoticeService = NoticeServiceImpl
    }
}