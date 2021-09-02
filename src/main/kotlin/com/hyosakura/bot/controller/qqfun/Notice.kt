package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.service.NoticeService
import com.hyosakura.bot.service.impl.NoticeServiceImpl
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf
import java.util.*

class Notice : FunctionListener {
    private val calendar = Calendar.getInstance()

    override suspend fun execute(box: MessageBox): Boolean {
        val groupID = box.group!!.id
        val senderID = box.sender.id
        noticeService.getMatchMessage(groupID, senderID)?.let {
            box.reply(it)
            noticeService.removeNotice(groupID, senderID)
            return true
        }

        if (box.text().startsWith("/notice @")) {
            val at = box.message(At::class.java)
            var messageChain = messageChainOf(PlainText(box.event.message[3].contentToString().replaceFirst(" ", "")))
            val listIterator = box.event.message.listIterator(4)
            while (listIterator.hasNext()) {
                messageChain += listIterator.next()
            }
            noticeService.addNotice(
                groupID,
                at!!.target,
                at + PlainText(
                    "\n${box.event.senderName}($senderID) ${getTime(Calendar.HOUR_OF_DAY)}:${getTime(Calendar.MINUTE)}:${
                        getTime(Calendar.SECOND)
                    }\n"
                ) + messageChain
            )
            box.reply(At(box.group!![senderID]!!) + "此留言将在该用户下次说话时发送！")
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