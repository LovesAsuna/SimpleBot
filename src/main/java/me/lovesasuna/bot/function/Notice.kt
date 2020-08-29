package me.lovesasuna.bot.function

import me.lovesasuna.bot.data.NoticeData
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.util.*
import java.util.stream.Collectors

class Notice : FunctionListener {
    private val calendar = Calendar.getInstance()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        event as GroupMessageEvent
        val groupID = event.group.id
        val senderID = event.sender.id
        val filterList = data.msgList.parallelStream().filter { it.first == groupID && it.second == senderID }.collect(Collectors.toList())
        if (filterList.isNotEmpty()) {
            event.reply(filterList.first().third)
            data.msgList.remove(filterList.first())
            return true
        }

        if (message.startsWith("/notice @")) {
            val at = event.message[At]
            if (at != null) {
                var messageChain = messageChainOf(PlainText(event.message[3].contentToString().replaceFirst(" ", "")))
                event.message.listIterator(4).forEach {
                    messageChain += it
                }
                data.msgList.add(Triple(groupID, at.target, at + PlainText("\n${event.senderName}($senderID) ${getTime(Calendar.HOUR_OF_DAY)}:${getTime(Calendar.MINUTE)}:${getTime(Calendar.SECOND)}\n") + messageChain))
                event.reply(At(event.group.get(senderID)) + "此留言将在该用户下次说话时发送！")
            }
            return true
        }
        return false
    }

    fun getTime(filed: Int): Int {
        return calendar.get(filed)
    }

    companion object {
        var data = NoticeData(arrayListOf())
    }
}